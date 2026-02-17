package starina_among_us.red;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {

    private static final int PUERTO = 12345;
    private static CopyOnWriteArrayList<HiloCliente> clientes = new CopyOnWriteArrayList<>();
    
    // CONTADOR DE JUGADORES (Para dar IDs únicos: 1, 2, 3...)
    private static int contadorIds = 1;

    public static void main(String[] args) {
        System.out.println("--- SERVIDOR  STARINA AMONG US ---");
        System.out.println("Esperando tripulantes...");

        try {
            ServerSocket servidor = new ServerSocket(PUERTO);

            while (true) {
                Socket socket = servidor.accept();
                System.out.println("¡Conexión entrante!");

                // 1. SORTEO: ¿Será impostor? (20% de probabilidad)
                // Math.random() da un número entre 0.0 y 1.0
                //boolean esImpostor = Math.random() < 0.2; 
                boolean esImpostor = Math.random() < 0.3;
                // 2. ASIGNAR ID y ROL
                int idAsignado = contadorIds++;
                
                // 3. Crear el hilo y pasarle estos datos
                HiloCliente hilo = new HiloCliente(socket, clientes, idAsignado, esImpostor);
                clientes.add(hilo);
                hilo.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}