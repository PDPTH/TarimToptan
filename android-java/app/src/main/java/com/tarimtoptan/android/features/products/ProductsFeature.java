package com.tarimtoptan.android.features.products;

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

public class ProductsFeature {
    private final Context context;
    private final ApiClient apiClient;
    private final SessionStore sessionStore;
    private final MainActivity.NavigationCallback nav;

    private int currentPage = 1;
    private int totalPages = 1;
    private String searchQuery = "";
    private String selectedCategory = "";
    private String selectedSort = "";
    private boolean managementAddTab = false;

    // Fallback products for when API fails
    private static final String[][] FALLBACK = {
            {"Kırmızı Mercimek", "Bakliyat", "54.90", "500"},
            {"Osmancık Pirinç", "Tahıl", "189.00", "300"},
            {"Sarı Nohut", "Bakliyat", "72.50", "400"},
            {"Köftelik Bulgur", "Tahıl", "42.00", "600"},
            {"Kuru Fasulye", "Bakliyat", "96.00", "250"},
            {"Tam Buğday Unu", "Tahıl", "38.50", "700"},
            {"Doğal Bahçe Domatesi", "Sebze", "79.90", "150"},
            {"Köy Biberi", "Sebze", "64.00", "220"},
            {"Amasya Elması", "Meyve", "58.00", "350"},
            {"Kuru Kayısı", "Meyve", "145.00", "180"},
            {"Ceviz İçi", "Kuruyemiş", "210.00", "90"},
            {"Antep Fıstığı", "Kuruyemiş", "325.00", "75"},
    };

    public ProductsFeature(Context context, ApiClient apiClient, SessionStore sessionStore, MainActivity.NavigationCallback nav) {
        this.context = context;
        this.apiClient = apiClient;
        this.sessionStore = sessionStore;
        this.nav = nav;
    }

    // ======= PRODUCT CATALOG =======
    public void showProducts(LinearLayout parent) {
        parent.removeAllViews();

        TextView heading = UiKit.text(context, "Ürünlerimiz", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 4));
        parent.addView(heading);

        TextView subtitle = UiKit.text(context, "Doğal tarım ürünlerini keşfedin", 14, UiKit.MUTED, Typeface.NORMAL);
        subtitle.setPadding(0, 0, 0, UiKit.dp(context, 16));
        parent.addView(subtitle);

        // Search bar
        EditText searchField = UiKit.field(context, "🔍 Ürün ara...", searchQuery);
        parent.addView(searchField);

        // Filter row
        LinearLayout filterRow = UiKit.horizontal(context);
        String[] categories = {"Tümü", "Bakliyat", "Tahıl", "Sebze", "Meyve", "Süt Ürünleri", "Kuruyemiş", "Diğer"};
        Spinner catSpinner = UiKit.createSpinner(context, categories);
        LinearLayout.LayoutParams sp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        sp1.setMargins(0, 0, UiKit.dp(context, 5), 0);
        catSpinner.setLayoutParams(sp1);

        String[] sorts = {"Varsayılan", "Fiyat ↑", "Fiyat ↓", "A-Z", "Z-A"};
        Spinner sortSpinner = UiKit.createSpinner(context, sorts);
        LinearLayout.LayoutParams sp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        sp2.setMargins(UiKit.dp(context, 5), 0, 0, 0);
        sortSpinner.setLayoutParams(sp2);

        filterRow.addView(catSpinner);
        filterRow.addView(sortSpinner);
        parent.addView(filterRow);

