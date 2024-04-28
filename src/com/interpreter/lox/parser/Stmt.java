package com.interpreter.lox.parser;

import com.interpreter.lox.lexer.Token;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitReturnStmt(Return stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stmt);
    }
    public static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements ;
        }

        public final List<Stmt> statements;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }
    public static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression ;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }
    public static class Function extends Stmt {
        Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name ;
            this.params = params ;
            this.body = body ;
        }

        public final Token name;
        public final  List<Token> params;
        public final  List<Stmt> body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }
    public static class If extends Stmt {
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition ;
            this.thenBranch = thenBranch ;
            this.elseBranch = elseBranch ;
        }

        public final Expr condition;
        public final  Stmt thenBranch;
        public final  Stmt elseBranch;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }
    public static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression ;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }
    public static class Return extends Stmt {
        Return(Token keyword, Expr value) {
            this.keyword = keyword ;
            this.value = value ;
        }

        public final Token keyword;
        public final  Expr value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }
    public static class Var extends Stmt {
        Var(Token name, Expr initializer) {
            this.name = name ;
            this.initializer = initializer ;
        }

        public final Token name;
        public final  Expr initializer;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }
    public static class While extends Stmt {
        While(Expr condition, Stmt body) {
            this.condition = condition ;
            this.body = body ;
        }

        public final Expr condition;
        public final  Stmt body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
