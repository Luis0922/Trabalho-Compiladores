package parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lexer.*;

public class SemanticParser extends Parser {

    private Map<String, String> symbolTable = new HashMap<>(); // Tabela de símbolos

    public SemanticParser(Lexer l) throws IOException {
        super(l);
    }

    @Override
    public void program() throws IOException {
        if (token.tag == Tag.APP) {
            eat(Tag.APP);
            eat(Tag.ID);  
            body();
        } else {
            error("Semantic error: Expect 'APP', but found " + token);
        }
    }


    @Override
    void identifier() throws IOException {
        if (token.tag == Tag.ID) {
            String varName = token.lexeme;

            // Verifica se a variável foi declarada antes de ser usada
            if (!symbolTable.containsKey(varName)) {
                error("Semantic error: Variable '" + varName + "' not declared.");
            }
            eat(Tag.ID);
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    @Override
    void decl() throws IOException {
        String varType = "";

        // Identifica o tipo de dado (INTEGER ou REAL)
        switch (token.tag) {
            case Tag.INTEGER:
                varType = "INTEGER";
                break;
            case Tag.REAL:
                varType = "REAL";
                break;
            default:
                error("Syntax error: Expect 'INTEGER' or 'REAL', but found " + Tag.getTagName(token.tag));
        }
        type();
        ident_list(varType);
    }

    void ident_list(String varType) throws IOException {
        if (token.tag == Tag.ID) {
            String varName = token.lexeme;

            // Verifica se a variável já foi declarada
            if (symbolTable.containsKey(varName)) {
                error("Semantic error: Variable '" + varName + "' already declared.");
            }

            // Adiciona a variável na tabela de símbolos
            symbolTable.put(varName, varType);
            eat(Tag.ID);

            while ((char) token.tag == ',') {
                eat(',');
                varName = token.lexeme;

                // Verifica se a variável já foi declarada
                if (symbolTable.containsKey(varName)) {
                    error("Semantic error: Variable '" + varName + "' already declared.");
                }

                // Adiciona a variável na tabela de símbolos
                symbolTable.put(varName, varType);
                eat(Tag.ID);
            }
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    @Override
    void assign_stmt() throws IOException {
        if (token.tag == Tag.ID) {
            String varName = token.lexeme;

            // Verifica se a variável foi declarada antes de ser usada
            if (!symbolTable.containsKey(varName)) {
                error("Semantic error: Variable '" + varName + "' not declared.");
            }

            String varType = symbolTable.get(varName);
            eat(Tag.ID);
            eat(Tag.DEFINED_AS);
            String exprType = simple_expr_withReturn();

            // Verifica a compatibilidade de tipos na atribuição
            if (varType.equals("INTEGER") && exprType.equals("REAL")) {
                error("Semantic error: Cannot assign a REAL to an INTEGER variable.");
            }
        } else {
            error("Syntax error: Expect 'ID', but found " + Tag.getTagName(token.tag));
        }
    }

    // Modificação da função simple_expr para retornar o tipo da expressão
    String simple_expr_withReturn() throws IOException {
        String termType = term_withReturn();
        while ((char) token.tag == '+' || (char) token.tag == '-' || token.tag == Tag.OR) {
            String operator = String.valueOf((char) token.tag);
            addop();
            String nextTermType = term_withReturn();

            // Verifica a compatibilidade de tipos entre os termos
            if (termType.equals("INTEGER") && nextTermType.equals("REAL")) {
                termType = "REAL";
            } else if (termType.equals("REAL") && nextTermType.equals("INTEGER")) {
                termType = "REAL";
            } else if (termType.equals("INTEGER") && nextTermType.equals("INTEGER")) {
                termType = "INTEGER";
            } else if (termType.equals("REAL") && nextTermType.equals("REAL")) {
                termType = "REAL";
            } else {
                error("Semantic error: Incompatible types in expression with operator '" + operator + "'.");
            }
        }
        return termType;
    }

    // Modificação da função term para retornar o tipo do termo
    String term_withReturn() throws IOException {
        String factorType = factor_a_withReturn();
        while ((char) token.tag == '*' || (char) token.tag == '/' || token.tag == Tag.AND) {
            mulop();
            String nextFactorType = factor_a_withReturn();

            // Verifica a compatibilidade de tipos entre os fatores
            if (factorType.equals("INTEGER") && nextFactorType.equals("REAL")) {
                factorType = "REAL";
            } else if (factorType.equals("REAL") && nextFactorType.equals("INTEGER")) {
                factorType = "REAL";
            } else if (factorType.equals("INTEGER") && nextFactorType.equals("INTEGER")) {
                factorType = "INTEGER";
            } else if (factorType.equals("REAL") && nextFactorType.equals("REAL")) {
                factorType = "REAL";
            } else {
                error("Semantic error: Incompatible types in term.");
            }
        }
        return factorType;
    }

    // Modificação da função factor_a para retornar o tipo do fator
    String factor_a_withReturn() throws IOException {
        String factorType = "";

        if (token.tag == Tag.ID) {
            String varName = token.lexeme;
            if (!symbolTable.containsKey(varName)) {
                error("Semantic error: Variable '" + varName + "' not declared.");
            }
            factorType = symbolTable.get(varName);
            identifier();
        } else if (token.tag == Tag.NUM) {
            factorType = "INTEGER";
            integer_const();
        } else if (token.tag == Tag.REAL) {
            factorType = "REAL";
            float_const();
        } else if ((char) token.tag == '(') {
            eat('(');
            factorType = simple_expr_withReturn();
            eat(')');
        } else if ((char) token.tag == '!') {
            eat('!');
            factorType = factor_withReturn();
        } else if ((char) token.tag == '-') {
            eat('-');
            factorType = factor_withReturn();
        } else {
            error("Syntax error: Expect 'ID', 'NUM', 'REAL', '(', '!', or '-', but found " + Tag.getTagName(token.tag));
        }

        return factorType;
    }

    String factor_withReturn() throws IOException {
        return factor_a_withReturn();
    }
}
