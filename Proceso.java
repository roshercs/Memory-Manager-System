/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica5;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Queue;

/**
 * Clase Proceso. Representación de procesos en el sistema operativo. Incluye clases para la administración de procesos, implicando su administración en memoria, su ejecución y su finalización.
 * nombre. Representa el nombre que caracteriza el proceso.
 * pid. Representa el identificador del proceso.
 * taman: Representa el espacio de memoria asignado al proceso
 * dir_inicio: Primer localidad asignada al proceso.
 * dir_fin: Ultima localidad asignada al proceso.
 * inst_total: Cantidad total de instrucciones del proceso.
 * inst_ejec: Cantidad de instrucciones ejecutadas del proceso.
 * @author roger & emi
 */
public class Proceso {
    public String nombre; //nombre del proceso
    public int pid; //numero de proceso
    public int taman;
    public int inst_total, inst_ejec;
    public List<Pagina> tabla=new LinkedList();
    int numPaginas;
    //public int dir_inicio, dir_fin, inst_total, inst_ejec; 
    static int tamanios[]={64,128,256,512}; //tamaño de espacios disponibles para el proceso
    Memory m=new Memory();
// dir_inicio:primera localidad de memoria, dir_fin: ultima localidad de memoria del proceso. inst__total:instrucciones totales del proceso, inst_ejec: instrucciones ejecutadas.
    public Proceso(){
        this.pid=-1;
        this.inst_total=0;
        this.taman=0;
    }
    
    /**
     * Constructor de la clase proceso, en esta únicamente se hace la asignación de nombre y pid del proceso, así como las instrucciones ejecutadas se fijan en 0 en comienzo.
     * La asignación de memoria se hace de forma externa.
     * @param nombre Nombre del proceso.
     * @param pid Numero de proceso, funge también como su identificador, pues es único
     * @param tam Tamaño de espacio en memoria asignado (espacio asignado)
     * @param inst Numero de instrucciones totales.
     * @param i Numero de páginas en las que se dividirá el proceso.
     */
    public Proceso(String nombre,int pid,int tam,int inst,int i){
        this.nombre=nombre;
        this.pid=pid;
        this.inst_ejec=0;
        this.taman=tam;
        this.inst_total=inst;
        //this.tabla=new Pagina[i]; //inicializa la tabla de páginas con la cantidad de páginas i
        this.numPaginas=i;
    }
    /**
     * crearProceso. Este método simula crear un proceso. Dentro de este se asingará un tamaño de espacio para el proceso,
     * así como la cantidad de instrucciones para el proceso. Estas asignaciones se hacen de forma aleatoria. El proceso se dividirá en páginas de tamaño 16 y se asignará cada una en memoria
     * @param list_mem Lista ligada de memoria. Dentro de esta se representa la asignación de memoria entre los procesos y espacios libres. Se modela con la clase Memory.
     * @param nom Nombre del proceso a crear.
     * @param numPid Valor pid global. Este valor se tiene de forma externa en la main principal. Contando a partir de 1, cada proceso tendrá un numero asignado. Este atributo indica cual es el pid último empleado.
     * @param queue Cola de procesos. Representa la cola ready de procesos. En esta, cada proceso está en estado ready a la espera de su ejecución.
     * @return 1 si el proceso fue creado correctamente. 0 si no.
     */
    public int crearProceso(List<Memory> list_mem, String nom,int numPid,ColaProcesos queue){
        Proceso p,aux;
        List<Memory> copyList=new LinkedList();
        //generamos una 1copia virtual de la lista ligada de memoria, los cambios en esta no se reflejan en la lista original
        m.copyMemory(list_mem, copyList);
        Pagina pag,auxPag;
        Nodo n;
        Memory m=new Memory();
        int tamC,numInst,index[],div;
        int i,auxIn,j;
        pag=new Pagina();
        tamC=espacioProceso();
        div=tamC/16; //para saber la cantidad de páginas en un proceso
        if(tamC%16!=0)div++; //por si tenemos la última página con menos de 16 instrucciones
        index=new int[div];
        auxIn=0;
        for(i=0;i<div;i++){
            index[i]=m.busqueda(copyList, auxIn); //se añade para que se busquen espacios para cada página
            if(index[i]>copyList.size()){ //hacemos que se indique que no hay memoria suficiente por que tecnicamente, no estamos implementando memoria virtual en esta práctica
                System.out.println("Index: "+index[i]);
                System.out.println("size: "+copyList.size());
                System.out.println("No hay memoria suficiente para la asignación de memoria para el proceso.\nConsidere eliminar o ejecutar otros procesos");
                return 0;
                        
            }
            m.acAsignacion(copyList, index[i], pag);
            auxIn=index[i]; //para retomar la busqueda en el elemento siguiente al recien asignado
        }
        /**
         * Al salir del ciclo for anterior, obtendremos todos los indices para las páginas del proceso. Se hizo asi por que
         * en la lista ligada pueden haber espacios vacios donde caben más de una página. No usamos una representación
         * de los marcos de memoria debido a la solicitud de usar la lista ligada para representar la memoria
         *        
        */
        numInst=instProceso();
        p=new Proceso(nom,numPid+1,tamC,numInst,div); //pese a crear el proceso, aun no tiene asignación en memoria ni está en la cola
        for(i=0;i<div;i++){
            pag=new Pagina(i,p.pid);
            auxPag=m.acAsignacion(list_mem, index[i], pag);
            pag.dir_inicio=auxPag.dir_inicio;
            pag.dir_fin=auxPag.dir_fin;
            pag.indexVirtual=index[i];
            p.tabla.add(pag); //se añade a la tabla de paginación
            if(Memory.auxAsig==1){ //si para asignar la pagina se dividio el segmento entonces se incrementa en uno los indices de las paginas existentes posteriores a este indice
                modIndexAum(list_mem, queue, index[i]); 
                Memory.auxAsig=0;
            }
        }
        n=new Nodo(p);
        queue.encolar(n);
        System.out.println("Espacio de proceso: "+tamC);
        System.out.println("Numero de instrucciones asignadas: "+numInst);
        //System.out.println("Primera localidad en memoria: "+aux.dir_inicio);
        System.out.println("Tabla de Paginación:");
        p.imprimeTabla();
        return 1;  //si se retorna 1 es que se creo el proceso. el numPid del main debe incrementarse en 1
    }

