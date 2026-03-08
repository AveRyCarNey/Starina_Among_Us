package starina_among_us.red;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase encargada de inicializar y gestionar el servidor central del juego.
 * Mantiene un puerto de escucha para aceptar las conexiones entrantes de los clientes
 * y le asigna a cada uno un hilo de ejecucion independiente (HiloCliente).
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class Servidor {

    private static final int PUERTO = 12345;
    private static CopyOnWriteArrayList<HiloCliente> clientes = new CopyOnWriteArrayList<>();
    
    // Contador global para asignar un id unico a cada jugador conectado
    private static int contadorIds = 1;
    
    public static String mapaActual = "Uni";

    /**
     * Metodo principal que arranca el socket del servidor.
     * @param args Argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        System.out.println("--- SERVIDOR STARINA AMONG US ---");
        System.out.println("Esperando tripulantes...");

        try {
            ServerSocket servidor = new ServerSocket(PUERTO);

            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Conexion entrante aceptada.");

                // Control de aforo maximo (10 jugadores)
                if (clientes.size() >= 10) {
                    java.io.DataOutputStream out = new java.io.DataOutputStream(socket.getOutputStream());
                    out.writeUTF("LLENA");
                    out.flush();
                    socket.close();
                    System.out.println("Conexion rechazada: La sala ya tiene 10 jugadores.");
                    continue; // Aborta la creacion del hilo y sigue escuchando
                }

                boolean esImpostor = false;
                // Asignacion de ID secuencial
                int idAsignado = contadorIds++;
                
                // Creacion de hilo dedicado para el nuevo cliente
                HiloCliente hilo = new HiloCliente(socket, clientes, idAsignado, esImpostor);
                clientes.add(hilo);
                hilo.start();
            }
        } catch (java.net.BindException e) {
            System.err.println("ERROR: El puerto " + PUERTO + " ya esta en uso.");
        } catch (Exception e) {
            System.err.println("Error critico en el servidor: " + e.getMessage());
        }
    }
}