package com.example.matrix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.ArrayList;

public class OnKeyClicked {

    public static boolean WARNING = false;

    private static final int OPERATION_VIEW = R.id.textViewOperation;

    private final ArrayList<Integer> id = new ArrayList<>();
    public boolean isDotClicked = false;
    private final Activity mActivity;
    private int currentId;
    private int currentPosInArr;
    private Matrix first_matrix;
    private Matrix second_matrix;
    private LinearMatrix linearMatrix;
    private int N, M, N2, M2;
    private @StringRes int operation;

    public OnKeyClicked(Activity activity) {
        this.mActivity = activity;
        updateID();
        currentId = R.id.textView1;
        currentPosInArr = 0;
        N = 0;
        M = 0;
        N2 = 0;
        M2 = 0;
    }

    public Matrix getFirstMatrix() {
        return first_matrix;
    }

    public Matrix getCurrentMatrix() {
        return isSecondMatrix() ? second_matrix : first_matrix;
    }

    public void appendDigit(String digit) {
        if (!digit.matches("\\d+")) return;

        TextView textView = getCurrentTextView();
        if (textView == null)
            getCurrentMatrix().appendToElem(currentId, Double.parseDouble(digit));
        else
            textView.append(digit);
    }

    //вызывается, если была нажата кнопка "/", ".", "-"
    public void appendSpecSymbol(@StringRes int symbol) {
//        TextView textView = getCurrentTextView();
//        if (textView == null) {
//            return;
//        }
        if (first_matrix != null) {
            isDotClicked = true;
            first_matrix.appendSpecSymbol(currentId, symbol);
        }

//        switch (symbol) {
//            case "-":
//                textView.setText(symbol);
//                break;
//            case ".":
//            case "/":
//                if (!textView.getText().equals("") &&
//                        textView.getText().toString().matches("\\d+"))
//                    textView.append(symbol);
//                break;
//        }
    }

    public void appendOperation(@StringRes int operationRes) {

        if (!isOperationView()) return;
        if (operationRes != R.string.btn_plus && operationRes != R.string.btn_minus &&
                operationRes != R.string.btn_multi && operationRes != R.string.btn_deg) return;

        getCurrentTextView().setText(operationRes);
    }



    public void go() {
        if (!updateInfo()) return;

        if (isMatrix() && getCurrentMatrix().get(currentId) == 0)
            appendDigit("0");

        if (isLastIndex() && !id.contains(OPERATION_VIEW))
            this.id.add(OPERATION_VIEW);

        else if (isLastIndex()) {
            if (!findAnswer())
                WARNING = true;
            return;
        }

        isDotClicked = false;
        nextView(1);
    }

    public void back() {
        TextView textView = getCurrentTextView();

        if (textView == null) {
            if (first_matrix.deleteOneSymbol(currentId)) return;

            if (currentPosInArr == 2) {
                first_matrix = null;
                nextView(-1);
                updateID();
                return;
            }
            nextView(-1);

            if (isOperationView()) {
                second_matrix = null;
                id.removeAll(id.subList(currentPosInArr + 1, id.size()));
                ((TextView) mActivity.findViewById(R.id.textView1)).setText(String.valueOf(N));
            }
            return;
        }

        String text = textView.getText().toString();

        if (text.equals("")) {
            nextView(-1);
            return;
        }
        text = text.substring(0, text.length() - 1);
        textView.setText(text);
    }


    public boolean isFirstMatrix() {
        return currentPosInArr > 1 &&
                    equalWithOperation(false);
    }

    public boolean isSecondMatrix() {
        return id.contains(OPERATION_VIEW) && equalWithOperation(true);
    }

    public boolean isMatrix() {
        return isFirstMatrix() || isSecondMatrix();
    }

    public boolean isOperationView() {
        return currentId == OPERATION_VIEW;
    }

    //если true, то проверяет >, если false - наоборот
    private boolean equalWithOperation(boolean much) {
        if (id.contains(OPERATION_VIEW))
            return currentPosInArr > id.indexOf(OPERATION_VIEW) && much ||
                    currentPosInArr < id.indexOf(OPERATION_VIEW) && !much;

        return true;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int newId) {
        this.currentId = newId;
        this.currentPosInArr = id.lastIndexOf(this.currentId);
        if (this.currentPosInArr == -1) return;

        if (getCurrentMatrix() != null && isMatrix())
            getCurrentMatrix().setNewPosition(newId);
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }

    public int getN2() {
        return N2;
    }

    public int getM2() {
        return M2;
    }

    public boolean isLastIndex() {
        return currentPosInArr == id.size() - 1;
    }

    public int positionInArray() {
        return currentPosInArr;
    }