    public int matarProceso(List<Memory> mem, ColaProcesos queue, List<String> eliminated){
        Nodo n;
        Proceso p;
        int inst_e, inst_tot, i;
        if(queue.isEmpty()){
            System.out.println("La cola de procesos esta vacia, no existe proceso para eliminar.");
            return 0;
        }
        System.out.println("Eliminando proceso...");
        //Desencolando el proceso actual y obteniendo sus atributos.
        n = queue.dequeue();
        p = n.proc; 
        //Imprimiendo instrucciones restantes.
        inst_e=p.inst_ejec;
        inst_tot=p.inst_total;
        i=inst_tot-inst_e;
        System.out.println("Proceso con " + i + " sin ejecutar");
        //Liberando su espacio en memoria y agregandolo a la lista de eliminados
        m.liberarMemoria(mem, p,queue);
        eliminated.add(p.nombre);
        System.out.println("Proceso eliminado");
        return 1;
    }
    /**
     * Impresión de tabla de paginación del proceso. Imprime la tabla de paginación, mostrando numero de páginas, tamaño,
     * El número de página, su dirección virtual, y su dirección física.
     */
    void imprimeTabla(){
        int i;
        Pagina aux;
        System.out.println("Numero de paginas: "+numPaginas);
        System.out.println("tamaño de paginacion: "+tabla.size());
        for(i=0;i<tabla.size();i++){
            aux=tabla.get(i);
            System.out.println("Pagina "+aux.numPag+": ");
            System.out.println("Frame: "+aux.indexVirtual);
            System.out.println("Localidades de memoria: "+aux.dir_inicio+"-"+aux.dir_fin);
            System.out.println("\n");
        }
    }
    /**
     * ejecutarProceso. Metodo que simula la ejecución del proceso actual (el primer elemento de la cola ready).
     * Para la simulación se "ejecutan" 5 instrucciones del proceso. Si se ejecutan todas las instrucciones totales del proceso,
     * entonces se indica su finalización y se libera la memoria asignada al proceso. Si no se ejecutan las instrucciones totales del proceso,
     * este pasa al final de la cola ready.
     * @param mem Lista ligada de memoria. Representa los segmentos en memoria, libres u ocupados por un proceso.
     * @param queue Cola de procesos. Contiene los procesos en estado ready a la espera de su ejecución.
     * @param ended Lista de procesos finalizados exitosamente su ejecución. Los procesos de esta lista ejecutaron todas las instrucciones asignadas.
     */
    public void ejecutarProceso(List<Memory> mem,ColaProcesos queue,List<String> ended){
        int i,j,inst_e, inst_tot;
        Nodo n;
        Proceso p;
        if(queue.isEmpty()){
            System.out.println("La cola de procesos esta vacia. Considere crear un proceso antes de ejecutar un proceso...");
            return;
        }
        n=queue.dequeue();
        p=n.proc;
        inst_e=p.inst_ejec;
        inst_tot=p.inst_total;
        i=inst_tot-inst_e; //i representa las instrucciones restantes por ejecutar.
        if(i<=5){  //este caso es cuando se tienen menos de 5 instrucciones restantes por ejecutar.
            System.out.println("Ejecutando proceso"+p.pid+"...");
            j=1;
            while(i>0){ 
                System.out.println("Instrucciones ejecutadas: "+j);
                j++;
                i--;
            }
            System.out.println("El proceso "+p.pid+" ha concluido su ejecucion\n Liberando memoria...");
            //Para liberar memoria 
            m.liberarMemoria(mem, p,queue);
            System.out.println("Memoria liberada");
            ended.add(p.nombre);
        }else{
            System.out.println("Ejecutando proceso"+p.pid+"...");
            for(j=0;j<5;j++) System.out.println("Instrucciones ejecutadas: "+j);
            inst_e+=5; //"restamos" 5 instrucciones. Se suman a inst_ejec para que la diferencia con total de las instrucciones restantes.
            p.inst_ejec=inst_e; //actualizamos las instrucciones ejecutadas del proceso.
            n.proc=p; //el valor del proceso en cola también es actualizado.
            System.out.println("Cambiando de proceso...");
            queue.encolar(n); //el proceso pasa al final de la cola.
            System.out.println("Cambio exitoso");
            /*try{
                Thread.sleep(3000);  //para detener el programa por 3 segundos.
            }catch(InterruptedException e){}*/
            
        }
        
        
    } 
    /**
     * nextProces. Simula el cambio del proceso actual por el siguiente en cola ready. El cambio en cola implica que el primer elemento
     * pase al final de la cola. Esto en efectos prácticos es desencolar y luego encolar en la cola.
     * @param queue Cola de procesos. Contiene el listado de procesos en estado ready esperando su ejecución.
     * @return 0: si no se pudo cambiar de proceso. 1: si el cambio fue correcto.
     */
    public int nextProcess(ColaProcesos queue){
        Nodo n;
        if(queue.isEmpty()){
            System.out.println("La cola de procesos esta vacia. Considere crear un proceso antes de cambiar entre ellos");
            return 0;
        }
        System.out.println("Cambiando de contexto...");
        n=queue.dequeue();
        queue.encolar(n);
        System.out.println("Proceso cambiado");
        return 1;
    }
    
