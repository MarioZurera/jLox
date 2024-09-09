package lox;

public enum TokenType {
    /*/ Symbols Token /*/

    // Control Token //
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    COMMA,          // ,
    DOT,            // .
    SEMICOLON,      // ;

    // Operation Token //
    ASSIGN,         // =
    PLUS,           // +
    MINUS,          // -
    STAR,           // *
    SLASH,          // /

    // Comparison Token //
    NOT,            // !
    EQUAL,          // ==
    UNEQUAL,        // !=
    GREATER,        // >
    LESS,           // <
    GREATER_EQUAL,  // >=
    LESS_EQUAL,     // <=

    // Literal Token //
    IDENTIFIER,    // variable name
    STRING,         // ""
    NUMBER,         // 1 2 3 4 5 6 7 8 9

    /*/ Keywords Token /*/

    // Initialization Token //
    VARIABLE,       // var
    FUNCTION,       // fun
    CLASS,          // class

    // Logical Token //
    AND,            // and
    OR,             // or
    TRUE,           // true
    FALSE,          // false

    // Control Token //
    IF,             // if
    ELSE,           // else
    WHILE,          // while
    FOR,            // for
    RETURN,         // return
    // BREAK        // break

    // Context Token //
    SUPER,          // super
    THIS,           // this

    // Value Token //
    NONE,           // nil

    // Built-in Token //
    PRINT,          // print

    // EOF Token //
    EOF
}
