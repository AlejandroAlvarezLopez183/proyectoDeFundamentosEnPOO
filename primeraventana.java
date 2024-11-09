package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;

public class primeraventana {

    static JFrame frame; // Hacer el frame accesible desde otras clases

    public static void main(String[] args) {
        frame = new JFrame("Sistema de Control Del Centro De Computo");
        frame.setSize(800, 600);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Rutas de la imagen de fondo y logos
        ImageIcon imagenFondo = new ImageIcon("src/ProyectoFundamentosEnPOO/centro.jpg");
        ImageIcon logoIzquierdoIcon = new ImageIcon("src/ProyectoFundamentosEnPOO/fiec-logo.png");
        ImageIcon logoDerechoIcon = new ImageIcon("src/ProyectoFundamentosEnPOO/logo-uv.jpg");

        // Crear un JPanel personalizado para el fondo
        JPanel fondoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        fondoPanel.setLayout(new BorderLayout(10, 10));

        // Panel superior para el nombre del programa y logos
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel logoIzquierdo = new JLabel(new ImageIcon(logoIzquierdoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        topPanel.add(logoIzquierdo, BorderLayout.WEST);

        JLabel titulo = new JLabel("Sistema de Control Del Centro De Computo", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titulo, BorderLayout.CENTER);

        JLabel logoDerecho = new JLabel(new ImageIcon(logoDerechoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        topPanel.add(logoDerecho, BorderLayout.EAST);

        fondoPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central con botones
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botones del menú
        JButton boton1 = new JButton("Registro de nuevo usuario al Centro de Computo");
        JButton boton2 = new JButton("Asistencia al centro de computo (alumnos registrados)");
        JButton boton3 = new JButton("Administrador");
        JButton boton4 = new JButton("Verificación de asistencias de los alumnos en una clase (Acceso para maestros)");

        // Acciones de los botones
        boton1.addActionListener(e -> {
            frame.setVisible(false); // Ocultar ventana principal
            new registro(frame); // Abrir la ventana de registro
        });

        boton2.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Funcionalidad para registrar asistencia aún no implementada.");
        });

        boton3.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Funcionalidad de administrador aún no implementada.");
        });

        boton4.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Funcionalidad de verificación de asistencias aún no implementada.");
        });

        // Personalizar tamaño de botones
        Dimension botonDimension = new Dimension(800, 50);
        JButton[] botones = {boton1, boton2, boton3, boton4};
        for (JButton boton : botones) {
            boton.setMaximumSize(botonDimension);
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelCentral.add(Box.createVerticalStrut(10));
            panelCentral.add(boton);
        }

        fondoPanel.add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con pie de página
        JPanel botonPanel = new JPanel();
        botonPanel.setOpaque(false);
        JLabel footer = new JLabel("Centro de Computo - Universidad Veracruzana © 2024");
        botonPanel.add(footer);
        fondoPanel.add(botonPanel, BorderLayout.SOUTH);

        frame.setContentPane(fondoPanel);
        frame.setVisible(true);
    }
}

