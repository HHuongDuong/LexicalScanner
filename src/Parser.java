import java.text.ParseException;

public class Parser {
    private Controller lexer;
    private Token currentToken;

    public Parser(Controller lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.peekToken();
    }

    private void advance() {
        currentToken = lexer.nextToken();
    }

    private boolean match(LexicalScanner.Type type) {
        if (currentToken.getType() == type) {
            advance();
            return true;
        }
        return false;
    }

    private void expect(LexicalScanner.Type type) throws ParseException {
        if (!match(type)) {
            throw new ParseException("Expected " + type + " but found " + currentToken.type, 0);
        }
    }

    public ASTNode parseProgram() throws ParseException {
        ASTNode programNode = new ASTNode("Program", null);
        while (currentToken.getType() != LexicalScanner.Type.EOF) {
            if (isType(currentToken)) {
                advance();
                if (lexer.peekToken().getType() == LexicalScanner.Type.Identifier) {
                    if (isFunctionDeclaration()) {
                        programNode.addChild(parseFuncDecl());
                    } else {
                        programNode.addChild(parseVarDecl());
                    }
                } else {
                    programNode.addChild(parseVarDecl());
                }
            } else {
                throw new ParseException("Unexpected token: " + currentToken.value, 0);
            }
        }
        return programNode;
    }

    private boolean isFunctionDeclaration() {
        Token nextToken = lexer.peek2Token();
        return nextToken != null && nextToken.value.equals("(");
    }

    private boolean isType(Token token) {
        return token.value.equals("void") ||
                token.value.equals("boolean") ||
                token.value.equals("int") ||
                token.value.equals("float");
    }

    private ASTNode parseFuncDecl() throws ParseException {
        ASTNode funcDeclNode = new ASTNode("FuncDecl", null);
        funcDeclNode.addChild(parseType());
        funcDeclNode.addChild(new ASTNode("Identifier", currentToken.value));
        expect(LexicalScanner.Type.Identifier);
        funcDeclNode.addChild(parseParaList());
        funcDeclNode.addChild(parseCompoundStmt());
        return funcDeclNode;
    }

    private ASTNode parseVarDecl() throws ParseException {
        ASTNode varDeclNode = new ASTNode("VarDecl", null);
        varDeclNode.addChild(parseType());
        varDeclNode.addChild(parseInitDeclaratorList());
        expect(LexicalScanner.Type.Separator);
        return varDeclNode;
    }

    private ASTNode parseInitDeclaratorList() throws ParseException {
        ASTNode initDeclaratorListNode = new ASTNode("InitDeclaratorList", null);
        initDeclaratorListNode.addChild(parseInitDeclarator());
        while ((currentToken.type == LexicalScanner.Type.Separator) && currentToken.value.equals(",")) {
            advance();
            initDeclaratorListNode.addChild(parseInitDeclarator());
        }
        return initDeclaratorListNode;
    }

    private ASTNode parseInitDeclarator() throws ParseException {
        ASTNode initDeclaratorNode = new ASTNode("InitDeclarator", null);
        initDeclaratorNode.addChild(parseDeclarator());
        if ((currentToken.type == LexicalScanner.Type.Operator) && currentToken.value.equals("=")) {
            advance();
            initDeclaratorNode.addChild(parseInitialiser());
        }
        return initDeclaratorNode;
    }

    private ASTNode parseDeclarator() throws ParseException {
        ASTNode declaratorNode = new ASTNode("Declarator", currentToken.value);
        expect(LexicalScanner.Type.Identifier);
        if ((currentToken.type == LexicalScanner.Type.Separator) && currentToken.value.equals("[")) {
            advance();
            if (currentToken.type == LexicalScanner.Type.StrLiteral) {
                advance();
                declaratorNode.addChild(new ASTNode("IntLiteral", currentToken.value));
            }
            expect(LexicalScanner.Type.Separator);
        }
        return declaratorNode;
    }

