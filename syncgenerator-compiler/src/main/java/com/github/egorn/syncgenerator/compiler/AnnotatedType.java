package com.github.egorn.syncgenerator.compiler;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.*;

class AnnotatedType {
    private final TypeElement element;
    private final String decoratorClassName;
    private final String originalClassName;

    AnnotatedType(TypeElement element) {
        this.element = element;
        this.decoratorClassName = generatedClassName(element, "Sync");
        this.originalClassName = element.getSimpleName().toString();
    }

    void createDecoratorClass(Types typeUtils, Filer filer) throws IOException {
        writeSourceFile(filer, TypeUtils.packageNameOf(element), generateCode(typeUtils));
    }

    private TypeSpec generateCode(Types typeUtils) {
        String fieldName = "wrapped";

        String pkg = TypeUtils.packageNameOf(element);
        TypeName classTypeName = ClassName.get(pkg, originalClassName);

        Set<MethodSpec> methods = new HashSet<>();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(classTypeName, fieldName)
                .addStatement("this.$N = $N", fieldName, fieldName)
                .build();

        methods.add(constructor);

        Set<Element> allElements = new HashSet<>();
        allElements.addAll(element.getEnclosedElements());
        for (TypeMirror typeMirror : element.getInterfaces()) {
            allElements.addAll(typeUtils.asElement(typeMirror).getEnclosedElements());
        }

        for (Element e : allElements) {
            if (!(e instanceof ExecutableElement)) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) e;
            String methodName = method.getSimpleName().toString();
            if ("<init>".equals(methodName)) { // skip constructors
                continue;
            }
            Set<Modifier> modifiers = new HashSet<>(method.getModifiers());
            if (modifiers.contains(Modifier.PRIVATE)
                    || modifiers.contains(Modifier.STATIC)
                    || modifiers.contains(Modifier.FINAL)) {
                continue;
            }
            modifiers.remove(Modifier.ABSTRACT);
            modifiers.add(Modifier.SYNCHRONIZED);

            MethodSpec.Builder spec = MethodSpec.methodBuilder(methodName)
                    .addModifiers(modifiers);

            List<? extends TypeMirror> thrownTypes = method.getThrownTypes();
            for (TypeMirror throwable : thrownTypes) {
                spec = spec.addException(TypeName.get(throwable));
            }

            String arguments = "";
            List<? extends VariableElement> parameters = method.getParameters();
            for (VariableElement parameter : parameters) {
                arguments += parameter.getSimpleName().toString();
                if (parameters.indexOf(parameter) != parameters.size() - 1) {
                    arguments += ", ";
                }
                spec.addParameter(ParameterSpec.get(parameter));
            }

            if (method.getReturnType() instanceof NoType) {
                spec = spec.addStatement("$N.$N($L)", fieldName, methodName, arguments);
            } else {
                spec = spec.addStatement("return $N.$N($L)", fieldName, methodName, arguments)
                        .returns(TypeName.get(method.getReturnType()));
            }

            methods.add(spec.build());
        }

        Set<Modifier> classModifiers = new HashSet<>(element.getModifiers());
        classModifiers.remove(Modifier.ABSTRACT);

        return TypeSpec.classBuilder(TypeUtils.simpleNameOf(decoratorClassName))
                .addModifiers(classModifiers.toArray(new Modifier[classModifiers.size()]))
                .addField(FieldSpec
                        .builder(classTypeName, fieldName, Modifier.FINAL)
                        .build())
                .addMethods(methods)
                .superclass(element.getKind() == ElementKind.INTERFACE ?
                        ClassName.OBJECT : TypeName.get(element.asType()))
                .addSuperinterfaces(getSuperInterfaces())
                .build();
    }

    private List<TypeName> getSuperInterfaces() {
        List<TypeName> superInterfaces;
        if (element.getKind() == ElementKind.INTERFACE) {
            superInterfaces = Collections.singletonList(TypeName.get(element.asType()));
        } else {
            superInterfaces = new ArrayList<>(element.getInterfaces().size());
            for (TypeMirror typeMirror : element.getInterfaces()) {
                superInterfaces.add(TypeName.get(typeMirror));
            }
        }
        return superInterfaces;
    }

    private String generatedClassName(TypeElement type, String prefix) {
        String name = type.getSimpleName().toString();
        String pkg = TypeUtils.packageNameOf(type);
        String dot = pkg.isEmpty() ? "" : ".";
        return String.format(Locale.US, "%s%s%s%s", pkg, dot, prefix, name);
    }

    private void writeSourceFile(Filer filer, String pkg, TypeSpec typeSpec) throws IOException {
        JavaFile.builder(pkg, typeSpec).build().writeTo(filer);
    }
}
