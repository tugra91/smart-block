import React from 'react';
import ReactDOM from 'react-dom';
import {Button,Modal} from 'react-bootstrap';


class InfoModal extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <Modal show={this.props.modalShow} onHide={() => this.props.modalFunc(false)}>
            <Modal.Header closeButton>
              <Modal.Title>Uyarı </Modal.Title>
            </Modal.Header>
            <Modal.Body>
              {this.props.isFail ? (<ul><td dangerouslySetInnerHTML={{__html: this.props.message}} /></ul>) : (this.props.message)}
            </Modal.Body>

            <Modal.Footer>
              <Button bsStyle="primary" onClick={() => this.props.modalFunc(false)}> Tamam </Button>
            </Modal.Footer>
        </Modal>
      </div>
    )
  }

}

export default InfoModal;
