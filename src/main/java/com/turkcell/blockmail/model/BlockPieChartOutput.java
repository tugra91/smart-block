package com.turkcell.blockmail.model;

import java.io.Serializable;
import java.util.List;

public class BlockPieChartOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9204531979009892600L;
	
	private List<BlockPieChartInfoModel> blockSystem;
	
	private List<BlockPieChartInfoModel> affectSystem;
	
	private List<BlockPieChartInfoModel> blockType;
	
	private List<BlockPieChartInfoModel> blockSystemPiece;
	
	private List<BlockPieChartInfoModel> affectSystemPiece;
	
	private List<BlockPieChartInfoModel> blockTypePiece;

	public List<BlockPieChartInfoModel> getBlockSystem() {
		return blockSystem;
	}

	public void setBlockSystem(List<BlockPieChartInfoModel> blockSystem) {
		this.blockSystem = blockSystem;
	}

	public List<BlockPieChartInfoModel> getAffectSystem() {
		return affectSystem;
	}

	public void setAffectSystem(List<BlockPieChartInfoModel> affectSystem) {
		this.affectSystem = affectSystem;
	}

	public List<BlockPieChartInfoModel> getBlockType() {
		return blockType;
	}

	public void setBlockType(List<BlockPieChartInfoModel> blockType) {
		this.blockType = blockType;
	}
	
	

	public List<BlockPieChartInfoModel> getBlockSystemPiece() {
		return blockSystemPiece;
	}

	public void setBlockSystemPiece(List<BlockPieChartInfoModel> blockSystemPiece) {
		this.blockSystemPiece = blockSystemPiece;
	}

	public List<BlockPieChartInfoModel> getAffectSystemPiece() {
		return affectSystemPiece;
	}

	public void setAffectSystemPiece(List<BlockPieChartInfoModel> affectSystemPiece) {
		this.affectSystemPiece = affectSystemPiece;
	}

	public List<BlockPieChartInfoModel> getBlockTypePiece() {
		return blockTypePiece;
	}

	public void setBlockTypePiece(List<BlockPieChartInfoModel> blockTypePiece) {
		this.blockTypePiece = blockTypePiece;
	}

	@Override
	public String toString() {
		return "BlockPieChartOutput [blockSystem=" + blockSystem + ", affectSystem=" + affectSystem + ", blockType="
				+ blockType + ", blockSystemPiece=" + blockSystemPiece + ", affectSystemPiece=" + affectSystemPiece
				+ ", blockTypePiece=" + blockTypePiece + "]";
	}
	
	
	
	

}
