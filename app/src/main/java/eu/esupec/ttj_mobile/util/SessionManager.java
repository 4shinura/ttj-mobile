package eu.esupec.ttj_mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

import eu.esupec.ttj_mobile.entity.User;

public class SessionManager {
    private static final String PREF_NAME = "TTJ_SESSION";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER = "user_info";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        decodeAndSaveUser(token);
        editor.apply();
    }

    /**
     * Décode le payload du JWT pour extraire les informations de l'utilisateur.
     * Le token est au format header.payload.signature
     */
    private void decodeAndSaveUser(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return;

            // Le payload est la deuxième partie du token (index 1)
            String payloadEncoded = parts[1];
            byte[] decodedBytes = Base64.decode(payloadEncoded, Base64.DEFAULT);
            String payloadJson = new String(decodedBytes, "UTF-8");
            
            Log.d("SessionManager", "Payload décode : " + payloadJson);
            
            JsonObject jsonObject = gson.fromJson(payloadJson, JsonObject.class);
            if (jsonObject.has("user")) {
                User user = gson.fromJson(jsonObject.get("user"), User.class);
                saveUser(user);
            }
        } catch (Exception e) {
            Log.e("SessionManager", "Erreur lors du décodage du token", e);
        }
    }

    public void saveUser(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson == null) return null;
        return gson.fromJson(userJson, User.class);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void logout() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
