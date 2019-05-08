import React from 'react';
import ReactDOM from 'react-dom';
import {Form, FormGroup, FormControl, ControlLabel, Checkbox,Grid,Row,Col,option,Button,HelpBlock, Modal} from 'react-bootstrap';
import $ from 'jquery';
import LoadingComponent from './LoadingComponent.jsx';





const globalValue = {
		wsdl : "",
		isAuth : false,
		isAuthOption : false,
		username:"",
		password : ""
}

class AdminServiceRegister extends React.Component{



	constructor(props) {
		super(props);
		this.state = {
			wsdlUrl : "",
			username : "",
			password : "",
			isAuth: "",
			isAuthOption : false,
			hasOperation:false,
			operation : "",
			operations : [],
			loading : false,
			serviceName : "",
			portName : "",
			bindingProtocol : "",
			targetNameSpace : "",
			endpointAddress : "",
			response : "",
			isTest : true,
			modalShow: false,
			modalMessage: ""
		}
		this.fetchServiceOperation = this.fetchServiceOperation.bind(this);
		this.setAuthValue = this.setAuthValue.bind(this);
		this.fetchTestCall = this.fetchTestCall.bind(this);
		this.modalClose = this.modalClose.bind(this);
		this.turnBack = this.turnBack.bind(this);
	}

	
	
	fetchServiceOperation(e) {
		e.preventDefault();
		this.setState({
			loading:true
		});
		
		globalValue.wsdl = this.refWsdlUrl.value;
		globalValue.username = this.refUsername != null && this.refUsername != undefined ? this.refUsername.value : "";
		globalValue.password = this.refPassword != null && this.refPassword != undefined ? this.refPassword.value : "";
		
		const inputData = {wsdlURL:globalValue.wsdl, auth:globalValue.isAuth,
				username:globalValue.username, password:globalValue.password
		};
		
		$.ajax({
				url: 'http://localhost:8080/admin/getServiceDetail',
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				type: 'POST',
				data: JSON.stringify(inputData),
				success: function(data) {
					if(data.success) {
						this.setState({
							loading:false,
							operations:data.operationList,
							serviceName:data.serviceName,
							portName : data.portName,
							bindingProtocol : data.bindingProtocol,
							targetNameSpace : data.targetNamespace,
							hasOperation : true
						});
					} else {
						this.setState({
							loading:false,
							modalShow:true,
							modalMessage:data.message
						});
					}
					console.log(data)
				}.bind(this),
				error: function(xhr, status, err){
					this.setState({
						loading:false
					});
					console.log(data)
				}.bind(this)
			});
	}
	
	fetchTestCall(e) {
		e.preventDefault();
		
		this.setState({
			loading:true
		});
		
		var request = this.refRequest != null && this.refRequest != undefined ? this.refRequest.value : "";
		var response = this.state.response;
		var endpointAddress = this.refEndpointAddress != null && this.refEndpointAddress != undefined ? this.refEndpointAddress.value : "";
		var serviceName = this.state.serviceName;
		var operationName = this.refOperation != null && this.refOperation != undefined ? this.refOperation.value : "";
		var portName = this.state.portName;
		var bindProtocol = this.state.bindingProtocol;
		var targetNameSpace = this.state.targetNameSpace;
		var wsdlAddress = globalValue.wsdl;
		var isAuth = globalValue.isAuth;
		var username = globalValue.username;
		var password = globalValue.password;
		
		
		const inputData = {request:request, response:response,endpointAddress:endpointAddress,
				serviceName:serviceName,operationName:operationName, portName:portName,
				bindProtocol:bindProtocol,wsdlAddress:wsdlAddress,
				targetNameSpace:targetNameSpace,auth:isAuth,
				username:username, password:password
		};
		
		if(this.state.isTest) {
			$.ajax({
					url: 'http://localhost:8080/admin/serviceTest',
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					type: 'POST',
					data: JSON.stringify(inputData),
					success: function(data) {
						this.setState({
							loading:false
						});
						if(data.testResult) {
							this.setState({
								modalShow:true,
								isTest : false,
								modalMessage: 'Test Başarıyla Geçti.Lütfen Kaydete basarak kayıt işlemini gerçekleştiriniz.....',
								response : data.response
							});
						} else {
							this.setState({
								modalShow:true,
								modalMessage: data.message
							});
						}
						console.log(data)
					}.bind(this),
					error: function(xhr, status, err){
						this.setState({
							loading:false
						});
						console.log(data)
					}.bind(this)
				});
		} else {
			$.ajax({
					url: 'http://localhost:8080/admin/serviceRegisterSave',
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					type: 'POST',
					data: JSON.stringify(inputData),
					success: function() {
						this.setState({
							modalShow:true,
							isTest : false,
							loading : false,
							modalMessage: 'Servis Başarıyla Kaydedildi.Yönlendiriliyorsunuz.....'
						});
					}.bind(this),
					error: function(xhr, status, err){
						this.setState({
							loading:false
						});
					}.bind(this)
				});
		}
	}
	
	setAuthValue(e) {
		this.setState({
			isAuthOption:e.target.checked
		});
		globalValue.isAuth = e.target.checked;
	}
	
	modalClose() {
		this.setState({
			modalShow: false,
			modalMessage: ""	
		});
	}
	
	turnBack(e) {
		e.preventDefault();
		this.setState({
			hasOperation : false
		});
	}
	
