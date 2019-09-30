import React from 'react';
import ReactDOM from 'react-dom';
import {Grid, Row, Col, FormGroup, FormControl,Panel} from 'react-bootstrap';
import BlockPanel from './BlockPanel.jsx';
import BlockModal from './BlockModal.jsx';
import moment from 'moment';
import $ from 'jquery';
import {Pie} from 'react-chartjs-2';

const globalValue = {
		callAjax:false,
		didMount:false,
		fetchData:null
}

class GenericDateRangeBlock extends React.Component {


	constructor(props) {
		super(props);
		this.state = this.getInitialState();

		this.modalShow = this.modalShow.bind(this);
		this.modalClose = this.modalClose.bind(this);
		this.fetchLoadData = this.fetchLoadData.bind(this);
		this.fetchPiechartInfo = this.fetchPiechartInfo.bind(this);
		this.createPieBlockStructure = this.createPieBlockStructure.bind(this);
		this.renderPieChart = this.renderPieChart.bind(this);
		this.updateTotalListFromModal = this.updateTotalListFromModal.bind(this);
		this.deleteBlockFromModal = this.deleteBlockFromModal.bind(this);
		this.isObjectEquivalent = this.isObjectEquivalent.bind(this);
	}

	getInitialState() {
		const state = {
				blockList:[],
				hasMore:true,
				modalShow:false,
				blockDetail:{},
				pieBlockSystemData:{},
				pieAffectSystemData:{},
				pieBlockTypeData:{},
				pieBlockSystemPieceData:{},
				pieAffectSystemPieceData:{},
				pieBlockTypePieceData:{},
				blockUrl:"",
				pieBlockUrl:"",
		}

		return state;
	}

