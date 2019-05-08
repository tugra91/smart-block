import React from 'react';
import ReactDOM from 'react-dom';
import DateRangePicker from 'react-bootstrap-daterangepicker';
import 'bootstrap-daterangepicker/daterangepicker.css';
import moment from 'moment';
import {FormControl} from 'react-bootstrap';


class StartDate extends React.Component {
	constructor(props){
		super(props);
		moment.locale('tr');
		this.state = {
				locale: {
					format: "DD/MM/YYYY",
			        separator: " - ",
			        applyLabel: "Onayla",
			        cancelLabel: "Kapat",
			        fromLabel: "Dan",
			        toLabel: "DANA",
			        customRangeLabel: "Custom",
			        weekLabel: "H",
			        daysOfWeek: [
			            "Pa",
			            "Pzt",
			            "Salı",
			            "Çar",
			            "Per",
			            "Cuma",
			            "Cmt"
			        ],
			        monthNames: [
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
			        firstDay: 1
				},
			   style: {
				   display: "inline-block",
				   width: "100%"
			   }
		}
	}

	handleChange(event, date){
			this.props.handleDate(event, date);
	}

	render(){
		return(
				<DateRangePicker
						selected={this.props.selectedDate}
						onEvent={this.handleChange.bind(this)}
						maxDate={this.props.maxDate}
						minDate={this.props.minDate}
						startDate={this.props.startDate}
						singleDatePicker={this.props.singleDatePicker}
						timePicker={true}
						timePickerIncrement={1}
						timePicker24Hour={true}
						locale={this.state.locale}
						containerStyles={this.state.style}
				>
				<FormControl type="text" value={this.props.selectedDate} placeholder="Lütfen Tarih Seçiniz" disabled />

				</DateRangePicker>
		)
	}
}

export default StartDate;
