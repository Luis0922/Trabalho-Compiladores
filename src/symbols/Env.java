package symbols;
import lexer.*;
import java.util.*;
public class Env {
    private Hashtable table;
    protected Env prev;

    public Env(Env n){
        table = new Hashtable();
        prev = n;
    }

    public void put(Token w, Id i){
        table.put(w,i);
    }

    public Id get(Token w){
        for (Env e = this; e!=null; e = e.prev){
            Id found = (Id) e.table.get(w);
            if (found != null) //se Token existir em uma das TS
                return found;
        }
        return null; //caso Token não exista em uma das TS
    }
}

