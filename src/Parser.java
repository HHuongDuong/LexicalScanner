import java.io.StringReader;

public class Parser {
    private Controller lexer;
    private Token curToken;
    private Token nextToken;

    Parser(Controller lexer) {
        this.lexer = lexer;
        this.curToken = lexer.nextToken();
        this.nextToken = lexer.peekToken();
    }

    private void consume(LexicalScanner.Type expectedType) {
        if (curToken.type == expectedType) {
            advance();
        } else {
            throw new RuntimeException("Expected token " + expectedType + " but got " + curToken.type);
        }
    }

    private void advance() {
        curToken = nextToken;
        nextToken = lexer.nextToken();
    }

    private boolean match(LexicalScanner.Type type) {
        if (curToken.type == type) {
            consume(type);
            return true;
        }
        return false;
    }

    // program -> ( func-decl | var-decl )*
    public ASTNode program() {
        ASTNode root = new ASTNode("program");
        while (curToken.type != LexicalScanner.Type.EOF) {
            if (match(LexicalScanner.Type.Keyword)) {
                Token next = lexer.peekToken();
                if (next.type == LexicalScanner.Type.Identifier) {
                    next = lexer.peekToken(); // look further ahead
                    if (next.type == LexicalScanner.Type.Separator) {
                        root.addChild(funcDecl());
                    } else {
                        root.addChild(varDecl());
                    }
                } else {
                    throw new RuntimeException("Expected identifier after keyword");
                }
            } else {
                throw new RuntimeException("Expected keyword for type declaration");
            }
        }
        return root;
    }

    // func-decl -> type identifier para-list compound-stmt
    private ASTNode funcDecl() {
        ASTNode funcDeclNode = new ASTNode("funcDecl");
        funcDeclNode.addChild(type());
        funcDeclNode.addChild(new ASTNode(curToken.value));
        consume(LexicalScanner.Type.Identifier);
        funcDeclNode.addChild(paraList());
        funcDeclNode.addChild(compoundStmt());
        return funcDeclNode;
    }

    // var-decl -> type init-declarator-list ";"
    private ASTNode varDecl() {
        ASTNode varDeclNode = new ASTNode("varDecl");
        varDeclNode.addChild(type());
        varDeclNode.addChild(initDeclaratorList());
        consume(LexicalScanner.Type.Separator);
        return varDeclNode;
    }

    // init-declarator-list -> init-declarator ( "," init-declarator )*
    private ASTNode initDeclaratorList() {
        ASTNode initDeclListNode = new ASTNode("initDeclaratorList");
        initDeclListNode.addChild(initDeclarator());
        while (match(LexicalScanner.Type.Separator) && curToken.value.equals(",")) {
            consume(LexicalScanner.Type.Separator);
            initDeclListNode.addChild(initDeclarator());
        }
        return initDeclListNode;
    }

    // init-declarator -> declarator ( "=" initialiser )?
    private ASTNode initDeclarator() {
        ASTNode initDeclNode = new ASTNode("initDeclarator");
        initDeclNode.addChild(declarator());
        if (match(LexicalScanner.Type.Operator) && curToken.value.equals("=")) {
            consume(LexicalScanner.Type.Operator);
            initDeclNode.addChild(initialiser());
        }
        return initDeclNode;
    }

    // declarator -> identifier | identifier "[" INTLITERAL? "]"
    private ASTNode declarator() {
        ASTNode declaratorNode = new ASTNode("declarator");
        declaratorNode.addChild(new ASTNode(curToken.value));
        consume(LexicalScanner.Type.Identifier);
        if (match(LexicalScanner.Type.Separator) && curToken.value.equals("[")) {
            consume(LexicalScanner.Type.Separator);
            if (match(LexicalScanner.Type.IntLiteral)) {
                declaratorNode.addChild(new ASTNode(curToken.value));
                consume(LexicalScanner.Type.IntLiteral);
            }
            consume(LexicalScanner.Type.Separator);
        }
        return declaratorNode;
    }

