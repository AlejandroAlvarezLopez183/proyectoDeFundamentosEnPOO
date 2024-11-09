package ProyectoFundamentosEnPOO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class maestros {

    public maestros(JFrame ventanaRegistro) {
        JFrame frameMaestros = new JFrame("Registro de Maestros");
        frameMaestros.setSize(600, 400);
        frameMaestros.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMaestros.setLayout(new BorderLayout());

        // Panel central para los campos del formulario
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campos de formulario
        JTextField campoNombre = new JTextField();
        JTextField campoApellidos = new JTextField();

        // Etiquetas y campos
        panelCentral.add(new JLabel("Nombre:"));
        panelCentral.add(campoNombre);
        panelCentral.add(Box.createVerticalStrut(10));

        panelCentral.add(new JLabel("Apellidos:"));
        panelCentral.add(campoApellidos);
        panelCentral.add(Box.createVerticalStrut(10));

        // Tabla para clases y horarios
        String[] columnas = {"Día", "Clase", "Horario"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaHorarios = new JTable(modeloTabla);

        // Rellenar tabla con días de la semana
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        for (String dia : diasSemana) {
            modeloTabla.addRow(new Object[]{dia, "", ""});
        }

        JScrollPane scrollTabla = new JScrollPane(tablaHorarios);
        panelCentral.add(scrollTabla);

        // Panel inferior para botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonGuardar = new JButton("Guardar");
        JButton botonAtras = new JButton("Atrás");

        panelInferior.add(botonGuardar);
        panelInferior.add(botonAtras);

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> {
            frameMaestros.dispose();
            ventanaRegistro.setVisible(true);
        });

        // Añadir paneles a la ventana
        frameMaestros.add(panelCentral, BorderLayout.CENTER);
        frameMaestros.add(panelInferior, BorderLayout.SOUTH);

        frameMaestros.setVisible(true);
    }
}
