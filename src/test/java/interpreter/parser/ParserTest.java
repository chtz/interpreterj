package interpreter.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import interpreter.ast.AssignmentStatement;
import interpreter.ast.BlockStatement;
import interpreter.ast.BooleanLiteral;
import interpreter.ast.CallExpression;
import interpreter.ast.ExpressionStatement;
import interpreter.ast.FunctionDeclaration;
import interpreter.ast.Identifier;
import interpreter.ast.IfStatement;
import interpreter.ast.InfixExpression;
import interpreter.ast.Node;
import interpreter.ast.NumberLiteral;
import interpreter.ast.Program;
import interpreter.ast.ReturnStatement;
import interpreter.ast.VariableDeclaration;
import interpreter.ast.WhileStatement;
import interpreter.lexer.Lexer;

/**
 * JUnit-based test suite for the Parser
 */
public class ParserTest {
    @Test
    @DisplayName("Test parsing of variable declarations")
    public void testVariableDeclaration() {
        String input = "let x = 5;";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof VariableDeclaration, "Statement should be a VariableDeclaration");
        
        VariableDeclaration varDecl = (VariableDeclaration) statement;
        assertEquals("x", varDecl.getName(), "Variable name should be 'x'");
        
        Node initializer = varDecl.getInitializer();
        assertTrue(initializer instanceof NumberLiteral, "Initializer should be a NumberLiteral");
        
