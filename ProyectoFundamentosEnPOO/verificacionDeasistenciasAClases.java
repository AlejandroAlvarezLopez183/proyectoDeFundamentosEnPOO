package ProyectoFundamentosEnPOO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class  verificacionDeasistenciasAClases {

    public void  verificacionDeasistenciasAClases(JFrame ventanaPrincipal) {
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
            // Consulta para obtener las materias asociadas al maestro
            String sqlMaterias = "SELECT Materia FROM UsuariosMaestros WHERE IDMaestro = ?";
            try (PreparedStatement pstmtMaterias = conn.prepareStatement(sqlMaterias)) {
                pstmtMaterias.setInt(1, idMaestro);

                try (ResultSet rsMaterias = pstmtMaterias.executeQuery()) {
                    boolean tieneMaterias = false;

                    while (rsMaterias.next()) {
                        tieneMaterias = true;
                        String materia = rsMaterias.getString("Materia");
                        
                        // Crear una tabla por cada materia y añadirla al tabbedPane
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

        // Crear la ventana para mostrar todas las tablas
        JFrame frame = new JFrame("Alumnos Inscritos por Materia");
        frame.add(tabbedPane);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private JTable crearTablaPorMateria(String materia) {
        String[] columnNames = {"Matrícula", "Nombre", "Apellido", "Asistencia"};
        DefaultTableModel modelo = new DefaultTableModel(columnNames, 0);

        try (Connection conn = ConexionSQLite.conectar()) {
            // Consulta para obtener los alumnos inscritos en la materia junto con su asistencia
            String sql = """
                SELECT ua.Matricula, ua.Nombre, ua.Apellido, COALESCE(ac.Asistencia, 0) AS Asistencia
                FROM UsuariosAlumnos ua
                LEFT JOIN AsistenciaCentroDeComputo ac
                ON ua.Matricula = ac.Matricula
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
                        int asistencia = rs.getInt("Asistencia");

                        modelo.addRow(new Object[]{matricula, nombre, apellido, asistencia});
                    }

                    if (!tieneAlumnos) {
                        JOptionPane.showMessageDialog(null,
                                "No se encontraron alumnos inscritos para la materia: " + materia,
                                "Información", JOptionPane.INFORMATION_MESSAGE);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar los datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        JTable tabla = new JTable(modelo);

        // Agregar botón para actualizar asistencias
        tabla.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        tabla.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 3) { // Columna de asistencia
                    String matricula = (String) modelo.getValueAt(row, 0);
                    boolean nuevaAsistencia = (boolean) modelo.getValueAt(row, 3);
                    actualizarAsistenciaEnBD(matricula, nuevaAsistencia ? 1 : 0);
                }
            }
        });

        return tabla;
    }
    private void actualizarAsistenciaEnBD(String matricula, int asistencia) {
        try (Connection conn = ConexionSQLite.conectar()) {
            String sql = """
                UPDATE AsistenciaCentroDeComputo
                SET Asistencia = ?
                WHERE Matricula = ?;
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, asistencia);
                pstmt.setString(2, matricula);

                int filasActualizadas = pstmt.executeUpdate();
                if (filasActualizadas == 0) {
                    // Si no se encontró la matrícula, agregar un registro nuevo
                    String sqlInsert = """
                        INSERT INTO AsistenciaCentroDeComputo (Nombre, Apellido, Carrera, Matricula, Proposito, Horas, FechaHora, Asistencia)
                        SELECT Nombre, Apellido, '', Matricula, '', 0, DATETIME('now'), ?
                        FROM UsuariosAlumnos
                        WHERE Matricula = ?;
                        """;
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                        pstmtInsert.setInt(1, asistencia);
                        pstmtInsert.setString(2, matricula);
                        pstmtInsert.executeUpdate();
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "Asistencia actualizada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar asistencia: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }}
}

