package lexer;

import symbols.*;

import java.util.Objects;

public class Id {

    private Word name;
    private Type type;
    private int scope;

    public Id(Word name, Type type, int scope) {
        this.name = name;
        this.type = type;
        this.scope = scope;
    }
    public Word getName() {
        return name;
    }

    public void setName(Word name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id id = (Id) o;
        return Objects.equals(name, id.name) &&
                Objects.equals(type, id.type) &&
                Objects.equals(scope, id.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, scope);
    }

    @Override
    public String toString() {
        return "Id{" +
                "nome='" + name + '\'' +
                ", tipo='" + type + '\'' +
                ", escopo='" + scope + '\'' +
                '}';
    }
}
