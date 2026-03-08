package starina_among_us.red;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import starina_among_us.modelo.GestorLenguaje;
import starina_among_us.modelo.Jugador;
import starina_among_us.modelo.excepciones.ErrorRedException;
import starina_among_us.vista.PanelJuego;

/**
 * Gestiona la conexion de red desde la perspectiva del jugador (Cliente).
 * Se encarga de enviar las acciones locales al servidor y de escuchar los 
 * eventos externos para actualizar la interfaz del juego en tiempo real.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class ClienteRed extends Thread {

    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private PanelJuego panel; 
    private String ip;

    /**
     * Constructor que inicia la conexion al servidor remoto usando una IP.
     * * @param panel Referencia al PanelJuego principal para actualizar su interfaz.
     * @param ip Direccion IP del servidor al que conectarse.
     * @throws ErrorRedException Si el servidor rechaza la conexion o esta apagado.
     */
    public ClienteRed(PanelJuego panel, String ip) throws ErrorRedException {
        this.panel = panel;
        this.ip = ip;
        try {
            socket = new Socket(ip, 12345);
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
            this.start();
        } catch (java.net.ConnectException e) {
            throw new ErrorRedException("Conexion rechazada. Esta el servidor encendido?");
        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    /**
     * Envia una cadena de texto plana al servidor, que representa una accion del jugador.
     * * @param mensaje Accion codificada (ej. "MOV,1,400,300").
     */
    public void enviar(String mensaje) {
        if (salida != null) {
            try {
                salida.writeUTF(mensaje);
            } catch (Exception e) {
                System.out.println("Error al enviar: " + e.getMessage());
            }
        }
    }

    /**
     * Ciclo continuo de escucha que procesa los paquetes recibidos desde el servidor
     * y los traduce en actualizaciones graficas o logicas sobre el PanelJuego.
     */
    @Override
    public void run() {
        try {
            System.out.println("ClienteRed: Hilo iniciado y escuchando al servidor...");
            
            while (true) {
                String mensajeServidor = entrada.readUTF();
                System.out.println("RECIBIDO: " + mensajeServidor);
                
                try { 
                    String[] partes = mensajeServidor.split(",");
                    String comando = partes[0];
                    
                    // Bloque para manejar el rechazo por sala llena
                    if (comando.equals("LLENA")) {
                        SwingUtilities.invokeLater(() -> {
                            String textoError = GestorLenguaje.get("msg_sala_llena");
                            JOptionPane.showMessageDialog(panel, textoError, "Aviso", JOptionPane.ERROR_MESSAGE);
                            System.exit(0); 
                        });
                        return; // Termina el hilo
                    }

                    else if (comando.equals("BIENVENIDO")) {
                        int miId = Integer.parseInt(partes[1]);
                        boolean soyImpostor = Boolean.parseBoolean(partes[2]);
                        double x = Double.parseDouble(partes[3]);
                        double y = Double.parseDouble(partes[4]);
                        
                        String mapaDelServidor = partes[5]; 
                        String miMapaSeleccionado = panel.getMapaElegido();
                        
                        if (miId > 1 && !miMapaSeleccionado.equalsIgnoreCase(mapaDelServidor)) {
                            SwingUtilities.invokeLater(() -> {
                                String textoError = GestorLenguaje.get("msg_mapa_error") + " " + mapaDelServidor;
                                String tituloError = GestorLenguaje.get("msg_mapa_titulo");
                                JOptionPane.showMessageDialog(panel, textoError, tituloError, JOptionPane.ERROR_MESSAGE);
                                System.exit(0); 
                            });
                            return;
                        }
                        
                        panel.inicializarJugadorLocal(miId, soyImpostor, x, y);
                        
                        String miNombre = panel.getMiNombreElegido();
                        Color miColor = panel.getMiColorElegido();
                        int r = (miColor != null) ? miColor.getRed() : 197;
                        int g = (miColor != null) ? miColor.getGreen() : 17;
                        int b = (miColor != null) ? miColor.getBlue() : 17;
                        
                        enviar("HOLA," + miNombre + "," + r + "," + g + "," + b + "," + miMapaSeleccionado); 
                    }
                    else if (comando.equals("START_REVEAL")) { panel.iniciarAnimacionReveal(); }
                    else if (comando.equals("MOV")) {
                        int id = Integer.parseInt(partes[1]);
                        Jugador j = panel.getJugador(id);
                        if (j != null) {
                            j.setX(Integer.parseInt(partes[2]));
                            j.setY(Integer.parseInt(partes[3]));
                            if (partes.length > 5) {
                                j.setMirandoDerecha(Boolean.parseBoolean(partes[4]));
                                j.setMoviendose(Boolean.parseBoolean(partes[5]));
                            }
                        }
                    }
                    else if (comando.equals("SINCRO")) {
                        int id = Integer.parseInt(partes[1]);
                        Jugador j = panel.getJugador(id);
                        if (j == null) {
                            panel.agregarJugador(id, partes[7], Integer.parseInt(partes[2]), Integer.parseInt(partes[3]), 
                                                 Integer.parseInt(partes[4]), Integer.parseInt(partes[5]), Integer.parseInt(partes[6]));
                        } else {
                            j.setColorRGB(Integer.parseInt(partes[4]), Integer.parseInt(partes[5]), Integer.parseInt(partes[6]));
                        }
                    }
                    else if (comando.equals("VENT")) {
                        int idJugador = Integer.parseInt(partes[1]);
                        Jugador j = panel.getJugador(idJugador);
                        if (j != null) { j.setEnVentilacion(Boolean.parseBoolean(partes[2])); panel.repaint(); }
                    }
                    else if (comando.equals("VENT_ANIM")) {
                        int idJugador = Integer.parseInt(partes[1]);
                        Jugador j = panel.getJugador(idJugador);
                        if (j != null) { j.setAnimandoVent(Boolean.parseBoolean(partes[2])); panel.repaint(); }
                    }
                    else if (comando.equals("SALIO")) { panel.eliminarJugador(Integer.parseInt(partes[1])); }
                    else if (comando.equals("COLOR")) { panel.actualizarColorJugador(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]), Integer.parseInt(partes[3]), Integer.parseInt(partes[4])); }
                    else if (comando.equals("MUERTE") || comando.equals("MATAR")) { panel.reportarMuerte(Integer.parseInt(partes[1])); }
                    else if (comando.equals("SABOTAJE_VISION")) { panel.activarSabotajeVision(); }
                    else if (comando.equals("ROL")) { panel.actualizarRolJugador(Integer.parseInt(partes[1]), Boolean.parseBoolean(partes[2])); }
                    else if (comando.equals("REUNION")) { panel.iniciarReunion(Integer.parseInt(partes[1]), false); }
                    else if (comando.equals("EMERGENCIA_RED")) { panel.iniciarReunion(Integer.parseInt(partes[1]), true); }
                    else if (comando.equals("CHAT")) {
                        String emisor = partes[1];
                        int indexInicioMsj = mensajeServidor.indexOf(emisor) + emisor.length() + 1;
                        panel.recibirMensajeChat(emisor, mensajeServidor.substring(indexInicioMsj));
                    }
                    else if (comando.equals("START_GAME")) { panel.iniciarPartidaLobby(); }
                    else if (comando.equals("VOTO")) { panel.registrarVotoRed(Integer.parseInt(partes[1]), Integer.parseInt(partes[2])); }
                    else if (comando.equals("TAREA_LISTA")) {
                        if (partes.length > 1) panel.getGestorTareas().registrarTareaCompletada(partes[1]);
                        panel.verificarFinDeJuego();
                        panel.repaint();
                    }
                    else if (comando.equals("VICTORIA_TAREAS")) { panel.forzarVictoriaTareas(); }
                    else if (comando.equals("COLOR_RAVE")) {
                        panel.aplicarColorRave(Integer.parseInt(partes[1]), new Color(Integer.parseInt(partes[2]), Integer.parseInt(partes[3]), Integer.parseInt(partes[4])));
                    }
                    
                } catch (Exception ex) {
                    System.out.println("ERROR INTERNO LEYENDO MENSAJE: " + mensajeServidor);
                }
            }
        } catch (java.net.SocketException | java.io.EOFException e) {
            System.out.println("Desconectado del servidor.");
        } catch (Exception e) {
            System.out.println("ERROR FATAL DE RED: " + e.getMessage());
        }
    }
    
    /**
     * Cierra todas las conexiones de red de manera controlada y segura
     * para evitar excepciones de puertos colgados (SocketException).
     */
    public void desconectar() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Desconectado del servidor limpiamente.");
        } catch (Exception e) {
            System.out.println("Error al desconectar: " + e.getMessage());
        }
    }
}