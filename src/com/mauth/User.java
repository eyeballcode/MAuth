package com.mauth;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class User {

    private String accessToken, username, password, id, ct;

    public User(String accessToken, String username, String password, String id, String ct) {
        this.accessToken = accessToken;
        this.username = username;
        this.password = password;
        this.ct = ct;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Signs out globally.
     * <p/>
     * This method clears ALL access tokens, regardless of whether they were made by MAuth or not.
     */
    public void signoutGlobal() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://authserver.mojang.com/signout").openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject send = new JSONObject("{}");
            send.put("username", username);
            send.put("password", password);
            send.put("clientToken", ct);
            connection.getOutputStream().write(("\n" + send.toString()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Invalidates the current access token.
     */
    public void signout() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://authserver.mojang.com/invalidate").openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject send = new JSONObject("{}");
            send.put("accessToken", accessToken);
            send.put("clientToken", ct);
            connection.getOutputStream().write(("\n" + send.toString()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the access token.
     */
    public void refresh() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://authserver.mojang.com/refresh").openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject send = new JSONObject("{}");
            send.put("accessToken", accessToken);
            send.put("clientToken", ct);
            connection.getOutputStream().write(("\n" + send.toString()).getBytes());
            try {
                JSONObject res = new JSONObject(new JSONTokener(connection.getInputStream()));
                this.accessToken = res.getString("accessToken");
            } catch (Exception e) {
                throw new InvalidCredentialsException("Access token invalidated!");
            }
        } catch (Exception ignored) {
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getClientToken() {
        return ct;
    }
}
