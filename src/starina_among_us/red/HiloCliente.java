package starina_among_us.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HiloCliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;  // Oído (Escuchar al jugador)
    private DataOutputStream salida; // Boca (Hablarle al jugador)
    private ArrayList<HiloCliente> listaTodos; // Referencia a todos los demás
    
    // DATOS DE ESTE JUGADOR
    private int id;
    private boolean esImpostor;

    public HiloCliente(Socket socket, ArrayList<HiloCliente> lista, int id, boolean impostor) {
        this.socket = socket;
        this.listaTodos = lista;
        this.id = id;               // Guardamos el ID que nos dio el servidor
        this.esImpostor = impostor; // Guardamos si es impostor o no
        
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
            // --- PASO CLAVE: EL SALUDO INICIAL ---
            // Apenas entra, le decimos: "BIENVENIDO, TU_ID, TU_ROL, TU_X, TU_Y"
            // Le damos una posición aleatoria para que no salgan todos montados
            int x = 100 + (id * 30); 
            int y = 100;
            
            // Enviamos mensaje privado al jugador nuevo
            this.enviarMensaje("BIENVENIDO," + id + "," + esImpostor + "," + x + "," + y);

            // BUCLE NORMAL (Escuchar movimientos)
            while (true) {
                String mensaje = entrada.readUTF();
                
                // Reenviar a todos los demas
                for (HiloCliente cliente : listaTodos) {
                    cliente.enviarMensaje(mensaje);
                }
            }
        } catch (Exception e) {
            // Si se desconecta, deberíamos avisar (pendiente para después)
        }
    }
    // Método para enviarle cosas a ESTE jugador
    public void enviarMensaje(String msg) {
        try { salida.writeUTF(msg); } catch (Exception e) {}
    }

   
    
}