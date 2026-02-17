package starina_among_us.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class HiloCliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private CopyOnWriteArrayList<HiloCliente> listaTodos;
    
    private int id;
    private boolean esImpostor;
    
    // --- MEMORIA DEL ESTADO DEL JUGADOR (Públicas para acceso rápido) ---
    public int x = 50; 
    public int y = 50;
    public int r = 197, g = 17, b = 17; // Color inicial (Rojo)
    public String nombre = "Tripulante";
    public boolean mirandoDerecha = true;
    public boolean moviendose = false;
    public boolean estaVivo = true;
    
    public HiloCliente(Socket socket, CopyOnWriteArrayList<HiloCliente> lista, int id, boolean impostor) {
        this.socket = socket;
        this.listaTodos = lista;
        this.id = id;
        this.esImpostor = impostor;
        
        // Posición inicial por defecto (para que no aparezcan todos encimados al principio)
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
            // ==========================================
            // 1. FASE INICIAL: Sincronizar al NUEVO con lo que ya existe
            // ==========================================
            
            // A) Saludo inicial (Tu ID, tu posición, etc.)
            this.enviarMensaje("BIENVENIDO," + id + "," + esImpostor + "," + x + "," + y);

            // B) Le contamos al NUEVO sobre los jugadores VIEJOS
for (HiloCliente otro : listaTodos) {
    if (otro.id != this.id) {
        // Enviamos R, G, B por separado en el protocolo SINCRO
        String msjSincro = "SINCRO," + otro.id + "," + 
                           otro.x + "," + otro.y + "," + 
                           otro.r + "," + otro.g + "," + otro.b + "," + 
                           otro.nombre + "," + 
                           otro.mirandoDerecha;
        
        this.enviarMensaje(msjSincro);
        
        if (!otro.estaVivo) {
            this.enviarMensaje("MUERTE," + otro.id);
        }
    }
}

            // ==========================================
            // 2. BUCLE DEL JUEGO (Escuchar mensajes)
            // ==========================================
            while (true) {
                String mensaje = entrada.readUTF();
                String[] partes = mensaje.split(",");
                String comando = partes[0];
                
                // --- COMANDO HOLA (Cuando el cliente se presenta) ---
                // Comando HOLA: HOLA, Nombre, R, G, B
if (comando.equals("HOLA")) {
    this.nombre = partes[1];
    if (partes.length > 4) {
        this.r = Integer.parseInt(partes[2]);
        this.g = Integer.parseInt(partes[3]);
        this.b = Integer.parseInt(partes[4]);
    }
    // Presentamos al nuevo enviando SINCRO con sus 3 colores
    for (HiloCliente otro : listaTodos) {
        if (otro.id != this.id) {
            String presentacion = "SINCRO," + this.id + "," + this.x + "," + this.y + "," + 
                                  this.r + "," + this.g + "," + this.b + "," + 
                                  this.nombre + "," + this.mirandoDerecha;
            otro.enviarMensaje(presentacion);
        }
    }
}
                
                // --- COMANDO MOVIMIENTO ---
                else if (comando.equals("MOV")) {
    this.x = Integer.parseInt(partes[2]);
    this.y = Integer.parseInt(partes[3]);
    // Verificación de seguridad para mensajes cortos
    if (partes.length > 5) {
        this.mirandoDerecha = Boolean.parseBoolean(partes[4]);
        this.moviendose = Boolean.parseBoolean(partes[5]);
    }
    broadcast(mensaje, this);
}

                // --- OTROS COMANDOS ---
                else if (comando.equals("COLOR")) {
    this.r = Integer.parseInt(partes[2]);
    this.g = Integer.parseInt(partes[3]);
    this.b = Integer.parseInt(partes[4]);
    broadcast(mensaje, this);
}
                
                else if (comando.equals("MATAR")) {
                    int idVictima = Integer.parseInt(partes[1]);
                    for (HiloCliente c : listaTodos) {
                        if (c.id == idVictima) c.estaVivo = false;
                    }
                    broadcast("MUERTE," + idVictima, null);
                }
                
                else if (comando.equals("REPORT")) {
                    broadcast("REUNION," + this.id, null);
                }
                
                // Reenvío general para cualquier otro mensaje
                else {
                    broadcast(mensaje, this);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Jugador " + id + " desconectado.");
            listaTodos.remove(this);
            broadcast("SALIO," + this.id, this);
        }
    }

    // Método auxiliar para enviar mensaje a este cliente
    public void enviarMensaje(String msg) {
        try { 
            if (salida != null) {
                salida.writeUTF(msg);
                salida.flush();
            } 
        } catch (Exception e) {}
    }
    
    // Método auxiliar para enviar a TODOS (menos al remitente opcional)
    private void broadcast(String msg, HiloCliente remitente) {
        for (HiloCliente c : listaTodos) {
            if (c != remitente) {
                c.enviarMensaje(msg);
            }
        }
    }
}