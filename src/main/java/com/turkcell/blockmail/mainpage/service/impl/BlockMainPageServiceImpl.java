package com.turkcell.blockmail.mainpage.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.Gson;
import com.turkcell.blockmail.common.service.BlockCommonService;
import com.turkcell.blockmail.document.BlockInfoDocumentInput;
import com.turkcell.blockmail.document.BlockSystemListDocument;
import com.turkcell.blockmail.document.BlockTypeListDocument;
import com.turkcell.blockmail.mainpage.dao.BlockMainPageDao;
import com.turkcell.blockmail.mainpage.service.BlockMainPageService;
import com.turkcell.blockmail.model.BlockInfoAsTimeOutput;
import com.turkcell.blockmail.model.BlockPieChartInfoModel;
import com.turkcell.blockmail.model.BlockPieChartOutput;
import com.turkcell.blockmail.util.thread.LastBlockControlThread;
import com.turkcell.blockmail.util.thread.LastBlockSettingThread;
import com.turkcell.blockmail.util.thread.service.LastBlockThreadService;

@Service
public class BlockMainPageServiceImpl implements BlockMainPageService {
	
	
	@Autowired
	private BlockMainPageDao blockMainPageDao;
	
	@Autowired
	private BlockCommonService blockCommonService;
	
	
	@Autowired
	private LastBlockThreadService lastBlockThreadService;
	
	
	
	private SseEmitter emitter;

	private Map<String, SseEmitter> emitterMap = new HashMap<>();
	
	
	
	@Override
	public List<Document> getLastBlocks(long skip, int limit) {
		List<Document> result = blockMainPageDao.getLastBlocks(skip, limit);
		
		
		
//		List<BlockInfoDocumentInput> blockInfoList = new ArrayList<>();
//		try {
//			blockInfoList = blockMainPageDao.getLastBlocks(skip, limit);
//		} catch (NullPointerException e) {
//			blockInfoList = new ArrayList<>();
//		}
//		Type listType = new TypeToken<List<Document>>() {}.getType();
//		List<Document> result = new Gson().fromJson(new Gson().toJson(blockInfoList), listType);
		return result;
	}

	@Override
	public DeferredResult<SseEmitter> getSseEmitter(String clientId) {
		boolean sseControl = true;
		final DeferredResult<SseEmitter> result = new DeferredResult<>();
		while(sseControl) {
			if(emitterMap.containsKey(clientId)) {
				result.setResult(emitterMap.get(clientId));
				sseControl = false;
			}
		}
		result.onCompletion(() -> {emitterMap.remove(clientId);});
		return result;
	}

	@Override
	public DeferredResult<BlockInfoAsTimeOutput> getLongPoll(long lastCreatedDate, String clientId) {
		final DeferredResult<BlockInfoAsTimeOutput> result = new DeferredResult<>(Long.MAX_VALUE);
		
		
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitterMap.put(clientId, emitter);
		
		
		Thread getBlockAllService = null;
		Thread getBlockSettingThread = null;

		boolean sseControl = true;

		while(sseControl) {
			if(emitter != null) {
				LastBlockControlThread cThread = new LastBlockControlThread();
				LastBlockSettingThread sThread = new LastBlockSettingThread();
				
				sThread.setEmitter(emitter);
				sThread.setLastCreatedDate(lastCreatedDate);
				sThread.setResult(result);
				sThread.setLastBlockThreadService(lastBlockThreadService);
				
				cThread.setCreateDate(lastCreatedDate);
				cThread.setEmitter(emitter);
				cThread.setResult(result);
				cThread.setLastBlockThreadService(lastBlockThreadService);
				cThread.setLatestBlockSettingThread(sThread);
				getBlockAllService = new Thread(cThread, "task-"+clientId);
				getBlockSettingThread = new Thread(sThread, "task-"+clientId+"#");
				sseControl = false;
			}
		}

		try {
			
			System.out.println("getBlockAllService ID:"+ String.valueOf(getBlockAllService.getName()) + " ve STATUS: "+ getBlockAllService.getState().toString());


			
			
			
			System.out.println("getBlockAllService ID:"+ String.valueOf(getBlockAllService.getName()) + " ve STATUS: "+ getBlockAllService.getState().toString());


			getBlockAllService.start();
			Thread.sleep(500);
			getBlockSettingThread.start();
			
			
			System.out.println("getBlockAllService ID:"+ String.valueOf(getBlockAllService.getName()) + " ve STATUS: "+ getBlockAllService.getState().toString());


		
			
		
			System.out.println("getBlockAllService ID:"+ String.valueOf(getBlockAllService.getName()) + " ve STATUS: "+ getBlockAllService.getState().toString());
			
			result.onCompletion(() -> {System.out.println("Result Tamamlandı Gençlik");});


		} catch (InterruptedException e) {
			
		}
		
		return result;
	}

