package com.example.syncgenerator;

import com.github.egorn.syncgenerator.GenerateSync;

import java.io.IOException;

@GenerateSync
abstract class Class {
    abstract String s(Boolean b, Integer i);

    final void f() {
    }

    Double d() throws IOException {
        return null;
    }
}
