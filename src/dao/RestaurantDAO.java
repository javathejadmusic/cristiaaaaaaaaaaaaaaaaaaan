/*
 *  Clase que se encarga de la persistencia en la BBDD
 *  Recibe y devuelve objetos
 */
package dao;

import excepciones.ExcepcionRestaurante;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Cocinero;
import modelo.Plato;
import modelo.RankingCocineroTO;

/**
 *
 * @author mfontana
 */
public class RestaurantDAO {

    Connection conexion;

    // ********************* Selects ****************************
    // Rankig de concineros
    public List<RankingCocineroTO> rankingCocineros() throws SQLException {
        String query = "select cocinero, count(*) as contador from plato group by cocinero order by contador desc;";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(query);
        List<RankingCocineroTO> ranking = new ArrayList<>();
        while (rs.next()) {
            RankingCocineroTO r = new RankingCocineroTO(rs.getString("cocinero"), rs.getInt("contador"));
            ranking.add(r);
        }
        rs.close();
        st.close();
        return ranking;
    }

    public List<Cocinero> selectAllCocineros() throws SQLException {
        String query = "select * from cocinero";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(query);
        List<Cocinero> cocineros = new ArrayList<>();
        while (rs.next()) {
            Cocinero c = new Cocinero();
            c.setNombre(rs.getString("nombre"));
            c.setEdad(rs.getInt("edad"));
            c.setEspecialidad(rs.getString("especialidad"));
            c.setExperiencia(rs.getInt("experiencia"));
            c.setSexo(rs.getString("sexo"));
            c.setTelefono(rs.getString("telefono"));
            cocineros.add(c);
        }
        rs.close();
        st.close();
        return cocineros;
    }

    // Función que devuelve un plato a partir del nombre
    public Plato getPlatoByNombre(String nombre) throws SQLException, ExcepcionRestaurante {
        Plato aux = new Plato(nombre);
        if (!existePlato(aux)) {
            throw new ExcepcionRestaurante("ERROR: No existe ningún plato con ese nombre");
        }
        String select = "select * from plato where nombre='" + nombre + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(select);
        Plato p = new Plato();
        if (rs.next()) {
            p.setNombre(nombre);
            p.setPrecio(rs.getDouble("precio"));
            p.setTipo(rs.getString("tipo"));
            p.setCocinero(getCocineroByNombre(rs.getString("cocinero")));
        }
        rs.close();
        st.close();
        return p;
    }

    // Función que devuelve un cocinero a partir del nombre
    public Cocinero getCocineroByNombre(String nombre) throws SQLException, ExcepcionRestaurante {
        Cocinero aux = new Cocinero(nombre);
        if (!existeCocinero(aux)) {
            throw new ExcepcionRestaurante("ERROR: No existe ningún cocinero con ese nombre");
        }
        String select = "select * from cocinero where nombre='" + nombre + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(select);
        Cocinero c = new Cocinero();
        if (rs.next()) {
            c.setNombre(rs.getString("nombre"));
            c.setEdad(rs.getInt("edad"));
            c.setEspecialidad(rs.getString("especialidad"));
            c.setExperiencia(rs.getInt("experiencia"));
            c.setSexo(rs.getString("sexo"));
            c.setTelefono(rs.getString("telefono"));
        }
        rs.close();
        st.close();
        return c;
    }

    // ********************* Updates ****************************
    // Función que sube la experiencia de un cocinero y el precio de un plato
    public void modificarExpCociPrecioPlato(Cocinero c, Plato p) throws SQLException, ExcepcionRestaurante {
        Statement st = conexion.createStatement();
        try {
            // Desactivamos autocommit para poder hacer la transacción
            conexion.setAutoCommit(false);
            String updateExp = "update cocinero set experiencia=experiencia +1 where nombre='" + c.getNombre() + "'";
            String updatePre = "update plato set precio=precio+1 where nombre='" + p.getNombre() + "'";
            st.executeUpdate(updateExp);
            st.executeUpdate(updatePre);
            conexion.commit();
        } catch (SQLException ex) {
            conexion.rollback();
            throw new ExcepcionRestaurante("No se ha podido actualizar los datos");
        } finally {
            st.close();
            conexion.setAutoCommit(true);
        }
    }

