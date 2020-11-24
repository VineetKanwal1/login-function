package com.techprimers.serverless;

public class LoginRequest {
	
	private String userId;
    private String password;
    
	public LoginRequest() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
