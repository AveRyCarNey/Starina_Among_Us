package starina_among_us.vista;

import java.awt.BasicStroke;
import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.awt.Toolkit;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import starina_among_us.modelo.HerramientasImagen;

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
    
    private JButton botonVent;
    private int idVentCercana = -1; // Cuál alcantarilla tengo cerca (0, 1 o 2)
    
    private JButton botonReport;
    private int idCuerpoCercano = -1;
    
    // Coordenadas [X, Y] de las 3 alcantarillas de Cafetería
    // 0: Izquierda Arriba, 1: Derecha Arriba, 2: Abajo Centro
    private final int[][] COORDENADAS_VENTS = {
        {180, 80},  
        {620, 80},  
        {400, 480}  
    };
    
    

    public PanelJuego() {
        this.setLayout(null);
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addFocusListener(this);

        // Inicializar la base de datos de jugadores vacía
        jugadoresConectados = new HashMap<>();

        // Cargar el mapa
        try {
            fondoMapa = new ImageIcon(getClass().getResource("/starina_among_us/recursos/mapas/Cafeteria.png")).getImage();
        } catch (Exception e) {
            System.out.println("Error cargando mapa");
        }
        
        // --- CARGAR HOJA DE SPRITES ---
        BufferedImage hojaBotones = null;
        try {
            hojaBotones = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/botones/gui_botones.png"));
        } catch (Exception e) {
            System.out.println("¡ALERTA! No se encontró gui_botones.png");
        }

        // --- INICIALIZAR BOTONES (Primero creamos, luego configuramos) ---
        
        // 1. BOTÓN KILL (Abajo Derecha)
        botonKill = new JButton();
        // Ajustamos el tamaño a 115x115 según tu recorte
        botonKill.setBounds(650, 430, 115, 115); 
        botonKill.setContentAreaFilled(false);
        botonKill.setBorderPainted(false);
        botonKill.setFocusPainted(false);
        botonKill.setVisible(false);
        botonKill.setEnabled(false);
        
        // 2. BOTÓN VENT (Arriba del Kill)
        botonVent = new JButton();
        // Lo subimos un poco (Y=310) para que no tape al Kill
        botonVent.setBounds(650, 310, 120, 110); 
        botonVent.setContentAreaFilled(false);
        botonVent.setBorderPainted(false);
        botonVent.setFocusPainted(false);
        botonVent.setVisible(false);
        botonVent.setEnabled(false);
        
        // 3. BOTÓN REPORT (A la izquierda del Kill)
        botonReport = new JButton();
        // Lo movemos a la izquierda (X=520) para que no choque con el Kill
        botonReport.setBounds(520, 433, 117, 112); 
        botonReport.setContentAreaFilled(false);
        botonReport.setBorderPainted(false);
        botonReport.setFocusPainted(false);
        botonReport.setVisible(false);
        botonReport.setEnabled(false);

        // --- APLICAR TUS RECORTES EXACTOS ---
        if (hojaBotones != null) {
            try {
                // TUS COORDENADAS:
                
                // KILL: 921, 116 | 115x115
                BufferedImage imgKill = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 921, 116, 115, 115);
                
                // VENT: 1243, 2 | 120x110
                BufferedImage imgVent = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 1243, 2, 120, 110);
                
                // REPORT: 589, 3 | 117x112
                BufferedImage imgReport = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 589, 3, 117, 112);
                
                // Transparencias (50%)
                BufferedImage killGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgKill, 0.5f);
                BufferedImage ventGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgVent, 0.5f);
                BufferedImage reportGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgReport, 0.5f);
                
                // Asignar Iconos
                botonKill.setIcon(new ImageIcon(imgKill));
                botonKill.setDisabledIcon(new ImageIcon(killGris));
                
                botonVent.setIcon(new ImageIcon(imgVent));
                botonVent.setDisabledIcon(new ImageIcon(ventGris));
                
                botonReport.setIcon(new ImageIcon(imgReport));
                botonReport.setDisabledIcon(new ImageIcon(reportGris));
                
            } catch (Exception e) {
                System.out.println("Error recortando botones: " + e.getMessage());
            }
        } else {
            // Respaldo de Texto
            botonKill.setText("MATAR"); botonKill.setContentAreaFilled(true); botonKill.setBackground(Color.RED);
            botonVent.setText("VENT"); botonVent.setContentAreaFilled(true); botonVent.setBackground(Color.GRAY);
            botonReport.setText("REPORT"); botonReport.setContentAreaFilled(true); botonReport.setBackground(Color.ORANGE);
        }

        // --- DEFINIR ACCIONES ---
        
        botonKill.addActionListener(e -> {
            if (idVictimaCercana != -1) {
                clienteRed.enviar("MATAR," + idVictimaCercana);
                botonKill.setEnabled(false);
                idVictimaCercana = -1;
                this.requestFocusInWindow(); 
            }
        });

        botonVent.addActionListener(e -> {
            if (idVentCercana != -1) {
                teletransportar(); 
                arriba = false; abajo = false; izquierda = false; derecha = false;
                this.requestFocusInWindow();
            }
        });
        
        botonReport.addActionListener(e -> {
            if (idCuerpoCercano != -1) {
                clienteRed.enviar("REPORT," + idCuerpoCercano);
                this.requestFocusInWindow();
            }
        });

        // --- AGREGAR AL PANEL ---
        this.add(botonKill);
        this.add(botonVent);
        this.add(botonReport);

        clienteRed = new ClienteRed(this);
        reloj = new Timer(15, this);
        reloj.start();
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
        
        // 3. Circulo de Muerte
        if (botonKill.isEnabled() && idVictimaCercana != -1) {
            if (jugadoresConectados.containsKey(idVictimaCercana)) {
                Jugador victima = jugadoresConectados.get(idVictimaCercana);
                
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3)); // Línea gruesa
                
                // Dibujar círculo alrededor de la víctima
                g2.drawOval((int)victima.getX() - 5, (int)victima.getY() - 5, 60, 60);
                
                // Dibujar línea conectando al asesino con la víctima
                Jugador yo = jugadoresConectados.get(miId);
                g2.drawLine((int)yo.getX()+25, (int)yo.getY()+25, (int)victima.getX()+25, (int)victima.getY()+25);
                
                g2.drawString(" KILL ID " + idVictimaCercana, (int)victima.getX(), (int)victima.getY() - 10);
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    // --- CICLO DEL JUEGO (Lo que hace el reloj cada 15ms) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // VALIDACIÓN DE SEGURIDAD
        if (!jugadoresConectados.containsKey(miId)) return;
        
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        // --- LOGICA DE MOVIMIENTO ---
        double dx = 0;
        double dy = 0;

        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;

        // CORRECCIÓN DE DIAGONAL (Normalización)
        // Si nos movemos en DOS ejes a la vez (diagonal), frenamos un poquito
        if (dx != 0 && dy != 0) {
            dx *= 0.7071; // Multiplicar por 1/raiz(2)
            dy *= 0.7071;
        }

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
            if (yo == null) return; 

            // Calculamos CENTRO
            double miCentroX = yo.getX() + 25; 
            double miCentroY = yo.getY() + 25;

            double distanciaMinima = 10000;
            Jugador victimaPotencial = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                
                // --- FILTRO 1: IDENTIDAD ---
                if (otro.getId() == miId) continue; // No soy yo
                
                // --- FILTRO 2: ESTADO ---
                if (!otro.isVivo()) continue;    // No muertos
                if (otro.esImpostor()) continue; // No amigos

                // --- CÁLCULO ---
                double otroCentroX = otro.getX() + 25;
                double otroCentroY = otro.getY() + 25;
                
                double distancia = Math.hypot(otroCentroX - miCentroX, otroCentroY - miCentroY);
                
                // --- FILTRO 3: DISTANCIA MÍNIMA (EL ESCUDO FÍSICO) ---
                // Si la distancia es MENOR a 10 pixeles, es imposible que sea otro jugador
                // (porque chocaríamos). Asumimos que es un error o soy yo mismo.
                if (distancia < 10) {
                    continue; // ¡Saltar! Demasiado cerca para ser real.
                }

                // Rango de ataque real (entre 10 y 60)
                if (distancia < 60) { 
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        victimaPotencial = otro;
                    }
                }
            }
            
            // --- GESTIÓN DEL BOTÓN ---
            if (victimaPotencial != null) {
                // Si encontramos una víctima NUEVA
                if (idVictimaCercana != victimaPotencial.getId()) {
                    idVictimaCercana = victimaPotencial.getId();
                    botonKill.setEnabled(true);
                    System.out.println("🎯 MIRA FIJADA EN: Jugador " + idVictimaCercana + " (Distancia: " + (int)distanciaMinima + ")");
                }
            } else {
                // Si no hay nadie o perdimos el objetivo
                if (botonKill.isEnabled()) {
                    botonKill.setEnabled(false);
                    idVictimaCercana = -1;
                    System.out.println("❌ Nadie cerca");
                }
            }
        }
        
        // --- RADAR DE VENTS (Solo si el botón es visible / Soy Impostor) ---
        if (botonVent.isVisible()) {
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            int ventEncontrada = -1;
            
            // Revisar las 3 alcantarillas
            for (int i = 0; i < COORDENADAS_VENTS.length; i++) {
                int ventX = COORDENADAS_VENTS[i][0];
                int ventY = COORDENADAS_VENTS[i][1];
                
                // Distancia (Pitágoras)
                double distancia = Math.hypot(ventX - yo.getX(), ventY - yo.getY());
                
                if (distancia < 80) { // Si estoy a menos de 80 pixeles
                    distanciaMinima = distancia;
                    ventEncontrada = i;
                }
            }
            
            // Activar o desactivar botón
            if (ventEncontrada != -1) {
                botonVent.setEnabled(true);
                idVentCercana = ventEncontrada;
            } else {
                botonVent.setEnabled(false);
                idVentCercana = -1;
            }
        }
        
        // --- RADAR DE REPORT (Busca cadáveres) ---
        // Regla: Solo puedo reportar si YO estoy vivo.
        if (jugadoresConectados.containsKey(miId) && jugadoresConectados.get(miId).isVivo()) {
            
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            Jugador cuerpoEncontrado = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                // Buscamos a alguien que NO sea yo y que esté MUERTO
                if (otro.getId() != miId && !otro.isVivo()) {
                    
                    double distancia = Math.hypot(otro.getX() - yo.getX(), otro.getY() - yo.getY());
                    
                    if (distancia < 60) { // Rango para reportar
                        distanciaMinima = distancia;
                        cuerpoEncontrado = otro;
                    }
                }
            }
            
            // Si encontramos un cuerpo, encendemos el botón
            if (cuerpoEncontrado != null) {
                botonReport.setVisible(true);
                botonReport.setEnabled(true);
                idCuerpoCercano = cuerpoEncontrado.getId();
            } else {
                botonReport.setVisible(false);
                botonReport.setEnabled(false);
                idCuerpoCercano = -1;
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
        
        if (tecla == KeyEvent.VK_C) {
            // Generamos color random
            int r = (int)(Math.random()*255);
            int g = (int)(Math.random()*255);
            int b = (int)(Math.random()*255);
            
            // 1. Enviamos al servidor: "COLOR,MiID,R,G,B"
            clienteRed.enviar("COLOR," + miId + "," + r + "," + g + "," + b);
            
            // 2. (Opcional) Lo cambiamos localmente de una vez para que sea instantáneo
            actualizarColorJugador(miId, r, g, b);
                repaint();
        
        }
        
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
        // Ignorar actualizaciones de mi propio jugador
        if (id == miId) return;

        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            double dx = x - j.getX(); 
            double dy = y - j.getY();
            
            // Calculamos la distancia real (Pitágoras)
            double distancia = Math.hypot(dx, dy);

            // --- CASO 1: TELETRANSPORTE (VENT) ---
            // Si la distancia es enorme (más de 100 px), es un salto de vent.
            // Lo movemos de golpe y FORZAMOS QUE ESTÉ QUIETO.
            if (distancia > 100) {
                j.setX(x);
                j.setY(y);
                j.detener(); // <--- Apaga la animación.
            }
            
            // --- CASO 2: ESTÁ PARADO (Corrección de lag) ---
            // Si la distancia es muy pequeña, lo pegamos a la coordenada y frenamos.
            else if (distancia < 5) {
                j.detener();
                j.setX(x); 
                j.setY(y);
            } 
            
            // --- CASO 3: CAMINANDO NORMAL ---
            // Si es una distancia media, usamos la interpolación suave.
            else {
                j.mover(dx/5.0, dy/5.0); 
            }
            
        } else {
            // Registrar nuevo jugador si no existía
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
            botonVent.setVisible(true);
        }
    }
    
    public void reportarMuerte(int idMuerto) {
        System.out.println("--- INTENTANDO MATAR A ID: " + idMuerto + " ---");
        boolean encontrado = false;

        // BÚSQUEDA MANUAL: Revisamos todos los muñecos uno por uno
        for (Jugador j : jugadoresConectados.values()) {
            if (j.getId() == idMuerto) {
                // ¡LO ENCONTRAMOS!
                j.setVivo(false); 
                j.detener(); // Detenerlo para que no sea un cadáver que camina
                
                System.out.println("¡MUERTE CONFIRMADA! Jugador " + idMuerto + " ahora es un cadáver.");
                encontrado = true;
                break; // Ya lo matamos, dejamos de buscar
            }
        }

        if (!encontrado) {
            System.out.println("ERROR: Se ordenó matar al ID " + idMuerto + " pero no lo encuentro en mi lista.");
            System.out.println("Jugadores visibles: " + jugadoresConectados.keySet());
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
    // Método que llama ClienteRed cuando llega un aviso de color
    public void actualizarColorJugador(int id, int r, int g, int b) {
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            Color nuevoColor = new Color(r, g, b);
            
            j.cambiarSkin(nuevoColor); // ¡Esto pintará las 12 imágenes de la animación!
            repaint();
        }
    }
    
    private void teletransportar() {
        if (!jugadoresConectados.containsKey(miId)) return;
        
        // 1. Calcular la SIGUIENTE alcantarilla (Circuito 0 -> 1 -> 2 -> 0)
        int siguienteVent = idVentCercana + 1;
        if (siguienteVent >= COORDENADAS_VENTS.length) {
            siguienteVent = 0; // Volver al inicio
        }
        
        // 2. Obtener coordenadas destino
        int nuevaX = COORDENADAS_VENTS[siguienteVent][0];
        int nuevaY = COORDENADAS_VENTS[siguienteVent][1];
        
        // 3. Mover a mi jugador LOCALMENTE
        Jugador yo = jugadoresConectados.get(miId);
        yo.setX(nuevaX);
        yo.setY(nuevaY);
        
        // 4. AVISAR AL SERVIDOR (Para que los demás me vean aparecer allá)
        // Enviamos un mensaje "MOV" normal, pero con las coordenadas nuevas de golpe
        String mensaje = "MOV," + miId + "," + nuevaX + "," + nuevaY;
        clienteRed.enviar(mensaje);
        
        System.out.println("Saltando de Vent " + idVentCercana + " a Vent " + siguienteVent);
    }
    
    public void actualizarRolJugador(int id, boolean esImpostor) {
        // Si el jugador ya existe, le actualizamos el rol
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            j.setImpostor(esImpostor);
            
            if (esImpostor && jugadoresConectados.get(miId).esImpostor()) {
                 System.out.println("¡Detectado aliado Impostor ID " + id + "!");
            }
        } else {
            // Si el mensaje ROL llega antes que el MOV (raro, pero posible),
            // creamos un muñeco temporal para guardar el dato.
            Jugador nuevo = new Jugador(id, "Jugador " + id, 0, 0, esImpostor);
            jugadoresConectados.put(id, nuevo);
        }
    }
    public void iniciarReunion(int idReportador) {
        System.out.println("🚨 REUNIÓN DE EMERGENCIA INICIADA 🚨");
        
        // 1. MOSTRAR MENSAJE (Bloqueante, pausa el juego un momento)
        javax.swing.JOptionPane.showMessageDialog(this, 
            "¡Cuerpo encontrado! \nLlamado por Jugador " + idReportador + "\nTodos a la Cafetería.");
        
        // 2. TELETRANSPORTAR A LA MESA (Cafetería)
        // Coordenadas aprox de la mesa central: X=350, Y=250
        if (jugadoresConectados.containsKey(miId)) {
            Jugador yo = jugadoresConectados.get(miId);
            
            // Un pequeño random para que no aparezcan todos apilados en el pixel exacto
            int randomX = 350 + (int)(Math.random() * 50); 
            int randomY = 250 + (int)(Math.random() * 50);
            
            yo.setX(randomX);
            yo.setY(randomY);
            yo.detener(); // Que aparezca quieto
            
            // 3. ENVIAR MI NUEVA POSICIÓN AL SERVIDOR
            // Es vital avisar que me moví, si no los demás me verán donde estaba antes.
            clienteRed.enviar("MOV," + miId + "," + randomX + "," + randomY);
        }
        
        // 4. LIMPIEZA (Opcional)
        // Aquí podrías resetear botones, enfriamientos, etc.
        botonKill.setEnabled(false);
        botonReport.setVisible(false);
        
        // Recuperar el foco para volver a movernos
        arriba = false; abajo = false; izquierda = false; derecha = false;
        this.requestFocusInWindow();
    }
    
    
    
}