        Button filterBtn = UiKit.secondaryButton(context, "Filtrele");
        LinearLayout.LayoutParams fbp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, UiKit.dp(context, 42));
        fbp.setMargins(0, UiKit.dp(context, 6), 0, UiKit.dp(context, 12));
        filterBtn.setLayoutParams(fbp);
        filterBtn.setOnClickListener(v -> {
            searchQuery = searchField.getText().toString().trim();
            int catIdx = catSpinner.getSelectedItemPosition();
            selectedCategory = catIdx == 0 ? "" : categories[catIdx];
            selectedSort = sorts[sortSpinner.getSelectedItemPosition()];
            currentPage = 1;
            loadProducts(parent, searchField, catSpinner, sortSpinner);
        });
        parent.addView(filterBtn);

        // Products container
        LinearLayout productsContainer = UiKit.vertical(context);
        productsContainer.setTag("products_container");
        parent.addView(productsContainer);

        // Pagination container
        LinearLayout paginationContainer = UiKit.horizontal(context);
        paginationContainer.setTag("pagination_container");
        paginationContainer.setGravity(Gravity.CENTER);
        parent.addView(paginationContainer);

        loadProducts(parent, searchField, catSpinner, sortSpinner);
    }

    private void loadProducts(LinearLayout parent, EditText searchField, Spinner catSpinner, Spinner sortSpinner) {
        LinearLayout productsContainer = parent.findViewWithTag("products_container");
        LinearLayout paginationContainer = parent.findViewWithTag("pagination_container");
        if (productsContainer == null || paginationContainer == null) return;

        productsContainer.removeAllViews();
        paginationContainer.removeAllViews();

        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        productsContainer.addView(loading);

        // Build URL
        StringBuilder url = new StringBuilder("/products?page=" + currentPage + "&limit=6");
        if (!selectedCategory.isEmpty()) {
            url.append("&category=").append(selectedCategory);
        }

        apiClient.get(url.toString(), null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                productsContainer.removeAllViews();
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    JSONObject pagination = root.optJSONObject("pagination");

                    if (pagination != null) {
                        totalPages = pagination.optInt("totalPages", 1);
                    }

                    if (data == null || data.length() == 0) {
                        TextView empty = UiKit.text(context, "Ürün bulunamadı.", 15, UiKit.MUTED, Typeface.NORMAL);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, UiKit.dp(context, 24), 0, UiKit.dp(context, 24));
                        productsContainer.addView(empty);
                        return;
                    }

                    // Filter by search query locally
                    JSONArray filtered = filterAndSort(data);
                    renderProductGrid(productsContainer, filtered, parent, searchField, catSpinner, sortSpinner);
                    renderPagination(paginationContainer, parent, searchField, catSpinner, sortSpinner);

                } catch (Exception e) {
                    renderFallback(productsContainer, parent);
                }
            }

            @Override
            public void onError(String m) {
                productsContainer.removeAllViews();
                renderFallback(productsContainer, parent);
            }
        });
    }

    private JSONArray filterAndSort(JSONArray data) {
        // Local search filter
        if (searchQuery.isEmpty()) return data;
        JSONArray filtered = new JSONArray();
        String q = searchQuery.toLowerCase();
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject p = data.getJSONObject(i);
                String name = p.optString("name", "").toLowerCase();
                String cat = p.optString("category", "").toLowerCase();
                if (name.contains(q) || cat.contains(q)) {
                    filtered.put(p);
                }
            } catch (Exception ignored) {}
        }
        return filtered;
    }

    private void renderProductGrid(LinearLayout container, JSONArray data,
                                   LinearLayout parent, EditText searchField, Spinner catSpinner, Spinner sortSpinner) {
        for (int i = 0; i < data.length(); i += 2) {
            LinearLayout row = UiKit.horizontal(context);
            try {
                row.addView(productCard(data.getJSONObject(i), parent));
            } catch (Exception ignored) {}

            if (i + 1 < data.length()) {
                try {
                    row.addView(productCard(data.getJSONObject(i + 1), parent));
                } catch (Exception ignored) {}
            } else {
                // Spacer for last odd card
                LinearLayout spacer = UiKit.vertical(context);
                LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, 1, 1);
                sp.setMargins(UiKit.dp(context, 4), 0, 0, 0);
                spacer.setLayoutParams(sp);
                row.addView(spacer);
            }
            container.addView(row);
        }
    }

    private LinearLayout productCard(JSONObject product, LinearLayout catalogParent) {
        String name = product.optString("name", "Ürün");
        String category = product.optString("category", "");
        double price = product.optDouble("price", 0);
        int stock = product.optInt("stock", 0);
        String productId = product.optString("id", "");
        String emoji = UiKit.categoryEmoji(category);

        LinearLayout card = UiKit.vertical(context);
        card.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 12, 1));
        card.setPadding(UiKit.dp(context, 12), UiKit.dp(context, 14), UiKit.dp(context, 12), UiKit.dp(context, 14));
        card.setClickable(true);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cp.setMargins(UiKit.dp(context, 4), 0, UiKit.dp(context, 4), UiKit.dp(context, 10));
        card.setLayoutParams(cp);

        // Emoji as image placeholder
        TextView emojiView = UiKit.text(context, emoji, 36, UiKit.ACCENT, Typeface.NORMAL);
        emojiView.setGravity(Gravity.CENTER);
        emojiView.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));
        card.addView(emojiView);

        // Category badge
        if (!category.isEmpty()) {
            card.addView(UiKit.badge(context, category, UiKit.ACCENT));
        }

        // Name
        TextView nameView = UiKit.text(context, name, 15, UiKit.TEXT, Typeface.BOLD);
        nameView.setMaxLines(2);
        nameView.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
        card.addView(nameView);

        // Price
        TextView priceView = UiKit.text(context, FeatureSupport.formatPrice(price) + " / kg", 15, UiKit.ORANGE, Typeface.BOLD);
        card.addView(priceView);

        // Stock
        String stockText = stock > 0 ? stock + " adet stokta" : "Stok tükendi";
        int stockColor = stock > 0 ? UiKit.ACCENT : UiKit.DANGER;
        TextView stockView = UiKit.text(context, stockText, 11, stockColor, Typeface.NORMAL);
        stockView.setPadding(0, UiKit.dp(context, 4), 0, 0);
        card.addView(stockView);

        card.setOnClickListener(v -> showProductDetail(catalogParent, productId));

        return card;
    }

    private void renderPagination(LinearLayout container, LinearLayout parent,
                                  EditText searchField, Spinner catSpinner, Spinner sortSpinner) {
        if (totalPages <= 1) return;

        container.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 12));

        // Previous
        if (currentPage > 1) {
            Button prev = pageButton("‹");
            prev.setOnClickListener(v -> {
                currentPage--;
                loadProducts(parent, searchField, catSpinner, sortSpinner);
            });
            container.addView(prev);
        }

        // Page numbers
        int start = Math.max(1, currentPage - 2);
        int end = Math.min(totalPages, currentPage + 2);
        for (int i = start; i <= end; i++) {
            Button page = pageButton(String.valueOf(i));
            if (i == currentPage) {
                page.setBackground(UiKit.rounded(context, UiKit.ACCENT, UiKit.ACCENT, 8, 0));
                page.setTextColor(UiKit.BG);
            }
            final int pageNum = i;
            page.setOnClickListener(v -> {
                currentPage = pageNum;
                loadProducts(parent, searchField, catSpinner, sortSpinner);
            });
            container.addView(page);
        }

        // Next
        if (currentPage < totalPages) {
            Button next = pageButton("›");
            next.setOnClickListener(v -> {
                currentPage++;
                loadProducts(parent, searchField, catSpinner, sortSpinner);
            });
            container.addView(next);
        }
    }

    private Button pageButton(String label) {
        Button btn = new Button(context);
        btn.setText(label);
        btn.setTextSize(14);
        btn.setTextColor(UiKit.TEXT);
        btn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        btn.setAllCaps(false);
        btn.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 8, 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UiKit.dp(context, 40), UiKit.dp(context, 40));
        lp.setMargins(UiKit.dp(context, 3), 0, UiKit.dp(context, 3), 0);
        btn.setLayoutParams(lp);
        btn.setPadding(0, 0, 0, 0);
        return btn;
    }

    private void renderFallback(LinearLayout container, LinearLayout parent) {
        TextView note = UiKit.text(context, "Örnek ürünler gösteriliyor", 12, UiKit.MUTED, Typeface.NORMAL);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, 0, 0, UiKit.dp(context, 12));
        container.addView(note);

        for (int i = 0; i < FALLBACK.length; i += 2) {
            LinearLayout row = UiKit.horizontal(context);
            row.addView(fallbackCard(FALLBACK[i]));
            if (i + 1 < FALLBACK.length) {
                row.addView(fallbackCard(FALLBACK[i + 1]));
            }
            container.addView(row);
        }
    }

    private LinearLayout fallbackCard(String[] data) {
        LinearLayout card = UiKit.vertical(context);
        card.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 12, 1));
        card.setPadding(UiKit.dp(context, 12), UiKit.dp(context, 14), UiKit.dp(context, 12), UiKit.dp(context, 14));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        cp.setMargins(UiKit.dp(context, 4), 0, UiKit.dp(context, 4), UiKit.dp(context, 10));
        card.setLayoutParams(cp);

        String emoji = UiKit.categoryEmoji(data[1]);
        TextView ev = UiKit.text(context, emoji, 36, UiKit.ACCENT, Typeface.NORMAL);
        ev.setGravity(Gravity.CENTER);
        ev.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));
        card.addView(ev);
        card.addView(UiKit.badge(context, data[1], UiKit.ACCENT));
        card.addView(UiKit.text(context, data[0], 15, UiKit.TEXT, Typeface.BOLD));
        card.addView(UiKit.text(context, "₺" + data[2] + " / kg", 15, UiKit.ORANGE, Typeface.BOLD));
        card.addView(UiKit.text(context, data[3] + " adet stokta", 11, UiKit.ACCENT, Typeface.NORMAL));

        return card;
    }

    // ======= PRODUCT DETAIL =======
    public void showProductDetail(LinearLayout parent, String productId) {
        parent.removeAllViews();

        Button backBtn = UiKit.secondaryButton(context, "‹ Ürünlere Dön");
        backBtn.setOnClickListener(v -> nav.showProducts());
        parent.addView(backBtn);

        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        parent.addView(loading);

        apiClient.get("/products/" + productId, null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                parent.removeView(loading);
                try {
                    JSONObject product = new JSONObject(body);
                    renderProductDetail(parent, product, productId);
                } catch (Exception e) {
                    parent.addView(UiKit.text(context, "Ürün bilgileri yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                parent.removeView(loading);
                parent.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private void renderProductDetail(LinearLayout parent, JSONObject product, String productId) {
        String name = product.optString("name", "Ürün");
        String description = product.optString("description", "");
        String category = product.optString("category", "");
        double price = product.optDouble("price", 0);
        int stock = product.optInt("stock", 0);
        String emoji = UiKit.categoryEmoji(category);

        // Emoji hero
        LinearLayout emojiCard = UiKit.card(context);
        emojiCard.setGravity(Gravity.CENTER);
        emojiCard.setBackground(UiKit.rounded(context, UiKit.PANEL_SOFT, UiKit.LINE, 14, 1));
        emojiCard.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 30), UiKit.dp(context, 20), UiKit.dp(context, 30));
        TextView emojiView = UiKit.text(context, emoji, 72, UiKit.ACCENT, Typeface.NORMAL);
        emojiView.setGravity(Gravity.CENTER);
        emojiCard.addView(emojiView);
        parent.addView(emojiCard);

        // Info card
        LinearLayout infoCard = UiKit.card(context);

        if (!category.isEmpty()) {
            infoCard.addView(UiKit.badge(context, category, UiKit.ACCENT));
        }

        TextView nameView = UiKit.text(context, name, 26, UiKit.TEXT, Typeface.BOLD);
        nameView.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 4));
        infoCard.addView(nameView);

        TextView priceView = UiKit.text(context, FeatureSupport.formatPrice(price), 24, UiKit.ORANGE, Typeface.BOLD);
        priceView.setPadding(0, 0, 0, UiKit.dp(context, 12));
        infoCard.addView(priceView);

        if (!description.isEmpty()) {
            TextView descView = UiKit.text(context, description, 14, UiKit.MUTED, Typeface.NORMAL);
            descView.setPadding(0, 0, 0, UiKit.dp(context, 12));
            infoCard.addView(descView);
        }

        // Stock badge
        String stockText = stock > 0 ? "✓ " + stock + " adet stokta" : "✗ Stok tükendi";
        int stockBadgeColor = stock > 0 ? UiKit.ACCENT : UiKit.DANGER;
        infoCard.addView(UiKit.badge(context, stockText, stockBadgeColor));

        parent.addView(infoCard);

        // Add to cart section
        if (stock > 0) {
            LinearLayout cartCard = UiKit.card(context);
            cartCard.addView(UiKit.text(context, "Sepete Ekle", 18, UiKit.TEXT, Typeface.BOLD));

            // Quantity
            final int[] qty = {1};
            LinearLayout qtyRow = UiKit.horizontal(context);
            qtyRow.setGravity(Gravity.CENTER_VERTICAL);
            qtyRow.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 12));

            Button minus = UiKit.secondaryButton(context, "−");
            LinearLayout.LayoutParams mbp = new LinearLayout.LayoutParams(UiKit.dp(context, 48), UiKit.dp(context, 48));
            minus.setLayoutParams(mbp);
            minus.setTextSize(20);

            TextView qtyText = UiKit.text(context, "1", 20, UiKit.TEXT, Typeface.BOLD);
            qtyText.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams qtp = new LinearLayout.LayoutParams(UiKit.dp(context, 60), LinearLayout.LayoutParams.WRAP_CONTENT);
            qtyText.setLayoutParams(qtp);

            Button plus = UiKit.secondaryButton(context, "+");
            LinearLayout.LayoutParams pbp = new LinearLayout.LayoutParams(UiKit.dp(context, 48), UiKit.dp(context, 48));
            plus.setLayoutParams(pbp);
            plus.setTextSize(20);

            minus.setOnClickListener(v -> {
                if (qty[0] > 1) { qty[0]--; qtyText.setText(String.valueOf(qty[0])); }
            });
            plus.setOnClickListener(v -> {
                if (qty[0] < stock) { qty[0]++; qtyText.setText(String.valueOf(qty[0])); }
            });

            qtyRow.addView(minus);
            qtyRow.addView(qtyText);
            qtyRow.addView(plus);
            cartCard.addView(qtyRow);

            Button addToCart = UiKit.orangeButton(context, "🛒 Sepete Ekle");
            addToCart.setOnClickListener(v -> {
                if (!sessionStore.isLoggedIn()) {
                    Toast.makeText(context, "Sepete eklemek için giriş yapın.", Toast.LENGTH_SHORT).show();
                    nav.showLogin();
                    return;
                }
                addToCart.setEnabled(false);
                addToCart.setText("Ekleniyor...");
                try {
                    JSONObject body = new JSONObject();
                    body.put("productId", productId);
                    body.put("quantity", qty[0]);
                    apiClient.post("/cart/items", body, sessionStore.getToken(), new ApiClient.Callback() {
                        @Override
                        public void onSuccess(String b) {
                            addToCart.setEnabled(true);
                            addToCart.setText("🛒 Sepete Ekle");
                            Toast.makeText(context, "✓ Ürün sepete eklendi!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String m) {
                            addToCart.setEnabled(true);
                            addToCart.setText("🛒 Sepete Ekle");
                            Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    addToCart.setEnabled(true);
                    addToCart.setText("🛒 Sepete Ekle");
                }
            });
            cartCard.addView(addToCart);
            parent.addView(cartCard);
        }

        // Reviews section
        renderReviewSection(parent, productId);
    }

    private void renderReviewSection(LinearLayout parent, String productId) {
        // Submit review
        LinearLayout reviewCard = UiKit.card(context);
        reviewCard.addView(UiKit.text(context, "Değerlendirme Yap", 18, UiKit.TEXT, Typeface.BOLD));

        // Star rating
        final int[] selectedRating = {0};
        LinearLayout stars = UiKit.horizontal(context);
        stars.setGravity(Gravity.CENTER);
        stars.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 12));

        TextView[] starViews = new TextView[5];
        for (int i = 0; i < 5; i++) {
            int starIdx = i;
            starViews[i] = UiKit.text(context, "☆", 32, UiKit.ORANGE, Typeface.NORMAL);
            starViews[i].setPadding(UiKit.dp(context, 4), 0, UiKit.dp(context, 4), 0);
            starViews[i].setOnClickListener(v -> {
                selectedRating[0] = starIdx + 1;
                for (int j = 0; j < 5; j++) {
                    starViews[j].setText(j <= starIdx ? "★" : "☆");
                }
            });
            stars.addView(starViews[i]);
        }
        reviewCard.addView(stars);

        EditText comment = UiKit.multiLineField(context, "Yorumunuzu yazın...", "");
        reviewCard.addView(comment);

        Button submitReview = UiKit.button(context, "Değerlendirmeyi Gönder");
        submitReview.setOnClickListener(v -> {
            if (!sessionStore.isLoggedIn()) {
                Toast.makeText(context, "Değerlendirme yapmak için giriş yapın.", Toast.LENGTH_SHORT).show();
                nav.showLogin();
                return;
            }
            if (selectedRating[0] == 0) {
                Toast.makeText(context, "Lütfen bir puan seçin.", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReview.setEnabled(false);
            try {
                JSONObject body = new JSONObject();
                body.put("rating", selectedRating[0]);
                body.put("comment", comment.getText().toString().trim());
                apiClient.post("/products/" + productId + "/reviews", body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        submitReview.setEnabled(true);
                        Toast.makeText(context, "Değerlendirmeniz gönderildi!", Toast.LENGTH_SHORT).show();
                        comment.setText("");
                        selectedRating[0] = 0;
                        for (TextView sv : starViews) sv.setText("☆");
                        // Refresh reviews
                        loadReviews(parent, productId);
                    }
                    @Override
                    public void onError(String m) {
                        submitReview.setEnabled(true);
                        Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                submitReview.setEnabled(true);
            }
        });
        reviewCard.addView(submitReview);
        parent.addView(reviewCard);

        // Reviews list container
        LinearLayout reviewsList = UiKit.vertical(context);
        reviewsList.setTag("reviews_list_" + productId);
        parent.addView(reviewsList);

        loadReviews(parent, productId);
    }

    private void loadReviews(LinearLayout parent, String productId) {
        LinearLayout reviewsList = parent.findViewWithTag("reviews_list_" + productId);
        if (reviewsList == null) return;
        reviewsList.removeAllViews();

        reviewsList.addView(UiKit.text(context, "Değerlendirmeler", 18, UiKit.TEXT, Typeface.BOLD));

        apiClient.get("/products/" + productId + "/reviews", null, new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        TextView empty = UiKit.text(context, "Henüz değerlendirme yapılmamış.", 14, UiKit.MUTED, Typeface.NORMAL);
                        empty.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 12));
                        reviewsList.addView(empty);
                        return;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject review = data.getJSONObject(i);
                        reviewsList.addView(reviewCard(review));
                    }
                } catch (Exception e) {
                    reviewsList.addView(UiKit.text(context, "Değerlendirmeler yüklenemedi.", 13, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                // Silently fail for reviews
            }
        });
    }

    private LinearLayout reviewCard(JSONObject review) {
        LinearLayout card = UiKit.card(context);
        String userName = review.optString("userName", "Anonim");
        int rating = review.optInt("rating", 0);
        String commentText = review.optString("comment", "");
        String date = FeatureSupport.formatDate(review.optString("createdAt", ""));

        LinearLayout header = UiKit.horizontal(context);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView avatar = UiKit.iconCircle(context, userName.substring(0, 1).toUpperCase(), 36, UiKit.PANEL_SOFT);
        avatar.setTextColor(UiKit.ACCENT);
        avatar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        avatar.setTextSize(14);
        header.addView(avatar);

        LinearLayout userInfo = UiKit.vertical(context);
        userInfo.setPadding(UiKit.dp(context, 10), 0, 0, 0);
        userInfo.addView(UiKit.text(context, userName, 14, UiKit.TEXT, Typeface.BOLD));

        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            starStr.append(i < rating ? "★" : "☆");
        }
        userInfo.addView(UiKit.text(context, starStr.toString(), 14, UiKit.ORANGE, Typeface.NORMAL));
        header.addView(userInfo);

        if (!date.isEmpty()) {
            LinearLayout.LayoutParams dp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            userInfo.setLayoutParams(dp2);
            header.addView(UiKit.text(context, date, 11, UiKit.MUTED, Typeface.NORMAL));
        }

        card.addView(header);

        if (!commentText.isEmpty()) {
            TextView comment = UiKit.text(context, commentText, 14, UiKit.MUTED, Typeface.NORMAL);
            comment.setPadding(0, UiKit.dp(context, 8), 0, 0);
            card.addView(comment);
        }

        return card;
    }

    // ======= PRODUCT MANAGEMENT =======
    public void showProductManagement(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) {
            parent.addView(loginPrompt());
            return;
        }

        TextView heading = UiKit.text(context, "Ürün Yönetimi", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        // Tabs
        LinearLayout tabs = UiKit.horizontal(context);
        LinearLayout.LayoutParams tabsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabsParams.setMargins(0, 0, 0, UiKit.dp(context, 16));
        tabs.setLayoutParams(tabsParams);

        Button myProductsTab = tabButton("Ürünlerim", !managementAddTab);
        Button addProductTab = tabButton("Yeni Ürün Ekle", managementAddTab);

        LinearLayout tabContent = UiKit.vertical(context);

        myProductsTab.setOnClickListener(v -> {
            managementAddTab = false;
            updateTabStyles(myProductsTab, addProductTab);
            showMyProducts(tabContent);
        });
        addProductTab.setOnClickListener(v -> {
            managementAddTab = true;
            updateTabStyles(myProductsTab, addProductTab);
            showAddProduct(tabContent);
        });

        tabs.addView(myProductsTab);
        tabs.addView(addProductTab);
        parent.addView(tabs);
        parent.addView(tabContent);

        if (managementAddTab) {
            showAddProduct(tabContent);
        } else {
            showMyProducts(tabContent);
        }
    }

    private Button tabButton(String label, boolean active) {
        Button btn = new Button(context);
        btn.setText(label);
        btn.setTextSize(14);
        btn.setAllCaps(false);
        btn.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
        btn.setTextColor(active ? UiKit.BG : UiKit.TEXT);
        btn.setBackground(UiKit.rounded(context, active ? UiKit.ACCENT : UiKit.PANEL, active ? UiKit.ACCENT : UiKit.LINE, 10, active ? 0 : 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 44), 1);
        lp.setMargins(UiKit.dp(context, 3), 0, UiKit.dp(context, 3), 0);
        btn.setLayoutParams(lp);
        return btn;
    }

    private void updateTabStyles(Button tab1, Button tab2) {
        boolean tab1Active = !managementAddTab;
        tab1.setTypeface(Typeface.DEFAULT, tab1Active ? Typeface.BOLD : Typeface.NORMAL);
        tab1.setTextColor(tab1Active ? UiKit.BG : UiKit.TEXT);
        tab1.setBackground(UiKit.rounded(context, tab1Active ? UiKit.ACCENT : UiKit.PANEL, tab1Active ? UiKit.ACCENT : UiKit.LINE, 10, tab1Active ? 0 : 1));

        tab2.setTypeface(Typeface.DEFAULT, !tab1Active ? Typeface.BOLD : Typeface.NORMAL);
        tab2.setTextColor(!tab1Active ? UiKit.BG : UiKit.TEXT);
        tab2.setBackground(UiKit.rounded(context, !tab1Active ? UiKit.ACCENT : UiKit.PANEL, !tab1Active ? UiKit.ACCENT : UiKit.LINE, 10, !tab1Active ? 0 : 1));
    }

    private void showMyProducts(LinearLayout container) {
        container.removeAllViews();
        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        container.addView(loading);

        apiClient.get("/products?producerId=" + sessionStore.getUserId() + "&limit=20", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                container.removeAllViews();
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        LinearLayout empty = UiKit.card(context);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 30), UiKit.dp(context, 20), UiKit.dp(context, 30));
                        empty.addView(UiKit.text(context, "📦", 48, UiKit.MUTED, Typeface.NORMAL));
                        TextView emptyMsg = UiKit.text(context, "Henüz ürün eklenmemiş", 16, UiKit.TEXT, Typeface.BOLD);
                        emptyMsg.setGravity(Gravity.CENTER);
                        emptyMsg.setPadding(0, UiKit.dp(context, 12), 0, 0);
                        empty.addView(emptyMsg);
                        container.addView(empty);
                        return;
                    }
                    container.addView(UiKit.text(context, data.length() + " ürün", 13, UiKit.MUTED, Typeface.NORMAL));
                    for (int i = 0; i < data.length(); i++) {
                        container.addView(managementProductCard(container, data.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    container.addView(UiKit.text(context, "Ürünler yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
                }
            }
            @Override
            public void onError(String m) {
                container.removeAllViews();
                container.addView(UiKit.text(context, FeatureSupport.parseError(m), 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private LinearLayout managementProductCard(LinearLayout container, JSONObject product) {
        LinearLayout card = UiKit.card(context);
        String name = product.optString("name", "");
        String category = product.optString("category", "");
        double price = product.optDouble("price", 0);
        int stock = product.optInt("stock", 0);
        String pid = product.optString("id", "");
        String emoji = UiKit.categoryEmoji(category);

        LinearLayout header = UiKit.horizontal(context);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView ev = UiKit.text(context, emoji, 28, UiKit.ACCENT, Typeface.NORMAL);
        ev.setPadding(0, 0, UiKit.dp(context, 10), 0);
        header.addView(ev);

        LinearLayout info = UiKit.vertical(context);
        info.addView(UiKit.text(context, name, 16, UiKit.TEXT, Typeface.BOLD));

        LinearLayout meta = UiKit.horizontal(context);
        if (!category.isEmpty()) {
            TextView catBadge = UiKit.badge(context, category, UiKit.ACCENT);
            LinearLayout.LayoutParams cbp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cbp.setMargins(0, 0, UiKit.dp(context, 8), 0);
            catBadge.setLayoutParams(cbp);
            meta.addView(catBadge);
        }
        meta.addView(UiKit.text(context, FeatureSupport.formatPrice(price), 14, UiKit.ORANGE, Typeface.BOLD));
        info.addView(meta);

        info.addView(UiKit.text(context, "Stok: " + stock, 12, UiKit.MUTED, Typeface.NORMAL));

        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        info.setLayoutParams(ip);
        header.addView(info);
        card.addView(header);

        // Edit/delete container
        LinearLayout editContainer = UiKit.vertical(context);
        editContainer.setTag("edit_" + pid);
        card.addView(editContainer);

        // Buttons
        LinearLayout btnRow = UiKit.horizontal(context);
        Button editBtn = UiKit.secondaryButton(context, "Düzenle");
        Button deleteBtn = UiKit.dangerButton(context, "Sil");

        editBtn.setOnClickListener(v -> showEditForm(editContainer, container, product));
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Ürün Silme")
                    .setMessage("\"" + name + "\" ürününü silmek istediğinize emin misiniz?")
                    .setPositiveButton("Sil", (d, w) -> {
                        apiClient.delete("/products/" + pid, sessionStore.getToken(), new ApiClient.Callback() {
                            @Override
                            public void onSuccess(String b) {
                                Toast.makeText(context, "Ürün silindi.", Toast.LENGTH_SHORT).show();
                                showMyProducts(container);
                            }
                            @Override
                            public void onError(String m) {
                                Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Vazgeç", null)
                    .show();
        });

        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 40), 1);
        hp.setMargins(0, UiKit.dp(context, 8), UiKit.dp(context, 5), 0);
        editBtn.setLayoutParams(hp);
        LinearLayout.LayoutParams hp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 40), 1);
        hp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 8), UiKit.dp(context, 5), 0);
        deleteBtn.setLayoutParams(hp2);

        Button stockBtn = UiKit.orangeButton(context, "Stok");
        LinearLayout.LayoutParams hp3 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 40), 1);
        hp3.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 8), 0, 0);
        stockBtn.setLayoutParams(hp3);
        stockBtn.setTextSize(13);
        stockBtn.setOnClickListener(v -> showStockForm(editContainer, container, pid, name, stock));

        btnRow.addView(editBtn);
        btnRow.addView(deleteBtn);
        btnRow.addView(stockBtn);
        card.addView(btnRow);

        return card;
    }

    private void showStockForm(LinearLayout editContainer, LinearLayout listContainer, String productId, String productName, int currentStock) {
        editContainer.removeAllViews();

        LinearLayout form = UiKit.vertical(editContainer.getContext());
        form.setBackground(UiKit.rounded(context, UiKit.PANEL_SOFT, UiKit.LINE, 10, 1));
        form.setPadding(UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14));
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(0, UiKit.dp(context, 12), 0, 0);
        form.setLayoutParams(fp);

        form.addView(UiKit.text(context, "📦 Stok Güncelle", 16, UiKit.TEXT, Typeface.BOLD));
        TextView subtitle = UiKit.text(context, productName, 13, UiKit.MUTED, Typeface.NORMAL);
        subtitle.setPadding(0, UiKit.dp(context, 2), 0, UiKit.dp(context, 12));
        form.addView(subtitle);

        // Current stock info
        form.addView(UiKit.text(context, "Mevcut stok: " + currentStock + " adet", 14, UiKit.ORANGE, Typeface.BOLD));

        // New stock input
        TextView label = UiKit.text(context, "Yeni stok miktarı:", 13, UiKit.MUTED, Typeface.NORMAL);
        label.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 4));
        form.addView(label);

        final int[] newStock = {currentStock};
        LinearLayout qtyRow = UiKit.horizontal(context);
        qtyRow.setGravity(Gravity.CENTER_VERTICAL);
        qtyRow.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 12));

        Button minus = UiKit.secondaryButton(context, "−");
        LinearLayout.LayoutParams mbp = new LinearLayout.LayoutParams(UiKit.dp(context, 48), UiKit.dp(context, 48));
        minus.setLayoutParams(mbp);
        minus.setTextSize(20);

        TextView qtyText = UiKit.text(context, String.valueOf(currentStock), 22, UiKit.TEXT, Typeface.BOLD);
        qtyText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams qtp = new LinearLayout.LayoutParams(UiKit.dp(context, 80), LinearLayout.LayoutParams.WRAP_CONTENT);
        qtyText.setLayoutParams(qtp);

        Button plus = UiKit.secondaryButton(context, "+");
        LinearLayout.LayoutParams pbp = new LinearLayout.LayoutParams(UiKit.dp(context, 48), UiKit.dp(context, 48));
        plus.setLayoutParams(pbp);
        plus.setTextSize(20);

        minus.setOnClickListener(v -> {
            if (newStock[0] > 0) { newStock[0] -= 10; if (newStock[0] < 0) newStock[0] = 0; qtyText.setText(String.valueOf(newStock[0])); }
        });
        plus.setOnClickListener(v -> {
            newStock[0] += 10; qtyText.setText(String.valueOf(newStock[0]));
        });

        qtyRow.addView(minus);
        qtyRow.addView(qtyText);
        qtyRow.addView(plus);
        form.addView(qtyRow);

        // Manual input
        EditText manualInput = UiKit.numberField(context, "veya miktar girin", "");
        form.addView(manualInput);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        form.addView(errorText);

        LinearLayout btnRow = UiKit.horizontal(context);
        Button saveBtn = UiKit.button(context, "Stok Güncelle");
        Button cancelBtn = UiKit.secondaryButton(context, "İptal");

        saveBtn.setOnClickListener(v -> {
            int finalStock = newStock[0];
            String manualVal = manualInput.getText().toString().trim();
            if (!manualVal.isEmpty()) {
                try { finalStock = Integer.parseInt(manualVal); } catch (Exception e) {
                    errorText.setText("Geçerli bir sayı girin.");
                    return;
                }
            }
            if (finalStock < 0) {
                errorText.setText("Stok negatif olamaz.");
                return;
            }
            saveBtn.setEnabled(false);
            saveBtn.setText("Güncelleniyor...");
            try {
                JSONObject body = new JSONObject();
                body.put("stock", finalStock);
                apiClient.patch("/products/" + productId + "/stock", body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        Toast.makeText(context, "✓ Stok güncellendi!", Toast.LENGTH_SHORT).show();
                        showMyProducts(listContainer);
                    }
                    @Override
                    public void onError(String m) {
                        saveBtn.setEnabled(true);
                        saveBtn.setText("Stok Güncelle");
                        errorText.setText(FeatureSupport.parseError(m));
                    }
                });
            } catch (Exception e) {
                saveBtn.setEnabled(true);
                saveBtn.setText("Stok Güncelle");
                errorText.setText("Bir hata oluştu.");
            }
        });
        cancelBtn.setOnClickListener(v -> editContainer.removeAllViews());

        LinearLayout.LayoutParams shp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        shp.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        saveBtn.setLayoutParams(shp);
        LinearLayout.LayoutParams shp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        shp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        cancelBtn.setLayoutParams(shp2);
        btnRow.addView(saveBtn);
        btnRow.addView(cancelBtn);
        form.addView(btnRow);

        editContainer.addView(form);
    }

    private void showEditForm(LinearLayout editContainer, LinearLayout listContainer, JSONObject product) {
        editContainer.removeAllViews();

        LinearLayout form = UiKit.vertical(context);
        form.setBackground(UiKit.rounded(context, UiKit.PANEL_SOFT, UiKit.LINE, 10, 1));
        form.setPadding(UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14));
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(0, UiKit.dp(context, 12), 0, 0);
        form.setLayoutParams(fp);

        form.addView(UiKit.text(context, "Ürün Düzenle", 16, UiKit.TEXT, Typeface.BOLD));

        EditText nameField = UiKit.field(context, "Ürün Adı", product.optString("name", ""));
        EditText descField = UiKit.multiLineField(context, "Açıklama", product.optString("description", ""));
        EditText priceField = UiKit.numberField(context, "Fiyat (₺)", String.valueOf(product.optDouble("price", 0)));
        EditText stockField = UiKit.numberField(context, "Stok", String.valueOf(product.optInt("stock", 0)));

        String[] cats = {"Bakliyat", "Tahıl", "Sebze", "Meyve", "Süt Ürünleri", "Kuruyemiş", "Diğer"};
        Spinner catSpinner = UiKit.createSpinner(context, cats);
        String curCat = product.optString("category", "");
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].equals(curCat)) { catSpinner.setSelection(i); break; }
        }

        form.addView(nameField);
        form.addView(descField);
        form.addView(catSpinner);
        form.addView(priceField);
        form.addView(stockField);

        LinearLayout btnRow = UiKit.horizontal(context);
        Button saveBtn = UiKit.button(context, "Kaydet");
        Button cancelBtn = UiKit.secondaryButton(context, "İptal");

        saveBtn.setOnClickListener(v -> {
            try {
                JSONObject body = new JSONObject();
                body.put("name", nameField.getText().toString().trim());
                body.put("description", descField.getText().toString().trim());
                body.put("category", catSpinner.getSelectedItem().toString());
                body.put("price", Double.parseDouble(priceField.getText().toString().trim()));
                body.put("stock", Integer.parseInt(stockField.getText().toString().trim()));

                saveBtn.setEnabled(false);
                apiClient.put("/products/" + product.optString("id"), body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        Toast.makeText(context, "Ürün güncellendi.", Toast.LENGTH_SHORT).show();
                        showMyProducts(listContainer);
                    }
                    @Override
                    public void onError(String m) {
                        saveBtn.setEnabled(true);
                        Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Geçersiz değerler girdiniz.", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(v -> editContainer.removeAllViews());

        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        saveBtn.setLayoutParams(hp);
        LinearLayout.LayoutParams hp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        cancelBtn.setLayoutParams(hp2);
        btnRow.addView(saveBtn);
        btnRow.addView(cancelBtn);
        form.addView(btnRow);

        editContainer.addView(form);
    }

    private void showAddProduct(LinearLayout container) {
        container.removeAllViews();

        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.text(context, "Yeni Ürün Ekle", 20, UiKit.TEXT, Typeface.BOLD));
        TextView sub = UiKit.text(context, "Ürün bilgilerini girin", 13, UiKit.MUTED, Typeface.NORMAL);
        sub.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 16));
        card.addView(sub);

        EditText nameField = UiKit.field(context, "Ürün Adı", "");
        EditText descField = UiKit.multiLineField(context, "Açıklama", "");

        String[] cats = {"Bakliyat", "Tahıl", "Sebze", "Meyve", "Süt Ürünleri", "Kuruyemiş", "Diğer"};
        Spinner catSpinner = UiKit.createSpinner(context, cats);

        EditText priceField = UiKit.numberField(context, "Fiyat (₺)", "");
        EditText unitField = UiKit.field(context, "Birim (kg, adet, litre)", "kg");
        EditText stockField = UiKit.numberField(context, "Stok Miktarı", "");

        card.addView(nameField);
        card.addView(descField);
        card.addView(catSpinner);
        card.addView(priceField);
        card.addView(unitField);
        card.addView(stockField);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        card.addView(errorText);

        Button addBtn = UiKit.button(context, "Ürün Ekle");
        addBtn.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String priceStr = priceField.getText().toString().trim();
            String stockStr = stockField.getText().toString().trim();

            if (name.isEmpty()) { errorText.setText("Ürün adını girin."); return; }
            if (priceStr.isEmpty()) { errorText.setText("Fiyat girin."); return; }
            if (stockStr.isEmpty()) { errorText.setText("Stok miktarını girin."); return; }

            try {
                JSONObject body = new JSONObject();
                body.put("name", name);
                body.put("description", descField.getText().toString().trim());
                body.put("category", catSpinner.getSelectedItem().toString());
                body.put("price", Double.parseDouble(priceStr));
                body.put("stock", Integer.parseInt(stockStr));
                body.put("unit", unitField.getText().toString().trim());
                body.put("producerId", sessionStore.getUserId());

                addBtn.setEnabled(false);
                addBtn.setText("Ekleniyor...");
                errorText.setText("");

                apiClient.post("/products", body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        addBtn.setEnabled(true);
                        addBtn.setText("Ürün Ekle");
                        Toast.makeText(context, "Ürün başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                        managementAddTab = false;
                        showMyProducts(container);
                    }
                    @Override
                    public void onError(String m) {
                        addBtn.setEnabled(true);
                        addBtn.setText("Ürün Ekle");
                        errorText.setText(FeatureSupport.parseError(m));
                    }
                });
            } catch (Exception e) {
                errorText.setText("Geçersiz değerler girdiniz.");
            }
        });
        card.addView(addBtn);

        container.addView(card);
    }

    private LinearLayout loginPrompt() {
        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 40), UiKit.dp(context, 20), UiKit.dp(context, 40));

        TextView icon = UiKit.text(context, "🔒", 48, UiKit.MUTED, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        card.addView(icon);

        TextView msg = UiKit.text(context, "Ürün yönetimi için giriş yapın.", 16, UiKit.TEXT, Typeface.BOLD);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, UiKit.dp(context, 16), 0, UiKit.dp(context, 20));
        card.addView(msg);

        Button loginBtn = UiKit.button(context, "Giriş Yap");
        loginBtn.setOnClickListener(v -> nav.showLogin());
        card.addView(loginBtn);

        return card;
    }
}
