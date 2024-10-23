package es.udc.ws.app.model.Partido;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlPartidoDao implements SqlPartidoDao{    
    /**
     * [FUNC-3] Encuentra un partido según el ID
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param partidoID ID del partido a buscar en la base de datos
     * @throws InstanceNotFoundException en caso de que el partido no exista
     * @return Retorna el partido asignado al ID dado
     * */
    @Override
    public Partido find(Connection connection, Long partidoID) throws InstanceNotFoundException {
        String query = "SELECT nombreVisitante, fechaInicio, fechaAlta, maximoEntradas, entradasVendidas, precio FROM Partido WHERE partidoID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, partidoID);

            // Ejecutamos la consulta
            ResultSet resultSet = preparedStatement.executeQuery();

            // Comprbamos que la consulta se ha ejecutado correctamente
            if (!resultSet.next())
                throw new InstanceNotFoundException(partidoID, Partido.class.getName());

            // Retornamos el partido encontrado con el ID de partido de la base de datos
            return new Partido(partidoID,                           // ID del partido
                    resultSet.getString(1),                          // Nombre Visitante
                    resultSet.getTimestamp(2).toLocalDateTime(),     // Fecha Inicio
                    resultSet.getTimestamp(3).toLocalDateTime(),     // Fecha Alta
                    resultSet.getInt(4),                             // Máximo Entradas
                    resultSet.getInt(5),                             // Entradas Vendidas
                    resultSet.getFloat(6)                            // Precio
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-2] Encuentra un partido según las fechas seleccionadas
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param date1 Primera fecha
     * @param date2 Segunda fecha
     * @return Lista con los partidos entre las dechas dadas
     * */
    @Override
    public List<Partido> findByDates(Connection connection, LocalDate date1, LocalDate date2) {
        String queryString = "SELECT partidoID, nombreVisitante, fechaInicio, fechaAlta, maximoEntradas, entradasVendidas, precio FROM Partido WHERE fechaInicio >= ? AND fechaInicio <= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(date1));
            preparedStatement.setDate(2, java.sql.Date.valueOf(date2));

            // Lanzamos la consulta
            ResultSet results = preparedStatement.executeQuery();

            // Inicializamnos la lista de partidos
            List<Partido> partidos = new ArrayList<>();

            // Partidos encontrados en la base de datos entre ambas fechas
            while(results.next()){

                // Añadimos el partido a la lista
                partidos.add(
                        new Partido(
                                results.getLong(1),                          // ID del partido
                                results.getString(2),                        // Nombre Visitante
                                results.getTimestamp(3).toLocalDateTime(),   // Fecha Inicio
                                results.getTimestamp(4).toLocalDateTime(),   // Fecha Alta
                                results.getInt(5),                           // Maximo Entradas
                                results.getInt(6),                           // Entradas Vendidas
                                results.getFloat(7)                          // Precio
                        )
                );

            }

            // En caso de que no encuentrar partidos entre ambas fechas, devolverá una lista vacía
            return partidos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [VARIOS] Actualiza los datos de un partido
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param partido Partido con los datos a actualizar en la base de datos
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido en la base de datos
     * @return Código exitoso en caso de haberse realizado con éxito, código erróneo en caso contrario
     * */
    @Override
    public int update(Connection connection, Partido partido) throws InstanceNotFoundException {
        String queryStr = "UPDATE Partido SET nombreVisitante = ?, fechaInicio = ?, fechaAlta = ?, maximoEntradas = ?, entradasVendidas = ?, precio = ? WHERE partidoID = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryStr)){
            preparedStatement.setString(1, partido.getNombreVisitante());                   // Nombre Visitante
            preparedStatement.setTimestamp(2, Timestamp.valueOf(partido.getFechaInicio())); // Fecha Inicio
            preparedStatement.setTimestamp(3, Timestamp.valueOf(partido.getFechaAlta()));   // Fecha Alta
            preparedStatement.setInt(4, partido.getMaximoEntradas());                       // Maximo Entradas
            preparedStatement.setInt(5, partido.getEntradasVendidas());                     // Entradas Vendidas
            preparedStatement.setFloat(6, partido.getPrecio());                             // Precio
            preparedStatement.setLong(7, partido.getPartidoID());                           // Consulta de ID de Partido

            int updatedPartido = preparedStatement.executeUpdate();

            // En caso de que no devuelva nada, existe un error al actualizar
            if(updatedPartido == 0)
                throw new InstanceNotFoundException(partido.getPartidoID(), Partido.class.getName());

            // En caso contrario devolvemos un codigo exitoso
            return updatedPartido;

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * [VARIOS] Borra un partido por su ID
     * @param connection Conexión con la base de datos para realizar la consulta
     * @param partidoID ID del partido a eliminar
     * @throws InstanceNotFoundException En caso de que no existiese el partido a eliminar o surja un problema en la consiulta
     * @return Código exitoso en caso de haberse realizado con éxito, código erróneo en caso contrario
     * */
    @Override
    public int remove(Connection connection, Long partidoID) throws InstanceNotFoundException {
        String queryStr = "DELETE FROM Partido WHERE partidoID = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryStr)){
            preparedStatement.setLong(1, partidoID);    // Consulta por ID para el borrado
            int removedPartido = preparedStatement.executeUpdate();

            // En caso de que no retorne nada, significa que no se ha podido eliminar de forma exitosa
            if(removedPartido == 0)
                throw new InstanceNotFoundException(partidoID, Partido.class.getName());

            // En caso contrario se devuelve un codigo exitoso
            return removedPartido;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}