package interpreter.lexer;

/**
 * Token types for the Cursor Interpreter language
 */
public enum TokenType {
    // Special tokens
    EOF,
    ILLEGAL,
    
    // Identifiers and literals
    IDENTIFIER,
    NUMBER,
    STRING,
    
    // Keywords
    DEF,
    LET,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    NULL,
    
    // Operators
    PLUS("+"),
    MINUS("-"),
    ASTERISK("*"),
    SLASH("/"),
    PERCENT("%"),
    
    // Comparison operators
    EQ("=="),
    NOT_EQ("!="),
    LT("<"),
    GT(">"),
    LT_EQ("<="),
    GT_EQ(">="),
    
    // Logical operators
    AND("&&"),
    OR("||"),
    NOT("!"),
    
    // Assignment
    ASSIGN("="),
    
    // Delimiters
    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]");
    
    private final String literal;
    
    TokenType() {
        this.literal = this.name();
    }
    
    TokenType(String literal) {
        this.literal = literal;
    }
    
    public String getLiteral() {
        return literal;
    }
} 