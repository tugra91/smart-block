import React from 'react';
import ReactDOM from 'react-dom';
import {Nav, NavItem,NavDropdown,MenuItem, Modal,Table,Button} from 'react-bootstrap';
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import BlockList from "./BlockList.jsx";
import BlockForm from "./BlockForm.jsx";
import SearchBlock from "./SearchBlock.jsx";
import GenericDateRangeBlock from "./GenericDateRangeBlock.jsx";
import UptimeService from "./UptimeService.jsx";
import BlockUpdate from "./BlockUpdate.jsx";
import LoginPage from "./LoginPage.jsx";
import LoadingComponent from './LoadingComponent.jsx';
import BlockModal from './BlockModal.jsx';
import UserModal from './UserModal.jsx';
import InfoModal from './InfoModal.jsx';
import $ from 'jquery';
import moment from 'moment';

class MainPage extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
				activeKey:0,
				isLogin:false,
				userInfo:{},
				applyUserModal:false,
				waitUserList:[],
				infoModal:false,
				infoModalMessage:"",
				applyUserLoading:false,
				isRegister:false
		}

		this.selectNav = this.selectNav.bind(this);
		this.logout = this.logout.bind(this);
		this.isAdminUser = this.isAdminUser.bind(this);
		this.waitUsersModal = this.waitUsersModal.bind(this);
		this.callGetWaitUsers = this.callGetWaitUsers.bind(this);
		this.adminDeleteOrApplyUser = this.adminDeleteOrApplyUser.bind(this);
		this.getAccessTokenViaRefreshToken = this.getAccessTokenViaRefreshToken.bind(this);
		this.getUserInformation = this.getUserInformation.bind(this);
		this.registerFunc = this.registerFunc.bind(this);
		this.infoModalStatus = this.infoModalStatus.bind(this);
	}



	getAccessTokenViaRefreshToken(tokenData) {
		$.ajax({
			url:'/getAccessTokenForRT',
			dataType:'json',
			cache:false,
			contentType: "application/json; charset=utf-8",
			type: 'POST',
			data: JSON.stringify(tokenData),
			success: function(res){
				localStorage.clear();
				if(res.status) {
					localStorage.setItem('accessToken',data.accessToken);
					this.getUserInformation();
				}
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	componentWillMount(){
		this.getUserInformation();
	}

	getUserInformation() {
		var accessToken = localStorage.getItem('accessToken');
		if(accessToken != null){
			$.ajax({
				url:'/user/getuserinformation',
				dataType:'json',
				cache:false,
				headers:{Authorization:"Bearer "+accessToken},
				success: function(res){
					this.setState({
						userInfo:res,
						isLogin:true
					});
				}.bind(this),
				error: function(xhr, status, err){
					if(xhr.status == 401) {
						var tokenData = {
							code: accessToken
						};
						this.getAccessTokenViaRefreshToken(tokenData);
					}
					console.error(status,err.toString());
				}.bind(this)
			});
		}
	}

	logout(){
		localStorage.clear();
		location.replace("/");
	}

	registerFunc(data, updateUserData) {
		this.setState({
			isRegister:data,
			userInfo:updateUserData != null ? updateUserData : this.state.userInfo
		})
	}

	infoModalStatus(data) {
		this.setState({
			infoModal:data
		})
	}


	isAdminUser(){
		var isAdmin = false;
		if(this.state.isLogin) {
		 	this.state.userInfo.roles.map(function(result,index){
				if(result == 'ROLE_ADMIN') {
					isAdmin = true;
				}
			})
		}
		return isAdmin;
	}


	selectNav(selectedKey){
		this.setState({
			activeKey:selectedKey
		})
	}

	waitUsersModal(openModal) {
		if(openModal) {
			this.callGetWaitUsers();
		} else {
			this.setState({
				applyUserModal:false,
				waitUserList:[]
			})
		}
	}

	callGetWaitUsers(){
		var accessToken = localStorage.getItem('accessToken');
		if(this.state.isLogin) {
			$.ajax({
				url:'/admin/getWaitUsers',
				dataType:'json',
				cache:false,
				headers:{Authorization:"Bearer "+accessToken},
				success: function(res){
					this.setState({
						applyUserModal:true,
						waitUserList:res.approveUserList
					});
				}.bind(this),
				error: function(xhr, status, err){
					console.error(status,err.toString());
				}.bind(this)
			});
		} else {
			this.setState({
				infoModal:true,
				infoModalMessage:"Login olmadığınız yada User bilgileri alınmadığı için Onaylama listesi açılamadı."
			});
		}
	}

	adminDeleteOrApplyUser(isDelete, userInformation) {

		if(this.state.isLogin
		  && userInformation != null) {
				this.setState({
					applyUserLoading:true
				})
				var accessToken = localStorage.getItem('accessToken');
				var lastUrl = "";
				if(isDelete) {
					lastUrl = "deleteUser";
				} else {
					lastUrl = "applyUser";
				}
				$.ajax({
					url:'/admin/'+lastUrl,
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					type: 'POST',
					data: JSON.stringify(userInformation),
					headers:{Authorization:"Bearer "+accessToken},
					success: function(res){
						if(res.status) {
							this.setState({
								infoModal:true,
								infoModalMessage:res.result,
								applyUserLoading:false
							});
							this.callGetWaitUsers();
						} else {
							this.setState({
								infoModal:true,
								infoModalMessage:res.result,
								applyUserLoading:false
							});
						}
					}.bind(this),
					error: function(xhr, status, err){
						this.setState({
							infoModal:true,
							infoModalMessage:"Sunucuyla bağlantıda bir hata yaşıyoruz. Lütfen Daha Sonra Tekrar Deneyiniz.",
							applyUserLoading:false
						});
						console.error(status,err.toString());
					}.bind(this)
				});
			} else {
				this.setState({
					infoModal:true,
					infoModalMessage:"Login olmadığınız yada User bilgileri alınmadığı için kullanıcı için işlem yapılamadı."
				});
			}
	}



	render() {
		var mainPageLabel = this.state.activeKey == 0 ? (<font color="white">Ana Sayfa</font>):(<font color="#FEC640">Ana Sayfa</font>);
		var blockEntryLabel = this.state.activeKey == 1 ? (<font color="white">Blok Girişi Yap</font>):(<font color="#FEC640">Blok Girişi Yap</font>);
		var blockSearchLabel = this.state.activeKey == 2 ? (<font color="white">Blok Ara</font>):(<font color="#FEC640">Blok Ara</font>);
		var todayBlockLabel = this.state.activeKey == 3 ? (<font color="white">Bugünün Blokları</font>):(<font color="#FEC640">Bugünün Blokları</font>);
		var weekBlockLabel = this.state.activeKey == 4 ? (<font color="white">Bu Haftanın Blokları</font>):(<font color="#FEC640">Bu Haftanın Blokları</font>);
		var monthBlockLabel = this.state.activeKey == 5 ? (<font color="white">Bu Ayın Blokları</font>):(<font color="#FEC640">Bu Ayın Blokları</font>);
		var loginPageLabel = this.state.activeKey == 6 ? (<font color="white">Giriş Yap</font>):(<font color="#FEC640">Giriş Yap</font>);
		var userInfoLabel = <font color="white">Hoşgeldiniz, {this.state.userInfo.name} </font>;
		var myBlocksLabel = this.state.activeKey == 7.1 ? (<font color="white">Bloklarım</font>):(<font color="#FEC640">Bloklarım</font>);
		var editUserInformationLabel = this.state.activeKey == 7.2 ? (<font color="white">Bilgilerimi Düzenle</font>):(<font color="#FEC640">Bilgilerimi Düzenle</font>);
		var adminApplyUser = this.state.activeKey == 7.3 ? (<font color="white">Kullanıcı Onayla</font>):(<font color="#FEC640">Kullanıcı Onayla</font>);
		var logoutLabel = this.state.activeKey == 7.9 ? (<font color="white">Çıkış Yap</font>):(<font color="#FEC640">Çıkış Yap</font>);

		const waitUsersTable = this.state.waitUserList.map((result,index) => (
				<tr>
					<td>#{index+1}</td>
		      <td>{result.username}</td>
		      <td>{result.email}</td>
		      <td>{result.name}</td>
		      <td>{result.surname}</td>
		      <td>{result.createdDate != 0 ? moment(result.createdDate).format("DD MMM YYYY HH:mm") : ('')}</td>
		      <td><Button bsStyle="danger" onClick={() => this.adminDeleteOrApplyUser(true, result)}>Sil</Button></td>
		      <td><Button bsStyle="success" onClick={() => this.adminDeleteOrApplyUser(false, result)}>Onayla</Button></td>
    		</tr>
			));

		return(
		<Router>
			<div class="container-fluid">

				<div class="navbar-fixed-top">
						<Nav bsStyle="pills" activeKey={this.state.activeKey} onSelect={e=>this.selectNav(e)}>
							<NavItem eventKey={0} >
								<Link to="/"> {mainPageLabel} </Link>
							</NavItem>
							{this.state.isLogin ?
								<NavItem eventKey={1} >
									<Link to="/blockSave">  {blockEntryLabel} </Link>
								</NavItem> : ('')
							}
							<NavItem eventKey={2} >
								<Link to="/searchBlock">{blockSearchLabel} </Link>
							</NavItem>
							<NavItem eventKey={3} >
								<Link to="/todayBlock">{todayBlockLabel} </Link>
							</NavItem>
							<NavItem eventKey={4} >
								<Link to="/weekBlock">{weekBlockLabel} </Link>
							</NavItem>
							<NavItem eventKey={5}>
								<Link to="/monthBlock">{monthBlockLabel} </Link>
							</NavItem>
							{!this.state.isLogin ?
								<NavItem eventKey={6}>
									<Link to="/login">{loginPageLabel} </Link>
								</NavItem>
								: ('')
							}
							<NavItem>
							</NavItem>
							<NavItem>
							</NavItem>
							{this.state.isLogin ?
							<NavDropdown title={userInfoLabel} id="basic-nav-dropdown">
								<MenuItem eventKey={7.1}><Link to="/myBlocks">{myBlocksLabel} </Link></MenuItem>
								<MenuItem eventKey={7.2}><p onClick={() => {this.setState({isRegister:true})}}>{editUserInformationLabel}</p> </MenuItem>
								<MenuItem divider />
								{this.isAdminUser() ? <MenuItem eventKey={7.3}><p onClick={() => this.waitUsersModal(true)} >{adminApplyUser} </p></MenuItem> : ('')}
								<MenuItem eventKey={7.9}><p onClick={() => this.logout()} >{logoutLabel} </p></MenuItem>
							</NavDropdown>
							 : ('')
							 }
						</Nav>

					</div>
					<div style={{marginTop:90}}>
						<Route exact path="/" render={(props) => <BlockList isLogin={this.state.isLogin} userInfo={this.state.userInfo} openApplyUser = {this.state.openApplyUser} openApplyUserFunc={this.linkApplyUser} />}  />
						<Route path="/blockSave" render={(props) => <BlockForm isLogin={this.state.isLogin} userInfo={this.state.userInfo} />} />
						<Route path="/searchBlock" render={(props) => <SearchBlock isLogin={this.state.isLogin} />} />
						<Route path="/todayBlock/" render={(props,params) => <GenericDateRangeBlock isLogin={this.state.isLogin} dateRange= "Today" />}  />
						<Route path="/weekBlock/" render={(props) => <GenericDateRangeBlock isLogin={this.state.isLogin} dateRange= "Week" />} />"
						<Route path="/monthBlock/" render={(props) => <GenericDateRangeBlock isLogin={this.state.isLogin} dateRange= "Month" />} />
						<Route path="/uptimeService" component={UptimeService} />
						<Route path="/blockUpdate/:id" component={BlockUpdate} />
						<Route path="/login" render={(props) => <LoginPage isLogin={this.state.isLogin} />} />
					</div>


					<LoadingComponent isActive={this.state.applyUserLoading} />
					<Modal show={this.state.applyUserModal} bsSize="large" onHide = {() => this.waitUsersModal(false)}>
		        <Modal.Header closeButton>
		          <Modal.Title>Onaylanmayı Bekleyen Kullanıcı Listesi</Modal.Title>
		        </Modal.Header>
		        <Modal.Body>
								<Table responsive>
										<thead>
											<tr>
												<th>#</th>
												<th>Kullanıcı Adı</th>
												<th>E-Mail</th>
												<th>İsim</th>
												<th>Soyisim</th>
												<th>Kayıt Tarihi</th>
												<th></th>
												<th></th>
											</tr>
										</thead>
										<tbody>
											{waitUsersTable}
										</tbody>
								</Table>
		        </Modal.Body>
		        <Modal.Footer>
		          <Button bsStyle="primary" onClick={() => this.waitUsersModal(false)}> Tamam </Button>
		        </Modal.Footer>
		      </Modal>

					<InfoModal
            modalShow={this.state.infoModal}
            modalFunc={this.infoModalStatus}
            isFail={false}
            message={this.state.infoModalMessage}  />

					<UserModal
	          isUpdate = {true}
	          userInfo = {this.state.userInfo}
	          isRegister = {this.state.isRegister}
	          registerModalFunc = {this.registerFunc}
	        />

				</div>
		</Router>

		)
	}
}

export default MainPage;
