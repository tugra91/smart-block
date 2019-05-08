import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import {Panel,Alert, Grid, Row, Col, Radio, FormControl,Button,ListGroup,ListGroupItem} from 'react-bootstrap';
import {withRouter} from "react-router-dom";
import LoadingComponent from './LoadingComponent.jsx';
import moment from 'moment';


class BlockUpdate extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
				blockInfo : {},
				panelOpen:true,
				loading:false,
				redirectTeam:false,
				wrongTeam:false
		}
		this.handleOkClick = this.handleOkClick.bind(this);
		this.handleCancelClick = this.handleCancelClick.bind(this);
		this.getBlockDates = this.getBlockDates.bind(this);
		moment.locale('tr');
	}

	componentDidMount() {
		var id = this.props.match.params.id;
		$.ajax({
			url:'/getBlockById?id='+id,
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				if(!res.status) {
					this.props.history.push("/");
				}
				this.setState({
					blockInfo:res
				})
			}.bind(this),
			error: function(xhr, status, err){
				this.props.history.push("/");
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	handleOkClick(e){
		e.preventDefault();
		this.setState({
			loading:true
		});
		var id = this.props.match.params.id;
		const blockDesc = this.state.blockDesc.trim();

		const blockData = {blockId:id, desc:blockDesc};
		$.ajax({
			url: '/updateBlockService',
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			type: 'POST',
			data: JSON.stringify(blockData),
			success: function(data) {
				this.setState({
					loading:false
				});
				this.props.history.push("/");
				console.log("Kaydettik");
			}.bind(this),
			error: function(xhr, status, err){
				this.setState({
					loading:false,
					modalTitle:"Bilgi!!",
					modalShow:true,
					resultMessage:"Kaydedemedik Babuş Ya Başka Zaman Tekrar Gel Dükkan Bakımda Şimdi."
				});
			}.bind(this)
		});

		this.refBlockDesc.value = null;
	}

	handleCancelClick() {
		this.props.history.push("/");
	}

	getBlockDates(dateMilis){
		if(dateMilis == 0){
			return "";
		}
		return moment(dateMilis).format("DD MMM YYYY HH:mm");
	}



	render() {
		const blockReplyComp = <ListGroupItem>
												<Row>
													<Col sm={4} md={4}>Bloku Açana Açıklama</Col><Col sm={8} md={8}><FormControl componentClass="textarea" placeholder="Lütfen bir açıklama giriniz." /></Col>
												</Row>
												<Row>
													<Col sm={4} md={4}><div style="float:right" ><Button type="submit">Bloku Geri Gönder</Button></div></Col><Col sm={4} md={4}><Button type="submit">Bloku Kapat</Button></Col>
												</Row>
										   </ListGroupItem>
		return(
				<div>
				<LoadingComponent isActive={this.state.loading} />
        <br />
				<Grid bsClass="container-fluid" >
					<Row>
						<Col xs={8} md={6}>
				      <Panel id="collapsible-panel-block-info" bsStyle="primary" expanded={this.state.panelOpen}>
								<Panel.Heading  onClick={() => this.setState({ panelOpen: !this.state.panelOpen })} >Blok Ayrıntıları</Panel.Heading>
				        <Panel.Collapse>
										<ListGroup>
									    <ListGroupItem>Blok İsmi : <b>{this.state.blockInfo.blockName}</b></ListGroupItem>
									    <ListGroupItem>Blok Açıklaması : <b>{this.state.blockInfo.blockDesc}</b></ListGroupItem>
									    <ListGroupItem>Blok Yaşatan Sistem: <b>{this.state.blockInfo.blockSystem}</b></ListGroupItem>
											<ListGroupItem>Blok Yaşatan Sistem : <b>{this.state.blockInfo.blockName}</b></ListGroupItem>
										  <ListGroupItem>Blok Ortamı : <b>{this.state.blockInfo.affectEnvironment}</b></ListGroupItem>
										  <ListGroupItem>Blok Başlangıç Tarihi: <b>{this.getBlockDates(this.state.blockInfo.startDate)}</b></ListGroupItem>
										  <ListGroupItem>Blok Açan Kullanıcı :<b>{this.state.blockInfo.openBlockUser}</b></ListGroupItem>
									  </ListGroup>
				        </Panel.Collapse>
				      </Panel>
						</Col>
						<Col xs={4} md={6}>
							<Row>
								<Panel id="collapsible-panel-block-reply" bsStyle="primary" expanded={this.state.panelOpen}>
									<Panel.Heading  onClick={() => this.setState({ panelOpen: !this.state.panelOpen })} >Blok Yanıtla</Panel.Heading>
									<Panel.Collapse>
										<Row>
											<Col sm={4} md={4}>
												<center><Radio name="blockReplyGroup" onClick={() => this.setState({ blockReply: true })} inline>Bloku Kapat</Radio></center>
											</Col>
											<Col sm={4} md={4}>
												<center><Radio name="blockReplyGroup" inline>Yanlış Ekip</Radio></center>
											</Col>
											<Col sm={4} md={4}>
												<center><Radio name="blockReplyGroup" inline>Başka Ekibe Yönlendir</Radio></center>
											</Col>
										</Row>
									</Panel.Collapse>
								</Panel>
							</Row>
							<Row>
								<Col><ListGroup>{this.state.blockReply ? blockReplyComp: '' }</ListGroup></Col>
							</Row>
						</Col>
					</Row>
				</Grid>

				</div>
		)

	}
}

export default withRouter(BlockUpdate);
