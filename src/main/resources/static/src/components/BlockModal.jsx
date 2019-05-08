import React from 'react';
import ReactDOM from 'react-dom';
import {Modal,PageHeader,Row,Col, Button, FormGroup, ControlLabel, Panel} from 'react-bootstrap';
import moment from 'moment';
import {withRouter} from "react-router-dom";
import LoadingComponent from './LoadingComponent.jsx';
import $ from 'jquery';
import StartDate from './TextInput.jsx';

class BlockModal extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			loading:false,
			warningModal:false,
			selectedDate:"",
			endDate:0,
			minDate:0,
			maxDate:0,
			infoModal:false,
			infoModalMessage:"",
			blockHours:Number(0),
			deleteModal:false
		}
		this.modalClose = this.modalClose.bind(this);
		this.getBlockDates = this.getBlockDates.bind(this);
		this.updateBlockTemp = this.updateBlockTemp.bind(this);
		this.handleChangeDate = this.handleChangeDate.bind(this);
		this.deleteBlock = this.deleteBlock.bind(this);
		this.checkFormBlockUpdate = this.checkFormBlockUpdate.bind(this);

		moment.locale('tr');
	}

	modalClose() {
		this.props.modalFunc();
	}


	componentDidUpdate(prevProps){
		if(prevProps.modalDetail.startDate != this.props.modalDetail.startDate) {
			var minDate = moment(this.props.modalDetail.startDate);
			var maxDate = moment();


			var sysHour = maxDate.get('hour');
			var sysMinute = maxDate.get('minute');
			if((sysHour == 7 && sysMinute >= 30)
					|| (sysHour > 7 && sysMinute < 16)
					|| (sysHour == 16 && sysMinute <= 30)) {
						maxDate.set('hour',sysHour);
						maxDate.set('minute',sysMinute);
			}
			if((sysHour >= 0 && sysHour < 7)
					|| (sysHour == 7 && sysMinute < 30)) {
						maxDate.dayOfYear(maxDate.dayOfYear() - 1);
						maxDate.set('hour', 16);
						maxDate.set('minute', 30);
			}
			if((sysHour <= 23 && sysHour > 16)
					|| (sysHour == 16 && sysMinute > 30 )) {
						maxDate.set('hour', 16);
						maxDate.set('minute', 30);
			}
			this.setState({
				minDate:minDate,
				maxDate:maxDate
			})
		}

		if(prevProps.modalShow != this.props.modalShow && this.props.modalShow) {
			var blockDetail = this.props.modalDetail.blockDetail != null ? this.props.modalDetail.blockDetail : this.props.modalDetail;

			if(this.props.modalDetail.blockHours != undefined) {
				this.setState({
					blockHours: Number(this.props.modalDetail.blockHours)
				})
			} else {
					var id = blockDetail._id == undefined ? blockDetail.id : blockDetail._id;
					const request = {
						id:id
					}
					$.ajax({
						url: '/getBlockHours',
						dataType: 'json',
						contentType: "application/json; charset=utf-8",
						type: 'POST',
						data: JSON.stringify(request),
						success: function(data) {
							this.setState({
								blockHours: Number(data.blockHours)
							})
						}.bind(this),
						error: function(xhr, status, err){
							this.setState({
								blockHours:Number(0)
							})
							console.log(xhr,status,err);
						}.bind(this)
					});
			}

		}
	}

	getBlockDates(dateMilis){
		if(dateMilis == 0){
			return "-";
		}
		return moment(dateMilis).format("DD MMM YYYY HH:mm");
	}

	updateBlockTemp(){
		this.setState({
			loading:true
		});
		if(this.checkFormBlockUpdate()) {
				var blockDetail = this.props.modalDetail.blockDetail != null ? this.props.modalDetail.blockDetail : this.props.modalDetail;
				var blockId = blockDetail._id != null ? blockDetail._id : blockDetail.id;
				const blockData = {
					id: blockId,
					endDate: this.state.endDate
				}
				var accessToken = localStorage.getItem('accessToken');
				$.ajax({
					url: '/user/updateBlockTemp',
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					type: 'POST',
					data: JSON.stringify(blockData),
					headers:{Authorization:"Bearer "+accessToken},
					success: function(result) {
						if(result.blockDetail != null ){
							this.setState({
								loading:false,
								warningModal:false,
								infoModal:true,
								infoModalMessage:"Tebrikler Bloku Kapattınız. Sevgiler :) "
							});
							this.modalClose();
							console.log("Kaydettik");
							this.props.updateListFunc(result.blockDetail);
						} else {
							this.setState({
								loading:false,
								warningModal:false,
								infoModal:true,
								infoModalMessage:"Bloku Kapatamadım Maalesef İçim de Bir Yer de Bişeyler Bozulmuş Olabilir. Lütfen Tuğra Er'e Bilgi Verin. :) "
							});
						}
					}.bind(this),
					error: function(xhr, status, err){
						if(xhr.status == 401) {
							location.replace("/");
						} else {
							this.setState({
								loading:false,
								warningModal:false,
								infoModal:true,
								infoModalMessage:"Sistemsel Bir Hata Aldık. Lütfen Tuğra Er'e Bilgi Verin."
							});
							this.modalClose();
							console.log("Kaydedemedik");
						}
					}.bind(this)
				});
		}
	}

	checkFormBlockUpdate(){
		const endDate = moment(this.state.endDate);
		const weekDay = endDate.day();
		const hour = endDate.get('hour');
		const minute = endDate.get('minute');
		var result = true;
		var message = "";
		if(weekDay == 0 || weekDay == 6) {
			message = message + "<p> Lütfen İş Günleri İçinde bir Tarih seçin </p>";
			result = false;
		}
		if((hour == 7 && minute < 30) || (hour < 7 && hour >= 0) ) {
			message = message + "Lütfen Saat 7:30'dan Sonrası için Bir Saat Seçin.";
			result = false;
		}
		if(hour == 16 && minute > 30 ) {
			message = message + "Lütfen Saat 16:30'dan Öncesi için Bir Saat Seçin.";
			result = false;
		}

		if(!result) {
			this.setState({
				loading:false,
				warningModal:false,
				infoModal:true,
				infoModalMessage:message
			});
		}
		return result;
	}

	deleteBlock() {
		this.setState({
			loading:true
		});
		var blockDetail = this.props.modalDetail.blockDetail != null ? this.props.modalDetail.blockDetail : this.props.modalDetail;
		var blockId = blockDetail._id != null ? blockDetail._id : blockDetail.id;
		const blockData = {
			id: blockId
		}
		var accessToken = localStorage.getItem('accessToken');
		$.ajax({
			url: '/user/deleteBlock',
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			type: 'POST',
			data: JSON.stringify(blockData),
			headers:{Authorization:"Bearer "+accessToken},
			success: function(result) {
				if(result.status){
					this.setState({
						loading:false,
						deleteModal:false,
						infoModal:true,
						infoModalMessage:"Tebrikler Bloku Sildiniz. "
					});
					this.modalClose();
					console.log("Kaydettik");
					this.props.deleteListFunc();
				} else {
					if(xhr.status == 401) {
						location.replace("/");
					} else {
						this.setState({
							loading:false,
							deleteModal:false,
							infoModal:true,
							infoModalMessage:result.result
						});
					}
				}
			}.bind(this),
			error: function(xhr, status, err){
				this.setState({
					loading:false,
					warningModal:false,
					infoModal:true,
					infoModalMessage:"Sistemsel Bir Hata Aldık. Lütfen Tuğra Er'e Bilgi Verin."
				});
				this.modalClose();
				console.log("Kaydedemedik");
			}.bind(this)
		});
	}

	handleChangeDate(event, date){
		this.setState({
			selectedDate: "Blok Bitiş Tarihi: " + date.startDate.format("DD/MM/YYYY").toString() +"      Saati: " +date.startDate.format("HH:mm").toString(),
			endDate: date.startDate.valueOf()
		})
	}


	render() {

		var blockDetail = this.props.modalDetail.blockDetail != null ? this.props.modalDetail.blockDetail : this.props.modalDetail;

		return(
			<div>
			<LoadingComponent isActive={this.state.loading} />
			<Modal show={this.props.modalShow} onHide={() => this.modalClose()} >
				<Modal.Header closeButton>
					<Modal.Title><b>{blockDetail.blockName}</b> İsimli Blok Detayları </Modal.Title>
				</Modal.Header>
				<Modal.Body>
					<Row className="show-grid">
						<Col xs={12} md={12}>
							<Panel bsStyle="primary" id="collapsible-panel-blockDesc" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Blokun Açıklaması
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.blockDesc}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
					</Row>
					<Row className="show-grid">
						<Col xs={8} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-affectSystem" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Bloktan Etkilenen Sistem
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.affectSystem}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
						<Col xs={4} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-affectEnvironment" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Blok Yaşanan Ortam
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.affectEnvironment}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
					</Row>
					<Row className="show-grid">
						<Col xs={8} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-blockSystem" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Blok Yaşatan Sistem
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.blockSystem}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
						<Col xs={4} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-blockType" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Blok Sebebi
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.blockType}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
					</Row>
					<Row className="show-grid">
						<Col xs={8} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-startDate" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Başlangıç Tarihi
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{this.getBlockDates(blockDetail.startDate)}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
						<Col xs={4} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-endDate" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Bitiş Tarihi
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{this.getBlockDates(blockDetail.endDate)}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
					</Row>
					<Row className="show-grid">
						<Col xs={8} md={6}>
							<Panel bsStyle="primary" id="collapsible-panel-openBlockUser" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Bloku Açan Kullanıcı
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.openBlockUser}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
						<Col xs={4} md={6}>
								<Panel bsStyle="primary" id="collapsible-panel-endBlockUser" defaultExpanded>
									<Panel.Heading>
										<Panel.Title toggle>
											Bloku Kapatan Kullanıcı
										</Panel.Title>
									</Panel.Heading>
									<Panel.Collapse>
										<Panel.Body>
											{!blockDetail.status? blockDetail.endBlockUser : ('-')}
										</Panel.Body>
									</Panel.Collapse>
								</Panel>
						</Col>
					</Row>
					<Row className="show-grid">
						<Col xs={8} md={6}>
							<Panel bsStyle={blockDetail.status ? 'danger':'success'} id="collapsible-panel-status" defaultExpanded>
								<Panel.Heading>
									<Panel.Title toggle>
										Durumu
									</Panel.Title>
								</Panel.Heading>
								<Panel.Collapse>
									<Panel.Body>
										{blockDetail.status ? <div><b>{this.state.blockHours.toFixed(2)}</b> Saattir Devam Ediyor</div> : <div><b>{this.state.blockHours.toFixed(2)}</b> Saat Sonunda Sonlandı </div>}
									</Panel.Body>
								</Panel.Collapse>
							</Panel>
						</Col>
						<Col xs={4} md={6}>
							<Row>
								{blockDetail.status && this.props.isLogin ? <center><Button bsStyle="success" bsSize="large" onClick={() => this.setState({ warningModal: true })}> Blok Sonlandır </Button></center> : ''}
							</Row>
							<Row>
								{this.props.isLogin ? <center><Button bsStyle="danger" bsSize="large" onClick={() => this.setState({ deleteModal: true })}> Blok Sil </Button></center> : ''}
							</Row>
						</Col>
					</Row>

				</Modal.Body>
			</Modal>

			<Modal show={this.state.warningModal}>
		    <Modal.Header>
		      <Modal.Title>Dikkat</Modal.Title>
		    </Modal.Header>

		    <Modal.Body>
					<FormGroup controlId="formBlockStarDate" >
						<Col componentClass={ControlLabel} sm={2}>
							Blok Bitiş Tarihini Giriniz.
						</Col>
						<Col sm={10}>
							<StartDate
								selectedDate = {this.state.selectedDate}
								handleDate = {this.handleChangeDate}
								minDate= {this.state.minDate}
								maxDate= {this.state.maxDate}
								singleDatePicker = {true}
							/>
						</Col>
					 </FormGroup>
				</Modal.Body>

		    <Modal.Footer>
		      <Button onClick={() => this.setState({ warningModal: false })}>Vazgeç</Button>
		      <Button bsStyle="success" onClick={() => this.updateBlockTemp()} >Bloku Kapat</Button>
		    </Modal.Footer>
	  	</Modal>

			<Modal show={this.state.deleteModal}>
		    <Modal.Header>
		      <Modal.Title>Dikkat</Modal.Title>
		    </Modal.Header>

		    <Modal.Body>
						Blok'u silmek istediğinizden emin misiniz?
				</Modal.Body>

		    <Modal.Footer>
		      <Button onClick={() => this.setState({ deleteModal: false })}>Vazgeç</Button>
		      <Button bsStyle="danger" onClick={() => this.deleteBlock()} >Bloku Sil</Button>
		    </Modal.Footer>
	  	</Modal>

			<Modal show={this.state.infoModal}>
		    <Modal.Header>
		      <Modal.Title>Bilgi</Modal.Title>
		    </Modal.Header>

		    <Modal.Body>
						{this.state.infoModalMessage}
				</Modal.Body>

		    <Modal.Footer>
		      <Button bsStyle="primary" onClick={() => this.setState({ infoModal: false })}>Tamam</Button>
		    </Modal.Footer>
	  	</Modal>
			</div>
		)

	}
}

export default withRouter(BlockModal);
