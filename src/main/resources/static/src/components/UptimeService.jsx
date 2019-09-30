import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import {Panel,Alert, Grid, Row, Col,ProgressBar, Table} from 'react-bootstrap';
import heartbeats from 'heartbeats';
import {withRouter} from "react-router";
import {BrowserRouter as Router, Route, Link} from "react-router-dom";




const globalValue = {
		res : []
}


class UptimeService extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			res:[],
			isOpenComponent:false,
			serviceModel:{}
		}
		this.decideStyle = this.decideStyle.bind(this);
		this.getServices = this.getServices.bind(this);
		this.runHearthBeat = this.runHearthBeat.bind(this);
		this.redirectAdminService  = this.redirectAdminService.bind(this);
		this.cleanStates = this.cleanStates.bind(this);
	}


	getInitialState(){
		const state = {
					res:[],
					isOpenComponent:false
		};
		return state;
	}

	componentWillMount() {
		if(!this.props.isLogin) {
			this.props.history.push("/");
		}

		this.getServices(false);
	}

	componentWillUnmount() {
		this.cleanStates();
		heartbeats.killHeart('GetServices');
	}

	cleanStates(){
		this.setState({
			res:[],
			isOpenComponent:false,
			serviceModel:{}
		});
	}


	runHearthBeat() {
		var self = this;
		var heart = heartbeats.createHeart(10000, 'GetServices');
		heart.createEvent(1, function(count, last){
			self.getServices(true);
		});
	}

	getServices(isHeartBeat){
		var accessToken = localStorage.getItem('accessToken');
		$.ajax({
			url:'/oThreadService/getServices',
			dataType:'json',
			cache:false,
			headers:{Authorization:"Bearer "+accessToken},
			success: function(res){
				console.log(res);
				this.setState({
					res:res
				});
				if(!isHeartBeat) {
					this.runHearthBeat();
				}
			}.bind(this),
			error: function(xhr, status, err){
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


	decideStyle(uptimePercent, status) {
		
		if(!status) {
			return "danger";
		}

		var bsStyle = "";
		if(uptimePercent <= 20) {
			bsStyle="danger";
		} else if(uptimePercent>20 && uptimePercent<=60){
			bsStyle="warning";
		} else if(uptimePercent>60 && uptimePercent<=100){
			bsStyle="success";
		}

		return bsStyle;
	}

	redirectAdminService(serviceModel) {
		this.props.redirectFunc(serviceModel);
	}



	render() {

		var self = this;

		var operationListDOM = this.state.res != null ? this.state.res.map(function(result, index) {
			
			var uptimePercent = Number.parseFloat(result.uptimePercent).toFixed(2)
			var percentText = uptimePercent + "%";
			var uptime = result.status ? result.uptime + ' ms' : 'Servis Cevap Vermiyor';
			return(
				
				<tr>
					<td style={{verticalAlign:'middle'}} width='3%'>{index + 1}</td>
					<td style={{verticalAlign:'middle'}} width='35%'>{result.serviceName}</td>
					<td style={{verticalAlign:'middle'}} width='50%'> <ProgressBar striped label={percentText} bsStyle={self.decideStyle(uptimePercent, result.status)} now={uptimePercent} /></td>
					<td style={{verticalAlign:'middle'}} width='9%'>{uptime}   </td>
					{self.props.isAdmin ?<td style={{verticalAlign:'middle'}} width='3%' align='center'><Link to="/serviceHealth"><img src='settings.png' onClick={() => self.redirectAdminService(result)} height='21' width='21'></img></Link></td> : null }
				</tr>
			)
		}) : null;

		return (
				<div>	
				{operationListDOM != null ? 
					(
						<Table striped bordered condensed hover>
							<thead>
							<tr>
								<th>#</th>
								<th>Servis İsmi</th>
								<th>UpTime</th>
								<th>Res. Süresi</th>
								{this.props.isAdmin ? <th>Düzelt</th> : null }
							</tr>
							</thead>
							<tbody>
							{operationListDOM}
							</tbody>
						 </Table>
					)
					:
					(
					'Kayıt Bulunamadı.'
					)
				}
				</div>
		)

		

	}
}

export default withRouter(UptimeService);
