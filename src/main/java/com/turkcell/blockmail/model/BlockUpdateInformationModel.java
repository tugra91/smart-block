/**
 * 
 */
package com.turkcell.blockmail.model;

import java.io.Serializable;

/**
 * @author TCMUER
 *
 */
public class BlockUpdateInformationModel implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1176152864904542209L;
	
	
	private String blockUpdateDesc;
	private boolean isBlockWrong;
	private String wrongBlockDesc;
	private boolean isRedirectDiffTeam;
	private String descForRedirectTeam;
	private String updateUser;
	private long updateDate;
	
	
	public String getBlockUpdateDesc() {
		return blockUpdateDesc;
	}
	public void setBlockUpdateDesc(String blockUpdateDesc) {
		this.blockUpdateDesc = blockUpdateDesc;
	}
	public boolean isBlockWrong() {
		return isBlockWrong;
	}
	public void setBlockWrong(boolean isBlockWrong) {
		this.isBlockWrong = isBlockWrong;
	}
	public String getWrongBlockDesc() {
		return wrongBlockDesc;
	}
	public void setWrongBlockDesc(String wrongBlockDesc) {
		this.wrongBlockDesc = wrongBlockDesc;
	}
	public boolean isRedirectDiffTeam() {
		return isRedirectDiffTeam;
	}
	public void setRedirectDiffTeam(boolean isRedirectDiffTeam) {
		this.isRedirectDiffTeam = isRedirectDiffTeam;
	}
	public String getDescForRedirectTeam() {
		return descForRedirectTeam;
	}
	public void setDescForRedirectTeam(String descForRedirectTeam) {
		this.descForRedirectTeam = descForRedirectTeam;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public long getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}
	
	@Override
	public String toString() {
		return "BlockUpdateInformationModel [blockUpdateDesc=" + blockUpdateDesc + ", isBlockWrong=" + isBlockWrong
				+ ", wrongBlockDesc=" + wrongBlockDesc + ", isRedirectDiffTeam=" + isRedirectDiffTeam
				+ ", descForRedirectTeam=" + descForRedirectTeam + ", updateUser=" + updateUser + ", updateDate="
				+ updateDate + "]";
	}
	
	
	
	
	
	
	
	
}
