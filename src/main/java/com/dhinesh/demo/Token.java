package com.dhinesh.demo;

public class Token {
    private int tokenId;
    private int userId;
    private String token;

    public int getUserId() {
        return userId;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
