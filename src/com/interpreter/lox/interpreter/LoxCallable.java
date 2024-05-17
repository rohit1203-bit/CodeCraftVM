package com.interpreter.lox.interpreter;

import java.util.List;

public interface LoxCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
