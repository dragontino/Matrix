package com.example.matrix;

import org.jetbrains.annotations.NotNull;

public class LinearMatrix extends Matrix {

    private final int length;

    public LinearMatrix(int length) {
        super(length, 1);
        this.length = length;
    }

//    public LinearMatrix(int length, double value) {
//        this(length);
//
//        for (int i = 0; i < length; i++)
//            set(i, value);
//    }

    public int size() {
        return length;
    }

    @Override
    public double get(int position) {
        return super.get(position, 0);
    }

    @Override
    public void set(int index, double value) {
        super.set(index, 0, value);
    }

    @Override
    public @NotNull String toString() {
        return super.toString(", ");
    }

    //    public void setSize(int newSize) {
//        super.setSize(newSize, 1);
//        this.length = newSize;
//    }
}
