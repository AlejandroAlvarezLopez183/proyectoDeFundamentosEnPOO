package ProyectoFundamentosEnPOO;

import static spark.Spark.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Base64;

public class generadorDeQR {
    
    public generadorDeQR() {
        iniciarServidor();
    }

    private void iniciarServidor() {
        // Configura Spark para escuchar en la IP de tu máquina local y el puerto 4567
        String ipLocal = "0.0.0.0";  // Reemplaza con la IP obtenida de tu máquina
        String url = "http://" + ipLocal + ":4567";

        port(4567);
        before((request, response) -> {
            System.out.println("Solicitud recibida desde: " + request.ip());
        });
        // Ruta para verificar la matrícula
        get("/verificarMatricula", (req, res) -> {
            String matricula = req.queryParams("matricula");
            String nombreUsuario = obtenerNombrePorMatricula(matricula);

            if (nombreUsuario != null) {
                String qrCodeBase64 = generarQRBase64(matricula);
                if (qrCodeBase64 != null) {
                    res.type("application/json");
                    return "{ \"nombre\": \"" + nombreUsuario + "\", \"qr\": \"" + qrCodeBase64 + "\" }";
                } else {
                    res.status(500);  // Error de servidor al generar QR
                    return "{ \"error\": \"Error al generar el código QR\" }";
                }
            } else {
                res.status(404);
                return "{ \"error\": \"Usuario no encontrado\" }";
            }
        });

    }


    private String obtenerNombrePorMatricula(String matricula) {
        String nombre = null;
        String url = "jdbc:sqlite:src/ProyectoFundamentosEnPOO/ProyectoProgramacion.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            String query = "SELECT Nombre FROM UsuariosAlumnos WHERE Matricula = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, matricula);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("Nombre");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nombre;
    }

    private String generarQRBase64(String matricula) {
        try {
            // Crear contenido dinámico para el QR
            String contenidoQR = generarContenidoDinamico(matricula);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(contenidoQR, BarcodeFormat.QR_CODE, 300, 300);

            // Convertir el QR a PNG en formato Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrBytes);
        } catch (WriterException | java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generarContenidoDinamico(String matricula) {
        // Obtener el tiempo actual en segundos
        long tiempoActual = Instant.now().getEpochSecond();

        // Dividir el tiempo en intervalos de 10 segundos
        long intervalo = tiempoActual / 10;

        // Contenido dinámico: matrícula + intervalo
        return matricula + "-" + intervalo;
    }
}
