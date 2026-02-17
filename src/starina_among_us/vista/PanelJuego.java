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
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import starina_among_us.red.ClienteRed;
import java.awt.Toolkit;

import java.awt.image.BufferedImage;

public class PanelJuego extends JPanel implements KeyListener, ActionListener, FocusListener {

    private Jugador miJugador;
    // Motor del juego: Controla los FPS y el ciclo de actualización
    private Timer reloj;
    // Referencia a la conexión de red para enviar/recibir datos
    private ClienteRed clienteRed;
    
    // Almacén principal de datos: Diccionario que vincula un ID (Entero) con un Jugador (Objeto)
    // Nos permite buscar rápido a cualquier jugador por su número.
    private ConcurrentHashMap<Integer, Jugador> jugadoresConectados;
    
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
    
    // --- VARIABLES DE ESPECTADOR ---
    private int idEspectando = -1; // A quién está mirando la cámara actualmente
    private boolean modoEspectador = false; // ¿Estoy muerto y espectando?
    
    // IMÁGENES DE REPORTE (Separadas)
    private java.awt.image.BufferedImage imgReporteFondo; // El efecto
    private java.awt.image.BufferedImage imgReporteTexto; // Las letras
    
    private boolean mostrandoAnimacionReporte = false;
    
    

    public PanelJuego() {
        this.setLayout(null);
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addFocusListener(this);

        // Inicializar la base de datos de jugadores vacía
        jugadoresConectados = new ConcurrentHashMap<>();

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
        
        
        
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // SOLO FUNCIONA SI ESTOY MUERTO
                if (modoEspectador) {
                    // Click Izquierdo (Botón 1) -> Anterior
                    // Click Derecho (Botón 3) -> Siguiente
                    boolean avanzar = (e.getButton() != java.awt.event.MouseEvent.BUTTON1);
                    cambiarObjetivoEspectador(avanzar);
                }
            }
        });
        
        
        // --- CARGAR ASSETS DE EVENTOS (REPORTE) ---
        imgReporteFondo = null; // Aseguramos que empiecen vacías
        imgReporteTexto = null;

        try {
            System.out.println("Intentando cargar: /starina_among_us/recursos/eventos/assets_events.png");
            
            // 1. Cargar la hoja
            java.net.URL urlImagen = getClass().getResource("/starina_among_us/recursos/eventos/assets_events.png");
            
            if (urlImagen == null) {
                System.out.println("❌ ERROR FATAL: La URL de la imagen es NULL. Verifica la carpeta 'eventos' y el nombre 'asstes_events.png'");
            } else {
                java.awt.image.BufferedImage hojaEventos = javax.imageio.ImageIO.read(urlImagen);
                System.out.println("✅ Imagen cargada correctamente. Tamaño: " + hojaEventos.getWidth() + "x" + hojaEventos.getHeight());

                // 2. RECORTAR FONDO (Effect) -> 1, 1 | 950 x 435
                imgReporteFondo = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 1, 940, 435);
                
                // 3. RECORTAR TEXTO (Dead Body) -> 1, 437 | 421 x 369
                imgReporteTexto = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 437, 420, 205);
                
                System.out.println("✂️ Recortes realizados con éxito.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ EXCEPCIÓN AL CARGAR IMAGEN: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 1. DIBUJAR MAPA
        if (fondoMapa != null) {
            g.drawImage(fondoMapa, 0, 0, getWidth(), getHeight(), this);
        }
        
        // --- INTERFAZ DE ESPECTADOR ---
        if (modoEspectador && idEspectando != -1) {
            if (jugadoresConectados.containsKey(idEspectando)) {
                Jugador objetivo = jugadoresConectados.get(idEspectando);
                
                Graphics2D g2 = (Graphics2D) g;
                
                // 1. DIBUJAR MARCO ALREDEDOR DEL JUGADOR OBSERVADO
                g2.setColor(Color.CYAN);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect((int)objetivo.getX() - 5, (int)objetivo.getY() - 5, 60, 70);
                
                // 2. DIBUJAR TEXTO EN PANTALLA (HUD)
                g2.setColor(Color.BLACK);
                g2.fillRect(250, 10, 300, 40); // Fondo negro arriba
                g2.setColor(Color.WHITE);
                g2.drawRect(250, 10, 300, 40); // Borde blanco
                
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
                g2.drawString("ESPECTANDO A: " + objetivo.getNombre(), 280, 35);
                
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
                g2.drawString("[CLICK IZQ / CLICK DER] PARA CAMBIAR", 300, 45);
            }
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
        
        
        // --- ANIMACIÓN DE REPORTE ---
        if (mostrandoAnimacionReporte) {
            
            // 1. Oscurecer pantalla
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;

            // --- PLAN A: DIBUJAR IMÁGENES ---
            if (imgReporteFondo != null && imgReporteTexto != null) {
                
                // Fondo
                int anchoFondo = 800; 
                int altoFondo = 350;
                g.drawImage(imgReporteFondo, centroX - (anchoFondo / 2), centroY - (altoFondo / 2), anchoFondo, altoFondo, this);
                
                // Texto
                int anchoTexto = 300;
                int altoTexto = 260;
                g.drawImage(imgReporteTexto, centroX - (anchoTexto / 2), centroY - (altoTexto / 2), anchoTexto, altoTexto, this);
            
            } 
            // --- PLAN B: DIBUJAR CUADRO DE ERROR (Si falló la imagen) ---
            else {
                g.setColor(Color.RED);
                g.fillRect(centroX - 200, centroY - 50, 400, 100);
                
                g.setColor(Color.WHITE);
                g.drawRect(centroX - 200, centroY - 50, 400, 100);
                
                g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
                g.drawString("ERROR CARGANDO IMAGEN", centroX - 130, centroY + 10);
                g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
                g.drawString("Revisa la consola (Output) para detalles", centroX - 110, centroY + 30);
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
        
        // --- 1. LÓGICA DE MOVIMIENTO (Calculada por el Panel) ---
        
        // Definimos la velocidad (Píxeles por frame)
        // IMPORTANTE: Si no ponemos velocidad, se moverá a 1 píxel por hora.
        double velocidad = 4.0; 
        
        // Si hay animación de reporte o estoy muerto, no me muevo
        if (mostrandoAnimacionReporte || (modoEspectador && !miMuñeco.isVivo())) {
            velocidad = 0;
        }

        double dx = 0;
        double dy = 0;

        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;

        // CORRECCIÓN DE DIAGONAL (Normalización)
        if (dx != 0 && dy != 0) {
            dx *= 0.7071; 
            dy *= 0.7071;
        }
        
        // APLICAR VELOCIDAD
        dx *= velocidad;
        dy *= velocidad;

        // ¿Me estoy moviendo ahora mismo?
        boolean meMuevoAhora = (dx != 0 || dy != 0);

        if (meMuevoAhora) {
            // A) SI ME MUEVO: Actualizamos las coordenadas AQUÍ DIRECTAMENTE
            miMuñeco.setX(miMuñeco.getX() + dx);
            miMuñeco.setY(miMuñeco.getY() + dy);
            miMuñeco.setMoviendose(true);
            
            // Dirección para el sprite
            if (dx > 0) miMuñeco.setMirandoDerecha(true);
            if (dx < 0) miMuñeco.setMirandoDerecha(false);
            
            // Avisar al servidor
            String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY() 
                             + "," + miMuñeco.isMirandoDerecha() + "," + miMuñeco.isMoviendose();
            clienteRed.enviar(mensaje);
            
            estabaMoviendose = true; 
            
        } else {
            // B) SI NO TOCO TECLAS:
            miMuñeco.setMoviendose(false);
            
            // C) ¡EL FRENO DE MANO!
            if (estabaMoviendose) {
                // Enviamos posición final y avisamos que paramos (false)
                String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY()
                                 + "," + miMuñeco.isMirandoDerecha() + ",false";
                clienteRed.enviar(mensaje);
                estabaMoviendose = false; 
            }
        }
        
        
        // ==========================================================
        //     DE AQUÍ PARA ABAJO ES TU CÓDIGO DE RADARES EXACTO
        //           (NO HE TOCADO NADA DE ESTA PARTE)
        // ==========================================================
        
        // --- NUEVO: RADAR DE ASESINO ---
        if (botonKill.isVisible()) { 
            Jugador yo = jugadoresConectados.get(miId);
            if (yo == null) return; 

            double miCentroX = yo.getX() + 25; 
            double miCentroY = yo.getY() + 25;

            double distanciaMinima = 10000;
            Jugador victimaPotencial = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                if (otro.getId() == miId) continue; 
                if (!otro.isVivo()) continue;    
                if (otro.esImpostor()) continue; 

                double otroCentroX = otro.getX() + 25;
                double otroCentroY = otro.getY() + 25;
                
                double distancia = Math.hypot(otroCentroX - miCentroX, otroCentroY - miCentroY);
                
                if (distancia < 10) continue; 

                if (distancia < 60) { 
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        victimaPotencial = otro;
                    }
                }
            }
            
            if (victimaPotencial != null) {
                if (idVictimaCercana != victimaPotencial.getId()) {
                    idVictimaCercana = victimaPotencial.getId();
                    botonKill.setEnabled(true);
                    System.out.println("🎯 MIRA FIJADA EN: Jugador " + idVictimaCercana);
                }
            } else {
                if (botonKill.isEnabled()) {
                    botonKill.setEnabled(false);
                    idVictimaCercana = -1;
                    System.out.println("❌ Nadie cerca");
                }
            }
        }
        
        // --- RADAR DE VENTS ---
        if (botonVent.isVisible()) {
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            int ventEncontrada = -1;
            
            for (int i = 0; i < COORDENADAS_VENTS.length; i++) {
                int ventX = COORDENADAS_VENTS[i][0];
                int ventY = COORDENADAS_VENTS[i][1];
                
                double distancia = Math.hypot(ventX - yo.getX(), ventY - yo.getY());
                
                if (distancia < 80) { 
                    distanciaMinima = distancia;
                    ventEncontrada = i;
                }
            }
            
            if (ventEncontrada != -1) {
                botonVent.setEnabled(true);
                idVentCercana = ventEncontrada;
            } else {
                botonVent.setEnabled(false);
                idVentCercana = -1;
            }
        }
        
        // --- RADAR DE REPORT ---
        if (jugadoresConectados.containsKey(miId) && jugadoresConectados.get(miId).isVivo()) {
            
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            Jugador cuerpoEncontrado = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                // CONDICIÓN: Muerto y NO reportado (Fantasma)
                if (otro.getId() != miId && !otro.isVivo() && !otro.isCuerpoReportado()) {

                double distancia = Math.hypot(otro.getX() - yo.getX(), otro.getY() - yo.getY());

                if (distancia < 60) { 
                    distanciaMinima = distancia;
                    cuerpoEncontrado = otro;
                }
            }
            }
            
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
        // --- ACTUALIZAR ANIMACIONES DE TODOS LOS JUGADORES ---
        // Esto es vital para ver mover los pies a los demas (y a ti mismo)
        for (Jugador j : jugadoresConectados.values()) {
            j.actualizarAnimacion();
        }
        repaint();
    }

    // --- TECLADO (Solo enciende/apaga interruptores) ---
    @Override
    public void keyPressed(KeyEvent e) {
        
        // SI ESTOY MUERTO, NO PUEDO MOVERME
        if (modoEspectador) return; 

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
                // Calculamos el pasito suave
                double pasoX = dx / 5.0;
                double pasoY = dy / 5.0;

                // Aplicamos la nueva posición directamente
                j.setX(j.getX() + pasoX);
                j.setY(j.getY() + pasoY);
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
    String nombre = "Jugador " + id + (esImpostor ? " (IMPOSTOR)" : "");
    
    // CORRECCIÓN: Asigna el objeto a la variable de la clase
    this.miJugador = new Jugador(id, nombre, x, y, esImpostor);
    
    jugadoresConectados.put(miId, this.miJugador);
        
        
        
        
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
        
        if (idMuerto == miId) {
            System.out.println("💀 ME HAN MATADO. ACTIVANDO MODO ESPECTADOR.");
            modoEspectador = true;
            idEspectando = -1; // Reset para que busque uno nuevo al hacer click o forzarlo
            cambiarObjetivoEspectador(true); // Auto-asignar el primero que encuentre
            
            // Apagar botones de interacción física
            botonKill.setVisible(false);
            botonVent.setVisible(false);
            botonReport.setVisible(false);
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

    // CORRECCIÓN EN focusLost
@Override
public void focusLost(FocusEvent e) {
    derecha = izquierda = arriba = abajo = false;
    if (jugadoresConectados.containsKey(miId)) {
        Jugador miMuñeco = jugadoresConectados.get(miId);
        miMuñeco.detener();
        // Agregamos las partes faltantes (mirandoDerecha y moviendose=false)
        String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY() 
                         + "," + miMuñeco.isMirandoDerecha() + ",false";
        clienteRed.enviar(mensaje);
    }
    repaint();
}

// CORRECCIÓN EN teletransportar
private void teletransportar() {
    if (!jugadoresConectados.containsKey(miId)) return;
    int siguienteVent = (idVentCercana + 1) % COORDENADAS_VENTS.length;
    int nuevaX = COORDENADAS_VENTS[siguienteVent][0];
    int nuevaY = COORDENADAS_VENTS[siguienteVent][1];
    
    Jugador yo = jugadoresConectados.get(miId);
    yo.setX(nuevaX);
    yo.setY(nuevaY);
    
    // Agregamos las partes faltantes para evitar que el servidor crashée
    String mensaje = "MOV," + miId + "," + nuevaX + "," + nuevaY + "," + yo.isMirandoDerecha() + ",false";
    clienteRed.enviar(mensaje);
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
        System.out.println("🚨 ANIMACIÓN DE REPORTE INICIADA 🚨");
        
        // 1. ACTIVAR ANIMACIÓN VISUAL
        mostrandoAnimacionReporte = true;
        
        // 2. APAGAR CONTROLES (Para que nadie se mueva durante la alerta)
        botonKill.setEnabled(false);
        botonReport.setVisible(false);
        botonReport.setEnabled(false);
        
        // Forzamos que se dibuje la pantalla roja INMEDIATAMENTE
        repaint();
        
        // --- AQUÍ ESTÁ LA CLAVE: USAMOS UN TIMER, NO UN JOPTIONPANE ---
        // Esto espera 3000 milisegundos (3 segundos) sin congelar la ventana
        Timer timerAnimacion = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // --- ESTO OCURRE DESPUÉS DE LOS 3 SEGUNDOS ---
                
                // 1. Quitar la pantalla roja
                mostrandoAnimacionReporte = false;
                
                // 2. Limpiar cadáveres (Convertirlos en fantasmas transparentes)
                for (Jugador j : jugadoresConectados.values()) {
                    if (!j.isVivo()) {
                        j.setCuerpoReportado(true);
                    }
                }
                
                // 3. Teletransportar A LA MESA (Solo si estoy vivo)
                if (jugadoresConectados.containsKey(miId)) {
                    Jugador yo = jugadoresConectados.get(miId);
                    
                    if (yo.isVivo()) {
                        // Coordenadas mesa
                        int randomX = 350 + (int)(Math.random() * 60); 
                        int randomY = 250 + (int)(Math.random() * 40);
                        
                        yo.setX(randomX);
                        yo.setY(randomY);
                        yo.detener();
                        
                        // Avisar al servidor
                        clienteRed.enviar("MOV," + miId + "," + randomX + "," + randomY);
                    }
                }
                
                // 4. Resetear variables de juego
                idCuerpoCercano = -1;
                
                // Recuperar el foco para poder escribir en el chat (cuando lo hagamos)
                PanelJuego.this.requestFocusInWindow();
                
                // DETENER EL TIMER (Para que no se repita infinitamente)
                ((Timer)e.getSource()).stop(); 
            }
        });
        
        timerAnimacion.setRepeats(false); // Aseguramos que solo suene una vez
        timerAnimacion.start(); // ¡CORRE TIEMPO!
    }
    
    private void cambiarObjetivoEspectador(boolean avanzar) {
        // 1. Obtener lista de jugadores VIVOS (excluyéndome a mí si estoy muerto)
        java.util.ArrayList<Integer> vivos = new java.util.ArrayList<>();
        
        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                vivos.add(j.getId());
            }
        }
        
        // Si no queda nadie vivo (o solo 1), no hacemos nada
        if (vivos.isEmpty()) return;
        
        // Ordenamos la lista para que el cambio sea predecible (1 -> 2 -> 5...)
        java.util.Collections.sort(vivos);
        
        // 2. Buscar índice actual
        int indiceActual = vivos.indexOf(idEspectando);
        
        // 3. Calcular nuevo índice
        if (avanzar) {
            indiceActual++;
            if (indiceActual >= vivos.size()) indiceActual = 0; // Vuelta al principio
        } else {
            indiceActual--;
            if (indiceActual < 0) indiceActual = vivos.size() - 1; // Vuelta al final
        }
        
        // 4. Asignar nuevo objetivo
        idEspectando = vivos.get(indiceActual);
        System.out.println("Espectando ahora a: Jugador " + idEspectando);
        repaint();
    }
    
    // Método para permitir que ClienteRed acceda a un jugador específico
    public Jugador getJugador(int id) {
        return jugadoresConectados.get(id);
    }


public void agregarJugador(int id, String nombre, int x, int y, int r, int g, int b) {
    // CORRECCIÓN: El constructor de Jugador pide (id, nombre, x, y, esImpostor)
    // Pasamos 'false' inicialmente; el comando ROL lo actualizará después si es necesario.
    Jugador nuevo = new Jugador(id, nombre, x, y, false);
    
    // Aplicamos el color RGB
    nuevo.setColorRGB(r, g, b); 
    
    jugadoresConectados.put(id, nuevo);
    repaint();
}
    
}