    // initialiser -> expr | "{" expr ( "," expr )* "}"
    private ASTNode initialiser() {
        ASTNode initialiserNode = new ASTNode("initialiser");
        if (match(LexicalScanner.Type.Separator) && curToken.value.equals("{")) {
            consume(LexicalScanner.Type.Separator);
            initialiserNode.addChild(expr());
            while (match(LexicalScanner.Type.Separator) && curToken.value.equals(",")) {
                consume(LexicalScanner.Type.Separator);
                initialiserNode.addChild(expr());
            }
            consume(LexicalScanner.Type.Separator);
        } else {
            initialiserNode.addChild(expr());
        }
        return initialiserNode;
    }

    // type -> void | boolean | int | float
    private ASTNode type() {
        if (match(LexicalScanner.Type.Keyword)) {
            String type = curToken.value;
            if (type.equals("void") || type.equals("boolean") || type.equals("int") || type.equals("float")) {
                ASTNode typeNode = new ASTNode(type);
                advance();
                return typeNode;
            } else {
                throw new RuntimeException("Unknown type: " + type);
            }
        } else {
            throw new RuntimeException("Expected a type keyword");
        }
    }

    // compound-stmt -> "{" var-decl* stmt* "}"
    private ASTNode compoundStmt() {
        ASTNode compoundStmtNode = new ASTNode("compoundStmt");
        consume(LexicalScanner.Type.Separator);
        while (curToken.type == LexicalScanner.Type.Keyword) {
            compoundStmtNode.addChild(varDecl());
        }
        while (curToken.type != LexicalScanner.Type.Separator || !curToken.value.equals("}")) {
            compoundStmtNode.addChild(stmt());
        }
        consume(LexicalScanner.Type.Separator);
        return compoundStmtNode;
    }

    // stmt -> compound-stmt | if-stmt | for-stmt | while-stmt | break-stmt | continue-stmt | return-stmt | expr-stmt
    private ASTNode stmt() {
        if (match(LexicalScanner.Type.Separator) && curToken.value.equals("{")) {
            return compoundStmt();
        } else if (match(LexicalScanner.Type.Keyword)) {
            switch (curToken.value) {
                case "if":
                    return ifStmt();
                case "for":
                    return forStmt();
                case "while":
                    return whileStmt();
                case "break":
                    return breakStmt();
                case "continue":
                    return continueStmt();
                case "return":
                    return returnStmt();
                default:
                    return exprStmt();
            }
        } else {
            return exprStmt();
        }
    }

    // if-stmt -> if "(" expr ")" stmt ( else stmt )?
    private ASTNode ifStmt() {
        ASTNode ifStmtNode = new ASTNode("ifStmt");
        consume(LexicalScanner.Type.Keyword);
        consume(LexicalScanner.Type.Separator);
        ifStmtNode.addChild(expr());
        consume(LexicalScanner.Type.Separator);
        ifStmtNode.addChild(stmt());
        if (curToken.type == LexicalScanner.Type.Keyword && curToken.value.equals("else")) {
            consume(LexicalScanner.Type.Keyword);
            ASTNode elseNode = new ASTNode("else");
            elseNode.addChild(stmt());
            ifStmtNode.addChild(elseNode);
        }
        return ifStmtNode;
    }

    // for-stmt -> for "(" expr? ";" expr? ";" expr? ")" stmt
    private ASTNode forStmt() {
        ASTNode forStmtNode = new ASTNode("forStmt");
        consume(LexicalScanner.Type.Keyword);
        consume(LexicalScanner.Type.Separator);
        if (curToken.type != LexicalScanner.Type.Separator) {
            forStmtNode.addChild(expr());
        }
        consume(LexicalScanner.Type.Separator);
        if (curToken.type != LexicalScanner.Type.Separator) {
            forStmtNode.addChild(expr());
        }
        consume(LexicalScanner.Type.Separator);
        if (curToken.type != LexicalScanner.Type.Separator && curToken.value.equals(")")) {
            forStmtNode.addChild(expr());
        }
        consume(LexicalScanner.Type.Separator);
        forStmtNode.addChild(stmt());
        return forStmtNode;
    }

    // while-stmt -> while "(" expr ")" stmt
    private ASTNode whileStmt() {
        ASTNode whileStmtNode = new ASTNode("whileStmt");
        consume(LexicalScanner.Type.Keyword);
        consume(LexicalScanner.Type.Separator);
        whileStmtNode.addChild(expr());
        consume(LexicalScanner.Type.Separator);
        whileStmtNode.addChild(stmt());
        return whileStmtNode;
    }

