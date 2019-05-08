import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import {FormControl,Col,Row,option,Button,Grid,Modal,ListGroup,ListGroupItem,FormGroup,ControlLabel} from 'react-bootstrap';
import {withRouter} from "react-router";
import InfoModal from "./InfoModal.jsx";
import LoadingComponent from './LoadingComponent.jsx';

class UserModal extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        rUsername:"",
        rPassword:"",
        rPasswordr:"",
        rEmail:"",
        rName:"",
        rSurname:"",
        infoModal:false,
        infoMessage:"",
        loading:false
      };
      this.failModalStatus = this.failModalStatus.bind(this);
      this.infoModalStatus = this.infoModalStatus.bind(this);
      this.register = this.register.bind(this);
      this.checkValueControl = this.checkValueControl.bind(this);
    }


    componentDidUpdate(prevProps,prevState){
      if(prevState.infoModal == true && this.state.infoModal == false) {
          this.props.history.push("/");
      }

      if(prevProps.isRegister == false
          && this.props.isRegister == true
          && this.props.isUpdate == true) {
            this.setState({
              rUsername:this.props.userInfo.username,
              rEmail:this.props.userInfo.email,
              rName:this.props.userInfo.name,
              rSurname:this.props.userInfo.surname
            })
      }
    }

    register() {
      const checkValueHtml = this.checkValueControl();
      if(checkValueHtml == ""){
        this.setState({
          loading:true
        })

        if(!this.props.isUpdate) {
        const userData = {
              username:this.state.rUsername,
              password:this.state.rPassword,
              email:this.state.rEmail,
              name:this.state.rName,
              surname:this.state.rSurname
          }
          $.ajax({
            url: '/saveUser',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            type: 'POST',
            data: JSON.stringify(userData),
            success: function(data) {
              if(data.status){
                this.props.registerModalFunc(false);
                this.setState({
                  infoModal:true,
                  infoMessage:data.result,
                  loading:false
                })
              } else {
                this.setState({
                  failMessage:"<li class='danger'>"+data.result+"</li>",
                  isFailModal:true,
                  loading:false
                })
              }
              console.log("Kaydettik");
            }.bind(this),
            error: function(xhr, status, err){
              this.setState({
                failMessage:"<li class='danger'>Kaydedemedik Lütfen Daha Sonra Tekrar Deneyin.</li>",
                isFailModal:true,
                loading:false
              })
            }.bind(this)
          });
        } else {
          const userData = {
              id:this.props.userInfo.id,
              username:this.state.rUsername,
              password:this.state.rPassword,
              email:this.state.rEmail,
              name:this.state.rName,
              surname:this.state.rSurname
          };
          $.ajax({
            url: '/updateUser',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            type: 'POST',
            data: JSON.stringify(userData),
            success: function(data) {
              if(data != null){
                this.props.registerModalFunc(false, data);
                this.setState({
                  infoModal:true,
                  infoMessage:"Güncelleme İşlemi Başarıyla Tamamlandı.",
                  loading:false
                })
              } else {
                this.setState({
                  failMessage:"<li class='danger'>Tüh ya güncelleme işlemini gerçekleştiremedim. Lütfen Daha Sonra Tekrar Deneyiniz.</li>",
                  isFailModal:true,
                  loading:false
                })
              }
              console.log("Kaydettik");
            }.bind(this),
            error: function(xhr, status, err){
              this.setState({
                failMessage:"<li class='danger'>Kaydedemedik Lütfen Daha Sonra Tekrar Deneyin.</li>",
                isFailModal:true,
                loading:false
              })
            }.bind(this)
          });
        }


      } else {
        this.setState({
          failMessage:checkValueHtml,
          isFailModal:true
        })
      }
    }

    failModalStatus(data) {
      this.setState({
        isFailModal:data
      })
    }

    infoModalStatus(data) {
      this.setState({
        infoModal:data
      })
    }

    checkValueControl(){
      var result = "";
      const username= this.state.rUsername;
      const password=this.state.rPassword;
      const rpassword = this.state.rPasswordr;
      const email=this.state.rEmail;
      const name=this.state.rName;
      const surname=this.state.rSurname;

      var atIndexOf = email.indexOf('@');

      var extEmail = atIndexOf != -1 ? email.substring(atIndexOf+1, email.length) : "";

      if(username == '' || username == null) {
        result = result + "<li class='danger'> Lütfen Kullanıcı Adını Giriniz </li>";
      }
      if(!this.props.isUpdate && (password=='' || password == null)){
        result = result + "<li class='danger'> Lütfen Şifre Giriniz </li>";
      }
      if(!this.props.isUpdate && (rpassword == '' || rpassword == null)) {
        result = result + "<li class='danger'> Lütfen Tekrar Şifreyi Giriniz </li>";
      }
      if(email == '' || email == null) {
        result = result + "<li class='danger'> Lütfen E-Mail Adresinizi Giriniz </li>";
      }
      if(atIndexOf == -1) {
          result = result + "<li class='danger'> Lütfen Geçerli bir mail adresi giriniz. </li>";
      }
      if(atIndexOf != -1 && (extEmail!="turkcell.com.tr" && extEmail != "consultant.turkcell.com.tr") ) {
        result = result + "<li class='danger'> Lütfen turkcell.com.tr uzantılı bir mail adresi giriniz. </li>";
      }
      if(name == '' || name == null){
        result = result + "<li class='danger'> Lütfen İsminizi Giriniz </li>";
      }
      if(surname == '' || surname == null) {
        result = result + "<li class='danger'> Lütfen Soyisminizi Giriniz </li>";
      }
      if((password != '' || password != null)
        &&(rpassword != '' || rpassword != null)
        &&(password != rpassword)) {
        result = result + "<li class='danger'> Lütfen Şifrenizi Uyumlu Giriniz </li>";
      }

      return result;
    }

    render() {

      return(
        <div>
        <div>
        <LoadingComponent isActive={this.state.loading} />
        <Modal show={this.props.isRegister} onHide={() => this.props.registerModalFunc(false)}>
          <Modal.Header closeButton>
            <Modal.Title> {this.props.isUpdate ? ("Bilgilerim") : ("Kayıt Ol") } </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <ListGroup>
              <ListGroupItem>
                  <FormGroup controlId="rUsername">
                    <ControlLabel>Kullanıcı Adınız</ControlLabel>
                    <FormControl
  										type="text"
                      disabled={this.props.isUpdate}
                      value = {this.state.rUsername}
  										onChange={(e) => {this.setState({rUsername:e.target.value})}}
  										placeholder="Lütfen Bir Kullanıcı Adı Giriniz"
  										/>
                  </FormGroup>
              </ListGroupItem>
              <ListGroupItem>
                <FormGroup controlId="rPassword">
                  <ControlLabel>Şifre</ControlLabel>
                  <FormControl
                    type="password"
                    onChange={e => this.state.rPassword = e.target.value}
                    placeholder={this.props.isUpdate ? ("Şifreni değiştirmek istemiyorsanız boş bırakabilirsin.") : ("Lütfen Bir Şifre Giriniz")}
                    />
                </FormGroup>
              </ListGroupItem>
              <ListGroupItem>
                <FormGroup controlId="rPasswordr">
                  <ControlLabel>Şifre Tekrar</ControlLabel>
                  <FormControl
                    type="password"
                    onChange={e => this.state.rPasswordr = e.target.value}
                    placeholder={this.props.isUpdate ? ("Şifreni değiştirmek istemiyorsanız boş bırakabilirsin.") : ("Lütfen Bir Şifre Giriniz")}
                    />
                </FormGroup>
              </ListGroupItem>
              <ListGroupItem>
                <FormGroup controlId="rEmail">
                  <ControlLabel>E-Mail</ControlLabel>
                  <FormControl
                    type="text"
                    value = {this.state.rEmail}
                    onChange={(e) => {this.setState({rEmail:e.target.value})}}
                    placeholder="Lütfen E-Mail Adresinizi Giriniz."
                    />
                </FormGroup>
              </ListGroupItem>
              <ListGroupItem>
                <FormGroup controlId="name">
                  <ControlLabel>İsminiz</ControlLabel>
                  <FormControl
                    type="text"
                    value = {this.state.rName}
                    onChange={(e) => {this.setState({rName:e.target.value})}}
                    placeholder="Lütfen İsminizi Giriniz."
                    />
                </FormGroup>
              </ListGroupItem>
              <ListGroupItem>
                <FormGroup controlId="surname">
                  <ControlLabel>Soyisminiz</ControlLabel>
                  <FormControl
                    type="text"
                    value = {this.state.rSurname}
                    onChange={(e) => {this.setState({rSurname:e.target.value})}}
                    placeholder="Lütfen Soyisminizi Giriniz."
                    />
                </FormGroup>
              </ListGroupItem>
            </ListGroup>
          </Modal.Body>
          <Modal.Footer>
            <Button bsStyle="primary" onClick={() => this.register()}> {this.props.isUpdate ? ("Bilgilerimi Güncelle") : ("Kayıt Ol") } </Button>
          </Modal.Footer>
        </Modal>
        </div>
        <div>
          <InfoModal
            modalShow={this.state.isFailModal}
            modalFunc={this.failModalStatus}
            isFail={true}
            message={this.state.failMessage}  />
        </div>
        <div>
          <InfoModal
            modalShow={this.state.infoModal}
            modalFunc={this.infoModalStatus}
            isFail={false}
            message={this.state.infoMessage}  />
        </div>
        </div>

      )
    }
}

export default withRouter(UserModal);
