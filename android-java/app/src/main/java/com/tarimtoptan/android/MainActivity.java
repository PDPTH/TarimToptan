package com.tarimtoptan.android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tarimtoptan.android.features.account.AccountFeature;
import com.tarimtoptan.android.features.checkout.CheckoutFeature;
import com.tarimtoptan.android.features.products.ProductsFeature;
import com.tarimtoptan.android.net.ApiClient;
import com.tarimtoptan.android.ui.UiKit;

public class MainActivity extends Activity {

    /** Navigation callback interface for cross-feature navigation */
    public interface NavigationCallback {
        void showHome();
        void showProducts();
        void showCart();
        void showProducers();
        void showLogin();
        void showRegister();
        void showProfile();
        void showOrders();
        void showProductManagement();
        void showAddresses();
        void showResetPassword();
        void showProducerDetail(String producerId);
        void refreshStatus();
    }

    private ApiClient apiClient;
    private SessionStore sessionStore;
    private LinearLayout content;
    private ScrollView scrollView;
    private TextView statusText;
    private PopupWindow menuWindow;
    private TextView cartBadgeView;

    // Navigation history for back button
    private String currentScreen = "home";
    private String previousScreen = "home";

    // Feature instances (lazy)
    private AccountFeature accountFeature;
    private ProductsFeature productsFeature;
    private CheckoutFeature checkoutFeature;

    private final NavigationCallback nav = new NavigationCallback() {
        @Override public void showHome() { MainActivity.this.showHome(); }
        @Override public void showProducts() { MainActivity.this.showProducts(); }
        @Override public void showCart() { MainActivity.this.showCartScreen(); }
        @Override public void showProducers() { MainActivity.this.showProducers(); }
        @Override public void showLogin() { MainActivity.this.showLogin(); }
        @Override public void showRegister() { MainActivity.this.showRegister(); }
        @Override public void showProfile() { MainActivity.this.showProfile(); }
        @Override public void showOrders() { MainActivity.this.showOrdersScreen(); }
        @Override public void showProductManagement() { MainActivity.this.showProductManagement(); }
        @Override public void showAddresses() { MainActivity.this.showAddresses(); }
        @Override public void showResetPassword() { MainActivity.this.showResetPassword(); }
        @Override public void showProducerDetail(String producerId) { MainActivity.this.showProducerDetail(producerId); }
        @Override public void refreshStatus() { MainActivity.this.refreshStatus(); }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new ApiClient();
        sessionStore = new SessionStore(this);
        setContentView(createRoot());
        showHome();
        updateCartBadge();
    }

