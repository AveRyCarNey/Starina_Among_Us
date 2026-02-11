package starina_among_us.vista;

import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import starina_among_us.red.ClienteRed;

public class PanelJuego extends JPanel implements KeyListener, ActionListener, FocusListener {

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
    
    // Para detectar cuando frenamos
    private boolean estabaMoviendose = false; 
    
    private JButton botonKill;
    private int idVictimaCercana = -1; // Guardaremos aquí a quién vamos a matar

    public PanelJuego() {
        this.setLayout(null);
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addFocusListener(this);

        // Inicializar la base de datos de jugadores vacía
        jugadoresConectados = new HashMap<>();


        // Cargar el mapa desde los recursos
        try {
            fondoMapa = new ImageIcon(getClass().getResource("/starina_among_us/recursos/mapas/Cafeteria.png")).getImage();
        } catch (Exception e) {
            System.out.println("Error cargando mapa");
        }

        // Iniciar la conexión con el servidor
        clienteRed = new ClienteRed(this);

        // Arrancar el ciclo de juego (aprox. 60 FPS)
        reloj = new Timer(15, this);
        reloj.start();
        
        
        // --- CONFIGURACIÓN DEL BOTÓN KILL ---
        botonKill = new JButton("MATAR");
        botonKill.setBounds(650, 450, 100, 50); // Posición (esquina inferior derecha)
        botonKill.setBackground(Color.RED);
        botonKill.setForeground(Color.WHITE);
        botonKill.setVisible(false); // Empieza invisible (hasta que sepamos si somos impostor)
        botonKill.setEnabled(false); // Empieza desactivado (hasta que haya alguien cerca)
        
        // ACCIÓN AL HACER CLIC
        botonKill.addActionListener(e -> {
            if (idVictimaCercana != -1) {
                clienteRed.enviar("MATAR," + idVictimaCercana);
                botonKill.setEnabled(false);
                
                // --- ARREGLO DEL BUG DE CAMINAR ---
                // 1. Apagamos todas las teclas por seguridad
                arriba = false; abajo = false; izquierda = false; derecha = false;
                
                // 2. Le decimos al Panel: "¡Mírame a mí otra vez!" (Recuperar el foco)
                this.requestFocusInWindow(); 
            }
        });
         this.add(botonKill); // Añadir el botón a la pantalla
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 1. DIBUJAR MAPA
        if (fondoMapa != null) {
            g.drawImage(fondoMapa, 0, 0, getWidth(), getHeight(), this);
        }
        
        // 2. DIBUJAR JUGADORES
         
        // Debemos dibujar solo a los que están en la lista (que te incluye a ti)
        for (Jugador j : jugadoresConectados.values()) {
            j.dibujar(g, this);
        }
    }

    // --- CICLO DEL JUEGO (Lo que hace el reloj cada 15ms) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // VALIDACIÓN DE SEGURIDAD
        if (!jugadoresConectados.containsKey(miId)) return;
        
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        // --- LOGICA DE MOVIMIENTO ---
        int dx = 0, dy = 0;
        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;

        // ¿Me estoy moviendo ahora mismo?
        boolean meMuevoAhora = (dx != 0 || dy != 0);

        if (meMuevoAhora) {
            // A) SI ME MUEVO:
            miMuñeco.mover(dx, dy); // Mover localmente
            
            // Avisar al servidor (usamos (int) para enviar coordenadas limpias)
            String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY();
            clienteRed.enviar(mensaje);
            
            estabaMoviendose = true; // Marcar que me estaba moviendo
            
        } else {
            // B) SI NO TOCO TECLAS:
            miMuñeco.detener(); // Detener animación local
            
            // C) ¡EL FRENO DE MANO! (IMPORTANTE)
            // Si en el frame anterior me movía, y ahora NO, significa que ACABO de frenar.
            // Tengo que enviar un ÚLTIMO mensaje con mi posición final exacta.
            if (estabaMoviendose) {
                String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY();
                clienteRed.enviar(mensaje);
                estabaMoviendose = false; // Ya avisé, apago la bandera
            }
        }
        
        
        // --- NUEVO: RADAR DE ASESINO ---
        // Solo si soy impostor y el botón está visible
        if (botonKill.isVisible()) {
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            Jugador victimaPotencial = null;
            
            // Buscar la víctima más cercana
            for (Jugador otro : jugadoresConectados.values()) {
                // Reglas: No puedo matarme a mí mismo, ni a otros impostores (opcional), ni a muertos
                if (otro.getId() != miId && otro.isVivo()) {
                    
                    // Fórmula de distancia (Pitágoras)
                    double distancia = Math.hypot(otro.getX() - yo.getX(), otro.getY() - yo.getY());
                    
                    if (distancia < 60) { // 60 píxeles de rango
                        distanciaMinima = distancia;
                        victimaPotencial = otro;
                    }
                }
            }
            
            // Si encontramos a alguien cerca...
            if (victimaPotencial != null) {
                botonKill.setEnabled(true);
                idVictimaCercana = victimaPotencial.getId(); // Guardamos su ID para el clic
            } else {
                botonKill.setEnabled(false);
                idVictimaCercana = -1;
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
            // Si la distancia es muy pequeña (menos de 0.5 píxeles), lo forzamos a estar QUIETO.
            if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
                j.detener();          // Corta el GIF
                j.setX(x); j.setY(y); // Lo teletransporta al píxel exacto para corregir errores
            } else {
                // Si la distancia es grande, lo movemos suavemente
                j.mover(dx/5.0, dy/5.0); 
            }
            
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
        
        //Si se es impostor, aparecera el boton
        if (esImpostor) {
            botonKill.setVisible(true);
        }
    }
    
    public void reportarMuerte(int idMuerto) {
        if (jugadoresConectados.containsKey(idMuerto)) {
            Jugador j = jugadoresConectados.get(idMuerto);
            j.setVivo(false); // Esto cambiará su imagen a "muerto" automáticamente
            
            System.out.println("El jugador " + idMuerto + " ha sido asesinado.");
            
            // Si yo soy el que murió, podría bloquear mi movimiento o mostrar un mensaje "GAME OVER"
            if (idMuerto == miId) {
                // Opcional: decirle al usuario que murió
            }
        }
        repaint();
    }
        
    
    public void eliminarJugador(int id) {
    if (jugadoresConectados.containsKey(id)) {
        jugadoresConectados.remove(id);
        repaint(); // Redibujar para que desaparezca
    }
}
    
    
    @Override
    public void focusGained(FocusEvent e) {
        // Cuando vuelves a hacer clic en la ventana. 
        // No hace falta hacer nada especial aquí.
    }

    @Override
    public void focusLost(FocusEvent e) {
        // ¡EMERGENCIA! El usuario hizo clic en otra ventana.
        // Soltamos todas las teclas virtualmente para que no se quede pegado.
        derecha = false;
        izquierda = false;
        arriba = false;
        abajo = false;
        
        // Forzamos al muñeco a detenerse
        if (jugadoresConectados.containsKey(miId)) {
            Jugador miMuñeco = jugadoresConectados.get(miId);
            miMuñeco.detener();
            
            // Enviamos un último mensaje al servidor diciendo "ME DETUVE"
            // para que los demás no me vean caminando infinito.
            String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY();
            clienteRed.enviar(mensaje);
        }
        
        repaint();
    }
}