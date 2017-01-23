package com.github.egorn.syncgenerator.compiler;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class GenerateSyncProcessorTest {
    @Test
    public void generateSyncForClass() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("Class.java"))
                .processedWith(new GenerateSyncProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("SyncClass.java"));
    }

    @Test
    public void generateSyncForInterface() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("Interface.java"))
                .processedWith(new GenerateSyncProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("SyncInterface.java"));
    }

    @Test
    public void throwForInnerStaticClass() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("InnerStaticClass.java"))
                .processedWith(new GenerateSyncProcessor())
                .failsToCompile()
                .withErrorContaining("Inner classes cannot be annotated with @GenerateSync");
    }

    @Test
    public void throwForFinalClass() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("FinalClass.java"))
                .processedWith(new GenerateSyncProcessor())
                .failsToCompile()
                .withErrorContaining("Final classes cannot be annotated with @GenerateSync");
    }

    @Test
    public void throwForNotAClassOrAnInterface() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("NotAClassOrAnInterface.java"))
                .processedWith(new GenerateSyncProcessor())
                .failsToCompile()
                .withErrorContaining("Only classes and interfaces can be annotated with @GenerateSync");
    }
}