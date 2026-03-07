package starina_among_us.modelo;

import javax.sound.sampled.*;
import java.net.URL;

public class GestorSonido {
    private static Clip clipMusica; 
    
    // --- VARIABLES DE VOLUMEN (Escala lineal 0.0 a 1.0) ---
    // Guardamos valores entre 0 y 1 para que los Sliders funcionen fácil.
    private static float volumenMusica = 0.5f; 
    private static float volumenEfectos = 0.5f; 

    /**
     * Reproduce música en bucle infinito.
     * @param archivo Nombre del archivo (ej: "intro.wav")
     */
    public static synchronized void musicaLoop(String archivo) {
        detenerMusica(); // Evita que se solapen dos canciones

        try {
            URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + archivo);
            if (url == null) {
                System.err.println("No se encontró el archivo de sonido: " + archivo);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(ais);
            
            // Aplicar el volumen actual antes de empezar
            actualizarVolumenMusica(); 
            
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
        } catch (Exception e) {
            System.err.println("Error en musicaLoop (" + archivo + "): " + e.getMessage());
        }
    }

    /**
     * Reproduce un efecto de sonido una sola vez (SFX).
     * @param archivo Nombre del archivo (ej: "kill.wav")
     */
    public static void jugar(String archivo) {
        new Thread(() -> {
            try {
                URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + archivo);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    
                    // Aplicar volumen lineal convertido a Decibelios
                    aplicarVolumen(clip, volumenEfectos);
                    
                    clip.start();
                    // El clip se libera automáticamente al terminar si no se guarda referencia
                }
            } catch (Exception e) {
                System.err.println("Error en jugar (SFX): " + archivo);
            }
        }).start();
    }

    /**
     * Ajusta el volumen del Clip actual de música.
     */
    private static void actualizarVolumenMusica() {
        if (clipMusica != null) {
            aplicarVolumen(clipMusica, volumenMusica);
        }
    }

    /**
     * Método interno para convertir volumen lineal (0-1) a Decibelios y aplicarlo.
     */
    private static void aplicarVolumen(Clip clip, float valorLineal) {
        try {
            // --- 1. SILENCIO ABSOLUTO (MUTE) ---
            if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
                BooleanControl muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
                if (valorLineal <= 0.001f) {
                    muteControl.setValue(true); // ¡Corta la señal de audio por completo!
                    return; // Salimos de la función, ya no hay que calcular decibelios
                } else {
                    muteControl.setValue(false); // Le quitamos el Mute si el jugador sube el slider
                }
            }

            // --- 2. CÁLCULO DE VOLUMEN NORMAL ---
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                // Fallback por si la PC no soporta Mute
                if (valorLineal <= 0.001f) {
                    gainControl.setValue(gainControl.getMinimum());
                    return;
                }

                // Fórmula logarítmica estándar
                float dB = (float) (Math.log10(valorLineal) * 20.0);
                dB += 3.0f; // Pequeño boost para que suene vivo

                // Aseguramos que el valor esté dentro de los límites
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                gainControl.setValue(Math.max(min, Math.min(max, dB)));
            }
        } catch (Exception e) {
            System.err.println("Error al ajustar volumen: " + e.getMessage());
        }
    }

    /**
     * Detiene y libera la música actual.
     */
    public static synchronized void detenerMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }
    
    // --- SETTERS PARA LOS SLIDERS DEL MENÚ ---

    public static void setVolumenMusica(float vol) { 
        volumenMusica = vol; 
        actualizarVolumenMusica();
    }

    public static void setVolumenEfectos(float vol) { 
        volumenEfectos = vol; 
    }
}