    private ASTNode parseInitialiser() throws ParseException {
        ASTNode initialiserNode = new ASTNode("Initialiser", null);
        if ((currentToken.type == LexicalScanner.Type.Separator) && currentToken.value.equals("{")) {
            advance();
            initialiserNode.addChild(parseExpr());
            while (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals(",")) {
                advance();
                initialiserNode.addChild(parseExpr());
            }
            expect(LexicalScanner.Type.Separator);
        } else {
            initialiserNode.addChild(parseExpr());
        }
        return initialiserNode;
    }

    private ASTNode parseType() throws ParseException {
        if (!isType(currentToken)) {
            throw new ParseException("Expected type but found " + currentToken.type, 0);
        }
        ASTNode typeNode = new ASTNode("Type", currentToken.value);
        advance();
        return typeNode;
    }

    private ASTNode parseParaList() throws ParseException {
        ASTNode paraListNode = new ASTNode("ParaList", null);
        expect(LexicalScanner.Type.Separator);
        if (currentToken.getType() != LexicalScanner.Type.Separator || !currentToken.value.equals(")")) {
            paraListNode.addChild(parseProperParaList());
        }
        expect(LexicalScanner.Type.Separator);
        return paraListNode;
    }

    private ASTNode parseProperParaList() throws ParseException {
        ASTNode properParaListNode = new ASTNode("ProperParaList", null);
        properParaListNode.addChild(parseParaDecl());
        while (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals(",")) {
            advance();
            properParaListNode.addChild(parseParaDecl());
        }
        return properParaListNode;
    }

    private ASTNode parseParaDecl() throws ParseException {
        ASTNode paraDeclNode = new ASTNode("ParaDecl", null);
        paraDeclNode.addChild(parseType());
        paraDeclNode.addChild(parseDeclarator());
        return paraDeclNode;
    }

    private ASTNode parseCompoundStmt() throws ParseException {
        ASTNode compoundStmtNode = new ASTNode("CompoundStmt", null);
        expect(LexicalScanner.Type.Separator);
        while (isType(currentToken)) {
            compoundStmtNode.addChild(parseVarDecl());
        }
        while (currentToken.getType() != LexicalScanner.Type.Separator || !currentToken.value.equals("}")) {
            compoundStmtNode.addChild(parseStmt());
        }
        expect(LexicalScanner.Type.Separator);
        return compoundStmtNode;
    }

    private ASTNode parseStmt() throws ParseException {
        ASTNode stmtNode;
        switch (currentToken.value) {
            case "if":
                stmtNode = parseIfStmt();
                break;
            case "for":
                stmtNode = parseForStmt();
                break;
            case "while":
                stmtNode = parseWhileStmt();
                break;
            case "break":
                stmtNode = parseBreakStmt();
                break;
            case "continue":
                stmtNode = parseContinueStmt();
                break;
            case "return":
                stmtNode = parseReturnStmt();
                break;
            default:
                stmtNode = parseExprStmt();
                break;
        }
        return stmtNode;
    }

    private ASTNode parseIfStmt() throws ParseException {
        ASTNode ifStmtNode = new ASTNode("IfStmt", null);
        expect(LexicalScanner.Type.Keyword);
        expect(LexicalScanner.Type.Separator);
        ifStmtNode.addChild(parseExpr());
        expect(LexicalScanner.Type.Separator);
        expect(LexicalScanner.Type.Separator);// {
        ifStmtNode.addChild(parseCompoundStmt());
        expect(LexicalScanner.Type.Separator); // }
        if (currentToken.value.equals("else")) {
            ifStmtNode.addChild(new ASTNode("ElseStmt", null));
            advance();
            ifStmtNode.addChild(parseCompoundStmt());
        }
        return ifStmtNode;
    }

