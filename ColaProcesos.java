/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica5;

import java.util.List;
import java.util.LinkedList;
/**
 * Clase ColaProceso. Representa colas de procesos. En esta se almacenan todos los procesos en el sistema.
 * Todos los procesos acceden a esta cola en espera de su ejecución, o con algún otro fin que se pretenda.
 * Nodo root: nodo inicial de la cola, representa el head de la cola.
 * last: nodo final de cola, representa el último elmento en cola.
 * size: representa la cantidad de elementos en cola.
 * @author roger
 */
public class ColaProcesos {
    Nodo root,last;
    int size;
    /**
     * Metodo constructor para una cola vacia.
     */
    public ColaProcesos(){
        root=null;
        last=null;
    }
    /**
     * Constructor de clase ColaProcesos. Crea una cola con un nodo n como head.
     * @param n Nodo que se asingará como root de la cola.
     */
    public ColaProcesos(Nodo n){
        root=n;
        last=n;
        size=1;
    }
    /**
     * isEmpty. Verifica si la cola se encuentra vacía.
     * @return True si la cola se encuentra vacia, false en caso contrario.
     */
    public boolean isEmpty(){
        if(root==null)
            return true;
        else return false;
    }
    /**
     * encolar. Metodo para acceso indirecto a método enqueue.
     * @param n Nodo a encolar
     */
    public void encolar(Nodo n){
        this.enqueue(n);
    }
    /**
     * enqueue. Añade un elemento a la cola al final de esta.
     * @param n Nodo a encolar.
     */
    private void enqueue(Nodo n){
        if(this.isEmpty()){
            root=n;
            size=1;
        }else{
            last.next=n;
            size++;
        }
        last=n;
    }
    /**
     * dequeue. Metodo para desencolar un elemento de la cola. Obtiene y elimina el primer elemento de la cola.
     * @return Devuelve el primer nodo de la cola, que contiene el proceso en cola.
     */
    public Nodo dequeue(){
        Nodo aux;
        if(this.isEmpty()){
            System.out.println("La cola de procesos se encuentra vacía");
            size=0;
            return null;
        }else{
            aux=root;
            size--;
            if(size!=0){
                root=root.next;
            }else{
               last=null;
               root=null;
            }
            
        }
        return aux;
    }
    /**
     * clear. Limpia la cola de procesos. Las referencias root y last se vuelven null, perdiendo referencia de cualquier otro proceso en la cola.
     * @return 
     */
    private List<Nodo> clear(){
        int i,tam;
        List<Nodo> lista=new LinkedList();
        tam=this.size;
        for(i=0;i<tam;i++){
            lista.add(this.dequeue());
        }
        this.root=null;
        this.last=null;
        return lista;
    }
    
    /**
     * imprimirCola. Imprime todos los elementos de la cola. Imprimiendo de cada proceso su pid, nombre e  instrucciones pendientes.
     */
    public void imprimirCola(){
        Nodo n;
        int i,inst_pendientes;
        n=this.root;
        for(i=0;i<size;i++){
            inst_pendientes=n.proc.inst_total-n.proc.inst_ejec;
            System.out.println("Proceso: "+n.proc.pid+"\nNombre: "+n.proc.nombre+"\nInstrucciones pendientes: "+inst_pendientes+"\n\n\n");
            n=n.next;
        }
    }
    /**
     * first. Regresa el primer nodo en la cola.
     */
    public Nodo first(){
        return this.root;
    }
    /**
     * numProcesos. Regresa el tamaño de la cola, que sirve para conocer el numero de procesos por ejecutar.
     * @return el numero de procesos en tipo entero.
     */
    public int numProcesos(){
        return this.size;
    }
    
}