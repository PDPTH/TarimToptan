package com.tarimtoptan.android;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionStore {
    private static final String PREFS_NAME = "tarimtoptan_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";

    private String token = "";
    private String userId = "";
    private String userName = "";
    private String userEmail = "";
    private String userPhone = "";
    private String selectedProductId = "";
    private String selectedProductName = "";

    private final SharedPreferences prefs;

    public SessionStore(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        token = prefs.getString(KEY_TOKEN, "");
        userId = prefs.getString(KEY_USER_ID, "");
        userName = prefs.getString(KEY_USER_NAME, "");
        userEmail = prefs.getString(KEY_USER_EMAIL, "");
        userPhone = prefs.getString(KEY_USER_PHONE, "");
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getUserPhone() { return userPhone; }
    public String getSelectedProductId() { return selectedProductId; }
    public String getSelectedProductName() { return selectedProductName; }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public void setSession(String token, String userId, String userName, String email) {
        this.token = token == null ? "" : token;
        this.userId = userId == null ? "" : userId;
        this.userName = userName == null ? "" : userName;
        this.userEmail = email == null ? "" : email;
        prefs.edit()
                .putString(KEY_TOKEN, this.token)
                .putString(KEY_USER_ID, this.userId)
                .putString(KEY_USER_NAME, this.userName)
                .putString(KEY_USER_EMAIL, this.userEmail)
                .apply();
    }

    public void setSession(String token, String userId, String userName) {
        setSession(token, userId, userName, this.userEmail);
    }

    public void updateProfile(String name, String email, String phone) {
        this.userName = name == null ? "" : name;
        this.userEmail = email == null ? "" : email;
        this.userPhone = phone == null ? "" : phone;
        prefs.edit()
                .putString(KEY_USER_NAME, this.userName)
                .putString(KEY_USER_EMAIL, this.userEmail)
                .putString(KEY_USER_PHONE, this.userPhone)
                .apply();
    }

    public void clearSession() {
        token = "";
        userId = "";
        userName = "";
        userEmail = "";
        userPhone = "";
        prefs.edit().clear().apply();
    }

    public void setSelectedProduct(String productId, String productName) {
        selectedProductId = productId == null ? "" : productId;
        selectedProductName = productName == null ? "" : productName;
    }
}
