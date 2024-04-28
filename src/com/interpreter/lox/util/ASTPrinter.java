package com.interpreter.lox.util;

import com.interpreter.lox.parser.Expr;

// TODO: Add support for statements as well, currently only supports expressions printing
public class ASTPrinter implements Expr.Visitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return genTree(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return genTree("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return genTree(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return null;
    }

    private String parenthesize(String name, Expr...exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for(Expr expr: exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private static int depth = 0;

    private static StringBuilder prefix = new StringBuilder();

    static {
        prefix.append(" ".repeat(4));
    }
    private static String pipe = "│";
    private static String tPipe = "├──";
    private static String lPipe = "└──";
    private String genTree(String name, Expr ...exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        StringBuilder temp = new StringBuilder(prefix);
        for(int i = 0; i < exprs.length; i++) {
            boolean atLast = i == exprs.length - 1;
            String symbol = atLast ? lPipe : tPipe;
            String prefixSymbol = atLast ? "" : pipe;

            prefix.append(prefixSymbol).append(" ".repeat(4));
            String nodeString = exprs[i].accept(this);
            builder.append('\n').append(prefix).append(symbol).append(nodeString);
            prefix = new StringBuilder(prefix.substring(prefix.length() - 5));
        }

        return builder.toString();
    }
}
