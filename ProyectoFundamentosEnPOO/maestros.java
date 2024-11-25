package ProyectoFundamentosEnPOO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class maestros {
    private final JPanel panelMaterias;
    private JTextField campoNombre, campoApellidos;

    public maestros(JFrame ventanaRegistro) {
        JFrame frameMaestros = new JFrame("Registro de Maestros");
        frameMaestros.setSize(600, 500);
        frameMaestros.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMaestros.setLayout(new BorderLayout());

        // Panel central para los campos del formulario
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campos de formulario
        campoNombre = new JTextField();
        campoApellidos = new JTextField();

        // Etiquetas y campos
        panelCentral.add(new JLabel("Nombre:"));
        panelCentral.add(campoNombre);
        panelCentral.add(Box.createVerticalStrut(10));

        panelCentral.add(new JLabel("Apellidos:"));
        panelCentral.add(campoApellidos);
        panelCentral.add(Box.createVerticalStrut(10));

        // Botón para añadir una nueva materia
        JButton botonAñadirMateria = new JButton("Añadir Materia");
        panelCentral.add(botonAñadirMateria);
        panelCentral.add(Box.createVerticalStrut(10));

        // Panel para mostrar las materias guardadas
        panelMaterias = new JPanel();
        panelMaterias.setLayout(new BoxLayout(panelMaterias, BoxLayout.Y_AXIS));

        JScrollPane scrollMaterias = new JScrollPane(panelMaterias);
        scrollMaterias.setPreferredSize(new Dimension(550, 200));

        // Panel inferior para botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonGuardar = new JButton("Guardar");
        JButton botonAtras = new JButton("Atrás");

        panelInferior.add(botonGuardar);
        panelInferior.add(botonAtras);

        // Acción del botón "Añadir Materia"
        botonAñadirMateria.addActionListener(e -> agregarNuevaMateria());

        // Acción del botón "Guardar"
        botonGuardar.addActionListener(e -> guardarDatosMaestro());

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> {
            frameMaestros.dispose();
            ventanaRegistro.setVisible(true);
        });

        // Añadir paneles a la ventana
        frameMaestros.add(panelCentral, BorderLayout.NORTH);
        frameMaestros.add(scrollMaterias, BorderLayout.CENTER);
        frameMaestros.add(panelInferior, BorderLayout.SOUTH);

        frameMaestros.setVisible(true);
    }

    private void agregarNuevaMateria() {
        JFrame nuevaMateriaFrame = new JFrame("Añadir Nueva Materia");
        nuevaMateriaFrame.setSize(700, 400);
        nuevaMateriaFrame.setLayout(new BorderLayout());

        // Panel para la nueva materia
        JPanel panelMateria = new JPanel();
        panelMateria.setLayout(new BoxLayout(panelMateria, BoxLayout.Y_AXIS));
        panelMateria.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo para el nombre de la materia
        JTextField campoNuevaMateria = new JTextField();
        panelMateria.add(new JLabel("Nombre de la Nueva Materia:"));
        panelMateria.add(campoNuevaMateria);
        panelMateria.add(Box.createVerticalStrut(10));

        // Crear checkboxes para los días de la semana
        JPanel panelDias = new JPanel();
        panelDias.setLayout(new FlowLayout(FlowLayout.LEFT));
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        JCheckBox[] checkBoxesDias = new JCheckBox[diasSemana.length];

        for (int i = 0; i < diasSemana.length; i++) {
            checkBoxesDias[i] = new JCheckBox(diasSemana[i]);
            panelDias.add(checkBoxesDias[i]);
        }
        panelMateria.add(new JLabel("Seleccione los días que dará clases:"));
        panelMateria.add(panelDias);

        // Crear tabla para horarios
        String[] columnas = {"Día", "Hora de Inicio", "Hora de Fin"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaHorarios = new JTable(modeloTabla);

        // Configurar editor y renderer para las columnas de horarios
        tablaHorarios.getColumnModel().getColumn(1).setCellEditor(new SpinnerCellEditor());
        tablaHorarios.getColumnModel().getColumn(2).setCellEditor(new SpinnerCellEditor());
        tablaHorarios.getColumnModel().getColumn(1).setCellRenderer(new SpinnerCellRenderer());
        tablaHorarios.getColumnModel().getColumn(2).setCellRenderer(new SpinnerCellRenderer());

        JScrollPane scrollTabla = new JScrollPane(tablaHorarios);
        panelMateria.add(scrollTabla);

        // Acción para actualizar la tabla según los días seleccionados
        for (JCheckBox checkBox : checkBoxesDias) {
            checkBox.addActionListener(e -> {
                String dia = checkBox.getText();

                if (checkBox.isSelected()) {
                    // Verificar que el día no esté ya en la tabla
                    boolean yaAgregado = false;
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        if (modeloTabla.getValueAt(i, 0).equals(dia)) {
                            yaAgregado = true;
                            break;
                        }
                    }
                    if (!yaAgregado) {
                        modeloTabla.addRow(new Object[]{dia, "08:00", "10:00"}); // Horarios iniciales por defecto
                    }
                } else {
                    // Eliminar el día de la tabla si se desmarca
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        if (modeloTabla.getValueAt(i, 0).equals(dia)) {
                            modeloTabla.removeRow(i);
                            break;
                        }
                    }
                }
            });
        }

        // Panel para el botón de guardar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botonGuardarMateria = new JButton("Guardar Nueva Materia");
        panelBoton.add(botonGuardarMateria);

        botonGuardarMateria.addActionListener(e -> {
            String nombreMateria = campoNuevaMateria.getText().trim();

            // Validar que el nombre de la materia no esté vacío
            if (nombreMateria.isEmpty()) {
                JOptionPane.showMessageDialog(nuevaMateriaFrame, "El nombre de la materia es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar conflictos de horario en la tabla
            for (int i = 0; i < tablaHorarios.getRowCount(); i++) {
                String dia = (String) modeloTabla.getValueAt(i, 0);
                String horaInicio = (String) modeloTabla.getValueAt(i, 1);
                String horaFin = (String) modeloTabla.getValueAt(i, 2);

                // Validación de conflicto de horario
                if (validarHorarioEnBaseDeDatos(dia, horaInicio, horaFin)) {
                    JOptionPane.showMessageDialog(nuevaMateriaFrame,
                        "Conflicto de horario detectado en el día " + dia + ": " + horaInicio + " - " + horaFin,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Si todo es válido, construir los horarios para guardarlos
            StringBuilder horarios = new StringBuilder();
            for (int i = 0; i < tablaHorarios.getRowCount(); i++) {
                String dia = (String) modeloTabla.getValueAt(i, 0);
                String horaInicio = (String) modeloTabla.getValueAt(i, 1);
                String horaFin = (String) modeloTabla.getValueAt(i, 2);
                horarios.append(dia).append(": ").append(horaInicio).append(" - ").append(horaFin).append("; ");
            }

            // Guardar la materia
            agregarPanelMiniMateria(nombreMateria, horarios.toString());
            nuevaMateriaFrame.dispose();
        });

        nuevaMateriaFrame.add(panelMateria, BorderLayout.CENTER);
        nuevaMateriaFrame.add(panelBoton, BorderLayout.SOUTH);
        nuevaMateriaFrame.setVisible(true);
    }

    
    // Clase para manejar los JSpinner como editor en la tabla
    class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner;

        public SpinnerCellEditor() {
            spinner = new JSpinner(new SpinnerDateModel());
            spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        }

        @Override
        public Object getCellEditorValue() {
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
            return formatoHora.format(spinner.getValue());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            try {
                SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
                spinner.setValue(formatoHora.parse((String) value));
            } catch (Exception e) {
                spinner.setValue(new Date());
            }
            return spinner;
        }
    }

    // Clase para mostrar los valores correctamente formateados en la tabla
    class SpinnerCellRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            setText(value != null ? value.toString() : "");
        }
    }

    private void agregarPanelMiniMateria(String nombreMateria, String horarios) {
        JPanel panelMiniMateria = new JPanel();
        panelMiniMateria.setLayout(new BoxLayout(panelMiniMateria, BoxLayout.Y_AXIS));
        panelMiniMateria.setBorder(BorderFactory.createTitledBorder(nombreMateria));

        JLabel labelHorarios = new JLabel(horarios);
        panelMiniMateria.add(labelHorarios);

        // Botón para eliminar materia
        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.addActionListener(e -> {
            // Confirmación para eliminar la materia
            int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Estás seguro de eliminar la materia \"" + nombreMateria + "\"?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                panelMaterias.remove(panelMiniMateria);
                panelMaterias.revalidate();
                panelMaterias.repaint();
            }
        });
        panelMiniMateria.add(botonEliminar);

        panelMaterias.add(panelMiniMateria);
        panelMaterias.revalidate();
        panelMaterias.repaint();
    }

    private boolean validarHorarioEnBaseDeDatos(String nuevoDia, String nuevaHoraInicio, String nuevaHoraFin) {
        String query = "SELECT Horario FROM UsuariosMaestros";  // Busca en todas las materias
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String horariosExistentes = rs.getString("Horario");
                String[] horarios = horariosExistentes.split("; "); // Suponiendo que los horarios están separados por "; "

                for (String horario : horarios) {
                    String[] partes = horario.split(": ");
                    String diaExistente = partes[0];
                    String[] horas = partes[1].split(" - ");
                    String horaInicioExistente = horas[0];
                    String horaFinExistente = horas[1];

                    // Verificar si el día es el mismo
                    if (diaExistente.equals(nuevoDia)) {
                        // Validar conflicto con el nuevo horario
                        if (hayConflictoDeHorario(horaInicioExistente, horaFinExistente, nuevaHoraInicio, nuevaHoraFin)) {
                            return true; // Hay un conflicto
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // No hay conflicto
    }


    private boolean hayConflictoDeHorario(String horaInicioExistente, String horaFinExistente, String nuevaHoraInicio, String nuevaHoraFin) {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

        try {
            Date horaInicioExistenteDate = formatoHora.parse(horaInicioExistente);
            Date horaFinExistenteDate = formatoHora.parse(horaFinExistente);
            Date nuevaHoraInicioDate = formatoHora.parse(nuevaHoraInicio);
            Date nuevaHoraFinDate = formatoHora.parse(nuevaHoraFin);

            long inicioExistente = horaInicioExistenteDate.getTime() / 60000;
            long finExistente = horaFinExistenteDate.getTime() / 60000;
            long inicioNuevo = nuevaHoraInicioDate.getTime() / 60000;
            long finNuevo = nuevaHoraFinDate.getTime() / 60000;

            // Verificar si las horas se solapan
            if ((inicioNuevo < finExistente && finNuevo > inicioExistente)) {
                // Solapamiento directo
                return true;
            }

            // Verificar si alguna de las horas intermedias está ocupada
            for (long i = inicioNuevo; i < finNuevo; i++) {
                // Si cualquier hora intermedia ya está ocupada, hay un conflicto
                if (i >= inicioExistente && i < finExistente) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private boolean horasSeSolapan(String inicio1, String fin1, String inicio2, String fin2) {
        // Convertir las horas en formato HH:mm a minutos para facilitar la comparación
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

        try {
            Date horaInicio1 = formatoHora.parse(inicio1);
            Date horaFin1 = formatoHora.parse(fin1);
            Date horaInicio2 = formatoHora.parse(inicio2);
            Date horaFin2 = formatoHora.parse(fin2);

            long inicio1EnMinutos = horaInicio1.getTime() / 60000; // Convertir a minutos
            long fin1EnMinutos = horaFin1.getTime() / 60000; // Convertir a minutos
            long inicio2EnMinutos = horaInicio2.getTime() / 60000; // Convertir a minutos
            long fin2EnMinutos = horaFin2.getTime() / 60000; // Convertir a minutos

            // Verificar si hay solapamiento
            return (inicio2EnMinutos < fin1EnMinutos && fin2EnMinutos > inicio1EnMinutos)
                    || (inicio2EnMinutos < fin1EnMinutos && fin2EnMinutos >= fin1EnMinutos) // Caso cuando el horario se solapa y cubre el horario existente
                    || (inicio2EnMinutos <= inicio1EnMinutos && fin2EnMinutos >= fin1EnMinutos); // Caso cuando el horario nuevo cubre el horario existente
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // No hay solapamiento si ocurre algún error
    }

    private int obtenerProximoIdMaestro() {
        int proximoId = 1;
        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM UsuariosMaestros")) {

            if (rs.next()) {
                proximoId = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proximoId;
    }

    private void guardarDatosMaestro() {
        String nombre = campoNombre.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        int idMaestro = generarIdAleatorio();
        if (idMaestro == -1) {
            JOptionPane.showMessageDialog(null, "Error al generar el ID del maestro.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (nombre.isEmpty() || apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre y apellidos del maestro son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (panelMaterias.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(null, "Debe añadir al menos una materia con horario válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO UsuariosMaestros (IDMaestro, Nombre, Apellido, Materia, Horario) VALUES (?, ?, ?, ?, ?)")) {

            for (Component c : panelMaterias.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel panelMateria = (JPanel) c;
                    String nombreMateria = ((TitledBorder) panelMateria.getBorder()).getTitle();
                    String horarios = ((JLabel) panelMateria.getComponent(0)).getText();

                    // Suponiendo que los horarios están en formato "Día: horaInicio - horaFin"
                    String[] partesHorario = horarios.split(": ");
                    String dia = partesHorario[0];
                    String[] horas = partesHorario[1].split(" - ");
                    String horaInicio = horas[0];
                    String horaFin = horas[1];

                    // Validar horarios antes de guardar
                    if (validarHorarioEnBaseDeDatos(dia, horaInicio, horaFin)) {
                        JOptionPane.showMessageDialog(null, "Conflicto de horario para la materia " + nombreMateria, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    pstmt.setInt(1, idMaestro);
                    pstmt.setString(2, nombre);
                    pstmt.setString(3, apellidos);
                    pstmt.setString(4, nombreMateria);
                    pstmt.setString(5, horarios);
                    pstmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Datos guardados correctamente.\nEl ID del maestro es: " + idMaestro,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
            campoNombre.setText("");
            campoApellidos.setText("");
            panelMaterias.removeAll();
            panelMaterias.revalidate();
            panelMaterias.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private int generarIdAleatorio() {
        int idMaestro = -1;
        try (Connection conn = ConexionSQLite.conectar()) {
            boolean idUnico = false;
            while (!idUnico) {
                idMaestro = (int) (Math.random() * 90000) + 10000; // Generar número entre 10000 y 99999

                String query = "SELECT COUNT(*) FROM UsuariosMaestros WHERE IDMaestro = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, idMaestro);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            idUnico = true; // El ID no existe en la base de datos
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indica error al generar el ID
        }
        return idMaestro;
    }

}


