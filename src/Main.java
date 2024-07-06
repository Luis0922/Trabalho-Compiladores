import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import lexer.Word;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var lexer = new Lexer("C:\\Users\\dti Digital\\Documents\\Projetos\\Cefet\\Compiladores\\Trabalho-Compiladores\\src\\teste.txt");
        Token token;
        while ((token = lexer.scan()) != null) {
            if(token.tag < 256) {
                System.out.println("< " + (char) token.tag + " >");
            }
            else {
                System.out.println("< " + Tag.getTagName(token.tag) + " , " + token + " >");
            }
        }
    }
}