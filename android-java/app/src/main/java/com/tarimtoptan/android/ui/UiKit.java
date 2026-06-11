package com.tarimtoptan.android.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UiKit {
    public static final int BG = Color.rgb(13, 31, 13);
    public static final int SURFACE = Color.rgb(9, 33, 14);
    public static final int PANEL = Color.rgb(22, 46, 22);
    public static final int PANEL_SOFT = Color.rgb(30, 62, 30);
    public static final int LINE = Color.rgb(54, 112, 58);
    public static final int TEXT = Color.rgb(232, 245, 233);
    public static final int MUTED = Color.rgb(165, 214, 167);
    public static final int ACCENT = Color.rgb(76, 175, 80);
    public static final int DANGER = Color.rgb(215, 78, 70);
    public static final int ORANGE = Color.rgb(255, 152, 0);
    public static final int ORANGE_DARK = Color.rgb(245, 124, 0);
    public static final int BLUE = Color.rgb(66, 165, 245);
    public static final int PURPLE = Color.rgb(171, 71, 188);
    public static final int YELLOW = Color.rgb(255, 202, 40);
    public static final int WARNING_BG = Color.rgb(45, 40, 15);

    public static TextView text(Context context, String value, int sp, int color, int style) {
        TextView textView = new TextView(context);
        textView.setText(value);
        textView.setTextSize(sp);
        textView.setTextColor(color);
        textView.setTypeface(Typeface.DEFAULT, style);
        textView.setLineSpacing(0, 1.12f);
        return textView;
    }

    public static LinearLayout vertical(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    public static LinearLayout horizontal(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    public static LinearLayout card(Context context) {
        LinearLayout card = vertical(context);
        card.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 16));
        card.setBackground(rounded(context, PANEL, LINE, 14, 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(context, 12));
        card.setLayoutParams(params);
        return card;
    }

    public static EditText field(Context context, String hint, String value) {
        EditText field = new EditText(context);
        field.setHint(hint);
        field.setText(value);
        field.setSingleLine(true);
        field.setTextColor(TEXT);
        field.setHintTextColor(MUTED);
        field.setTextSize(15);
        field.setPadding(dp(context, 14), 0, dp(context, 14), 0);
        field.setBackground(rounded(context, SURFACE, LINE, 10, 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 52)
        );
        params.setMargins(0, dp(context, 6), 0, dp(context, 6));
        field.setLayoutParams(params);
        return field;
    }

    public static EditText multiLineField(Context context, String hint, String value) {
        EditText field = new EditText(context);
        field.setHint(hint);
        field.setText(value);
        field.setSingleLine(false);
        field.setMinLines(3);
        field.setMaxLines(5);
        field.setGravity(Gravity.TOP);
        field.setTextColor(TEXT);
        field.setHintTextColor(MUTED);
        field.setTextSize(15);
        field.setPadding(dp(context, 14), dp(context, 12), dp(context, 14), dp(context, 12));
        field.setBackground(rounded(context, SURFACE, LINE, 10, 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(context, 6), 0, dp(context, 6));
        field.setLayoutParams(params);
        return field;
    }

    public static EditText passwordField(Context context, String hint, String value) {
        EditText field = field(context, hint, value);
        field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return field;
    }

    public static EditText numberField(Context context, String hint, String value) {
        EditText field = field(context, hint, value);
        field.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return field;
    }

    public static EditText emailField(Context context, String hint, String value) {
        EditText field = field(context, hint, value);
        field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        return field;
    }

    public static EditText phoneField(Context context, String hint, String value) {
        EditText field = field(context, hint, value);
        field.setInputType(InputType.TYPE_CLASS_PHONE);
        return field;
    }

    public static Spinner createSpinner(Context context, String[] items) {
        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(TEXT);
                view.setTextSize(15);
                view.setPadding(dp(context, 14), dp(context, 10), dp(context, 14), dp(context, 10));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(TEXT);
                view.setBackgroundColor(PANEL);
                view.setPadding(dp(context, 14), dp(context, 12), dp(context, 14), dp(context, 12));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackground(rounded(context, SURFACE, LINE, 10, 1));
        spinner.setPadding(dp(context, 4), 0, dp(context, 4), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 52)
        );
        params.setMargins(0, dp(context, 6), 0, dp(context, 6));
        spinner.setLayoutParams(params);
        return spinner;
    }

    public static Button button(Context context, String label) {
        Button button = baseButton(context, label);
        button.setTextColor(Color.rgb(6, 24, 12));
        button.setBackground(rounded(context, ACCENT, ACCENT, 10, 1));
        return button;
    }

    public static Button secondaryButton(Context context, String label) {
        Button button = baseButton(context, label);
        button.setTextColor(TEXT);
        button.setBackground(rounded(context, PANEL, LINE, 10, 1));
        return button;
    }

    public static Button orangeButton(Context context, String label) {
        Button button = baseButton(context, label);
        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(context, ORANGE, ORANGE_DARK, 10, 1));
        return button;
    }

    public static Button dangerButton(Context context, String label) {
        Button button = baseButton(context, label);
        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(context, DANGER, DANGER, 10, 1));
        return button;
    }

    public static Button menuButton(Context context, String label) {
        Button button = baseButton(context, label);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setTextColor(TEXT);
        button.setPadding(dp(context, 16), 0, dp(context, 16), 0);
        button.setBackground(rounded(context, PANEL, LINE, 10, 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 52)
        );
        params.setMargins(0, 0, 0, dp(context, 10));
        button.setLayoutParams(params);
        return button;
    }

    public static ImageButton iconButton(Context context, int drawableId) {
        ImageButton button = new ImageButton(context);
        button.setImageResource(drawableId);
        button.setColorFilter(MUTED);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setScaleType(ImageView.ScaleType.CENTER);
        button.setPadding(dp(context, 10), dp(context, 10), dp(context, 10), dp(context, 10));
        button.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 44), dp(context, 44)));
        return button;
    }

    private static Button baseButton(Context context, String label) {
        Button button = new Button(context);
        button.setText(label);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setTextSize(14);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 48)
        );
        params.setMargins(0, dp(context, 6), 0, dp(context, 6));
        button.setLayoutParams(params);
        return button;
    }

    public static TextView badge(Context context, String text, int bgColor) {
        TextView badge = new TextView(context);
        badge.setText(text);
        badge.setTextSize(11);
        badge.setTextColor(Color.WHITE);
        badge.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        badge.setPadding(dp(context, 10), dp(context, 4), dp(context, 10), dp(context, 4));
        badge.setBackground(rounded(context, bgColor, bgColor, 6, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(context, 6));
        badge.setLayoutParams(params);
        return badge;
    }

    public static View divider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(LINE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 1)
        );
        params.setMargins(0, dp(context, 12), 0, dp(context, 12));
        divider.setLayoutParams(params);
        return divider;
    }

    public static TextView iconCircle(Context context, String emoji, int sizeDp, int bgColor) {
        TextView circle = new TextView(context);
        circle.setText(emoji);
        circle.setTextSize(sizeDp / 2);
        circle.setGravity(Gravity.CENTER);
        circle.setBackground(rounded(context, bgColor, bgColor, sizeDp / 2, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dp(context, sizeDp),
                dp(context, sizeDp)
        );
        circle.setLayoutParams(params);
        return circle;
    }

    public static TextView cartBadge(Context context) {
        TextView badge = new TextView(context);
        badge.setTextSize(10);
        badge.setTextColor(Color.WHITE);
        badge.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(rounded(context, ORANGE, ORANGE, 10, 0));
        badge.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dp(context, 20),
                dp(context, 20)
        );
        params.setMargins(dp(context, -12), dp(context, 2), 0, 0);
        badge.setLayoutParams(params);
        return badge;
    }

    public static void addTitle(LinearLayout parent, Context context, String title, String subtitle) {
        parent.addView(text(context, title, 20, TEXT, Typeface.BOLD));
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView sub = text(context, subtitle, 14, MUTED, Typeface.NORMAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, dp(context, 4), 0, dp(context, 10));
            sub.setLayoutParams(params);
            parent.addView(sub);
        }
    }

    public static void setEnabledDeep(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;
            for (int i = 0; i < layout.getChildCount(); i++) {
                setEnabledDeep(layout.getChildAt(i), enabled);
            }
        }
    }

    public static GradientDrawable rounded(Context context, int fill, int stroke, int radius, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(context, radius));
        if (strokeWidth > 0) {
            drawable.setStroke(dp(context, strokeWidth), stroke);
        }
        return drawable;
    }

    public static int dp(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    /** Get the category emoji for product display */
    public static String categoryEmoji(String category) {
        if (category == null) return "🌿";
        switch (category.toLowerCase()) {
            case "sebze": return "🥬";
            case "meyve": return "🍎";
            case "bakliyat": return "🫘";
            case "tahıl": case "tahil": return "🌾";
            case "süt ürünleri": case "sut urunleri": return "🧀";
            case "et ürünleri": case "et urunleri": return "🥩";
            case "bal & reçel": case "bal recel": return "🍯";
            case "kuruyemiş": case "kuruyemis": return "🥜";
            default: return "🌿";
        }
    }
}
