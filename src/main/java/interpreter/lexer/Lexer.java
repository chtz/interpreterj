package interpreter.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer class to tokenize the input source code
 * Following the KISS principle: Simple, straightforward lexical analysis
 */
public class Lexer {
    private final String input;
    private int position;       // Current position in input (points to current character)
    private int readPosition;   // Next position in input (after current character)
    private char ch;            // Current character under examination
    private int line;           // Current line number
    private int column;         // Current column number
    
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 0;
        this.ch = 0;
        this.line = 1;
        this.column = 0;
        
        readChar(); // Initialize first character
    }
    
    /**
     * Advances to the next character in the input
     */
    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0; // EOF
        } else {
            ch = input.charAt(readPosition);
        }
        
        position = readPosition;
        readPosition += 1;
        column += 1;
        
        // Handle newlines to track line numbers
        if (ch == '\n') {
            line++;
            column = 0;
        }
    }
    
    /**
     * Peeks at the next character without advancing
     */
    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0; // EOF
        } else {
            return input.charAt(readPosition);
        }
    }
    
    /**
     * Reads the next token from the input
     */
    public Token nextToken() {
        Token token;
        
        // Loop to repeatedly skip both whitespace and comments
        // This handles cases where there are multiple newlines or comments in sequence
        boolean skippedSomething;
        do {
            int positionBeforeSkipping = position;
            skipWhitespace();
            skipComments();
            // If position didn't change, we didn't skip anything
            skippedSomething = (position > positionBeforeSkipping);
        } while (skippedSomething);
        
        switch (ch) {
            case '=':
                if (peekChar() == '=') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.EQ, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.ASSIGN, String.valueOf(ch), line, column);
                }
                break;
            case '+':
                token = new Token(TokenType.PLUS, String.valueOf(ch), line, column);
                break;
            case '-':
                token = new Token(TokenType.MINUS, String.valueOf(ch), line, column);
                break;
            case '*':
                token = new Token(TokenType.ASTERISK, String.valueOf(ch), line, column);
                break;
            case '#':
                skipComments();
                return nextToken();
            case '/':
                if (peekChar() == '/' || peekChar() == '*') {
                    skipComments();
                    return nextToken();
                } else {
                    token = new Token(TokenType.SLASH, String.valueOf(ch), line, column);
                }
                break;
            case '%':
                token = new Token(TokenType.PERCENT, String.valueOf(ch), line, column);
                break;
            case '!':
                if (peekChar() == '=') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.NOT_EQ, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.NOT, String.valueOf(ch), line, column);
                }
                break;
            case '<':
                if (peekChar() == '=') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.LT_EQ, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.LT, String.valueOf(ch), line, column);
                }
                break;
            case '>':
                if (peekChar() == '=') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.GT_EQ, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.GT, String.valueOf(ch), line, column);
                }
                break;
            case '&':
                if (peekChar() == '&') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.AND, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.ILLEGAL, String.valueOf(ch), line, column);
                }
                break;
            case '|':
                if (peekChar() == '|') {
                    char currentChar = ch;
                    readChar();
                    String literal = currentChar + "" + ch;
                    token = new Token(TokenType.OR, literal, line, column - 1);
                } else {
                    token = new Token(TokenType.ILLEGAL, String.valueOf(ch), line, column);
                }
                break;
            case ',':
                token = new Token(TokenType.COMMA, String.valueOf(ch), line, column);
                break;
            case ';':
                token = new Token(TokenType.SEMICOLON, String.valueOf(ch), line, column);
                break;
            case '(':
                token = new Token(TokenType.LPAREN, String.valueOf(ch), line, column);
                break;
            case ')':
                token = new Token(TokenType.RPAREN, String.valueOf(ch), line, column);
                break;
            case '{':
                token = new Token(TokenType.LBRACE, String.valueOf(ch), line, column);
                break;
            case '}':
                token = new Token(TokenType.RBRACE, String.valueOf(ch), line, column);
                break;
            case '"':
            case '\'':
                int startColumn = column;
                String stringLiteral = readString(ch);
                return new Token(TokenType.STRING, stringLiteral, line, startColumn);
            case 0:
                token = new Token(TokenType.EOF, "", line, column);
                break;
            default:
                if (isLetter(ch)) {
                    int startColumn2 = column;
                    String identifier = readIdentifier();
                    TokenType type = Keywords.lookup(identifier);
                    return new Token(type, identifier, line, startColumn2);
                } else if (isDigit(ch)) {
                    int startColumn2 = column;
                    String number = readNumber();
                    return new Token(TokenType.NUMBER, number, line, startColumn2);
                } else {
                    token = new Token(TokenType.ILLEGAL, String.valueOf(ch), line, column);
                }
        }
        
        readChar();
        return token;
    }
    
    /**
     * Reads an identifier from the input
     */
    private String readIdentifier() {
        int startPosition = position;
        while (isLetter(ch) || isDigit(ch)) {
            readChar();
        }
        return input.substring(startPosition, position);
    }
    
    /**
     * Reads a number from the input
     */
    private String readNumber() {
        int startPosition = position;
        boolean hasDot = false;
        
        while (isDigit(ch) || (ch == '.' && !hasDot)) {
            if (ch == '.') {
                hasDot = true;
            }
            readChar();
        }
        
        return input.substring(startPosition, position);
    }
    
    /**
     * Reads a string from the input (handles both single and double quotes)
     */
    private String readString(char quote) {
        readChar(); // Skip the opening quote
        int startPosition = position;
        
        while (ch != 0 && ch != quote) {
            // Handle escape sequences
            if (ch == '\\' && peekChar() == quote) {
                readChar(); // Skip the backslash
            }
            readChar();
        }
        
        if (ch == 0) {
            return input.substring(startPosition, position); // Unterminated string
        }
        
        String result = input.substring(startPosition, position);
        readChar(); // Skip the closing quote
        return result;
    }
    
    /**
     * Skips whitespace characters
     */
    private void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
    }
    
    /**
     * Skips comments in the code
     * Handles both single-line comments (// ...) and multi-line comments
     */
    private void skipComments() {
        if (ch == '/') {
            if (peekChar() == '/') {
                // Single-line comment, skip until the end of line
                while (ch != 0 && ch != '\n') {
                    readChar();
                }
                
                if (ch != 0) {
                    readChar(); // Skip the newline
                }
            } else if (peekChar() == '*') {
                // Multi-line comment, skip until closing */
                readChar(); // Skip /
                readChar(); // Skip *
                
                boolean ended = false;
                while (!ended && ch != 0) {
                    if (ch == '*' && peekChar() == '/') {
                        ended = true;
                        readChar(); // Skip *
                        readChar(); // Skip /
                    } else {
                        readChar();
                    }
                }
            }
        } else if (ch == '#') {
            // Python-style comment, skip until the end of line
            while (ch != 0 && ch != '\n') {
                readChar();
            }
            
            if (ch != 0) {
                readChar(); // Skip the newline
            }
        }
    }
    
    /**
     * Checks if the character is a letter (a-z, A-Z or _)
     */
    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }
    
    /**
     * Checks if the character is a digit (0-9)
     */
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    /**
     * Tokenize the entire input and return a list of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        
        return tokens;
    }
} 