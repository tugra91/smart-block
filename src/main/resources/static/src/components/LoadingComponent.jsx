import React from 'react';
import ReactDOM from 'react-dom';

class LoadingComponent extends React.Component {
	
	
	constructor(props) {
		super(props);
		
	}
	
	componentDidUpdate(prevProps,prevState){
		if(prevProps.isActive == this.props.isActive){
			return;
		} else if(this.props.isActive) {
			this.refs.overlay.style.display = "block";
		} else {
			this.refs.overlay.style.display = "none";
		}
	}
	
	render() {
		return(
				<div class="overlay" ref="overlay">
					<div class="overlay-content">
						{this.props.isActive ? (<div class="sk-chasing-dots"><div class="sk-child sk-dot1"></div><div class="sk-child sk-dot2"></div></div>) : ("")}
					</div>
				</div>
		)
	}

}

export default LoadingComponent;