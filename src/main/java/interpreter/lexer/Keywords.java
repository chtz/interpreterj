package interpreter.lexer;

import java.util.HashMap;
import java.util.Map;

/**
 * Keywords mapping for the language
 */
public class Keywords {
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        KEYWORDS.put("def", TokenType.DEF);
        KEYWORDS.put("let", TokenType.LET);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("null", TokenType.NULL);
    }
    
    /**
     * Lookup a keyword and return its token type, or IDENTIFIER if not a keyword
     */
    public static TokenType lookup(String identifier) {
        return KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER);
    }
} 