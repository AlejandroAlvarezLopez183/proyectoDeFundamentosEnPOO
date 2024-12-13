package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class alumnos {

    public alumnos(JFrame ventanaRegistro) {
        JFrame frameAlumnos = new JFrame("Registro de Alumnos");
        frameAlumnos.setSize(500, 500);
        frameAlumnos.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAlumnos.setLayout(new BorderLayout());

        // Panel central para los campos del formulario
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campos de formulario
        JTextField campoMatricula = new JTextField();
        JTextField campoNombre = new JTextField();
        JTextField campoApellidos = new JTextField();

        // Etiquetas y campos
        panelCentral.add(new JLabel("Matrícula:"));
        panelCentral.add(campoMatricula);
        panelCentral.add(Box.createVerticalStrut(10));

        panelCentral.add(new JLabel("Nombre:"));
        panelCentral.add(campoNombre);
        panelCentral.add(Box.createVerticalStrut(10));

        panelCentral.add(new JLabel("Apellidos:"));
        panelCentral.add(campoApellidos);
        panelCentral.add(Box.createVerticalStrut(10));

        // ComboBox para seleccionar materias
        JComboBox<String> comboBoxMaterias = new JComboBox<>();
        cargarMaterias(comboBoxMaterias);
        panelCentral.add(new JLabel("Seleccionar Materia:"));
        panelCentral.add(comboBoxMaterias);
        panelCentral.add(Box.createVerticalStrut(10));

        // Botón para añadir materia a la lista de materias inscritas
        JButton botonAñadirMateria = new JButton("Añadir Materia");
        JTextArea campoMateriasInscritas = new JTextArea(5, 30);
        campoMateriasInscritas.setEditable(false);
        JScrollPane scrollMaterias = new JScrollPane(campoMateriasInscritas);

        botonAñadirMateria.addActionListener(e -> {
            String materiaSeleccionada = (String) comboBoxMaterias.getSelectedItem();
            if (materiaSeleccionada != null && !materiaSeleccionada.isEmpty()) {
                // Verificar si la materia ya está en el JTextArea
                if (!campoMateriasInscritas.getText().contains(materiaSeleccionada)) {
                    campoMateriasInscritas.append(materiaSeleccionada + "\n");
                } else {
                    JOptionPane.showMessageDialog(null, "La materia ya ha sido añadida.");
                }
            }
        });


        panelCentral.add(botonAñadirMateria);
        panelCentral.add(scrollMaterias);
        panelCentral.add(Box.createVerticalStrut(10));

        // Botones "Guardar" y "Atrás"
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonGuardar = new JButton("Guardar");
        JButton botonAtras = new JButton("Atrás");

        panelInferior.add(botonGuardar);
        panelInferior.add(botonAtras);

        // Acción del botón "Guardar"
     // Acción del botón "Guardar"
        botonGuardar.addActionListener(e -> {
            String matricula = campoMatricula.getText();
            String nombre = campoNombre.getText();
            String apellidos = campoApellidos.getText();
            String clases = campoMateriasInscritas.getText().trim();

            // Validar que la matrícula siga el formato "ZS" seguido de 8 dígitos
            if (!matricula.matches("^zs\\d{8}$")) {
                JOptionPane.showMessageDialog(null, "La matrícula debe comenzar con 'ZS' seguido de 8 dígitos.");
                return;
            }

            if (matricula.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || clases.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, llena todos los campos.");
            } else {
                registrarAlumno(matricula, nombre, apellidos, clases);
            }
        });

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> {
            frameAlumnos.dispose();
            ventanaRegistro.setVisible(true);
        });

        // Añadir paneles a la ventana
        frameAlumnos.add(panelCentral, BorderLayout.CENTER);
        frameAlumnos.add(panelInferior, BorderLayout.SOUTH);

        frameAlumnos.setVisible(true);
    }

    // Método para registrar alumno en la base de datos
    private void registrarAlumno(String matricula, String nombre, String apellidos, String clases) {
        String sql = "INSERT INTO UsuariosAlumnos (Matricula, Nombre, Apellido, Materias) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, clases);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Alumno registrado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar alumno: " + e.getMessage());
        }
    }

    // Método para cargar las materias desde la base de datos
    private void cargarMaterias(JComboBox<String> comboBox) {
        String sql = "SELECT DISTINCT Materia FROM UsuariosMaestros";
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String materia = rs.getString("Materia");
                comboBox.addItem(materia);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar materias: " + e.getMessage());
        }
    }

    // Método para agregar una nueva materia a la base de datos
    private void agregarNuevaMateria(String materia) {
        String sqlCheck = "SELECT COUNT(*) FROM UsuariosMaestros WHERE Materia = ?";
        String sqlInsert = "INSERT INTO UsuariosMaestros (Materia) VALUES (?)";
        
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
             
            // Verificar si la materia ya existe
            pstmtCheck.setString(1, materia);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Si la materia ya existe, mostrar mensaje
                    JOptionPane.showMessageDialog(null, "La materia ya está registrada.");
                } else {
                    // Si no existe, insertar nueva materia
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                        pstmtInsert.setString(1, materia);
                        pstmtInsert.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Materia registrada exitosamente.");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar nueva materia: " + e.getMessage());
        }
    }

}

