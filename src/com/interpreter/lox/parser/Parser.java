package com.interpreter.lox.parser;

import com.interpreter.lox.Lox;
import com.interpreter.lox.lexer.Token;
import com.interpreter.lox.lexer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.interpreter.lox.lexer.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // TODO: Add support for ternary operator (?:)

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {
            if(match(FUN)) {
                return function("function");
            }
            if(match(VAR)) {
                return varDeclaration();
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt function(String kind) {
        Token name = consume(IDENTIFIER, "Expected " + kind + " name.");
        consume(LEFT_PAREN, "Expected '(' after " + kind + " name.");
        List<Token> params = new ArrayList<>();
        if(!check(RIGHT_PAREN)) {
            do {
                if(params.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters");
                }
                params.add(consume(IDENTIFIER, "Expected parameter name."));
            }while (match(COMMA));

        }
        consume(RIGHT_PAREN, "Expected ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, params, body);

    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expected variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expected ';' after statement");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if(match(FOR)) return forStatement();

        if(match(IF)) return ifStatement();

        if(match(PRINT)) return printStatement();

        if(match(RETURN)) return returnStatement();

        if(match(WHILE)) return whileStatement();

        if(match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;

        if(!check(SEMICOLON)) {
            value = expression();
        }
        consume(SEMICOLON, "Expected ';' after return value");
        return new Stmt.Return(keyword,value);

    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'for'");
        Stmt initiailizer;
        if(match(SEMICOLON)) {
            initiailizer = null;
        } else if(match(VAR)) {
            initiailizer = varDeclaration();
        } else {
            initiailizer = expressionStatement();
        }

        Expr condition = null;
        if(!check(SEMICOLON)) {
            condition = expression();
        }

        consume(SEMICOLON, "Expected ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expected ')' after for clauses.");
        Stmt body = statement();

        if(increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }

        if(condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if(initiailizer != null) {
            body = new Stmt.Block(Arrays.asList(initiailizer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after while condition.");

        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'if'");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after if condition");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expected ';' after statement");

        return new Stmt.Print(value);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expected ';' after statement");

        return new Stmt.Expression(expr);
    }

    private Expr expression() { return assignment(); }

    private Expr assignment() {
        // try to parse as equality / logical operator
        Expr expr = or();

        // if match EQUALITY means the above expr is an IDENTIFIER (Variable)
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            // try casting as IDENTIFIER
            if(expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }
        // Just return the equality expression
        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // Rule: equality -> comparison (("==" | "!=") comparison)*
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL ,EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Rule: comparison -> term ((">" | ">=" | "<" | "<=") term)*
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Rule: factor (("-" | "+") factor)*
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Rule: unary (("/" | "*") unary)*
    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // Rule: ("!" | "-") unary | primary
    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return  new Expr.Unary(operator, right);
        }
        else if(match(STAR, SLASH, PLUS)) {
            throw error(previous(), "Expected left Hand side of the Binary operator");
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if(match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if(!check(RIGHT_PAREN)) {
            do {
                if(arguments.size() >= 255) {
                    // we don't throw error here, we just report it to the user
                    error(peek(), "Can't have more than 255 arguments");
                }
                arguments.add(expression());
            }while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {
        if(match(FALSE)) return  new Expr.Literal(FALSE);
        if(match(TRUE)) return new Expr.Literal(TRUE);
        if(match(NIL)) return new Expr.Literal(NIL);
        if(match(NUMBER, STRING)) {
            return  new Expr.Literal(previous().literal);
        }
        if(match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expected ')' after expression.");
            return new Expr.Grouping(expr);
        }
        if(match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw  error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private boolean match(TokenType...types) {
        for(TokenType type: types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }


    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }
}
