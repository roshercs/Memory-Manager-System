/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica5;

/**
 *
 * @author roger
 */
public class Nodo {
    Proceso proc;
    Nodo next;

    public Nodo(Proceso p){
        next=null;
        proc=p;
    }
    public Nodo(Proceso p, Nodo n){
        next=n;
        proc=p;
    }
}