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
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("var",    TokenType.VARIABLE);
        keywords.put("fun",    TokenType.FUNCTION);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("and",    TokenType.AND);
        keywords.put("or",     TokenType.OR);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("if",     TokenType.IF);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("while",  TokenType.WHILE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("nil",    TokenType.NONE);
        keywords.put("print",  TokenType.PRINT);
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
        char c = nextChar();
        if (Character.isWhitespace(c)) {
            if (c == '\n')
                line++;
            return;
        }
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
        else if (c == '/') {
            if (matchNextChar('/'))
                skipLine();
            else
                addToken(TokenType.SLASH);
        }
        else if (c == '!')
            addToken(matchNextChar('=') ? TokenType.UNEQUAL : TokenType.NOT);
        else if (c == '=')
            addToken(matchNextChar('=') ? TokenType.EQUAL : TokenType.ASSIGN);
        else if (c == '<')
            addToken(matchNextChar('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        else if (c == '>')
            addToken(matchNextChar('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        else if (c == '"')
            stringLiteral();
        else if (Character.isDigit(c))
            numberLiteral();
        else if (Character.isAlphabetic(c) || c == '_')
            identifierLiteral();
        else
            Lox.error(line, "Unexpected token");
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

    private boolean matchNextChar(char expected) {
        if (isAtEnd() || getNextChar() != expected)
            return false;
        nextChar();
        return true;
    }

    private void stringLiteral() {
        while (!isAtEnd() && getNextChar() != '"')
        {
            if (getNextChar() == '\n')
                line++;
            nextChar();
        }
        if (isAtEnd())
            Lox.error(line, "Unterminated string literal");
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

        Double number = Double.parseDouble(source.substring(start, current));
        addToken(TokenType.NUMBER, number);
    }

    private void analyzeNumber() {
        while (Character.isDigit(getNextChar()) || getNextChar() == '_') {
            char c = nextChar();
            if (c == '_' && Character.isDigit(getNextChar()))
                Lox.error(line, "numeric separators are not allowed at the end of numeric literals");
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
