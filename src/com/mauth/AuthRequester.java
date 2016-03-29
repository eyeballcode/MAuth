package com.mauth;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

/**
 * A class that helps with Minecraft login related stuff.
 */
public class AuthRequester {

    private String token;

    public AuthRequester(String clientToken) {
        token = clientToken;
    }

    /**
     * Checks if an access token is valid
     *
     * @param accessToken The access token
     * @return True if it's valid, else false.
     */
    public boolean isValid(String accessToken) throws IOException {

        HttpsURLConnection connection = (HttpsURLConnection) new URL("https://authserver.mojang.com/validate").openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        JSONObject send = new JSONObject("{}");

        send.put("accessToken", accessToken);
        send.put("clientToken", token);
        connection.getOutputStream().write(("\n" + send.toString()).getBytes());
        return connection.getResponseCode() == 204;
    }

    public User login(String username, String password) throws InvalidCredentialsException, IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL("https://authserver.mojang.com/authenticate").openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        JSONObject send = new JSONObject("{}"),
                agent = new JSONObject("{}");

        agent.put("name", "minecraft");
        agent.put("version", 1);

        send.put("username", username);
        send.put("password", password);
        send.put("agent", agent);
        send.put("clientToken", token);
        connection.getOutputStream().write(("\n" + send.toString()).getBytes());
        if (connection.getResponseCode() == 403)
            throw new InvalidCredentialsException("Invalid Credentials! ");
        JSONObject res = new JSONObject(new JSONTokener(connection.getInputStream()));
        if (!res.getString("clientToken").equals(token)) {
            throw new RuntimeException("Somehow got funny request, try again");
        }
        JSONArray profiles = res.getJSONArray("availableProfiles");
        String dn,
                id;
        if (profiles.length() < 1) {
            dn = username;
            id = res.getString("accessToken");
        } else {
            JSONObject profile = res.getJSONObject("selectedProfile");
            dn = profile.getString("name");
            id = profile.getString("id");
        }
        return new User(res.getString("accessToken"), dn, password, id, token);
    }

}

