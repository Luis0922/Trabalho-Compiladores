package Exceptions;

import java.io.IOException;

public class IncompleteValueException extends IOException {
    public IncompleteValueException(int line, String message) {
        super(String.format("A operação falhou na linha %d.\n\nDetalhes: %s\n\n", line, message));
    }
}
