import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import InfiniteScroll from 'react-infinite-scroller';
import {Panel,Alert, Grid, Row, Col, Modal,Table,Button} from 'react-bootstrap';
import moment from 'moment';
import BlockPanel from './BlockPanel.jsx';
import BlockModal from './BlockModal.jsx';
import BlockListPieChart from './BlockListPieChart.jsx';
import uuidv4 from 'uuid/v4';





const globalValue = {
		callAjax:false,
		controlPolling:true,
		pollingXhr:null,
		loadDataXhr:null,
		sseXhr:null,
		clientId:''
}

class BlockList extends React.Component {
	constructor(props) {
		super(props);
		this.state = this.getInitialState();
		this.modalShow = this.modalShow.bind(this);
		this.modalClose = this.modalClose.bind(this);
		this.getBlockDates = this.getBlockDates.bind(this);
		this.showLongPollingBlock = this.showLongPollingBlock.bind(this);
		this.fetchLoadData = this.fetchLoadData.bind(this);
		this.createPieChartData = this.createPieChartData.bind(this);
		this.createPieBlockStructure = this.createPieBlockStructure.bind(this);
		this.updateTotalListFromModal = this.updateTotalListFromModal.bind(this);
		this.isObjectEquivalent = this.isObjectEquivalent.bind(this);
		this.deleteBlockFromModal = this.deleteBlockFromModal.bind(this);
		globalValue.clientId = uuidv4();
	}


	getInitialState(){
		const state = {
				totalItems:[],
				hasMore:true,
				blockDetail:{},
				modalShow:false,
				pieBlockSystemData:{},
				pieAffectSystemData:{},
				pieBlockTypeData:{},
				pieBlockSystemPieceData:{},
				pieAffectSystemPieceData:{},
				pieBlockTypePieceData:{},
				blockSystemList:[],
				blockTypeList:[],
				longPollingItems:[]
		};
		return state;
	}

	componentDidMount(){
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

		$.ajax({
			url:'/getBlockTypeList',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				this.setState({
					blockTypeList:res.typeList,
				})
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});

	}





	componentWillUnmount(){
		globalValue.pollingXhr.abort();
		globalValue.loadDataXhr.abort();
		globalValue.sseXhr.abort();
		globalValue.callAjax = false;
		globalValue.controlPolling=true;
		globalValue.pollingXhr=null;
		globalValue.laodDataXhr=null;
		globalValue.sseXhr=null;
		this.setState(this.getInitialState());
	}


	updateTotalListFromModal(updatedBlock) {
		var tempList = []
		this.state.totalItems.map((result,index) => {
			var blockDetail = result.blockDetail != null ? result.blockDetail : result;
			var blockId = blockDetail._id != null ? blockDetail._id : blockDetail.id;
			if(this.isObjectEquivalent(blockId, updatedBlock.id)) {
				Object.assign(blockDetail, updatedBlock);
			}
			tempList.push(blockDetail);
		});
		this.setState({
			totalItems:tempList
		});
		this.createPieChartData(0,this.state.totalItems.length,"");
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
		var urlLimit = deleteValueLimit ? 'limit='+this.state.totalItems.length:'limit=20';
		var urlSkip = deleteValueLimit ? 'skip=0':'skip='+this.state.totalItems.length;
		if(!globalValue.callAjax) {
			globalValue.loadDataXhr = $.ajax({
				type:'GET',
				url:'/getLastBlocks?'+urlSkip+'&'+urlLimit+'&segment=',
				dataType:'json',
				cache:false,
				success: function(res){
					console.log(res);
					if(deleteValueLimit) {
						globalValue.callAjax = false;
						this.setState({
							totalItems:res.blockDetail
						})
						this.createPieChartData(0,res.blockDetail.length,"");
					} else {
						if(res.blockDetail.length != 0){
							var totalItems = self.state.totalItems;
							var concatList = []
							concatList  = totalItems.concat(res.blockDetail);
							globalValue.callAjax = false;
							this.createPieChartData(0,concatList.length,"");
							self.setState({
								totalItems:concatList
							});
						} else {
							self.setState({
								hasMore: false
							});
						}
						if(globalValue.controlPolling){
							this.sendLongPollingRequest(res.blockDetail[0].createDate);
							globalValue.controlPolling=false;
						}
					}
				}.bind(this),
				error: function(xhr, status, err){
					this.setState({
						totalItems:[]
					})
					console.error(status,err.toString());
				}.bind(this)
			});
		}
		globalValue.callAjax = true;
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

	sendLongPollingRequest(lastCreateDate) {

		globalValue.sseXhr = $.ajax({
			type:'GET',
			url:'/getSse?clientId='+globalValue.clientId,
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
			}.bind(this),
			error: function(xhr, status, err){
				console.error(status,err.toString());
			}.bind(this)
		});

		globalValue.pollingXhr = $.ajax({
			type: 'GET',
			url:'/getLongPollingBlock/' + lastCreateDate+'?clientId='+globalValue.clientId+'&segment=',
			dataType:'json',
			cache:false,
			success: function(res){
				console.log(res);
				var tempList = res.blockDetail.concat(this.state.longPollingItems);
				this.setState({
					longPollingItems:tempList
				});
				var concatPollingResult = this.state.longPollingItems.concat(this.state.totalItems);
				this.createPieChartData(0,concatPollingResult.length,"");
				this.sendLongPollingRequest(res.blockDetail[0].createDate);
			}.bind(this),
			error: function(xhr, status, err){
				this.setState({
					longPollingItems:[]
				})
				console.error(status,err.toString());
			}.bind(this)
		});
	}

