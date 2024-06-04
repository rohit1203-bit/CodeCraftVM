package com.interpreter.lox.util;

import com.interpreter.lox.lexer.Token;
import com.interpreter.lox.parser.Expr;
import com.interpreter.lox.parser.Stmt;

import java.util.List;

// TODO: Add support for statements as well, currently only supports expressions printing
public class ASTPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }
//> Statements and State omit

    public String print(Stmt stmt) {
        return stmt.accept(this);
    }
    //< Statements and State omit
//> visit-methods
//> Statements and State omit
    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(block ");

        for (Stmt statement : stmt.statements) {
            builder.append(statement.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }
//< Statements and State omit
//> Classes omit

    @Override
    public String visitClassStmt(Stmt.Class stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(class " + stmt.name.lexeme);
//> Inheritance omit

        if (stmt.superclass != null) {
            builder.append(" < " + print(stmt.superclass));
        }
//< Inheritance omit

        for (Stmt.Function method : stmt.methods) {
            builder.append(" " + print(method));
        }

        builder.append(")");
        return builder.toString();
    }
//< Classes omit
//> Statements and State omit

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return parenthesize(";", stmt.expression);
    }
//< Statements and State omit
//> Functions omit

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(fun " + stmt.name.lexeme + "(");

        for (Token param : stmt.params) {
            if (param != stmt.params.get(0)) builder.append(" ");
            builder.append(param.lexeme);
        }

        builder.append(") ");

        for (Stmt body : stmt.body) {
            builder.append(body.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }
//< Functions omit
//> Control Flow omit

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch == null) {
            return parenthesize2("if", stmt.condition, stmt.thenBranch);
        }

        return parenthesize2("if-else", stmt.condition, stmt.thenBranch,
                stmt.elseBranch);
    }
//< Control Flow omit
//> Statements and State omit

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("print", stmt.expression);
    }
//< Statements and State omit
//> Functions omit

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        if (stmt.value == null) return "(return)";
        return parenthesize("return", stmt.value);
    }
//< Functions omit
//> Statements and State omit

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer == null) {
            return parenthesize2("var", stmt.name);
        }

        return parenthesize2("var", stmt.name, "=", stmt.initializer);
    }
//< Statements and State omit
//> Control Flow omit

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return parenthesize2("while", stmt.condition, stmt.body);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize2("=", expr.name.lexeme, expr.value);
    }
//< Statements and State omit

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }
//> Functions omit

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return parenthesize2("call", expr.callee, expr.arguments);
    }
//< Functions omit
//> Classes omit

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return parenthesize2(".", expr.object, expr.name.lexeme);
    }
//< Classes omit

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }
//> Control Flow omit

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
//< Control Flow omit
//> Classes omit

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return parenthesize2("=",
                expr.object, expr.name.lexeme, expr.value);
    }
//< Classes omit
//> Inheritance omit

    @Override
    public String visitSuperExpr(Expr.Super expr) {
        return parenthesize2("super", expr.method);
    }
//< Inheritance omit
//> Classes omit

    @Override
    public String visitThisExpr(Expr.This expr) {
        return "this";
    }
//< Classes omit

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
//> Statements and State omit

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }
    //< Statements and State omit
//< visit-methods
//> print-utilities
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
    //< print-utilities
//> omit
    // Note: AstPrinting other types of syntax trees is not shown in the
    // book, but this is provided here as a reference for those reading
    // the full code.
    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        transform(builder, parts);
        builder.append(")");

        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr) {
                builder.append(((Expr)part).accept(this));
//> Statements and State omit
            } else if (part instanceof Stmt) {
                builder.append(((Stmt) part).accept(this));
//< Statements and State omit
            } else if (part instanceof Token) {
                builder.append(((Token) part).lexeme);
            } else if (part instanceof List) {
                transform(builder, ((List) part).toArray());
            } else {
                builder.append(part);
            }
        }
    }
}



//package com.interpreter.lox.util;
//
//import com.interpreter.lox.parser.Expr;
//
//// TODO: Add support for statements as well, currently only supports expressions printing
//public class ASTPrinter implements Expr.Visitor<String> {
//    public String print(Expr expr) {
//        return expr.accept(this);
//    }
//
//    @Override
//    public String visitAssignExpr(Expr.Assign expr) {
//        return null;
//    }
//
//    @Override
//    public String visitBinaryExpr(Expr.Binary expr) {
//        return genTree(expr.operator.lexeme, expr.left, expr.right);
//    }
//
//    @Override
//    public String visitCallExpr(Expr.Call expr) {
//        return null;
//    }
//
//    @Override
//    public String visitGetExpr(Expr.Get expr) {
//        return null;
//    }
//
//    @Override
//    public String visitGroupingExpr(Expr.Grouping expr) {
//        return genTree("group", expr.expression);
//    }
//
//    @Override
//    public String visitLiteralExpr(Expr.Literal expr) {
//        if(expr.value == null) return "nil";
//        return expr.value.toString();
//    }
//
//    @Override
//    public String visitLogicalExpr(Expr.Logical expr) {
//        return null;
//    }
//
//    @Override
//    public String visitSetExpr(Expr.Set expr) {
//        return null;
//    }
//
//    @Override
//    public String visitSuperExpr(Expr.Super expr) {
//        return null;
//    }
//
//    @Override
//    public String visitThisExpr(Expr.This expr) {
//        return null;
//    }
//
//    @Override
//    public String visitUnaryExpr(Expr.Unary expr) {
//        return genTree(expr.operator.lexeme, expr.right);
//    }
//
//    @Override
//    public String visitVariableExpr(Expr.Variable expr) {
//        return null;
//    }
//
//    private String parenthesize(String name, Expr...exprs) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("(").append(name);
//        for(Expr expr: exprs) {
//            builder.append(" ");
//            builder.append(expr.accept(this));
//        }
//        builder.append(")");
//
//        return builder.toString();
//    }
//
//    private static int depth = 0;
//
//    private static StringBuilder prefix = new StringBuilder();
//
//    static {
//        prefix.append(" ".repeat(4));
//    }
//    private static String pipe = "│";
//    private static String tPipe = "├──";
//    private static String lPipe = "└──";
//    private String genTree(String name, Expr ...exprs) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(name);
//        StringBuilder temp = new StringBuilder(prefix);
//        for(int i = 0; i < exprs.length; i++) {
//            boolean atLast = i == exprs.length - 1;
//            String symbol = atLast ? lPipe : tPipe;
//            String prefixSymbol = atLast ? "" : pipe;
//
//            prefix.append(prefixSymbol).append(" ".repeat(4));
//            String nodeString = exprs[i].accept(this);
//            builder.append('\n').append(prefix).append(symbol).append(nodeString);
//            prefix = new StringBuilder(prefix.substring(prefix.length() - 5));
//        }
//
//        return builder.toString();
//    }
//}
