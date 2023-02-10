/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica5;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author roger & emi
 */
public class main {
    public int pid=0;
    Proceso p;
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        Proceso p = new Proceso();
        ColaProcesos colaDeProcesos = new ColaProcesos();
        LinkedList<Memory> memoria = new LinkedList<>();
        List<String> eliminados = new ArrayList<>();
        List<String> finalizados = new ArrayList<>();
        Memory ax=new Memory();
        int eleccion;
        int contadorPid = 0;
        int aux;
        String nombre;
        //memoria.add(new Memory(2048,0));
        memoria.add(new Memory(1024,0)); //nuevo tamaño de memoria indicado por el enunciado
        do{
            System.out.println("SIMULADOR DE PROCESOS Y MEMORIA\n");
            System.out.println("1.Crear proceso nuevo");
            System.out.println("2.Ver estado de los procesos");
            System.out.println("3.Ver estado de la memoria");
            System.out.println("4.Imprimir cola de procesos");
            System.out.println("5.Ver proceso actual");
            System.out.println("6.Ejecutar proceso actual");
            System.out.println("7.Pasar al proceso siguiente");
            System.out.println("8.Matar proceso actual");
            System.out.println("9.Desfragmentar memoria");
            System.out.println("10.Salir del programa");
            System.out.print("\nIntroduce alguna opcion: ");
            eleccion = sc.nextInt();
            sc.nextLine();
            switch (eleccion) {
                case 1: //1.Crear proceso nuevo
                    System.out.print("Introduce el nombre del proceso: ");
                    nombre = sc.nextLine();
                    aux=p.crearProceso(memoria, nombre, contadorPid, colaDeProcesos);
                    if(aux==1)contadorPid++;
                    aux=0;            
                    break;
                case 2://2.Ver estado actual del sistema
                    p.verEstadoActual(memoria, colaDeProcesos, eliminados, finalizados);
                    break;
                case 3: //3. Ver estado de la memoria
                    ax.estado(memoria);
                    break;
                case 4://4.Imprimir cola de colaDeProcesos
                    p.imprimeProcesos(colaDeProcesos);
                    break;
                case 5://5.Ver proceso actual
                    p.imprimeProcesoActual(colaDeProcesos);
                    break;
                case 6://6.Ejecutar proceso actual
                    p.ejecutarProceso(memoria,colaDeProcesos,finalizados);
                    break;
                case 7://7.Pasar al proceso siguiente
                    p.nextProcess(colaDeProcesos);
                    break;
                case 8://8.Matar proceso actual
                    p.matarProceso(memoria, colaDeProcesos, eliminados);
                    break;
                case 9://9.Desfragmentar
                    ax.desfragmentar(memoria);
                    break;
                case 10: //10. Salir del sistema
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Entrada invalida. Intente alguna opcion.");
            }
            System.out.println("\n\nPresione enter para continuar");
            sc.nextLine();
            limpiarPantalla();
        }while(eleccion != 10);
    }
    
    public static void limpiarPantalla(){
        int i;
        for(i=0;i<30;i++){
            System.out.println();
        }
    }
}
