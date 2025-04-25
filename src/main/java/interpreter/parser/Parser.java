package interpreter.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import interpreter.ast.ArrayLiteral;
import interpreter.ast.AssignmentStatement;
import interpreter.ast.BlockStatement;
import interpreter.ast.BooleanLiteral;
import interpreter.ast.CallExpression;
import interpreter.ast.ExpressionStatement;
import interpreter.ast.FunctionDeclaration;
import interpreter.ast.Identifier;
import interpreter.ast.IfStatement;
import interpreter.ast.IndexAssignmentStatement;
import interpreter.ast.IndexExpression;
import interpreter.ast.InfixExpression;
import interpreter.ast.Node;
import interpreter.ast.NullLiteral;
import interpreter.ast.NumberLiteral;
import interpreter.ast.PrefixExpression;
import interpreter.ast.Program;
import interpreter.ast.ReturnStatement;
import interpreter.ast.StringLiteral;
import interpreter.ast.VariableDeclaration;
import interpreter.ast.WhileStatement;
import interpreter.lexer.Lexer;
import interpreter.lexer.Token;
import interpreter.lexer.TokenType;

/**
 * Parser class for converting tokens into an AST
 * Implementation of a predictive recursive descent parser (LL(1))
 */
public class Parser {
    // Precedence levels for operators
    private enum Precedence {
        LOWEST(1),
        OR(2),      // ||
        AND(3),     // &&
        EQUALS(4),  // == !=
        COMPARE(5), // > >= < <=
        SUM(6),     // + -
        PRODUCT(7), // * / %
        PREFIX(8),  // -x !x
        CALL(9);    // myFunction(x)
        
        private final int value;
        
