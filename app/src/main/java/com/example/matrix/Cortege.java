package com.example.matrix;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Cortege<A, B> {
    private final ArrayList<A> first;
    private final ArrayList<B> second;
    private int size;

    public Cortege() {
        this(null, null);
    }

    public Cortege(A first, B second) {
        this.first = new ArrayList<>();
        this.second = new ArrayList<>();
        this.size = 0;
        set(first, second);
    }

    public A getFirstValue() {
        return getFirstValue(size - 1);
    }

    public A getFirstValue(int index) {
        if (index >= size) index = size - 1;
        return first.get(index);
    }

    public B getSecondValue() {
        return getSecondValue(size - 1);
    }

    public B getSecondValue(int index) {
        if (index >= size) index = size - 1;
        return second.get(index);
    }

    public Cortege<A, B> get(int position) {

        A a = getFirstValue(position);
        B b = getSecondValue(position);

        return new Cortege<>(a, b);
    }

    public void set(A first, B second) {
        if (size == 0)
            add(first, second);
        else
            set(size - 1, first, second);
    }

    public void set(int index, A first, B second) {
        if (index >= size) {
            updateSize(index + 1, first, second);
            return;
        }
        this.first.set(index, first);
        this.second.set(index, second);
    }

    public void setFirstValue(A first) {
        set(first, getSecondValue());
    }

    public void setFirstValue(int index, A first) {
        set(index, first, getSecondValue(index));
    }

    public void setSecondValue(int index, B second) {
        set(index, getFirstValue(index), second);
    }

    public void setSecondValue(B second) {
        set(getFirstValue(), second);
    }

    public void add(A first, B second) {
        if (size == 1 && (getFirstValue() == null || getSecondValue() == null)) {
            set(first, second);
            return;
        }
        this.first.add(first);
        this.second.add(second);
        size++;
    }

    private void updateSize(int newSize, A first, B second) {
        for (int i = size; i < newSize; i++)
            add(first, second);
    }

    public void clear() {
        set(null, null);
    }

    //если size == 1, то удаляется first или second
    public void clear(int index) {
        if (size == 1) {
            if (index == 0)
                setFirstValue(null);
            else
                setSecondValue(null);
        }
        else set(index, null, null);
    }

    public void clearAll() {
        for (int i = 0; i < size; i++)
            clear(i);
    }

    //возвращает количество ненулевых элементов по заданному индексу
    public int size(int index) {
        int size = 0;
        if (first.get(index) != null) size++;
        if (second.get(index) != null) size++;
        return size;
    }

    //возвращает количество ненулевых элементов наверху списка
    public int size() {
        return size(size - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public @NotNull String toString() {

        String[] strings = new String[size];

        for (int i = 0; i < size; i++)
            strings[i] = "Cortege №" + (i + 1) +
                    " {" + getFirstValue(i) + ", " + getSecondValue(i) + "}";

        return String.join("\n", strings);
    }

    public String toString(int what) {
        return toString(size - 1, what);
    }

    public String toString(int index, int what) {
        if (what == 0)
            return String.valueOf(getFirstValue(index));
        else
            return String.valueOf(getSecondValue(index));
    }
}
