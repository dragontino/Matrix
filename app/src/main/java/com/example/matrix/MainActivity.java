package com.example.matrix;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView textView2;
    private TextView textAnswer, operation;
    private Keyboard keyboard;
    private RecyclerView firstRecyclerView, secondRecyclerView;
    private MenuItem menuItem;

    private OnKeyClicked clicked;
//    private KeyAdapter gridAdapter;

    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout./*recycler_view*/activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        getWindow().setStatusBarColor(Color.WHITE);
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.white));

        SpannableString title = new SpannableString(getTitle());
        title.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                title.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        setTitle(title);

        TextView textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textAnswer = findViewById(R.id.answer_text_view);

        operation = findViewById(R.id.textViewOperation);
        operation.setText("  ");

        GridLayout gridLayout = findViewById(R.id.grid_layout);
        keyboard = new Keyboard(this, gridLayout);
        keyboard.create();
        keyboard.setListeners(this);

        clicked = new OnKeyClicked(this);

        firstRecyclerView = findViewById(R.id.recyclerView);
        updateRecyclerView(1);
        updateAdapter(0);

        secondRecyclerView = findViewById(R.id.second_recyclerView);
//        updateRecyclerView(true, 1);
//        updateAdapter(true, 0);
    }

    private RecyclerView currentRecyclerView() {
        return clicked.isSecondMatrix() ? secondRecyclerView : firstRecyclerView;
    }

    private void updateRecyclerView(@IntRange(from = 1) int spanCount) {
        currentRecyclerView().setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    private void updateAdapter(int itemCount) {
        currentRecyclerView().setAdapter(new MatrixAdapter(itemCount));
    }

    private void updateUI(int N, int M) {
        updateRecyclerView(M);
        updateAdapter(N * M);
        updateMenu(true);
    }

    private void updateMenu(boolean visible) {
        if (menuItem != null)
            menuItem.setVisible(visible);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.menu_item_clear_all);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_clear_all) {
            clearUI();
            clicked.setCurrentId(R.id.textView2);
            textView2.setBackgroundResource(R.drawable.edittext_gray_style);
            return true;
        }
        return false;
    }

    private void clearUI() {
        firstRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        firstRecyclerView.setAdapter(new MatrixAdapter(0));
        secondRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        secondRecyclerView.setAdapter(new MatrixAdapter(0));
        updateMenu(false);
        textAnswer.setVisibility(View.GONE);
        textAnswer.setText("");
        operation.setVisibility(View.GONE);
        operation.setText("");
    }


    public class MatrixAdapter extends RecyclerView.Adapter<MatrixHolder> {
        private final int itemCount;

        public MatrixAdapter(int itemCount) {
            this.itemCount = itemCount;
        }

        @NonNull
        @Override
        public MatrixHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @LayoutRes int layoutRes = R.layout.list_item_text_view;

            View view = getLayoutInflater().inflate(layoutRes, parent, false);

            return new MatrixHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MatrixHolder holder, int position) {

            if (clicked.getCurrentId() == position)
                holder.setBackground(R.drawable.edittext_gray_style);
            else
                holder.setBackground(R.drawable.edittext_style);
            if (clicked.getCurrentId() < position) {
                holder.bindMatrix("  ");
            }
            else {
                double val = clicked.getCurrentMatrix().get(position);
                holder.bindMatrix(
                        val % 1 == 0 && !clicked.isDotClicked ?
                                " " + (int) val + " " :
                                " " + val + " "
                );
            }

            holder.setOnClickListener(v -> {
                int prevId = clicked.getCurrentId();
                clicked.setCurrentId(position);
                if (position < prevId)
                    notifyItemChanged(prevId);
                else for (int k = prevId; k < position; k++)
                    notifyItemChanged(k);
                holder.setBackground(R.drawable.edittext_gray_style);
            });
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }
    }


    public static class MatrixHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public MatrixHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text_view);
        }

        public void bindMatrix(String text) {
            if (textView == null) {
                Log.d(TAG, text);
                return;
            }
            textView.setText(text);
        }

        public void setBackground(@DrawableRes int resId) {
            textView.setBackgroundResource(resId);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            textView.setOnClickListener(listener);
        }
    }



