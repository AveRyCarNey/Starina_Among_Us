package starina_among_us.modelo;

import javax.sound.sampled.*;
import java.net.URL;

public class GestorSonido {
    private static Clip clipMusicaActual; 
    
    // --- VARIABLES DE VOLUMEN (Decibelios) ---
    // 0.0f = Original | -10.0f = Medio | -20.0f = Bajo | -80.0f = Silencio
    private static float volumenMusica = -15.0f; 
    private static float volumenEfectos = -5.0f; 

    public static synchronized void musicaLoop(String rutaRelativa) {
        detenerMusica(); 

        try {
            URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + rutaRelativa);
            if (url == null) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clipMusicaActual = AudioSystem.getClip();
            clipMusicaActual.open(audioIn);
            
            // --- APLICAR VOLUMEN A LA MÚSICA ---
            aplicarVolumen(clipMusicaActual, volumenMusica);
            
            clipMusicaActual.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusicaActual.start();
        } catch (Exception e) {
            System.err.println("Error en musicaLoop: " + e.getMessage());
        }
    }

    public static void jugar(String rutaRelativa) {
        new Thread(() -> {
            try {
                URL url = GestorSonido.class.getResource("/starina_among_us/recursos/sonidos/" + rutaRelativa);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    
                    // --- APLICAR VOLUMEN A LOS EFECTOS ---
                    aplicarVolumen(clip, volumenEfectos);
                    
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Error en jugar: " + rutaRelativa);
            }
        }).start();
    }

    // --- MÉTODO AUXILIAR PARA EL CONTROL DE GANANCIA ---
    private static void aplicarVolumen(Clip clip, float valorDB) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // Aseguramos que el valor no se pase de los límites del sistema
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float actual = Math.max(min, Math.min(max, valorDB));
                gainControl.setValue(actual);
            }
        } catch (Exception e) {
            System.err.println("No se pudo ajustar el volumen: " + e.getMessage());
        }
    }

    public static synchronized void detenerMusica() {
        if (clipMusicaActual != null) {
            clipMusicaActual.stop();
            clipMusicaActual.close();
            clipMusicaActual = null;
        }
    }
    
    // Métodos para cambiar el volumen desde otros archivos (Opcional)
    public static void setVolumenMusica(float db) { volumenMusica = db; }
    public static void setVolumenEfectos(float db) { volumenEfectos = db; }
}