	fetchPiechartInfo(env,url){
		var baseUrl = this.state.pieBlockUrl == "" ? url : this.state.pieBlockUrl;
		var concatUrl = baseUrl + "env=" + env;
		$.ajax({
			url:concatUrl,
			dataType:'json',
			cache:false,
			success: function(res){
				this.setState({
					pieBlockSystemData:this.createPieBlockStructure(res.blockSystem),
					pieAffectSystemData:this.createPieBlockStructure(res.affectSystem),
					pieBlockTypeData:this.createPieBlockStructure(res.blockType),
					pieBlockSystemPieceData:this.createPieBlockStructure(res.blockSystemPiece),
					pieAffectSystemPieceData:this.createPieBlockStructure(res.affectSystemPiece),
					pieBlockTypePieceData:this.createPieBlockStructure(res.blockTypePiece)
				});
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	createPieBlockStructure(result) {
		var blockData = {data:[], backgroundColor:[], hoverBackgroundColor:[] };
		var pieBlockData = {labels:[], datasets:[]};
		for(var i in result) {
			if(result[i].value != 0) {
				blockData.data.push(result[i].value);
				blockData.backgroundColor.push("#"+((1<<24)*Math.random()|0).toString(16));
				blockData.hoverBackgroundColor.push("#"+((1<<24)*Math.random()|0).toString(16));
				pieBlockData.labels.push(result[i].label);
			}
		}
		pieBlockData.datasets.push(blockData)
		return pieBlockData ;
	}

	componentDidMount() {

		var type = this.props.dateRange;

		if(type == 'Today') {
			var baseBlockUrl = "/getBlockToday?segment=''&";
			var basePieUrl = "/getPieChartToday?segment=''&";
			this.setState({
				blockUrl:baseBlockUrl,
				pieBlockUrl:basePieUrl
			});
			this.fetchPiechartInfo("", basePieUrl);

		} else if(type == 'Week') {
			var baseBlockUrl = "/getBlockWeek?segment=''&";
			var basePieUrl = "/getPieChartWeek?startDate="+moment().valueOf()+"&segment=''&";
			this.setState({
				blockUrl:baseBlockUrl+"startDate="+moment().valueOf()+"&",
				pieBlockUrl:basePieUrl
			});
			this.fetchPiechartInfo("", basePieUrl);
		} else if(type = 'Month') {
			var baseBlockUrl = "/getBlockMonth?segment=''&";
			var basePieUrl = "/getPieChartMonth?startDate="+moment().valueOf()+"&segment=''&";
			this.setState({
				blockUrl:baseBlockUrl+"startDate="+moment().valueOf()+"&",
				pieBlockUrl:basePieUrl
			});
			this.fetchPiechartInfo("", basePieUrl);
		}

		globalValue.didMount = true;
	}

	componentWillUnmount(){
		globalValue.callAjax = false;
		globalValue.didMount = false;
		if(globalValue.fetchData != null) {
			globalValue.fetchData.abort();
		}
		this.setState(this.getInitialState());
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
		fetchPiechartInfo("", this.state.basePieUrl);
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
		this.fetchPiechartInfo("", this.state.basePieUrl);
	}

	fetchLoadData(deleteValueLimit) {
		var self = this;
		var urlLimit = deleteValueLimit ? 'limit='+this.state.blockList.length:'limit=20';
		var urlSkip = deleteValueLimit ? 'skip=0':'skip='+this.state.blockList.length;
		if(deleteValueLimit || (!globalValue.callAjax && globalValue.didMount)) {
			globalValue.fetchData = $.ajax({
				type:'GET',
				url:this.state.blockUrl+urlSkip+'&'+urlLimit,
				dataType:'json',
				cache:false,
				success: function(res){
					if(deleteValueLimit) {
							globalValue.callAjax = false;
							this.setState({
								blockList:res.blockDetail
							})
						} else {
							if(res.blockDetail.length != 0){
								var blockList = self.state.blockList;
								var concatList  = blockList.concat(res.blockDetail);
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
				}.bind(this)
			});
		}
		if(globalValue.didMount){
			globalValue.callAjax = true;
		}
	}

	calculateBlockPiece(blockList) {


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


	renderPieChart (title, data) {

		var bodyModel = "";

		if(data.labels == null || data.datasets == null) {
			bodyModel = <center> Veri Alınamadı </center>
		} else {
			bodyModel = data.labels.length > 0 && data.datasets.length > 0 ? <Pie data={data} /> : <center> Veri Alınamadı </center>;
		}

		var model = <div>
						<Panel bsStyle="primary">
							<Panel.Heading>
								<Panel.Title componentClass="h3"> {title} </Panel.Title>
							</Panel.Heading>
							<Panel.Body>
								{bodyModel}
							</Panel.Body>
						</Panel>
					</div>;

		return model;
	}


	render() {

		var type = this.props.dateRange;
		var time = "";
		switch(type) {
			case  "Today":
				time = "Bugün";
				break;
			case  "Week":
				time = "Bu Hafta";
				break;
			case  "Month":
				time = "Bu Ay";
				break;
			default :
				time = "";
		}

		return(
				<Grid bsClass="container-fluid">
					<Row>
						<Col xs={6} md={4}>
							{this.renderPieChart("Blok Yaşatan Sisteme Göre Blok Süreleri" , this.state.pieBlockSystemData)}
						</Col>
						<Col xs={6} md={4}>
							{this.renderPieChart("Blok Yaşayan Sisteme Göre Blok Süreleri" , this.state.pieAffectSystemData)}
						</Col>
						<Col xsHidden md={4}>
							{this.renderPieChart("Blok Tipine Göre Blok Süreleri" , this.state.pieBlockTypeData)}

						</Col>
					</Row>
					<Row>
						<Col xs={6} md={4}>
						</Col>
						<Col xs={6} md={4}>
							<div>
								<Panel bsStyle="info">
									<Panel.Heading>
										<Panel.Title componentClass="h3"> Ortam Seçin </Panel.Title>
									</Panel.Heading>
									<Panel.Body>
										<FormGroup controlId="formBlockSystem">
											 <Col sm={12}>
												 <FormControl onChange={e => this.fetchPiechartInfo(e.target.value)} componentClass="select" >
													 <option value="">Tüm Ortamlar </option>
													 <option value="DEV" > DEV </option>
													 <option value="STB" > STB </option>
													 <option value="PRP" > PRP </option>
												</FormControl>
											 </Col>
									 	</FormGroup>
								 	</Panel.Body>
								 </Panel>
							</div>
						</Col>
						<Col xsHidden md={4}>
						</Col>
					</Row>
					<Row>
						<Col xs={6} md={4}>
							{this.renderPieChart("Blok Yaşatan Sisteme Göre Blok Adetleri" , this.state.pieBlockSystemPieceData)}
						</Col>
						<Col xs={6} md={4}>
							{this.renderPieChart("Blok Yaşayan Sisteme Göre Blok Adetleri" , this.state.pieAffectSystemPieceData)}
						</Col>
						<Col xsHidden md={4}>
							{this.renderPieChart("Blok Tipine Göre Blok Adetleri" , this.state.pieBlockTypePieceData)}

						</Col>
					</Row>
					<Row>
						<Col xs={12} md={12}>
						<div>
						<Panel bsStyle="primary">
							<Panel.Heading>
								<Panel.Title componentClass="h3"> Tüm Ortamlara Göre {time} Açılan Bloklar </Panel.Title>
							</Panel.Heading>
							<Panel.Body>
								<BlockPanel
									items={this.state.blockList}
									modalFunc={this.modalShow}
									fetchFunc={this.fetchLoadData}
									hasMore={this.state.hasMore}
									isAllBlockDetail={true}
									loader="Yükleniyor Bekleyiniz" />

								<BlockModal
									modalShow={this.state.modalShow}
									modalFunc={this.modalClose}
									isLogin={this.props.isLogin}
									updateListFunc = {this.updateTotalListFromModal}
									deleteListFunc = {this.deleteBlockFromModal}
									modalDetail={this.state.blockDetail}
								/>
							</Panel.Body>
						</Panel>
					</div>
						</Col>
					</Row>
				</Grid>
		)

	}
}

export default GenericDateRangeBlock;