    /**
     * imprimeProcesoActual. Imprime la informacion del proceso actual.
     * @param queue Cola de procesos. Contiene el listado de procesos en estado ready esperando su ejecución.
     */
    public void imprimeProcesoActual(ColaProcesos queue){
        Nodo n;
        Proceso p;
        if(queue.isEmpty()){
            System.out.println("La cola de procesos esta vacia. Considere crear un proceso.");
            return;
        }
        System.out.println("Proceso actual:");
        n = queue.first();
        p = n.proc; 
        //imprimiendo atributos del proceso actual.
        System.out.println("Nombre: " + p.nombre);
        System.out.println("ID: " + p.pid);
        System.out.println("Instrucciones totales: " + p.inst_total);
        System.out.println("Instrucciones ejecutadas: " + p.inst_ejec);
        System.out.println("Tabla de paginación:");
        p.imprimeTabla();
    }
    /**
     * matarProceso. Simula la finalizacion abrupta del proceso en ejecucion, liberando su espacio de memoria
     * pasandolo a los procesos eliminados y finalmente informando al usuario las instrucciones restantes
     * @param mem Lista ligada de memoria. Representa los segmentos en memoria, libres u ocupados por un proceso.
     * @param queue Cola de procesos. Contiene los procesos en estado ready a la espera de su ejecución.
     * @param eliminated Lista de procesos finalizados abruptamente. Los procesos de esta lista no finalizaron todas sus instrucciones.
     */
    public void verEstadoActual(List<Memory> mem, ColaProcesos queue, List<String> eliminated, List<String> ended){
        System.out.println("Sistema: ");
        if(queue.isEmpty()){
            System.out.println("Ningun proceso en espera.");
        }else{
           System.out.println("Numero de procesos listos: "+ queue.numProcesos());
        }
        if(ended.isEmpty()){
            System.out.println("Ningun proceso finalizado.");
        }else{
            System.out.println("Procesos finalizados:");
            for (String proceso : ended) {
                System.out.println(proceso);
            }
        }
        if(eliminated.isEmpty()){
            System.out.println("Ningun proceso eliminado.");
        }else{
           System.out.println("Procesos eliminados:");
            for (String proceso : eliminated) {
                System.out.println(proceso);
            }
        }
        //System.out.println("\nEstado de memoria:");
        //m.estado(mem);
        
    }
    /**
     * Método espacioProceso. Obtiene de forma aleatoria el tamaño de espacio que se asignará al proceso.
     * Usa el método Math.random para generar números aleatorios. Con la operación *4+1 indicamos que el número generado esté entre 1 y 4.
     * En el arreglo tamanios definimos los posibles tamaños de procesos, el valor devuelto por el método Math.random se castea a entero y se usa como índice para el arreglo.
     * @return num: valor entero que representa la cantidad de localidades a asignar a un proceso.
     */
    private int espacioProceso(){
        int num;
        num=tamanios[(int)(Math.random()*4)];
        return num;
    }
    /**
     * instProceso. Obtiene una cantidad de instrucciones (10-30) para un proceso de forma aleatoria.
     * @return Valor entero con la cantidad de instrucciones a asignar al proceso.
     */
    private int instProceso(){
        int num;
        num=(int)(10+Math.random()*20); //para que el valor generado esté entre 10 y 30
        return num;
    }
    /**
     * imrpimeProcesos. Imprime la cola de procesos ready. Hace uso de método imprimeProcesos de la clase ColaProcesos.
     * @param queue  Cola de procesos. 
     */
    public void imprimeProcesos(ColaProcesos queue){
        System.out.println("Procesos: ");
        queue.imprimirCola();
    }
    
