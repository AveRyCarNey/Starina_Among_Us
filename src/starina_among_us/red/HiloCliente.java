package starina_among_us.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Hilo de ejecucion en el servidor para manejar la comunicacion con un cliente especifico.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class HiloCliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private CopyOnWriteArrayList<HiloCliente> listaTodos;
    
    private int id;
    private boolean esImpostor;
    
    // Variables de estado del jugador replicadas en el servidor
    public int x = 50; 
    public int y = 50;
    public int r = 197, g = 17, b = 17; 
    public String nombre = "Tripulante";
    public boolean mirandoDerecha = true;
    public boolean moviendose = false;
    public boolean estaVivo = true;
    
    public HiloCliente(Socket socket, CopyOnWriteArrayList<HiloCliente> lista, int id, boolean impostor) {
        this.socket = socket;
        this.listaTodos = lista;
        this.id = id;
        this.esImpostor = impostor;
        
        // Asignacion de posicion inicial escalonada
        this.x = 100 + (id * 30);
        this.y = 200;
        
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    @Override
    public void run() {
        try {
            // Sincronizacion inicial del jugador recien conectado
            this.enviarMensaje("BIENVENIDO," + id + "," + esImpostor + "," + x + "," + y + "," + Servidor.mapaActual);

            // Informar al nuevo cliente sobre los jugadores que ya estaban en la sala
            for (HiloCliente otro : listaTodos) {
                if (otro.id != this.id) {
                    String msjSincro = "SINCRO," + otro.id + "," + 
                                       otro.x + "," + otro.y + "," + 
                                       otro.r + "," + otro.g + "," + otro.b + "," + 
                                       otro.nombre + "," + 
                                       otro.mirandoDerecha;
                    
                    this.enviarMensaje(msjSincro);
                    this.enviarMensaje("ROL," + otro.id + "," + otro.esImpostor);
                    
                    if (!otro.estaVivo) {
                        this.enviarMensaje("MUERTE," + otro.id);
                    }
                }
            }

            // Bucle principal de escucha de comandos
            while (true) {
                String mensaje = entrada.readUTF();
                String[] partes = mensaje.split(",");
                String comando = partes[0];
                
                if (comando.equals("HOLA")) {
                    this.nombre = partes[1];
                    if (partes.length > 4) {
                        this.r = Integer.parseInt(partes[2]);
                        this.g = Integer.parseInt(partes[3]);
                        this.b = Integer.parseInt(partes[4]);
                    }
                    if (this.id == 1 && partes.length > 5) {
                        Servidor.mapaActual = partes[5];
                        System.out.println("El Host ha establecido el mapa: " + Servidor.mapaActual);
                    }
                    
                    // Notificar a los demas sobre los datos del nuevo jugador
                    for (HiloCliente otro : listaTodos) {
                        if (otro.id != this.id) {
                            String presentacion = "SINCRO," + this.id + "," + this.x + "," + this.y + "," + 
                                                  this.r + "," + this.g + "," + this.b + "," + 
                                                  this.nombre + "," + this.mirandoDerecha;
                            otro.enviarMensaje(presentacion);
                            otro.enviarMensaje("ROL," + this.id + "," + this.esImpostor);
                        }
                    }
                }
                else if (comando.equals("MOV")) {
                    this.x = Integer.parseInt(partes[2]);
                    this.y = Integer.parseInt(partes[3]);
                    if (partes.length > 5) {
                        this.mirandoDerecha = Boolean.parseBoolean(partes[4]);
                        this.moviendose = Boolean.parseBoolean(partes[5]);
                    }
                    broadcast(mensaje, this);
                }
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
                else if (comando.equals("RESET_SERVER")) {
                    System.out.println("Reiniciando el servidor para nueva partida...");
                    listaTodos.clear(); 
                }
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

    public void enviarMensaje(String msg) {
        try { 
            if (salida != null) {
                salida.writeUTF(msg);
                salida.flush();
            } 
        } catch (Exception e) {}
    }
    
    private void broadcast(String msg, HiloCliente remitente) {
        for (HiloCliente c : listaTodos) {
            if (c != remitente) {
                c.enviarMensaje(msg);
            }
        }
    }
}