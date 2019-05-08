import React from 'react';
import ReactDOM from 'react-dom';
import {Grid, Row, Col, FormControl, FormGroup, FieldGroup, ControlLabel} from 'react-bootstrap';
import StartDate from './TextInput.jsx';
import $ from 'jquery';
import BlockPanel from './BlockPanel.jsx';
import BlockModal from './BlockModal.jsx';
import moment from 'moment';
import {DateRangePicker} from 'react-bootstrap-daterangepicker';


const globalValue = {
		callAjax : false
}

class SearchBlock extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
				blockSystem:"",
				affectSystem:"",
				affectEnvironment:"",
				blockType:"",
				searchText:"",
				blockSystemList:[],
				blockList:[],
				blockDetail:{},
				modalShow:false,
				startDate:0,
				endDate:0,
				selectedDate:null,
				hasMore:true,
				loading:null,
				endDateTemp:null
		};
		this.handleMultipleData = this.handleMultipleData.bind(this);
		this.handleChangeDate = this.handleChangeDate.bind(this);
		this.handleOnblur = this.handleOnblur.bind(this);
		this.modalShow = this.modalShow.bind(this);
		this.modalClose = this.modalClose.bind(this);
		this.getBlockDates = this.getBlockDates.bind(this);
		this.fetchLoadData = this.fetchLoadData.bind(this);
		this.updateTotalListFromModal = this.updateTotalListFromModal.bind(this);
		this.deleteBlockFromModal = this.deleteBlockFromModal.bind(this);
		this.isObjectEquivalent = this.isObjectEquivalent.bind(this);
	}

	handleChangeDate(event, date){
		this.setState({
			selectedDate:"Başlangıç Tarihi: " + date.startDate.format("DD/MM/YYYY HH:mm").toString() + " - Bitiş Tarihi: " + date.endDate.format("DD/MM/YYYY HH:mm").toString(),
			startDate:date.startDate.valueOf(),
			endDate:date.endDate.valueOf()
		})
	}

	handleMultipleData(e){
		var self = this;
		$('#blockSystemMultiple').change(function(e){
			var selected = $(e.target).val();
			if(selected.includes("")) {
				$(e.target).val([""]);
			}
			self.setState({
				blockSystem:selected.toString()
			})
		});

		$('#affectSystemMultiple').change(function(e){
			var selected = $(e.target).val();
			if(selected.includes("")) {
				$(e.target).val([""]);
			}
			self.setState({
				affectSystem:selected.toString()
			})
		});

		$('#affectEnvMultiple').change(function(e){
			var selected = $(e.target).val();
			if(selected.includes("")) {
				$(e.target).val([""]);
			}
			self.setState({
				affectEnvironment:selected.toString()
			})
		});

		$('#blockTypeMultiple').change(function(e){
			var selected = $(e.target).val();
			if(selected.includes("")) {
				$(e.target).val([""]);
			}
			self.setState({
				blockType:selected.toString()
			})
		});
	}

	componentWillMount(){
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


	handleOnblur(e){
		const searchData = {
			searchText:this.state.searchText,
			blockSystem:this.state.blockSystem,
			affectSystem:this.state.affectSystem,
			affectEnvironment:this.state.affectEnvironment,
			blockType:this.state.blockType,
			startDate:this.state.startDate,
			endDate:this.state.endDate
		};
		this.setState({
			loading:true
		});
		$.ajax({
			url: '/searchBlock?skip=0&limit=20',
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			type: 'POST',
			data: JSON.stringify(searchData),
			success: function(result) {
				this.setState({
					loading:false,
					blockList:result.searchOutputList
				})
				console.log("Search Output", result.searchOutputList)
				console.log("Aradık");
			}.bind(this),
			error: function(xhr, status, err){
				this.setState({
					loading:false,
					blockList:[]
				})
				console.log("Arayamadık");
			}.bind(this)
		});

	}

	modalShow(blockDetail) {
		this.setState({
			blockDetail:blockDetail,
			modalShow:true
		});
	}

	modalClose() {
		this.setState({
			blockDetail:{},
			modalShow:false
		})
	}

	getBlockDates(dateMilis){
		if(dateMilis == 0){
			return "";
		}
		return moment(dateMilis).format("DD MMM YYYY HH:mm");
	}

	updateTotalListFromModal(updatedBlock) {
		var tempList = []
		this.state.blockList.map((result,index) => {
			var blockDetail = result.blockDetail != null ? result.blockDetail : result;
			var blockId = blockDetail._id != null ? blockDetail._id : blockDetail.id;

			if(this.isObjectEquivalent(blockId, updatedBlock.id)) {
				Object.assign(blockDetail, updatedBlock);
			}
			tempList.push(blockDetail);
		});
		this.setState({
			blockList:tempList
		});
	}

	isObjectEquivalent(a, b) {

    var aProps = Object.getOwnPropertyNames(a);
    var bProps = Object.getOwnPropertyNames(b);


    for (var i = 0; i < bProps.length; i++) {
        var propName = bProps[i];
        if (a[propName] !== b[propName]) {
            return false;
        }
    }

    return true;
	}

	deleteBlockFromModal () {
		this.fetchLoadData(true);
	}

	fetchLoadData(deleteValueLimit) {
		var self = this;
		if(!globalValue.callAjax && this.state.blockList.length > 0) {

			const searchData = {
					searchText:this.state.searchText,
					blockSystem:this.state.blockSystem,
					affectSystem:this.state.affectSystem,
					affectEnvironment:this.state.affectEnvironment,
					blockType:this.state.blockType,
					startDate:this.state.startDate,
					endDate:this.state.endDate
			}

			var urlLimit = deleteValueLimit ? 'limit='+this.state.blockList.length:'limit=20';
			var urlSkip = deleteValueLimit ? 'skip=0':'skip='+this.state.blockList.length;

			$.ajax({
				url:'/searchBlock?'+urlSkip+'&'+urlLimit,
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				type: 'POST',
				data: JSON.stringify(searchData),
				success: function(res){
					console.log(res);
					if(deleteValueLimit) {
							globalValue.callAjax = false;
							this.setState({
								blockList:res.searchOutputList
							})
					} else {
						if(res.searchOutputList.length != 0){
							var blockList = self.state.blockList;
							var concatList  = blockList.concat(res.searchOutputList);
							globalValue.callAjax = false;
							self.setState({
								blockList:concatList
							});
						} else {
							self.setState({
								hasMore: false
							});
						}
					}
				}.bind(this),
				error: function(xhr, status, err){
					this.setState({
						blockList:[]
					})
					console.error(status,err.toString());
				}.bind(this)
			});
		}
		globalValue.callAjax = true;
	}


	render() {

		const blockSystemList = this.state.blockSystemList.map((result, index)  => (
				<option value={result.systemName} key={index}>{result.systemName}</option>
			));

		return(
			<div>
				<form>
					<Grid>
						<div>
							<Row className="show-grid">
								<Col sm={6} md={3}>
									<FormGroup controlId="blockSystemMultiple">
									  <ControlLabel>Blok Yaşatan Sistem</ControlLabel>
									  <FormControl  onChange = {e => this.handleMultipleData(e)} onBlur = {e => this.handleOnblur(e)} componentClass="select" multiple>
									     <option value="">Hepsi</option>
									     {blockSystemList}
									  </FormControl>
									 </FormGroup>
								</Col>
								<Col sm={6} md={3}>
									<FormGroup controlId="affectSystemMultiple">
										<ControlLabel>Blok Yaşanan Sistem</ControlLabel>
										<FormControl onChange = {e => this.handleMultipleData(e)} onBlur = {e => this.handleOnblur(e)} componentClass="select" multiple>
											<option value="">Hepsi</option>
											{blockSystemList}
										</FormControl>
									</FormGroup>
								</Col>
								<Col sm={6} md={6}>
									<FormGroup controlId="searchStartDate">
										<ControlLabel>Tarih Aralığı Seçin </ControlLabel>
										<StartDate
											selectedDate = {this.state.selectedDate}
										    handleDate = {this.handleChangeDate}
											singleDatePicker = {false}
											onBlur = {e => this.handleOnblur(e)}
										/>

									</FormGroup>
								</Col>
							</Row>
							<Row className="show-grid">
								<Col sm={6} md={3}>
									<FormGroup controlId="affectEnvMultiple">
										<ControlLabel>Blok Yaşanan Ortam</ControlLabel>
										<FormControl onChange = {e => this.handleMultipleData(e)} onBlur = {e => this.handleOnblur(e)} componentClass="select" multiple>
										 	<option value="">Hepsi</option>
										 	<option value="DEV" > DEV </option>
		 									<option value="STB" > STB </option>
											<option value="PRP" > PRP </option>
										</FormControl>
									</FormGroup>
								</Col>
								<Col sm={6} md={3}>
									<FormGroup controlId="blockTypeMultiple">
										<ControlLabel>Blok Tipi</ControlLabel>
										<FormControl onChange = {e => this.handleMultipleData(e)} onBlur = {e => this.handleOnblur(e)}  componentClass="select" multiple>
										 	<option value="">Hepsi</option>
										 	<option value="Kesinti" > Kesinti (Servis kesintileri, timeout) </option>
		 									<option value="Devreye Alım" > Devreye Alım (Normal sürede gerçekleşmeyen devreye alım) </option>
											<option value="Başka Ekip" > Diğer Boardlardan Beklenen Gereksinimler </option>
											<option value="Geliştirme Ortam Sorunu" > Geliştirme Ortamında Yaşanan Uygulama yada Konfigrasyon sorunları </option>
											<option value="İş Birimi" > İş Biriminden Beklenen Gereksinimler </option>
											<option value="Cihaz" > Cihaz Ekipman Sorunları </option>
										</FormControl>
									</FormGroup>
								</Col>
								<Col sm={8} md={6}>
									<ControlLabel></ControlLabel>
									<FormGroup controlId="searchText">
										<FormControl type="text" onBlur = {e => this.handleOnblur(e)} onChange = {e => this.state.searchText = e.target.value} placeholder="Lütfen Aramak İstediğini Kelimeyi Giriniz" />
									</FormGroup>
								</Col>
							</Row>
						</div>
					</Grid>
				</form>



				{this.state.loading ? (<center>Lütfen Bekleyiniz Sonuçlar Getiriliyor</center>) : ('')}

				{this.state.loading != null && !this.state.loading && this.state.blockList.length == 0 ? (<center> Herhangi Bir Sonuç Bulunamadı </center>) : ('')}

				{this.state.blockList.length>0 ? (<BlockPanel items={this.state.blockList} modalFunc={this.modalShow} fetchFunc={this.fetchLoadData} hasMore={this.state.hasMore} loader="Yükleniyor Bekleyiniz" /> ) : ('')}

				<BlockModal
					modalShow={this.state.modalShow}
					modalFunc={this.modalClose}
					isLogin={this.props.isLogin}
					modalDetail={this.state.blockDetail}
					updateListFunc = {this.updateTotalListFromModal}
					deleteListFunc = {this.deleteBlockFromModal}
					dateFunc={this.getBlockDates}
				/>

			</div>


		)
	}

}

export default SearchBlock;