    // break-stmt -> break ";"
    private ASTNode breakStmt() {
        consume(LexicalScanner.Type.Keyword);
        consume(LexicalScanner.Type.Separator);
        return new ASTNode("breakStmt");
    }

    // continue-stmt -> continue ";"
    private ASTNode continueStmt() {
        consume(LexicalScanner.Type.Keyword);
        consume(LexicalScanner.Type.Separator);
        return new ASTNode("continueStmt");
    }

    // return-stmt -> return expr? ";"
    private ASTNode returnStmt() {
        ASTNode returnStmtNode = new ASTNode("returnStmt");
        consume(LexicalScanner.Type.Keyword);
        if (curToken.type != LexicalScanner.Type.Separator) {
            returnStmtNode.addChild(expr());
        }
        consume(LexicalScanner.Type.Separator);
        return returnStmtNode;
    }

    // expr-stmt -> expr? ";"
    private ASTNode exprStmt() {
        ASTNode exprStmtNode = new ASTNode("exprStmt");
        if (curToken.type != LexicalScanner.Type.Separator) {
            exprStmtNode.addChild(expr());
        }
        consume(LexicalScanner.Type.Separator);
        return exprStmtNode;
    }

    // expr -> assignment-expr
    private ASTNode expr() {
        return assignmentExpr();
    }

    // assignment-expr -> ( cond-or-expr "=" )* cond-or-expr
    private ASTNode assignmentExpr() {
        ASTNode assignmentExprNode = new ASTNode("assignmentExpr");
        assignmentExprNode.addChild(condOrExpr());
        while (curToken.type == LexicalScanner.Type.Operator && curToken.value.equals("=")) {
            consume(LexicalScanner.Type.Operator);
            assignmentExprNode.addChild(condOrExpr());
        }
        return assignmentExprNode;
    }

    // cond-or-expr -> cond-and-expr | cond-or-expr "||" cond-and-expr
    private ASTNode condOrExpr() {
        ASTNode condOrExprNode = new ASTNode("condOrExpr");
        condOrExprNode.addChild(condAndExpr());
        while (curToken.type == LexicalScanner.Type.Operator && curToken.value.equals("||")) {
            consume(LexicalScanner.Type.Operator);
            condOrExprNode.addChild(condAndExpr());
        }
        return condOrExprNode;
    }

    // cond-and-expr -> equality-expr | cond-and-expr "&&" equality-expr
    private ASTNode condAndExpr() {
        ASTNode condAndExprNode = new ASTNode("condAndExpr");
        condAndExprNode.addChild(equalityExpr());
        while (curToken.type == LexicalScanner.Type.Operator && curToken.value.equals("&&")) {
            consume(LexicalScanner.Type.Operator);
            condAndExprNode.addChild(equalityExpr());
        }
        return condAndExprNode;
    }

    // equality-expr -> rel-expr | equality-expr "==" rel-expr | equality-expr "!=" rel-expr
    private ASTNode equalityExpr() {
        ASTNode equalityExprNode = new ASTNode("equalityExpr");
        equalityExprNode.addChild(relExpr());
        while (curToken.type == LexicalScanner.Type.Operator && (curToken.value.equals("==") || curToken.value.equals("!="))) {
            consume(LexicalScanner.Type.Operator);
            equalityExprNode.addChild(relExpr());
        }
        return equalityExprNode;
    }

    // rel-expr -> add-expr | rel-expr ( "<" | ">" | "<=" | ">=" ) add-expr
    private ASTNode relExpr() {
        ASTNode relExprNode = new ASTNode("relExpr");
        relExprNode.addChild(addExpr());
        while (curToken.type == LexicalScanner.Type.Operator && (curToken.value.equals("<") || curToken.value.equals(">") || curToken.value.equals("<=") || curToken.value.equals(">="))) {
            consume(LexicalScanner.Type.Operator);
            relExprNode.addChild(addExpr());
        }
        return relExprNode;
    }

    // add-expr -> mul-expr | add-expr ( "+" | "-" ) mul-expr
    private ASTNode addExpr() {
        ASTNode addExprNode = new ASTNode("addExpr");
        addExprNode.addChild(mulExpr());
        while (curToken.type == LexicalScanner.Type.Operator && (curToken.value.equals("+") || curToken.value.equals("-"))) {
            consume(LexicalScanner.Type.Operator);
            addExprNode.addChild(mulExpr());
        }
        return addExprNode;
    }

