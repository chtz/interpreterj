package interpreter.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit-based test suite for the Lexer
 */
public class LexerTest {
    
    @Test
    @DisplayName("Test lexing of identifiers")
    public void testIdentifiers() {
        String input = "foo bar baz x y z _underscore camelCase PascalCase snake_case";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "foo");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "bar");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "baz");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "x");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "y");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "z");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "_underscore");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "camelCase");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "PascalCase");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "snake_case");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of keywords")
    public void testKeywords() {
        String input = "def let if else while return true false null";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.DEF, "def");
        assertToken(lexer.nextToken(), TokenType.LET, "let");
        assertToken(lexer.nextToken(), TokenType.IF, "if");
        assertToken(lexer.nextToken(), TokenType.ELSE, "else");
        assertToken(lexer.nextToken(), TokenType.WHILE, "while");
        assertToken(lexer.nextToken(), TokenType.RETURN, "return");
        assertToken(lexer.nextToken(), TokenType.TRUE, "true");
        assertToken(lexer.nextToken(), TokenType.FALSE, "false");
        assertToken(lexer.nextToken(), TokenType.NULL, "null");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of operators")
    public void testOperators() {
        String input = "+ - * / % == != < > <= >= && || ! =";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.PLUS, "+");
        assertToken(lexer.nextToken(), TokenType.MINUS, "-");
        assertToken(lexer.nextToken(), TokenType.ASTERISK, "*");
        assertToken(lexer.nextToken(), TokenType.SLASH, "/");
        assertToken(lexer.nextToken(), TokenType.PERCENT, "%");
        assertToken(lexer.nextToken(), TokenType.EQ, "==");
        assertToken(lexer.nextToken(), TokenType.NOT_EQ, "!=");
        assertToken(lexer.nextToken(), TokenType.LT, "<");
        assertToken(lexer.nextToken(), TokenType.GT, ">");
        assertToken(lexer.nextToken(), TokenType.LT_EQ, "<=");
        assertToken(lexer.nextToken(), TokenType.GT_EQ, ">=");
        assertToken(lexer.nextToken(), TokenType.AND, "&&");
        assertToken(lexer.nextToken(), TokenType.OR, "||");
        assertToken(lexer.nextToken(), TokenType.NOT, "!");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of delimiters")
    public void testDelimiters() {
        String input = ", ; ( ) { }";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.COMMA, ",");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.LPAREN, "(");
        assertToken(lexer.nextToken(), TokenType.RPAREN, ")");
        assertToken(lexer.nextToken(), TokenType.LBRACE, "{");
        assertToken(lexer.nextToken(), TokenType.RBRACE, "}");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of number literals")
    public void testNumberLiterals() {
        String input = "0 123 456.789 3.14159";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.NUMBER, "0");
        assertToken(lexer.nextToken(), TokenType.NUMBER, "123");
        assertToken(lexer.nextToken(), TokenType.NUMBER, "456.789");
        assertToken(lexer.nextToken(), TokenType.NUMBER, "3.14159");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of string literals")
    public void testStringLiterals() {
        String input = "\"Hello, world!\" 'Single quoted string'";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.STRING, "Hello, world!");
        assertToken(lexer.nextToken(), TokenType.STRING, "Single quoted string");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of comments")
    public void testComments() {
        String input = 
            "// This is a single-line comment\n\n" +
            "let x = 10; // Comment after statement\n" +
            "/* This is a\n" +
            "   multi-line comment */\n" +
            "let y = 20;";
        Lexer lexer = new Lexer(input);
        
        // Skip the comment and get the 'let' token
        assertToken(lexer.nextToken(), TokenType.LET, "let");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "x");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.NUMBER, "10");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        
        // Skip the comment and get the next 'let' token
        assertToken(lexer.nextToken(), TokenType.LET, "let");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "y");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.NUMBER, "20");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of illegal tokens")
    public void testIllegalTokens() {
        String input = "let x = @;";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.LET, "let");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "x");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.ILLEGAL, "@");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    @Test
    @DisplayName("Test lexing of a complete program")
    public void testCompleteProgram() {
        String input = 
            "def factorial(n) {\n" +
            "  if (n <= 1) {\n" +
            "    return 1;\n" +
            "  }\n" +
            "  return n * factorial(n - 1);\n" +
            "}\n" +
            "\n" +
            "let result = factorial(5);\n" +
            "console_put(result); // Print the result\n";
        
        Lexer lexer = new Lexer(input);
        List<Token> tokens = new ArrayList<>();
        
        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        
        // Just check the count of tokens for brevity
        // -1 because we don't count the EOF token
        assertEquals(41, tokens.size() - 1, "Expected 41 tokens (excluding EOF)");
        
        // Check a few key tokens
        assertEquals(TokenType.DEF, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("factorial", tokens.get(1).getLiteral());
    }
    
    @Test
    @DisplayName("Test position tracking")
    public void testPositionTracking() {
        String input = "let x = 10;\nlet y = 20;";
        Lexer lexer = new Lexer(input);
        
        // First line: "let x = 10;"
        Token letToken = lexer.nextToken();
        assertEquals(TokenType.LET, letToken.getType());
        assertEquals(1, letToken.getLine());
        assertEquals(1, letToken.getColumn());
        
        Token xToken = lexer.nextToken();
        assertEquals(TokenType.IDENTIFIER, xToken.getType());
        assertEquals(1, xToken.getLine());
        assertEquals(5, xToken.getColumn());
        
        // Skip to the second line
        lexer.nextToken(); // =
        lexer.nextToken(); // 10
        lexer.nextToken(); // ;
        
        // Second line: "let y = 20;"
        Token letToken2 = lexer.nextToken();
        assertEquals(TokenType.LET, letToken2.getType());
        assertEquals(2, letToken2.getLine());
        assertEquals(1, letToken2.getColumn());
    }
    
    @Test
    @DisplayName("Test empty input")
    public void testEmptyInput() {
        String input = "";
        Lexer lexer = new Lexer(input);
        
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }
    
    private void assertToken(Token token, TokenType expectedType, String expectedLiteral) {
        assertEquals(expectedType, token.getType(), 
            "Token type mismatch: expected " + expectedType + ", got " + token.getType());
        assertEquals(expectedLiteral, token.getLiteral(), 
            "Token literal mismatch: expected '" + expectedLiteral + "', got '" + token.getLiteral() + "' ("+token.getType()+")");
    }
} 