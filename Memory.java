/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica5;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * Clase Memory. Simula la memoria de un sistema a partir de su asignacion de segmentos. La clase en particular define los segmentos como si fuesen nodos.
 * De forma externa, se emplea la clase en listas ligadas para representar los segmentos de memoria.
 * Atributos:
 * type_space: Valor booleano que indica si un segmento contiene un proceso (pagina) o un espacio libre. (Pagina: True, Espacio libre: False)
 * tam: tamaño del segmento que representa. La cantidad de localidades que le corresponden al segmento. En este caso todos son de tamaño 16
 * loc_start: localidad inicial a partir de la que se define el segmento.
 * proc: Pagina asignada al macro
 * auxAsig: Marca si cuando una página se asigno el segmento donde se hizo tuvo que dividirse o no. Solo válido en ejecutción secuencial, no util en programación paralela.
 */
public class Memory {
    boolean type_space; //Define el tipo de segmento: Proceso (P)=True, Espacio libre (H)=False
    int tam,loc_start;
    Pagina proc; //Cambio de ser proceso a pagina. A cada segmento de la memoria se asigna una página de proceso, ya no el proceso completo
    static int auxAsig=0;
    /**
     * Constructor de clase. Este constructor inicializa una memoria vacía de tamaño 0 con "mapa" de macros vacios.
     */
    public Memory(){
        int i;
        type_space=false;
        tam=0;
        //for(i=0;i<64;i++)marcos[i]=false; //todos los marcos en un principio son vacios (false), cuando se le asigna un proceso se asigna pasa a ser true.
    }
    /**
     * Constructor de clase Memory. Este constructor se aplica para crear una asignación de espacio vacío. 
     * Siempre se definirá el segmento original como un segmento de espacio libre con tamaño total de memoria.
     * @param tamanio Tamaño relacionado al bloque.
     * @param loc_inicio Primera localidad del bloque.
     */
    public Memory(int tamanio,int loc_inicio){
        type_space=false;
        this.tam=tamanio;
        this.loc_start=loc_inicio;
    }
    /**
     * Constructor de la clase Memory. Este constructor se aplica cuando en memoria se está definiendo un proceso, es decir, se asinga un proceso a la memoria.
     * Este constructor no ase la asignación en memoria, sino que solo modela un nodo que se deberá insertar en la lista.
     * @param p Referencia al proceso.
     * @param tamanio Tamaño del bloque
     * @param loc_inicio Primera localidad a asingar.
     */
    public Memory(Pagina p, int tamanio, int loc_inicio){
        type_space=true;
        this.tam=tamanio;
        this.loc_start=loc_inicio;
        proc=p;
    }
    /**
     * busqueda. Recorre la lista ligada de memoria en busca de segmentos de espacio libre (tye_space==false)
     * @param lis Listado de memoria. Representa la memoria a partir de segmentos asignados y libres.
     * @param inS Posicion a partir de la cual se inicia la busqueda
     * @return Devuelve el índice en la lista del segmento vacio. En caso de no encontrarlo, devuelve un índice fuera del límite de la lista.
     */
    public int busqueda(List<Memory> lis,int inS){
        int index=inS,sizeList;
        sizeList=lis.size();
        while(index<sizeList){  //para recorrer todos los nodos de la lista
            while(index<sizeList&&lis.get(index).type_space){//Mientras el espacio este ocupado y no se rebase la lista
                index++;
            }
            if(index<sizeList)return index;   //todas las paginas son de 16 localidades exactamente, lo mismo con los macros, el primero de este libre se asigna directamente
            index++;
        }
        index++; 
        return index;
    }   
    /**
     * estado. Itera en la memoria imprimiendo cada macro definida dentro de ella mostrando si está ocupado por una página (P) o se encuentra libre (H)
     * De igual forma imprime la primera localidad de la macro y su extensión. El programa imprime por segmentos unicos, es decir, une los segmentos vacios como uno solo si son consecutivos
     * @param lis Listado de memoria. Representa la memoria a partir de segmentos asignados y libres.
     */
    public void estado(List<Memory> lis){
        int cont=0; //contador para contabilizar los espacios vacios.
        for (Memory segmento : lis) { //Iterando a traves de la memoria.
            if(cont==3){
                System.out.println("");
                cont=0;
            }
            if(segmento.type_space){ //Si el espacio esta ocupado por un proceso.
                System.out.print("|P:"+segmento.proc.pid+",Page:"+segmento.proc.numPag+"|"+segmento.loc_start+"|16|-->");
            }else{
                System.out.print("|H|"+segmento.loc_start+"|"+segmento.tam+"|-->");
            }
            cont++;
        }
        System.out.println("x");
    }   
        
