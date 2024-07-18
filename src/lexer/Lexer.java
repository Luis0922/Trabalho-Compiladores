package lexer;

import Exceptions.IncompleteValueException;

import java.io.*;
import java.util.*;
public class Lexer {
    public static int line = 1; //contador de linhas
    private char character = ' '; //caractere lido do arquivo
    private FileReader file;
    private boolean EOF;
    private Hashtable symbolTable = new Hashtable();

    /* Método para inserir palavras reservadas na HashTable */
    private void addReservedWord(Word word) {
        symbolTable.put(word.getLexeme(), word); // lexema é a chave para entrada na
    }

    /* Método construtor */
    public Lexer(String fileName) throws IOException {
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException exception) {
            throw new FileNotFoundException("File not found");
        }

        // Insere palavras reservadas na HashTable
        addReservedWord(new Word("if", Tag.IF));
        addReservedWord(new Word("app", Tag.APP));
        addReservedWord(new Word("var", Tag.VAR));
        addReservedWord(new Word("init", Tag.INIT));
        addReservedWord(new Word("return", Tag.RETURN));
        addReservedWord(new Word("integer", Tag.INTEGER));
        addReservedWord(new Word("real", Tag.REAL));
        addReservedWord(new Word("else", Tag.ELSE));
        addReservedWord(new Word("then", Tag.THEN));
        addReservedWord(new Word("end", Tag.END));
        addReservedWord(new Word("repeat", Tag.REPEAT));
        addReservedWord(new Word("until", Tag.UNTIL));
        addReservedWord(new Word("read", Tag.READ));
        addReservedWord(new Word("write", Tag.WRITE));
    }

    /*Lê o próximo caractere do arquivo*/
    private void readNextCharacter() throws IOException {
        int next = file.read();
        if (next == -1) {
            EOF = true;
        } else {
            character = (char) next;
        }
    }

    /* Lê o próximo caractere do arquivo e verifica se é igual a nextCharacter*/
    private boolean readNextCharacter(char nextCharacter) throws IOException {
        readNextCharacter();
        if (character != nextCharacter) return false;
        character = ' ';
        return true;
    }

    public Token scan() throws Exception {
        if(EOF){
            return null;
        }
        // Desconsidera delimitadores na entrada
        for (;; readNextCharacter()) {
            if (character == ' ' || character == '\t' || character == '\r' || character == '\b') continue;
            else if (character == '\n') line++; // line count
            else break;
        }

        // Literal
        if (character == '{'){
            StringBuilder literal = new StringBuilder();
            readNextCharacter();
            while (character != '}'){
                literal.append(character);
                readNextCharacter();
                if (EOF) throw new IncompleteValueException(line, "Literal " + literal + "\nmal formado");
            }
            readNextCharacter();
            return new Literal(literal.toString());
        }

        switch(character){
            // Operators
            case '!':
                if (readNextCharacter('=')) return Word.not_equal;
                else return new Token('!');
            case '>':
                if (readNextCharacter('=')) return Word.big_equal;
                else return new Token('>');
            case '<':
                if (readNextCharacter('=')) return Word.least_equal;
                else return new Token('>');
            case '|':
                if (readNextCharacter('|')) return Word.or;
                else return new Token('|');
            case '&':
                if (readNextCharacter('&')) return Word.and;
                else return new Token('&');
            case ':':
                if (readNextCharacter('=')) return Word.defined_as;
                else return new Token(':');
        }

        // Digit
        if (Character.isDigit(character)){
            int number=0;
            while(Character.isDigit(character)){
                number = 10*number + Character.digit(character,10);
                readNextCharacter();
            }

            if(character == '.') {
                readNextCharacter();
                if(Character.isDigit(character)){
                    float decimalNumber = number;
                    float fraction = 10;
                    while(Character.isDigit(character)){
                        decimalNumber = decimalNumber + Character.digit(character,10)/fraction;
                        fraction *= 10;
                        readNextCharacter();
                    }
                    return new Real(decimalNumber);
                }
                else {
                    throw new IncompleteValueException(line, String.format("O valor está incompleto: " + number + "."));
                }
            }
            return new Num(number);
        }

        // Identifiers
        if(Character.isLetterOrDigit(character)){
            StringBuilder stringBuffer = new StringBuilder();

            do{
                stringBuffer.append(character);
                readNextCharacter();
            }while(Character.isLetterOrDigit(character));

            String string = stringBuffer.toString();
            Word word = (Word)symbolTable.get(string);
            if (word != null) return word; // word already exists in HashTable
            word = new Word(string, Tag.ID);
            symbolTable.put(string, word);
            return word;
        }

        // Characters not specified
        Token token = new Token(character);
        character = ' ';
        return token;
    }
}
