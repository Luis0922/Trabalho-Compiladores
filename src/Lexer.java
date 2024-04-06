import java.io.*;
import java.util.*;
public class Lexer {
    public static int line = 1; //contador de linhas
    private char character = ' '; //caractere lido do arquivo
    private FileReader file;
    private Hashtable symbolTable = new Hashtable();

    /* Método para inserir palavras reservadas na HashTable */
    private void addReservedWordIntoSymbolTable(Word word) {
        symbolTable.put(word.getLexeme(), word); // lexema é a chave para entrada na
        //HashTable
    }

    /* Método construtor */
    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException exception) {
            System.out.println("Arquivo não encontrado");
            throw exception;
        }

        //Insere palavras reservadas na HashTable
        addReservedWordIntoSymbolTable(new Word("if", Tag.IF));
        addReservedWordIntoSymbolTable(new Word("program", Tag.PRG));
        addReservedWordIntoSymbolTable(new Word("begin", Tag.BEG));
        addReservedWordIntoSymbolTable(new Word("end", Tag.END));
        addReservedWordIntoSymbolTable(new Word("type", Tag.TYPE));
        addReservedWordIntoSymbolTable(new Word("int", Tag.INT));
    }

    /*Lê o próximo caractere do arquivo*/
    private void readNextCharacter() throws IOException {
        character = (char) file.read();
    }

    /* Lê o próximo caractere do arquivo e verifica se é igual a nextCharacter*/
    private boolean readNextCharacter(char nextCharacter) throws IOException {
        readNextCharacter();
        if (character != nextCharacter) return false;
        character = ' ';
        return true;
    }

    public Token scan() throws IOException{
        //Desconsidera delimitadores na entrada
        for (;; readNextCharacter()) {
            if (character == ' ' || character == '\t' || character == '\r' || character == '\b') continue;
            else if (character == '\n') line++; // line count
            else break;
        }

        switch(character){
            // Operators
            case '!':
                // Se o proximo caractere for = retorna o not equal, se não cria um novo token "!"
                if (readNextCharacter('=')) return Word.not_eq;
                else return new Token('!');
            case '>':
                if (readNextCharacter('>')) return Word.big_big;
                else return new Token('>');
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
            Word word = (Word)symbolTable.get(string);
            if (word != null) return word; // word already exists in HashTable
            word = new Word (string, Tag.ID);
            symbolTable.put(string, word);
            return word;
        }

        // Characters not specified
        Token token = new Token(character);
        character = ' ';
        return token;
    }
}
