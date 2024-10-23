package es.udc.ws.app.model.Entrada;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlEntradaDao {
    // [FUNC-4] Persiste una inscripción a la BBDD. El método devuelve un objeto Inscription con sus atributos correctamente inicializados
    Entrada create(Connection connection, Entrada entrada);

    // [FUNC-5] Busca una entrada por ID
    Entrada find(Connection connection, Long entradaID) throws InstanceNotFoundException;

    // [FUNC-5] Recuperar todas las inscripciones de un usuario
    List<Entrada> findByEmail(Connection connection, String email);

    // [VARIOS] Actualizar una inscripción
    int update(Connection connection, Entrada entrada) throws InstanceNotFoundException;

    // [VARIOS] Borrar una inscripción según su ID
    int remove(Connection connection, Long entradaID) throws InstanceNotFoundException;
}
