import React from 'react';
import ReactDOM from 'react-dom';
import {Form, FormGroup, FormControl, Col,ControlLabel,option,Button,HelpBlock, Modal} from 'react-bootstrap';
import $ from 'jquery';
import moment from 'moment';
import StartDate from './TextInput.jsx';
import LoadingComponent from './LoadingComponent.jsx';
import {withRouter} from "react-router-dom";


class BlockForm extends React.Component{


	constructor(props) {
		super(props);

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
		var startDate = moment();
		startDate.set('hour', 7);
		startDate.set('minute', 30);
		this.state = {
			blockSystemList: [],
			blockTypeList : [],
			blockName: '',
			blockDesc: '',
			blockSystem: '',
			affectSystem: '',
			affectEnvironment: '',
			startDate: 0,
			blockType: '',
			openBlockUser: this.props.userInfo.name + ' ' + this.props.userInfo.surname,
			maxDate:maxDate,
			startDateInit:startDate,
			modalShow:false,
			resultMessage:'',
			selectedDate: null,
			loading:false
		}

		this.modalClose = this.modalClose.bind(this);
		this.checkFormControl = this.checkFormControl.bind(this);
		this.blockSystemChange = this.blockSystemChange.bind(this);

	}


	componentWillMount(){
		if(!this.props.isLogin) {
			this.props.history.push("/");
		}
		$.ajax({
			url:'/getBlockSystemList',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				this.setState({
					blockSystemList:res.systemList,
				})
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});
		$.ajax({
			url:'/getBlockSystemList',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				this.setState({
					blockSystemList:res.systemList,
				})
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});

		$.ajax({
			url:'/getBlockTypeList',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				this.setState({
					blockTypeList:res.typeList,
				})
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	handleChangeDate(event, date){
		this.setState({
			selectedDate: "Blok Başlangıç Tarihi:  " + date.startDate.format("DD/MM/YYYY").toString() +"      Saati: " +date.startDate.format("HH:mm").toString(),
			startDate: date.startDate.valueOf()
		})
	}


	handleSubmitData(e){
		e.preventDefault();
		this.setState({
			loading:true
		})
		var checkFormHtml = this.checkFormControl();
		if(checkFormHtml == "") {
			const blockName = this.state.blockName.trim();
			const blockDesc = this.state.blockDesc.trim();
			const startDate = this.state.startDate;
			const blockSystem= this.state.blockSystem;
			const affectSystem = this.state.affectSystem;
			const affectEnvironment = this.state.affectEnvironment;
			const blockType = this.state.blockType;
			const openBlockUser =this.state.openBlockUser.trim();
			const userId = this.props.userInfo.id;

			const blockData = {blockName:blockName, blockDesc:blockDesc,
				startDate:startDate, blockSystem:blockSystem,
				affectSystem:affectSystem, affectEnvironment:affectEnvironment,
				blockType:blockType, openBlockUser:openBlockUser, userId:userId, status:true
			};
			var accessToken = localStorage.getItem('accessToken');
			$.ajax({
				url: '/user/saveblock',
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				type: 'POST',
				data: JSON.stringify(blockData),
				headers:{Authorization:"Bearer "+accessToken},
				success: function(data) {
					if(data.status) {
						this.setState({
							loading:false,
							modalTitle:"Bilgi!!",
							modalShow:true,
							resultMessage:"Tebrikler Nur Topu Gibi Bir Bloğunuz Oldu."
						});
					} else {
						this.setState({
							loading:false,
							modalTitle:"Bilgi!!",
							modalShow:true,
							resultMessage:data.result
						});
					}
					console.log("Kaydettik");
				}.bind(this),
				error: function(xhr, status, err){
					if(xhr.status == 401) {
						location.replace("/");
					} else {
						this.setState({
							loading:false,
							modalTitle:"Bilgi!!",
							modalShow:true,
							resultMessage:"Kaydedemedik Babuş Ya Başka Zaman Tekrar Gel Dükkan Bakımda Şimdi."
						});
					}
				}.bind(this)
			});

			this.refBlockName.value = null;
			this.refBlockDesc.value = null;
			this.refBlockSystem.value = "select";
			this.refAffectSystem.value = "select";
			this.refEnvSystem.value = "select";
			this.refBlockType.value = "select";
			this.refOpenUser.value = null;
			this.setState({
				selectedDate:""
			})
		} else {
			this.setState({
				modalTitle:"Dikkat!!",
				modalShow:true,
				loading:false,
				resultMessage:checkFormHtml
			});
		}


	}


	checkFormControl(){
		var result = "";
		const blockName = this.state.blockName.trim();
		const blockDesc = this.state.blockDesc.trim();
		const startDate = this.state.startDate;
		const blockSystem= this.state.blockSystem;
		const affectSystem = this.state.affectSystem;
		const affectEnvironment = this.state.affectEnvironment;
		const blockType = this.state.blockType;
		const openBlockUser =this.state.openBlockUser.trim();

		const startDateObj = moment(this.state.startDate);
		const weekDay = startDateObj.day();
		const hour = startDateObj.get('hour');
		const minute =  startDateObj.get('minute');

		if(blockName == '' || blockName == null){
			result = result + "<li class='danger'> Lütfen Blok İsmini Giriniz </li>";
		}

		if(blockDesc == '' || blockDesc == null){
			result = result + "<li> Lütfen Blok Açıklamasını Giriniz </li>";
		}

		if(startDate == 0 || startDate == null){
			result = result + "<li> Lütfen Başlangıç Tarihini Giriniz </li>";
		}

		if(blockSystem == "select" || blockSystem == null){
			result = result + "<li> Lütfen Blok Yaşatan Sistemi Giriniz </li>";
		}

		if(affectSystem == "select" || affectSystem == null){
			result = result + "<li> Lütfen Blok Yaşayan Sistemi Giriniz </li>";
		}

		if(affectEnvironment == "select" || affectEnvironment == null){
			result = result + "<li> Lütfen Blok Yaşanan Ortamı Giriniz </li>";
		}

		if(blockType == "select" || blockType == null){
			result = result + "<li> Lütfen Blok Tipini Giriniz </li>";
		}

		if(weekDay == 0 || weekDay == 6) {
			result = result + "<li> Lütfen Başlangıç Tarihini İş Günleri Arasında Giriniz </li>";
		}
		if((hour == 7 && minute < 30) || (hour < 7 && hour > 0) ) {
			result = result + "<li> Lütfen Saat 7:30'dan Sonrası için Bir Saat Seçin. </li>";
		}
		if(hour == 16 && minute > 30 ) {
			result = result + "<li> Lütfen Saat 16:30'dan Öncesi için Bir Saat Seçin. </li>";
		}

		return result;
	}

	blockSystemChange(e, systemType) {
		if(systemType == 'blockSystem') {
			this.setState ({
				blockSystem:e.target.value
			});
			if(this.state.affectSystem == e.target.value) {
				this.refAffectSystem.value = "select";
				this.setState({
					affectSystem:"select"
				})
			}
		}
		if(systemType == 'affectSystem') {
			this.setState ({
				affectSystem:e.target.value
			});
			if(this.state.blockSystem == e.target.value) {
				this.refBlockSystem.value = "select";
				this.setState({
					blockSystem:"select"
				})
			}
		}
	}

	modalClose(){
		this.setState({
			modalTitle:"",
			modalShow:false,
			resultMessage:""
		});
	}


	render() {

		var blockSystemOption = this.state.blockSystemList.map(function(result, i) {
			return (
					 <option value={result.systemName} key={i}> {result.systemName} </option>
			)
		});

		var blockTypeOption =  this.state.blockTypeList.map(function(result, i) {
			return (
					<option value={result.blockType} key={i}> {result.blockType}</option>
			)
		});

		return (
			<div>
				<LoadingComponent isActive={this.state.loading} />
				<Form horizontal onSubmit = {this.handleSubmitData.bind(this)} >

						<FormGroup controlId="formBlockName">
								<Col componentClass={ControlLabel} sm={2}>
									Blok İsmi
								</Col>
								<Col sm={10}>
									<FormControl
										type="text"
										onChange={e => this.state.blockName = e.target.value}
										placeholder="Blok İsmini Giriniz"
										inputRef={(ref) => {this.refBlockName = ref}}
										/>
								</Col>
						 </FormGroup>
						 <FormGroup controlId="formBlockDesc">
	 							<Col componentClass={ControlLabel} sm={2}>
	 								Blok Açıklaması
	 							</Col>
	 							<Col sm={10}>
	 								<FormControl
	 									componentClass="textarea"
	 									onChange={e => this.state.blockDesc = e.target.value}
	 									placeholder="Blok Açıklamasını Giriniz"
	 									inputRef={(ref) => {this.refBlockDesc = ref}}
	 									/>
	 							</Col>
	 					 </FormGroup>
						 <FormGroup controlId="formBlockSystem">
	 							<Col componentClass={ControlLabel} sm={2}>
	 								Blok Yaşatan Sistem
	 							</Col>
	 							<Col sm={10}>
	 								<FormControl onChange={e => this.blockSystemChange(e,'blockSystem')} componentClass="select" inputRef={(ref) => {this.refBlockSystem = ref}}>
											<option value="select">Lütfen Seçiniz </option>
											{blockSystemOption}
									</FormControl>
	 							</Col>
	 					 </FormGroup>
						 <FormGroup controlId="formBlockSystem">
								 <Col componentClass={ControlLabel} sm={2}>
									 Blok Yaşanan Sistem
								 </Col>
								 <Col sm={10}>
								<FormControl onChange={e => this.blockSystemChange(e,'affectSystem')} componentClass="select" inputRef={(ref) => {this.refAffectSystem = ref}} >
										 <option value="select">Lütfen Seçiniz </option>
										 {blockSystemOption}
								 </FormControl>
								 </Col>
							</FormGroup>
							<FormGroup controlId="formBlockSystem">
	 							 <Col componentClass={ControlLabel} sm={2}>
	 								 Blok Yaşanan Ortam
	 							 </Col>
	 							 <Col sm={10}>
	 								 <FormControl onChange={e => this.state.affectEnvironment = e.target.value} componentClass="select" inputRef={(ref) => {this.refEnvSystem = ref}} >
	 									 <option value="select">Lütfen Seçiniz </option>
										 <option value="DEV" > DEV </option>
	 									 <option value="STB" > STB </option>
										 <option value="PRP" > PRP </option>
	 							 </FormControl>
	 							 </Col>
	 						</FormGroup>
							<FormGroup controlId="formBlockSystem">
	 							 <Col componentClass={ControlLabel} sm={2}>
	 								 Blok Tipi
	 							 </Col>
	 							 <Col sm={10}>
	 								 <FormControl onChange={e => this.state.blockType = e.target.value} componentClass="select" inputRef={(ref) => {this.refBlockType = ref}} >
	 									 <option value="select">Lütfen Seçiniz </option>
	 									{blockTypeOption}
	 							 </FormControl>
	 							 </Col>
	 						</FormGroup>
							<FormGroup controlId="formBlockDesc">
	  							<Col componentClass={ControlLabel} sm={2}>
	  								Bloku Açan Kullanıcı
	  							</Col>
	  							<Col sm={10}>
	  								<FormControl type="text" value={this.state.openBlockUser} disabled />
	  							</Col>
	  					 </FormGroup>
						 <FormGroup controlId="formBlockStarDate" >
						 	<Col componentClass={ControlLabel} sm={2}>
								Blok Başlangıç Tarihi
							</Col>
							<Col sm={10}>
								<StartDate
									selectedDate = {this.state.selectedDate}
								  handleDate = {this.handleChangeDate.bind(this)}
								  maxDate= {this.state.maxDate}
								  startDate = {this.state.startDateInit}
									singleDatePicker = {true}
								/>
							</Col>
						 </FormGroup>
						 <FormGroup>
	    	 				<Col smOffset={2} sm={10}>
	      					<Button type="submit">Blok Kaydet</Button>
	    					</Col>
	  				</FormGroup>
				</Form>

				<Modal show={this.state.modalShow} onHide={() => this.modalClose()}>
						<Modal.Header closeButton>
							<Modal.Title>{this.state.modalTitle} </Modal.Title>
						</Modal.Header>
						<Modal.Body>
							<ul><td dangerouslySetInnerHTML={{__html: this.state.resultMessage}} /></ul>
						</Modal.Body>

						<Modal.Footer>
							<Button bsStyle="primary" onClick={() => this.modalClose()}> Tamam </Button>
						</Modal.Footer>
				</Modal>
			</div>

		)
	}
}


export default withRouter(BlockForm);