        Precedence(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    // Error class to represent parser errors
    public static class Error {
        private final String message;
        private final int line;
        private final int column;
        
        public Error(String message, int line, int column) {
            this.message = message;
            this.line = line;
            this.column = column;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            return String.format("Parse error at %d:%d: %s", line, column, message);
        }
    }
    
    private final Lexer lexer;
    private final List<Token> tokens;
    private int currentPosition;
    private final List<Error> errors;
    
    private Token currentToken;
    private Token peekToken;
    
    // Maps to store parsing functions for different token types
    private final Map<TokenType, Supplier<Node>> prefixParseFns;
    private final Map<TokenType, Function<Node, Node>> infixParseFns;
    
    // Mapping of token types to their respective precedence
    private final Map<TokenType, Precedence> precedences;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.tokens = new ArrayList<>();
        this.currentPosition = 0;
        this.errors = new ArrayList<>();
        
        this.prefixParseFns = new HashMap<>();
        this.infixParseFns = new HashMap<>();
        
        // Initialize precedence table
        this.precedences = new HashMap<>();
        this.precedences.put(TokenType.OR, Precedence.OR);
        this.precedences.put(TokenType.AND, Precedence.AND);
        this.precedences.put(TokenType.EQ, Precedence.EQUALS);
        this.precedences.put(TokenType.NOT_EQ, Precedence.EQUALS);
        this.precedences.put(TokenType.LT, Precedence.COMPARE);
        this.precedences.put(TokenType.GT, Precedence.COMPARE);
        this.precedences.put(TokenType.LT_EQ, Precedence.COMPARE);
        this.precedences.put(TokenType.GT_EQ, Precedence.COMPARE);
        this.precedences.put(TokenType.PLUS, Precedence.SUM);
        this.precedences.put(TokenType.MINUS, Precedence.SUM);
        this.precedences.put(TokenType.ASTERISK, Precedence.PRODUCT);
        this.precedences.put(TokenType.SLASH, Precedence.PRODUCT);
        this.precedences.put(TokenType.PERCENT, Precedence.PRODUCT);
        this.precedences.put(TokenType.LPAREN, Precedence.CALL);
        this.precedences.put(TokenType.LBRACKET, Precedence.CALL);
        
        // Initialize with next two tokens
        nextToken();
        nextToken();
        
        // Register prefix parse functions
        registerPrefix(TokenType.IDENTIFIER, this::parseIdentifier);
        registerPrefix(TokenType.NUMBER, this::parseNumberLiteral);
        registerPrefix(TokenType.STRING, this::parseStringLiteral);
        registerPrefix(TokenType.TRUE, this::parseBooleanLiteral);
        registerPrefix(TokenType.FALSE, this::parseBooleanLiteral);
        registerPrefix(TokenType.NULL, this::parseNullLiteral);
        registerPrefix(TokenType.LPAREN, this::parseGroupedExpression);
        registerPrefix(TokenType.MINUS, this::parsePrefixExpression);
        registerPrefix(TokenType.NOT, this::parsePrefixExpression);
        registerPrefix(TokenType.LBRACKET, this::parseArrayLiteral);
        
        // Register infix parse functions
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(TokenType.SLASH, this::parseInfixExpression);
        registerInfix(TokenType.PERCENT, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
        registerInfix(TokenType.LT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.GT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.AND, this::parseInfixExpression);
        registerInfix(TokenType.OR, this::parseInfixExpression);
        registerInfix(TokenType.LPAREN, this::parseCallExpression);
        registerInfix(TokenType.LBRACKET, this::parseIndexExpression);
    }
    
    /**
     * Register a prefix parse function
     */
    private void registerPrefix(TokenType tokenType, Supplier<Node> fn) {
        prefixParseFns.put(tokenType, fn);
    }
    
    /**
     * Register an infix parse function
     */
    private void registerInfix(TokenType tokenType, Function<Node, Node> fn) {
        infixParseFns.put(tokenType, fn);
    }
    
    /**
     * Get the parsing errors
     */
    public List<Error> getErrors() {
        return errors;
    }
    
    /**
     * Parse the entire program
     */
    public Program parseProgram() {
        Program program = new Program();
        
        while (!currentTokenIs(TokenType.EOF)) {
            Node stmt = parseStatement();
            if (stmt != null) {
                program.addStatement(stmt);
            }
            nextToken();
        }
        
        return program;
    }
    
    /**
     * Parse a statement
     */
    private Node parseStatement() {
        // Skeleton implementation
        switch (currentToken.getType()) {
            case LET:
                return parseVariableDeclaration();
            case DEF:
                return parseFunctionDeclaration();
            case IF:
                return parseIfStatement();
            case WHILE:
                return parseWhileStatement();
            case RETURN:
                return parseReturnStatement();
            case LBRACE:
                return parseBlockStatement();
            default:
                // Check for index assignment (array[index] = value)
                if (currentTokenIs(TokenType.IDENTIFIER)) {
                    Token lookAhead = peekToken;
                    if (lookAhead.getType() == TokenType.LBRACKET) {
                        // Remember the current position in the token stream
                        int savedPosition = currentPosition;
                        String identifier = currentToken.getLiteral();
                        
                        // Parse the index expression
                        nextToken(); // Move past identifier to '['
                        if (!currentTokenIs(TokenType.LBRACKET)) {
                            // If not at '[', restore position and treat as regular expression
                            currentPosition = savedPosition;
                            peekToken = tokens.get(currentPosition - 1);
                            currentToken = tokens.get(currentPosition - 2);
                            return parseExpressionStatement();
                        }
                        
                        nextToken(); // Move past '['
                        Node index = parseExpression(Precedence.LOWEST);
                        
                        if (!expectPeek(TokenType.RBRACKET)) {
                            // Error in syntax, restore position and try as expression
                            currentPosition = savedPosition;
                            peekToken = tokens.get(currentPosition - 1);
                            currentToken = tokens.get(currentPosition - 2);
                            return parseExpressionStatement();
                        }
                        
                        // Check if the next token is '=' for assignment
                        if (peekTokenIs(TokenType.ASSIGN)) {
                            // It's an assignment, parse it as IndexAssignmentStatement
                            // Reset position and process through parseIndexAssignmentStatement
                            currentPosition = savedPosition;
                            peekToken = tokens.get(currentPosition - 1);
                            currentToken = tokens.get(currentPosition - 2);
                            return parseIndexAssignmentStatement();
                        } else {
                            // It's just an index expression
                            currentPosition = savedPosition;
                            peekToken = tokens.get(currentPosition - 1);
                            currentToken = tokens.get(currentPosition - 2);
                            return parseExpressionStatement();
                        }
                    }
                    // Check for assignment statements (identifier = expression)
                    else if (peekTokenIs(TokenType.ASSIGN)) {
                        return parseAssignmentStatement();
                    }
                }
                return parseExpressionStatement();
        }
    }
    
    /**
     * Advance to the next token
     */
    private void nextToken() {
        currentToken = peekToken;
        
        if (currentPosition < tokens.size()) {
            peekToken = tokens.get(currentPosition);
            currentPosition++;
        } else {
            Token nextToken = lexer.nextToken();
            tokens.add(nextToken);
            peekToken = nextToken;
            currentPosition++;
        }
    }
    
    /**
     * Check if the current token is of the given type
     */
    private boolean currentTokenIs(TokenType tokenType) {
        return currentToken != null && currentToken.getType() == tokenType;
    }
    
    /**
     * Check if the next token is of the given type
     */
    private boolean peekTokenIs(TokenType tokenType) {
        return peekToken != null && peekToken.getType() == tokenType;
    }
    
    /**
     * Expect the next token to be of the given type, and advance if it is
     */
    private boolean expectPeek(TokenType tokenType) {
        if (peekTokenIs(tokenType)) {
            nextToken();
            return true;
        } else {
            peekError(tokenType);
            return false;
        }
    }
    
    /**
     * Add a peek error
     */
    private void peekError(TokenType tokenType) {
        String msg = String.format("Expected next token to be %s, got %s instead",
                tokenType, peekToken != null ? peekToken.getType() : "null");
        errors.add(new Error(msg, 
                peekToken != null ? peekToken.getLine() : 0,
                peekToken != null ? peekToken.getColumn() : 0));
    }
    
    /**
     * Get the precedence of the peek token
     */
    private Precedence peekPrecedence() {
        Precedence p = precedences.get(peekToken != null ? peekToken.getType() : null);
        return p != null ? p : Precedence.LOWEST;
    }
    
    /**
     * Get the precedence of the current token
     */
    private Precedence currentPrecedence() {
        Precedence p = precedences.get(currentToken != null ? currentToken.getType() : null);
        return p != null ? p : Precedence.LOWEST;
    }
    
    // Placeholder methods for parsing different types of statements and expressions
    // These would be implemented in a full parser
    
    /**
     * Parse a variable declaration statement
     */
    private Node parseVariableDeclaration() {
        // let x = 5;
        Token token = currentToken;  // 'let' token
        
        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null;
        }
        
        String name = currentToken.getLiteral();
        
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        
        nextToken();
        
        Node initializer = parseExpression(Precedence.LOWEST);
        
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        
        VariableDeclaration variableDeclaration = new VariableDeclaration(name, initializer);
        variableDeclaration.setPosition(token.getLine(), token.getColumn());
        
        return variableDeclaration;
    }
    
