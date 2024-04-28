package com.interpreter.lox.parser;

import com.interpreter.lox.lexer.Token;

import java.util.List;

public abstract class Expr {
    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }
    public static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name ;
            this.value = value ;
        }

        public final Token name;
        public final  Expr value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }
    public static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left ;
            this.operator = operator ;
            this.right = right ;
        }

        public final Expr left;
        public final  Token operator;
        public final  Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }
    public static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee ;
            this.paren = paren ;
            this.arguments = arguments ;
        }

        public final Expr callee;
        public final  Token paren;
        public final  List<Expr> arguments;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }
    public static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression ;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }
    public static class Literal extends Expr {
        Literal(Object value) {
            this.value = value ;
        }

        public final Object value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }
    public static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left ;
            this.operator = operator ;
            this.right = right ;
        }

        public final Expr left;
        public final  Token operator;
        public final  Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }
    public static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator ;
            this.right = right ;
        }

        public final Token operator;
        public final  Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }
    public static class Variable extends Expr {
        Variable(Token name) {
            this.name = name ;
        }

        public final Token name;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
