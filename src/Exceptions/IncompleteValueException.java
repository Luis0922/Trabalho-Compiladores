package Exceptions;

public class IncompleteValueException extends Exception {
    public IncompleteValueException(int line, String message) {
        super(String.format("A operação falhou na linha %d.\n\nDetalhes: %s\n\n", line, message));
    }
}
