package ProyectoFundamentosEnPOO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class asistenciaClases extends JFrame {
    private Webcam webcam;
    private boolean running = true;

    public asistenciaClases() {
        setTitle("Lector de QR");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializar la cámara
        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(640, 480));

        // Crear un panel para mostrar la cámara
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setMirrored(false);

        // Agregar el panel a la ventana
        add(panel, BorderLayout.CENTER);

        // Iniciar el escaneo automático en segundo plano
        Thread scannerThread = new Thread(this::scanQRCode);
        scannerThread.setDaemon(true);
        scannerThread.start();

        setVisible(true);
    }

    private void scanQRCode() {
        while (running) {
            try {
                if (webcam.isOpen()) {
                    // Capturar una imagen de la cámara
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        // Decodificar el QR
                        String qrData = decodeQRCode(image);
                        if (qrData != null) {
                            // Mostrar el contenido del QR
                            JOptionPane.showMessageDialog(this, "QR Detectado: " + qrData, "QR Escaneado", JOptionPane.INFORMATION_MESSAGE);

                            // Detener el escaneo después de un QR exitoso (opcional)
                            running = false;
                        }
                    }
                }
                Thread.sleep(500); // Evitar sobrecarga de la CPU
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String decodeQRCode(BufferedImage image) {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText(); // Retorna el contenido del QR
        } catch (NotFoundException e) {
            // No se encontró un código QR en la imagen
            return null;
        }
    }
}