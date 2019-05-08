package com.turkcell.blockmail.util;

import java.util.HashMap;
import java.util.Map;

public class StatusMovementConstantsUtil {
	
	private static final String SAVE_BLOCK = "Blok Kaydedildi.";
	private static final String UPDATE_BLOCK = "Blok Güncellendi.";
	private static final String CLOSE_BLOCK = "Blok Kapatıldı.";
	private static final String REVERT_BLOCK = "Blok Geri Döndü.";
	private static final String ASSIGN_DIFF_TEAM = "Blok Başka Ekibe Atandı.";
	
	public static final Integer SAVE_BLOCK_STATUS = 1;
	public static final Integer UPDATE_BLOCK_STATUS = 2;
	public static final Integer CLOSE_BLOCK_STATUS = 3;
	public static final Integer REVERT_BLOCK_STATUS = 4;
	public static final Integer ASSIGN_DIFF_TEAM_STATUS = 5;
	
	
	private static final Map<Integer, String> statusLabelMap = new HashMap<>();
	
	
	public static final Map<Integer, String> statusLabelMapBuild() {
		statusLabelMap.put(SAVE_BLOCK_STATUS, SAVE_BLOCK);
		statusLabelMap.put(UPDATE_BLOCK_STATUS, UPDATE_BLOCK);
		statusLabelMap.put(CLOSE_BLOCK_STATUS, CLOSE_BLOCK);
		statusLabelMap.put(REVERT_BLOCK_STATUS, REVERT_BLOCK);
		statusLabelMap.put(ASSIGN_DIFF_TEAM_STATUS, ASSIGN_DIFF_TEAM);
		return statusLabelMap;
	}
	

}