        NumberLiteral numberLiteral = (NumberLiteral) initializer;
        assertEquals(5.0, numberLiteral.getValue(), "NumberLiteral value should be 5.0");
    }
    
    @Test
    @DisplayName("Test parsing of assignment statements")
    public void testAssignmentStatement() {
        String input = "x = 5;";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof AssignmentStatement, "Statement should be an AssignmentStatement");
        
        AssignmentStatement assignment = (AssignmentStatement) statement;
        assertEquals("x", assignment.getName(), "Variable name should be 'x'");
        
        Node value = assignment.getValue();
        assertTrue(value instanceof NumberLiteral, "Value should be a NumberLiteral");
        
        NumberLiteral numberLiteral = (NumberLiteral) value;
        assertEquals(5.0, numberLiteral.getValue(), "NumberLiteral value should be 5.0");
    }
    
    @Test
    @DisplayName("Test parsing of if statements")
    public void testIfStatement() {
        String input = "if (x > 5) { return true; } else { return false; }";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof IfStatement, "Statement should be an IfStatement");
        
        IfStatement ifStatement = (IfStatement) statement;
        assertTrue(ifStatement.getCondition() instanceof InfixExpression, "Condition should be an InfixExpression");
        assertTrue(ifStatement.getConsequence() instanceof BlockStatement, "Consequence should be a BlockStatement");
        assertTrue(ifStatement.getAlternative() instanceof BlockStatement, "Alternative should be a BlockStatement");
        
        // Check the condition: x > 5
        InfixExpression condition = (InfixExpression) ifStatement.getCondition();
        assertEquals(">", condition.getOperator(), "Operator should be '>'");
        assertTrue(condition.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(condition.getRight() instanceof NumberLiteral, "Right side should be a NumberLiteral");
        
        Identifier left = (Identifier) condition.getLeft();
        assertEquals("x", left.getName(), "Identifier name should be 'x'");
        
        NumberLiteral right = (NumberLiteral) condition.getRight();
        assertEquals(5.0, right.getValue(), "NumberLiteral value should be 5.0");
        
        // Check the consequence: { return true; }
        BlockStatement consequence = (BlockStatement) ifStatement.getConsequence();
        assertEquals(1, consequence.getStatements().size(), "Consequence should have exactly one statement");
        assertTrue(consequence.getStatements().get(0) instanceof ReturnStatement, "Statement should be a ReturnStatement");
        
        ReturnStatement returnTrue = (ReturnStatement) consequence.getStatements().get(0);
        assertTrue(returnTrue.getValue() instanceof BooleanLiteral, "Return value should be a BooleanLiteral");
        assertTrue(((BooleanLiteral) returnTrue.getValue()).getValue(), "Return value should be true");
        
        // Check the alternative: { return false; }
        BlockStatement alternative = (BlockStatement) ifStatement.getAlternative();
        assertEquals(1, alternative.getStatements().size(), "Alternative should have exactly one statement");
        assertTrue(alternative.getStatements().get(0) instanceof ReturnStatement, "Statement should be a ReturnStatement");
        
        ReturnStatement returnFalse = (ReturnStatement) alternative.getStatements().get(0);
        assertTrue(returnFalse.getValue() instanceof BooleanLiteral, "Return value should be a BooleanLiteral");
        assertFalse(((BooleanLiteral) returnFalse.getValue()).getValue(), "Return value should be false");
    }
    
    @Test
    @DisplayName("Test parsing of while statements")
    public void testWhileStatement() {
        String input = "while (i < 10) { i = i + 1; }";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof WhileStatement, "Statement should be a WhileStatement");
        
        WhileStatement whileStatement = (WhileStatement) statement;
        assertTrue(whileStatement.getCondition() instanceof InfixExpression, "Condition should be an InfixExpression");
        assertTrue(whileStatement.getBody() instanceof BlockStatement, "Body should be a BlockStatement");
        
        // Check the condition: i < 10
        InfixExpression condition = (InfixExpression) whileStatement.getCondition();
        assertEquals("<", condition.getOperator(), "Operator should be '<'");
        assertTrue(condition.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(condition.getRight() instanceof NumberLiteral, "Right side should be a NumberLiteral");
        
        Identifier left = (Identifier) condition.getLeft();
        assertEquals("i", left.getName(), "Identifier name should be 'i'");
        
        NumberLiteral right = (NumberLiteral) condition.getRight();
        assertEquals(10.0, right.getValue(), "NumberLiteral value should be 10.0");
        
        // Check the body: { i = i + 1; }
        BlockStatement body = (BlockStatement) whileStatement.getBody();
        assertEquals(1, body.getStatements().size(), "Body should have exactly one statement");
        assertTrue(body.getStatements().get(0) instanceof AssignmentStatement, "Statement should be an AssignmentStatement");
        
        AssignmentStatement assignment = (AssignmentStatement) body.getStatements().get(0);
        assertEquals("i", assignment.getName(), "Variable name should be 'i'");
        assertTrue(assignment.getValue() instanceof InfixExpression, "Value should be an InfixExpression");
        
        InfixExpression value = (InfixExpression) assignment.getValue();
        assertEquals("+", value.getOperator(), "Operator should be '+'");
        assertTrue(value.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(value.getRight() instanceof NumberLiteral, "Right side should be a NumberLiteral");
        
        Identifier valueLeft = (Identifier) value.getLeft();
        assertEquals("i", valueLeft.getName(), "Identifier name should be 'i'");
        
        NumberLiteral valueRight = (NumberLiteral) value.getRight();
        assertEquals(1.0, valueRight.getValue(), "NumberLiteral value should be 1.0");
    }
    
    @Test
    @DisplayName("Test parsing of function declarations")
    public void testFunctionDeclaration() {
        String input = "def add(a, b) { return a + b; }";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof FunctionDeclaration, "Statement should be a FunctionDeclaration");
        
        FunctionDeclaration funcDecl = (FunctionDeclaration) statement;
        assertEquals("add", funcDecl.getName(), "Function name should be 'add'");
        
        List<String> parameters = funcDecl.getParameters();
        assertEquals(2, parameters.size(), "Function should have exactly two parameters");
        assertEquals("a", parameters.get(0), "First parameter should be 'a'");
        assertEquals("b", parameters.get(1), "Second parameter should be 'b'");
        
        BlockStatement body = (BlockStatement) funcDecl.getBody();
        assertEquals(1, body.getStatements().size(), "Body should have exactly one statement");
        assertTrue(body.getStatements().get(0) instanceof ReturnStatement, "Statement should be a ReturnStatement");
        
        ReturnStatement returnStmt = (ReturnStatement) body.getStatements().get(0);
        assertTrue(returnStmt.getValue() instanceof InfixExpression, "Return value should be an InfixExpression");
        
        InfixExpression returnValue = (InfixExpression) returnStmt.getValue();
        assertEquals("+", returnValue.getOperator(), "Operator should be '+'");
        assertTrue(returnValue.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(returnValue.getRight() instanceof Identifier, "Right side should be an Identifier");
        
        Identifier returnValueLeft = (Identifier) returnValue.getLeft();
        assertEquals("a", returnValueLeft.getName(), "Identifier name should be 'a'");
        
        Identifier returnValueRight = (Identifier) returnValue.getRight();
        assertEquals("b", returnValueRight.getName(), "Identifier name should be 'b'");
    }
    
    @Test
    @DisplayName("Test parsing of call expressions")
    public void testCallExpression() {
        String input = "add(1, 2 + 3);";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof ExpressionStatement, "Statement should be an ExpressionStatement");
        
        ExpressionStatement exprStmt = (ExpressionStatement) statement;
        assertTrue(exprStmt.getExpression() instanceof CallExpression, "Expression should be a CallExpression");
        
        CallExpression callExpr = (CallExpression) exprStmt.getExpression();
        assertTrue(callExpr.getCallee() instanceof Identifier, "Function should be an Identifier");
        
        Identifier function = (Identifier) callExpr.getCallee();
        assertEquals("add", function.getName(), "Function name should be 'add'");
        
        List<Node> arguments = callExpr.getArguments();
        assertEquals(2, arguments.size(), "Call should have exactly two arguments");
        assertTrue(arguments.get(0) instanceof NumberLiteral, "First argument should be a NumberLiteral");
        assertTrue(arguments.get(1) instanceof InfixExpression, "Second argument should be an InfixExpression");
        
        NumberLiteral arg1 = (NumberLiteral) arguments.get(0);
        assertEquals(1.0, arg1.getValue(), "First argument value should be 1.0");
        
        InfixExpression arg2 = (InfixExpression) arguments.get(1);
        assertEquals("+", arg2.getOperator(), "Operator should be '+'");
        assertTrue(arg2.getLeft() instanceof NumberLiteral, "Left side should be a NumberLiteral");
        assertTrue(arg2.getRight() instanceof NumberLiteral, "Right side should be a NumberLiteral");
        
        NumberLiteral arg2Left = (NumberLiteral) arg2.getLeft();
        assertEquals(2.0, arg2Left.getValue(), "Left value should be 2.0");
        
        NumberLiteral arg2Right = (NumberLiteral) arg2.getRight();
        assertEquals(3.0, arg2Right.getValue(), "Right value should be 3.0");
    }
    
    @Test
    @DisplayName("Test operator precedence")
    public void testOperatorPrecedence() {
        // Test that "a + b * c" is parsed as "a + (b * c)" and not "(a + b) * c"
        String input = "a + b * c;";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof ExpressionStatement, "Statement should be an ExpressionStatement");
        
        ExpressionStatement exprStmt = (ExpressionStatement) statement;
        assertTrue(exprStmt.getExpression() instanceof InfixExpression, "Expression should be an InfixExpression");
        
        InfixExpression infixExpr = (InfixExpression) exprStmt.getExpression();
        assertEquals("+", infixExpr.getOperator(), "Root operator should be '+'");
        assertTrue(infixExpr.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(infixExpr.getRight() instanceof InfixExpression, "Right side should be an InfixExpression");
        
        Identifier left = (Identifier) infixExpr.getLeft();
        assertEquals("a", left.getName(), "Left identifier should be 'a'");
        
        InfixExpression right = (InfixExpression) infixExpr.getRight();
        assertEquals("*", right.getOperator(), "Right operator should be '*'");
        assertTrue(right.getLeft() instanceof Identifier, "Right-Left should be an Identifier");
        assertTrue(right.getRight() instanceof Identifier, "Right-Right should be an Identifier");
        
        Identifier rightLeft = (Identifier) right.getLeft();
        assertEquals("b", rightLeft.getName(), "Right-Left identifier should be 'b'");
        
        Identifier rightRight = (Identifier) right.getRight();
        assertEquals("c", rightRight.getName(), "Right-Right identifier should be 'c'");
    }
    
    @Test
    @DisplayName("Test logical operator precedence")
    public void testLogicalOperatorPrecedence() {
        // Test that "a || b && c" is parsed as "a || (b && c)" and not "(a || b) && c"
        String input = "a || b && c;";
        Program program = parseProgram(input);
        
        assertEquals(1, program.getStatements().size(), "Program should have exactly one statement");
        
        Node statement = program.getStatements().get(0);
        assertTrue(statement instanceof ExpressionStatement, "Statement should be an ExpressionStatement");
        
        ExpressionStatement exprStmt = (ExpressionStatement) statement;
        assertTrue(exprStmt.getExpression() instanceof InfixExpression, "Expression should be an InfixExpression");
        
        InfixExpression infixExpr = (InfixExpression) exprStmt.getExpression();
        assertEquals("||", infixExpr.getOperator(), "Root operator should be '||'");
        assertTrue(infixExpr.getLeft() instanceof Identifier, "Left side should be an Identifier");
        assertTrue(infixExpr.getRight() instanceof InfixExpression, "Right side should be an InfixExpression");
        
        Identifier left = (Identifier) infixExpr.getLeft();
        assertEquals("a", left.getName(), "Left identifier should be 'a'");
        
        InfixExpression right = (InfixExpression) infixExpr.getRight();
        assertEquals("&&", right.getOperator(), "Right operator should be '&&'");
        assertTrue(right.getLeft() instanceof Identifier, "Right-Left should be an Identifier");
        assertTrue(right.getRight() instanceof Identifier, "Right-Right should be an Identifier");
        
        Identifier rightLeft = (Identifier) right.getLeft();
        assertEquals("b", rightLeft.getName(), "Right-Left identifier should be 'b'");
        
        Identifier rightRight = (Identifier) right.getRight();
        assertEquals("c", rightRight.getName(), "Right-Right identifier should be 'c'");
    }
    
    private Program parseProgram(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        
        // Check for parsing errors
        List<Parser.Error> errors = parser.getErrors();
        if (!errors.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("Parser has errors:\n");
            for (Parser.Error error : errors) {
                errorMsg.append(error.toString()).append("\n");
            }
            fail(errorMsg.toString());
        }
        
        return program;
    }
} 