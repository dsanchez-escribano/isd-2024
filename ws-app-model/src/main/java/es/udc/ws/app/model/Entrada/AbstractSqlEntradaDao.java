package es.udc.ws.app.model.Entrada;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEntradaDao implements SqlEntradaDao{
    /**
     * [FUNC-5] Busca una entrada por ID
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param entradaID ID de la entrada a buscar en la base de datos
     * @throws InstanceNotFoundException En caso de que no se encuentre la entradaa buscar
     * @return entrada buscada
     * */
    @Override
    public Entrada find(Connection connection, Long entradaID) throws InstanceNotFoundException {
        String queryStr = "SELECT email, numeroTarjeta, numeroEntradas, fechaCompra, estado, partidoID FROM Entrada WHERE entradaID = ?";
        
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryStr)){
            preparedStatement.setLong(1, entradaID);
            
            // Execute query
            ResultSet results = preparedStatement.executeQuery();

            // Comprobamos el retorno de la query
            if (!results.next())
                throw new InstanceNotFoundException(entradaID, Entrada.class.getName());
            
            // Retornamos los datos de la entrada hallada por la consulta
            return new Entrada(
                entradaID,                                  // ID de Entrada
                results.getString(1),                        // Email
                results.getString(2),                        // Número de Targeta
                results.getInt(3),                           // Número de entradas
                results.getTimestamp(4).toLocalDateTime(),   // Fecha de compra
                results.getBoolean(5),                       // Estado
                results.getLong(6)                           // ID de Partido
            );
        
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-5] Recuperar todas las inscripciones de un usuario
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param email Email con el que se compraron las entradas a buscar
     * @return Listado con las entradas encontradas o lista vacía en caso de que no se encuentren
     * */
    @Override
    public List<Entrada> findByEmail(Connection connection, String email) {
        String queryStr = "SELECT entradaID, numeroTarjeta, numeroEntradas, fechaCompra, estado, partidoID FROM Entrada WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryStr)) {
            preparedStatement.setString(1, email);

            // Ejecutamos la query
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Entrada> salesList = new ArrayList<>();

            // Añadimos a la lista de entradas, las entradas que se encontrasen en la base de datos
            while (resultSet.next()) {

                // Añadimos la entrada encontrada a la lista
                salesList.add(
                        new Entrada(
                                resultSet.getLong(1),                        // ID de la Entrada
                                email,                                      // email
                                resultSet.getString(2),                      // Numero de tarjeta
                                resultSet.getInt(3),                         // Numero de entradas
                                resultSet.getTimestamp(4).toLocalDateTime(), // Fecha de la compra
                                resultSet.getBoolean(5),                     // Estado
                                resultSet.getLong(6)                         // ID del partido
                        )
                );
            }

            // En caso de no encontrar ninguna entrada, devuelve una lista vacía
            return salesList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [VARIOS] Actualizar una inscripción
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param entrada Entrada a actualizar en la base de datos
     * @throws InstanceNotFoundException Excepción ocurrida en caso de que no pueda identificarse la entrada en la base de datos
     * @return Codigo exitoso en caso de haberse realizado con éxito, código erróneo en caso contrario
     * */
    @Override
    public int update(Connection connection, Entrada entrada) throws InstanceNotFoundException {
        String queryStr = "UPDATE Entrada " +
                "SET email = ?, numeroTarjeta = ?, numeroEntradas = ?," +
                "fechaCompra = ?, estado = ?, partidoID = ? WHERE entradaID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryStr)){
            preparedStatement.setString(1, entrada.getEmail());
            preparedStatement.setString(2, entrada.getNumeroTarjeta());
            preparedStatement.setInt(3, entrada.getNumeroEntradas());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(entrada.getFechaCompra()));
            preparedStatement.setBoolean(5, entrada.getEstado());
            preparedStatement.setLong(6, entrada.getPartidoID());

            preparedStatement.setLong(7, entrada.getEntradaID());

            // Obtenemos el resultado de la consulta
            int updateRows = preparedStatement.executeUpdate();

            // En caso de que no se obtengan datos actualizados,
            // significa que hubo un error en la actualizacion de los datos
            if(updateRows == 0)
                throw new InstanceNotFoundException(entrada.getEntradaID(), Entrada.class.getName());

            // Retornamos un codigo exitoso
            return updateRows;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [VARIOS] Borrar una inscripción según su ID
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param entradaID ID de la entrada a eliminar
     * @throws InstanceNotFoundException Excepción que se lanza en caso de que no existiese dicha entrada en la base de datos
     * @return Codigo exitoso en caso de haberse realizado con éxito, código erróneo en caso contrario
     * */
    @Override
    public int remove(Connection connection, Long entradaID) throws InstanceNotFoundException {
        String queryStr = "DELETE FROM Entrada WHERE entradaID = ?";

        // Preparamos la consulta
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryStr)){
            preparedStatement.setLong(1, entradaID);

            int removeOut = preparedStatement.executeUpdate();

            // En caso de que no retorne datos,
            // significa que hubo un error a la hora de eliminar los datos de la base de datos
            if(removeOut == 0)
                throw new InstanceNotFoundException(entradaID, Entrada.class.getName());

            // Retornamos el estado de salida de la consulta ejecutada
            return removeOut;

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}