//    @SuppressLint("ResourceType")
//    private void createUI() {
//
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
//        screenHeight = size.y;
//
//        int[] text = {R.string.btn_1, R.string.btn_2, R.string.btn_3, R.string.btn_multi, R.string.btn_deg, R.string.btn_4,
//                R.string.btn_5, R.string.btn_6, R.string.btn_plus, R.string.btn_minus, R.string.btn_7, R.string.btn_8, R.string.btn_9};
//
//        LinearLayout llUp = findViewById(R.id.llUp);
//        llMain = findViewById(R.id.llMain);
//
//        textView1 = findViewById(R.id.textView1);
//        textView1.setOnClickListener(v -> {
//            findViewById(id).setBackgroundResource(R.drawable.edittext_style);
//            id = R.id.textView1;
//            textView1.setBackgroundResource(R.drawable.edittext_gray_style);
//        });
//
//        textView2 = findViewById(R.id.textView2);
//        textView2.setOnClickListener(v -> {
//            findViewById(id).setBackgroundResource(R.drawable.edittext_style);
//            id = R.id.textView2;
//            textView2.setBackgroundResource(R.drawable.edittext_gray_style);
//        });
//
//        id = R.id.textView1;
//
//
////        LinearLayout.LayoutParams paramsUp = (LinearLayout.LayoutParams) llUp.getLayoutParams();
////        paramsUp.height = screenHeight / 5;
////        llUp.setLayoutParams(paramsUp);
////
////        ConstraintLayout.LayoutParams paramsMain = (ConstraintLayout.LayoutParams) llMain.getLayoutParams();
////        paramsMain.height = (int) (0.8 * (screenHeight - screenWidth));
////        paramsMain.verticalBias = (float) 0.3;
////        llMain.setLayoutParams(paramsMain);
////
////        ConstraintLayout.LayoutParams paramsDown = (ConstraintLayout.LayoutParams) llDown.getLayoutParams();
////        paramsDown.height = 4 * screenWidth / 5;
////        paramsDown.verticalBias = 1;
////        llDown.setLayoutParams(paramsDown);
//
//        bCParams = new ConstraintLayout.LayoutParams((int)
//                (25 * this.getResources().getDisplayMetrics().density), (int)
//                (25 * this.getResources().getDisplayMetrics().density));
//        bCParams.topToTop = ConstraintSet.PARENT_ID;
//        bCParams.startToStart = ConstraintSet.PARENT_ID;
//        bCParams.endToEnd = ConstraintSet.PARENT_ID;
//        bCParams.bottomToBottom = ConstraintSet.PARENT_ID;
//
//        ConstraintLayout.LayoutParams Cl_0_params = new ConstraintLayout.LayoutParams(2 * screenWidth / 5, screenWidth / 5);
//        Cl_0_params.bottomToBottom = ConstraintSet.PARENT_ID;
//        Cl_0_params.topToTop = ConstraintSet.PARENT_ID;
//        Cl_0_params.startToStart = ConstraintSet.PARENT_ID;
//        Cl_0_params.endToEnd = ConstraintSet.PARENT_ID;
//        Cl_0_params.horizontalBias = 0;
//        Cl_0_params.verticalBias = (float) 1;
//
//        ConstraintLayout.LayoutParams Cl_Dot_params = new ConstraintLayout.LayoutParams(screenWidth / 5, screenWidth / 5);
//        Cl_Dot_params.bottomToBottom = ConstraintSet.PARENT_ID;
//        Cl_Dot_params.topToTop = ConstraintSet.PARENT_ID;
//        Cl_Dot_params.startToStart = ConstraintSet.PARENT_ID;
//        Cl_Dot_params.endToEnd = ConstraintSet.PARENT_ID;
//        Cl_Dot_params.horizontalBias = (float) 0.5;
//        Cl_Dot_params.verticalBias = (float) 1;
//
//        ConstraintLayout.LayoutParams Cl_Equal_params = new ConstraintLayout.LayoutParams(screenWidth / 5, screenWidth / 5);
//        Cl_Equal_params.bottomToBottom = ConstraintSet.PARENT_ID;
//        Cl_Equal_params.topToTop = ConstraintSet.PARENT_ID;
//        Cl_Equal_params.startToStart = ConstraintSet.PARENT_ID;
//        Cl_Equal_params.endToEnd = ConstraintSet.PARENT_ID;
//        Cl_Equal_params.horizontalBias = (float) 0.75;
//        Cl_Equal_params.verticalBias = (float) 1;
//
//        ConstraintLayout.LayoutParams Cl_Enter_params = new ConstraintLayout.LayoutParams(screenWidth / 5, screenWidth / 5);
//        Cl_Enter_params.bottomToBottom = ConstraintSet.PARENT_ID;
//        Cl_Enter_params.topToTop = ConstraintSet.PARENT_ID;
//        Cl_Enter_params.startToStart = ConstraintSet.PARENT_ID;
//        Cl_Enter_params.endToEnd = ConstraintSet.PARENT_ID;
//        Cl_Enter_params.horizontalBias = (float) 1;
//        Cl_Enter_params.verticalBias = (float) 1;
//
//        ConstraintLayout.LayoutParams Cl_Clear_params = new ConstraintLayout.LayoutParams(2 * screenWidth / 5, screenWidth / 5);
//        Cl_Clear_params.bottomToBottom = ConstraintSet.PARENT_ID;
//        Cl_Clear_params.topToTop = ConstraintSet.PARENT_ID;
//        Cl_Clear_params.startToStart = ConstraintSet.PARENT_ID;
//        Cl_Clear_params.endToEnd = ConstraintSet.PARENT_ID;
//        Cl_Clear_params.horizontalBias = (float) 1;
//        Cl_Clear_params.verticalBias = (float) 0;
//
//        Button btn_0 = new Button(this);
//        btn_0.setText(R.string.btn_0);
//        btn_0.setTextSize(16);
//        btn_0.setBackgroundResource(R.drawable.button_style);
//        btn_0.setId(0);
//        btn_0.setOnClickListener(this);
//
//        Button btn_dot = new Button(this);
//        btn_dot.setText(R.string.btn_Dot);
//        btn_dot.setTextSize(20);
//        btn_dot.setTextColor(Color.WHITE);
//        btn_dot.setBackgroundResource(R.drawable.button_style_2);
//        btn_dot.setId(1);
//        btn_dot.setOnClickListener(this);
//
//        Button btn_equal = new Button(this);
//        btn_equal.setText(R.string.btn_equal);
//        btn_equal.setTextSize(16);
//        btn_equal.setTextColor(Color.WHITE);
//        btn_equal.setBackgroundResource(R.drawable.button_style_2);
//        btn_equal.setId(2);
//        btn_equal.setOnClickListener(this);
//
//        Button btn_enter = new Button(this);
//        btn_enter.setBackground(getDrawable(R.drawable.enter));
//        btn_enter.setOnClickListener(this);
//        btn_enter.setId(3);
//
//        ImageButton btn_clear = new ImageButton(this);
//        btn_clear.setBackgroundResource(R.drawable.clear2);
//        btn_clear.setId(17);
//        btn_clear.setOnClickListener(this);
//
//        for (int k = 0; k < 13; k++) {
//
//            ConstraintLayout.LayoutParams Cl_k_params = new ConstraintLayout.LayoutParams(screenWidth / 5, screenWidth / 5);
//            Cl_k_params.bottomToBottom = ConstraintSet.PARENT_ID;
//            Cl_k_params.topToTop = ConstraintSet.PARENT_ID;
//            Cl_k_params.startToStart = ConstraintSet.PARENT_ID;
//            Cl_k_params.endToEnd = ConstraintSet.PARENT_ID;
//            Cl_k_params.horizontalBias = (float) (0.25 * (k % 5));
//            Cl_k_params.verticalBias = (float) (0.67 - 0.335 * (k / 5));
//
//            Button btn_k = new Button(this);
//            btn_k.setText(text[k]);
//            btn_k.setTextSize(16);
//            btn_k.setId(k + 4);
//            btn_k.setOnClickListener(this);
//            if (k % 5 < 3) btn_k.setBackgroundResource(R.drawable.button_style);
//            else {
//                btn_k.setBackgroundResource(R.drawable.button_style_2);
//                btn_k.setTextColor(Color.WHITE);
//            }
//        }
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    private void create_matrix(int N, int M, double left_bottom, double up_bottom, int last_id) {
//
//        for (int i = 0; i < N; i++) for (int j = 0; j < M; j++) {
//            ConstraintLayout.LayoutParams etIJ = new ConstraintLayout.LayoutParams
//                    (screenWidth / 10, (int) (0.08 * (screenHeight - screenWidth)));
//
//            llMain.removeView(findViewById(last_id + 1 + M * i + j));
//
//            etIJ.topToTop = ConstraintSet.PARENT_ID;
//            etIJ.startToStart = ConstraintSet.PARENT_ID;
//            etIJ.endToEnd = ConstraintSet.PARENT_ID;
//            etIJ.bottomToBottom = ConstraintSet.PARENT_ID;
//            etIJ.horizontalBias = (float) (j * 0.124 + left_bottom);
//            etIJ.verticalBias = (float) (i * 0.115 + up_bottom);
//
//            TextView textViewI = new TextView(this);
//            textViewI.setGravity(1);
//            textViewI.setId(last_id + 1 + M * i + j);
//            textViewI.setTextSize(16);
//            textViewI.setBackgroundResource(R.drawable.edittext_style);
//            textViewI.setTextColor(Color.BLACK);
//            textViewI.setClickable(true);
//            textViewI.setFocusable(true);
//
//            id_max = last_id + 1 + M * i + j;
//
//            textViewI.setOnClickListener(v1 -> {
//                if (!((TextView) findViewById(id)).getText().toString().isEmpty()) {
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_style);
//                    id = textViewI.getId();
//                    textViewI.setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//            });
//
//            llMain.addView(textViewI, etIJ);
//        }
//    }
//
//
//
//    private void create_answer() {
//        ConstraintLayout.LayoutParams ansParams = new ConstraintLayout.LayoutParams
//                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        ansParams.topToTop = ConstraintSet.PARENT_ID;
//        ansParams.startToStart = ConstraintSet.PARENT_ID;
//        ansParams.endToEnd = ConstraintSet.PARENT_ID;
//        ansParams.bottomToBottom = ConstraintSet.PARENT_ID;
//        ansParams.verticalBias = (float) (N * 0.115 + 0.09);
//        ansParams.horizontalBias = (float) 0.3;
//
//        textAnswer = new TextView(this);
//        textAnswer.setTextSize(16);
//        textAnswer.setTextColor(Color.BLACK);
//        textAnswer.setVisibility(View.VISIBLE);
//        textAnswer.setWidth(screenWidth);
//
//        llMain.addView(textAnswer, ansParams);
//    }
//
//
//
//    private void create_operation() {
//        ConstraintLayout.LayoutParams et3Params = new ConstraintLayout.LayoutParams
//                (115, ViewGroup.LayoutParams.WRAP_CONTENT);
//        et3Params.topToTop = ConstraintSet.PARENT_ID;
//        et3Params.startToStart = ConstraintSet.PARENT_ID;
//        et3Params.endToEnd = ConstraintSet.PARENT_ID;
//        et3Params.bottomToBottom = ConstraintSet.PARENT_ID;
//        et3Params.horizontalBias = (float) (M * 0.124 + 0.05);
//        et3Params.verticalBias = (float) (((N - 1) * 0.105) / 2);
//
//        operation = new TextView(this);
//        operation.setTextSize(16);
//        operation.setVisibility(View.VISIBLE);
//        operation.setVisibility(View.VISIBLE);
//        operation.setGravity(1);
//        operation.setTextColor(Color.BLACK);
//        operation.setBackgroundResource(R.drawable.edittext_style);
//        operation.setMaxEms(1);
//        operation.setId(M * N + 18);
//        operation.setClickable(true);
//        operation.setFocusable(true);
//
//        llMain.addView(operation, et3Params);
//
//        id_max = M * N + 18;
//
//        operation.setOnClickListener(v1 -> {
//            if (!((TextView) findViewById(id)).getText().toString().isEmpty()) {
//                findViewById(id).setBackgroundResource(R.drawable.edittext_style);
//                id = operation.getId();
//                operation.setBackgroundResource(R.drawable.edittext_gray_style);
//            }
//        });
//    }
//
//
//
//
//
//    private void updateAnswer(String text, int visibility, boolean append, boolean needToClear) {
//        if (needToClear)
//            textAnswer.setText("");
//
//        if (append)
//            textAnswer.append(text);
//        else
//            textAnswer.setText(text);
//
//        textAnswer.setVisibility(visibility);
//    }
//
//
//
//    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
//    @Override
//    public void onClick(View v) throws NumberFormatException {
//
//        TextView tv = findViewById(id);
//        String text = tv.getText().toString();
//
//        double tv3_horizontal = M * 0.124 + 0.05;
//        int m2, n2;
//
//        switch (v.getId()) {
//            case -1:
//                llMain.removeAllViews();
//
//                id = R.id.textView2;
//                id_max = 17;
//                textView2.setBackgroundResource(R.drawable.edittext_gray_style);
//            case 0:
//                if ((id == R.id.textView1 || id == R.id.textView2) && !text.isEmpty() || id >= 18 && id <= M * N + 17 || id >= M * N + 19)
//                    tv.append("0");
//                break;
//            case 1:
//                if ((id >= 18 && id <= M * N + 17 || id > M * N + 19 || id == M * N + 19 && !operation.getText().equals("^")) && !text.isEmpty() && !text.startsWith("-", text.length() - 1) &&
//                        !text.startsWith(".", text.length() - 1)) tv.append(".");
//                break;
//            case 3:
//                if (!text.isEmpty()) {
//
//                    tv.setBackgroundResource(R.drawable.edittext_style);
//
//                    if (id == R.id.textView1) id = R.id.textView2;
//                    else if (id == R.id.textView2) {
//                        N = Integer.parseInt(textView1.getText().toString());
//                        M = Integer.parseInt(textView2.getText().toString());
//
//                        //remove previous matrix
//                        llMain.removeAllViews();
//
//                        //initialization first array
//                        first_matrix = new Matrix(N, M);
//
//                        //create first matrix
//                        create_matrix(N, M, 0, 0, 17);
//
//                        //create answer field
//                        create_answer();
//
//                        //create btn_clear_all button
//                        create_clear();
//
//                        id = 18;
//                    }
//                    else if (id <= N * M + 16 && isDigit(text.substring(text.length() - 1))) {
//                        double numb = 0, numb_save = 0;
//                        String sign;
//
//                        for (int i = 0; i < text.length(); i ++) {
//                            if (isDigit(String.valueOf(text.charAt(i)))) numb = 10 * numb + Double.parseDouble(String.valueOf(text.charAt(i)));
//                            else {
//                                switch (String.valueOf(text.charAt(i))) {
//                                    case "+":
//                                        numb_save += numb;
//                                        break;
//                                    case "-":
//                                        numb_save -= numb;
//                                        break;
//                                    case "*":
//                                        numb_save *= numb;
//                                        break;
//                                    case "^":
//                                        numb_save = Math.pow(numb_save, numb);
//                                        break;
//                                }
//                                numb = 0;
//                            }
//                        }
//                        //fill first matrix
//                        first_matrix.set((id - 18) / M, (id - 18) % M, Double.parseDouble(text));
//                        id ++;
//                    }
//                    else if (id == N * M + 17) {
//
//                        //create textViewOperation
//                        create_operation();
//
//                        Toast.makeText(this, "Введите операцию", Toast.LENGTH_SHORT).show();
//
//                        TextView textView = findViewById(id);
//                        first_matrix.set(
//                                N - 1, M - 1, Double.parseDouble(textView.getText().toString()));
//
//                        if (N == M) {
//                            double det = first_matrix.det();
//                            String str = "Определитель = " + det;
//
//                            if (det % 1 == 0) str = "Определитель = " + (int) det;
//
//                            updateAnswer(str, View.VISIBLE, false, false);
//                        }
//                        id++;
//                    }
//                    else if (id == N * M + 18) {
//
//                        String str = "";
//                        int visibility = View.GONE;
//
//                        switch (text) {
//                            case "+":
//                                str = "Сумма матриц    =\n     ";
//
//                                //create second matrix
//                                m2 = M;
//                                create_matrix(N, m2, tv3_horizontal + 0.2, 0,N * M + 18);
//                                break;
//                            case "-":
//                                str = "Разность матриц    =\n     ";
//
//                                //create second matrix
//                                m2 = M;
//                                create_matrix(N, m2, tv3_horizontal + 0.2,  0, N * M + 18);
//
//                                //textView1.setId(N * M + 19);
//                                //textView2.setId(N * M + 20);
//                                break;
//                            case "=":
//                                if (N == M) {
//                                    //create second matrix
//                                    m2 = 1;
//                                    create_matrix(N, m2, tv3_horizontal + 0.2, 0, N * M + 18);
//
//                                    Toast.makeText(this, "Введите свободные коэффициенты уравнений", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    Toast.makeText(this,
//                                            "К сожалению, мы не можем решить эту задачу :(", Toast.LENGTH_LONG).show();
//                                    operation.setText("");
//                                    id --;
//                                }
//                                break;
//                            case "*":
//                                textView1.setText("" + M);
//                                textView2.setText("");
//                                textView2.setId(N * M + 19);
//                                id_max = N * M + 20;
//                                Toast.makeText(this, "Введите число столбцов второй матрицы", Toast.LENGTH_LONG).show();
//                                break;
//                            case "^":
//                                if (N != M) {
//                                    str = "Только квадратные матрицы можно возводить в степень ¯\\_(ツ)_/¯";
//                                    visibility = View.VISIBLE;
//                                    operation.setText("");
//                                    id --;
//                                }
//                                else {
//                                    //create number field
//                                    create_matrix(1, 1, tv3_horizontal + 0.2,
//                                            (0.08 * (screenHeight - screenWidth) * (N - 1) + 0.115 * (N - 1)) / 2000, N * M + 18);
//                                    Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show();
//                                }
//                                break;
//                        }
//
//                        updateAnswer(str, visibility, false, true);
//                        id ++;
//                    }
//                    else if (id >  M * N + 18 && id < id_max) {
//                        switch (operation.getText().toString()) {
//                            case "-":
//                                int K = (id - N * M - 19) / M;
//                                int L = (id - N * M - 19) % M;
//                                double res = first_matrix.get(K, L) - Integer.parseInt(text);
//
//                                if (res % 1 == 0)
//                                    textAnswer.append((int) res + "     ");
//                                else textAnswer.append(res + "     ");
//
//                                if (L == M - 1) textAnswer.append("\n     ");
//
//                                break;
//                            case "+":
//                                K = (id - N * M - 19) / M;
//                                L = (id - N * M - 19) % M;
//                                res = first_matrix.get(K, L) + Integer.parseInt(text);
//
//                                if (res % 1 == 0)
//                                    textAnswer.append((int) res + "     ");
//                                else textAnswer.append(res + "     ");
//
//                                if (L == M - 1) textAnswer.append("\n     ");
//                                break;
//                            case "*":
//                                if (id == N * M + 19) {
//                                    n2 = Integer.parseInt(textView1.getText().toString());
//                                    m2 = Integer.parseInt(textView2.getText().toString());
//
//                                    create_matrix(n2, m2, tv3_horizontal + 0.2, 0, N * M + 19);
//                                    textView2.setId(R.id.textView2);
//                                }
//                                break;
//                        }
//                        id ++;
//                    }
//                    else if (id == id_max) {
//
//                        switch (operation.getText().toString()) {
//
//                            case "+":
//                                double sum = first_matrix.get(N - 1, M - 1) + Integer.parseInt(text);
//                                int len = Double.toString(sum).length() + 5;
//
//                                //remove previous number
//                                //answer.setText(answer.getText().toString().substring(0, answer.getText().length() - len));
//                                if (sum % 1 == 0)
//                                    textAnswer.append((int) sum + "     ");
//                                else textAnswer.append(sum + "     ");
//                                break;
//                            case "-":
//                                double diff = first_matrix.get(N - 1, M - 1) - Integer.parseInt(text);
//                                len = Double.toString(diff).length() + 5;
//
//                                //remove previous number
//                                //answer.setText(answer.getText().toString().substring(0, answer.getText().length() - len));
//                                if (diff % 1 == 0)
//                                    textAnswer.append((int) diff + "     ");
//                                else textAnswer.append(diff + "     ");
//                                break;
//                            case "=":
//                                LinearMatrix second_matrix = new LinearMatrix(N);
//
//                                for (int ind = M * N + 19; ind <= id_max; ind ++) {
//                                    TextView textView = findViewById(ind);
//                                    second_matrix.set(ind - N * M - 19,
//                                            Double.parseDouble(textView.getText().toString()));
//                                }
//
//                                LinearMatrix kramer = first_matrix.kramer(second_matrix);
//
//                                if (kramer == null) {
//                                    textAnswer.setText("Система не имеет решений");
//                                    break;
//                                }
//
//                                textAnswer.setText("X ∈ {(");
//
//                                for (int i = 0; i < kramer.size() - 1; i++) {
//                                    double elem = kramer.get(i);
//
//                                    if (elem % 1 == 0) textAnswer.append((int) elem + ", ");
//                                    else textAnswer.append(elem + ", ");
//                                }
//
//                                double answer = kramer.get(-1);
//                                String string;
//
//                                if (answer % 1 == 0)
//                                    string = String.valueOf((int) answer);
//                                else
//                                    string = String.valueOf(answer);
//
//                                textAnswer.append(string + ")}");
//                                break;
//                            case "*":
//                                n2 = Integer.parseInt(textView1.getText().toString());
//                                m2 = Integer.parseInt(textView2.getText().toString());
//
//                                Matrix matrix_2 = new Matrix(n2, m2);
//
//                                for (int i = M * N + 20; i <= id_max; i ++) {
//                                    int first_ind = (i - N * M - 20) / m2;
//                                    int second_ind = (i - N * M - 20) % m2;
//
//                                    TextView textView = findViewById(i);
//
//                                    matrix_2.set(
//                                            first_ind,
//                                            second_ind,
//                                            Double.parseDouble(textView.getText().toString())
//                                    );
//                                }
//
//                                first_matrix.multiBy(matrix_2);
//                                textAnswer.setText("Произведение матриц =\n");
//
//                                for (double[] i : first_matrix.getMatrix()) {
//                                    for (double j : i) {
//                                        String str;
//                                        if (j % 1 == 0)
//                                            str = String.valueOf((int) j);
//                                        else
//                                            str = String.valueOf(j);
//
//                                        textAnswer.append(str);
//                                    }
//                                    textAnswer.append("\n");
//                                }
//                                break;
//                            case "^":
//                                int numb = Integer.parseInt(text);
//
//                                textAnswer.setText("");
//
//                                if (numb == 0) textAnswer.setText("Матрица в 0 степени не определена");
//                                else if (numb < 0) {
//                                    first_matrix.inverse();
//                                    first_matrix.degreeAt(-numb);
//                                }
//                                else
//                                    first_matrix.degreeAt(numb);
//
//
//                                if (textAnswer.getText().toString().equals("")) {
//
//                                    textAnswer.setText("Матрица в " + numb + " степени =\n");
//                                    for (double[] i : first_matrix.getMatrix()) {
//                                        for (double j : i) {
//                                            String str;
//                                            if (j % 1 == 0) str = (int) j + "   ";
//                                            else str = j + "  ";
//
//                                            textAnswer.append(str);
//                                        }
//                                        textAnswer.append("\n");
//                                    }
//                                }
//
//                                break;
//                        }
//                        textAnswer.setVisibility(View.VISIBLE);
//                    }
//
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                break;
//            case 4:
//            case 5:
//            case 6:
//            case 9:
//            case 10:
//            case 11:
//            case 14:
//            case 15:
//            case 16:
//                if (id != M * N + 18) {
//                    String numb = ((Button) findViewById(v.getId())).getText().toString();
//                    tv.append(numb);
//                }
//                break;
//            case 2:
//            case 7:
//            case 8:
//            case 12:
//                if (id == M * N + 18) {
//                    String operation = ((Button) findViewById(v.getId())).getText().toString();
//                    tv.setText(operation);
//                }
//                else if (id >= 18 && !text.isEmpty() && isDigit(text.substring(text.length() - 1))) {
//                    String operation = ((Button) findViewById(v.getId())).getText().toString();
//                    tv.append(operation);
//                }
//                break;
//            case 13:
//                if (id >= 18 && text.isEmpty()) tv.setText("-");
//                break;
//            case 17:
//                if (!text.isEmpty()) tv.setText(text.substring(0, text.length() - 1));
//
//                else if (id == R.id.textView2) {
//                    tv.setBackgroundResource(R.drawable.edittext_style);
//                    id = R.id.textView1;
//                    textView1.setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                else if (id == 18) {
//                    tv.setBackgroundResource(R.drawable.edittext_style);
//                    id = R.id.textView2;
//                    llMain.removeAllViews();
//                    id_max = 17;
//                    textView2.setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                else if (id > 18 && id < N * M + 18) {
//                    tv.setBackgroundResource(R.drawable.edittext_style);
//                    id --;
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                else if (id == N * M + 18) {
//
//                    //remove textview3 field
//                    llMain.removeView(operation);
//                    id --;
//                    id_max = id;
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                else if (id == N * M + 19) {
//
//                    //remove previous views
//                    for (int elem = N * M + 19; elem <= id_max; elem ++)
//                        llMain.removeView(findViewById(elem));
//
//                    id --;
//                    id_max = id;
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_gray_style);
//                    textAnswer.setVisibility(View.GONE);
//                }
//                else if (id > N * M + 19 && id != R.id.textView1) {
//                    tv.setBackgroundResource(R.drawable.edittext_style);
//                    id --;
//                    findViewById(id).setBackgroundResource(R.drawable.edittext_gray_style);
//                }
//                break;
//        }
//    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        @StringRes
        int buttonTextRes = keyboard.getButtonText(v.getId());

        switch (v.getId()) {
            case R.id.textView1:
            case R.id.textView2:
            case R.id.textViewOperation:
                if (clicked.isMatrix()) {
                    int id = clicked.getCurrentId();
                    clicked.setCurrentId(v.getId());
                    Objects.requireNonNull(firstRecyclerView.getAdapter())
                            .notifyItemChanged(id);
                } else {
                    findViewById(clicked.getCurrentId())
                            .setBackgroundResource(R.drawable.edittext_style);
                    clicked.setCurrentId(v.getId());
                }

                findViewById(clicked.getCurrentId()).setBackgroundResource(R.drawable.edittext_gray_style);
                break;

            case Keyboard.ID_0:
            case Keyboard.ID_1:
            case Keyboard.ID_2:
            case Keyboard.ID_3:
            case Keyboard.ID_4:
            case Keyboard.ID_5:
            case Keyboard.ID_6:
            case Keyboard.ID_7:
            case Keyboard.ID_8:
            case Keyboard.ID_9:
                clicked.appendDigit(getString(buttonTextRes));
                if (clicked.isMatrix()) {
                    Objects.requireNonNull(currentRecyclerView().getAdapter())
                            .notifyItemChanged(clicked.getCurrentId());
                }
                break;

//            case Keyboard.ID_DOT:
////            case Keyboard.ID_MINUS:
////            case Keyboard.ID_DIV:
//                clicked.appendSpecSymbol(buttonTextRes);
//                break;

            case Keyboard.ID_PLUS:
            case Keyboard.ID_MINUS:
            case Keyboard.ID_MULTI:
            case Keyboard.ID_DEG:
            case Keyboard.ID_EQUAL:
                clicked.appendOperation(buttonTextRes);
                break;

            case Keyboard.ID_GO:
                clicked.go();
                if (clicked.positionInArray() == 2)
                    updateUI(clicked.getN(), clicked.getM());

                else if (clicked.getCurrentId() == 0)
                    updateUI(clicked.getM2(), clicked.getN2());

                else if (clicked.isMatrix()) {
                    Objects.requireNonNull(currentRecyclerView().getAdapter())
                            .notifyItemChanged(clicked.getCurrentId() - 1);
                    currentRecyclerView().getAdapter().notifyItemChanged(clicked.getCurrentId());

                    updateAnswer();
                }
                else if (clicked.isOperationView()) {
                    operation.setVisibility(View.VISIBLE);
                    operation.setText("  ");
                    Objects.requireNonNull(firstRecyclerView.getAdapter())
                            .notifyItemChanged(clicked.getN() * clicked.getM() - 1);
                    updateAnswer();
                }

                if (clicked.isSecondMatrix()) {
                    if (OnKeyClicked.WARNING)
                        textAnswer.setText("К сожалению, мы не можем решить данную задачу :(");
                    else
                        textAnswer.setText(clicked.getStringAnswer());

                    textAnswer.setVisibility(View.VISIBLE);
                }
                break;

                case Keyboard.ID_CLEAR:

                    if (clicked.isOperationView() && operation.getText().equals(""))
                        operation.setVisibility(View.GONE);

                    clicked.back();
                    if (clicked.isMatrix()) {
                        Toast.makeText(this, "проверка = " + clicked.positionInArray(), Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(currentRecyclerView().getAdapter())
                                .notifyItemChanged(clicked.getCurrentId());
                        if (!clicked.isLastIndex())
                            currentRecyclerView().getAdapter().notifyItemChanged(clicked.getCurrentId() + 1);
                    }
                    if (clicked.isOperationView() || clicked.positionInArray() < 2) {
                        updateUI(0, 1);
                    }

                    if ((clicked.getCurrentId() == R.id.textView2 || clicked.getCurrentId() == R.id.textView1)
                            && Objects.requireNonNull(firstRecyclerView.getAdapter()).getItemCount() == 0)
                        clearUI();
                    break;
        }
    }

    private void findDeterminant() {
        double det = clicked.getFirstMatrix().det();
        if (det % 1 == 0)
            textAnswer.setText(getString(R.string.determinant_int, (int) det));
        else
            textAnswer.setText(getString(R.string.determinant_double, det));
        textAnswer.setVisibility(View.VISIBLE);
    }

    private void updateAnswer() {
        if (clicked.isSecondMatrix()) {
            textAnswer.setText("");
            textAnswer.setVisibility(View.GONE);
        } else {
            findDeterminant();
        }
    }
}