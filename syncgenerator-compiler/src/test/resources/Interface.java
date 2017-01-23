package com.example.syncgenerator;

import com.github.egorn.syncgenerator.GenerateSync;

import java.io.IOException;

@GenerateSync
interface Interface {
    void method1(String s);
    Boolean method2(Integer i1, Integer i2) throws IOException;
}