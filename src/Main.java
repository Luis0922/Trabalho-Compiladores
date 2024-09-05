import lexer.Lexer;
import parser.SemanticParser;  // Certifique-se de que essa classe existe e extenda Parser.

public class Main {
    public static void main(String[] args) throws Exception {
        var lexer = new Lexer("./src/teste.txt");
        SemanticParser parser = new SemanticParser(lexer);  // Usando o SemanticParser
        parser.program();  // Inicia a análise sintática e semântica
        System.out.println("Arquivo lido com sucesso!");
    }
}
