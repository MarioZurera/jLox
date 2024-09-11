package lox;

public class Token {
    private final TokenType _type;
    private final String _lexeme;
    private final Object _literal;
    private final int _line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this._type = type;
        this._lexeme = lexeme;
        this._literal = literal;
        this._line = line;
    }

    public TokenType getType() {
        return _type;
    }

    public String getLexeme() {
        return _lexeme;
    }

    public Object getLiteral() {
        return _literal;
    }

    public int getLine() {
        return _line;
    }

    @Override
    public String toString() {
        String typeText = _type.toString();
        if (typeText.length() < 4)
            typeText += "\t";
        if (typeText.length() < 8)
            typeText += "\t";
        String value = _literal != null ? _literal.toString() : _lexeme;
        return typeText + "\t\t" + value;
    }
}