    // mul-expr -> unary-expr | mul-expr ( "*" | "/" | "%" ) unary-expr
    private ASTNode mulExpr() {
        ASTNode mulExprNode = new ASTNode("mulExpr");
        mulExprNode.addChild(unaryExpr());
        while (curToken.type == LexicalScanner.Type.Operator && (curToken.value.equals("*") || curToken.value.equals("/") || curToken.value.equals("%"))) {
            consume(LexicalScanner.Type.Operator);
            mulExprNode.addChild(unaryExpr());
        }
        return mulExprNode;
    }

    // unary-expr -> ( "+" | "-" | "!" ) unary-expr | primary-expr
    private ASTNode unaryExpr() {
        ASTNode unaryExprNode = new ASTNode("unaryExpr");
        if (curToken.type == LexicalScanner.Type.Operator && (curToken.value.equals("+") || curToken.value.equals("-") || curToken.value.equals("!"))) {
            unaryExprNode.addChild(new ASTNode(curToken.value));
            consume(LexicalScanner.Type.Operator);
            unaryExprNode.addChild(unaryExpr());
        } else {
            unaryExprNode.addChild(primaryExpr());
        }
        return unaryExprNode;
    }

    // primary-expr -> identifier | intliteral | floatliteral | "(" expr ")"
    private ASTNode primaryExpr() {
        ASTNode primaryExprNode = new ASTNode("primaryExpr");
        if (curToken.type == LexicalScanner.Type.Identifier) {
            primaryExprNode.addChild(new ASTNode(curToken.value));
            consume(LexicalScanner.Type.Identifier);
        } else if (curToken.type == LexicalScanner.Type.IntLiteral) {
            primaryExprNode.addChild(new ASTNode(curToken.value));
            consume(LexicalScanner.Type.IntLiteral);
        } else if (curToken.type == LexicalScanner.Type.RealLiteral) {
            primaryExprNode.addChild(new ASTNode(curToken.value));
            consume(LexicalScanner.Type.RealLiteral);
        } else if (curToken.type == LexicalScanner.Type.Separator && curToken.value.equals("(")) {
            consume(LexicalScanner.Type.Separator);
            primaryExprNode.addChild(expr());
            consume(LexicalScanner.Type.Separator);
        } else {
            throw new RuntimeException("Expected primary expression");
        }
        return primaryExprNode;
    }

    // para-list -> "(" proper-para-list? ")"
    private ASTNode paraList() {
        ASTNode paraListNode = new ASTNode("paraList");
        consume(LexicalScanner.Type.Separator);
        if (curToken.value.equals(")")) {
            paraListNode.addChild(properParaList());
        }
        consume(LexicalScanner.Type.Separator);
        return paraListNode;
    }

    // proper-para-list -> para-decl ( "," para-decl )*
    private ASTNode properParaList() {
        ASTNode properParaListNode = new ASTNode("properParaList");
        properParaListNode.addChild(paraDecl());
        while (curToken.value.equals(",")) {
            consume(LexicalScanner.Type.Separator);
            properParaListNode.addChild(paraDecl());
        }
        return properParaListNode;
    }

    // para-decl -> type declarator
    private ASTNode paraDecl() {
        ASTNode paraDeclNode = new ASTNode("paraDecl");
        paraDeclNode.addChild(type());
        paraDeclNode.addChild(declarator());
        return paraDeclNode;
    }

    // arg-list -> "(" proper-arg-list? ")"
    private ASTNode argList() {
        ASTNode argListNode = new ASTNode("argList");
        consume(LexicalScanner.Type.Separator);
        if (curToken.value.equals(")")) {
            argListNode.addChild(properArgList());
        }
        consume(LexicalScanner.Type.Separator);
        return argListNode;
    }

    // proper-arg-list -> arg ( "," arg )*
    private ASTNode properArgList() {
        ASTNode properArgListNode = new ASTNode("properArgList");
        properArgListNode.addChild(arg());
        while (curToken.value.equals(",")) {
            consume(LexicalScanner.Type.Separator);
            properArgListNode.addChild(arg());
        }
        return properArgListNode;
    }

    // arg -> expr
    private ASTNode arg() {
        return expr();
    }
}

