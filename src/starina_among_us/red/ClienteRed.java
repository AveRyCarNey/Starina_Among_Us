package starina_among_us.red;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import starina_among_us.modelo.Jugador;
import starina_among_us.vista.PanelJuego;

public class ClienteRed extends Thread {

    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private PanelJuego panel; 
    private String ip;

    public ClienteRed(PanelJuego panel, String ip) {
        this.panel = panel;
        try {
            socket = new Socket(ip, 12345);
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
            this.start();
        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    public void enviar(String mensaje) {
        if (salida != null) {
            try {
                salida.writeUTF(mensaje);
            } catch (Exception e) {}
        }
    }

    @Override
    public void run() {
        try {
            // SI ESTE MENSAJE NO SALE EN CONSOLA, NETBEANS NO COMPILÓ
            System.out.println("📡 ClienteRed: Hilo iniciado y escuchando al servidor...");
            
            while (true) {
                String mensaje = entrada.readUTF();
                System.out.println("📥 RECIBIDO: " + mensaje);
                
                try { 
                    String[] partes = mensaje.split(",");
                    String comando = partes[0];

                    if (comando.equals("BIENVENIDO")) {
                        int miId = Integer.parseInt(partes[1]);
                        boolean soyImpostor = Boolean.parseBoolean(partes[2]);
                        double x = Double.parseDouble(partes[3]);
                        double y = Double.parseDouble(partes[4]);
                        
                        panel.inicializarJugadorLocal(miId, soyImpostor, x, y);
                        
                        // --- LA MAGIA DEL COLOR Y NOMBRE EN RED ---
                        String miNombre = panel.getMiNombreElegido();
                        Color miColor = panel.getMiColorElegido();
                        
                        // Si por algún motivo el color es null, usamos rojo por defecto
                        int r = (miColor != null) ? miColor.getRed() : 197;
                        int g = (miColor != null) ? miColor.getGreen() : 17;
                        int b = (miColor != null) ? miColor.getBlue() : 17;
                        
                        // Le enviamos al servidor nuestro verdadero nombre y color!
                        enviar("HOLA," + miNombre + "," + r + "," + g + "," + b); 
                    }
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
                    else if (comando.equals("MATAR")) {
                        panel.reportarMuerte(Integer.parseInt(partes[1]));
                    }
                    else if (comando.equals("VENT")) {
                        // Alguien entró o salió de una alcantarilla
                        int idJugador = Integer.parseInt(partes[1]);
                        boolean estaOculto = Boolean.parseBoolean(partes[2]);
                        
                        Jugador j = panel.getJugador(idJugador);
                        if (j != null) {
                            j.setEnVentilacion(estaOculto);
                            panel.repaint();
                        }
                    }
                    else if (comando.equals("VENT_ANIM")) {
                        // Alguien empezó o terminó su animación de bajar/subir
                        int idJugador = Integer.parseInt(partes[1]);
                        boolean estaAnimando = Boolean.parseBoolean(partes[2]);
                        
                        Jugador j = panel.getJugador(idJugador);
                        if (j != null) {
                            j.setAnimandoVent(estaAnimando);
                            panel.repaint();
                        }
                    }
                    else if (comando.equals("SALIO")) {
                        panel.eliminarJugador(Integer.parseInt(partes[1]));
                    }
                    else if (comando.equals("COLOR")) {
                        panel.actualizarColorJugador(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]), Integer.parseInt(partes[3]), Integer.parseInt(partes[4]));
                    }
                    else if (comando.equals("MUERTE")) {
                        panel.reportarMuerte(Integer.parseInt(partes[1]));
                    }
                    else if (comando.equals("ROL")) {
                        panel.actualizarRolJugador(Integer.parseInt(partes[1]), Boolean.parseBoolean(partes[2]));
                    }
                    else if (comando.equals("REUNION")) {
                        panel.iniciarReunion(Integer.parseInt(partes[1]));
                    }
                    
                    else if (comando.equals("CHAT")) {
                        // El mensaje puede contener comas (ej. "Hola, ¿qué tal?").
                        // Así que reconstruimos el mensaje completo a partir del nombre.
                        String emisor = partes[1];
                        int indexInicioMsj = mensaje.indexOf(emisor) + emisor.length() + 1;
                        String msjCompleto = mensaje.substring(indexInicioMsj);
                        
                        panel.recibirMensajeChat(emisor, msjCompleto);
                    }
                    else if (comando.equals("START_GAME")) {
                        panel.iniciarPartidaLobby();
                    }
                    
                    else if (comando.equals("VOTO")) {
                        // El servidor nos avisa que alguien votó y POR QUIÉN votó
                        int idVotante = Integer.parseInt(partes[1]);
                        int idVotado = Integer.parseInt(partes[2]); // Leemos la tercera parte del mensaje
                        
                        panel.registrarVotoRed(idVotante, idVotado);
                    }
                    else if (comando.equals("TAREA_LISTA")) {
                        // Verificamos que nos hayan enviado el ID de la tarea
                        if (partes.length > 1) {
                            String idTareaRecibida = partes[1];
                            panel.getGestorTareas().registrarTareaCompletada(idTareaRecibida);
                        } 
                        panel.verificarFinDeJuego();
                        
                        panel.repaint();
                    }
                    else if (comando.equals("COLOR_RAVE")) {
                        int idRave = Integer.parseInt(partes[1]);
                        int r = Integer.parseInt(partes[2]);
                        int g = Integer.parseInt(partes[3]);
                        int b = Integer.parseInt(partes[4]);
                        
                        panel.aplicarColorRave(idRave, new java.awt.Color(r, g, b));
                    }
                    
                    
                } catch (Exception ex) {
                    System.out.println("❌ ERROR INTERNO LEYENDO MENSAJE: " + mensaje);
                    ex.printStackTrace();
                }
            }
        } catch (java.net.SocketException | java.io.EOFException e) {
            // Este catch atrapa los cierres de ventana y desconexiones normales
            System.out.println("🔌 Desconectado del servidor (Juego cerrado).");
        } catch (Exception e) {
            // Este catch atrapa errores graves reales
            System.out.println("💥 ERROR FATAL DE RED. Razón:");
            e.printStackTrace(); 
        }
    }
    
    
    public void desconectar() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("🔌 Desconectado del servidor limpiamente.");
        } catch (Exception e) {
            System.out.println("⚠️ Error al desconectar: " + e.getMessage());
        }
    }
}