package ProyectoFundamentosEnPOO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {
    public static Connection conectar() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:src/ProyectoFundamentosEnPOO/ProyectoProgramacion.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Conexión a SQLite establecida.");
            
            // Crear las tablas si no existen
            crearTablaUsuariosAlumnos(conn);
            crearTablaUsuariosMaestros(conn);
            crearTablaAsistenciaCentroDeComputo(conn); // Nueva tabla
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    private static void crearTablaUsuariosAlumnos(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS UsuariosAlumnos (
                Matricula TEXT NOT NULL UNIQUE,
                Nombre TEXT NOT NULL,
                Apellido TEXT NOT NULL,
                Materias TEXT NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla UsuariosAlumnos verificada/creada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear/verificar la tabla UsuariosAlumnos: " + e.getMessage());
        }
    }

    private static void crearTablaUsuariosMaestros(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS UsuariosMaestros (
                IDMaestro INTEGER,
                Nombre TEXT NOT NULL,
                Apellido TEXT NOT NULL,
                Materia TEXT NOT NULL,
                Horario TEXT NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla UsuariosMaestros verificada/creada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear/verificar la tabla UsuariosMaestros: " + e.getMessage());
        }
    }

    private static void crearTablaAsistenciaCentroDeComputo(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS AsistenciaCentroDeComputo (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL,
                Apellido TEXT NOT NULL,
                Carrera TEXT NOT NULL,
                Matricula TEXT NOT NULL,
                Proposito TEXT NOT NULL,
                Horas INTEGER NOT NULL,
                FechaHora TEXT NOT NULL,
                 Asistencia INTEGER DEFAULT 0 
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla AsistenciaCentroDeComputo verificada/creada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear/verificar la tabla AsistenciaCentroDeComputo: " + e.getMessage());
        }
    }

}
