import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import {DateRangePicker} from 'react-bootstrap-daterangepicker';
import 'react-datepicker/dist/react-datepicker.css';
import moment from 'moment';
import {FormControl} from 'react-bootstrap';


class PageNumbers extends React.Component {

	constructor(props) {
		super(props);
		var maxDate = moment();
		maxDate.set('hour', 16);
		maxDate.set('minute', 30);
		var startDate = moment();
		startDate.set('hour', 7);
		startDate.set('minute', 30);
		this.state = {
				startDateTemp: null,
				startDate: 0,
				endDate:true,
				maxDate:maxDate,
				startDate:startDate,
				locale: {
					"format": "DD/MM/YYYY",
			        "separator": " / ",
			        "applyLabel": "Onayla",
			        "cancelLabel": "Kapat",
			        "fromLabel": "Dan",
			        "toLabel": "DANA",
			        "customRangeLabel": "Custom",
			        "weekLabel": "H",
			        "daysOfWeek": [
			            "Pa",
			            "Pzt",
			            "Salı",
			            "Çar",
			            "Per",
			            "Cuma",
			            "Cmt"
			        ],
			        "monthNames": [
			            "Ocak",
			            "Şubat",
			            "Mart",
			            "Nisan",
			            "Mayıs",
			            "Haziran",
			            "Temmuz",
			            "Ağustos",
			            "Eylül",
			            "Ekim",
			            "Kasım",
			            "Aralık"
			        ],
			        "firstDay": 1
				}
		}
		this.handleChangeDate = this.handleChangeDate.bind(this);
		this.handleChangeDateWithEndDate = this.handleChangeDateWithEndDate.bind(this);
	}
	
	
	handleChangeDate(event, date){
		this.setState({
			startDateTemp: date.startDate.format("DD/MM/YYYY HH:mm").toString(),
			startDate: date.startDate.valueOf()
		})
	}
	
	handleChangeDateWithEndDate(event, date){
		this.setState({
			startDateTemp: date.startDate.format("DD/MM/YYYY HH:mm").toString() + '-' + date.endDate.format("DD/MM/YYYY HH:mm").toString(),
			startDate: date.startDate.valueOf()
		})
	}

	render() {
		return(
			<div>
			<DateRangePicker
				selected={this.state.startDateTemp}
				onEvent={this.state.endDate ? this.handleChangeDateWithEndDate : this.handleChangeDate}
				maxDate={this.state.maxDate}
				startDate={this.state.startDate}
				singleDatePicker={false}
				timePicker={true}
				timePickerIncrement={1}
				timePicker24Hour={true}
				locale={this.state.locale}
			>
			<FormControl type="text" value={this.state.startDateTemp} placeholder="Lütfen Tarih Seçiniz" disabled />
		
			</DateRangePicker>
			</div>
		)
		

	}

}



export default PageNumbers;
