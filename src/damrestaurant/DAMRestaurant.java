/*
 * Clase para testear que nuestro programa funciona
 */
package damrestaurant;

import dao.RestaurantDAO;
import excepciones.ExcepcionRestaurante;
import java.sql.SQLException;
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
public class DAMRestaurant {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Necesitamos un objeto de tipo RestaurantDAO
        RestaurantDAO restaurantDAO = new RestaurantDAO();

        // Datos de test
        Cocinero c1 = new Cocinero("Maria Lapera", "666554433", "Mujer", 33, 6, "Postres");
        Cocinero c2 = new Cocinero("Karlos Arguiñano", "33333333", "Mujer", 23, 0, "Entrantes");
        Cocinero c3 = new Cocinero("Anonimo", "888444568", "Anonimo", 40, 10, "Platos principales");
        Cocinero c4 = new Cocinero("Cocinero Auxiliar", "999999999", "Auxiliar", 10, 10, "Auxiliar");
        Plato p1 = new Plato("Macedonia", "Postre", 4.20, c1);
        Plato p2 = new Plato("Huevos fritos con patatas", "Platos principales", 4.75, c2);
        Plato p3 = new Plato("Ensalada de la huerta", "Entrantes", 9.90, c3);

        // TEST:        
        System.out.println("************************************************************");
        System.out.println("Testeando conexión con la base de datos...");
        try {
            restaurantDAO.conectar();
            System.out.println("Establecida la conexión.");

            System.out.println("************************************************************");
            System.out.println("Testeando insert cocinero " + c4.getNombre());
            altaCocinero(restaurantDAO, c4);
            System.out.println("************************************************************");
            System.out.println("Testeando insert cocinero duplicado " + c4.getNombre());
            altaCocinero(restaurantDAO, c4);

            System.out.println("************************************************************");
            System.out.println("Testeando insert plato: " + p1.getNombre());
            altaPlato(restaurantDAO, p1);
            System.out.println("************************************************************");
            System.out.println("Testeando insert plato duplicado " + p1.getNombre());
            altaPlato(restaurantDAO, p1);
            System.out.println("************************************************************");
            System.out.println("Testeando insert plato con cocinero que no existe " + p3.getNombre());
            altaPlato(restaurantDAO, p3);

            System.out.println("************************************************************");
            System.out.println("Testeando modificar experiencia cocinero " + c4.getNombre());
            System.out.println("Datos actuales");
            System.out.println(c4);
            System.out.println("Estableciendo experiencia en 20 años");
            c4.setExperiencia(20);
            modificarCocinero(restaurantDAO, c4);
            System.out.println("************************************************************");
            System.out.println("Testeando modificar experiencia de un cocinero que no existe " + c3.getNombre());
            System.out.println("Datos actuales");
            System.out.println(c3);
            System.out.println("Estableciendo experiencia en 0 años");
            c3.setExperiencia(0);
            modificarCocinero(restaurantDAO, c3);

            System.out.println("************************************************************");
            System.out.println("Testeando listado de todos los cocineros");
            List<Cocinero> cocineros;
            cocineros = restaurantDAO.selectAllCocineros();
            if (cocineros.isEmpty()) {
                System.out.println("No hay cocineros todavía");
            } else {
                System.out.println("Listado de cocineros");
                for (Cocinero x : cocineros) {
                    System.out.println(x);
                }
            }

            System.out.println("************************************************************");
            System.out.println("Testeando getPlatoByNombre para Lentejas");
            platoByNombre(restaurantDAO, "Lentejas");
            System.out.println("************************************************************");
            System.out.println("Testeando getPlatoByNombre para Garbanzos");
            platoByNombre(restaurantDAO, "Garbanzos");
            
            System.out.println("************************************************************");
            System.out.println("Testeando Ranking de cocineros");
            List<RankingCocineroTO> ranking = restaurantDAO.rankingCocineros();
            for (RankingCocineroTO r : ranking) {
                System.out.println(r);
            }

            System.out.println("************************************************************");
            System.out.println("Testeando borrar cocinero " + c1.getNombre());
            borrarCocinero(restaurantDAO, c4);
            System.out.println("************************************************************");
            System.out.println("Testeando borrar cocinero que ya no existe " + c1.getNombre());
            borrarCocinero(restaurantDAO, c4);

            System.out.println("************************************************************");
            System.out.println("Testeando borrar plato " + p1.getNombre());
            borrarPlato(restaurantDAO, p1);
            System.out.println("************************************************************");
            System.out.println("Testeando borrar plato que ya no existe  " + p1.getNombre());
            borrarPlato(restaurantDAO, p1);

            System.out.println("************************************************************");
            System.out.println("Cerrando conexión con la base de datos");
            restaurantDAO.desconectar();
            System.out.println("Conexión cerrada.");
            System.out.println("************************************************************");
            System.out.println("Fin del programa.");
            
        } catch (SQLException ex) {
            System.out.println("Error SQL: " + ex.getMessage());
        }
    }

    private static void platoByNombre(RestaurantDAO restaurantDAO, String nombre) throws SQLException {
        Plato aux;
        try {
            aux = restaurantDAO.getPlatoByNombre(nombre);
            System.out.println(aux);
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void borrarPlato(RestaurantDAO restaurantDAO, Plato p) throws SQLException {
        try {
            restaurantDAO.borrarPlato(p);
            System.out.println("Plato borrado de la base de datos.");
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void borrarCocinero(RestaurantDAO restaurantDAO, Cocinero c) throws SQLException {
        try {
            restaurantDAO.borrarCocinero(c);
            System.out.println("Cocinero borrado de la base de datos");
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void modificarCocinero(RestaurantDAO restaurantDAO, Cocinero c) throws SQLException {
        try {
            restaurantDAO.modificarExperienciaCocinero(c);
            System.out.println("Experiencia modificada.");
            System.out.println("Obteniendo datos de la BBDD del cocinero " + c.getNombre() + " para comprobar la nueva experiencia");
            Cocinero aux = restaurantDAO.getCocineroByNombre(c.getNombre());
            System.out.println("Nuevos datos");
            System.out.println(aux);
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void altaPlato(RestaurantDAO restaurantDAO, Plato p) throws SQLException {
        try {
            restaurantDAO.insertarPlato(p);
            System.out.println("Plato dado de alta.");
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void altaCocinero(RestaurantDAO restaurantDAO, Cocinero c) throws SQLException {
        try {
            restaurantDAO.insertarCocinero(c);
            System.out.println("Cocinero dado de alta.");
        } catch (ExcepcionRestaurante ex) {
            System.out.println(ex.getMessage());
        }
    }

}
