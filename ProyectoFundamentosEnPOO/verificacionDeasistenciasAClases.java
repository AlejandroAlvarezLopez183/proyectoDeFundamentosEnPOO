package ProyectoFundamentosEnPOO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class verificacionDeasistenciasAClases {

    public void verificacionDeasistenciasAClases(JFrame ventanaPrincipal) {
        String input = JOptionPane.showInputDialog(null, "Ingrese el ID del maestro (5 dígitos):",
                                                   "Verificación de Asistencias", JOptionPane.QUESTION_MESSAGE);

        if (input != null && input.matches("\\d{5}")) {
            int idMaestro = Integer.parseInt(input);
            mostrarTablasPorMaterias(idMaestro);
        } else {
            JOptionPane.showMessageDialog(null, "ID inválido. Por favor ingrese un número de 5 dígitos.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTablasPorMaterias(int idMaestro) {
        JTabbedPane tabbedPane = new JTabbedPane();

        try (Connection conn = ConexionSQLite.conectar()) {
            String sqlMaterias = "SELECT Materia FROM UsuariosMaestros WHERE IDMaestro = ?";
            try (PreparedStatement pstmtMaterias = conn.prepareStatement(sqlMaterias)) {
                pstmtMaterias.setInt(1, idMaestro);

                try (ResultSet rsMaterias = pstmtMaterias.executeQuery()) {
                    boolean tieneMaterias = false;

                    while (rsMaterias.next()) {
                        tieneMaterias = true;
                        String materia = rsMaterias.getString("Materia");
                        JTable tabla = crearTablaPorMateria(materia);

                        if (tabla != null) {
                            JScrollPane scrollPane = new JScrollPane(tabla);
                            tabbedPane.addTab(materia, scrollPane);
                        }
                    }

                    if (!tieneMaterias) {
                        JOptionPane.showMessageDialog(null, "No se encontraron materias para este maestro.",
                                                      "Información", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar la base de datos: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        JFrame frame = new JFrame("Alumnos Inscritos por Materia");
        frame.add(tabbedPane);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private JTable crearTablaPorMateria(String materia) {
        String[] columnNames = {"Matrícula", "Nombre", "Apellido"};
        DefaultTableModel modelo = new DefaultTableModel(columnNames, 0);

        try (Connection conn = ConexionSQLite.conectar()) {
            String sql = """
                SELECT ua.Matricula, ua.Nombre, ua.Apellido
                FROM UsuariosAlumnos ua
                WHERE ',' || ua.Materias || ',' LIKE '%,' || ? || ',%';
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, materia);

                try (ResultSet rs = pstmt.executeQuery()) {
                    boolean tieneAlumnos = false;

                    while (rs.next()) {
                        tieneAlumnos = true;
                        String matricula = rs.getString("Matricula");
                        String nombre = rs.getString("Nombre");
                        String apellido = rs.getString("Apellido");

                        modelo.addRow(new Object[]{matricula, nombre, apellido});
                    }

                    if (!tieneAlumnos) {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar los datos: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return new JTable(modelo);
    }
}

