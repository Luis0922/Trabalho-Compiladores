package symbols;
import lexer.Word;
public class Id {
    public final Word word;
    public final String type;
    public final String value;

    public Id(Word word, String type, String value, String type1, String value1) {
        this.word = word;
        this.type = type1;
        this.value = value1;
    }
}
