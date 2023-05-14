package com.manech.manechapp.model;

import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {


    private static Executor getExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        Executor executor = getExecutor();
        return new ViewModelFactory(executor, context);
    }
}