    public void modIndexInd(List<Memory> lis,ColaProcesos queue, int index,int aux){
        modIndex(lis, queue,index,aux);
    }
    /**
     * Modificador de indices virtuales de páginas de proceso. Modifica el indexVirtual de los procesos en la ColaProcesos.
     * Aplicable cuando se libera una página de memoria, lo que modifica el indice virtual de todas las páginas asignadas posterior a esta.
     * @param lis Lista ligada que representa la memoria
     * @param queue Cola de procesos activos.
     * @param index Indice de la página que fue eliminada.
     * @param aux Indica la cantidad de segmentos que se "perdieron" en la lista. 2 para indicar que se elimino un elemento, 3 para indicar que fueron 2 elementos.
     */
    private void modIndex(List<Memory> lis,ColaProcesos queue, int index,int aux){
        Nodo n;
        Proceso p;
        Pagina pg,pgb;
        int i,j,ind;
        aux--;
        for(i=0;i<queue.size;i++){
            n=queue.dequeue();
            p=n.proc;
            j=p.numPaginas-1;
            while(j>=0&&p.tabla.get(j).indexVirtual>index){  //recorre todas las págigas de un proceso de la ultima a la primera y aquellas con indice mayor al del segmento modificado se disminuye
                ind=p.tabla.get(j).indexVirtual-aux;
                p.tabla.get(j).indexVirtual-=aux; //Al fusionar dos segmentos la lista disminuye en uno su indice, por eso se resta a todas aquellas tablas posteriores a este indice uno o 2 si se fusionaron 3 segmentos
                lis.get(ind).proc.indexVirtual=ind;
                j--;
            }
            n.proc=p;
            queue.encolar(n);
            
        }
        
        /*if(aux==2){ //caso donde hubo una única union de segmentos y por tanto se disminuye en uno el índice virtual de las páginas
            for(i=0;i<queue.size;i++){
                n=queue.dequeue();
                p=n.proc;
                for(j=0;j<p.numPaginas;j++){
                    pg=p.tabla.get(j);
                    if(pg.indexVirtual>index){
                        ind=pg.indexVirtual-1;
                        p.tabla.set(j, pg);
                        m=lis.get(ind);
                        m.proc.indexVirtual=ind;
                        lis.set(ind, m);
                    }
                }
                n.proc=p;
                queue.encolar(n);
            }
        }else{  //caso donde hubo dos uniones de segmentos (al liberar una página se une con un segmento vacio y un post vacio)
            for(i=0;i<queue.size;i++){
                n=queue.dequeue();
                p=n.proc;
                for(j=0;j<p.numPaginas;j++){
                    pg=p.tabla.get(j);
                    if(pg.indexVirtual>index){
                        ind=pg.indexVirtual-2;
                        m=lis.get(ind);
                        m.proc.indexVirtual=ind;
                        lis.set(ind, m);
                    }
                }
                n.proc=p;
                queue.encolar(n);
            }
        }/**/
    }
    /**
     * modIntexAum. Incrementa los indices de las páginas existentes en memoria cuyo valor de indice sea mayor que el indicado 'index'
     * @param lis Lista ligada de memoria donde se revisan las páginas.
     * @param queue Cola de procesos existentes.
     * @param index  Indice con el que se compara. Todas las páginas con indice mayor a este incrementan en 1 el indexVirtual
     */
    private void modIndexAum(List<Memory> lis,ColaProcesos queue, int index){
        Nodo n;
        Proceso p;
        Pagina pg,pgb;
        int i,j,ind;
        for(i=0;i<queue.size;i++){
            n=queue.dequeue();
            p=n.proc;
            j=p.numPaginas-1;
            while(j>=0&&p.tabla.get(j).indexVirtual>index){
                ind=p.tabla.get(j).indexVirtual+1;
                p.tabla.get(j).indexVirtual+=1;
                lis.get(ind).proc.indexVirtual=ind;
                j--;
            }
            n.proc=p;
            queue.encolar(n);
            
        }
    }
}
