package Exceptions;

import java.io.IOException;

public class SemanticException extends IOException {
    public SemanticException(int line, String message) {
        super(String.format("Erro semântico na linha %d.\n\nDetalhes: %s\n\n", line, message));
    }
}
