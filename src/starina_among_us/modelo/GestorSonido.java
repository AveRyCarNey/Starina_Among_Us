package starina_among_us.modelo;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Gestor global encargado del subsistema de audio del juego.
 * Permite reproducir musica de fondo en bucle y efectos de sonido simultaneos
 * utilizando hilos separados. Tambien maneja la conversion matematica de volumen lineal a decibelios.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class GestorSonido {
    private static Clip clipMusica; 
    
    private static float volumenMusica = 0.5f; 
    private static float volumenEfectos = 0.5f; 

    /**
     * Inicia la reproduccion de una pista de musica ambiental en bucle infinito.
     * Detiene automaticamente cualquier otra pista que este sonando.
     * * @param archivo El nombre y extension del archivo de sonido (ej: "intro.wav").
     */
    public static synchronized void musicaLoop(String archivo) {
        detenerMusica(); 

        try {
            URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + archivo);
            if (url == null) {
                System.err.println("No se encontro el archivo de sonido: " + archivo);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(ais);
            
            actualizarVolumenMusica(); 
            
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
        } catch (Exception e) {
            System.err.println("Error en musicaLoop (" + archivo + "): " + e.getMessage());
        }
    }

    /**
     * Reproduce un efecto de sonido corto una sola vez.
     * Se ejecuta en un nuevo hilo para no interrumpir ni pausar el motor grafico.
     * * @param archivo El nombre del archivo de efecto de sonido (ej: "kill.wav").
     */
    public static void jugar(String archivo) {
        new Thread(() -> {
            try {
                URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + archivo);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    
                    aplicarVolumen(clip, volumenEfectos);
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Error en jugar efecto: " + archivo);
            }
        }).start();
    }

    private static void actualizarVolumenMusica() {
        if (clipMusica != null) {
            aplicarVolumen(clipMusica, volumenMusica);
        }
    }

    /**
     * Convierte un valor de volumen lineal en una escala logaritmica de decibelios
     * y se lo aplica directamente a un Clip de audio de Java.
     * * @param clip El objeto Clip de Java Sound a modificar.
     * @param valorLineal Nivel de volumen deseado (0.0 es silencio, 1.0 es maximo).
     */
    private static void aplicarVolumen(Clip clip, float valorLineal) {
        try {
            if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
                BooleanControl muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
                if (valorLineal <= 0.001f) {
                    muteControl.setValue(true);
                    return; 
                } else {
                    muteControl.setValue(false); 
                }
            }

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                if (valorLineal <= 0.001f) {
                    gainControl.setValue(gainControl.getMinimum());
                    return;
                }

                float dB = (float) (Math.log10(valorLineal) * 20.0);
                dB += 3.0f; 

                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                gainControl.setValue(Math.max(min, Math.min(max, dB)));
            }
        } catch (Exception e) {
            System.err.println("Error al ajustar volumen: " + e.getMessage());
        }
    }

    /**
     * Detiene la musica de fondo actual y libera los recursos de memoria asociados.
     */
    public static synchronized void detenerMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }
    
    /**
     * Define el volumen maestro para la musica ambiental.
     * * @param vol Nivel de volumen entre 0.0 y 1.0.
     */
    public static void setVolumenMusica(float vol) { 
        volumenMusica = vol; 
        actualizarVolumenMusica();
    }

    /**
     * Define el volumen maestro para los efectos de sonido.
     * * @param vol Nivel de volumen entre 0.0 y 1.0.
     */
    public static void setVolumenEfectos(float vol) { 
        volumenEfectos = vol; 
    }
}