    /**
     * acAsignacion. Metodo de acceso indirecto al método asignacionMemoria.
     * @param lis Listado de memoria. Representa la memoria a partir de segmentos asignados y libres.
     * @param index Indice en lista del espacio donde se asigna el nuevo proceso.
     * @param p Pagina que busca asignarse dentro de memoria.
     * @return  Copia de la pagina con las localidades en memoria asignada
     */
    public Pagina acAsignacion(List<Memory>lis,int index, Pagina p){
        Pagina px;
        px=asignacionMemoria(lis, index, p);
        return px;
    }
    /**
     * asignacionMemoria. Asigna en memoria un proceso indicado. El método accede a la posición indicada en la lista y realiza los cambios necesarios para crear el nuevo segmento en memoria.
     * Si el segmento donde se va a asignar es del mismo tamaño que el proceso, solo se referencia el proceso al segmento ya definido y se modifica a true el valor de type_space.
     * En caso de que el segmento sea menor al tamaño requerido por el proceso, se realiza una partición de dicho segmento.
     * @param lis Lista ligada de memoria. Representa la memoria a partir de segmentos asignados y libres.
     * @param index Indice en lista del segmento donde se pretende realizar la asignación.
     * @param p Proceso a asignar en memoria.
     * @return 
     */
    private Pagina asignacionMemoria(List<Memory> lis,int index,Pagina p){
        Memory aux,auxb;
        int tamA;
        aux=lis.get(index);
        tamA=16; //espacio constante de 16 pues ese es el tamaño de macros definido.
        p.dir_inicio=aux.loc_start;
        p.dir_fin=p.dir_inicio+tamA-1;
        if((aux.tam-tamA)==0){  //si el segmento donde se está asignando tiene el tamaño exacto del proceso, solo se modifican unos valores del segmento.
            aux.type_space=true;
            aux.proc=p;
            lis.set(index,aux);
            Memory.auxAsig=0;
        }else{ //si el segmento es de mayor espacio que el requerido por el proceso, se realiza una partición del segmento.
            auxb=new Memory(p,tamA,p.dir_inicio); //se crea un segmento de memoria auxiliar, tiene el tamaño de memoria requerido por el proceso y se asigna en la primera localidad del segmento vacío.
            lis.add(index, auxb); //se añade el nuevo segmento antes del segmento vacio donde trabajabamos.
            index++;
            aux.loc_start=p.dir_inicio+tamA;
            aux.tam=aux.tam-tamA; 
            lis.set(index, aux); //se modifica el espacio del segmento vacio, dejando el segmento original particionado en dos: uno donde se guarda el proceso, otro vacio.
            Memory.auxAsig=1;
        }
        return p;
    }
    /**
     * copyMemory. Realiza la copia simulada de la lsita ligada que representa una memoria. Esta copia solo tiene valores de tamaño de segmento, tipo del mismo y su loc_Start
     * La página asociada, se almacena con una página falsa sin información.
     * @param lis lista ligada de memoria original a copiar
     * @param copy Lista donde se almacena la copia de la memoria.
     */
    public void copyMemory(List<Memory> lis,List<Memory> copy){
        int i;
        Pagina pag;
        Proceso p;
        Memory mem;
        for(i=0;i<lis.size();i++){
            copy.add(new Memory(lis.get(i).tam,lis.get(i).loc_start));
            if(lis.get(i).type_space){
                copy.get(i).type_space=true;
                copy.get(i).proc=new Pagina();
            }
        }
    }
    /**
     * liberarMemoria. Metodo para el acceso indirecto para freeMemory.
     * @param lis Lista ligada de memoria. Representa la memoria a partir de segmentos asignados y libres.
     * @param p Proceso del que se pretende borrar su asignación en memoria.
     * @param queue Cola de procesos.
     * @return Retorna 1 si la liberacion fue correcta. 0 si hubo algun error.
     */
    public int liberarMemoria(List<Memory> lis, Proceso p, ColaProcesos queue){
        int aux;
        aux=freeFrames(lis,p,queue);
        if(aux==0)return 0;
        return aux;
    }
    public int libMem(List<Memory> lis, Pagina p, int index){
        if(freeMemory(lis,p, index)==0)return 0;
        return 1;
    }
    /**
     * freeMemory. Borra la asignación en memoria de un proceso indicado. El metodo recorre la lista en busca del segmento que represente al proceso indicado (con su pid).
     * En esta posición evalua los segmentos anterior y posterior a este. Si alguno es vacío,  entonces el segmento vacío absorbera al del proceso. En caso de que ambos sean vacios el primer segmento vacio absorbe al otro y al del proceso.
     * @param lis Lista ligada de memoria. Representa la memoria a partir de segmentos asignados y libres.
     * @param p Prorceso a eliminar de memoria.
     * @return Retorna 1 si la liberacion fue correcta. 0 si hubo algun error.
     */
    private int freeMemory(List<Memory> lis, Pagina p,int index){
        int sizeList=lis.size();
        Memory auxA,aux,auxB;
        int prev,post;
        //if(index<sizeList&&lis.get(index).proc!=null&&lis.get(index).proc.pid==p.pid){ 
            aux=lis.get(index);
            prev=index-1;
            post=index+1;
            if(index>0&&index<sizeList-1){ //si el segmento es el primero de la lista, entonces no tiene previo por evaluar
                if(!lis.get(prev).type_space){   //caso para evaluar si el segmento previo es espacio vacio
                    auxA=lis.get(prev);
                    auxA.tam+=aux.tam;  //el tamaño del segmento vacio absorbe al del proceso. Al eliminar el proceso de la lista, el segmento vacio cubre todo su espacio
                    lis.remove(index);
                    lis.set(prev, auxA);
                    //return 2;
                } else if(!lis.get(post).type_space){  //caso por si el segmento previo no es vacio pero si el segundo
                    auxB=lis.get(post);
                    aux.type_space=false;
                    aux.proc=null;
                    aux.tam+=auxB.tam;  //con esto el segmento del proceso ahora vacio "absorbe" el espacio del segmento vacio.
                    lis.remove(post);
                    lis.set(index, aux);
                    return 2;
                }
                auxA=lis.get(prev);
                if(!lis.get(index).type_space){  //caso para cuando el segmento previo era vacío y el post también
                    auxB=lis.get(index);
                    auxA.tam+=auxB.tam;
                    lis.remove(index);
                    lis.set(prev, auxA);
                    return 3;
                }
            }
            if(index==0&&index!=sizeList-1){
                if(!lis.get(post).type_space){  //caso por si no hay segmento previo y el segmento siguiente es vacio
                    auxB=lis.get(post);
                    aux.type_space=false;
                    aux.proc=null;
                    aux.tam+=auxB.tam;  //con esto el segmento del proceso ahora vacio "absorbe" el espacio del segmento vacio.
                    lis.remove(post);
                    lis.set(index, aux);
                    return 2;
                }
            }else if(index>0&&index==sizeList-1){ //caso por si hay segmento previo y vacío pero no hay segmento siguiente
                if(!lis.get(prev).type_space){   //caso para evaluar si el segmento previo es espacio vacio
                    auxA=lis.get(prev);
                    auxA.tam+=aux.tam;  //el tamaño del segmento vacio absorbe al del proceso. Al eliminar el proceso de la lista, el segmento vacio cubre todo su espacio
                    lis.remove(index);
                    lis.set(prev, auxA);
                    return 2;
                }
            }
            //El segmento no tiene segmento previo ni siguiente, por tanto es unico.
            aux.type_space=false;  //caso donde ninguno de los segmentos previo y post eran vacios.
            aux.proc=null;
            return 1;
        /*else{
                System.out.println("Error de asignación, el proceso indicado no coincide con alguno de los procesos asignados");
                return 0;
        }*/
        /**int sizeList=lis.size();
        Memory aux;
        if(index<sizeList&&lis.get(index).proc!=null&&lis.get(index).proc.pid==p.pid){ 
            aux=lis.get(index);
            aux.type_space=false;  //caso donde ninguno de los segmentos previo y post eran vacios.
            aux.proc=null;
            lis.set(index, aux);
            return 1;
        }else{
            System.out.println("Error de asignación, el proceso indicado no coincide con alguno de los procesos asignados");
            return 0;
        }**/
    }
    /**
     * freeFrames. Metodo para liberar la memoria asignada para el proceso en todas sus paginas. La liberación es de la última página en memoria a la primera.
     * @param lis Lista ligada que representa la memoria
     * @param p Proceso del que se debe liberar las paginas.
     * @param queue Cola de procesos activos.
     * @return Retorna 1 si la liberacion fue correcta. 0 si hubo algun error.
     */
    private int freeFrames(List<Memory> lis,Proceso p, ColaProcesos queue){
        int i,aux;
        Pagina pg;
        p.imprimeProcesos(queue);
        for(i=p.numPaginas-1;i>=0;i--){
            pg=p.tabla.get(i);
            aux=freeMemory(lis,pg,pg.indexVirtual);
            if(aux==0)return 0;
            if(aux!=1&&queue.size!=0)p.modIndexInd(lis,queue,pg.indexVirtual,aux);
        }
        return 1;
    }
    /**
     * desfragmentar. Metodo de la clase memoria utilizado para realizar una desfragmentacion de la memoria
     * para liberarla de huecos en esta y tener una representacion eficiente de esta lo que podria
     * ser de utilidad para almacenar mas procesos en los 1024 bloques manejados.
     * @param memoria Listado de memoria. Representa la memoria a partir de segmentos asignados y libres.
     */
        public void desfragmentar(LinkedList<Memory> memoria){
        int num_espacios_vacios = 0; //Para crear un hueco al final de los procesos
        int i; //Contador "i" extremadamente importante para los nuevos datos.
        List<Memory> paginas=new ArrayList<>(); //Lista de paginas a organizar
        boolean memoriaVacia = true;
        for (Memory segmento : memoria) { //Iterando a traves de la memoria.
            if(segmento.type_space){ //Si esta ocupado por un proceso.
                paginas.add(segmento);//Agregalo a paginas
                memoriaVacia = false;
            }else{
                num_espacios_vacios += segmento.tam;//Registra numero de huecos libres.
            }
        }
        if (memoriaVacia){//En caso de que este vacia
            System.out.println("Memoria vacia. Considere crear algun proceso.");
            return;
        }
        System.out.println("Desfragmentando memoria...");
        memoria.clear(); //Limpia la memoria para comenzar la defragmentacion.
        /*
        Debido a que agregamos secuencialmente podemos utilizar un unico contador i
        con este solo es necesario realizar los calculos necesarios en cada iteracion
        para tener los datos de todos los procesos en regla.
        */
        i=0; 
        for (Memory pagina : paginas){
            pagina.proc.indexVirtual = i; //El idx virtual solo aumenta en 1 cada pagina
            pagina.proc.dir_inicio = i*16;//Cada inicio de pagina comienza en multiplo de 16
            pagina.proc.dir_fin = i*16+15;//Multiplo de 16 + 15; ej 15 31 47 ... etc
            pagina.loc_start = i*16; //Cada locacion inicial de la pagina crecera en 16
            memoria.add(pagina); //Añadimos la pagina a la nueva memoria
            i++;
        }
        //Añadimos el segmento vacio para asegurarnos de mantener los 1024 bloques.
        memoria.add(new Memory(num_espacios_vacios,i*16));
        System.out.println("Memoria desfragmentada correctamente.");
    }
}