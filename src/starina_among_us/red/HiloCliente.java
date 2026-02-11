package starina_among_us.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HiloCliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private ArrayList<HiloCliente> listaTodos;
    
    private int id;
    private boolean esImpostor;
    
    // El servidor necesita recordar dónde está este jugador
    private double x, y;

    public HiloCliente(Socket socket, ArrayList<HiloCliente> lista, int id, boolean impostor) {
        this.socket = socket;
        this.listaTodos = lista;
        this.id = id;
        this.esImpostor = impostor;
        // Posición inicial por defecto
        this.x = 100 + (id * 30);
        this.y = 200;
        
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void run() {
        try {
            // 1. BIENVENIDA: Le decimos al nuevo quién es
            this.enviarMensaje("BIENVENIDO," + id + "," + esImpostor + "," + x + "," + y);

            // --- NUEVO: ACTUALIZACIÓN DE ESTADO ---
            // Le contamos al nuevo dónde están los jugadores VIEJOS
            for (HiloCliente otro : listaTodos) {
                if (otro.id != this.id) {
                    // "Oye nuevo, el jugador 'otro' está en tal posición"
                    this.enviarMensaje("MOV," + otro.id + "," + otro.x + "," + otro.y);
                    
                    // Y de paso, le avisamos al viejo que llegó uno nuevo
                    otro.enviarMensaje("MOV," + this.id + "," + this.x + "," + this.y);
                }
            }

            // 2. BUCLE: Escuchar mensajes
            while (true) {
                String mensaje = entrada.readUTF();
                
                // SI ES MOVIMIENTO, ACTUALIZAMOS LAS COORDENADAS EN EL SERVIDOR
                if (mensaje.startsWith("MOV")) {
                    String[] partes = mensaje.split(",");
                    // Guardamos la última posición conocida en el servidor
                    this.x = Double.parseDouble(partes[2]);
                    this.y = Double.parseDouble(partes[3]);
                }

                // Reenviar a todos (Broadcast)
                for (HiloCliente cliente : listaTodos) {
                    cliente.enviarMensaje(mensaje);
                    
                }
                
            }
        } catch (Exception e) {
            System.out.println("Jugador " + id + " desconectado.");
            
            // 1. Lo borramos de la lista del servidor
            listaTodos.remove(this);
            
            // 2. Avisamos a los sobrevivientes para que borren el dibujo
            for (HiloCliente cliente : listaTodos) {
                cliente.enviarMensaje("SALIO," + this.id);
            }
        }
    }

    public void enviarMensaje(String msg) {
        try { if (salida != null) salida.writeUTF(msg); } catch (Exception e) {}
    }
}