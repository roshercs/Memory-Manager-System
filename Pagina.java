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
public class Pagina {
    int pid;
    int numPag;
    int dir_inicio,dir_fin;
    int inst;
    int indexVirtual; //indice virtual, es decir, el indice en la lista ligada de memoria
    /**
     * Constructor de clase Pagina
     * @param num   Representa el numero de página que representa esta para el proceso
     * @param proc Numero de proceso (pid)
     * @param dirI Localidad inicial de pagina
     * @param dirF Ultima localidad de pagina
     * @param ind Indice dentro de la lista ligada que representa la memoria
     */
    public Pagina(int num,int proc, int dirI, int dirF,int ind){
        numPag=num;
        pid=proc;
        dir_inicio=dirI;
        dir_fin=dirF;
        indexVirtual=ind;
        //inst=i;
    }
    /**
     * Constructor "falso" de página. Se emplea únicamente para poder hacer asignaciones de páginas de forma virtual, en una copia de lista de memoria
     */
    public Pagina(){
        pid=-1;
    }
    /**
     * Constructor de clase Pagina. Este constructor asigna numero de pagina y proceso, la asignación de memoria se realiza de forma externa.
     * @param num Representa el numero de página que representa esta para el proceso
     * @param proc  Numero de proceso (pid)
     */
    public Pagina(int num,int proc){
        numPag=num;
        pid=proc;
    }
    /**
     * imprimePag. Imprime el contenido de una página.
     */
    void imprimePag(){
        System.out.println("Pid:"+pid);
        System.out.println("Numero de pagina:"+numPag);
        System.out.println("Primera localidad en memoria:"+dir_inicio);
        System.out.println("Ultima localidad en memoria"+dir_fin);
        
    }
}
