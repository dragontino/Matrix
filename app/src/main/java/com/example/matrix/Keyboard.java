package com.example.matrix;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class Keyboard {

    public static final int ID_0 = 0xA000;
    public static final int ID_1 = 0xA001;
    public static final int ID_2 = 0xA002;
    public static final int ID_3 = 0xA003;
    public static final int ID_4 = 0xA004;
    public static final int ID_5 = 0xA005;
    public static final int ID_6 = 0xA006;
    public static final int ID_7 = 0xA007;
    public static final int ID_8 = 0xA008;
    public static final int ID_9 = 0xA009;
    public static final int ID_PLUS = 0xA0011;
    public static final int ID_MINUS = 0xA0012;
    public static final int ID_MULTI = 0xA0013;
    public static final int ID_DEG = 0xA0014;
    public static final int ID_DIV = 0xA015;
    public static final int ID_EQUAL = 0xA0016;
    public static final int ID_DOT = 0xA0017;
    public static final int ID_CLEAR = 0xA0018;
    public static final int ID_GO = 0xA0019;

    private static final int[] BUTTONS_ID = new int[] {
            ID_7, ID_8, ID_9, ID_CLEAR,
            ID_4, ID_5, ID_6, ID_PLUS, ID_MINUS,
            ID_1, ID_2, ID_3, ID_MULTI, ID_DEG,
            ID_0, ID_DIV, ID_DOT, ID_EQUAL, ID_GO
    };

    @StringRes
    private static final int[] keys = new int[] {
            R.string.btn_7, R.string.btn_8, R.string.btn_9, R.string.btn_clear,
            R.string.btn_4, R.string.btn_5, R.string.btn_6, R.string.btn_plus, R.string.btn_minus,
            R.string.btn_1, R.string.btn_2, R.string.btn_3, R.string.btn_multi, R.string.btn_deg,
            R.string.btn_0, R.string.btn_div, R.string.btn_dot, R.string.btn_equal, R.string.btn_go
    };

    private static final int TYPE_NUMBER = 1;
    private static final int TYPE_SIGN = 2;

    private static final int START_COUNT = 4;
    private static final int ITEM_COUNT = keys.length;

    private final GridLayout gridLayout;
    private final Context context;

    public Keyboard(Context context, GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        this.context = context;
    }

    public void create() {
        updateButtons();

        Button button;

        for (int i = START_COUNT; i < ITEM_COUNT; i++) {
            if (Matrix.isDigit(context, keys[i]))
                button = createButton(TYPE_NUMBER, i);
            else
                button = createButton(TYPE_SIGN, i);

            int size = getDPs(80);

            gridLayout.setColumnCount(5);
            gridLayout.addView(button, size, size);
        }
    }

    public void setListeners(View.OnClickListener listener) {
        for (int pos = 0; pos < ITEM_COUNT; pos++) {
            View child = gridLayout.getChildAt(pos);
            child.setOnClickListener(listener);
        }
    }

    public @StringRes int getButtonText(int id) {
        for (int pos = 0; pos < ITEM_COUNT; pos++) {
            if (BUTTONS_ID[pos] == id)
                return keys[pos];
        }
        return keys[0];
    }

    private void updateButtons() {

        for (int i = 0; i < START_COUNT - 1; i++) {
            Button btn = (Button) gridLayout.getChildAt(i);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(103, 133, 240)));
            btn.setTextColor(Color.BLACK);
            btn.setId(BUTTONS_ID[i]);
        }

        ImageButton btnClear = (ImageButton) gridLayout.getChildAt(START_COUNT - 1);
        btnClear.setId(BUTTONS_ID[START_COUNT - 1]);
    }

    private Button createButton(int type, int position) {
        int margin = getDPs(5);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(margin, margin, margin, margin);


        if (keys[position] == R.string.btn_clear) {
            params.setGravity(Gravity.FILL);
            params.rowSpec = GridLayout.spec(3, 3, 3f);
            params.columnSpec = GridLayout.spec(0, 3, 3f);
        }
        else
            params.setGravity(Gravity.START);
//
        Button button = new Button(context);
        button.setLayoutParams(params);
        button.setId(BUTTONS_ID[position]);
        button.setTextSize(16);
        button.setGravity(Gravity.CENTER);
        button.setText(keys[position]);

//        Button button = (Button) inflater.inflate(R.layout.grid_item_button, gridLayout, false);
//        button.setId(BUTTONS_ID[position]);
//        button.setTextSize(16);
//        button.setGravity(Gravity.CENTER);
//        button.setText(keys[position]);

        switch (type) {
            case TYPE_NUMBER:
                button.setBackgroundResource(R.drawable.button_style);
                button.setTextColor(Color.BLACK);
                break;
            case TYPE_SIGN:
                button.setBackgroundResource(R.drawable.button_style_2);
                button.setTextColor(Color.WHITE);
                break;
        }

        if (BUTTONS_ID[position] == ID_GO)
            button.setBackgroundResource(R.drawable.ic_send);

        return button;
    }

    private int getDPs(int value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
