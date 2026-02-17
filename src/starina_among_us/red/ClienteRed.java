package starina_among_us.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import starina_among_us.modelo.Jugador;
import starina_among_us.vista.PanelJuego;

public class ClienteRed extends Thread {

    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private PanelJuego panel; // Referencia para poder mover los muñecos

    public ClienteRed(PanelJuego panel) {
        this.panel = panel;
        try {
            // "localhost" significa "mi propia computadora". 
            // Cuando jueguen online, aquí pondrán la IP del compañero que sea servidor.
            socket = new Socket("localhost", 12345);
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
            
            // Iniciamos el hilo para escuchar siempre
            this.start();
            
        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    // El juego usará esto para decir "ME MOVÍ"
    public void enviar(String mensaje) {
        if (salida != null) {
            try {
                salida.writeUTF(mensaje);
            } catch (Exception e) {
                // Si falla al enviar, asumimos que se cayó el server
                System.out.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }

    // Aquí escuchamos lo que dice el servidor 
    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = entrada.readUTF();
                String[] partes = mensaje.split(",");
                String comando = partes[0];

                if (comando.equals("BIENVENIDO")) {
                    int miId = Integer.parseInt(partes[1]);
                    boolean soyImpostor = Boolean.parseBoolean(partes[2]);
                    double x = Double.parseDouble(partes[3]);
                    double y = Double.parseDouble(partes[4]);
                    panel.inicializarJugadorLocal(miId, soyImpostor, x, y);
                    enviar("HOLA,Jugador " + miId + ",197,17,17"); 
                }
               else if (comando.equals("MOV")) {
                    try {
                        // 1. Parsear los datos básicos
                        int id = Integer.parseInt(partes[1]);
                        int x = Integer.parseInt(partes[2]);
                        int y = Integer.parseInt(partes[3]);
                        Jugador j = panel.getJugador(id);
                        
                        // 2. Parsear los datos de ANIMACIÓN (¡Nuevos!)
                        // Usamos Boolean.parseBoolean para convertir el texto "true"/"false"
                        boolean mirandoDerecha = Boolean.parseBoolean(partes[4]);
                        boolean moviendose = Boolean.parseBoolean(partes[5]); 

                        

                        if (j != null) {
                        j.setX(Integer.parseInt(partes[2]));
                        j.setY(Integer.parseInt(partes[3]));
                        // Solo leemos si el mensaje está completo
                        if (partes.length > 5) {
                            j.setMirandoDerecha(Boolean.parseBoolean(partes[4]));
                            j.setMoviendose(Boolean.parseBoolean(partes[5]));
                        }
                    }
                    } catch (Exception e) {
                        System.out.println("⚠️ Error al procesar movimiento: " + mensaje);
                    }
                }
                else if (comando.equals("MATAR")) {
                    // Mensaje: "MATAR,ID_VICTIMA"
                    int idMuerto = Integer.parseInt(partes[1]);
                    
                    // Avisamos al panel para que actualice el sprite
                    panel.reportarMuerte(idMuerto);
                }
                else if (comando.equals("SALIO")) {
                    int idQueSeFue = Integer.parseInt(partes[1]);
                    panel.eliminarJugador(idQueSeFue);
                }
                else if (comando.equals("COLOR")) {
                    // Protocolo: COLOR, ID, R, G, B
                    int idJugador = Integer.parseInt(partes[1]);
                    int r = Integer.parseInt(partes[2]);
                    int g = Integer.parseInt(partes[3]);
                    int b = Integer.parseInt(partes[4]);
                    
                    // Avisamos al panel para que pinte al muñeco
                    panel.actualizarColorJugador(idJugador, r, g, b);
                }
                else if (comando.equals("MUERTE")) {
                    System.out.println("CLIENTE: ALGUIEN HA MUERTO, ACTUALIZANDO...");
                    int idMuerto = Integer.parseInt(partes[1]);
                    // Avisamos al panel
                    panel.reportarMuerte(idMuerto);
                }
                else if (comando.equals("ROL")) {
                    // Protocolo: ROL, ID, ES_IMPOSTOR (true/false)
                    int id = Integer.parseInt(partes[1]);
                    boolean esImpostor = Boolean.parseBoolean(partes[2]);
                    
                    panel.actualizarRolJugador(id, esImpostor);
                }
                else if (comando.equals("REUNION")) {
                    int idReportador = Integer.parseInt(partes[1]);
                    panel.iniciarReunion(idReportador);
                }
                else if (comando.equals("SINCRO")) {
                    int id = Integer.parseInt(partes[1]);
                    int x = Integer.parseInt(partes[2]);
                    int y = Integer.parseInt(partes[3]);
                    int r = Integer.parseInt(partes[4]);
                    int g = Integer.parseInt(partes[5]);
                    int b = Integer.parseInt(partes[6]);
                    String nombre = partes[7];
                    
                    Jugador j = panel.getJugador(id);
                    if (j == null) {
                        panel.agregarJugador(id, nombre, x, y, r, g, b);
                        j = panel.getJugador(id);
                    }
                    if (j != null) j.setColorRGB(r, g, b);
                }
                
            }
        } catch (Exception e) {
            System.out.println("Desconectado");
        }
    }
}