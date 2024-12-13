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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class asistenciaClases extends JFrame {
    private Webcam webcam;
    private volatile boolean running = true; // Volatile para garantizar cambios entre hilos

    public asistenciaClases() {
        setTitle("Lector de QR");
        setSize(640, 540); // Ajustar el tamaño para acomodar el botón
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Cámara detectada: " + webcam.getName());
            webcam.setViewSize(new Dimension(640, 480));

            // Intentar abrir la cámara
            try {
                webcam.open();
                System.out.println("Cámara abierta correctamente.");
            } catch (Exception e) {
                System.err.println("Error al abrir la cámara: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("No se detectó ninguna cámara en el sistema.");
        }

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setMirrored(false);

        // Crear el botón de "Atrás"
        JButton btnAtras = new JButton("Atrás");
        btnAtras.addActionListener(e -> {
            detenerEscaneo(); // Detener el escaneo antes de cerrar la ventana
            dispose(); // Cerrar la ventana actual
        });

        // Añadir el panel de la cámara y el botón a la ventana
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnAtras);

        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        Thread scannerThread = new Thread(this::scanQRCode);
        scannerThread.setDaemon(true);
        scannerThread.start();

        setVisible(true);
    }

    private void scanQRCode() {
        while (running) {
            try {
                if (!webcam.isOpen()) {
                    System.err.println("La cámara no está abierta.");
                    break;
                }

                BufferedImage image = webcam.getImage();
                if (image == null) {
                    System.err.println("No se capturó ninguna imagen de la cámara.");
                    continue;
                }

                String qrData = decodeQRCode(image);
                if (qrData != null) {
                    System.out.println("QR Detectado: " + qrData);
                    String matricula = qrData.trim().split("-")[0];
                    System.out.println("QR escaneado a las: " + java.time.LocalTime.now());

                    // Conexión a la base de datos
                    try (Connection conn = ConexionSQLite.conectar()) {
                        if (conn == null) {
                            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }

                        // Buscar datos del alumno
                        String query = "SELECT * FROM UsuariosAlumnos WHERE Matricula = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setString(1, matricula);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    String nombre = rs.getString("Nombre");
                                    String apellido = rs.getString("Apellido");
                                    String materia = rs.getString("Materias"); // Obtener la materia desde la base de datos

                                    // Verificar horario de clase
                                    if (estaEnHorario(conn, materia)) {
                                        if (registrarAsistencia(conn, matricula, nombre, apellido, materia)) {
                                            JOptionPane.showMessageDialog(this, "Asistencia registrada para " + materia, "Asistencia", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(this, "Error al registrar la asistencia.", "Error", JOptionPane.WARNING_MESSAGE);
                                        }
                                    } else {
                                        System.out.println("Alumno fuera del horario para la materia: " + materia);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(this, "No se encontró el alumno con la matrícula " + matricula, "Error", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        }
                    }
                }

                Thread.sleep(500); // Ajusta este tiempo según sea necesario

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void detenerEscaneo() {
        running = false; // Detenemos el hilo
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    @Override
    public void dispose() {
        detenerEscaneo(); // Aseguramos que los recursos se liberen
        super.dispose(); // Llamamos al método original de JFrame
    }

    private int convertirHoraAEntero(String hora) {
        try {
            // Formato esperado: HH:mm o HH:mm:ss
            String[] partes = hora.split(":");
            int horas = Integer.parseInt(partes[0]) * 100;
            int minutos = Integer.parseInt(partes[1]);
            return horas + minutos;
        } catch (Exception e) {
            System.err.println("Error al convertir hora: " + e.getMessage());
            return -1;
        }
    }

    private boolean verificarHoraEnRango(String horaEscaneada, String rangoHoras) {
        try {
            String[] horas = rangoHoras.split("-");
            int horaInicio = convertirHoraAEntero(horas[0].trim());
            int horaFin = convertirHoraAEntero(horas[1].trim());
            int horaActual = convertirHoraAEntero(horaEscaneada);

            return horaActual >= horaInicio && horaActual <= horaFin;
        } catch (Exception e) {
            System.err.println("Error al verificar el rango de horas: " + e.getMessage());
            return false;
        }
    }

    private boolean estaEnHorario(Connection conn, String materia) {
        if (conn == null || materia == null || materia.trim().isEmpty()) {
            System.err.println("Conexión o materia inválida.");
            return false;
        }

        List<String> horarios = new ArrayList<>();
        try {
            String query = "SELECT Horario FROM UsuariosMaestros WHERE Materia = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, materia);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        horarios.add(rs.getString("Horario"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (horarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron horarios para la materia: " + materia, "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean enHorario = false;
        for (String horario : horarios) {
            if (verificarDiaYHora(horario)) {
                enHorario = true;
                break;
            }
        }

        if (!enHorario) {
            JOptionPane.showMessageDialog(this, "No estás en el horario establecido para la materia: " + materia + ". Horarios: " + horarios, "Fuera de Horario", JOptionPane.INFORMATION_MESSAGE);
        }

        return enHorario;
    }


    private boolean registrarAsistencia(Connection conn, String matricula, String nombre, String apellido, String materia) {
        try {
            String querySelect = """
                SELECT Asistencia 
                FROM AsistenciaCentroDeComputo 
                WHERE Matricula = ? AND Carrera = ? AND Proposito = 'Asistencia a clase'
            """;
            try (PreparedStatement pstmtSelect = conn.prepareStatement(querySelect)) {
                pstmtSelect.setString(1, matricula);
                pstmtSelect.setString(2, materia);

                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        int asistenciaActual = rs.getInt("Asistencia");
                        String queryUpdate = """
                            UPDATE AsistenciaCentroDeComputo 
                            SET Asistencia = ?, FechaHora = ? 
                            WHERE Matricula = ? AND Carrera = ?
                        """;
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(queryUpdate)) {
                            pstmtUpdate.setInt(1, asistenciaActual + 1);
                            pstmtUpdate.setString(2, java.time.LocalDateTime.now().toString());
                            pstmtUpdate.setString(3, matricula);
                            pstmtUpdate.setString(4, materia);
                            pstmtUpdate.executeUpdate();
                            return true;
                        }
                    } else {
                        String queryInsert = """
                            INSERT INTO AsistenciaCentroDeComputo 
                            (Matricula, Nombre, Apellido, Carrera, Proposito, Horas, FechaHora, Asistencia) 
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                        try (PreparedStatement pstmtInsert = conn.prepareStatement(queryInsert)) {
                            pstmtInsert.setString(1, matricula);
                            pstmtInsert.setString(2, nombre);
                            pstmtInsert.setString(3, apellido);
                            pstmtInsert.setString(4, materia);
                            pstmtInsert.setString(5, "Asistencia a clase");
                            pstmtInsert.setInt(6, 0);
                            pstmtInsert.setString(7, java.time.LocalDateTime.now().toString());
                            pstmtInsert.setInt(8, 1);
                            pstmtInsert.executeUpdate();
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String decodeQRCode(BufferedImage image) {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }
    private boolean verificarDiaYHora(String diaYHora) {
        try {
            // Separar el día del rango de horas
            String[] partes = diaYHora.split(" ");
            String dia = partes[0].trim(); // Ejemplo: "Martes"
            String rangoHoras = partes[1].trim(); // Ejemplo: "08:00-10:00"

            // Obtener el día actual
            String diaActual = java.time.LocalDate.now().getDayOfWeek().toString().toLowerCase();
            String diaEnEspañol = convertirDiaAEs(diaActual);

            // Si el día no coincide, salir
            if (!diaEnEspañol.equalsIgnoreCase(dia)) {
                return false;
            }

            // Verificar la hora dentro del rango
            return verificarHoraEnRango(LocalTime.now().toString(), rangoHoras);
        } catch (Exception e) {
            System.err.println("Error al verificar el día y la hora: " + e.getMessage());
            return false;
        }
    }

    private String convertirDiaAEs(String dia) {
        return switch (dia) {
            case "monday" -> "Lunes";
            case "tuesday" -> "Martes";
            case "wednesday" -> "Miércoles";
            case "thursday" -> "Jueves";
            case "friday" -> "Viernes";
            case "saturday" -> "Sábado";
            case "sunday" -> "Domingo";
            default -> "";
        };
    }

}