    @Override
    public void onBackPressed() {
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.dismiss();
            return;
        }
        if (!"home".equals(currentScreen)) {
            showHome();
        } else {
            super.onBackPressed();
        }
    }

    private LinearLayout createRoot() {
        LinearLayout root = UiKit.vertical(this);
        root.setBackgroundColor(UiKit.BG);

        root.addView(createTopBar());

        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(UiKit.BG);

        LinearLayout page = UiKit.vertical(this);
        page.setPadding(UiKit.dp(this, 16), UiKit.dp(this, 12), UiKit.dp(this, 16), UiKit.dp(this, 28));
        scrollView.addView(page);

        statusText = UiKit.text(this, "", 12, UiKit.MUTED, Typeface.NORMAL);
        statusText.setPadding(0, 0, 0, UiKit.dp(this, 8));
        page.addView(statusText);

        content = UiKit.vertical(this);
        page.addView(content);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
        ));

        return root;
    }

    private View createTopBar() {
        LinearLayout bar = UiKit.horizontal(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(UiKit.dp(this, 10), UiKit.dp(this, 10), UiKit.dp(this, 10), UiKit.dp(this, 10));
        bar.setBackgroundColor(UiKit.SURFACE);

        // Hamburger menu
        ImageButton menuButton = UiKit.iconButton(this, R.drawable.ic_menu);
        menuButton.setContentDescription("Menü");
        menuButton.setOnClickListener(this::showDrawer);
        bar.addView(menuButton);

        // Brand logo + text
        LinearLayout brand = UiKit.horizontal(this);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        brand.setPadding(UiKit.dp(this, 6), 0, 0, 0);
        brand.setOnClickListener(view -> showHome());

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_logo_wheat);
        brand.addView(logo, new LinearLayout.LayoutParams(UiKit.dp(this, 32), UiKit.dp(this, 32)));

        TextView brandText = UiKit.text(this, "TarımToptan", 20, UiKit.ACCENT, Typeface.BOLD);
        LinearLayout.LayoutParams brandTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        brandTextParams.setMargins(UiKit.dp(this, 8), 0, 0, 0);
        brand.addView(brandText, brandTextParams);

        bar.addView(brand, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        // Account button
        ImageButton accountButton = UiKit.iconButton(this, R.drawable.ic_account);
        accountButton.setContentDescription("Hesap");
        accountButton.setOnClickListener(view -> {
            if (sessionStore.isLoggedIn()) {
                showProfile();
            } else {
                showLogin();
            }
        });
        bar.addView(accountButton);

        // Cart button with badge
        LinearLayout cartContainer = UiKit.horizontal(this);
        cartContainer.setGravity(Gravity.CENTER_VERTICAL);
        ImageButton cartButton = UiKit.iconButton(this, R.drawable.ic_cart);
        cartButton.setContentDescription("Sepet");
        cartButton.setOnClickListener(view -> showCartScreen());
        cartContainer.addView(cartButton);

        cartBadgeView = UiKit.cartBadge(this);
        cartContainer.addView(cartBadgeView);

        bar.addView(cartContainer);

        return bar;
    }

    private void showDrawer(View anchor) {
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.dismiss();
            return;
        }

        LinearLayout drawer = UiKit.vertical(this);
        drawer.setPadding(UiKit.dp(this, 18), UiKit.dp(this, 28), UiKit.dp(this, 18), UiKit.dp(this, 18));
        drawer.setBackgroundColor(UiKit.SURFACE);

        // Brand header
        LinearLayout drawerBrand = UiKit.horizontal(this);
        drawerBrand.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_logo_wheat);
        drawerBrand.addView(logo, new LinearLayout.LayoutParams(UiKit.dp(this, 34), UiKit.dp(this, 34)));

        TextView title = UiKit.text(this, "TarımToptan", 22, UiKit.ACCENT, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(UiKit.dp(this, 10), 0, 0, 0);
        drawerBrand.addView(title, titleParams);
        drawer.addView(drawerBrand);

        TextView subtitle = UiKit.text(this, "Taze tarım ürünleri", 13, UiKit.MUTED, Typeface.NORMAL);
        subtitle.setPadding(0, UiKit.dp(this, 4), 0, UiKit.dp(this, 16));
        drawer.addView(subtitle);

        // Main navigation items
        drawer.addView(drawerItem("🏠  Ana Sayfa", view -> showHome()));
        drawer.addView(drawerItem("📦  Ürünler", view -> showProducts()));
        drawer.addView(drawerItem("👨‍🌾  Üreticiler", view -> showProducers()));

        // Divider
        drawer.addView(UiKit.divider(this));

        if (sessionStore.isLoggedIn()) {
            drawer.addView(drawerItem("🛒  Sepetim", view -> showCartScreen()));
            drawer.addView(drawerItem("📋  Siparişlerim", view -> showOrdersScreen()));
            drawer.addView(drawerItem("➕  Ürün Ekle", view -> showProductManagement()));

            drawer.addView(UiKit.divider(this));

            drawer.addView(drawerItem("👤  Profilim", view -> showProfile()));
            drawer.addView(drawerItem("📍  Adreslerim", view -> showAddresses()));

            drawer.addView(UiKit.divider(this));

            Button logoutButton = drawerItem("🚪  Çıkış (" + sessionStore.getUserName() + ")", view -> {
                sessionStore.clearSession();
                Toast.makeText(this, "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
                showHome();
                updateCartBadge();
            });
            logoutButton.setTextColor(UiKit.DANGER);
            drawer.addView(logoutButton);
        } else {
            drawer.addView(UiKit.divider(this));
            drawer.addView(drawerItem("🔑  Giriş Yap", view -> showLogin()));
            drawer.addView(drawerItem("📝  Kayıt Ol", view -> showRegister()));
        }

        menuWindow = new PopupWindow(
                drawer,
                UiKit.dp(this, 288),
                LinearLayout.LayoutParams.MATCH_PARENT,
                true
        );
        menuWindow.setBackgroundDrawable(new ColorDrawable(UiKit.SURFACE));
        menuWindow.setOutsideTouchable(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            menuWindow.setElevation(UiKit.dp(this, 16));
        }
        menuWindow.showAtLocation(anchor, Gravity.START | Gravity.TOP, 0, 0);
    }

    private Button drawerItem(String label, View.OnClickListener listener) {
        Button button = UiKit.menuButton(this, label);
        button.setOnClickListener(view -> {
            closeDrawer();
            listener.onClick(view);
        });
        return button;
    }

    private void closeDrawer() {
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.dismiss();
        }
    }

    private void refreshStatus() {
        if (sessionStore.isLoggedIn()) {
            statusText.setText("Hoş geldin, " + sessionStore.getUserName() + " 👋");
        } else {
            statusText.setText("Tarım ürünlerini keşfetmeye başlayın.");
        }
    }

    private void resetContent() {
        content.removeAllViews();
        refreshStatus();
        scrollView.scrollTo(0, 0);
    }

    private void navigate(String screen) {
        previousScreen = currentScreen;
        currentScreen = screen;
    }

    // ===== Feature instance helpers =====

    private AccountFeature getAccountFeature() {
        if (accountFeature == null) {
            accountFeature = new AccountFeature(this, apiClient, sessionStore, nav);
        }
        return accountFeature;
    }

    private ProductsFeature getProductsFeature() {
        if (productsFeature == null) {
            productsFeature = new ProductsFeature(this, apiClient, sessionStore, nav);
        }
        return productsFeature;
    }

    private CheckoutFeature getCheckoutFeature() {
        if (checkoutFeature == null) {
            checkoutFeature = new CheckoutFeature(this, apiClient, sessionStore, nav);
        }
        return checkoutFeature;
    }

    // ===== Navigation methods =====

    private void showHome() {
        closeDrawer();
        resetContent();
        navigate("home");

        // Hero section
        LinearLayout hero = UiKit.vertical(this);
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(0, UiKit.dp(this, 28), 0, UiKit.dp(this, 30));

        TextView heading = UiKit.text(this, "Tarladan Sofranıza\nTaze Tarım Ürünleri", 33, UiKit.MUTED, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setLineSpacing(0, 1.05f);
        hero.addView(heading);

        TextView paragraph = UiKit.text(this,
                "Nohut, pirinç, mercimek, bulgur ve daha fazlası doğrudan üreticilerden, toptan fiyatlarla kapınıza kadar.",
                16, UiKit.MUTED, Typeface.NORMAL);
        paragraph.setGravity(Gravity.CENTER);
        paragraph.setPadding(0, UiKit.dp(this, 18), 0, UiKit.dp(this, 20));
        hero.addView(paragraph);

        // CTA buttons
        LinearLayout actions = UiKit.horizontal(this);
        Button discover = UiKit.button(this, "Ürünleri Keşfet");
        Button registerBtn = UiKit.secondaryButton(this, "Hemen Üye Ol");
        discover.setOnClickListener(view -> showProducts());
        registerBtn.setOnClickListener(view -> {
            if (sessionStore.isLoggedIn()) {
                showProfile();
            } else {
                showRegister();
            }
        });
        actions.addView(homeAction(discover, 0, UiKit.dp(this, 5)));
        actions.addView(homeAction(registerBtn, UiKit.dp(this, 5), 0));
        hero.addView(actions);
        content.addView(hero);

        // Stats row 1
        LinearLayout rowOne = UiKit.horizontal(this);
        rowOne.addView(statCard("500+", "Ürün Çeşidi", 0, UiKit.dp(this, 6)));
        rowOne.addView(statCard("120+", "Üretici", UiKit.dp(this, 6), 0));
        content.addView(rowOne);

        // Stats row 2
        LinearLayout rowTwo = UiKit.horizontal(this);
        rowTwo.addView(statCard("10K+", "Mutlu Müşteri", 0, UiKit.dp(this, 6)));
        rowTwo.addView(statCard("4.8", "Müşteri Puanı", UiKit.dp(this, 6), 0));
        content.addView(rowTwo);

        // Features section
        content.addView(featureCard("🌿", "Doğal Ürünler", "Tüm ürünler doğal ve organik tarım yöntemleriyle üretilmektedir."));
        content.addView(featureCard("🚚", "Hızlı Teslimat", "Siparişleriniz en kısa sürede kapınıza teslim edilir."));
        content.addView(featureCard("💰", "Toptan Fiyat", "Aracısız, doğrudan üreticiden toptan fiyatlarla alışveriş yapın."));

        // CTA banner
        LinearLayout ctaBanner = UiKit.card(this);
        ctaBanner.setGravity(Gravity.CENTER);
        ctaBanner.setPadding(UiKit.dp(this, 20), UiKit.dp(this, 24), UiKit.dp(this, 20), UiKit.dp(this, 24));

        TextView ctaTitle = UiKit.text(this, "Üretici Olarak Katılın", 22, UiKit.TEXT, Typeface.BOLD);
        ctaTitle.setGravity(Gravity.CENTER);
        ctaBanner.addView(ctaTitle);

        TextView ctaDesc = UiKit.text(this, "Ürünlerinizi binlerce müşteriye ulaştırın.", 14, UiKit.MUTED, Typeface.NORMAL);
        ctaDesc.setGravity(Gravity.CENTER);
        ctaDesc.setPadding(0, UiKit.dp(this, 8), 0, UiKit.dp(this, 16));
        ctaBanner.addView(ctaDesc);

        Button ctaButton = UiKit.orangeButton(this, "Hemen Başlayın");
        ctaButton.setOnClickListener(view -> {
            if (sessionStore.isLoggedIn()) {
                showProductManagement();
            } else {
                showRegister();
            }
        });
        ctaBanner.addView(ctaButton);
        content.addView(ctaBanner);
    }

    private LinearLayout featureCard(String emoji, String title, String description) {
        LinearLayout card = UiKit.card(this);
        LinearLayout row = UiKit.horizontal(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView emojiView = UiKit.text(this, emoji, 28, UiKit.ACCENT, Typeface.NORMAL);
        emojiView.setPadding(0, 0, UiKit.dp(this, 14), 0);
        row.addView(emojiView);

        LinearLayout textContainer = UiKit.vertical(this);
        textContainer.addView(UiKit.text(this, title, 17, UiKit.TEXT, Typeface.BOLD));
        TextView desc = UiKit.text(this, description, 13, UiKit.MUTED, Typeface.NORMAL);
        desc.setPadding(0, UiKit.dp(this, 4), 0, 0);
        textContainer.addView(desc);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textContainer.setLayoutParams(textParams);
        row.addView(textContainer);

        card.addView(row);
        return card;
    }

    private Button homeAction(Button button, int leftMargin, int rightMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, UiKit.dp(this, 54), 1);
        params.setMargins(leftMargin, 0, rightMargin, 0);
        button.setLayoutParams(params);
        return button;
    }

    private LinearLayout statCard(String value, String label, int leftMargin, int rightMargin) {
        LinearLayout card = UiKit.card(this);
        card.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, UiKit.dp(this, 120), 1);
        params.setMargins(leftMargin, 0, rightMargin, UiKit.dp(this, 12));
        card.setLayoutParams(params);

        TextView number = UiKit.text(this, value, 32, UiKit.ACCENT, Typeface.BOLD);
        number.setGravity(Gravity.CENTER);
        card.addView(number);

        TextView caption = UiKit.text(this, label, 13, UiKit.MUTED, Typeface.NORMAL);
        caption.setGravity(Gravity.CENTER);
        caption.setPadding(0, UiKit.dp(this, 6), 0, 0);
        card.addView(caption);
        return card;
    }

    private void showProducts() {
        closeDrawer();
        resetContent();
        navigate("products");
        getProductsFeature().showProducts(content);
    }

    private void showCartScreen() {
        closeDrawer();
        resetContent();
        navigate("cart");
        getCheckoutFeature().showCart(content);
    }

    private void showOrdersScreen() {
        closeDrawer();
        resetContent();
        navigate("orders");
        getCheckoutFeature().showOrders(content);
    }

    private void showProducers() {
        closeDrawer();
        resetContent();
        navigate("producers");
        getAccountFeature().showProducers(content);
    }

    private void showLogin() {
        closeDrawer();
        resetContent();
        navigate("login");
        getAccountFeature().showLogin(content);
    }

    private void showRegister() {
        closeDrawer();
        resetContent();
        navigate("register");
        getAccountFeature().showRegister(content);
    }

    private void showProfile() {
        closeDrawer();
        resetContent();
        navigate("profile");
        getAccountFeature().showProfile(content);
    }

    private void showProductManagement() {
        closeDrawer();
        resetContent();
        navigate("product_management");
        getProductsFeature().showProductManagement(content);
    }

    private void showAddresses() {
        closeDrawer();
        resetContent();
        navigate("addresses");
        getAccountFeature().showAddresses(content);
    }

    private void showResetPassword() {
        closeDrawer();
        resetContent();
        navigate("reset_password");
        getAccountFeature().showResetPassword(content);
    }

    private void showProducerDetail(String producerId) {
        closeDrawer();
        resetContent();
        navigate("producer_detail");
        getAccountFeature().showProducerDetail(content, producerId);
    }

    /** Update the cart badge count */
    public void updateCartBadge() {
        if (!sessionStore.isLoggedIn() || cartBadgeView == null) {
            if (cartBadgeView != null) cartBadgeView.setVisibility(View.GONE);
            return;
        }
        apiClient.get("/cart", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    org.json.JSONObject cart = new org.json.JSONObject(body);
                    org.json.JSONArray items = cart.optJSONArray("items");
                    int count = items != null ? items.length() : 0;
                    if (count > 0) {
                        cartBadgeView.setText(String.valueOf(count));
                        cartBadgeView.setVisibility(View.VISIBLE);
                    } else {
                        cartBadgeView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    cartBadgeView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                cartBadgeView.setVisibility(View.GONE);
            }
        });
    }
}
