package com.example.syncgenerator;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Runnable;
import java.lang.String;

class SyncClass extends Class implements Runnable {
    final Class wrapped;

    public SyncClass(Class wrapped) {
        this.wrapped = wrapped;
    }

    synchronized String s(Boolean b, Integer i) {
        return wrapped.s(b, i);
    }

    public synchronized void run() {
        wrapped.run();
    }

    synchronized Double d() throws IOException {
        return wrapped.d();
    }
}