package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String _source;
    private final List<Token> _tokens = new ArrayList<>();
    private static final Map<String, TokenType> _operators = new HashMap<>();
    private static final Map<String, TokenType> _keywords = new HashMap<>();
    private int _start = 0;
    private int _current = 0;
    private int _line = 1;

    static {
        _operators.put("{",      TokenType.LEFT_BRACE);
        _operators.put("}",      TokenType.RIGHT_BRACE);
        _operators.put("(",      TokenType.LEFT_PAREN);
        _operators.put(")",      TokenType.RIGHT_PAREN);
        _operators.put(";",      TokenType.SEMICOLON);
        _operators.put(".",      TokenType.DOT);
        _operators.put(",",      TokenType.COMMA);
        _operators.put("+",      TokenType.PLUS);
        _operators.put("-",      TokenType.MINUS);
        _operators.put("*",      TokenType.STAR);
        _operators.put("/",      TokenType.SLASH);
        _operators.put("=",      TokenType.ASSIGN);
        _operators.put("!",      TokenType.NOT);
        _operators.put("<",      TokenType.LESS);
        _operators.put(">",      TokenType.GREATER);
        _operators.put("==",     TokenType.EQUAL);
        _operators.put("!=",     TokenType.UNEQUAL);
        _operators.put(">=",     TokenType.GREATER_EQUAL);
        _operators.put("<=",     TokenType.LESS_EQUAL);
        _keywords.put("var",     TokenType.VARIABLE);
        _keywords.put("fun",     TokenType.FUNCTION);
        _keywords.put("class",   TokenType.CLASS);
        _keywords.put("and",     TokenType.AND);
        _keywords.put("or",      TokenType.OR);
        _keywords.put("true",    TokenType.TRUE);
        _keywords.put("false",   TokenType.FALSE);
        _keywords.put("if",      TokenType.IF);
        _keywords.put("else",    TokenType.ELSE);
        _keywords.put("while",   TokenType.WHILE);
        _keywords.put("for",     TokenType.FOR);
        _keywords.put("return",  TokenType.RETURN);
        _keywords.put("super",   TokenType.SUPER);
        _keywords.put("this",    TokenType.THIS);
        _keywords.put("none",    TokenType.NONE);
        _keywords.put("print",   TokenType.PRINT);
    }


    Scanner(String source) {
        this._source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            _start = _current;
            scanNextToken();
        }

        _tokens.add(new Token(TokenType.EOF, "", null, _line));
        return _tokens;
    }

    private void scanNextToken() {
        if (matchComment() || matchWhitespace())
            return;

        if (!matchOperatorToken() && !matchLiteralToken()) {
            Lox.error(_line, "Syntax", "Unexpected token: " + getCurrentChar());
            nextChar();
        }
    }

    private boolean matchComment() {
        if (getCurrentChar() == '/' && getNextChar() == '/') {
            skipLine();
            return true;
        }
        return false;
    }

    private boolean matchWhitespace() {
        if (Character.isWhitespace(getCurrentChar())) {
            if (getCurrentChar() == '\n')
                _line++;
            nextChar();
            return true;
        }
        return false;
    }

    private boolean matchOperatorToken() {
        return (attemptTokenMatch(2) || attemptTokenMatch(1));
    }

    private boolean matchLiteralToken() {
        if (getCurrentChar() == '"') {
            stringLiteral();
            return true;
        }
        if (Character.isDigit(getCurrentChar())) {
            numberLiteral();
            return true;
        }
        if (Character.isAlphabetic(getCurrentChar()) || getCurrentChar() == '_') {
            identifierLiteral();
            return true;
        }
        return false;
    }

    private boolean attemptTokenMatch(int length) {
        if (_current + length > _source.length())
            return false;
        String lexeme = _source.substring(_start, _current + length);
        TokenType type = _operators.get(lexeme);
        if (type != null) {
            moveCursor(length);
            addToken(type);
            return true;
        }
        return false;
    }

    private void moveCursor(int numberOfElements) {
        _current += numberOfElements;
    }

    private char getCurrentChar() {
        if (isAtEnd())
            return '\0';
        return _source.charAt(_current);
    }

    private char getNextChar() {
        if (_current + 1 >= _source.length())
            return '\0';
        return _source.charAt(_current + 1);
    }

    private void nextChar() {
        moveCursor(1);
    }

    private void stringLiteral() {
        while (!isAtEnd() && getCurrentChar() != '"')
        {
            if (getCurrentChar() == '\n')
                _line++;
            nextChar();
        }
        if (isAtEnd())
            Lox.error(_line, "Syntax", "Unterminated string literal");
        nextChar();

        String literal = _source.substring(_start + 1, _current - 1);
        addToken(TokenType.STRING, literal);
    }

    private void numberLiteral() {
        analyzeNumber();

        if (hasDecimals()) {
            nextChar();
            analyzeNumber();
        }

        String text = _source.substring(_start, _current).replace("_", "");
        Double number = Double.parseDouble(text);
        addToken(TokenType.NUMBER, number);
    }

    private void analyzeNumber() {
        while (Character.isDigit(getCurrentChar()) || getCurrentChar() == '_') {
            if (getCurrentChar() == '_' && !Character.isDigit(getNextChar()))
                Lox.error(_line, "Syntax", "numeric separators are not allowed at the end of numeric literals");
            nextChar();
        }
    }

    private boolean hasDecimals() {
        return getCurrentChar() == '.' && Character.isDigit(getNextChar());
    }

    private void identifierLiteral() {
        while (isAlphanumeric(getCurrentChar()) || getCurrentChar() == '_')
            nextChar();
        String identifier = _source.substring(_start, _current);

        TokenType type = _keywords.get(identifier);
        if (type == null)
            type = TokenType.IDENTIFIER;

        addToken(type);
    }

    private boolean isAlphanumeric(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    private void skipLine() {
        while (!isAtEnd() && getCurrentChar() != '\n')
            nextChar();
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = _source.substring(_start, _current);
        _tokens.add(new Token(type, lexeme, literal, _line));
    }

    private boolean isAtEnd() {
        return _current >= _source.length();
    }
}
