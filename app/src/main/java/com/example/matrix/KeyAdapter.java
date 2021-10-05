package com.example.matrix;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class KeyAdapter extends ArrayAdapter<Integer> {

    private Button gridButton;

    @StringRes
    private static final Integer[] keys = new Integer[] {
            R.string.btn_7, R.string.btn_8, R.string.btn_9, R.string.btn_clear, R.string.btn_clear,
            R.string.btn_4, R.string.btn_5, R.string.btn_6, R.string.btn_plus, R.string.btn_minus,
            R.string.btn_1, R.string.btn_2, R.string.btn_3, R.string.btn_multi, R.string.btn_deg,
            R.string.btn_0, R.string.btn_div, R.string.btn_dot, R.string.btn_equal, R.string.btn_go
    };

    private final Context mContext;

    public KeyAdapter(@NonNull Context context, int resource) {
        super(context, resource, keys);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View grid;

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.grid_item_button, parent, false);
        }
        else
            grid = convertView;

        gridButton = grid.findViewById(R.id.grid_item_button);
        gridButton.setText(keys[position]);
//        gridButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Вы выбрали " + gridButton.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

        String numb = gridButton.getText().toString();
        if (!Matrix.isDigit(numb)) {
            gridButton.setBackgroundResource(R.drawable.button_style_2);
            gridButton.setTextColor(Color.WHITE);
        }

        return grid;
    }

    @StringRes
    public Integer getItem(int position) {
        return keys[position];
    }

    @Nullable
    public Button getButton() {
        return gridButton;
    }
}