	@Override
	public BlockPieChartOutput getBlockPiechartInfoForLastBlocks(long skip, int limit, String env, boolean isActive) {
		

		BlockPieChartOutput output = new BlockPieChartOutput();
		

		List<BlockPieChartInfoModel> blockSystemList = new ArrayList<>();
		List<BlockPieChartInfoModel> affectSystemList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockTypeList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockSystemPieceList = new ArrayList<>();
		List<BlockPieChartInfoModel> affectSystemPieceList = new ArrayList<>();
		List<BlockPieChartInfoModel> blockTypePieceList = new ArrayList<>();
		List<BlockSystemListDocument> systemList  = blockCommonService.getSystemList().getSystemList();
		List<BlockTypeListDocument> typeList = blockCommonService.getBlockType().getTypeList();
		
		double totalBlockSum = 0;
		NumberFormat numberFormat = new DecimalFormat("#0.00");
		
		Gson gson = new Gson();
		
		List<Document> lastBlocks = getLastBlocks(skip, limit);
		
		
		for(BlockSystemListDocument rs: systemList) {
			BlockPieChartInfoModel blockSystemModel = new BlockPieChartInfoModel();
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.reduce(0d,(sum, p1) -> {
						
						return sum + p1.getDouble("blockHours").doubleValue();
						
					}, (sum1, sum2) -> sum1 + sum2);
			blockSystemModel.setLabel(rs.getSystemName());
			blockSystemModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			blockSystemList.add(blockSystemModel);

			BlockPieChartInfoModel blockSystemPieceModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.count();
			blockSystemPieceModel.setLabel(rs.getSystemName());
			blockSystemPieceModel.setValue(totalBlockSum);
			blockSystemPieceList.add(blockSystemPieceModel);


			BlockPieChartInfoModel affectSystemModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.reduce(0d,(sum, p1) -> {
						
						return sum + p1.getDouble("blockHours").doubleValue();
						
					}, (sum1, sum2) -> sum1 + sum2);
			affectSystemModel.setLabel(rs.getSystemName());
			affectSystemModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			affectSystemList.add(affectSystemModel);

			BlockPieChartInfoModel affectSystemPieceModel = new BlockPieChartInfoModel();
			totalBlockSum = 0;
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectSystem(), rs.getSystemName());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.count();
			affectSystemPieceModel.setLabel(rs.getSystemName());
			affectSystemPieceModel.setValue(totalBlockSum);
			affectSystemPieceList.add(affectSystemPieceModel);
		}
		
		
		for(BlockTypeListDocument rs: typeList) {
			BlockPieChartInfoModel blockTypeModel = new BlockPieChartInfoModel();
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockType(), rs.getBlockType());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.reduce(0d,(sum, p1) -> {
						
						return sum + p1.getDouble("blockHours").doubleValue();
						
					}, (sum1, sum2) -> sum1 + sum2);
			blockTypeModel.setLabel(rs.getBlockType());
			blockTypeModel.setValue(Double.parseDouble(numberFormat.format(totalBlockSum).replace(",", ".")));
			blockTypeList.add(blockTypeModel);

			BlockPieChartInfoModel blockTypePieceModel = new BlockPieChartInfoModel();
			totalBlockSum = lastBlocks
					.stream()
					.filter(s ->{return StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getBlockType(), rs.getBlockType());})
					.filter(s ->{
						boolean result = true;
						if(StringUtils.isNotEmpty(env)) {
							result = StringUtils.equalsIgnoreCase(gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class).getAffectEnvironment(), env);
						}
						return result;

					})
					.filter(s -> {
						boolean result = true;
						BlockInfoDocumentInput input = gson.fromJson(gson.toJson(s.get("blockDetail")), BlockInfoDocumentInput.class);
						if(isActive && !input.isStatus()) {
							result = false;
						}
						return result;
					})
					.count();
			blockTypePieceModel.setLabel(rs.getBlockType());
			blockTypePieceModel.setValue(totalBlockSum);
			blockTypePieceList.add(blockTypePieceModel);
		}


		output.setBlockSystem(blockSystemList);
		output.setAffectSystem(affectSystemList);
		output.setBlockType(blockTypeList);
		output.setBlockSystemPiece(blockSystemPieceList);
		output.setAffectSystemPiece(affectSystemPieceList);
		output.setBlockTypePiece(blockTypePieceList);
		
		
		return output;
	}

	public SseEmitter getEmitter() {
		return emitter;
	}

	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}

}
