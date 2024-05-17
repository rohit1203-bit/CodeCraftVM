package com.interpreter.lox;

import com.interpreter.lox.interpreter.Interpreter;
import com.interpreter.lox.lexer.Scanner;
import com.interpreter.lox.lexer.Token;
import com.interpreter.lox.parser.*;
import com.interpreter.lox.util.RuntimeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.interpreter.lox.lexer.TokenType.EOF;

public class Lox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            // when we press Ctrl+D, it signals 'end-of-file' condition
            // when this happens readline returns null
            if (line == null) {
                input.close();
                reader.close();
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void runFile(String path) throws IOException {
        Path p = Paths.get(path);
        byte[] bytes = Files.readAllBytes(p);
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError)
            System.exit(65);
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError)
            return;
        interpreter.interpret(statements);

        if (hadRuntimeError)
            System.exit(70);
        // System.out.println(new ASTPrinter().print(expression));
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}



//package com.interpreter.lox;
//
//import com.interpreter.lox.lexer.Scanner;
//import com.interpreter.lox.lexer.Token;
//import com.interpreter.lox.parser.Parser;
//import com.interpreter.lox.parser.Stmt;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//import static com.interpreter.lox.lexer.TokenType.EOF;
//
//public class Lox {
//    static boolean hadError = false;
//    static boolean hadRuntimeError = false;
//
////    private static final Interpreter interpreter = new Interpreter();
//
//    public static void main(String[] args) throws IOException {
//        if (args.length > 2) {
//            System.out.println("Usage: jlox [script]");
//            System.exit(64);
//        } else if (args.length == 1) {
//            runFile(args[0]);
//        } else {
//            runPrompt();
//        }
//    }
//
//    private static void runPrompt() throws IOException {
//        InputStreamReader input = new InputStreamReader(System.in);
//        BufferedReader reader = new BufferedReader(input);
//
//        while (true) {
//            System.out.print("> ");
//            String line = reader.readLine();
//            // when we press Ctrl+D, it signals 'end-of-file' condition
//            // when this happens readline returns null
//            if (line == null) {
//                input.close();
//                reader.close();
//                break;
//            }
//            run(line);
//            hadError = false;
//        }
//    }
//
//    private static void runFile(String path) throws IOException {
//        Path p = Paths.get(path);
//        byte[] bytes = Files.readAllBytes(p);
//        run(new String(bytes, Charset.defaultCharset()));
//        if (hadError)
//            System.exit(65);
//    }
//
//    private static void run(String source) {
//        Scanner scanner = new Scanner(source);
//        List<Token> tokens = scanner.scanTokens();
//
//        Parser parser = new Parser(tokens);
//        List<Stmt> statements = parser.parse();
//
//        if (hadError)
//            return;
//
//        for(Token token: tokens){
//            System.out.println(token.toString());
//        }
//
//
//        if (hadRuntimeError)
//            System.exit(70);
//        // System.out.println(new ASTPrinter().print(expression));
//    }
//
//    public static void error(int line, String message) {
//        report(line, "", message);
//    }
//
//    public static void error(Token token, String message) {
//        if (token.type == EOF) {
//            report(token.line, " at end", message);
//        } else {
//            report(token.line, " at '" + token.lexeme + "'", message);
//        }
//    }
//
//
//    private static void report(int line, String where, String message) {
//        System.err.println("[line " + line + "] Error" + where + ": " + message);
//        hadError = true;
//    }
//
//}



