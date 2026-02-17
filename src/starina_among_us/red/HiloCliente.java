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
    
    private boolean estaVivo = true;
    
    private int r = 255, g = 0, b = 0;

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

            // --- ACTUALIZACIÓN DE ESTADO ---
            // Le contamos al nuevo dónde están los jugadores VIEJOS
            for (HiloCliente otro : listaTodos) {
                if (otro.id != this.id) {
                    // Le decimos dónde está
                    this.enviarMensaje("MOV," + otro.id + "," + otro.x + "," + otro.y);
                    // Le decimos su color
                    this.enviarMensaje("COLOR," + otro.id + "," + otro.r + "," + otro.g + "," + otro.b);
                    
                    // NUEVO: Le decimos SU ROL (Para que sepa si es aliado o víctima)
                    this.enviarMensaje("ROL," + otro.id + "," + otro.esImpostor);
                    
                    if (!otro.estaVivo) {
                        this.enviarMensaje("MUERTE," + otro.id);
                    }
                    
                    // --- AVISAMOS A LOS OTROS QUE LLEGUÉ YO ---
                    otro.enviarMensaje("MOV," + this.id + "," + this.x + "," + this.y);
                    // Avisamos mi rol a los demás
                    otro.enviarMensaje("ROL," + this.id + "," + this.esImpostor);
                }
            }

            // 2. BUCLE: Escuchar mensajes
            while (true) {
                String mensaje = entrada.readUTF();
                
                // NUEVO: Si llega un cambio de color
                if (mensaje.startsWith("COLOR")) {
                    String[] partes = mensaje.split(",");
                    // Guardamos el color en el servidor
                    this.r = Integer.parseInt(partes[2]);
                    this.g = Integer.parseInt(partes[3]);
                    this.b = Integer.parseInt(partes[4]);
                }
                
                // Si es movimiento (guardamos posición)
                else if (mensaje.startsWith("MOV")) {
                    String[] partes = mensaje.split(",");
                    this.x = Double.parseDouble(partes[2]);
                    this.y = Double.parseDouble(partes[3]);
                }
                // COMANDO MATAR: El Impostor dice "Maté a X"
                else if (mensaje.startsWith("MATAR")) {
                    String[] partes = mensaje.split(",");
                    int idVictima = Integer.parseInt(partes[1]);
                    System.out.println("SERVIDOR: MATARON A " + idVictima);
                    
                    // A) Buscamos a la víctima en la lista del servidor y la marcamos como muerta
                    for (HiloCliente cliente : listaTodos) {
                        if (cliente.id == idVictima) {
                            cliente.estaVivo = false; // <--- ¡AQUÍ GUARDAMOS EL DATO!
                        }
                        // B) Avisamos a todos (Broadcast)
                        cliente.enviarMensaje("MUERTE," + idVictima);
                    }
                }
                // COMANDO REPORT: Alguien encontró un cuerpo
                else if (mensaje.startsWith("REPORT")) {
                    // Mensaje entrante: "REPORT,ID_MUERTO"
                    System.out.println("SERVIDOR: ¡REUNIÓN DE EMERGENCIA LLAMADA POR " + this.id + "!");
                    
                    // Avisamos a TODOS que hay reunión
                    // El protocolo será: "REUNION,ID_DEL_QUE_REPORTO"
                    for (HiloCliente cliente : listaTodos) {
                        cliente.enviarMensaje("REUNION," + this.id);
                    }
                }

                // REENVIAR A TODOS (Broadcast)
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