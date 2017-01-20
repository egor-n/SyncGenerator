package com.github.egorn.syncgenerator.compiler;

import javax.lang.model.element.Element;

class ProcessingException extends Exception {
    private final Element element;

    ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    Element getElement() {
        return element;
    }
}