	componentDidUpdate(prevProps, prevState) {
		if(this.refWsdlUrl != null && this.refWsdlUrl != undefined) {
			this.refWsdlUrl.value = globalValue.wsdl;
		}
		
		if(this.refIsAuth != null && this.refIsAuth != undefined) {
			this.refIsAuth.checked = globalValue.isAuth;
		}
		
		if(this.refUsername != null && this.refUsername != undefined) {
			this.refUsername.value = globalValue.username;
		}
		
		if(this.refPassword != null && this.refPassword != undefined) {
			this.refPassword.value = globalValue.password;
		}
	}
	
	render() {
	
		var serviceOperations = this.state.operations != null ? this.state.operations.map (function(result, index) {
			return(
				<option value={result} key={index}> {result}</option>
			)
		}) : "";
		
		const userPassForm = <div><FormGroup controlId="formUsername">
									<Col componentClass={ControlLabel} sm={2}>
												Kullanıcı Adı:
									</Col>
									<Col sm={10}>
										<FormControl 
											type="text"  
											placeholder="Kullanıcı Adını Giriniz..."
											inputRef={(ref) => {this.refUsername = ref}}
										/>
									</Col>
								</FormGroup>
								
								<FormGroup controlId="formPassword">
									<Col componentClass={ControlLabel} sm={2}>
												Şifre:
									</Col>
									<Col sm={10}>
										<FormControl 
											type="text"  
											placeholder="Şifreyi Giriniz..."
											inputRef={(ref) => {this.refPassword = ref}}
										/>
									</Col>
								</FormGroup></div>;
					
		
		
		return(
			<div>
				<LoadingComponent isActive={this.state.loading} />
				{!this.state.hasOperation ? 
					(
						<Form horizontal onSubmit = {e => this.fetchServiceOperation(e)} >
							<FormGroup controlId="formWSDLURL">
								<Col componentClass={ControlLabel} sm={2}>
											WSDL Adresi:
								</Col>
								<Col sm={10}>
									<FormControl 
										type="text"  
										placeholder="Wsdl Adresini Giriniz.."  
										inputRef={(ref) => {this.refWsdlUrl = ref}}
									/>
								</Col>
							</FormGroup>
							<FormGroup controlId="formIsAuth">
								<Col componentClass={ControlLabel} sm={2}>
											Auth Var mı?:
								</Col>
								<Col sm={10}>
									<Checkbox onChange={e => this.setAuthValue(e)} inputRef={(ref) => {this.refIsAuth = ref}} inline>{this.state.isAuthOption ? ('Varr') : ('Yok') } </Checkbox>
								</Col>
							</FormGroup>
							{this.state.isAuthOption ? userPassForm : ('')};
							<FormGroup>
				    			<Col smOffset={2} sm={10}>
				    					<Button type="submit">Devam</Button>
				   				</Col>
			  				</FormGroup>
			  			</Form>
					) :
				 	(
				 		<Form horizontal >
							<FormGroup controlId="formOperation">
								<Col componentClass={ControlLabel} sm={2}>
											Operasyonu Seçiniz
								</Col>
								<Col sm={10}>
									<FormControl componentClass="select" inputRef={(ref) => {this.refOperation = ref}} >
										<option value="select">Lütfen Seçiniz </option>
												 {serviceOperations}
									</FormControl>
								</Col>
							</FormGroup>
							<FormGroup controlId="formEndPoint">
								<Col componentClass={ControlLabel} sm={2}>
											Endpoint Adresi:
								</Col>
								<Col sm={10}>
									<FormControl 
										type="text"  
										placeholder="EndPoint Adresini Giriniz"
										inputRef={(ref) => {this.refEndpointAddress = ref}}
									/>
								</Col>
							</FormGroup>
							<FormGroup controlId="formResponse">
								<Col componentClass={ControlLabel} sm={2}>
											Request
								</Col>
								<Col sm={4}>
									<FormControl 
										componentClass="textarea" 
										placeholder="SOAP Örnek Request Giriniz."
										inputRef={(ref) => {this.refRequest = ref}}
									/>
								</Col>
								<Col componentClass={ControlLabel} sm={2}>
											Response
								</Col>
								<Col sm={4}>
									<FormControl 
										componentClass="textarea"
										value = {this.state.response}
										placeholder="Test Et Butonuna Basınız."
									/>
								</Col>
							</FormGroup>
							<FormGroup controlId="formSecondSubmit">
								<Col md={2}></Col>
								<Col md={4}>
									<Button bsStyle="danger" onClick = {e => this.turnBack(e)} type = "submit">Geri</Button>
								</Col>
								<Col md={1}></Col>
								<Col md={4}>
									<Button bsStyle="primary" onClick = {e => this.fetchTestCall(e)} type="submit">{this.state.isTest ? ('Test Et') : ('Kaydet') }</Button>
								</Col>
							</FormGroup>
						</Form>
					 ) 
				}
				
				<Modal show={this.state.modalShow} onHide={() => this.modalClose()}>
						<Modal.Header closeButton>
							<Modal.Title>!! Dikkat </Modal.Title>
						</Modal.Header>
						<Modal.Body>
							{this.state.modalMessage}
						</Modal.Body>
						
						<Modal.Footer>
							<Button bsStyle="primary" onClick={() => this.modalClose()}> Tamam </Button>
						</Modal.Footer>
				</Modal>
			</div>
		)
	}
}


export default AdminServiceRegister;