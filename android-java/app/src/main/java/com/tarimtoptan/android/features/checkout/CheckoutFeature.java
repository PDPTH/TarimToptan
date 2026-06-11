package com.tarimtoptan.android.features.checkout;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
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

public class CheckoutFeature {
    private final Context context;
    private final ApiClient apiClient;
    private final SessionStore sessionStore;
    private final MainActivity.NavigationCallback nav;

    private int checkoutStep = 0;
    private String selectedAddressId = "";
    private JSONObject selectedAddressObj = null;
    private JSONArray cartItems = null;
    private double cartTotal = 0;
    private String paymentMethod = "credit_card";

    public CheckoutFeature(Context context, ApiClient apiClient, SessionStore sessionStore, MainActivity.NavigationCallback nav) {
        this.context = context;
        this.apiClient = apiClient;
        this.sessionStore = sessionStore;
        this.nav = nav;
    }

    // ======= CART =======
    public void showCart(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) {
            showLoginPrompt(parent, "Sepetinizi görüntülemek için giriş yapın.");
            return;
        }

        TextView heading = UiKit.text(context, "Sepetim", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        parent.addView(loading);

        apiClient.get("/cart", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                parent.removeView(loading);
                try {
                    JSONObject cart = new JSONObject(body);
                    JSONArray items = cart.optJSONArray("items");
                    if (items == null || items.length() == 0) {
                        showEmptyCart(parent);
                        return;
                    }
                    renderCartItems(parent, items, cart.optDouble("totalPrice", 0));
                } catch (Exception e) {
                    showEmptyCart(parent);
                }
            }
            @Override
            public void onError(String m) {
                parent.removeView(loading);
                showEmptyCart(parent);
            }
        });
    }

    private void showEmptyCart(LinearLayout parent) {
        LinearLayout emptyCard = UiKit.card(context);
        emptyCard.setGravity(Gravity.CENTER);
        emptyCard.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 40), UiKit.dp(context, 20), UiKit.dp(context, 40));

        TextView emoji = UiKit.text(context, "🛒", 56, UiKit.MUTED, Typeface.NORMAL);
        emoji.setGravity(Gravity.CENTER);
        emptyCard.addView(emoji);

        TextView msg = UiKit.text(context, "Sepetiniz boş", 20, UiKit.TEXT, Typeface.BOLD);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 8));
        emptyCard.addView(msg);

        TextView desc = UiKit.text(context, "Harika ürünlerimizi keşfetmeye başlayın!", 14, UiKit.MUTED, Typeface.NORMAL);
        desc.setGravity(Gravity.CENTER);
        desc.setPadding(0, 0, 0, UiKit.dp(context, 20));
        emptyCard.addView(desc);

        Button browseBtn = UiKit.button(context, "Ürünleri Keşfet");
        browseBtn.setOnClickListener(v -> nav.showProducts());
        emptyCard.addView(browseBtn);

        parent.addView(emptyCard);
    }

    private void renderCartItems(LinearLayout parent, JSONArray items, double totalPrice) {
        TextView countText = UiKit.text(context, items.length() + " ürün", 13, UiKit.MUTED, Typeface.NORMAL);
        countText.setPadding(0, 0, 0, UiKit.dp(context, 8));
        parent.addView(countText);

        double subtotal = 0;
        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.getJSONObject(i);
                parent.addView(cartItemCard(parent, item));
                subtotal += item.optDouble("price", 0) * item.optInt("quantity", 1);
            } catch (Exception ignored) {}
        }

        // Summary
        parent.addView(UiKit.divider(context));

        LinearLayout summary = UiKit.card(context);
        summary.addView(UiKit.text(context, "Sipariş Özeti", 18, UiKit.TEXT, Typeface.BOLD));
        summary.addView(UiKit.divider(context));

        summary.addView(summaryRow("Ara Toplam", FeatureSupport.formatPrice(subtotal)));

        double shipping = subtotal >= 500 ? 0 : 29.90;
        String shippingText = shipping == 0 ? "Ücretsiz" : FeatureSupport.formatPrice(shipping);
        summary.addView(summaryRow("Kargo", shippingText));

        if (subtotal < 500 && subtotal > 0) {
            TextView freeNote = UiKit.text(context, "₺500 ve üzeri alışverişlerde kargo ücretsiz!", 12, UiKit.ACCENT, Typeface.NORMAL);
            freeNote.setPadding(0, 0, 0, UiKit.dp(context, 8));
            summary.addView(freeNote);
        }

        summary.addView(UiKit.divider(context));

        double total = subtotal + shipping;
        LinearLayout totalRow = UiKit.horizontal(context);
        totalRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView totalLabel = UiKit.text(context, "Toplam", 18, UiKit.TEXT, Typeface.BOLD);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        totalLabel.setLayoutParams(tp);
        totalRow.addView(totalLabel);
        totalRow.addView(UiKit.text(context, FeatureSupport.formatPrice(total), 22, UiKit.ORANGE, Typeface.BOLD));
        summary.addView(totalRow);

        parent.addView(summary);

        Button checkoutBtn = UiKit.button(context, "Siparişi Tamamla");
        checkoutBtn.setOnClickListener(v -> {
            checkoutStep = 0;
            showCheckout(parent);
        });
        parent.addView(checkoutBtn);
    }

    private LinearLayout cartItemCard(LinearLayout parentContainer, JSONObject item) {
        LinearLayout card = UiKit.card(context);
        String name = item.optString("name", "Ürün");
        double price = item.optDouble("price", 0);
        int quantity = item.optInt("quantity", 1);
        String itemId = item.optString("id", "");

        TextView nameView = UiKit.text(context, name, 16, UiKit.TEXT, Typeface.BOLD);
        card.addView(nameView);

        LinearLayout detailRow = UiKit.horizontal(context);
        detailRow.setGravity(Gravity.CENTER_VERTICAL);
        detailRow.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));

        TextView priceView = UiKit.text(context, FeatureSupport.formatPrice(price), 15, UiKit.ORANGE, Typeface.BOLD);
        detailRow.addView(priceView);

        TextView x = UiKit.text(context, "  ×  " + quantity, 15, UiKit.MUTED, Typeface.NORMAL);
        detailRow.addView(x);

        LinearLayout.LayoutParams spacer = new LinearLayout.LayoutParams(0, 1, 1);
        TextView sp = new TextView(context);
        sp.setLayoutParams(spacer);
        detailRow.addView(sp);

        TextView subtotal = UiKit.text(context, FeatureSupport.formatPrice(price * quantity), 16, UiKit.TEXT, Typeface.BOLD);
        detailRow.addView(subtotal);

        card.addView(detailRow);

        Button removeBtn = UiKit.dangerButton(context, "Kaldır");
        LinearLayout.LayoutParams rbp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, UiKit.dp(context, 40));
        rbp.setMargins(0, UiKit.dp(context, 4), 0, 0);
        removeBtn.setLayoutParams(rbp);
        removeBtn.setTextSize(13);
        removeBtn.setOnClickListener(v -> {
            removeBtn.setEnabled(false);
            apiClient.delete("/cart/items/" + itemId, sessionStore.getToken(), new ApiClient.Callback() {
                @Override
                public void onSuccess(String b) {
                    Toast.makeText(context, "Ürün sepetten kaldırıldı.", Toast.LENGTH_SHORT).show();
                    showCart(parentContainer);
                }
                @Override
                public void onError(String m) {
                    removeBtn.setEnabled(true);
                    Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                }
            });
        });
        card.addView(removeBtn);

        return card;
    }

    private LinearLayout summaryRow(String label, String value) {
        LinearLayout row = UiKit.horizontal(context);
        row.setPadding(0, UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        TextView lbl = UiKit.text(context, label, 15, UiKit.MUTED, Typeface.NORMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lbl.setLayoutParams(lp);
        row.addView(lbl);
        boolean isFree = "Ücretsiz".equals(value);
        row.addView(UiKit.text(context, value, 15, isFree ? UiKit.ACCENT : UiKit.TEXT, Typeface.BOLD));
        return row;
    }

    // ======= ORDERS =======
    public void showOrders(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) {
            showLoginPrompt(parent, "Siparişlerinizi görüntülemek için giriş yapın.");
            return;
        }

        TextView heading = UiKit.text(context, "Siparişlerim", 26, UiKit.TEXT, Typeface.BOLD);
        heading.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 16));
        parent.addView(heading);

        TextView loading = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        loading.setGravity(Gravity.CENTER);
        parent.addView(loading);

        apiClient.get("/orders", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                parent.removeView(loading);
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data == null || data.length() == 0) {
                        showEmptyOrders(parent);
                        return;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        parent.addView(orderCard(parent, data.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    showEmptyOrders(parent);
                }
            }
            @Override
            public void onError(String m) {
                parent.removeView(loading);
                showEmptyOrders(parent);
            }
        });
    }

    private void showEmptyOrders(LinearLayout parent) {
        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 40), UiKit.dp(context, 20), UiKit.dp(context, 40));

        TextView emoji = UiKit.text(context, "📋", 48, UiKit.MUTED, Typeface.NORMAL);
        emoji.setGravity(Gravity.CENTER);
        card.addView(emoji);

        TextView msg = UiKit.text(context, "Henüz siparişiniz bulunmuyor", 18, UiKit.TEXT, Typeface.BOLD);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, UiKit.dp(context, 12), 0, UiKit.dp(context, 16));
        card.addView(msg);

        Button shopBtn = UiKit.button(context, "Alışverişe Başla");
        shopBtn.setOnClickListener(v -> nav.showProducts());
        card.addView(shopBtn);

        parent.addView(card);
    }

    private LinearLayout orderCard(LinearLayout parentContainer, JSONObject order) {
        LinearLayout card = UiKit.card(context);

        // Header: order number + date
        LinearLayout headerRow = UiKit.horizontal(context);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        String orderId = order.optString("id", "");
        String shortId = orderId.length() > 8 ? orderId.substring(0, 8) : orderId;
        TextView orderNo = UiKit.text(context, "Sipariş #" + shortId, 15, UiKit.TEXT, Typeface.BOLD);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        orderNo.setLayoutParams(np);
        headerRow.addView(orderNo);

        String date = FeatureSupport.formatDate(order.optString("createdAt", ""));
        headerRow.addView(UiKit.text(context, date, 12, UiKit.MUTED, Typeface.NORMAL));
        card.addView(headerRow);

        // Status badge
        String status = order.optString("status", "pending");
        String statusText; int statusColor;
        switch (status) {
            case "confirmed": statusText = "Onaylandı"; statusColor = UiKit.BLUE; break;
            case "shipped": statusText = "Kargoda"; statusColor = UiKit.PURPLE; break;
            case "delivered": statusText = "Teslim Edildi"; statusColor = UiKit.ACCENT; break;
            case "cancelled": statusText = "İptal Edildi"; statusColor = UiKit.DANGER; break;
            default: statusText = "Beklemede"; statusColor = UiKit.ORANGE; break;
        }
        TextView badge = UiKit.badge(context, statusText, statusColor);
        badge.setPadding(UiKit.dp(context, 12), UiKit.dp(context, 5), UiKit.dp(context, 12), UiKit.dp(context, 5));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bp.setMargins(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));
        badge.setLayoutParams(bp);
        card.addView(badge);

        card.addView(UiKit.divider(context));

        // Items
        JSONArray items = order.optJSONArray("items");
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    LinearLayout itemRow = UiKit.horizontal(context);
                    itemRow.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
                    itemRow.setGravity(Gravity.CENTER_VERTICAL);

                    String pName = item.optString("productName", item.optString("name", "Ürün"));
                    int qty = item.optInt("quantity", 1);
                    double unitPrice = item.optDouble("unitPrice", item.optDouble("price", 0));

                    TextView nameView = UiKit.text(context, pName, 14, UiKit.TEXT, Typeface.NORMAL);
                    LinearLayout.LayoutParams npp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    nameView.setLayoutParams(npp);
                    itemRow.addView(nameView);

                    itemRow.addView(UiKit.text(context, qty + " × " + FeatureSupport.formatPrice(unitPrice), 13, UiKit.MUTED, Typeface.NORMAL));
                    card.addView(itemRow);
                } catch (Exception ignored) {}
            }
        }

        card.addView(UiKit.divider(context));

        // Total
        LinearLayout totalRow = UiKit.horizontal(context);
        totalRow.setPadding(0, UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        TextView totalLabel = UiKit.text(context, "Toplam", 16, UiKit.TEXT, Typeface.BOLD);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        totalLabel.setLayoutParams(tlp);
        totalRow.addView(totalLabel);
        totalRow.addView(UiKit.text(context, FeatureSupport.formatPrice(order.optDouble("totalAmount", 0)), 18, UiKit.ORANGE, Typeface.BOLD));
        card.addView(totalRow);

        // Cancel button (only for pending/confirmed)
        if ("pending".equals(status) || "confirmed".equals(status)) {
            Button cancelBtn = UiKit.dangerButton(context, "Siparişi İptal Et");
            LinearLayout.LayoutParams cbp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, UiKit.dp(context, 42));
            cbp.setMargins(0, UiKit.dp(context, 8), 0, 0);
            cancelBtn.setLayoutParams(cbp);
            cancelBtn.setTextSize(13);
            cancelBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Sipariş İptali")
                        .setMessage("Bu siparişi iptal etmek istediğinize emin misiniz?")
                        .setPositiveButton("İptal Et", (d, w) -> {
                            apiClient.delete("/orders/" + orderId, sessionStore.getToken(), new ApiClient.Callback() {
                                @Override
                                public void onSuccess(String b) {
                                    Toast.makeText(context, "Sipariş iptal edildi.", Toast.LENGTH_SHORT).show();
                                    showOrders(parentContainer);
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
            card.addView(cancelBtn);
        }

        return card;
    }

    // ======= CHECKOUT =======
    public void showCheckout(LinearLayout parent) {
        parent.removeAllViews();
        if (!sessionStore.isLoggedIn()) {
            showLoginPrompt(parent, "Sipariş vermek için giriş yapın.");
            return;
        }

        // Step indicator
        parent.addView(stepIndicator());

        switch (checkoutStep) {
            case 0: checkoutStepAddress(parent); break;
            case 1: checkoutStepSummary(parent); break;
            case 2: checkoutStepPayment(parent); break;
        }
    }

    private LinearLayout stepIndicator() {
        LinearLayout row = UiKit.horizontal(context);
        row.setGravity(Gravity.CENTER);
        row.setPadding(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 20));

        String[] labels = {"Adres", "Özet", "Ödeme"};
        for (int i = 0; i < 3; i++) {
            boolean active = i == checkoutStep;
            boolean done = i < checkoutStep;

            LinearLayout stepItem = UiKit.vertical(context);
            stepItem.setGravity(Gravity.CENTER);

            TextView circle = new TextView(context);
            circle.setText(done ? "✓" : String.valueOf(i + 1));
            circle.setTextSize(14);
            circle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            circle.setGravity(Gravity.CENTER);
            int circleColor = active ? UiKit.ACCENT : (done ? UiKit.ACCENT : UiKit.PANEL_SOFT);
            int textColor = active || done ? UiKit.BG : UiKit.MUTED;
            circle.setTextColor(textColor);
            circle.setBackground(UiKit.rounded(context, circleColor, circleColor, 16, 0));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(UiKit.dp(context, 32), UiKit.dp(context, 32));
            circle.setLayoutParams(cp);
            stepItem.addView(circle);

            TextView label = UiKit.text(context, labels[i], 11, active ? UiKit.ACCENT : UiKit.MUTED, active ? Typeface.BOLD : Typeface.NORMAL);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, UiKit.dp(context, 4), 0, 0);
            stepItem.addView(label);

            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            stepItem.setLayoutParams(sp);
            row.addView(stepItem);

            if (i < 2) {
                View line = new android.view.View(context);
                line.setBackgroundColor(i < checkoutStep ? UiKit.ACCENT : UiKit.LINE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UiKit.dp(context, 30), UiKit.dp(context, 2));
                lp.setMargins(0, UiKit.dp(context, 14), 0, 0);
                line.setLayoutParams(lp);
                row.addView(line);
            }
        }
        return row;
    }

    // Step 1: Address
    private void checkoutStepAddress(LinearLayout parent) {
        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.text(context, "Teslimat Adresi", 20, UiKit.TEXT, Typeface.BOLD));
        TextView sub = UiKit.text(context, "Siparişinizin teslim edileceği adresi seçin", 13, UiKit.MUTED, Typeface.NORMAL);
        sub.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 16));
        card.addView(sub);

        LinearLayout addressList = UiKit.vertical(context);
        card.addView(addressList);

        LinearLayout formContainer = UiKit.vertical(context);
        card.addView(formContainer);

        TextView errorText = UiKit.text(context, "", 13, UiKit.DANGER, Typeface.NORMAL);
        card.addView(errorText);

        Button addNew = UiKit.secondaryButton(context, "＋ Yeni Adres Ekle");
        addNew.setOnClickListener(v -> showInlineAddressForm(formContainer, addressList, card));
        card.addView(addNew);

        parent.addView(card);

        // Next button
        Button nextBtn = UiKit.button(context, "Devam →");
        nextBtn.setOnClickListener(v -> {
            if (selectedAddressId.isEmpty()) {
                errorText.setText("Lütfen bir teslimat adresi seçin.");
                return;
            }
            checkoutStep = 1;
            showCheckout(parent);
        });
        parent.addView(nextBtn);

        // Load addresses
        apiClient.get("/addresses", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject root = new JSONObject(body);
                    JSONArray data = root.optJSONArray("data");
                    if (data != null && data.length() > 0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject addr = data.getJSONObject(i);
                            addressList.addView(selectableAddressCard(addr, addressList));
                        }
                        // Auto-select first
                        if (selectedAddressId.isEmpty()) {
                            selectedAddressId = data.getJSONObject(0).optString("id", "");
                            selectedAddressObj = data.getJSONObject(0);
                            refreshAddressSelection(addressList);
                        }
                    } else {
                        addressList.addView(UiKit.text(context, "Henüz adres eklenmemiş. Yeni bir adres ekleyin.", 14, UiKit.MUTED, Typeface.NORMAL));
                    }
                } catch (Exception ignored) {}
            }
            @Override
            public void onError(String m) {
                addressList.addView(UiKit.text(context, "Adresler yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    private LinearLayout selectableAddressCard(JSONObject addr, LinearLayout addressList) {
        String addrId = addr.optString("id", "");
        boolean selected = addrId.equals(selectedAddressId);

        LinearLayout card = UiKit.vertical(context);
        int borderColor = selected ? UiKit.ACCENT : UiKit.LINE;
        card.setBackground(UiKit.rounded(context, UiKit.PANEL, borderColor, 12, selected ? 2 : 1));
        card.setPadding(UiKit.dp(context, 14), UiKit.dp(context, 12), UiKit.dp(context, 14), UiKit.dp(context, 12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, UiKit.dp(context, 8));
        card.setLayoutParams(lp);
        card.setClickable(true);

        String title = addr.optString("title", "Adres");
        card.addView(UiKit.badge(context, title, selected ? UiKit.ACCENT : UiKit.PANEL_SOFT));

        String fullName = addr.optString("fullName", "");
        if (!fullName.isEmpty()) {
            card.addView(UiKit.text(context, fullName, 15, UiKit.TEXT, Typeface.BOLD));
        }

        String addressLine = addr.optString("addressLine", "");
        String city = addr.optString("city", "");
        String district = addr.optString("district", "");
        String full = addressLine;
        if (!district.isEmpty()) full += ", " + district;
        if (!city.isEmpty()) full += " / " + city;
        card.addView(UiKit.text(context, full, 13, UiKit.MUTED, Typeface.NORMAL));

        card.setOnClickListener(v -> {
            selectedAddressId = addrId;
            selectedAddressObj = addr;
            refreshAddressSelection(addressList);
        });

        card.setTag(addrId);
        return card;
    }

    private void refreshAddressSelection(LinearLayout addressList) {
        for (int i = 0; i < addressList.getChildCount(); i++) {
            android.view.View child = addressList.getChildAt(i);
            if (child instanceof LinearLayout && child.getTag() instanceof String) {
                String id = (String) child.getTag();
                boolean sel = id.equals(selectedAddressId);
                child.setBackground(UiKit.rounded(context, UiKit.PANEL, sel ? UiKit.ACCENT : UiKit.LINE, 12, sel ? 2 : 1));
            }
        }
    }

    private void showInlineAddressForm(LinearLayout formContainer, LinearLayout addressList, LinearLayout parentCard) {
        formContainer.removeAllViews();
        LinearLayout form = UiKit.vertical(context);
        form.setBackground(UiKit.rounded(context, UiKit.PANEL_SOFT, UiKit.LINE, 10, 1));
        form.setPadding(UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14), UiKit.dp(context, 14));

        form.addView(UiKit.text(context, "Yeni Adres", 16, UiKit.TEXT, Typeface.BOLD));

        EditText fullName = UiKit.field(context, "Ad Soyad", "");
        EditText phone = UiKit.phoneField(context, "Telefon", "");
        EditText city = UiKit.field(context, "İl", "");
        EditText district = UiKit.field(context, "İlçe", "");
        EditText addressLine = UiKit.multiLineField(context, "Adres", "");
        EditText postalCode = UiKit.field(context, "Posta Kodu", "");

        form.addView(fullName);
        form.addView(phone);
        form.addView(city);
        form.addView(district);
        form.addView(addressLine);
        form.addView(postalCode);

        LinearLayout btnRow = UiKit.horizontal(context);
        Button saveBtn = UiKit.button(context, "Kaydet");
        Button cancelBtn = UiKit.secondaryButton(context, "İptal");

        saveBtn.setOnClickListener(v -> {
            if (city.getText().toString().trim().isEmpty()) {
                Toast.makeText(context, "İl alanını doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject body = new JSONObject();
                body.put("title", "Ev");
                body.put("fullName", fullName.getText().toString().trim());
                body.put("phone", phone.getText().toString().trim());
                body.put("city", city.getText().toString().trim());
                body.put("district", district.getText().toString().trim());
                body.put("addressLine", addressLine.getText().toString().trim());
                body.put("postalCode", postalCode.getText().toString().trim());

                apiClient.post("/addresses", body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        formContainer.removeAllViews();
                        Toast.makeText(context, "Adres eklendi.", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject newAddr = new JSONObject(b);
                            selectedAddressId = newAddr.optString("id", "");
                            selectedAddressObj = newAddr;
                        } catch (Exception ignored) {}
                        // Reload addresses
                        addressList.removeAllViews();
                        apiClient.get("/addresses", sessionStore.getToken(), new ApiClient.Callback() {
                            @Override
                            public void onSuccess(String body) {
                                try {
                                    JSONObject root = new JSONObject(body);
                                    JSONArray data = root.optJSONArray("data");
                                    if (data != null) {
                                        for (int i = 0; i < data.length(); i++) {
                                            addressList.addView(selectableAddressCard(data.getJSONObject(i), addressList));
                                        }
                                    }
                                } catch (Exception ignored2) {}
                            }
                            @Override public void onError(String m) {}
                        });
                    }
                    @Override
                    public void onError(String m) {
                        Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Bir hata oluştu.", Toast.LENGTH_SHORT).show();
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
        form.addView(btnRow);

        LinearLayout.LayoutParams fmp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fmp.setMargins(0, UiKit.dp(context, 8), 0, UiKit.dp(context, 8));
        form.setLayoutParams(fmp);
        formContainer.addView(form);
    }

    // Step 2: Summary
    private void checkoutStepSummary(LinearLayout parent) {
        // Selected address
        LinearLayout addrCard = UiKit.card(context);
        addrCard.addView(UiKit.text(context, "Teslimat Adresi", 16, UiKit.ACCENT, Typeface.BOLD));
        if (selectedAddressObj != null) {
            String name = selectedAddressObj.optString("fullName", "");
            String line = selectedAddressObj.optString("addressLine", "");
            String city = selectedAddressObj.optString("city", "");
            String district = selectedAddressObj.optString("district", "");
            if (!name.isEmpty()) addrCard.addView(UiKit.text(context, name, 15, UiKit.TEXT, Typeface.BOLD));
            String full = line;
            if (!district.isEmpty()) full += ", " + district;
            if (!city.isEmpty()) full += " / " + city;
            addrCard.addView(UiKit.text(context, full, 13, UiKit.MUTED, Typeface.NORMAL));
        }
        parent.addView(addrCard);

        // Cart items
        LinearLayout itemsCard = UiKit.card(context);
        itemsCard.addView(UiKit.text(context, "Sipariş Detayı", 16, UiKit.ACCENT, Typeface.BOLD));

        TextView loadText = UiKit.text(context, "Yükleniyor...", 14, UiKit.MUTED, Typeface.NORMAL);
        itemsCard.addView(loadText);
        parent.addView(itemsCard);

        // Nav buttons
        LinearLayout btnRow = UiKit.horizontal(context);
        Button backBtn = UiKit.secondaryButton(context, "← Geri");
        Button nextBtn = UiKit.button(context, "Devam →");
        backBtn.setOnClickListener(v -> { checkoutStep = 0; showCheckout(parent); });
        nextBtn.setOnClickListener(v -> { checkoutStep = 2; showCheckout(parent); });

        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        backBtn.setLayoutParams(hp);
        LinearLayout.LayoutParams hp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        hp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        nextBtn.setLayoutParams(hp2);
        btnRow.addView(backBtn);
        btnRow.addView(nextBtn);
        parent.addView(btnRow);

        // Fetch cart for summary
        apiClient.get("/cart", sessionStore.getToken(), new ApiClient.Callback() {
            @Override
            public void onSuccess(String body) {
                itemsCard.removeView(loadText);
                try {
                    JSONObject cart = new JSONObject(body);
                    cartItems = cart.optJSONArray("items");
                    cartTotal = cart.optDouble("totalPrice", 0);

                    if (cartItems != null) {
                        double subtotal = 0;
                        for (int i = 0; i < cartItems.length(); i++) {
                            JSONObject item = cartItems.getJSONObject(i);
                            String n = item.optString("name", "Ürün");
                            int q = item.optInt("quantity", 1);
                            double p = item.optDouble("price", 0);
                            subtotal += p * q;

                            LinearLayout ir = UiKit.horizontal(context);
                            ir.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 4));
                            TextView nt = UiKit.text(context, n, 14, UiKit.TEXT, Typeface.NORMAL);
                            LinearLayout.LayoutParams ntp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                            nt.setLayoutParams(ntp);
                            ir.addView(nt);
                            ir.addView(UiKit.text(context, q + " × " + FeatureSupport.formatPrice(p), 13, UiKit.MUTED, Typeface.NORMAL));
                            itemsCard.addView(ir);
                        }
                        itemsCard.addView(UiKit.divider(context));
                        double ship = subtotal >= 500 ? 0 : 29.90;
                        itemsCard.addView(summaryRow("Ara Toplam", FeatureSupport.formatPrice(subtotal)));
                        itemsCard.addView(summaryRow("Kargo", ship == 0 ? "Ücretsiz" : FeatureSupport.formatPrice(ship)));
                        itemsCard.addView(UiKit.divider(context));

                        LinearLayout tr = UiKit.horizontal(context);
                        TextView tl = UiKit.text(context, "Toplam", 17, UiKit.TEXT, Typeface.BOLD);
                        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                        tl.setLayoutParams(tlp);
                        tr.addView(tl);
                        tr.addView(UiKit.text(context, FeatureSupport.formatPrice(subtotal + ship), 20, UiKit.ORANGE, Typeface.BOLD));
                        itemsCard.addView(tr);
                    }
                } catch (Exception ignored) {}
            }
            @Override
            public void onError(String m) {
                itemsCard.removeView(loadText);
                itemsCard.addView(UiKit.text(context, "Sepet yüklenemedi.", 14, UiKit.DANGER, Typeface.NORMAL));
            }
        });
    }

    // Step 3: Payment
    private void checkoutStepPayment(LinearLayout parent) {
        LinearLayout card = UiKit.card(context);
        card.addView(UiKit.text(context, "Ödeme Yöntemi", 20, UiKit.TEXT, Typeface.BOLD));
        TextView sub = UiKit.text(context, "Ödeme yönteminizi seçin", 13, UiKit.MUTED, Typeface.NORMAL);
        sub.setPadding(0, UiKit.dp(context, 4), 0, UiKit.dp(context, 16));
        card.addView(sub);

        // Payment options
        LinearLayout ccOption = paymentOption("💳", "Kredi Kartı", "credit_card");
        LinearLayout codOption = paymentOption("🚚", "Kapıda Ödeme", "cash_on_delivery");
        card.addView(ccOption);
        card.addView(codOption);

        // Credit card form (shown by default)
        LinearLayout ccForm = UiKit.vertical(context);
        ccForm.setPadding(0, UiKit.dp(context, 12), 0, 0);
        EditText cardNum = UiKit.field(context, "Kart Numarası", "");
        cardNum.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText expiry = UiKit.field(context, "Son Kullanma (AA/YY)", "");
        EditText cvv = UiKit.field(context, "CVV", "");
        cvv.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText cardName = UiKit.field(context, "Kart Üzerindeki İsim", "");
        ccForm.addView(cardNum);

        LinearLayout row2 = UiKit.horizontal(context);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        hp.setMargins(0, 0, UiKit.dp(context, 5), 0);
        expiry.setLayoutParams(hp);
        LinearLayout.LayoutParams hp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        hp2.setMargins(UiKit.dp(context, 5), 0, 0, 0);
        cvv.setLayoutParams(hp2);
        row2.addView(expiry);
        row2.addView(cvv);
        ccForm.addView(row2);
        ccForm.addView(cardName);
        card.addView(ccForm);

        // Toggle payment method
        ccOption.setOnClickListener(v -> {
            paymentMethod = "credit_card";
            ccOption.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.ACCENT, 10, 2));
            codOption.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 10, 1));
            ccForm.setVisibility(android.view.View.VISIBLE);
        });
        codOption.setOnClickListener(v -> {
            paymentMethod = "cash_on_delivery";
            codOption.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.ACCENT, 10, 2));
            ccOption.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 10, 1));
            ccForm.setVisibility(android.view.View.GONE);
        });
        // Default selection
        ccOption.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.ACCENT, 10, 2));

        parent.addView(card);

        // Buttons
        LinearLayout btnRow = UiKit.horizontal(context);
        Button backBtn = UiKit.secondaryButton(context, "← Geri");
        Button confirmBtn = UiKit.button(context, "Siparişi Onayla");

        backBtn.setOnClickListener(v -> { checkoutStep = 1; showCheckout(parent); });
        confirmBtn.setOnClickListener(v -> {
            confirmBtn.setEnabled(false);
            confirmBtn.setText("İşleniyor...");
            try {
                JSONObject body = new JSONObject();
                body.put("addressId", selectedAddressId);
                apiClient.post("/orders", body, sessionStore.getToken(), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String b) {
                        confirmBtn.setEnabled(true);
                        String orderId = "";
                        try {
                            orderId = new JSONObject(b).optString("id", "");
                        } catch (Exception ignored) {}
                        showOrderSuccess(parent, orderId);
                    }
                    @Override
                    public void onError(String m) {
                        confirmBtn.setEnabled(true);
                        confirmBtn.setText("Siparişi Onayla");
                        Toast.makeText(context, FeatureSupport.parseError(m), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                confirmBtn.setEnabled(true);
                confirmBtn.setText("Siparişi Onayla");
            }
        });

        LinearLayout.LayoutParams bhp = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        bhp.setMargins(0, UiKit.dp(context, 6), UiKit.dp(context, 5), UiKit.dp(context, 6));
        backBtn.setLayoutParams(bhp);
        LinearLayout.LayoutParams bhp2 = new LinearLayout.LayoutParams(0, UiKit.dp(context, 48), 1);
        bhp2.setMargins(UiKit.dp(context, 5), UiKit.dp(context, 6), 0, UiKit.dp(context, 6));
        confirmBtn.setLayoutParams(bhp2);
        btnRow.addView(backBtn);
        btnRow.addView(confirmBtn);
        parent.addView(btnRow);
    }

    private LinearLayout paymentOption(String icon, String label, String method) {
        LinearLayout opt = UiKit.horizontal(context);
        opt.setGravity(Gravity.CENTER_VERTICAL);
        opt.setBackground(UiKit.rounded(context, UiKit.PANEL, UiKit.LINE, 10, 1));
        opt.setPadding(UiKit.dp(context, 16), UiKit.dp(context, 14), UiKit.dp(context, 16), UiKit.dp(context, 14));
        opt.setClickable(true);
        LinearLayout.LayoutParams olp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        olp.setMargins(0, 0, 0, UiKit.dp(context, 8));
        opt.setLayoutParams(olp);

        TextView iconView = UiKit.text(context, icon, 24, UiKit.TEXT, Typeface.NORMAL);
        iconView.setPadding(0, 0, UiKit.dp(context, 12), 0);
        opt.addView(iconView);
        opt.addView(UiKit.text(context, label, 16, UiKit.TEXT, Typeface.BOLD));
        return opt;
    }

    private void showOrderSuccess(LinearLayout parent, String orderId) {
        parent.removeAllViews();
        checkoutStep = 0;
        selectedAddressId = "";
        selectedAddressObj = null;

        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 40), UiKit.dp(context, 20), UiKit.dp(context, 40));

        TextView check = UiKit.text(context, "✅", 64, UiKit.ACCENT, Typeface.NORMAL);
        check.setGravity(Gravity.CENTER);
        card.addView(check);

        TextView title = UiKit.text(context, "Siparişiniz Alındı!", 24, UiKit.TEXT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, UiKit.dp(context, 16), 0, UiKit.dp(context, 8));
        card.addView(title);

        TextView desc = UiKit.text(context, "Siparişiniz başarıyla oluşturuldu. Siparişlerim sayfasından takip edebilirsiniz.", 14, UiKit.MUTED, Typeface.NORMAL);
        desc.setGravity(Gravity.CENTER);
        desc.setPadding(0, 0, 0, UiKit.dp(context, 8));
        card.addView(desc);

        if (!orderId.isEmpty()) {
            String shortId = orderId.length() > 8 ? orderId.substring(0, 8) : orderId;
            card.addView(UiKit.badge(context, "Sipariş #" + shortId, UiKit.ACCENT));
        }

        parent.addView(card);

        Button ordersBtn = UiKit.button(context, "Siparişlerime Git");
        ordersBtn.setOnClickListener(v -> nav.showOrders());
        parent.addView(ordersBtn);

        Button shopBtn = UiKit.secondaryButton(context, "Alışverişe Devam Et");
        shopBtn.setOnClickListener(v -> nav.showProducts());
        parent.addView(shopBtn);
    }

    // ======= HELPERS =======
    private void showLoginPrompt(LinearLayout parent, String message) {
        LinearLayout card = UiKit.card(context);
        card.setGravity(Gravity.CENTER);
        card.setPadding(UiKit.dp(context, 20), UiKit.dp(context, 40), UiKit.dp(context, 20), UiKit.dp(context, 40));

        TextView icon = UiKit.text(context, "🔒", 48, UiKit.MUTED, Typeface.NORMAL);
        icon.setGravity(Gravity.CENTER);
        card.addView(icon);

        TextView msg = UiKit.text(context, message, 16, UiKit.TEXT, Typeface.BOLD);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, UiKit.dp(context, 16), 0, UiKit.dp(context, 20));
        card.addView(msg);

        Button loginBtn = UiKit.button(context, "Giriş Yap");
        loginBtn.setOnClickListener(v -> nav.showLogin());
        card.addView(loginBtn);

        parent.addView(card);
    }
}
