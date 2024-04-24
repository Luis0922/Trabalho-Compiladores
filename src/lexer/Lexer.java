package lexer;

import java.io.*;
import java.util.*;
public class Lexer {
    public static int line = 1; //contador de linhas
    private char character = ' '; //caractere lido do arquivo
    private FileReader file;
    private boolean EOF;
    private Hashtable reservedWords = new Hashtable();

    /* Método para inserir palavras reservadas na HashTable */
    private void addReservedWord(Word word) {
        reservedWords.put(word.getLexeme(), word); // lexema é a chave para entrada na
        //HashTable
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

        Token token;
        while ((token = scan()) != null) {
            System.out.print(token + " ");
            if(token.toString().equals("59")){
                System.out.print("\n");
            }
        }
    }

    /*Lê o próximo caractere do arquivo*/
    private void readNextCharacter() throws IOException {
        int next = file.read();
        if (next == -1) {
            EOF = true; // supondo que você tenha uma constante EOF definida
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

    public Token scan() throws IOException{
        if(EOF){
            return null;
        }
        // Desconsidera delimitadores na entrada
        for (;; readNextCharacter()) {
            if (character == ' ' || character == '\t' || character == '\r' || character == '\b') continue;
            else if (character == '\n') line++; // line count
            else break;
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
            case '&':
                if (readNextCharacter('&')) return Word.and;
            case ':':
                if (readNextCharacter('=')) return Word.defined_as;
        }

        // Digit
        if (Character.isDigit(character)){
            int value=0;
            do{
                value = 10*value + Character.digit(character,10);
                readNextCharacter();
            }while(Character.isDigit(character));
            return new Num(value);
        }

        // Identifiers
        if(Character.isLetterOrDigit(character)){
            StringBuffer stringBuffer = new StringBuffer();

            do{
                stringBuffer.append(character);
                readNextCharacter();
            }while(Character.isLetterOrDigit(character));

            String string = stringBuffer.toString();
            Word word = (Word)reservedWords.get(string);
            if (word != null) return word; // word already exists in HashTable
            word = new Word(string, Tag.ID);
            reservedWords.put(string, word);
            return word;
        }

        // Characters not specified
        Token token = new Token(character);
        character = ' ';
        return token;
    }
}
