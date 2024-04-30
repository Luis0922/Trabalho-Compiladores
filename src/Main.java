import lexer.Lexer;
import lexer.Token;
import lexer.Word;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var lexer = new Lexer("C:\\Users\\dti Digital\\Documents\\Projetos\\Cefet\\Compiladores\\Trabalho-Compiladores\\src\\teste.txt");
        Token token;
        while ((token = lexer.scan()) != null) {
            System.out.print("<" + token.tag + ", " + Word.token.tag + "> ");
            if(token.toString().equals("59")){
                System.out.print("\n");
            }
        }
    }
}