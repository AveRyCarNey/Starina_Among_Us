package starina_among_us.vista;

import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import starina_among_us.red.ClienteRed;

public class PanelJuego extends JPanel implements KeyListener, ActionListener {

    private Jugador miJugador;
    // Motor del juego: Controla los FPS y el ciclo de actualización
    private Timer reloj;
    // Referencia a la conexión de red para enviar/recibir datos
    private ClienteRed clienteRed;
    
    // Almacén principal de datos: Diccionario que vincula un ID (Entero) con un Jugador (Objeto)
    // Nos permite buscar rápido a cualquier jugador por su número.
    private HashMap<Integer, Jugador> jugadoresConectados;
    
    // Identidad del usuario local: Define cuál de todos los muñecos soy "yo" para controlarlo.
    private int miId; 
    
    // Recursos gráficos del escenario
    private Image fondoMapa;
    
    // Banderas para saber qué teclas están presionadas
    private boolean arriba, abajo, izquierda, derecha;

    public PanelJuego() {
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);

        // 1. Inicializar la base de datos de jugadores vacía
        jugadoresConectados = new HashMap<>();

        // 2. Definir mi identidad temporal (El servidor debería asignar esto en el futuro)
        miId = 1; 

        // 3. Crear mi personaje local y guardarlo en la lista
        // (ID, Nombre, X, Y, esImpostor)
        Jugador yo = new Jugador(miId, "Starina", 100.0, 100.0, false);
        jugadoresConectados.put(miId, yo);

        // 4. Cargar el mapa desde los recursos
        try {
            fondoMapa = new ImageIcon(getClass().getResource("/starina_among_us/recursos/mapas/Cafeteria.png")).getImage();
        } catch (Exception e) {
            System.out.println("Error cargando mapa");
        }

        // 5. Iniciar la conexión con el servidor
        clienteRed = new ClienteRed(this);
        clienteRed.enviar("HOLA"); // Protocolo de saludo inicial

        // 6. Arrancar el ciclo de juego (aprox. 60 FPS)
        reloj = new Timer(15, this);
        reloj.start();
        clienteRed = new ClienteRed(this); // Iniciar conexión
clienteRed.enviar("HOLA, SOY NUEVO"); // Saludar al entrar
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 1. DIBUJAR MAPA
        if (fondoMapa != null) {
            g.drawImage(fondoMapa, 0, 0, getWidth(), getHeight(), this);
        }
        
        // 2. DIBUJAR JUGADORES
        
        // --- ERROR AQUÍ ---
        // Seguramente tienes esta línea escrita:
        // miJugador.dibujar(g, this);  <-- ¡BORRA ESTA LÍNEA!
        // Esa variable está vacía (null) y por eso explota.
        
        // --- LO CORRECTO ---
        // Debemos dibujar solo a los que están en la lista (que te incluye a ti)
        for (Jugador j : jugadoresConectados.values()) {
            j.dibujar(g, this);
        }
    }

    // --- CICLO DEL JUEGO (Lo que hace el reloj cada 15ms) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        if (miMuñeco != null) {
            double dx = 0, dy = 0;
            if (izquierda) dx = -0.7;
            if (derecha)   dx = -0.7; // OJO: Corregir si copiaste mal, derecha es 1
            if (derecha)   dx = 0.7;
            if (arriba)    dy = -0.7;
            if (abajo)     dy = 0.7;

            // Si hay movimiento...
            if (dx != 0 || dy != 0) {
                // 1. Me muevo yo en mi pantalla (para que sea instantáneo)
                miMuñeco.mover(dx, dy);
                
                // 2. LE GRITO AL MUNDO DÓNDE ESTOY
                // Formato: "MOV,MiID,MiX,MiY"
                String mensaje = "MOV," + miId + "," + miMuñeco.getX() + "," + miMuñeco.getY();
                clienteRed.enviar(mensaje);
            } else {
                miMuñeco.detener();
            }
        }
        repaint();
    }

    // --- TECLADO (Solo enciende/apaga interruptores) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = true;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = true;
        if (tecla == KeyEvent.VK_UP)    arriba = true;
        if (tecla == KeyEvent.VK_DOWN)  abajo = true;
        
        if (tecla == KeyEvent.VK_SPACE) miJugador.setVivo(!miJugador.isVivo());
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = false;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = false;
        if (tecla == KeyEvent.VK_UP)    arriba = false;
        if (tecla == KeyEvent.VK_DOWN)  abajo = false;
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    
    
    
   // Sincronizar estado de jugadores remotos basado en datos del servidor
    public void actualizarJugadorRemoto(int id, double x, double y) {
        // Ignorar actualizaciones de mi propio jugador (predicción local)
        if (id == miId) return;

        if (jugadoresConectados.containsKey(id)) {
            // Actualizar posición existente interpolando el movimiento
            Jugador j = jugadoresConectados.get(id);
            double dx = x - j.getX(); 
            double dy = y - j.getY();
            
            // Aplicar movimiento visual
            j.mover(dx/5.0, dy/5.0); 
            
            // Forzar sincronización exacta si la desincronización es muy grande
            // (Opcional: j.setX(x); j.setY(y); si tuvieras los setters)
        } else {
            // Registrar nuevo jugador en la sesión actual
            // NOTA: Aquí pasamos 'id' primero para cumplir con el constructor
            Jugador nuevo = new Jugador(id, "Jugador " + id, x, y, false);
            jugadoresConectados.put(id, nuevo);
        }
        repaint();
    }
       // Este se ejecuta cuando llega el mensaje "BIENVENIDO" del servidor
    public void inicializarJugadorLocal(int id, boolean esImpostor, double x, double y) {
        this.miId = id;
        
        String nombre = "Jugador " + id;
        if (esImpostor) nombre += " (IMPOSTOR)"; // Para que sepas qué eres
        
        Jugador yo = new Jugador(id, nombre, x, y, esImpostor);
        
        // Si soy impostor, me pinto de otro color (solo para mis ojos, por ahora)
        // Ojo: Necesitarías un setter para el color en Jugador.java, o déjalo así.
        
        jugadoresConectados.put(miId, yo);
        System.out.println("¡Soy el ID " + id + "! Rol: " + (esImpostor ? "IMPOSTOR" : "TRIPULANTE"));
    }
    
    
        
}