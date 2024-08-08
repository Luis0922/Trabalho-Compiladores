
import lexer.Lexer;

import parser.Parser;



public class Main {
    public static void main(String[] args) throws Exception {
        var lexer = new Lexer("./src/teste.txt");
        Parser parser = new Parser(lexer);
        parser.program();
        System.out.println("fim");
    }
}