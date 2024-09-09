package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> operators = new HashMap<>();
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        operators.put("{",      TokenType.LEFT_BRACE);
        operators.put("}",      TokenType.RIGHT_BRACE);
        operators.put("(",      TokenType.LEFT_PAREN);
        operators.put(")",      TokenType.RIGHT_PAREN);
        operators.put(";",      TokenType.SEMICOLON);
        operators.put(".",      TokenType.DOT);
        operators.put(",",      TokenType.COMMA);
        operators.put("+",      TokenType.PLUS);
        operators.put("-",      TokenType.MINUS);
        operators.put("*",      TokenType.STAR);
        operators.put("/",      TokenType.SLASH);
        operators.put("=",      TokenType.ASSIGN);
        operators.put("!",      TokenType.NOT);
        operators.put("<",      TokenType.LESS);
        operators.put(">",      TokenType.GREATER);
        operators.put("==",     TokenType.EQUAL);
        operators.put("!=",     TokenType.UNEQUAL);
        operators.put(">=",     TokenType.GREATER_EQUAL);
        operators.put("<=",     TokenType.LESS_EQUAL);
        keywords.put("var",     TokenType.VARIABLE);
        keywords.put("fun",     TokenType.FUNCTION);
        keywords.put("class",   TokenType.CLASS);
        keywords.put("and",     TokenType.AND);
        keywords.put("or",      TokenType.OR);
        keywords.put("true",    TokenType.TRUE);
        keywords.put("false",   TokenType.FALSE);
        keywords.put("if",      TokenType.IF);
        keywords.put("else",    TokenType.ELSE);
        keywords.put("while",   TokenType.WHILE);
        keywords.put("for",     TokenType.FOR);
        keywords.put("return",  TokenType.RETURN);
        keywords.put("super",   TokenType.SUPER);
        keywords.put("this",    TokenType.THIS);
        keywords.put("none",    TokenType.NONE);
        keywords.put("print",   TokenType.PRINT);
    }


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
        if (getNextChar() == '/' && get2NextChar() == '/') {
            skipLine();
            return;
        }

        if (Character.isWhitespace(getNextChar())) {
            if (getNextChar() == '\n')
                line++;
            current++;
            return;
        }

        if (!matchOperatorToken() && !matchLiteralToken())
            Lox.error(line, "Syntax", "Unexpected token: " + source.charAt(current - 1));
    }



    private boolean matchOperatorToken() {
        return (attemptTokenMatch(2) || attemptTokenMatch(1));
    }

    private boolean matchLiteralToken() {
        char currentChar = nextChar();
        if (currentChar == '"') {
            stringLiteral();
            return true;
        }
        if (Character.isDigit(currentChar)) {
            numberLiteral();
            return true;
        }
        if (Character.isAlphabetic(currentChar) || currentChar == '_') {
            identifierLiteral();
            return true;
        }
        return false;
    }

    private boolean attemptTokenMatch(int length) {
        if (current + length > source.length())
            return false;
        String lexeme = source.substring(start, current + length);
        TokenType type = operators.get(lexeme);
        if (type != null) {
            current += length;
            addToken(type);
            return true;
        }
        return false;
    }

    private char getNextChar() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char get2NextChar() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private char nextChar() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current++);
    }

    private void stringLiteral() {
        while (!isAtEnd() && getNextChar() != '"')
        {
            if (getNextChar() == '\n')
                line++;
            nextChar();
        }
        if (isAtEnd())
            Lox.error(line, "Syntax", "Unterminated string literal");
        nextChar();

        String literal = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, literal);
    }

    private void numberLiteral() {
        analyzeNumber();

        if (hasDecimals()) {
            nextChar();
            analyzeNumber();
        }

        String text = source.substring(start, current).replace("_", "");
        Double number = Double.parseDouble(text);
        addToken(TokenType.NUMBER, number);
    }

    private void analyzeNumber() {
        while (Character.isDigit(getNextChar()) || getNextChar() == '_') {
            char c = nextChar();
            if (c == '_' && !Character.isDigit(getNextChar()))
                Lox.error(line, "Syntax", "numeric separators are not allowed at the end of numeric literals");
        }
    }

    private boolean hasDecimals() {
        return getNextChar() == '.' && Character.isDigit(get2NextChar());
    }

    private void identifierLiteral() {
        while (isAlphanumeric(getNextChar()) || getNextChar() == '_')
            nextChar();
        String identifier = source.substring(start, current);

        TokenType type = keywords.get(identifier);
        if (type == null)
            type = TokenType.IDENTIFIER;

        addToken(type);
    }

    private boolean isAlphanumeric(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    private void skipLine() {
        while (!isAtEnd() && getNextChar() != '\n')
            nextChar();
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
