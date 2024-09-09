package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lox.TokenType;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = nextChar();
        if (c == '{')
            addToken(TokenType.LEFT_BRACE);
        else if (c == '}')
            addToken(TokenType.RIGHT_BRACE);
        else if (c == '(')
            addToken(TokenType.LEFT_PAREN);
        else if (c == ')')
            addToken(TokenType.RIGHT_PAREN);
        else if (c == ';')
            addToken(TokenType.SEMICOLON);
        else if (c == ',')
            addToken(TokenType.COMMA);
        else if (c == '.')
            addToken(TokenType.DOT);
        else if (c == '+')
            addToken(TokenType.PLUS);
        else if (c == '-')
            addToken(TokenType.MINUS);
        else if (c == '*')
            addToken(TokenType.STAR);
        //else if (c == '/')
        //    addToken(TokenType.SLASH);
        else if (c == '!')
            addToken(TokenType.UNEQUAL);
        else
            Lox.error(line, "Unexpected token");
    }

    private char nextChar() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