    @SuppressLint("NonConstantResourceId")
    public String getStringAnswer() {
        StringBuilder answer = new StringBuilder();
        String type = "";
        switch (operation) {
            case R.string.btn_plus:
                type = "Сумма";
                break;
            case R.string.btn_minus:
                type = "Разность";
                break;
            case R.string.btn_multi:
                type = "Произведение";
                break;
            case R.string.btn_deg:
                answer.append(mActivity.getString(R.string.degMatrix, (int) second_matrix.get(0)))
                .append(first_matrix.toString());
                break;
            case R.string.btn_equal:
                answer.append("X ∈ {(").append(linearMatrix.toString()).append(")}");
                break;
        }
        if (!type.equals("")) {
            answer.append(mActivity.getString(R.string.sumAndSub, type))
                    .append(first_matrix.toString());
        }
        return answer.toString();
    }


    @Nullable
    private TextView getCurrentTextView() {
        return (TextView) mActivity.findViewById(currentId);
    }

    private void updateCurrentTextView(boolean gray) {
        TextView textView = getCurrentTextView();
        if (textView == null) return;

        if (gray)
            textView.setBackgroundResource(R.drawable.edittext_gray_style);
        else
            textView.setBackgroundResource(R.drawable.edittext_style);
    }

    private boolean updateInfo() {
        TextView textView = getCurrentTextView();
        if (textView == null) return true;

        String text = textView.getText().toString();
        if (!checkText(text)) return false;

        if (currentId == R.id.textView1) {
            N = Integer.parseInt(text);
            if (N == 0) {
                N = 1;
                textView.setText("1");
            }
        } else if (currentId == R.id.textView2 && currentPosInArr == 1) {
            M = Integer.parseInt(text);
            if (M == 0) {
                M = 1;
                textView.setText("1");
            }
            first_matrix = new Matrix(N, M);
            updateID();

            for (int id = 0; id < N * M; id++)
                this.id.add(id);
        }
        else if (currentId == R.id.textView2) {
            M2 = Integer.parseInt(text);
            if (M2 == 0) {
                M2 = 1;
                textView.setText("1");
            }

            second_matrix = new Matrix(N2, M2);

            for (int id = 0; id < N2 * M2; id++)
                this.id.add(id);
        }
        else if (currentId == OPERATION_VIEW)
            setOperation(text);
//        else
//            first_matrix.set(currentId, Double.parseDouble(text));

        return true;
    }

    private void updateID() {
        id.clear();
        id.add(R.id.textView1);
        id.add(R.id.textView2);
    }

    private void nextView(int next) {
        this.currentPosInArr = currentPosInArr + next;
        if (currentPosInArr < 0 || currentPosInArr >= id.size()) return;

        updateCurrentTextView(false);
        currentId = id.get(currentPosInArr);
        updateCurrentTextView(true);
    }

    private boolean checkText(String text) {
        return !text.equals("");
    }

    private void setOperation(String operation) {
        switch (operation) {
            case "+":
            case "—":

                if (operation.equals("+"))
                    this.operation = R.string.btn_plus;
                else
                    this.operation = R.string.btn_minus;

                N2 = N;
                M2 = M;
                second_matrix = new Matrix(N2, M2);

                for (int i = 0; i < N2 * M2; i++)
                    this.id.add(i);
                break;
            case "*":
                this.id.add(R.id.textView2);
                N2 = M;
                ((TextView) mActivity.findViewById(R.id.textView1)).setText(String.valueOf(N2));
                this.operation = R.string.btn_multi;
                break;
            case "^":
                N2 = 1;
                M2 = 1;
                second_matrix = new Matrix(N2, M2);
                this.operation = R.string.btn_deg;
                break;
            case "=":
                N2 = 1;
                M2 = M;
                second_matrix = new LinearMatrix(M2);
                this.operation = R.string.btn_equal;
                break;
            default:
                Toast.makeText(mActivity, "Введенная операция не может быть выполнена!",
                        Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private boolean findAnswer() {
        switch (operation) {
            case R.string.btn_plus:
                return first_matrix.sumWith(second_matrix);
            case R.string.btn_minus:
                return first_matrix.subtract(second_matrix);
            case R.string.btn_multi:
                return first_matrix.multiBy(second_matrix);
            case R.string.btn_deg:
                int power = (int) second_matrix.get(0);
                if (power == 0)
                    return false;
                else if (power < 0) {
                    if (!first_matrix.inverse())
                        return false;

                    return first_matrix.degreeAt(-power);
                } else return first_matrix.degreeAt(power);
            case R.string.btn_equal:
                linearMatrix = first_matrix.kramer((LinearMatrix) second_matrix);
                return linearMatrix != null;
            default:
                return false;
        }
    }
}
