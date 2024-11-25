package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class administrador {

    public administrador(JFrame ventanaPrincipal) {
        // Solicitar la contraseña de administrador
        String password = JOptionPane.showInputDialog(null, "Ingresa la contraseña del administrador:");
        
        // Verificar la contraseña
        if (password == null) {
            ventanaPrincipal.setVisible(true);
            return;
        }
        
        if (!password.equals("admin123")) { // Ajusta la contraseña según tus necesidades
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
            ventanaPrincipal.setVisible(true);
            return;
        }

        // Crear la ventana del administrador
        JFrame frameAdmin = new JFrame("Panel del Administrador");
        frameAdmin.setSize(500, 300);
        frameAdmin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAdmin.setLayout(new BorderLayout());

        // Panel central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botón para ver alumnos registrados
        JButton botonVerAlumnos = new JButton("Ver Alumnos Registrados");
        botonVerAlumnos.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonVerAlumnos.addActionListener(e -> mostrarAlumnosRegistrados());

        // Botón para ver horarios de maestros
        JButton botonHorariosMaestros = new JButton("Horarios de los Maestros");
        botonHorariosMaestros.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonHorariosMaestros.addActionListener(e -> mostrarHorariosMaestros());
        
        //boton del reporte del dia
        JButton botonReporteAsistencia = new JButton("Reporte de Asistencia");
        botonReporteAsistencia.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonReporteAsistencia.addActionListener(e -> mostrarReporteAsistencia());

        panelCentral.add(botonVerAlumnos);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(botonHorariosMaestros);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(botonReporteAsistencia);

        // Botón "Atrás"
        JButton botonAtras = new JButton("Atrás");
        botonAtras.addActionListener(e -> {
            frameAdmin.dispose();
            ventanaPrincipal.setVisible(true);
        });

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.add(botonAtras);

        // Añadir paneles a la ventana
        frameAdmin.add(panelCentral, BorderLayout.CENTER);
        frameAdmin.add(panelInferior, BorderLayout.SOUTH);

        frameAdmin.setVisible(true);
    }

    // Método para verificar la contraseña de administrador
    private boolean verificarContraseñaAdmin() {
        String password = JOptionPane.showInputDialog(null, "Ingresa la contraseña del administrador:");
        return password != null && password.equals("admin123");
    }

    // Método para mostrar los alumnos registrados
    private void mostrarAlumnosRegistrados() {
        JFrame frameAlumnos = new JFrame("Alumnos Registrados");
        frameAlumnos.setSize(700, 400);
        
        JButton botonAtras = new JButton("Atrás");

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> frameAlumnos.dispose());
        
        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Matrícula");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Materias");

        JTable tablaAlumnos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaAlumnos);

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM UsuariosAlumnos";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String matricula = rs.getString("Matricula");
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String materias = rs.getString("Materias").replaceAll("\\s+", " ").trim();
                modeloTabla.addRow(new Object[]{matricula, nombre, apellido, materias});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.getMessage());
        }

        // Botones para eliminar alumnos
        JButton botonEliminarSeleccionado = new JButton("Eliminar Alumno Seleccionado");
        JButton botonEliminarTodos = new JButton("Eliminar Todos los Alumnos");

        botonEliminarSeleccionado.addActionListener(e -> {
            int filaSeleccionada = tablaAlumnos.getSelectedRow();
            if (filaSeleccionada != -1) {
                String matricula = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
                if (verificarContraseñaAdmin()) {
                    eliminarAlumnoPorMatricula(matricula);
                    modeloTabla.removeRow(filaSeleccionada);
                }
            } else {
                JOptionPane.showMessageDialog(frameAlumnos, "Seleccione un alumno para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonEliminarTodos.addActionListener(e -> {
            if (verificarContraseñaAdmin()) {
                int confirmacion = JOptionPane.showConfirmDialog(frameAlumnos, "¿Eliminar todos los alumnos?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    eliminarTodosLosAlumnos();
                    modeloTabla.setRowCount(0);
                }
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonEliminarSeleccionado);
        panelBotones.add(botonEliminarTodos);
        panelBotones.add(botonAtras);

        frameAlumnos.add(scrollPane, BorderLayout.CENTER);
        frameAlumnos.add(panelBotones, BorderLayout.SOUTH);
        frameAlumnos.setVisible(true);
    }

    // Método para mostrar los horarios de los maestros
    private void mostrarHorariosMaestros() {
        JFrame frameHorarios = new JFrame("Horarios de los Maestros");
        frameHorarios.setSize(700, 400);

        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Materia");
        modeloTabla.addColumn("Horario");

        JTable tablaHorarios = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaHorarios);

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT IDMaestro, Nombre, Apellido, Materia, Horario FROM UsuariosMaestros";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{rs.getInt("IDMaestro"), rs.getString("Nombre"), rs.getString("Apellido"), rs.getString("Materia"), rs.getString("Horario")});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.getMessage());
        }

        JButton botonEliminarSeleccionado = new JButton("Eliminar Maestro Seleccionado");
        JButton botonEliminarTodos = new JButton("Eliminar Todos los Maestros");

        botonEliminarSeleccionado.addActionListener(e -> {
            int filaSeleccionada = tablaHorarios.getSelectedRow();
            if (filaSeleccionada != -1) {
                int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                if (verificarContraseñaAdmin()) {
                    eliminarMaestroPorID(id);
                    modeloTabla.removeRow(filaSeleccionada);
                }
            }
        });

        botonEliminarTodos.addActionListener(e -> {
            if (verificarContraseñaAdmin()) {
                eliminarTodosLosMaestros();
                modeloTabla.setRowCount(0);
            }
        });
        JButton botonAtras = new JButton("Atrás");

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> frameHorarios.dispose());
        

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonEliminarSeleccionado);
        panelBotones.add(botonEliminarTodos);
        panelBotones.add(botonAtras); 

        frameHorarios.add(scrollPane, BorderLayout.CENTER);
        frameHorarios.add(panelBotones, BorderLayout.SOUTH);
        frameHorarios.setVisible(true);
    }

    // Métodos de eliminación
    private void eliminarAlumnoPorMatricula(String matricula) {
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM UsuariosAlumnos WHERE Matricula = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, matricula);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar alumno: " + e.getMessage());
        }
    }
    
    private void eliminarTodosLosAlumnos() {
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM UsuariosAlumnos";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar alumnos: " + e.getMessage());
        }
    }
    
    private void eliminarMaestroPorID(int id) {
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM UsuariosMaestros WHERE IDMaestro = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar maestro: " + e.getMessage());
        }
    }
    
    private void eliminarTodosLosMaestros() {
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM UsuariosMaestros";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar maestros: " + e.getMessage());
        }
    }
 // Método para mostrar el reporte de asistencia
    private void mostrarReporteAsistencia() {
        JFrame frameReporte = new JFrame("Reporte de Asistencia al Centro de Cómputo");
        frameReporte.setSize(800, 400);

        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Carrera");
        modeloTabla.addColumn("Matrícula");
        modeloTabla.addColumn("Propósito");
        modeloTabla.addColumn("Horas");
        modeloTabla.addColumn("Fecha y Hora");

        JTable tablaReporte = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaReporte);

        // Conectar a la base de datos y recuperar datos
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM AsistenciaCentroDeComputo";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Carrera"),
                    rs.getString("Matricula"),
                    rs.getString("Proposito"),
                    rs.getInt("Horas"),
                    rs.getString("FechaHora")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.getMessage());
        }

        // Botón "Atrás"
        JButton botonAtras = new JButton("Atrás");
        botonAtras.addActionListener(e -> frameReporte.dispose());

        // Panel para el botón
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonAtras);

        // Añadir componentes a la ventana
        frameReporte.add(scrollPane, BorderLayout.CENTER);
        frameReporte.add(panelBotones, BorderLayout.SOUTH);
        frameReporte.setVisible(true);
    }


}