	showLongPollingBlock(){
		var concatPollingResult = this.state.longPollingItems.concat(this.state.totalItems);
		this.setState({
			longPollingItems:[],
			totalItems:concatPollingResult
		});
	}

	createPieChartData(skip,limit, env) {

		var lastBlockPieChartInput = {
								skip:skip,
								limit:limit,
								env:env,
								active:false
							};

		$.ajax({
			url:'/getPieChartLastBlocks?env='+env,
			dataType:'json',
			contentType: "application/json; charset=utf-8",
			type: 'POST',
			data: JSON.stringify(lastBlockPieChartInput),
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



	render() {
		const blockAlert = <Alert bsStyle="warning"> Hey dostum sen buralarda takılırken tamı tamına {this.state.longPollingItems.length} tane block daha çıktı. Görmek için <p onClick={()=>this.showLongPollingBlock()}>Tıkla</p> </Alert>

		return(
				<div>
					<Grid bsClass="container-fluid" >
						<Row>
							<BlockListPieChart
								titleOne="Blok Yaşatan Sisteme Göre Blok Süreleri"
								titleTwo="Blok Yaşatan Sisteme Göre Blok ADETLERİ"
								pieDataOne={this.state.pieBlockSystemData}
								pieDataTwo = {this.state.pieBlockSystemPieceData} />


							<Col xs={6} md={4}>
								<Row>
									<Col xs={12} md={12}>
										{this.state.longPollingItems.length>0 ? blockAlert: ('')}

										<BlockPanel
											items={this.state.totalItems}
											modalFunc={this.modalShow}
											fetchFunc={this.fetchLoadData}
											hasMore={this.state.hasMore}
											isAllBlockDetail={true}
											loader="Yükleniyor Bekleyiniz" />

										<BlockModal
											modalShow={this.state.modalShow}
											modalFunc={this.modalClose}
											isLogin={this.props.isLogin}
											modalDetail={this.state.blockDetail}
											updateListFunc={this.updateTotalListFromModal}
											deleteListFunc={this.deleteBlockFromModal}
											dateFunc={this.getBlockDates}
										/>
									</Col>
								</Row>
							</Col>


							<BlockListPieChart
								titleOne="Blok Yaşayan Sisteme Göre Blok Süreleri"
								titleTwo="Blok Yaşayan Sisteme Göre Blok ADETLERİ"
								pieDataOne={this.state.pieAffectSystemData}
								pieDataTwo = {this.state.pieAffectSystemPieceData} />

						</Row>
					</Grid>



			</div>


		)
	}
}

export default BlockList;
