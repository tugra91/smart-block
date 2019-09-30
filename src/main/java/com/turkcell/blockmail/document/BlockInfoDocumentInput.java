package com.turkcell.blockmail.document;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.turkcell.blockmail.model.BlockUpdateInformationModel;

@Document
public class BlockInfoDocumentInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5157635105118910577L;
	
	@Id
	private ObjectId id;
	private String blockName;
	private String blockDesc;
	private String blockSystem;
	private String affectSystem;
	private String affectEnvironment;
	private long startDate;
	private long endDate;
	private String blockType;
	private String informTeam;
	private String openBlockUser;
	private String endBlockUser;
	private long createDate;
	private List<BlockUpdateInformationModel> updateInfoList;
	private long totalBlockTime;
	private int statusCode;
	private ObjectId userId;
	private ObjectId serviceId;
	private String segment;
	private boolean status;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public String getBlockDesc() {
		return blockDesc;
	}
	public void setBlockDesc(String blockDesc) {
		this.blockDesc = blockDesc;
	}
	public String getBlockSystem() {
		return blockSystem;
	}
	public void setBlockSystem(String blockSystem) {
		this.blockSystem = blockSystem;
	}
	public String getAffectSystem() {
		return affectSystem;
	}
	public void setAffectSystem(String affectSystem) {
		this.affectSystem = affectSystem;
	}
	public String getAffectEnvironment() {
		return affectEnvironment;
	}
	public void setAffectEnvironment(String affectEnvironment) {
		this.affectEnvironment = affectEnvironment;
	}
	public String getBlockType() {
		return blockType;
	}
	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public String getInformTeam() {
		return informTeam;
	}
	public void setInformTeam(String informTeam) {
		this.informTeam = informTeam;
	}
	public String getOpenBlockUser() {
		return openBlockUser;
	}
	public void setOpenBlockUser(String openBlockUser) {
		this.openBlockUser = openBlockUser;
	}
	public String getEndBlockUser() {
		return endBlockUser;
	}
	public void setEndBlockUser(String endBlockUser) {
		this.endBlockUser = endBlockUser;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}
	public List<BlockUpdateInformationModel> getUpdateInfoList() {
		return updateInfoList;
	}
	public void setUpdateInfoList(List<BlockUpdateInformationModel> updateInfoList) {
		this.updateInfoList = updateInfoList;
	}
	public long getTotalBlockTime() {
		return totalBlockTime;
	}
	public void setTotalBlockTime(long totalBlockTime) {
		this.totalBlockTime = totalBlockTime;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public ObjectId getUserId() {
		return userId;
	}
	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getServiceId() {
		return serviceId;
	}

	public void setServiceId(ObjectId serviceId) {
		this.serviceId = serviceId;
	}

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "BlockInfoDocumentInput{" +
				"id=" + id +
				", blockName='" + blockName + '\'' +
				", blockDesc='" + blockDesc + '\'' +
				", blockSystem='" + blockSystem + '\'' +
				", affectSystem='" + affectSystem + '\'' +
				", affectEnvironment='" + affectEnvironment + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", blockType='" + blockType + '\'' +
				", informTeam='" + informTeam + '\'' +
				", openBlockUser='" + openBlockUser + '\'' +
				", endBlockUser='" + endBlockUser + '\'' +
				", createDate=" + createDate +
				", updateInfoList=" + updateInfoList +
				", totalBlockTime=" + totalBlockTime +
				", statusCode=" + statusCode +
				", userId=" + userId +
				", serviceId=" + serviceId +
				", status=" + status +
				'}';
	}
}
