import React from 'react';
import ReactDOM from 'react-dom';
import Sticky from 'react-sticky-el';
import {Pie} from 'react-chartjs-2';
import {Panel,Row, Col} from 'react-bootstrap';
import 'chart.piecelabel.js';


class BlockListPieChart extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
				renderDataOne : null,
				renderDataTwo : null
		}
		this.renderPieChart = this.renderPieChart.bind(this);
		this.handleScroll = this.handleScroll.bind(this);
	}
	
	
	componentDidMount() {
		window.addEventListener('scroll', this.handleScroll, { passive: true });
	}
	
	componentDidUpdate(prevProps, prevState) {
		
		if(this.props.pieDataOne.datasets != null && prevProps.pieDataOne.datasets == null) {
			this.setState({
				renderDataOne :	this.renderPieChart(this.props.titleOne, this.props.pieDataOne),
				renderDataTwo : this.renderPieChart(this.props.titleTwo, this.props.pieDataTwo)
			})
		} else if (this.props.pieDataOne.datasets != null && prevProps.pieDataOne.datasets != null) {
			if(( JSON.stringify(this.props.pieDataOne.datasets[0].hoverBackgroundColor) !== JSON.stringify(prevProps.pieDataOne.datasets[0].hoverBackgroundColor) ) 
					|| ( JSON.stringify(this.props.pieDataTwo.datasets[0].hoverBackgroundColor) !== JSON.stringify(prevProps.pieDataTwo.datasets[0].hoverBackgroundColor) ) ) {
				this.setState({
					renderDataOne :	this.renderPieChart(this.props.titleOne, this.props.pieDataOne),
					renderDataTwo : this.renderPieChart(this.props.titleTwo, this.props.pieDataTwo)
				})
			}
		}
		
	
	}
	
	componentWillUnmount(){
		window.removeEventListener('scroll', this.handleScroll, { passive: true });
	}
	
	renderPieChart (title, data) {
		
		var bodyModel = "";
		
		var options = {
				pieceLabel: {
				    render: 'value',
				    fontColor: '#000',
				    overlap:true,
				    position:'outside',
				    outsidePadding: 4
				  }	
		}
		
		if(data.labels == null || data.datasets == null) {
			bodyModel = <center> Veri Alınamadı </center>
		} else {
			bodyModel = data.labels.length > 0 && data.datasets.length > 0 ? <Pie data={data} legend={{position:"bottom"}} options={options} /> : <center> Veri Alınamadı </center>;
		}
		
		var model = <div>
						<Panel bsStyle="primary">
							<Panel.Heading>
								<Panel.Title componentClass="h3"> {title} </Panel.Title>
							</Panel.Heading>
							<Panel.Body>
								{bodyModel}
							</Panel.Body>
						</Panel>
					</div>;
					
		return model;
	}
	
	
	handleScroll(event){
		var currentTopValue = this.refs.blockRef.getBoundingClientRect().top;
		var currentBottomValue = this.refs.blockRef.getBoundingClientRect().bottom;
		if(currentTopValue < 42) {
			this.refs.blockRef.style.position = "sticky";
			this.refs.blockRef.style.top = "42px";
		} 
		
		console.log(currentBottomValue);
	}
	
	render() {
		return (
				<div  ref="blockRef">
					<Col xs={6} md={4}>
					
						<Row>
							<Col xs={12} md={12}>
								{this.state.renderDataOne}
							</Col>
						</Row>
						<Row>
							<Col xs={12} md={12}>
								{this.state.renderDataTwo}
							</Col>
						</Row>
					
					</Col>
				</div>
				
		)
	}
}

export default BlockListPieChart;