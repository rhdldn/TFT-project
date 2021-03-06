/**
  * @Package : com.jylee.tft.service
  * @FileName : DataManager.java
  * @Date : 2020. 10. 21. 
  * @Author : "LeeJaeYeon"
  * @Version :
  * @Information :
  */

package com.jylee.tft.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.jylee.tft.dao.MatchInfo;
import com.jylee.tft.dao.MatchesAndPuuids;
import com.jylee.tft.dao.Participants;
import com.jylee.tft.util.PondUtil;

import lombok.extern.slf4j.Slf4j;

/**
  * @Package : com.jylee.tft.service
  * @FileName : DataManager.java
  * @Date : 2020. 10. 21. 
  * @Author : "LeeJaeYeon"
  * @Version :
  * @Information : 데이터 관리
  */

@Slf4j
@Service
public class DataManager {

	@Autowired
	ParticipantsService participantsService;

	@Autowired
	MatchInfoService matchInfoService;

	@Autowired
	MatchesAndPuuidsService matchesAndPuuidsService;

	@Autowired
	ApiManager apiManager;

	public void update(String puuid) {

		List<String> matchIdList = apiManager.retrieveMatchId(puuid);
		
		if (!matchIdList.isEmpty()) {

			List<MatchesAndPuuids> matchList = matchesAndPuuidsService.getListMatchesAndPuuids(puuid);
			
			List<String> dbList = matchList.stream()
					.map(MatchesAndPuuids::getMatchId)
					.collect(Collectors.toCollection(ArrayList::new));
			
			List<String> insertList = new ArrayList<String>();
			
			for(int i = 0; i < matchIdList.size(); i++) {
				if(!dbList.contains(matchIdList.get(i))) {
					insertList.add(matchIdList.get(i));
				}
			}
						
			for (String matchId : insertList) {
				MatchInfo matchInfo = apiManager.retrieveMatchInfo(matchId);
				matchInfoService.setMatchInfo(matchInfo);
				participantsService.setparticipants(matchInfo.getParticipantLists());
				matchesAndPuuidsService.setMatchesAndPuuids(matchId, puuid);
			}
			
		}
	}

	
}
