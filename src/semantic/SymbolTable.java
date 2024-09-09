package semantic;

import Exceptions.SemanticException;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> symbols = new HashMap<>();

    public void addSymbol(Symbol symbol, int line) throws SemanticException {
        if (symbols.containsKey(symbol.getName())) {
            throw new SemanticException(line, "Variável '" + symbol.getName() + "' já declarada.");
        }
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol getSymbol(String name, int line) throws SemanticException{
        Symbol symbol = symbols.get(name);
        if (symbol == null) {
            throw new SemanticException(line, "Variável '" + name + "' não declarada.");
        }
        return symbol;
    }

    public void print() {
        System.out.println("Tabela de Símbolos:");
        for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getType());
        }
    }
}