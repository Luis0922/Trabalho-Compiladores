package symbols;

import lexer.Token;

import java.util.*;

public class Env {
    private Hashtable table; //tabela de símbolos do ambiente
    protected Env prev; //ambiente imediatamente superior

    public Env(Env n){
        table = new Hashtable(); //cria a TS para o ambiente
        prev = n; //associa o ambiente atual ao anterior
    }

    /*Este método insere uma entrada na TS do ambiente */
    /*A chave da entrada é o Lexer.Token devolvido pelo analisador léxico */
    /*Id é uma classe que representa os dados a serem armazenados na TS para */
    /*identificadores */
    public void put(Token w, Id i){
        table.put(w,i);
    }

    /*Este método retorna as informações (Id) referentes a determinado Lexer.Token */
    /*O Lexer.Token é pesquisado do ambiente atual para os anteriores */
    public Id get(Token w){
        for (Env e = this; e!=null; e = e.prev){
            Id found = (Id) e.table.get(w);
            if (found != null) //se Lexer.Token existir em uma das TS
                return found;
        }
        return null; //caso Lexer.Token não exista em uma das TS
    }
}