    private ASTNode parseForStmt() throws ParseException {
        ASTNode forStmtNode = new ASTNode("ForStmt", null);
        expect(LexicalScanner.Type.Keyword);
        expect(LexicalScanner.Type.Separator); //(
        if (!currentToken.value.equals(";")) {
            forStmtNode.addChild(parseExpr());
        }
        expect(LexicalScanner.Type.Separator);
        if (!currentToken.value.equals(";")) {
            forStmtNode.addChild(parseExpr());
        }
        expect(LexicalScanner.Type.Separator);
        if (!currentToken.value.equals(")")) {
            forStmtNode.addChild(parseExpr());
        }
        expect(LexicalScanner.Type.Separator); // )
        expect(LexicalScanner.Type.Separator); // {
        forStmtNode.addChild(parseCompoundStmt());
        expect(LexicalScanner.Type.Separator); // }
        return forStmtNode;
    }

    private ASTNode parseWhileStmt() throws ParseException {
        ASTNode whileStmtNode = new ASTNode("WhileStmt", null);
        expect(LexicalScanner.Type.Keyword);
        expect(LexicalScanner.Type.Separator); // (
        whileStmtNode.addChild(parseExpr());
        expect(LexicalScanner.Type.Separator); // )
        // {
        whileStmtNode.addChild(parseCompoundStmt());
        // }
        return whileStmtNode;
    }

    private ASTNode parseBreakStmt() throws ParseException {
        ASTNode breakStmtNode = new ASTNode("BreakStmt", null);
        expect(LexicalScanner.Type.Keyword);
        expect(LexicalScanner.Type.Separator); // ;
        return breakStmtNode;
    }

    private ASTNode parseContinueStmt() throws ParseException {
        ASTNode continueStmtNode = new ASTNode("ContinueStmt", null);
        expect(LexicalScanner.Type.Keyword);
        expect(LexicalScanner.Type.Separator); // ;
        return continueStmtNode;
    }

    private ASTNode parseReturnStmt() throws ParseException {
        ASTNode returnStmtNode = new ASTNode("ReturnStmt", null);
        expect(LexicalScanner.Type.Keyword);
        if (currentToken.getType() != LexicalScanner.Type.Separator) {
            returnStmtNode.addChild(parseExpr());
        }
        expect(LexicalScanner.Type.Separator); // ;
        return returnStmtNode;
    }

    private ASTNode parseExprStmt() throws ParseException {
        ASTNode exprStmtNode = new ASTNode("ExprStmt", null);
        while (currentToken.getType() != LexicalScanner.Type.Separator) {
            exprStmtNode.addChild(parseExpr());
        }
        expect(LexicalScanner.Type.Separator); // ???
        return exprStmtNode;
    }

    private ASTNode parseExpr() throws ParseException {
        return parseAssignExpr();
    }

    private ASTNode parseAssignExpr() throws ParseException { //???
        if (currentToken.type == LexicalScanner.Type.Operator && currentToken.value.equals("=")) {
            advance();
            ASTNode assignNode = new ASTNode("AssignExpr", "=");
            return assignNode;
        }
        ASTNode assignExprNode = parseOrExpr();
        return assignExprNode;
    }