    /**
     * Parse a function declaration
     */
    private Node parseFunctionDeclaration() {
        Token token = currentToken;  // 'def' token
        
        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null;
        }
        
        String name = currentToken.getLiteral();
        
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        
        List<String> parameters = parseFunctionParameters();
        
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        
        Node body = parseBlockStatement();
        
        FunctionDeclaration function = new FunctionDeclaration(name, parameters, body);
        function.setPosition(token.getLine(), token.getColumn());
        
        return function;
    }
    
    /**
     * Parse function parameters
     */
    private List<String> parseFunctionParameters() {
        List<String> parameters = new ArrayList<>();
        
        // Handle the case of no parameters: def fn() { ... }
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return parameters;
        }
        
        nextToken();
        
        // First parameter
        parameters.add(currentToken.getLiteral());
        
        // Parse the rest of the parameters
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();  // consume comma
            nextToken();  // move to parameter name
            
            parameters.add(currentToken.getLiteral());
        }
        
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        
        return parameters;
    }
    
    /**
     * Parse an if statement
     */
    private Node parseIfStatement() {
        Token token = currentToken;  // 'if' token
        
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        
        nextToken();
        Node condition = parseExpression(Precedence.LOWEST);
        
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        
        Node consequence = parseBlockStatement();
        
        Node alternative = null;
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }
            
            alternative = parseBlockStatement();
        }
        
        IfStatement ifStatement = new IfStatement(condition, consequence, alternative);
        ifStatement.setPosition(token.getLine(), token.getColumn());
        
        return ifStatement;
    }
    
    /**
     * Parse a return statement
     */
    private Node parseReturnStatement() {
        Token token = currentToken;  // 'return' token
        
        nextToken();
        
        Node value = null;
        if (!currentTokenIs(TokenType.SEMICOLON)) {
            value = parseExpression(Precedence.LOWEST);
        }
        
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        
        ReturnStatement returnStatement = new ReturnStatement(value);
        returnStatement.setPosition(token.getLine(), token.getColumn());
        
        return returnStatement;
    }
    
    /**
     * Parse a while statement
     */
    private Node parseWhileStatement() {
        Token token = currentToken;  // 'while' token
        
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        
        nextToken();
        Node condition = parseExpression(Precedence.LOWEST);
        
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        
        Node body = parseBlockStatement();
        
        WhileStatement whileStatement = new WhileStatement(condition, body);
        whileStatement.setPosition(token.getLine(), token.getColumn());
        
        return whileStatement;
    }
    
    /**
     * Parse a block statement
     */
    private Node parseBlockStatement() {
        Token token = currentToken;  // '{' token
        BlockStatement block = new BlockStatement();
        block.setPosition(token.getLine(), token.getColumn());
        
        nextToken();
        
        while (!currentTokenIs(TokenType.RBRACE) && !currentTokenIs(TokenType.EOF)) {
            Node statement = parseStatement();
            
            if (statement != null) {
                block.addStatement(statement);
            }
            
            nextToken();
        }
        
        if (!currentTokenIs(TokenType.RBRACE)) {
            errors.add(new Error(
                    "Expected '}' at the end of block statement",
                    currentToken.getLine(),
                    currentToken.getColumn()
            ));
            return null;
        }
        
        return block;
    }
    
    /**
     * Parse an expression statement
     */
    private Node parseExpressionStatement() {
        Token token = currentToken;
        Node expression = parseExpression(Precedence.LOWEST);
        
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        
        ExpressionStatement statement = new ExpressionStatement(expression);
        statement.setPosition(token.getLine(), token.getColumn());
        
        return statement;
    }
    
    /**
     * Parse an expression with the given precedence
     */
    private Node parseExpression(Precedence precedence) {
        // Get the prefix parsing function for the current token
        Supplier<Node> prefix = prefixParseFns.get(currentToken.getType());
        if (prefix == null) {
            errors.add(new Error(
                    "No prefix parse function for " + currentToken.getType() + " (" + currentToken.getLiteral() + ")",
                    currentToken.getLine(),
                    currentToken.getColumn()
            ));
            return null;
        }
        
        // Parse the prefix expression
        Node leftExp = prefix.get();
        
        // Continue parsing infix expressions as long as the precedence
        // is lower than the next operator
        while (!peekTokenIs(TokenType.SEMICOLON) && 
               precedence.getValue() < peekPrecedence().getValue()) {
            
            Function<Node, Node> infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }
            
            nextToken();
            
            leftExp = infix.apply(leftExp);
        }
        
        return leftExp;
    }
    
    /**
     * Parse an identifier
     */
    private Node parseIdentifier() {
        Identifier identifier = new Identifier(currentToken.getLiteral());
        identifier.setPosition(currentToken.getLine(), currentToken.getColumn());
        return identifier;
    }
    
    /**
     * Parse a number literal
     */
    private Node parseNumberLiteral() {
        try {
            double value = Double.parseDouble(currentToken.getLiteral());
            NumberLiteral numberLiteral = new NumberLiteral(value);
            numberLiteral.setPosition(currentToken.getLine(), currentToken.getColumn());
            return numberLiteral;
        } catch (NumberFormatException e) {
            errors.add(new Error(
                    "Could not parse '" + currentToken.getLiteral() + "' as a number",
                    currentToken.getLine(),
                    currentToken.getColumn()
            ));
            return null;
        }
    }
    
    /**
     * Parse a string literal
     */
    private Node parseStringLiteral() {
        StringLiteral stringLiteral = new StringLiteral(currentToken.getLiteral());
        stringLiteral.setPosition(currentToken.getLine(), currentToken.getColumn());
        return stringLiteral;
    }
    
    /**
     * Parse a boolean literal
     */
    private Node parseBooleanLiteral() {
        BooleanLiteral booleanLiteral = new BooleanLiteral(
                currentToken.getType() == TokenType.TRUE
        );
        booleanLiteral.setPosition(currentToken.getLine(), currentToken.getColumn());
        return booleanLiteral;
    }
    
    /**
     * Parse a null literal
     */
    private Node parseNullLiteral() {
        NullLiteral nullLiteral = new NullLiteral();
        nullLiteral.setPosition(currentToken.getLine(), currentToken.getColumn());
        return nullLiteral;
    }
    
    /**
     * Parse a grouped expression (parenthesized expression)
     */
    private Node parseGroupedExpression() {
        nextToken();
        
        Node expression = parseExpression(Precedence.LOWEST);
        
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        
        return expression;
    }
    
    /**
     * Parse a prefix expression
     */
    private Node parsePrefixExpression() {
        Token token = currentToken;
        String operator = currentToken.getLiteral();
        
        nextToken();
        
        Node right = parseExpression(Precedence.PREFIX);
        
        PrefixExpression prefixExpression = new PrefixExpression(operator, right);
        prefixExpression.setPosition(token.getLine(), token.getColumn());
        
        return prefixExpression;
    }
    
    /**
     * Parse an infix expression
     */
    private Node parseInfixExpression(Node left) {
        Token token = currentToken;
        String operator = currentToken.getLiteral();
        
        Precedence precedence = currentPrecedence();
        nextToken();
        
        Node right = parseExpression(precedence);
        
        InfixExpression infixExpression = new InfixExpression(left, operator, right);
        infixExpression.setPosition(token.getLine(), token.getColumn());
        
        return infixExpression;
    }
    
    /**
     * Parse a call expression
     */
    private Node parseCallExpression(Node function) {
        Token token = currentToken;
        List<Node> arguments = parseCallArguments();
        
        CallExpression callExpression = new CallExpression(function, arguments);
        callExpression.setPosition(token.getLine(), token.getColumn());
        
        return callExpression;
    }
    
    /**
     * Parse call arguments
     */
    private List<Node> parseCallArguments() {
        List<Node> args = new ArrayList<>();
        
        // Handle the case of no arguments: fn()
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return args;
        }
        
        nextToken();
        
        // First argument
        args.add(parseExpression(Precedence.LOWEST));
        
        // Parse the rest of the arguments
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();  // consume comma
            nextToken();  // move to next expression
            
            args.add(parseExpression(Precedence.LOWEST));
        }
        
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        
        return args;
    }
    
    /**
     * Parse an assignment statement
     */
    private Node parseAssignmentStatement() {
        Token token = currentToken;  // identifier token
        String name = currentToken.getLiteral();
        
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        
        nextToken();
        
        Node value = parseExpression(Precedence.LOWEST);
        
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        
        AssignmentStatement assignmentStatement = new AssignmentStatement(name, value);
        assignmentStatement.setPosition(token.getLine(), token.getColumn());
        
        return assignmentStatement;
    }
    
    /**
     * Parse an array literal expression [1, 2, 3]
     */
    private Node parseArrayLiteral() {
        Token token = currentToken;  // '[' token
        List<Node> elements = parseArrayElements();
        
        ArrayLiteral arrayLiteral = new ArrayLiteral(elements);
        arrayLiteral.setPosition(token.getLine(), token.getColumn());
        
        return arrayLiteral;
    }
    
    /**
     * Parse array elements
     */
    private List<Node> parseArrayElements() {
        List<Node> elements = new ArrayList<>();
        
        // Handle the case of empty array: []
        if (peekTokenIs(TokenType.RBRACKET)) {
            nextToken();
            return elements;
        }
        
        nextToken();
        
        // First element
        elements.add(parseExpression(Precedence.LOWEST));
        
        // Parse the rest of the elements
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();  // consume comma
            nextToken();  // move to next expression
            
            elements.add(parseExpression(Precedence.LOWEST));
        }
        
        if (!expectPeek(TokenType.RBRACKET)) {
            return null;
        }
        
        return elements;
    }
    
    /**
     * Parse an index expression (array[index])
     */
    private Node parseIndexExpression(Node array) {
        Token token = currentToken;  // '[' token
        
        nextToken();  // Move past '['
        Node index = parseExpression(Precedence.LOWEST);
        
        if (!expectPeek(TokenType.RBRACKET)) {
            return null;
        }
        
        IndexExpression indexExpression = new IndexExpression(array, index);
        indexExpression.setPosition(token.getLine(), token.getColumn());
        
        return indexExpression;
    }
    
    /**
     * Parse an index assignment statement (array[index] = value)
     */
    private Node parseIndexAssignmentStatement() {
        Token token = currentToken;  // identifier token
        String identifier = currentToken.getLiteral();
        
        // Create the array identifier node
        Identifier array = new Identifier(identifier);
        array.setPosition(token.getLine(), token.getColumn());
        
        nextToken();  // Move to '['
        
        if (!currentTokenIs(TokenType.LBRACKET)) {
            errors.add(new Error(
                "Expected '[' in index expression",
                currentToken.getLine(),
                currentToken.getColumn()
            ));
            return null;
        }
        
        nextToken();  // Move past '['
        Node index = parseExpression(Precedence.LOWEST);
        
        if (!expectPeek(TokenType.RBRACKET)) {
            return null;
        }
        
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        
        nextToken();  // Move past '='
        Node value = parseExpression(Precedence.LOWEST);
        
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();  // Optional semicolon
        }
        
        IndexAssignmentStatement indexAssignmentStatement = new IndexAssignmentStatement(array, index, value);
        indexAssignmentStatement.setPosition(token.getLine(), token.getColumn());
        
        return indexAssignmentStatement;
    }
} 