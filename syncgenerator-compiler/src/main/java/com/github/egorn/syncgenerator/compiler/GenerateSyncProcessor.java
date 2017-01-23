package com.github.egorn.syncgenerator.compiler;

import com.github.egorn.syncgenerator.GenerateSync;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.egorn.syncgenerator.GenerateSync")
public final class GenerateSyncProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Collection<? extends Element> annotatedElements =
                    roundEnv.getElementsAnnotatedWith(GenerateSync.class);
            for (Element element : annotatedElements) {
                if (element.getKind() != ElementKind.CLASS
                        && element.getKind() != ElementKind.INTERFACE) {
                    throw new ProcessingException(element, "Only classes and interfaces can be annotated with @%s",
                            GenerateSync.class.getSimpleName());
                }
                if (element.getModifiers().contains(Modifier.FINAL)) {
                    throw new ProcessingException(element, "Final classes cannot be annotated with @%s",
                            GenerateSync.class.getSimpleName());
                }

                processType(new AnnotatedType((TypeElement) element));
            }
        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            error(null, e.getMessage());
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void processType(AnnotatedType type) throws IOException {
        type.createDecoratorClass(processingEnv.getTypeUtils(), processingEnv.getFiler());
    }

    /**
     * Prints an error message
     *
     * @param e   The element which has caused the error. Can be null
     * @param msg The error message
     */
    private void error(Element e, String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
