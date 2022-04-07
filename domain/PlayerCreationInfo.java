package com.lixar.apba.domain;

import org.springframework.boot.configurationprocessor.json.JSONObject;

public class PlayerCreationInfo {

	private JSONObject player;
	private Avatar avatar;
	private Integer diceSid;
	private JSONObject gameData;
	private String team;
	
	public PlayerCreationInfo(JSONObject player, Avatar avatar, int diceSid, JSONObject gameData, String team) {
		this.player = player;
		this.avatar = avatar;
		this.diceSid = diceSid;
		this.gameData = gameData;
		this.team = team;
	}
	
	public Avatar getAvatar() {
		return avatar;
	}
	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}
	public int getDiceSid() {
		return diceSid;
	}
	public void setDiceSid(int diceSid) {
		this.diceSid = diceSid;
	}
	public JSONObject getGameData() {
		return gameData;
	}
	public void setGameData(JSONObject gameData) {
		this.gameData = gameData;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public JSONObject getPlayer() {
		return player;
	}
	public void setPlayer(JSONObject player) {
		this.player = player;
	}

}
