package com.interpreter.lox.lexer;

import com.interpreter.lox.Lox;
import com.interpreter.lox.lexer.Token;
import com.interpreter.lox.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interpreter.lox.lexer.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("or", OR);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    public List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            case '!':
                addToken(match('=') ? BANG_EQUAL: BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL: LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL: GREATER);
                break;

            case '/':
                if(match('/')) {
                    while(peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if(match('*')) {
                    while(!(peek() == '*') && !isAtEnd()) {
                        if(peek() == '\n') line++;
                        advance();
                    }
                    if(isAtEnd()) {
                        Lox.error(line, "Unterminated Comment Blog");
                        return;
                    }
                    advance();
                    if(peek() != '/' || isAtEnd()) {
                        Lox.error(line ,"Unterminated Comment blog");
                        return;
                    }
                    advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ': break;
            case '\r': break;
            case '\t': break;

            case '\n': line++; break;
            case '"': string(); break;
            default:
                if(isDigit(c))
                    number();
                else if(isAlpha(c)) {
                    identifier();
                }
                else
                    Lox.error(line, "Unexpected character: " + c); break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();
        if(peek() == '.' && isDigit(peekNext()))  {
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start,current)));
    }

    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()) {
            Lox.error(line, "Unterminated String.");
            return;
        }

        advance();
        String val = source.substring(start + 1, current - 1);
        addToken(STRING, val);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isDigit(char c) {
        return c  >= '0' && c <='9';
    }
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    private char advance() {
        return source.charAt(current++);
    }
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }
}
