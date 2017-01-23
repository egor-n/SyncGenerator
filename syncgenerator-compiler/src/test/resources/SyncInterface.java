package com.example.syncgenerator;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;

class SyncInterface implements Interface {
    final Interface wrapped;

    public SyncInterface(Interface wrapped) {
        this.wrapped = wrapped;
    }

    public synchronized Boolean method2(Integer i1, Integer i2) throws IOException {
        return wrapped.method2(i1, i2);
    }

    public synchronized void method1(String s) {
        wrapped.method1(s);
    }
}