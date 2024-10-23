package es.udc.ws.app.model.Partido;

import java.sql.*;

public class Jbdc3CcSqlPartidoDao extends AbstractSqlPartidoDao{
    /**
     * [FUNC-1] Persiste un partido a la Base de Datos. El método devuelve un objeto Partido con el atributo partidoID inicializado al valor correcto.
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param partido Datos del partido a añadir a la base de datos
     * @return Partido con el ID asignado por la base de datos
     * */
    @Override
    public Partido create(Connection connection, Partido partido) {
        String queryString = "INSERT INTO Partido"
                + " (nombreVisitante, fechaInicio, fechaAlta, maximoEntradas, entradasVendidas, precio)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, partido.getNombreVisitante());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(partido.getFechaInicio()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(partido.getFechaAlta()));
            preparedStatement.setInt(4, partido.getMaximoEntradas());
            preparedStatement.setInt(5, partido.getEntradasVendidas());
            preparedStatement.setDouble(6, partido.getPrecio());

            // Ejecutamos la consulta en la base de datos
            if(preparedStatement.executeUpdate() == 0)
                throw new SQLException("No rows were added");

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            // Comprobamos que la ejecución de la sentencia retorna algún valor generado
            if (!resultSet.next())
                throw new SQLException("JDBC driver did not return generated key.");

            // Retorna el partido en el ID generado en la base de datos
            return new Partido(resultSet.getLong(1),    // ID del Partido en la base de datos
                    partido.getNombreVisitante(),       // Nombre Visitante
                    partido.getFechaInicio(),           // Fecha Inicio
                    partido.getFechaAlta(),             // Fecha Alta
                    partido.getMaximoEntradas(),        // Maximo Entradas
                    partido.getEntradasVendidas(),      // Entradas Vendidas
                    partido.getPrecio()                 // Precio
            );

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
