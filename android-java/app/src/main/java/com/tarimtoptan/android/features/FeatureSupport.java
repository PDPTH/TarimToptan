package com.tarimtoptan.android.features;

import android.widget.Toast;

import com.tarimtoptan.android.net.ApiClient;

import org.json.JSONObject;

public class FeatureSupport {

    /** Parse a user-friendly error message from API error string */
    public static String parseError(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return "Bir hata oluştu. Lütfen tekrar deneyin.";
        }
        try {
            // Error format: "HTTP 4xx - {json}"
            int dashIndex = errorMessage.indexOf(" - ");
            if (dashIndex >= 0) {
                String jsonPart = errorMessage.substring(dashIndex + 3).trim();
                JSONObject errorJson = new JSONObject(jsonPart);
                String message = errorJson.optString("message", "");
                if (!message.isEmpty()) {
                    return message;
                }
            }
        } catch (Exception ignored) {
        }
        if (errorMessage.contains("ConnectException") || errorMessage.contains("connect")) {
            return "Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin.";
        }
        if (errorMessage.contains("SocketTimeout") || errorMessage.contains("timeout")) {
            return "Bağlantı zaman aşımına uğradı. Lütfen tekrar deneyin.";
        }
        if (errorMessage.startsWith("HTTP 401") || errorMessage.contains("401")) {
            return "Oturum süresi dolmuş. Lütfen tekrar giriş yapın.";
        }
        if (errorMessage.startsWith("HTTP 403") || errorMessage.contains("403")) {
            return "Bu işlem için yetkiniz bulunmuyor.";
        }
        if (errorMessage.startsWith("HTTP 404") || errorMessage.contains("404")) {
            return "İstenen kaynak bulunamadı.";
        }
        if (errorMessage.startsWith("HTTP 409") || errorMessage.contains("409")) {
            return "Bu kayıt zaten mevcut.";
        }
        return "Bir hata oluştu. Lütfen tekrar deneyin.";
    }

    /** Format ISO date string to Turkish readable format */
    public static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            String datePart = isoDate.contains("T") ? isoDate.substring(0, isoDate.indexOf("T")) : isoDate;
            String[] parts = datePart.split("-");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                String[] months = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
                return day + " " + months[month - 1] + " " + year;
            }
        } catch (Exception ignored) {
        }
        return isoDate;
    }

    /** Format price as Turkish Lira */
    public static String formatPrice(double price) {
        return String.format("₺%.2f", price);
    }

    /** Create a JSON object safely */
    public static JSONObject json() {
        return new JSONObject();
    }
}
