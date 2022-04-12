package com.lixar.apba.domain;

public class ChatMessageBackingObject {
	private String username;
    private String message;
    
    public ChatMessageBackingObject() {}
    
    public ChatMessageBackingObject(ChatMessage cm) {
    	username = cm.getUsername();
    	message = cm.getMessage();
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
