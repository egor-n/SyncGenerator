package com.example.syncgenerator;

public class Main {
    public static void main(String[] args) {
        new SyncClass(new Class() {
            String s(Boolean b, Integer i) {
                return null;
            }
        });

        new SyncInterface(new Interface() {
            public Boolean interfaceMethodWithReturn() {
                return null;
            }
        });
    }
}
