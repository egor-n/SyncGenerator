package com.example.syncgenerator;

import com.github.egorn.syncgenerator.GenerateSync;

import java.io.IOException;

class Class {
    @GenerateSync
    abstract static class InnerStaticClass implements Runnable {
        int i;

        abstract String s(Boolean b, Integer i);

        final void f() {
        }

        Double d() throws IOException {
            return null;
        }
    }
}