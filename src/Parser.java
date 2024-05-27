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

    // program -> ( func-decl | var-decl )*
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

    // func-decl -> type identifier para-list compound-stmt
    private ASTNode parseFuncDecl() throws ParseException {
        ASTNode funcDeclNode = new ASTNode("FuncDecl", null);
        funcDeclNode.addChild(parseType());
        advance();
        funcDeclNode.addChild(new ASTNode("Identifier", currentToken.value));
        expect(LexicalScanner.Type.Identifier);
        funcDeclNode.addChild(parseParaList());
        funcDeclNode.addChild(parseCompoundStmt());
        return funcDeclNode;
    }

    // var-decl -> type init-declarator-list ";"
    private ASTNode parseVarDecl() throws ParseException {
        ASTNode varDeclNode = new ASTNode("VarDecl", null);
        varDeclNode.addChild(parseType());
        advance();
        varDeclNode.addChild(parseInitDeclaratorList());
        expect(LexicalScanner.Type.Separator);
        return varDeclNode;
    }

    // init-declarator-list-> init-declarator ( "," init-declarator )*
    private ASTNode parseInitDeclaratorList() throws ParseException {
        ASTNode initDeclaratorListNode = new ASTNode("InitDeclaratorList", null);
        initDeclaratorListNode.addChild(parseInitDeclarator());
        while (lexer.peek2Token().value.equals(",")) {
            advance();
            initDeclaratorListNode.addChild(new ASTNode(",", null));
            advance(); // 1
            initDeclaratorListNode.addChild(parseInitDeclarator());
        }
        return initDeclaratorListNode;
    }

    // init-declarator -> declarator ( "=" initialiser )?
    private ASTNode parseInitDeclarator() throws ParseException {
        ASTNode initDeclaratorNode = new ASTNode("InitDeclarator", null);
        initDeclaratorNode.addChild(parseDeclarator());
        if (lexer.peek2Token().value.equals("=")) {
            advance();
            initDeclaratorNode.addChild(new ASTNode("=", null));
            advance(); //2
            initDeclaratorNode.addChild(parseInitialiser());
        }
        return initDeclaratorNode;
    }

    // declarator -> identifier
    //             | identifier "[" INTLITERAL? "]"
    private ASTNode parseDeclarator() throws ParseException {
        ASTNode declaratorNode = new ASTNode("Declarator", currentToken.value);
        if (lexer.peek2Token().value.equals("[")) {
            advance();
            declaratorNode.addChild(new ASTNode("[", null));
            if (lexer.peek2Token().type.equals(LexicalScanner.Type.IntLiteral)) {
                advance();
                declaratorNode.addChild(new ASTNode("IntLiteral", currentToken.value));
                advance();
            }
            declaratorNode.addChild(new ASTNode("]", null));
            expect(LexicalScanner.Type.Separator);
        }
        return declaratorNode;
    }

    // initialiser -> expr
    //              | "{" expr ( "," expr )* "}"
    private ASTNode parseInitialiser() throws ParseException {
        ASTNode initialiserNode = new ASTNode("Initializer", null);
        if (currentToken.value.equals("{")) {
            initialiserNode.addChild(new ASTNode("{", null));
            advance();
            initialiserNode.addChild(parseExpr());
            advance();
            while (currentToken.value.equals(",")) {
                advance();
                initialiserNode.addChild(parseExpr());
                advance();
            }
            initialiserNode.addChild(new ASTNode("}", null));
            expect(LexicalScanner.Type.Separator);
        } else {
            initialiserNode.addChild(parseExpr());
        }
        return initialiserNode;
    }

    // type -> void | boolean | int | float
    private ASTNode parseType() throws ParseException {
        if (!isType(currentToken)) {
            throw new ParseException("Expected type but found " + currentToken.type, 0);
        }
        return new ASTNode("Type", currentToken.value);
    }

    // para-list -> "(" proper-para-list? ")"
    private ASTNode parseParaList() throws ParseException {
        ASTNode paraListNode = new ASTNode("ParaList", null);
        paraListNode.addChild(new ASTNode("(", null));
        expect(LexicalScanner.Type.Separator);
        if (!currentToken.value.equals(")")) {
            paraListNode.addChild(parseProperParaList());
            advance();;
        }
        paraListNode.addChild(new ASTNode(")", null));
        expect(LexicalScanner.Type.Separator);
        return paraListNode;
    }

    //proper-para-list -> para-decl ( "," para-decl )*
    private ASTNode parseProperParaList() throws ParseException {
        ASTNode properParaListNode = new ASTNode("ProperParaList", null);
        properParaListNode.addChild(parseParaDecl());
        while (lexer.peek2Token().value.equals(",")) {
            advance();
            properParaListNode.addChild(new ASTNode(",", null));
            advance();
            properParaListNode.addChild(parseParaDecl());
        }
        return properParaListNode;
    }

    // para-decl -> type declarator
    private ASTNode parseParaDecl() throws ParseException {
        ASTNode paraDeclNode = new ASTNode("ParaDecl", null);
        paraDeclNode.addChild(parseType());
        advance();
        paraDeclNode.addChild(parseDeclarator());
        return paraDeclNode;
    }

    // compound-stmt -> "{" var-decl* stmt* "}"
    private ASTNode parseCompoundStmt() throws ParseException {
        ASTNode compoundStmtNode = new ASTNode("CompoundStmt", null);
        compoundStmtNode.addChild(new ASTNode("{", null));
        expect(LexicalScanner.Type.Separator);
        while (!currentToken.value.equals("}")) {
            while (isType(currentToken)) {
                compoundStmtNode.addChild(parseVarDecl());
                advance();
            }
            while (currentToken.getType() != LexicalScanner.Type.Separator || !currentToken.value.equals("}")) {
                compoundStmtNode.addChild(parseStmt());
            }
        }
        compoundStmtNode.addChild(new ASTNode("}", null));
        expect(LexicalScanner.Type.Separator);
        return compoundStmtNode;
    }

    // stmt -> compound-stmt
    //| if-stmt
    //| for-stmt
    //| while-stmt
    //| break-stmt
    //| continue-stmt
    //| return-stmt
    //| expr-stmt
    private ASTNode parseStmt() throws ParseException {
        ASTNode stmtNode;
        switch (currentToken.value) {
            case "{":
                stmtNode = parseCompoundStmt();
                break;
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

    // if-stmt -> if "(" expr ")" stmt ( else stmt )?
    private ASTNode parseIfStmt() throws ParseException {
        ASTNode ifStmtNode = new ASTNode("IfStmt", null);
        expect(LexicalScanner.Type.Keyword);
        ifStmtNode.addChild(new ASTNode("(", null));
        expect(LexicalScanner.Type.Separator);
        ifStmtNode.addChild(parseExpr());
        ifStmtNode.addChild(new ASTNode(")", null));
        expect(LexicalScanner.Type.Separator);
        ifStmtNode.addChild(parseStmt());
        advance();
        if (currentToken.value.equals("else")) {
            ifStmtNode.addChild(new ASTNode("ElseStmt", null));
            advance();
            ifStmtNode.addChild(parseStmt());
        }
        return ifStmtNode;
    }

    // for-stmt -> for "(" expr? ";" expr? ";" expr? ")" stmt
    private ASTNode parseForStmt() throws ParseException {
        ASTNode forStmtNode = new ASTNode("ForStmt", null);
        expect(LexicalScanner.Type.Keyword);
        forStmtNode.addChild(new ASTNode("(", null));
        expect(LexicalScanner.Type.Separator);
        while (!currentToken.value.equals(";")) {
            forStmtNode.addChild(parseExpr());
        }
        forStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator);
        while (!currentToken.value.equals(";")) {
            forStmtNode.addChild(parseExpr());
        }
        forStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator);
        while (!currentToken.value.equals(")")) {
            forStmtNode.addChild(parseExpr());
        }
        forStmtNode.addChild(new ASTNode(")", null));
        expect(LexicalScanner.Type.Separator); // )
        forStmtNode.addChild(parseStmt());
        return forStmtNode;
    }

    // while-stmt -> while "(" expr ")" stmt
    private ASTNode parseWhileStmt() throws ParseException {
        ASTNode whileStmtNode = new ASTNode("WhileStmt", null);
        expect(LexicalScanner.Type.Keyword); // while
        whileStmtNode.addChild(new ASTNode("(", null));
        expect(LexicalScanner.Type.Separator); // (
        whileStmtNode.addChild(parseExpr());
        whileStmtNode.addChild(new ASTNode(")", null));
        expect(LexicalScanner.Type.Separator); // )
        whileStmtNode.addChild(parseStmt());
        return whileStmtNode;
    }

    // break-stmt -> break ";"
    private ASTNode parseBreakStmt() throws ParseException {
        ASTNode breakStmtNode = new ASTNode("BreakStmt", null);
        expect(LexicalScanner.Type.Keyword);
        breakStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator); // ;
        return breakStmtNode;
    }

    // continue-stmt -> continue ";"
    private ASTNode parseContinueStmt() throws ParseException {
        ASTNode continueStmtNode = new ASTNode("ContinueStmt", null);
        expect(LexicalScanner.Type.Keyword);
        continueStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator); // ;
        return continueStmtNode;
    }

    // return-stmt -> return expr? ";"
    private ASTNode parseReturnStmt() throws ParseException {
        ASTNode returnStmtNode = new ASTNode("ReturnStmt", null);
        expect(LexicalScanner.Type.Keyword);
        if (currentToken.getType() != LexicalScanner.Type.Separator) {
            returnStmtNode.addChild(parseExpr());
            advance();
        }
        returnStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator); // ;
        return returnStmtNode;
    }

    // expr-stmt -> expr? ";"
    private ASTNode parseExprStmt() throws ParseException {
        ASTNode exprStmtNode = new ASTNode("ExprStmt", null);
        if (currentToken.getType() != LexicalScanner.Type.Separator) {
            exprStmtNode.addChild(parseExpr());
        }
        exprStmtNode.addChild(new ASTNode(";", null));
        expect(LexicalScanner.Type.Separator);
        return exprStmtNode;
    }

    // expr -> assignment-expr
    private ASTNode parseExpr() throws ParseException {
        return parseAssignExpr();
    }

    // assignment-expr -> cond-or-expr ( "=" cond-or-expr )*
    private ASTNode parseAssignExpr() throws ParseException {
        ASTNode AssignExprNode = parseOrExpr();
        while (currentToken.value.equals("=")) {
            AssignExprNode.addChild(new ASTNode("AssignExpr", currentToken.value));
            advance();
            AssignExprNode.addChild(parseOrExpr());
        }
        return AssignExprNode;
    }

    // cond-or-expr -> cond-and-expr ( "||" cond-and-expr )?
    private ASTNode parseOrExpr() throws ParseException {
        ASTNode OrExprNode = parseAndExpr();
        while (currentToken.value.equals("||")) {
            OrExprNode.addChild(new ASTNode("OrExpr", currentToken.value));
            advance();
            OrExprNode.addChild(parseAndExpr());
        }
        return OrExprNode;
    }

    // cond-and-expr -> equality-expr ( "&&" equality-expr )?
    private ASTNode parseAndExpr() throws ParseException {
        ASTNode AndExprNode = parseEqualityExpr();
        while (currentToken.value.equals("&&")) {
            AndExprNode.addChild(new ASTNode("AndExpr", currentToken.value));
            advance();
            AndExprNode.addChild(parseEqualityExpr());
        }
        return AndExprNode;
    }

    // equality-expr -> rel-expr ( "==" rel-expr )?
    //                | rel-expr ( "!=" rel-expr )?
    private ASTNode parseEqualityExpr() throws ParseException {
        ASTNode EqualityExprNode = parseRelExpr();
        while (currentToken.value.equals("==") || currentToken.value.equals("!=")) {
            EqualityExprNode.addChild(new ASTNode("EqualityExpr", currentToken.value));
            advance();
            EqualityExprNode.addChild(parseRelExpr());
        }
        return EqualityExprNode;
    }

    //rel-expr -> additive-expr ( "<" additive-expr )?
    //         | additive-expr ( "<=" additive-expr )?
    //         | additive-expr ( ">" additive-expr )?
    //         | additive-expr ( ">=" additive-expr )?
    private ASTNode parseRelExpr() throws ParseException {
        ASTNode RelExprNode = parseAddExpr();
        while (currentToken.value.equals("<") || currentToken.value.equals("<=")
                || currentToken.value.equals(">") || currentToken.value.equals(">=")) {
            RelExprNode.addChild(new ASTNode("RelExpr", currentToken.value));
            advance();
            RelExprNode.addChild(parseAddExpr());
        }
        return RelExprNode;
    }

    //additive-expr -> multiplicative-expr ( "+" multiplicative-expr )?
    //               | multiplicative-expr ( "-" multiplicative-expr )?
    private ASTNode parseAddExpr() throws ParseException {
        ASTNode AddExprNode = parseMulExpr();
        while (currentToken.value.equals("+") || currentToken.value.equals("-")) {
            AddExprNode.addChild(new ASTNode("AddExpr", currentToken.value));
            advance();
            AddExprNode.addChild(parseMulExpr());
        }
        return AddExprNode;
    }

    //multiplicative-expr -> unary-expr ( "*" unary-expr )?
    //                     | unary-expr ( "/" unary-expr )?
    private ASTNode parseMulExpr() throws ParseException {
        ASTNode MulExprNode = parseUnaryExpr();
        while (currentToken.value.equals("*") || currentToken.value.equals("/")) {
            MulExprNode.addChild(new ASTNode("MulExpr", currentToken.value));
            advance();
            MulExprNode.addChild(parseUnaryExpr());
        }
        return MulExprNode;
    }

    // unary-expr -> "+" unary-expr
    //             | "-" unary-expr
    //             | "!" unary-expr
    //             | primary-expr
    private ASTNode parseUnaryExpr() throws ParseException {
        if (currentToken.value.equals("++") || currentToken.value.equals("--")
                || currentToken.value.equals("!")) {
            ASTNode UnaryExprNode = new ASTNode("UnaryExpr", currentToken.value);
            advance();
            UnaryExprNode.addChild(parseUnaryExpr());
            return UnaryExprNode;
        } else {
            return parsePrimaryExpr();
        }
    }

    // primary-expr -> identifier arg-list?
    //               | identifier "[" expr "]"
    //               | "(" expr ")"
    //               | INTLITERAL
    //               | FLOATLITERAL
    //               | BOOLLITERAL
    //               | STRINGLITERAL
    private ASTNode parsePrimaryExpr() throws ParseException {
        ASTNode PrimaryExprNode = null;
        if (currentToken.type.equals(LexicalScanner.Type.Identifier)) {
            PrimaryExprNode = new ASTNode("Identifier", currentToken.value);
            advance();
            if (lexer.peekToken().value.equals("(") || lexer.peekToken().value.equals("[")) {
                if (currentToken.value.equals("(")) {
                    PrimaryExprNode.addChild(parseArgList());
                    advance();
                } else if (currentToken.value.equals("[")) {
                    PrimaryExprNode.addChild(parseExpr());
                    expect(LexicalScanner.Type.Separator); // ]
                }
            }
        } else if (currentToken.type == LexicalScanner.Type.IntLiteral ||
                currentToken.type == LexicalScanner.Type.RealLiteral ||
                currentToken.type == LexicalScanner.Type.StrLiteral) {
            PrimaryExprNode = new ASTNode(currentToken.type.toString(), currentToken.value);
            advance();
        } else if (currentToken.value.equals("(")) {
            advance();
            PrimaryExprNode.addChild(parseExpr());
            PrimaryExprNode.addChild(new ASTNode(")", null));
            expect(LexicalScanner.Type.Separator);
        }
        return PrimaryExprNode;
    }

    // para-list -> "(" proper-para-list? ")"
    private ASTNode parseArgList() throws ParseException {
        ASTNode argListNode = new ASTNode("ArgList", null);
        argListNode.addChild(new ASTNode("(", null));
        expect(LexicalScanner.Type.Separator);
        if (!currentToken.value.equals(")")) {
            argListNode.addChild(parseProperArgList());
            advance();
        }
        argListNode.addChild(new ASTNode(")", null));
        expect(LexicalScanner.Type.Separator);
        return argListNode;
    }

    // proper-arg-list -> arg ( "," arg )*
    private ASTNode parseProperArgList() throws ParseException {
        ASTNode properArgListNode = new ASTNode("ProperArgList", null);
        properArgListNode.addChild(parseArg());
        while (lexer.peek2Token().value.equals(",")) {
            advance();
            properArgListNode.addChild(parseArg());
            advance();
        }
        return properArgListNode;
    }

    // arg -> expr
    private ASTNode parseArg() throws ParseException {
        return parseExpr();
    }
}