    public void modificarExperienciaCocinero(Cocinero c) throws SQLException, ExcepcionRestaurante {
        if (!existeCocinero(c)) {
            throw new ExcepcionRestaurante("ERROR: No existe un cocinero con ese nombre");
        }
        String update = "update cocinero set experiencia=? where nombre=?";
        PreparedStatement ps = conexion.prepareStatement(update);
        ps.setInt(1, c.getExperiencia());
        ps.setString(2, c.getNombre());
        ps.executeUpdate();
        ps.close();
    }

    // ********************* Deletes ****************************
    // Función que borra un cocinero
    public void borrarCocinero(Cocinero c) throws SQLException, ExcepcionRestaurante {
        if (!existeCocinero(c)) {
            throw new ExcepcionRestaurante("ERROR: No existe un cocinero con ese nombre");
        }
        String delete = "delete from cocinero where nombre='" + c.getNombre() + "'";
        Statement st = conexion.createStatement();
        st.executeUpdate(delete);
        st.close();
    }

    // Función que borra un plato
    public void borrarPlato(Plato p) throws SQLException, ExcepcionRestaurante {
        if (!existePlato(p)) {
            throw new ExcepcionRestaurante("ERROR: No existe un plato con ese nombre");
        }
        String delete = "delete from plato where nombre='" + p.getNombre() + "'";
        Statement st = conexion.createStatement();
        st.executeUpdate(delete);
        st.close();
    }

    // ********************* Inserts ****************************
    public void insertarPlato(Plato p) throws ExcepcionRestaurante, SQLException {
        if (existePlato(p)) {
            throw new ExcepcionRestaurante("ERROR: Ya existe un plato con ese nombre");
        }
        if (!existeCocinero(p.getCocinero())) {
            throw new ExcepcionRestaurante("ERROR: No existe el cocinero del plato");

        }
        String insert = "insert into plato values (?, ?, ?, ?)";
        PreparedStatement ps = conexion.prepareStatement(insert);
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getTipo());
        ps.setDouble(3, p.getPrecio());
        ps.setString(4, p.getCocinero().getNombre());
        ps.executeUpdate();
        ps.close();
    }

    public void insertarCocinero(Cocinero c) throws SQLException, ExcepcionRestaurante {
        if (existeCocinero(c)) {
            throw new ExcepcionRestaurante("ERROR: Ya existe un cocinero con ese nombre");
        }
        String insert = "insert into cocinero values (?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conexion.prepareStatement(insert);
        ps.setString(1, c.getNombre());
        ps.setString(2, c.getTelefono());
        ps.setString(3, c.getSexo());
        ps.setInt(4, c.getEdad());
        ps.setInt(5, c.getExperiencia());
        ps.setString(6, c.getEspecialidad());
        ps.executeUpdate();
        ps.close();
    }

    // ********************* Funciones auxiliares ****************************
    private boolean existePlato(Plato p) throws SQLException {
        String select = "select * from plato where nombre='" + p.getNombre() + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(select);
        boolean existe = false;
        if (rs.next()) {
            existe = true;
        }
        rs.close();
        st.close();
        return existe;
    }

    private boolean existeCocinero(Cocinero c) throws SQLException {
        String select = "select * from cocinero where nombre='" + c.getNombre() + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(select);
        boolean existe = false;
        if (rs.next()) {
            existe = true;
        }
        rs.close();
        st.close();
        return existe;
    }

    // ********************* Conectar / Desconectar ****************************
    public void conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/restaurant";
        String user = "root";
        String pass = "root";
        conexion = DriverManager.getConnection(url, user, pass);
    }

    public void desconectar() throws SQLException {
        if (conexion != null) {
            conexion.close();
        }
    }

}
