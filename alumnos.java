package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;

public class alumnos {

    public alumnos(JFrame ventanaRegistro) {
        JFrame frameAlumnos = new JFrame("Registro de Alumnos");
        frameAlumnos.setSize(400, 400);
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
        JTextField campoClasesInscritas = new JTextField();

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

        panelCentral.add(new JLabel("Clases Inscritas:"));
        panelCentral.add(campoClasesInscritas);
        panelCentral.add(Box.createVerticalStrut(10));

        // Botones "Guardar" y "Atrás"
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonGuardar = new JButton("Guardar");
        JButton botonAtras = new JButton("Atrás");

        panelInferior.add(botonGuardar);
        panelInferior.add(botonAtras);

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
}
