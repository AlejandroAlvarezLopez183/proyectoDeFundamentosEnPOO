package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;

public class registro {

    // Contraseña del administrador
    private final String ADMIN_PASSWORD = "admin123"; // Puedes cambiar esta contraseña

    public registro(JFrame menuPrincipal) {
        JFrame frameRegistro = new JFrame("Registro de Usuarios");
        frameRegistro.setSize(600, 400);
        frameRegistro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameRegistro.setLayout(new BorderLayout());

        // Panel central para los botones de registro
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botones de registro
        JButton botonEstudiantes = new JButton("Inscripcion para Estudiantes pertenecientes a una clase");
        JButton botonMaestros = new JButton(" Inscripión para Maestros");

        botonEstudiantes.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonMaestros.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(botonEstudiantes);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(botonMaestros);

        // Panel inferior para el botón "Atrás"
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonAtras = new JButton("Atrás");
        panelInferior.add(botonAtras);

        // Acción del botón "Atrás"
        botonAtras.addActionListener(e -> {
            frameRegistro.dispose(); // Cerrar ventana de registro
            menuPrincipal.setVisible(true); // Mostrar ventana principal
        });

        // Acción del botón "Registro para Estudiantes"
        botonEstudiantes.addActionListener(e -> {
            frameRegistro.dispose();
            new alumnos(frameRegistro);
        });

     // Acción del botón "Registro para Maestros"
        botonMaestros.addActionListener(e -> {
            // Pedir la contraseña del administrador
            String password = JOptionPane.showInputDialog(frameRegistro, "Ingrese la contraseña del administrador:");

            // Validar si el usuario canceló la entrada
            if (password == null) {
                return; // No hacer nada si se cancela
            }

            // Validar la contraseña
            if (ADMIN_PASSWORD.equals(password)) {
                frameRegistro.dispose();
                new maestros(frameRegistro); // Llamar a la clase maestros si la contraseña es correcta
            } else {
                JOptionPane.showMessageDialog(frameRegistro, "Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Añadir los paneles a la ventana de registro
        frameRegistro.add(panelCentral, BorderLayout.CENTER);
        frameRegistro.add(panelInferior, BorderLayout.SOUTH);

        frameRegistro.setVisible(true);
    }
}
