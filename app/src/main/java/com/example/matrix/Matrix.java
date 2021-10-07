package com.example.matrix;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Matrix {

    private double [][] matrix;
    private int weight;
    private int width;
    private int height;
    private Cortege<Integer, Integer> coefficients;

    public static boolean isDigit(String digit) {
        try {
            Double.parseDouble(digit);
            return true;
        }
        catch (NumberFormatException e) {
            Log.i("Matrix.IsDigit", "value " + digit + " isn't a number!");
            return false;
        }
    }

    public static boolean isDigit(Context context, @StringRes int stringRes) {
        String string = context.getString(stringRes);
        return isDigit(string);
    }

    public static double log(double x, double a) {
        //return logarithm (base a) of x
        return Math.log(x) / Math.log(a);
    }



    public Matrix(int height, int width) {
        matrix = new double[height][width];
        weight = 0;
        this.height = height;
        this.width = width;
        this.coefficients = new Cortege<>();
    }

    public Matrix(Matrix matrix) {
        setMatrix(matrix);
    }

    public void add(double value) {
        if (weight >= width * height) return;

        coefficients.add(10, 1);

        int i = weight / width;
        int j = weight % width;
        weight++;

        matrix[i][j] = value;
    }

    public void appendToElem(int position, double value) {
        if (position >= weight || weight == 0) {
            set(position, value);
            setNewPosition(position);
            return;
        }

        double elem = get(position);
        int first = coefficients.getFirstValue(position);
        int second = coefficients.getSecondValue(position);
        set(position, elem * first + value / second);

        if (second != 1)
            coefficients.setSecondValue(position, second * 10);
    }

    public void appendSpecSymbol(int position, @StringRes int stringId) {
        if (position >= weight || weight == 0) {
            if (stringId == R.string.btn_minus)
                add(-1);
            return;
        }
        if (stringId == R.string.btn_dot) {
            if (get(position) == 0) return;
            coefficients.set(position, 1, 10);
        }
    }

    public boolean deleteOneSymbol(int position) {
        double value = get(position);
        if (value == 0) return false;

        value /= 10;
        set(position, (int) value);
        return true;
    }

    public void setNewPosition(int position) {
        if (position >= weight || weight == 0) {
            weight = position + 1;
            coefficients.set(position, 10, 1);
        }
    }

//    public void setSize(int newHeight, int newWidth) {
//        if (newHeight == height && newWidth == width || newHeight <= 0 || newWidth <= 0) return;
//
//        double[][] newMatrix = new double[newHeight][newWidth];
//        int weight = 0;
//
//        for (int i = 0; i < newHeight; i++)
//            for (int j = 0; j < newWidth; j++)
//                if (i >= matrix.length || j >= matrix[0].length)
//                    newMatrix[i][j] = 0;
//                else {
//                    newMatrix[i][j] = this.matrix[i][j];
//                    weight++;
//                }
//        this.matrix = newMatrix;
//        this.weight = weight;
//        this.height = newHeight;
//        this.width = newWidth;
//    }


    public void set(int i, int j, double value) {

        if (i >= height || j >= width || i < 0 || j < 0) return;

        matrix[i][j] = value;
    }

    public void set(int position, double value) {
        int i = position / width;
        int j = position % width;

        set(i, j, value);
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix.matrix;
        this.width = matrix.width;
        this.height = matrix.height;
        this.weight = matrix.weight;
        this.coefficients = matrix.coefficients;
    }

    public double get(int position) {
        int i = position / width;
        int j = position % width;

        return get(i, j);
    }

    public double get(int i, int j) {

        if (i < 0) i = height + i;
        else if (j < 0) j = width + j;

        i = Math.abs(i) % height;
        j = Math.abs(j) % width;

        return matrix[i][j];
    }

    public int getWeight() {
        return weight;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public boolean equals(Matrix matrix) {
        if (this.width != matrix.width || this.height != matrix.height || this.weight != matrix.weight)
            return false;

        for (int i = 0; i < this.weight; i++)
            if (this.get(i) != matrix.get(i)) return false;

        return true;
    }



    public double det() {
        if (width != height) return 0;
        if (matrix.length == 1) return matrix[0][0];

        double ans = 0;
        for (int k = 0; k < matrix.length; k ++) {
            Matrix new_A = new Matrix(matrix.length - 1, matrix.length - 1);
            int i_1 = 0, j_1 = 0;

            for (int i = 1; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++)
                    if (j != k) {
                        new_A.set(i_1, j_1, matrix[i][j]);
                        j_1++;
                    }

                i_1 ++;
                j_1 = 0;
            }
            ans += matrix[0][k] * (Math.pow(-1, k) * new_A.det());
        }
        return ans;
    }


    public boolean sumWith(Matrix matrix) {
        return unite(true, matrix);
    }

    public boolean subtract(Matrix matrix) {
        return unite(false, matrix);
    }


    public boolean multiBy(Matrix matrixAt) {
        if (this.width != matrixAt.height) return false;

        Matrix result = new Matrix(this.height, matrixAt.width);
        int s = 0;

        for (int i = 0; i < this.height; i ++) for (int j = 0; j < matrixAt.width; j ++) {
            for (int i1 = 0; i1 < this.width; i1++)
                s += this.get(i, i1) * matrixAt.get(i1, j);

            result.add(s);
            s = 0;
        }
        this.setMatrix(result);

        return true;
    }

    public void squareUp() {
        multiBy(this);
    }

    @Nullable
    public LinearMatrix kramer(LinearMatrix free_coefficients) {

        int N = Math.min(height, free_coefficients.size());

        LinearMatrix result = new LinearMatrix(N);
        double detA = this.det();

        if (detA == 0) return null;

        for (int i = 0; i < N; i++) {
            Matrix I = new Matrix(N, N);

            for (int j = 0; j < N; j ++) for (int e = 0; e < N; e ++) {
                    if (e == i)
                        I.set(j, e, free_coefficients.get(j));
                    else
                        I.set(j, e, this.get(j, e));
            }
            result.set(i, I.det() / detA);
        }

        return result;
    }


    public boolean inverse() {
        Matrix exitMatrix = new Matrix(this);
        double det_m = this.det();
        int x = 0, y = 0;

        if (height == 1 || det_m == 0 || height != width) return false;

        for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) {

            Matrix mas = new Matrix(height - 1, width - 1);

            for (int t = 0; t < height; t++)
                if (t != i) for (int q = 0; q < height; q++)
                    if (q != j) {
                        mas.set(x, y, this.get(t, q));
                        if (y == height - 2) {
                            x++;
                            y = 0;
                        } else y++;
                    }
            x = y = 0;
            double m = mas.det();
            double ind = Math.pow(-1, i + j) * m;

            exitMatrix.set(j, i, ind / det_m);
        }

        this.setMatrix(exitMatrix);
        return true;
    }


    public boolean degreeAt(int pow) {
        if (pow <= 1 || height != width) return false;

        if (log(pow, 2) % 1 == 0) {
            if (!this.degreeAt(pow / 2)) return false;
            this.squareUp();
        }
        else {
            String bin = new StringBuffer(Integer.toBinaryString(pow)).reverse().toString();
            ArrayList<Matrix> matrixList = new ArrayList<>();
            ArrayList<Integer> powList = new ArrayList<>();

            for (int i = 0; i < bin.length(); i ++) {
                if (bin.charAt(i) == 49 && matrixList.size() == 0) {

                    Matrix nextMatrix = new Matrix(this);
                    nextMatrix.degreeAt((int) Math.pow(2, i));
                    matrixList.add(nextMatrix);
                    powList.add((int) Math.pow(2, i));
                }
                else if (bin.charAt(i) == 49) {
                    Matrix nextMatrix = matrixList.get(matrixList.size() - 1);
                    int nextPow = (int) (Math.pow(2, i) / powList.get(powList.size() - 1));

                    nextMatrix.degreeAt(nextPow);
                    matrixList.add(nextMatrix);
                    powList.add((int) Math.pow(2, i));
                }
            }
            Matrix ans = matrixList.get(0);
            for (int k = 1; k < matrixList.size(); k ++)
                ans.multiBy(matrixList.get(k));

            this.setMatrix(ans);
        }
        return true;
    }

    public @NotNull String toString() {
        return toString("   ");
    }


    private boolean unite(boolean plus, Matrix matrix) {
        if (this.width != matrix.width || this.height != matrix.height)
            return false;

        for (int i = 0; i < this.height; i++)
            for (int j = 0; j < this.width; j++) {
                double value = this.get(i, j);
                if (plus)
                    value += matrix.get(i, j);
                else
                    value -= matrix.get(i ,j);

                this.set(i, j, value);
            }

        return true;
    }

    protected  @NotNull String toString(String sep) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                double value = get(i, j);

                if (value % 1 == 0) answer.append((int) value);
                else answer.append(value);

                answer.append(sep);
            }
            answer.append("\n");
        }
        return answer.toString();
    }
}
