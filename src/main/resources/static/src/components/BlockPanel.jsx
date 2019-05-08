import React from 'react';
import ReactDOM from 'react-dom';
import {Panel} from 'react-bootstrap';
import InfiniteScroll from 'react-infinite-scroller';

class BlockPanel extends React.Component {
	constructor(props) {
		super(props);

		this.modalShow = this.modalShow.bind(this);
		this.fetchLoadData = this.fetchLoadData.bind(this);
		this.getStatusStyle = this.getStatusStyle.bind(this);
	}



	modalShow(result){
		this.props.modalFunc(result);
	}

	fetchLoadData() {
		this.props.fetchFunc();
	}

	getStatusStyle(status) {
		if(status) {
			return "danger";
		} else {
			return "success";
		}
	}

	render() {
		var blockList = this.props.items.blockDetail != null ? this.props.items.blockDetail : this.props.items;
		var items = this.props.items.map((result,index) => (
				<Panel bsStyle = {result.blockDetail != null ? this.getStatusStyle(result.blockDetail.status) : this.getStatusStyle(result.status)}>
					<Panel.Heading>
						<Panel.Title componentClass="h3">#{index + 1}-{result.blockDetail != null ? result.blockDetail.blockName : result.blockName}</Panel.Title>
					</Panel.Heading>
					<Panel.Body>
						{result.blockDetail != null ? result.blockDetail.affectSystem : result.affectSystem} sistemi {result.blockDetail != null ? result.blockDetail.blockSystem : result.blockSystem} tarafından {result.blockDetail != null ? result.blockDetail.affectEnvironment : result.affectEnvironment } ortamında kesintiye uğradığı bildirilmiştir. Detayına ulaşmak için <p onClick={()=>this.modalShow(result.blockDetail != null && !this.props.isAllBlockDetail ? result.blockDetail : result)}>tıklayın.</p>
					</Panel.Body>
				</Panel>
			));

		return(
				<div>
					<InfiniteScroll
						pageStart={0}
						loadMore={this.fetchLoadData}
						hasMore={this.props.hasMore}
						loader={this.props.loader}
					>
						{items}
					</InfiniteScroll>
				</div>
		)

	}
}

export default BlockPanel;
