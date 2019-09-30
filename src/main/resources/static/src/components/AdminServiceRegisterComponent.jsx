import React from 'react';
import ReactDOM from 'react-dom';
import heartbeats from 'heartbeats';
import {Form, FormGroup, FormControl, ControlLabel, Checkbox,Grid,Row,Col,option,Button,HelpBlock, Modal, ListGroup, ListGroupItem} from 'react-bootstrap';
import $ from 'jquery';
import LoadingComponent from './LoadingComponent.jsx';
import {withRouter} from "react-router";





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
			isAuth: false,
			isAuthOption : false,
			hasBinding:false,
			bindList : [],
			operationList: [],
			operationIndex : null,
			loading : false,
			serviceName : "",
			serviceURL : "",
			serviceWsdlURL : "",
			serviceType : 0,
			operationName : "",
			soapAction : "",
			bindingName : "",
			sourceSystem : "",
			targetSystem : [],
			methodType : "",
			listGroupBindClick : false,
			request : "",
			response : "",
			isTest : true,
			modalShow: false,
			modalMessage: "",
			blockSystemList:[],
			affectEnvironment : "",
			isOperationList:[],
			isUpdate : false,
			listGroupOperationClick:false,
			isReqResCheck:false,
			slaTime:null
		}
		this.fetchServiceOperation = this.fetchServiceOperation.bind(this);
		this.setAuthValue = this.setAuthValue.bind(this);
		this.fetchTestCall = this.fetchTestCall.bind(this);
		this.modalClose = this.modalClose.bind(this);
		this.turnBack = this.turnBack.bind(this);
		this.clickBindingItem = this.clickBindingItem.bind(this);
		this.checkSLANumber = this.checkSLANumber.bind(this);
		this.fetchBlockSystemList = this.fetchBlockSystemList.bind(this);
		this.cleanStates = this.cleanStates.bind(this);
	}

	componentWillMount(){
		if(!this.props.isLogin) {
			this.props.history.push("/");
		}

		if(this.props.isUptime) {
			this.fetchBlockSystemList();
			var serModel = this.props.serviceModel;
	

			this.setState({
				wsdlUrl : serModel.serviceWsdlURL,
				username : serModel.userName,
				password : serModel.password,
				isAuth: serModel.isAuth,
				serviceName:serModel.serviceName,
				serviceURL:serModel.serviceURL,
				request : serModel.request,
				response : serModel.response,
				operationName : serModel.operationName,
 				soapAction : serModel.soapAction,
				bindingName : serModel.bindingName,
				sourceSystem : serModel.sourceSystem,
				targetSystem : serModel.targetSystem,
				affectEnvironment : serModel.env,
				slaTime : serModel.slaTime,
				isReqResCheck:serModel.reqResCheck,
				hasBinding:true,
				listGroupBindClick:false,
				listGroupOperationClick:true,
				isUpdate:true
			});
		}
	}

	componentWillUnmount(){
		this.cleanStates();
		this.props.cleanPropsAdminServiceFunc();
	}

	cleanStates(){


		this.setState({
			wsdlUrl : "",
			username : "",
			password : "",
			isAuth: false,
			isAuthOption : false,
			hasBinding:false,
			bindList : [],
			operationList: [],
			operationIndex : null,
			loading : false,
			serviceName : "",
			serviceURL : "",
			serviceWsdlURL : "",
			serviceType : 0,
			operationName : "",
			soapAction : "",
			bindingName : "",
			sourceSystem : "",
			targetSystem : [],
			methodType : "",
			listGroupBindClick : false,
			request : "",
			response : "",
			isTest : true,
			modalShow: false,
			modalMessage: "",
			blockSystemList:[],
			affectEnvironment : "",
			isOperationList:[],
			isUpdate : false,
			listGroupOperationClick:false,
			slaTime:null,
			isReqResCheck:false
		});
	}

	fetchBlockSystemList() {
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
	}
	
	fetchServiceOperation(e) {
		e.preventDefault();
		this.setState({
			loading:true
		});
		

		
		

		const inputData = {serviceWsdlURL:this.state.wsdlUrl, hasAuth:this.state.isAuth,
				userName:this.state.username, password:this.state.password
		};

		this.fetchBlockSystemList();

		var accessToken = localStorage.getItem('accessToken');
		$.ajax({
				url: '/threadService/getBindigns',
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				type: 'POST',
				data: JSON.stringify(inputData),
				headers:{Authorization:"Bearer "+accessToken},
				success: function(data) {
					if(data.result) {
						this.setState({
							loading:false,
							bindList:data.bindingList,
							hasBinding : true
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
						loading:false,
						modalShow:true,
						modalMessage:data.message
					});
					console.log(data)
				}.bind(this)
			});
	}

	clickBindingItem(e, index) {
		var bindingName = this.state.bindList[index].bindingName;
		var serviceURL = this.state.bindList[index].endpointAddres;
		var operationList = this.state.bindList[index].operationList;


		const inputData = {wsdlURL:this.state.wsdlUrl, bindingName:bindingName};

		this.setState({
			loading:true
		});
		var accessToken = localStorage.getItem('accessToken');
		$.ajax({
				url: '/threadService/getHasOperationList',
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				type: 'POST',
				data: JSON.stringify(inputData),
				headers:{Authorization:"Bearer "+accessToken},
				success: function(data) {
					if(data.result) {
						this.setState({
							loading:false,
							isOperationList:data.isOperationList
						});
					} else {
						this.setState({
							loading:false
						});
					}
					console.log(data)
				}.bind(this),
				error: function(xhr, status, err){
					if(xhr.status == 401) {
						location.replace("/");
					} else {
						this.setState({
								loading:false
							});
					}
				}.bind(this)
			});

		this.setState({
			"listGroupBindClick":true,
			"bindingName":bindingName,
			"serviceURL":serviceURL,
			"operationList":operationList
		});
	}

	clickOperationItem(e, index) {
		var operation = this.state.operationList[index];
		var enterUpdate = false;
		for(var i= 0; i<this.state.isOperationList.length; i++ ) {
			if(this.state.isOperationList[i].operationName == operation.operationName) {
				this.setState({
					"serviceName":this.state.isOperationList[i].serviceName,
					"serviceURL":this.state.isOperationList[i].serviceURL,
					"serviceType":this.state.isOperationList[i].serviceType,
					"request" : this.state.isOperationList[i].request,
					"response" : this.state.isOperationList[i].response,
					"sourceSystem" :this.state.isOperationList[i].sourceSystem,
					"targetSystem" :this.state.isOperationList[i].targetSystem,
					"affectEnvironment" :this.state.isOperationList[i].env,
					"methodType":this.state.isOperationList[i].methodType,
					"segment":this.state.isOperationList[i].segment,
					"isReqResCheck":this.state.isOperationList[i].reqResCheck,
					"isUpdate":true
				});
				enterUpdate = true;
				break;
			}
		}

		if(!enterUpdate) {
			this.setState({
				"serviceName":"",
				"serviceType":0,
				"request" : "",
				"response" : "",
				"sourceSystem" :"",
				"targetSystem" :[],
				"affectEnvironment" :"",
				"isReqResCheck":false,
				"isUpdate":false
			});
		}


		this.setState({
			"operationName":operation.operationName,
			"soapAction":operation.soapAction,
			"listGroupOperationClick":true
		});
	}
	
	fetchTestCall(e) {
		
		
		e.preventDefault();
		var accessToken = localStorage.getItem('accessToken');

		this.setState({
			loading:true
		});
		
		var servicename = this.state.serviceName;
		var serviceurl = this.state.serviceURL;
		var servicewsdlurl = this.state.wsdlUrl;
		var servicetype = 0;
		var hasauth = this.state.isAuth;
		var username = this.state.username;
		var password = this.state.password;
		var request = this.state.request;
		var response = this.state.response;
		var operationname = this.state.operationName;
		var soapaction = this.state.soapAction;
		var bindingname = this.state.bindingName;
		var sourcesystem = this.state.sourceSystem;
		var targetsystem = this.state.targetSystem;
		var env = this.state.affectEnvironment;
		var isReqResCheck =this.state.isReqResCheck;
		var methodType = "POST";
		var segment = "MARKETING_SOLUTION";
		var slaTime = this.state.slaTime;

		if(sourcesystem == 'select') {
			sourcesystem = "";
		}

		if(env == 'select') {
			env = "";
		}

		if(targetsystem.includes("select")) {
			targetsystem = [];
		}
		
		
		const inputData = {serviceName:servicename, serviceURL:serviceurl,serviceWsdlURL:servicewsdlurl,
				serviceType:servicetype,hasAuth:hasauth, userName:username,
				password:password,request:request,
				response:response, operationName:operationname,
				soapAction:soapaction, bindingName:bindingname,
				sourceSystem:sourcesystem, targetSystem:targetsystem,
				env:env, uptime:0,
				slaTime:slaTime, methodType:methodType,
				reqResCheck:isReqResCheck,
				segment:segment,status:true
		};
		
		if(this.state.isTest) {
			$.ajax({
					url: '/threadService/testService',
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					type: 'POST',
					data: JSON.stringify(inputData),
					headers:{Authorization:"Bearer "+accessToken},
					success: function(data) {
						this.setState({
							loading:false
						});
						if(data.result) {
							this.setState({
								modalShow:true,
								isTest : false,
								modalMessage: 'Test Başarıyla Geçti.Lütfen Kaydete basarak kayıt işlemini gerçekleştiriniz.....',
								response : data.message
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
						if(xhr.status == 401) {
							location.replace("/");
						} else {
							this.setState({
								loading:false
							});
						}
					}.bind(this)
				});
		} else {
			if(!this.state.isUpdate) {
				$.ajax({
						url: '/threadService/saveService',
						dataType: 'json',
						contentType: "application/json; charset=utf-8",
						type: 'POST',
						data: JSON.stringify(inputData),
						headers:{Authorization:"Bearer "+accessToken},
						success: function() {
							this.setState({
								modalShow:true,
								isTest : false,
								loading : false,
								modalMessage: 'Servis Başarıyla Kaydedildi.Yönlendiriliyorsunuz.....'
							});
							location.replace("/");
						}.bind(this),
						error: function(xhr, status, err){
							if(xhr.status == 401) {
								location.replace("/");
							} else {
								this.setState({
									loading:false
								});
							}
						}.bind(this)
					});
			} else {
					$.ajax({
						url: '/threadService/updateService',
						dataType: 'json',
						contentType: "application/json; charset=utf-8",
						type: 'POST',
						data: JSON.stringify(inputData),
						headers:{Authorization:"Bearer "+accessToken},
						success: function() {
							this.setState({
								modalShow:true,
								isTest : false,
								loading : false,
								modalMessage: 'Servis Başarıyla Güncellendi.Yönlendiriliyorsunuz.....'
							});
							location.replace("/");
						}.bind(this),
						error: function(xhr, status, err){
							if(xhr.status == 401) {
								location.replace("/");
							} else {
								this.setState({
									loading:false
								});
							}
						}.bind(this)
					});
			}
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
			listGroupOperationClick : false
		});
	}

	turnBackBinding(e) {
		e.preventDefault();
		this.setState({
			hasBinding:false
		})
	}
	


	blockSystemChange(e, systemType) {
		var self = this;
		if(systemType == 'blockSystem') {
			this.setState ({
				sourceSystem:e.target.value
			});
		}
		if(systemType == 'affectSystem') {
			
			var selected = $(e.target).val();
			console.log(selected);
			if(selected.includes("select")) {
				$(e.target).val(["select"]);
			}
			self.setState({
				targetSystem:selected
			});
		}
	}

	isOperationHas(operationName) {
		for(var i= 0; i<this.state.isOperationList.length; i++ ) {
			if(this.state.isOperationList[i].operationName == operationName) {
				return true;
			}
		}
	}

	getServiceName(operationName) {
		for(var i= 0; i<this.state.isOperationList.length; i++ ) {
			if(this.state.isOperationList[i].operationName == operationName) {
				return " - " + this.state.isOperationList[i].serviceName;
			}
		}
	}

	checkSLANumber(e) {
		var value = e.target.value;
		if(((/^\d*$/.test(value)) || value == null || value == "" ) && value < 300000){
			this.setState({
				slaTime:e.target.value
			});
		} else {
			this.setState({
				slaTime:null
			});
			e.target.value = null;
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
											value = {this.state.username}
											onChange={(e) => {this.setState({username:e.target.value})}}
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
											value = {this.state.password}
											onChange={(e) => {this.setState({password:e.target.value})}}
										/>
									</Col>
								</FormGroup></div>;

		
		var listGroupBind = this.state.bindList.map((result, index) => (
			<ListGroupItem onClick = {e => this.clickBindingItem(e, index)}>{result.bindingName}</ListGroupItem>
		));

		var listGroupOperation = this.state.operationList.map((result, index) => (
			<ListGroupItem bsStyle={this.isOperationHas(result.operationName) ? ('success') : ''} onClick = {e => this.clickOperationItem(e, index, result.operationName)}>{result.operationName} {this.getServiceName(result.operationName)}</ListGroupItem>
		));

		const bindOperationList = <div>
									<Grid>
										<Row>
										{!this.state.listGroupOperationClick ? 
											(<Col md={12}>
												<Button bsStyle="danger" onClick = {e => this.turnBackBinding(e)} type = "submit">Geri</Button>
											</Col>)
										:('')}
										</Row>
										<Row>
										 {this.state.hasBinding && !this.state.listGroupOperationClick ? (<Col xs={12} sm={12}>
												<ListGroup>
													{listGroupBind}
												</ListGroup>
											</Col>): ('')}

										 {this.state.listGroupBindClick && !this.state.listGroupOperationClick ? (
											<Col sm={6}>
												<ListGroup>
													 {listGroupOperation}
											   </ListGroup>
											</Col>
											) : ('')}
										</Row>
									</Grid>
								  </div>

		
					
		var blockSystemOption = this.state.blockSystemList.map(function(result, i) {
			return (
					 <option value={result.systemName} key={i}> {result.systemName} </option>
			)
		});
		
		return(
			<div>
				<LoadingComponent isActive={this.state.loading} />

				<Grid>
					<Row>
						<Col xs={12} sm={12} >
							{!this.state.hasBinding ? 
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
													value = {this.state.wsdlUrl}
													onChange={(e) => {this.setState({wsdlUrl:e.target.value})}}
												/>
											</Col>
										</FormGroup>
										<FormGroup controlId="formWSDLSLA">
											<Col componentClass={ControlLabel} sm={2}>
														Timeout Süresi:
											</Col>
											<Col sm={10}>
												<FormControl 
													type="text"  
													placeholder="Servisin Timeout Süresini Giriniz."
													value = {this.state.slaTime}
													onChange={(e) => this.checkSLANumber(e)}
											/>
											</Col>
										</FormGroup>
										<FormGroup controlId="formIsAuth">
											<Col componentClass={ControlLabel} sm={2}>
														Authorization Var mı?:
											</Col>
											<Col sm={10}>
												<Checkbox 
												value = {this.state.isAuth}
												onChange={(e) => {this.setState({isAuth:e.target.checked})}}
												inline>{this.state.isAuth ? ('Var') : ('Yok') } 
												</Checkbox>
											</Col>
										</FormGroup>
										{this.state.isAuth ? userPassForm : ('')}
										<FormGroup>
				    						<Col smOffset={2} sm={10}>
				    								<Button type="submit">Devam</Button>
				   							</Col>
			  							</FormGroup>
			  						</Form>
								) : bindOperationList }
							</Col>
						</Row>
					
						{this.state.listGroupOperationClick ?  (
						<Row>
							<Col xs={12} sm={12}>
				 				<Form horizontal >


									<FormGroup controlId="formEndPoint">
										<Col componentClass={ControlLabel} sm={2}>
													Servisin Adı:
										</Col>
										<Col sm={10}>
											<FormControl 
												type="text"  
												placeholder="Servis Adını Giriniz."
												value = {this.state.serviceName}
												onChange={(e) => {this.setState({serviceName:e.target.value})}}
											/>
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
												value = {this.state.serviceURL}
												onChange={(e) => {this.setState({serviceURL:e.target.value})}}
											/>
										</Col>
									</FormGroup>

									<FormGroup>
										<Col componentClass={ControlLabel} sm={2}>
													Servisin Çalıştığı Ortam:
										</Col>
										<Col sm={10}>
											 <FormControl 
											 value = {this.state.affectEnvironment}
											 onChange={(e) => {this.setState({affectEnvironment:e.target.value})}}
											 componentClass="select" >
	 											 <option value="select">Lütfen Seçiniz </option>
	 											 <option value="STB" > STB </option>
												 <option value="PRP" > PRP </option>
	 										</FormControl>
										</Col>
									</FormGroup>

									<FormGroup controlId="formResponse">
										<Col componentClass={ControlLabel} sm={2}>
													Request
										</Col>
										<Col sm={3}>
											<FormControl 
												componentClass="textarea" 
												placeholder="SOAP Örnek Request Giriniz."
												value = {this.state.request}
											    onChange={(e) => {this.setState({request:e.target.value})}}
											/>
										</Col>
										<Col componentClass={ControlLabel} sm={2}>
													Response
										</Col>
										<Col sm={3}>
											<FormControl 
												componentClass="textarea"
												value = {this.state.response}
												placeholder="Test Et Butonuna Basınız."
											/>
										</Col>
										<Col sm={2}>
												<Checkbox 
												value = {this.state.isReqResCheck}
												checked = {this.state.isReqResCheck}
												onChange={(e) => {this.setState({isReqResCheck:e.target.checked})}}
												inline>{this.state.isReqResCheck ? ('Check') : ('No Check') } 
												</Checkbox>
										</Col>
									</FormGroup>

									<FormGroup controlId="formSourceSystem">
										<Col componentClass={ControlLabel} sm={2}>
													Kaynak Sistem
										</Col>
										<Col sm={4}>
											<FormControl 
											onChange={e => this.blockSystemChange(e,'blockSystem')} 
											componentClass="select"
											value = {this.state.sourceSystem}
											>
													<option value="select">Lütfen Seçiniz </option>
													{blockSystemOption}
											</FormControl>
										</Col>
										<Col componentClass={ControlLabel} sm={2}>
													Hedef Sistem/ler
										</Col>
										<Col sm={4}>
										
												<FormControl componentClass="select" multiple 
												onChange={e => this.blockSystemChange(e,'affectSystem')} 
												onBlur={e => this.blockSystemChange(e,'affectSystem')}
												value = {this.state.targetSystem}
												
												>
													<option value="select">Lütfen Seçiniz </option>
													{blockSystemOption}
												</FormControl>
										
										</Col>
									</FormGroup>

									<FormGroup controlId="formSecondSubmit">
										<Col md={2}></Col>
										<Col md={4}>
											{!this.props.isUptime ? <Button bsStyle="danger" onClick = {e => this.turnBack(e)} type = "submit">Geri</Button>:null}
										</Col>
										<Col md={1}></Col>
										<Col md={4}>
											<Button bsStyle="primary" onClick = {e => this.fetchTestCall(e)} type="submit">{this.state.isTest ? ('Test Et') : ('Kaydet') }</Button>
										</Col>
									</FormGroup>
								</Form>
							</Col>
						</Row>
						) : '' }
					</Grid>
				
				
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


export default withRouter(AdminServiceRegister);