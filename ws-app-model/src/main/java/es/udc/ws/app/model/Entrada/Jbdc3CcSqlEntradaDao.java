package es.udc.ws.app.model.Entrada;

import java.sql.*;

public class Jbdc3CcSqlEntradaDao extends AbstractSqlEntradaDao{
    /**
     * [FUNC-4] Persiste una inscripción a la BBDD. El método devuelve un objeto Inscription con sus atributos correctamente inicializados
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param entrada Entrada con los datos bases a añadir en la base de datos
     * @return Entrada generada en la base de datos con el ID dado por la misma base de datos
     * */
    @Override
    public Entrada create(Connection connection, Entrada entrada){
        String queryStr = "INSERT INTO Entrada"
             + "(email, numeroTarjeta, numeroEntradas, fechaCompra, estado, partidoID)"
             + "VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryStr, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, entrada.getEmail());
            preparedStatement.setString(2, entrada.getNumeroTarjeta());
            preparedStatement.setLong(3, entrada.getNumeroEntradas());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(entrada.getFechaCompra()));
            preparedStatement.setBoolean(5, entrada.getEstado());
            preparedStatement.setLong(6, entrada.getPartidoID());

            // Ejecutamos la consulta
            preparedStatement.executeUpdate();

            // Obtenemos el resultado por parte de la base de datos
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next())
                throw new SQLException("JDBC driver did not return generated key.");

            // Retornamos la entrada con el ID asignado por la base de datos
            return new Entrada(
                    resultSet.getLong(1),            // ID de la Entrada
                    entrada.getEmail(),             // Email
                    entrada.getNumeroTarjeta(),     // Número de la tarjeta
                    entrada.getNumeroEntradas(),    // Numero de entradas compradas
                    entrada.getFechaCompra(),       // Fecha de la compra
                    entrada.getEstado(),            // Estado de la entrada
                    entrada.getPartidoID()          // ID del Partido
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}