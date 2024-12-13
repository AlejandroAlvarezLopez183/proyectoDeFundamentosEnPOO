package ProyectoFundamentosEnPOO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaRegistroAlSalonDeComputo extends JFrame {

    private JFrame ventanaPrincipal; // Referencia a la ventana principal

    public VentanaRegistroAlSalonDeComputo(JFrame ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal; // Guardar la referencia

        // Configuración de la ventana
        setTitle("Registro al Salón de Cómputo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20)); // Usar FlowLayout

        // Botón para registro de alumnos no inscritos
        JButton btnNoInscritos = new JButton("Registro de alumnos no inscritos al centro de cómputo");
        btnNoInscritos.setPreferredSize(null); // Dejar que el tamaño dependa del texto
        btnNoInscritos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear y mostrar la ventana de asistencia (enlazada a la clase asistenciasAlCentroDeComputo)
                asistenciasAlCentroDeComputo ventanaAsistencia = new asistenciasAlCentroDeComputo();
                ventanaAsistencia.setVisible(true);
                // No cerrar la ventana principal
            }
        });

        // Botón para alumnos inscritos a una clase
        JButton btnInscritos = new JButton("Alumnos inscritos a una clase del centro de cómputo");
        btnInscritos.setPreferredSize(null); // Dejar que el tamaño dependa del texto
        btnInscritos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear y mostrar la ventana de asistencia a clases
                asistenciaClases ventanaAsistenciaAClase = new asistenciaClases();
                ventanaAsistenciaAClase.setVisible(true);
                // No cerrar la ventana principal
            }
        });

        // Botón "Atrás"
        JButton btnAtras = new JButton("Atrás");
        btnAtras.setPreferredSize(null); // Dejar que el tamaño dependa del texto
        btnAtras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volver a la ventana principal
                ventanaPrincipal.setVisible(true); // Mostrar la ventana principal
                dispose(); // Cerrar esta ventana
            }
        });

        // Agregar los botones a la ventana
        add(btnNoInscritos);
        add(btnInscritos);
        add(btnAtras); // Agregar el botón "Atrás"
    }
}
