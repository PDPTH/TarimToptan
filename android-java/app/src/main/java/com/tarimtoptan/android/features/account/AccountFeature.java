package com.tarimtoptan.android.features.account;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tarimtoptan.android.MainActivity;
import com.tarimtoptan.android.SessionStore;
import com.tarimtoptan.android.features.FeatureSupport;
import com.tarimtoptan.android.net.ApiClient;
import com.tarimtoptan.android.ui.UiKit;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccountFeature {
    private final Context context;
    private final ApiClient apiClient;
    private final SessionStore sessionStore;
    private final MainActivity.NavigationCallback nav;

    public AccountFeature(Context context, ApiClient apiClient, SessionStore sessionStore, MainActivity.NavigationCallback nav) {
        this.context = context;
        this.apiClient = apiClient;
        this.sessionStore = sessionStore;
        this.nav = nav;
    }

    // ======= LOGIN =======
    public void showLogin(LinearLayout parent) {
        parent.removeAllViews();

        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 28), UiKit.dp(context, 20), UiKit.dp(context, 28));

        TextView icon = UiKit.text(context, "🔑", 40, UiKit.ACCENT, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        icon.setPadding(0, 0, 0, UiKit.dp(context, 12));
        card.addView(icon);

        TextView heading = UiKit.text(context, "Giriş Yap", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, 0, 0, UiKit.dp(context, 6));
        card.addView(heading);

        TextView sub = UiKit.text(context, "Hesabınıza giriş yapın", 14, UiKit.MUTED, Typeface.NORMAL);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, UiKit.dp(context, 20));
        card.addView(sub);

        EditText emailField = UiKit.emailField(context, "E-posta adresiniz", "");
        EditText passwordField = UiKit.passwordField(context, "Şifreniz", "");
        card.addView(emailField);
        card.addView(passwordField);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        errorText.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
        card.addView(errorText);

        Button loginBtn = UiKit.button(context, "Giriş Yap");
        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                errorText.setText("E-posta ve şifre alanlarını doldurun.");
                return;
            }
            errorText.setText("");
            loginBtn.setEnabled(false);
            loginBtn.setText("Giriş yapılıyor...");
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);
                apiClient.post("/auth/login", body, null, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String responseBody) {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Giriş Yap");
                        try {
                            JSONObject root = new JSONObject(responseBody);
                            String token = root.optString("token");
                            JSONObject user = root.getJSONObject("user");
                            String name = (user.optString("firstName", "") + " " + user.optString("lastName", "")).trim();
                            String userId = user.optString("id");
                            String userEmail = user.optString("email");
                            sessionStore.setSession(token, userId, name, userEmail);
                            Toast.makeText(context, "Hoş geldiniz, " + name + "!", Toast.LENGTH_SHORT).show();
                            nav.refreshStatus();
                            nav.showHome();
                        } catch (Exception e) {
                            errorText.setText("Giriş bilgileri okunamadı.");
                        }
                    }
                    @Override
                    public void onError(String message) {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Giriş Yap");
                        errorText.setText(FeatureSupport.parseError(message));
                    }
                });
            } catch (Exception e) {
                loginBtn.setEnabled(true);
                loginBtn.setText("Giriş Yap");
                errorText.setText("Bir hata oluştu.");
            }
        });
        card.addView(loginBtn);

        card.addView(UiKit.divider(context));

        TextView forgotLink = UiKit.text(context, "Şifremi Unuttum", 14, UiKit.ACCENT, Typeface.NORMAL);
        forgotLink.setGravity(Gravity.CENTER);
        forgotLink.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));
        forgotLink.setOnClickListener(v -> nav.showResetPassword());
        card.addView(forgotLink);

        TextView registerLink = UiKit.text(context, "Hesabınız yok mu? Kayıt Ol", 14, UiKit.ACCENT, Typeface.BOLD);
        registerLink.setGravity(Gravity.CENTER);
        registerLink.setPadding(0, UiKit.dp(context, 4), 0, 0);
        registerLink.setOnClickListener(v -> nav.showRegister());
        card.addView(registerLink);

        parent.addView(card);
    }

    // ======= REGISTER =======
    public void showRegister(LinearLayout parent) {
        parent.removeAllViews();

        LinearLayout card = UiKit.card(context);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 28), UiKit.dp(context, 20), UiKit.dp(context, 28));

        TextView icon = UiKit.text(context, "📝", 40, UiKit.ACCENT, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        icon.setPadding(0, 0, 0, UiKit.dp(context, 12));
        card.addView(icon);

        TextView heading = UiKit.text(context, "Kayıt Ol", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, 0, 0, UiKit.dp(context, 20));
        card.addView(heading);

        EditText firstName = UiKit.field(context, "Adınız", "");
        EditText lastName = UiKit.field(context, "Soyadınız", "");
        EditText email = UiKit.emailField(context, "E-posta adresiniz", "");
        EditText password = UiKit.passwordField(context, "Şifre (en az 8 karakter)", "");
        EditText confirmPassword = UiKit.passwordField(context, "Şifre tekrar", "");

        card.addView(firstName);
        card.addView(lastName);
        card.addView(email);
        card.addView(password);
        card.addView(confirmPassword);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        errorText.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
        card.addView(errorText);

        Button registerBtn = UiKit.button(context, "Kayıt Ol");
        registerBtn.setOnClickListener(v -> {
            String fn = firstName.getText().toString().trim();
            String ln = lastName.getText().toString().trim();
            String em = email.getText().toString().trim();
            String pw = password.getText().toString().trim();
            String cpw = confirmPassword.getText().toString().trim();

            if (fn.isEmpty() || ln.isEmpty() || em.isEmpty() || pw.isEmpty()) {
                errorText.setText("Tüm alanları doldurun.");
                return;
            }
            if (pw.length() < 8) {
                errorText.setText("Şifre en az 8 karakter olmalıdır.");
                return;
            }
            if (!pw.equals(cpw)) {
                errorText.setText("Şifreler eşleşmiyor.");
                return;
            }
            errorText.setText("");
            registerBtn.setEnabled(false);
            registerBtn.setText("Kayıt yapılıyor...");
            try {
                JSONObject body = new JSONObject();
                body.put("email", em);
                body.put("password", pw);
                body.put("firstName", fn);
                body.put("lastName", ln);
                apiClient.post("/auth/register", body, null, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String responseBody) {
                        registerBtn.setEnabled(true);
                        registerBtn.setText("Kayıt Ol");
                        Toast.makeText(context, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show();
                        nav.showLogin();
                    }
                    @Override
                    public void onError(String message) {
                        registerBtn.setEnabled(true);
                        registerBtn.setText("Kayıt Ol");
                        errorText.setText(FeatureSupport.parseError(message));
                    }
                });
            } catch (Exception e) {
                registerBtn.setEnabled(true);
                registerBtn.setText("Kayıt Ol");
                errorText.setText("Bir hata oluştu.");
            }
        });
        card.addView(registerBtn);

        card.addView(UiKit.divider(context));

        TextView loginLink = UiKit.text(context, "Zaten hesabınız var mı? Giriş Yap", 14, UiKit.ACCENT, Typeface.BOLD);
        loginLink.setGravity(Gravity.CENTER);
        loginLink.setOnClickListener(v -> nav.showLogin());
        card.addView(loginLink);

        parent.addView(card);
    }

    // ======= PROFILE =======
    public void showProfile(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) { nav.showLogin(); return; }

        TextView heading = UiKit.text(context, "Profilim", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        // Fetch user info
        apiClient.get("/users/" + sessionStore.getUserId(), sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject user = new JSONObject(body);
                    renderProfileCard(parent, user);
                } catch (Exception e) {
                    renderProfileFallback(parent);
                }
            }
            @Override
            public void onError(String message) {
                renderProfileFallback(parent);
            }
        });
    }

    private void renderProfileCard(LinearLayout parent, JSONObject user) {
        String fn = user.optString("firstName", "");
        String ln = user.optString("lastName", "");
        String em = user.optString("email", "");
        String ph = user.optString("phone", "");

        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.iconCircle(context, "👤", 64, UiKit.PANEL_SOFT));

        TextView nameLabel = UiKit.text(context, fn + " " + ln, 22, UiKit.TEXT, Typeface.BOLD);
        nameLabel.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 4));
        card.addView(nameLabel);

        card.addView(infoRow("📧", "E-posta", em));
        card.addView(infoRow("📱", "Telefon", ph.isEmpty() ? "Belirtilmemiş" : ph));

        String joinDate = FeatureSupport.formatDate(user.optString("createdAt", ""));
        if (!joinDate.isEmpty()) {
            card.addView(infoRow("📅", "Üyelik Tarihi", joinDate));
        }

        card.addView(UiKit.divider(context));

        Button editBtn = UiKit.secondaryButton(context, "Profili Düzenle");
        editBtn.setOnClickListener(v -> showEditProfile(parent, user));
        card.addView(editBtn);

        parent.addView(card);

        // Danger zone
        LinearLayout dangerCard = UiKit.card(context);
        dangerCard.addView(UiKit.text(context, "Hesap İşlemleri", 18, UiKit.TEXT, Typeface.BOLD));

        Button deleteBtn = UiKit.dangerButton(context, "Hesabı Sil");
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hesabı Sil")
                    .setMessage("Hesabınız kalıcı olarak silinecektir. Bu işlem geri alınamaz.")
                    .setPositiveButton("Sil", (d, w) -> {
                        apiClient.delete("/users/" + sessionStore.getUserId(), sessionStore.getToken(), new ApiClient.Callback() {
                            @Override
                            public void onSuccess(String b) {
                                sessionStore.clearSession();
                                Toast.makeText(context, "Hesabınız silindi.", Toast.LENGTH_SHORT).show();
                                nav.refreshStatus();
                                nav.showHome();
                            }
                            @Override
                            public void onError(String m) {
                                Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        });
        dangerCard.addView(deleteBtn);
        parent.addView(dangerCard);
    }

    private void renderProfileFallback(LinearLayout parent) {
        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.text(context, sessionStore.getUserName(), 22, UiKit.TEXT, Typeface.BOLD));
        card.addView(infoRow("📧", "E-posta", sessionStore.getUserEmail()));
        parent.addView(card);
    }

    private void showEditProfile(LinearLayout parent, JSONObject user) {
        parent.removeAllViews();
        TextView heading = UiKit.text(context, "Profili Düzenle", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        LinearLayout card = UiKit.card(context);
        EditText fnField = UiKit.field(context, "Adınız", user.optString("firstName", ""));
        EditText lnField = UiKit.field(context, "Soyadınız", user.optString("lastName", ""));
        EditText phField = UiKit.phoneField(context, "Telefon", user.optString("phone", ""));

        card.addView(fnField);
        card.addView(lnField);
        card.addView(phField);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        card.addView(errorText);

        LinearLayout btnRow = UiKit.horizontal(context);
        Button saveBtn = UiKit.button(context, "Kaydet");
        Button cancelBtn = UiKit.secondaryButton(context, "İptal");

        saveBtn.setOnClickListener(v -> {
            try {
                JSONObject body = new JSONObject();
                body.put("firstName", fnField.getText().toString().trim());
                body.put("lastName", lnField.getText().toString().trim());
                body.put("phone", phField.getText().toString().trim());
                saveBtn.setEnabled(false);
                apiClient.put("/users/" + sessionStore.getUserId(), body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        saveBtn.setEnabled(true);
                        String name = fnField.getText().toString().trim() + " " + lnField.getText().toString().trim();
                        sessionStore.updateProfile(name.trim(), sessionStore.getUserEmail(), phField.getText().toString().trim());
                        Toast.makeText(context, "Profil güncellendi.", Toast.LENGTH_SHORT).show();
                        nav.refreshStatus();
                        showProfile(parent);
                    }
                    @Override
                    public void onError(String m) {
                        saveBtn.setEnabled(true);
                        errorText.setText(FeatureSupport.parseError(m));
                    }
                });
            } catch (Exception e) {
                errorText.setText("Bir hata oluştu.");
            }
        });
        cancelBtn.setOnClickListener(v -> showProfile(parent));

        LinearLayout.LayoutParams halfParams = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        halfParams.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        saveBtn.setLayoutParams(halfParams);
        LinearLayout.LayoutParams halfParams2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        halfParams2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        cancelBtn.setLayoutParams(halfParams2);
        btnRow.addView(saveBtn);
        btnRow.addView(cancelBtn);
        card.addView(btnRow);
        parent.addView(card);
    }

    private LinearLayout infoRow(String icon, String label, String value) {
        LinearLayout row = UiKit.horizontal(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        TextView iconView = UiKit.text(context, icon + " ", 14, UiKit.MUTED, Typeface.NORMAL);
        row.addView(iconView);
        TextView labelView = UiKit.text(context, label + ": ", 14, UiKit.MUTED, Typeface.BOLD);
        row.addView(labelView);
        TextView valueView = UiKit.text(context, value, 14, UiKit.TEXT, Typeface.NORMAL);
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        valueView.setLayoutParams(vp);
        row.addView(valueView);
        return row;
    }

    // ======= RESET PASSWORD =======
    public void showResetPassword(LinearLayout parent) {
        parent.removeAllViews();

        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 28), UiKit.dp(context, 20), UiKit.dp(context, 28));

        TextView icon = UiKit.text(context, "🔒", 40, UiKit.ACCENT, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        card.addView(icon);

        TextView heading = UiKit.text(context, "Şifre Sıfırlama", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 8));
        card.addView(heading);

        TextView desc = UiKit.text(context, "E-posta adresinizi girin. Şifre sıfırlama bağlantısı gönderilecektir.", 14, UiKit.MUTED, Typeface.NORMAL);
        desc.setGravity(Gravity.CENTER);
        desc.setPadding(0, 0, 0, UiKit.dp(context, 16));
        card.addView(desc);

        EditText emailField = UiKit.emailField(context, "E-posta adresiniz", "");
        card.addView(emailField);

        TextView msgText = UiKit.text(context, "", 13, UiKit.ACCENT, Typeface.NORMAL);
        msgText.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
        card.addView(msgText);

        Button sendBtn = UiKit.button(context, "Sıfırlama Bağlantısı Gönder");
        sendBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                msgText.setTextColor(UiKit.DANGER);
                msgText.setText("E-posta adresinizi girin.");
                return;
            }
            sendBtn.setEnabled(false);
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                apiClient.post("/auth/reset-password-request", body, null, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        sendBtn.setEnabled(true);
                        msgText.setTextColor(UiKit.ACCENT);
                        msgText.setText("✉ Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.");
                    }
                    @Override
                    public void onError(String m) {
                        sendBtn.setEnabled(true);
                        msgText.setTextColor(UiKit.DANGER);
                        msgText.setText(FeatureSupport.parseError(m));
                    }
                });
            } catch (Exception e) {
                sendBtn.setEnabled(true);
                msgText.setTextColor(UiKit.DANGER);
                msgText.setText("Bir hata oluştu.");
            }
        });
        card.addView(sendBtn);

        card.addView(UiKit.divider(context));

        TextView backLink = UiKit.text(context, "← Giriş sayfasına dön", 14, UiKit.ACCENT, Typeface.BOLD);
        backLink.setGravity(Gravity.CENTER);
        backLink.setOnClickListener(v -> nav.showLogin());
        card.addView(backLink);

        parent.addView(card);
    }

    // ======= ADDRESSES =======
    public void showAddresses(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) { nav.showLogin(); return; }

        TextView heading = UiKit.text(context, "Adreslerim", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        LinearLayout formContainer = UiKit.vertical(context);
        parent.addView(formContainer);

        LinearLayout listContainer = UiKit.vertical(context);
        parent.addView(listContainer);

        Button addBtn = UiKit.orangeButton(context, "＋ Yeni Adres Ekle");
        addBtn.setOnClickListener(v -> showAddressForm(formContainer, listContainer, null));
        parent.addView(addBtn);

        loadAddresses(listContainer, formContainer);
    }

    private void loadAddresses(LinearLayout listContainer, LinearLayout formContainer) {
        listContainer.removeAllViews();
        apiClient.get("/addresses", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        TextView empty = UiKit.text(context, "📍 Henüz adres eklenmemiş.", 15, UiKit.MUTED, Typeface.NORMAL);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, UiKit.dp(context, 24), 0, UiKit.dp(context, 24));
                        listContainer.addView(empty);
                        return;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject addr = data.getJSONObject(i);
                        listContainer.addView(addressCard(addr, listContainer, formContainer));
                    }
                } catch (Exception e) {
                    listContainer.addView(UiKit.text(context, "Adresler yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                listContainer.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private LinearLayout addressCard(JSONObject addr, LinearLayout listContainer, LinearLayout formContainer) {
        LinearLayout card = UiKit.card(context);
        String title = addr.optString("title", "Adres");
        String fullName = addr.optString("fullName", "");
        String city = addr.optString("city", "");
        String district = addr.optString("district", "");
        String addressLine = addr.optString("addressLine", "");
        String phone = addr.optString("phone", "");
        String addrId = addr.optString("id", "");

        LinearLayout titleRow = UiKit.horizontal(context);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.addView(UiKit.badge(context, title, UiKit.ACCENT));
        card.addView(titleRow);

        if (!fullName.isEmpty()) {
            card.addView(UiKit.text(context, fullName, 16, UiKit.TEXT, Typeface.BOLD));
        }
        String fullAddr = addressLine;
        if (!district.isEmpty()) fullAddr += ", " + district;
        if (!city.isEmpty()) fullAddr += " / " + city;
        TextView addrText = UiKit.text(context, fullAddr, 14, UiKit.MUTED, Typeface.NORMAL);
        addrText.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
        card.addView(addrText);

        if (!phone.isEmpty()) {
            card.addView(UiKit.text(context, "📱 " + phone, 13, UiKit.MUTED, Typeface.NORMAL));
        }

        Button editBtn = UiKit.secondaryButton(context, "Düzenle");
        editBtn.setOnClickListener(v -> showAddressForm(formContainer, listContainer, addr));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, UiKit.dp(context, 40));
        btnParams.setMargins(0, UiKit.dp(context, 8), 0, 0);
        editBtn.setLayoutParams(btnParams);
        card.addView(editBtn);

        return card;
    }

    private void showAddressForm(LinearLayout formContainer, LinearLayout listContainer, JSONObject existing) {
        formContainer.removeAllViews();
        boolean isEdit = existing != null;

        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.text(context, isEdit ? "Adresi Düzenle" : "Yeni Adres Ekle", 18, UiKit.TEXT, Typeface.BOLD));

        Spinner titleSpinner = UiKit.createSpinner(context, new String[]{"Ev", "İş", "Diğer"});
        if (isEdit) {
            String t = existing.optString("title", "Ev");
            if ("İş".equals(t)) titleSpinner.setSelection(1);
            else if ("Diğer".equals(t)) titleSpinner.setSelection(2);
        }
        card.addView(titleSpinner);

        EditText fullName = UiKit.field(context, "Ad Soyad", isEdit ? existing.optString("fullName", "") : "");
        EditText phone = UiKit.phoneField(context, "Telefon", isEdit ? existing.optString("phone", "") : "");
        EditText city = UiKit.field(context, "İl", isEdit ? existing.optString("city", "") : "");
        EditText district = UiKit.field(context, "İlçe", isEdit ? existing.optString("district", "") : "");
        EditText addressLine = UiKit.multiLineField(context, "Adres", isEdit ? existing.optString("addressLine", "") : "");
        EditText postalCode = UiKit.field(context, "Posta Kodu", isEdit ? existing.optString("postalCode", "") : "");

        card.addView(fullName);
        card.addView(phone);
        card.addView(city);
        card.addView(district);
        card.addView(addressLine);
        card.addView(postalCode);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        card.addView(errorText);

        LinearLayout btnRow = UiKit.horizontal(context);
        Button saveBtn = UiKit.button(context, "Kaydet");
        Button cancelBtn = UiKit.secondaryButton(context, "İptal");

        saveBtn.setOnClickListener(v -> {
            if (city.getText().toString().trim().isEmpty()) {
                errorText.setText("İl alanını doldurun.");
                return;
            }
            try {
                JSONObject body = new JSONObject();
                body.put("title", titleSpinner.getSelectedItem().toString());
                body.put("fullName", fullName.getText().toString().trim());
                body.put("phone", phone.getText().toString().trim());
                body.put("city", city.getText().toString().trim());
                body.put("district", district.getText().toString().trim());
                body.put("addressLine", addressLine.getText().toString().trim());
                body.put("postalCode", postalCode.getText().toString().trim());

                ApiClient.Callback cb = new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        formContainer.removeAllViews();
                        Toast.makeText(context, isEdit ? "Adres güncellendi." : "Adres eklendi.", Toast.LENGTH_SHORT).show();
                        loadAddresses(listContainer, formContainer);
                    }
                    @Override
                    public void onError(String m) {
                        errorText.setText(FeatureSupport.parseError(m));
                    }
                };

                if (isEdit) {
                    apiClient.put("/addresses/" + existing.optString("id"), body, sessionStore.getToken(), cb);
                } else {
                    apiClient.post("/addresses", body, sessionStore.getToken(), cb);
                }
            } catch (Exception e) {
                errorText.setText("Bir hata oluştu.");
            }
        });
        cancelBtn.setOnClickListener(v -> formContainer.removeAllViews());

        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        saveBtn.setLayoutParams(hp);
        LinearLayout.LayoutParams hp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        cancelBtn.setLayoutParams(hp2);
        btnRow.addView(saveBtn);
        btnRow.addView(cancelBtn);
        card.addView(btnRow);

        formContainer.addView(card);
    }

    // ======= PRODUCERS =======
    public void showProducers(LinearLayout parent) {
        parent.removeAllViews();

        TextView heading = UiKit.text(context, "Üreticilerimiz", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        parent.addView(loading);

        apiClient.get("/producers?limit=20", null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                parent.removeView(loading);
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        TextView empty = UiKit.text(context, "Henüz üretici bulunmuyor.", 15, UiKit.MUTED, Typeface.NORMAL);
                        empty.setGravity(Gravity.CENTER);
                        parent.addView(empty);
                        return;
                    }

                    TextView countText = UiKit.text(context, data.length() + " üretici bulundu", 13, UiKit.MUTED, Typeface.NORMAL);
                    countText.setPadding(0, 0, 0, UiKit.dp(context, 12));
                    parent.addView(countText);

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject p = data.getJSONObject(i);
                        parent.addView(producerCard(p));
                    }
                } catch (Exception e) {
                    parent.addView(UiKit.text(context, "Üreticiler yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                parent.removeView(loading);
                parent.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private LinearLayout producerCard(JSONObject producer) {
        LinearLayout card = UiKit.card(context);
        card.setClickable(true);

        LinearLayout row = UiKit.horizontal(context);
        row.setGravity(Gravity.CENTER_VERTICAL);

        String name = producer.optString("name", "Üretici");
        String initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
        TextView avatar = UiKit.iconCircle(context, initial, 50, UiKit.ACCENT);
        avatar.setTextColor(UiKit.BG);
        avatar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        avatar.setTextSize(20);
        row.addView(avatar);

        LinearLayout info = UiKit.vertical(context);
        info.setPadding(UiKit.dp(context, 14), 0, 0, 0);
        info.addView(UiKit.text(context, name, 17, UiKit.TEXT, Typeface.BOLD));

        String address = producer.optString("address", "");
        if (!address.isEmpty()) {
            info.addView(UiKit.text(context, "📍 " + address, 13, UiKit.MUTED, Typeface.NORMAL));
        }
        String email = producer.optString("email", "");
        if (!email.isEmpty()) {
            info.addView(UiKit.text(context, "📧 " + email, 12, UiKit.MUTED, Typeface.NORMAL));
        }

        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        info.setLayoutParams(ip);
        row.addView(info);

        TextView arrow = UiKit.text(context, "›", 24, UiKit.MUTED, Typeface.BOLD);
        row.addView(arrow);

        card.addView(row);

        String producerId = producer.optString("id", "");
        card.setOnClickListener(v -> nav.showProducerDetail(producerId));
        return card;
    }

    // ======= PRODUCER DETAIL =======
    public void showProducerDetail(LinearLayout parent, String producerId) {
        parent.removeAllViews();

        Button backBtn = UiKit.secondaryButton(context, "‹ Üreticilere Dön");
        backBtn.setOnClickListener(v -> nav.showProducers());
        parent.addView(backBtn);

        apiClient.get("/producers/" + producerId, null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject producer = new JSONObject(body);
                    renderProducerDetail(parent, producer, producerId);
                } catch (Exception e) {
                    parent.addView(UiKit.text(context, "Üretici bilgileri yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                parent.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private void renderProducerDetail(LinearLayout parent, JSONObject producer, String producerId) {
        // Header banner
        LinearLayout header = UiKit.card(context);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setBackground(UiKit.rounded(context, UiKit.ACCENT, UiKit.ACCENT, 14, 0));
        header.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 24), UiKit.dp(context, 20), UiKit.dp(context, 24));

        String name = producer.optString("name", "Üretici");
        String initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
        TextView avatar = UiKit.iconCircle(context, initial, 72, UiKit.PANEL);
        avatar.setTextColor(UiKit.TEXT);
        avatar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        avatar.setTextSize(28);
        header.addView(avatar);

        TextView nameView = UiKit.text(context, name, 24, UiKit.BG, Typeface.BOLD);
        nameView.setGravity(Gravity.CENTER);
        nameView.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 4));
        header.addView(nameView);

        String address = producer.optString("address", "");
        if (!address.isEmpty()) {
            TextView loc = UiKit.text(context, "📍 " + address, 14, UiKit.PANEL, Typeface.NORMAL);
            loc.setGravity(Gravity.CENTER);
            header.addView(loc);
        }
        String email = producer.optString("email", "");
        if (!email.isEmpty()) {
            TextView em = UiKit.text(context, "📧 " + email, 13, UiKit.PANEL, Typeface.NORMAL);
            em.setGravity(Gravity.CENTER);
            header.addView(em);
        }
        parent.addView(header);

        // Products section
        TextView prodTitle = UiKit.text(context, "Ürünler", 20, UiKit.TEXT, Typeface.BOLD);
        prodTitle.setPadding(0, UiKit.dp(context, 20), 0, UiKit.dp(context, 12));
        parent.addView(prodTitle);

        apiClient.get("/products?producerId=" + producerId + "&limit=20", null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        parent.addView(UiKit.text(context, "Bu üreticiye ait ürün bulunmuyor.", 14, UiKit.MUTED, Typeface.NORMAL));
                        return;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject prod = data.getJSONObject(i);
                        LinearLayout card = UiKit.card(context);
                        LinearLayout row = UiKit.horizontal(context);
                        row.setGravity(Gravity.CENTER_VERTICAL);

                        String cat = prod.optString("category", "");
                        String emoji = UiKit.categoryEmoji(cat);
                        TextView emojiView = UiKit.text(context, emoji, 28, UiKit.ACCENT, Typeface.NORMAL);
                        emojiView.setPadding(0, 0, UiKit.dp(context, 12), 0);
                        row.addView(emojiView);

                        LinearLayout info = UiKit.vertical(context);
                        info.addView(UiKit.text(context, prod.optString("name", ""), 16, UiKit.TEXT, Typeface.BOLD));
                        TextView price = UiKit.text(context, FeatureSupport.formatPrice(prod.optDouble("price", 0)), 15, UiKit.ORANGE, Typeface.BOLD);
                        price.setPadding(0, UiKit.dp(context, 2), 0, 0);
                        info.addView(price);
                        LinearLayout.LayoutParams ip2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                        info.setLayoutParams(ip2);
                        row.addView(info);

                        card.addView(row);
                        parent.addView(card);
                    }
                } catch (Exception e) {
                    parent.addView(UiKit.text(context, "Ürünler yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                parent.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }
}
