package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class asistenciasAlCentroDeComputo extends JFrame {

    public asistenciasAlCentroDeComputo() {
        // Configuración básica de la ventana
        setTitle("Registro de Asistencia - Centro de Cómputo");
        setSize(400, 450); // Aumentamos ligeramente el tamaño para acomodar el botón de regresar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear el panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10)); // 8 filas para incluir el botón "Regresar"

        // Crear los componentes del formulario
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();

        JLabel lblApellido = new JLabel("Apellido:");
        JTextField txtApellido = new JTextField();

        JLabel lblCarrera = new JLabel("Carrera:");
        JTextField txtCarrera = new JTextField();

        JLabel lblMatricula = new JLabel("Matrícula:");
        JTextField txtMatricula = new JTextField();

        JLabel lblProposito = new JLabel("Propósito del equipo:");
        JTextField txtProposito = new JTextField();

        JLabel lblHoras = new JLabel("Horas de uso:");
        JTextField txtHoras = new JTextField();

        JButton btnEnviar = new JButton("Enviar");
        JButton btnRegresar = new JButton("Regresar"); // Nuevo botón "Regresar"

        // Agregar los componentes al panel
        panel.add(lblNombre);
        panel.add(txtNombre);
        panel.add(lblApellido);
        panel.add(txtApellido);
        panel.add(lblCarrera);
        panel.add(txtCarrera);
        panel.add(lblMatricula);
        panel.add(txtMatricula);
        panel.add(lblProposito);
        panel.add(txtProposito);
        panel.add(lblHoras);
        panel.add(txtHoras);
        panel.add(btnRegresar); // Agregar botón "Regresar"
        panel.add(btnEnviar);   // Botón "Enviar" permanece al final

        // Agregar el panel al marco
        add(panel);

        // Acción del botón "Enviar"
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText();
                String apellido = txtApellido.getText();
                String carrera = txtCarrera.getText();
                String matricula = txtMatricula.getText();
                String proposito = txtProposito.getText();
                String horas = txtHoras.getText();

                // Validar que todos los campos estén llenos
                if (nombre.isEmpty() || apellido.isEmpty() || carrera.isEmpty() || matricula.isEmpty() || proposito.isEmpty() || horas.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try (Connection conn = ConexionSQLite.conectar()) {
                        String sql = """
                            INSERT INTO AsistenciaCentroDeComputo (Nombre, Apellido, Carrera, Matricula, Proposito, Horas, FechaHora)
                            VALUES (?, ?, ?, ?, ?, ?, ?);
                        """;
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, nombre);
                            pstmt.setString(2, apellido);
                            pstmt.setString(3, carrera);
                            pstmt.setString(4, matricula);
                            pstmt.setString(5, proposito);
                            pstmt.setString(6, horas);
                            pstmt.setString(7, obtenerFechaHoraLocal()); // Configura FechaHora
                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Registro guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al guardar el registro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Acción del botón "Regresar"
        btnRegresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra esta ventana y abre el menú principal
                dispose(); // Cierra la ventana actual
            }
        });
    }
    public static String obtenerFechaHoraLocal() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formato.format(new Date());
    }
}
