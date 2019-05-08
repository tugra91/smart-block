package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.math.BigInteger;

public class BlockUpdateInput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092268861391169614L;
	private BigInteger blockId;
	private String updateUser;
	private String desc;
	
	
	
	public BigInteger getBlockId() {
		return blockId;
	}
	public void setBlockId(BigInteger blockId) {
		this.blockId = blockId;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

}
