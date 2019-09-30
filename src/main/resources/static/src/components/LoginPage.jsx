import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import {FormControl,Col,Row,option,Button,Grid,Modal,ListGroup,ListGroupItem,FormGroup,ControlLabel} from 'react-bootstrap';
import {withRouter} from "react-router";
import LoadingComponent from './LoadingComponent.jsx';
import UserModal from "./UserModal.jsx";
import InfoModal from "./InfoModal.jsx";


class LoginPage extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      username :"",
      password :"",
      failMessage:"",
      isFailModal:false,
      isRegister:false,
      loading:false
    }

    this.login = this.login.bind(this);
    this.failModalStatus = this.failModalStatus.bind(this);
    this.registerFunc = this.registerFunc.bind(this);
	this.enterEvent = this.enterEvent.bind(this);
  }

  componentWillMount(){
    if(this.props.isLogin) {
			this.props.history.push("/");
	}
  }

  componentDidMount() {
	window.addEventListener('keydown', this.enterEvent, { passive: true });
  }

  componentWillUnmount(){
	window.removeEventListener('keydown', this.enterEvent, { passive: true });
  }

  enterEvent(event) {
	if(event.key == 'Enter') {
		this.login();
	}
  }

  login() {
    this.setState({
      loading:true
    })
    const userdata = {
      username:this.state.username,
      password:this.state.password
    }
    $.ajax({
      url: '/getAccessToken',
      dataType: 'json',
      contentType: "application/json; charset=utf-8",
      type: 'POST',
      data: JSON.stringify(userdata),
      success: function(data) {
        if(data.status) {
          localStorage.setItem('accessToken',data.accessToken);
          location.replace("/");
          console.log(data);
        } else {
          this.setState({
            failMessage:"<li class='danger'>"+data.result+"</li>",
            isFailModal:true,
            loading:false
          });
        }
      }.bind(this),
      error: function(xhr, status, err){
        this.setState({
          failMessage:"<li class='danger'>Giriş Yapamıyorsunuz. Sebebi çok çeşitli olabilir üyeliğiniz hala onaylanmamış yada yanlış giriş bilgilerini kullanıyor olabilirsiniz. Yada hiçbiri olmayıp tamamen sistemlerimizle alakalı bir problem olabilir. Bu hata mesajı daha sonra detaylandırlacaktır. Şimdilik Tekrar Deneyin :)</li>",
          isFailModal:true,
          loading:false
        })
        console.log(xhr,status,err);
      }.bind(this)
    });
  }



  failModalStatus(data) {
    this.setState({
      isFailModal:data
    })
  }

  registerFunc(data) {
    this.setState({
      isRegister:data
    })
  }

  



  render(){

    return(

      <div style={{marginTop:10+'%'}}>
      <LoadingComponent isActive={this.state.loading} />
      <Grid bsClass="container-fluid">
        <Row>
          <Col md={3} >
          </Col>
          <Col sm={8} md={6}>
            <Col sm={12} md={3}>
              Kullanıcı İsminiz:
            </Col>
            <Col sm={12} md={9}>
              <FormControl
                type="text"
                onChange={e => this.state.username = e.target.value}
                placeholder="Kullanıcı İsmini Giriniz"
                />
            </Col>
          </Col>
          <Col sm={4} md={6}>
          </Col>
        </Row>
        <div style={{marginTop:15+'px'}}>
        <Row>
          <Col md={3}>
          </Col>
          <Col sm={8} md={6}>
            <Col sm={12} md={3}>
              Şifreniz
            </Col>
            <Col sm={12} md={9}>
              <FormControl
                type="password"
                onChange={e => this.state.password = e.target.value}
                placeholder="Şifrenizi Giriniz"
                />
            </Col>
          </Col>
          <Col sm={4} md={3}>
          </Col>
        </Row>
        </div>
        <div style={{marginTop:15+'px'}}>
        <Row>
          <Col md={3}>
          </Col>
          <Col sm={8} md={6}>
            <Col sm={12} md={3}>
            </Col>
            <Col sm={12} md={9}>
              <div style={{float:'right', paddingRight:0+'%'}}>
                <Button bsStyle="primary" onClick={() => this.login()}>Giriş Yap</Button>
              </div>
              <div style={{float:'left', paddingLeft:60+'%'}}>
                <Button bsStyle="info" onClick={() => this.setState({isRegister:true})}>Kayıt Ol</Button>
              </div>
            </Col>
          </Col>
          <Col sm={4} md={3}>
          </Col>
        </Row>
        </div>
      </Grid>

      <div>
      <InfoModal
        modalShow={this.state.isFailModal}
        modalFunc={this.failModalStatus}
        isFail={true}
        message={this.state.failMessage}  />
      </div>
      <div>
        <UserModal
          isUpdate = {false}
          userInfo = {null}
          isRegister = {this.state.isRegister}
          registerModalFunc = {this.registerFunc}
        />
      </div>
      </div>
    )

  }

}

export default withRouter(LoginPage);
