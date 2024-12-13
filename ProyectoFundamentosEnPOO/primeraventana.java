package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;

public class primeraventana {

    static JFrame frame; // Hacer el frame accesible desde otras clases

    public static void main(String[] args) {
    	 new generadorDeQR();
        frame = new JFrame("Sistema de Control Del Centro De Computo");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Rutas de la imagen de fondo y logos
        ImageIcon imagenFondo = new ImageIcon("src/ProyectoFundamentosEnPOO/centro.jpg");
        ImageIcon logoIzquierdoIcon = new ImageIcon("src/ProyectoFundamentosEnPOO/fiec-logo.png");
        ImageIcon logoDerechoIcon = new ImageIcon("src/ProyectoFundamentosEnPOO/logo-uv.jpg");

        // Crear un JPanel para dividir en dos mitades
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2)); // Dividir en dos columnas

        // Crear un JPanel personalizado para la mitad izquierda con imagen de fondo
        JPanel fondoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        fondoPanel.setLayout(new BorderLayout());

        // Añadir el fondoPanel al mainPanel
        mainPanel.add(fondoPanel);

        // Crear la mitad derecha para los botones y elementos
        JPanel derechaPanel = new JPanel();
        derechaPanel.setLayout(new BorderLayout());
        derechaPanel.setBackground(Color.WHITE);

        // Panel superior para los logos y el título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Panel de logos
        JPanel logosPanel = new JPanel();
        logosPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Alinear a la derecha
        logosPanel.setOpaque(false);

        JLabel logoIzquierdo = new JLabel(new ImageIcon(logoIzquierdoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        JLabel logoDerecho = new JLabel(new ImageIcon(logoDerechoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        logosPanel.add(logoIzquierdo);
        logosPanel.add(Box.createHorizontalStrut(10)); // Espacio entre logos
        logosPanel.add(logoDerecho);

        // Añadir los logos al topPanel
        topPanel.add(logosPanel, BorderLayout.NORTH);

        // Título debajo de los logos
        JLabel titulo = new JLabel("Sistema de Control Del Centro De Computo", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 19));
        titulo.setForeground(new Color(0, 102, 204)); // Color azul personalizado
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Espaciado superior e inferior
        topPanel.add(titulo, BorderLayout.CENTER);

        // Añadir el topPanel al derechaPanel
        derechaPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central para los botones
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Botones del menú
        JButton boton1 = new JButton("Inscripción de usuarios pertenecientes a una clase");
        JButton boton2 = new JButton("Registro al centro de cómputo");
        JButton boton3 = new JButton("Administrador");
        JButton boton4 = new JButton("Verificación de asistencias de los alumnos en clase");

        // Personalizar tamaño de botones
        Dimension botonDimension = new Dimension(350, 40);
        JButton[] botones = {boton1, boton2, boton3, boton4};
        for (JButton boton : botones) {
            boton.setMaximumSize(botonDimension);
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelCentral.add(Box.createVerticalStrut(15)); // Espacio entre botones
            panelCentral.add(boton);
        }

        // Acciones de los botones
        boton1.addActionListener(e -> {
            frame.setVisible(false); // Ocultar ventana principal
            new registro(frame); // Abrir la ventana de registro
        });

        boton2.addActionListener(e -> {
            frame.setVisible(false); // Ocultar la ventana principal
            new VentanaRegistroAlSalonDeComputo(frame).setVisible(true); // Pasar la referencia de la ventana principal
        });

        boton3.addActionListener(e -> {
            frame.setVisible(false); // Ocultar ventana principal
            new administrador(frame); // Llamar a la interfaz del administrador
        });

        boton4.addActionListener(e -> {
            // Llamar a la funcionalidad de verificación de asistencias
            verificacionDeasistenciasAClases verificacion = new verificacionDeasistenciasAClases();
            verificacion.verificacionDeasistenciasAClases(frame);
        });

        // Añadir el panel de botones al derechaPanel
        derechaPanel.add(panelCentral, BorderLayout.CENTER);

        // Panel inferior para el pie de página
        JPanel piePanel = new JPanel();
        piePanel.setOpaque(false);
        JLabel footer = new JLabel("Centro de Computo - Universidad Veracruzana © 2024");
        piePanel.add(footer);

        derechaPanel.add(piePanel, BorderLayout.SOUTH);

        // Añadir el derechaPanel al mainPanel
        mainPanel.add(derechaPanel);

        // Añadir el mainPanel al frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
