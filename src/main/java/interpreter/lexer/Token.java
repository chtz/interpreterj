package interpreter.lexer;

/**
 * Token class to represent a lexical token
 */
public class Token {
    private final TokenType type;
    private final String literal;
    private final int line;
    private final int column;
    
    public Token(TokenType type, String literal, int line, int column) {
        this.type = type;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getLiteral() {
        return literal;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("Token(%s, '%s', %d:%d)", type, literal, line, column);
    }
} 