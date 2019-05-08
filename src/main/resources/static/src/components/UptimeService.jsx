import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import {Panel,Alert, Grid, Row, Col,ProgressBar} from 'react-bootstrap';





const globalValue = {
		pollingXhr:null,
		sseXhr:null
}

class UptimeService extends React.Component {
	constructor(props) {
		super(props);
		this.state = this.getInitialState();
		this.sendLongPollingRequest = this.sendLongPollingRequest.bind(this);
		this.prepareProgressBar = this.prepareProgressBar.bind(this);
	}


	getInitialState(){
		const state = {
					blockService : {},
					responseTime : 0,
					uptimePercent : 0,
					bsStyle : ""
		};
		return state;
	}

	componentDidMount() {
		$.ajax({
			type:'GET',
			url:'/getBlockService',
			dataType:'json',
			cache:false,
			success: function(res){
				this.prepareProgressBar(res);
				this.sendLongPollingRequest(res.serviceName, res.lastUpdate);
				console.log(res);
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});

	}

	componentWillUnmount() {
		globalValue.pollingXhr.abort();
		globalValue.sseXhr.abort();
		this.setState(this.getInitialState());
	}


	sendLongPollingRequest(serviceName, lastUpdateDate) {

		globalValue.sseXhr = $.ajax({
			type:'GET',
			url:'/getSseUptime',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});

		globalValue.pollingXhr = $.ajax({
			type: 'GET',
			url:'/getUptimePoll?serviceName='+serviceName+'&lastUpdateTime='+lastUpdateDate,
			dataType:'json',
			cache:false,
			success: function(res){
				this.prepareProgressBar(res);
				this.sendLongPollingRequest(res.serviceName, res.lastUpdate);
			}.bind(this),
			error: function(xhr, status, err){
				this.setState({
					longPollingItems:[]
				})
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	prepareProgressBar(res){
		var responseTime = Math.trunc(res.responseTime);
		var timeoutTime = res.timeoutTime/1000;
		var uptimePercent = Number.parseFloat(100-((responseTime*100)/timeoutTime)).toFixed(2);
		var bsStyle = "";
		if(uptimePercent <= 20) {
			bsStyle="danger";
		} else if(uptimePercent>20 && uptimePercent<=60){
			bsStyle="warning";
		} else if(uptimePercent>60 && uptimePercent<=100){
			bsStyle="success";
		}
		this.setState({
			blockService : res,
			responseTime : responseTime,
			uptimePercent : uptimePercent,
			bsStyle: bsStyle
		})
	}

	render() {
		return (
				<Grid bsClass="container-fluid" >
					<Row>
						<Col xs={6} md={4}>
							<h1>{this.state.blockService.serviceName} ---%{this.state.uptimePercent} </h1>
						</Col>
						<Col xs={6} md={4}>
							<ProgressBar bsStyle={this.state.bsStyle} now={this.state.uptimePercent} />
						</Col>
						<Col xsHidden md={4}>
							<h1>Response Süresi: {this.state.responseTime} sn dir.</h1>
						</Col>
					</Row>
				</Grid>
		)

	}
}

export default UptimeService;
