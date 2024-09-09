package semantic;

public class Symbol {
    private String name;
    private String type;

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters para name e type
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
