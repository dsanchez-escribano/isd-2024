package es.udc.ws.app.model.Partido;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface SqlPartidoDao{
    // [FUNC-1] Persiste un partido a la Base de Datos. El método devuelve un objeto Partido con el atributo partidoID inicializado al valor correcto.
    Partido create(Connection connection, Partido partido);

    // [FUNC-3] Encuentra un partido según el ID
    Partido find(Connection connection, Long partidoID) throws InstanceNotFoundException;

    // [FUNC-2] Encuentra un partido según las fechas seleccionadas
    List<Partido> findByDates(Connection connection, LocalDate date1, LocalDate date2);

    // [VARIOS] Actualiza los datos de un partido
    int update(Connection connection, Partido partido) throws InstanceNotFoundException;

    // [VARIOS] Borra un partido por su ID
    int remove(Connection connection, Long partidoID) throws InstanceNotFoundException;
}   