    private ASTNode parseOrExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator && currentToken.value.equals("||")) {
            advance();
            ASTNode orNode = new ASTNode("OrExpr", "||");
            return orNode;
        }
        ASTNode orExprNode = parseAndExpr();
        return orExprNode;
    }

    private ASTNode parseAndExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator && currentToken.value.equals("&&")) {
            advance();
            ASTNode andNode = new ASTNode("AndExpr", "&&");
            return andNode;
        }
        ASTNode andExprNode = parseEqualityExpr();
        return andExprNode;
    }

    private ASTNode parseEqualityExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator &&
                (currentToken.value.equals("==") || currentToken.value.equals("!="))) {
            ASTNode equalityNode = new ASTNode("EqualityExpr", currentToken.value);
            advance();
            return equalityNode;
        }
        ASTNode equalityExprNode = parseRelExpr();
        return equalityExprNode;
    }

    private ASTNode parseRelExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator &&
                (currentToken.value.equals("<") || currentToken.value.equals(">") ||
                        currentToken.value.equals("<=") || currentToken.value.equals(">="))) {
            ASTNode relNode = new ASTNode("RelExpr", currentToken.value);
            advance();
            return relNode;
        }
        ASTNode relExprNode = parseAddExpr();
        return relExprNode;
    }

    private ASTNode parseAddExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator &&
                (currentToken.value.equals("+") || currentToken.value.equals("-"))) {
            ASTNode addNode = new ASTNode("AddExpr", currentToken.value);
            advance();
            return addNode;
        }
        ASTNode addExprNode = parseMulExpr();
        return addExprNode;
    }

    private ASTNode parseMulExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator &&
                (currentToken.value.equals("*") || currentToken.value.equals("/"))) {
            ASTNode mulNode = new ASTNode("MulExpr", currentToken.value);
            advance();;return mulNode;
        }
        ASTNode mulExprNode = parseUnaryExpr();
        return mulExprNode;
    }

    private ASTNode parseUnaryExpr() throws ParseException {
        if (currentToken.type == LexicalScanner.Type.Operator &&
                (currentToken.value.equals("++") || currentToken.value.equals("--"))) {
            ASTNode unaryNode = new ASTNode("UnaryExpr", currentToken.value);
            unaryNode.addChild(parseUnaryExpr());
            return unaryNode;
        }
        ASTNode unaryExprNode = parsePrimaryExpr();
        return unaryExprNode;
    }

    private ASTNode parsePrimaryExpr() throws ParseException {
        ASTNode primaryNode;
        if (currentToken.type == LexicalScanner.Type.Identifier) {
            if (lexer.peekToken().value.equals("(") || lexer.peekToken().value.equals("[")) {
                primaryNode = new ASTNode("Identifier", currentToken.value);
                advance();
                if (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals("(")) {
                    advance();
                    primaryNode = new ASTNode("FunctionCall", primaryNode.getValue());
                    primaryNode.addChild(parseArgList());
                } else if (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals("[")) {
                    advance();
                    primaryNode = new ASTNode("ArrayAccess", primaryNode.getValue());
                    primaryNode.addChild(parseExpr());
                    expect(LexicalScanner.Type.Separator);
                }
            } else {
                primaryNode = new ASTNode("Identifier", currentToken.value);
                advance();
                if (currentToken.type == LexicalScanner.Type.Operator) {
                    primaryNode.addChild(parseExpr());
                    primaryNode.addChild(parseExpr());
                }
            }
        } else if (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals("(")) {
            advance();
            primaryNode = new ASTNode("ParenExpr", null);
            primaryNode.addChild(parseExpr());
            expect(LexicalScanner.Type.Separator);
        } else if (currentToken.type == LexicalScanner.Type.IntLiteral ||
                currentToken.type == LexicalScanner.Type.RealLiteral ||
                currentToken.type == LexicalScanner.Type.StrLiteral) {
            primaryNode = new ASTNode(currentToken.type.toString(), currentToken.value);
            advance();
        } else {
            throw new ParseException("Unexpected token: " + currentToken.value, 0);
        }
        return primaryNode;
    }

    private ASTNode parseArgList() throws ParseException {
        ASTNode argListNode = new ASTNode("ArgList", null);
        if (!currentToken.value.equals(")")) {
            argListNode.addChild(parseProperArgList());
        }
        expect(LexicalScanner.Type.Separator);
        return argListNode;
    }

    private ASTNode parseProperArgList() throws ParseException {
        ASTNode properArgListNode = new ASTNode("ProperArgList", null);
        properArgListNode.addChild(parseArg());
        while (currentToken.type == LexicalScanner.Type.Separator && currentToken.value.equals(",")) {
            properArgListNode.addChild(parseArg());
        }
        return properArgListNode;
    }

    private ASTNode parseArg() throws ParseException {
        return parseExpr();
    }
}