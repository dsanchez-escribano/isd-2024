package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.Entrada.Entrada;
import es.udc.ws.app.model.Entrada.SqlEntradaDao;
import es.udc.ws.app.model.Entrada.SqlEntradaDaoFactory;
import es.udc.ws.app.model.Partido.Partido;
import es.udc.ws.app.model.Partido.SqlPartidoDao;
import es.udc.ws.app.model.Partido.SqlPartidoDaoFactory;
import es.udc.ws.app.model.partidoService.PartidoService;
import es.udc.ws.app.model.partidoService.PartidoServiceFactory;
import es.udc.ws.app.model.partidoService.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    // ===== ATRIBUTOS =====
    // =====================
    private static PartidoService partidoService = null;
    private static SqlEntradaDao  entradaDao = null;
    private static SqlPartidoDao partidoDao = null;

    // ===== MÉTODOS PRIVADOS =====
    // =============================
    // Primer método en ejecución de la clase
    @BeforeAll
    public static void init(){
        DataSource dataSource = new SimpleDataSource();

        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        partidoService = PartidoServiceFactory.getService();
        entradaDao = SqlEntradaDaoFactory.getDao();
        partidoDao = SqlPartidoDaoFactory.getDao();
    }

    // Último método en ejecución de la clase
    @AfterAll
    public static void end(){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        // Entradas de SQL
        String deletePartidoQuery = "SELECT partidoID from Partido";
        String deleteEntradaQuery = "SELECT partidoID from Entrada";

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // ===== Eliminar los partidos de la tabla Partido =====
                // Preparamos la consulta
                preparedStatement = connection.prepareStatement(deletePartidoQuery);

                // Ejecutamos la consulta
                resultSet = preparedStatement.executeQuery();

                // Comprobamos que la consulta se ha ejecutado correctamente
                if (resultSet.next()) {
                    try {
                        while (resultSet.next()) {
                            // Eliminamos los datos de la tabla
                            partidoDao.remove(connection, resultSet.getLong(1));
                        }
                    } catch (InstanceNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                // ===== Eliminar las Entradas de la tabla Entrada =====
                // Preparamos la consulta
                preparedStatement = connection.prepareStatement(deleteEntradaQuery);

                // Ejecutamos la consulta
                resultSet = preparedStatement.executeQuery();

                // Comprobamos que la consulta se ha ejecutado correctamente
                if (resultSet.next()) {
                    try {
                        while (resultSet.next()) {
                            // Eliminamos los datos de la tabla
                            partidoDao.remove(connection, resultSet.getLong(1));
                        }
                    } catch (InstanceNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                // En caso de no encontrar el partido en la base de datos
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);

                // En caso de producirs ecualquier otro error en la conexión
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

                // Cerramos la conexión para no dejar liberados los recursos
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Función que convierte un formato de fecha y lo convierte en LocalDateTime
    private LocalDateTime toLocalDateTime(String fecha){
        return LocalDateTime.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // =====> [Instancias de Partido y Entrada] <=====
    // Crear uns instancia de partido válida
    private Partido getValidPartido(){
        // Partido = {partidoID [X], nombreVisitante=["Nombre Visitante"], fechaInicio=["2025-01-01 05:05"], fechaAlta=[null], maximoEntradas=[100O], entradasVendidas=[null], precio=[10.0]}
        return new Partido("Nombre Visitante", toLocalDateTime("2025-01-01 05:05"), 1000, 10F);
    }

    // Crea una instancia valida de una entrada con un partido asociado
    private Entrada getValidEntrada(Long partidoID) {
        // Entrada = {entradaID=[null], email=["test@test.com"], numeroTarjeta=[1234567891234567], numeroEntradas=[5], fechaCompra=[null], estado=[null], partidoID=[null]}
        return new Entrada(partidoID, "test@test.com", "1234567891234567", 5);
    }


    // =====> [Crear y eliminar un partido en la base de datos] <=====
    // Registra un partido dentro de la base de datos
    private Partido createPartido(Partido partido) {
        Partido addedPartido;
        try {
            addedPartido = partidoService.addPartido(partido);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedPartido;
    }

    // Elimina un partido de la base de datos
    private void removePartido(Long partidoID) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try(Connection connection = dataSource.getConnection()) {

            // Creamos la conexion con la base de datos y eliminamos el partido de la base de datos
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                partidoDao.remove(connection, partidoID);
                connection.commit();

            // En caso de no encontrar el partido en la base de datos
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);

            // En caso de error en la consulta a la base de datos
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);

            // En caso de producirs ecualquier otro error en la conexión
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

            // Cerramos la conexión para no dejar liberados los recursos
            } finally {
                connection.commit();
                connection.close();
            }

            // En caso de error al crear la coneión con el socket de sql
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Devuelve una lista con los partidos entre dos fechas
    private List<Partido> getPartidosBetweenDates(LocalDate ld1, LocalDate ld2) throws InputValidationException, InstanceNotFoundException {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        List<Partido> partidos;

        try (Connection connection = dataSource.getConnection()) {

            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                partidos = partidoService.findPartidos(ld1, ld2);

                return partidos;

                // En caso de no encontrar el partido en la base de datos
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);

                // En caso de producirs ecualquier otro error en la conexión
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

                // Cerramos la conexión para no dejar liberados los recursos
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // =====> [Crear y eliminar una entrada en la base de datos] <=====
    // Realiza una compra de X entradas establecidas
    private Long buyEntrada(Entrada entrada) throws InstanceNotFoundException, InputValidationException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException {
        Long entradaID;
        try {
            entradaID = partidoService.buyTickets(entrada);
        } catch(RuntimeException e){
            throw new RuntimeException(e);
        }
        return entradaID;
    }

    // Eliminamos una entrada de la base de datos
    private void removeEntrada(Long entradaID) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            // Creamos la conexion con la base de datos y eliminamos la entrada de la base de datos
            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                entradaDao.remove(connection, entradaID);
                connection.commit();

            // En caso de no encontrar la entrada en la base de datos
            } catch (InstanceNotFoundException e){
                connection.commit();
                throw new RuntimeException(e);

            // En caso de error en la consulta a la base de datos
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);

            // En caso de producirs ecualquier otro error en la conexión
            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

            // Liberamos los recursos asignados a la conexión
            } finally {
                connection.commit();
                connection.close();
            }

            // En caso de error al crear la coneión con el socket de sql
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Busca una entrada en la base de datos
    private Entrada searchEntrada(Long entradaID) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()){
            try{
                return entradaDao.find(connection, entradaID);
            } finally {
                connection.close();
            }
        }catch (InstanceNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Busca las entradas asociadas a un mail
    private List<Entrada> searchEntradasByEmail(String email) throws InputValidationException {
        return partidoService.findEntradas(email);
    }

    // Actualiza los datos de la entrada en la base de datos
    private void updateEntrada(Entrada entrada) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                entradaDao.update(connection, entrada);
                connection.commit();

            // En caso de no encontrar el partido en la base de datos
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);

            // En caso de error en la consulta a la base de datos
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);

            // En caso de producirs ecualquier otro error en la conexión
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

            // Cerramos la conexión para no dejar liberados los recursos
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Marca como entregada una entrada
    private void makeEntryAsPickup(Long entradaID, String creditCard) throws InstanceNotFoundException, InputValidationException, InvalidCardException, AlreadyTakenException {
        partidoService.marcarEntradaRecogida(entradaID, creditCard);
    }


    // ===== MÉTODODS PÚBLICOS (TESTS) =====
    // =====================================
    /**
     * Probar a insertar un partido en la base de datos y se encuentra
     * [FUNC-1] Prueba a insertar un partido en la base de datos
     * [FUNC-3] Buscar el partido en la base de datos
     * @throws InputValidationException En caso de que haya algún error en la creación del partido
     * @throws InstanceNotFoundException En caso de que el partido no se encuentre en la base de datos
     * */
    @Test
    public void testFindExistentPartidoByID() throws InputValidationException, InstanceNotFoundException {
        // Partido con el ID dado por la base de datos
        Partido addPartido = null;

        try{
            // Añadimos el partido a la base de datos
            addPartido = partidoService.addPartido(this.getValidPartido());

            // Comprobamos que el partido se ha creado en la base de datos
            assertEquals(addPartido, partidoService.findPartido(addPartido.getPartidoID()));

        } finally {
            assert addPartido != null;
            removePartido(addPartido.getPartidoID());
        }
    }

    /**
     * [FUNC-1] Insertar partidos con las fechas incorrectas
     * @throws InputValidationException En caso de que haya algún error en la creación del partido
     * */
    @Test
    public void testCreateNewPartidoWithWrongDate() throws InputValidationException {
        // Partido a insertar
        Partido addPartido = this.getValidPartido();

        // Modificamos los parámetros del partido
        addPartido.setFechaInicio(this.toLocalDateTime("2000-01-01 11:11").withNano(0));

        // Añadimos el partido a la base de datos
        assertThrows(InputValidationException.class, () -> partidoService.addPartido(addPartido), "InputValidationException expected");
    }

    /**
     * Buscar un partido que no existe en la base de datos
     * [FUNC-1] Insertar partidos en la base de datos
     * [FUNC-3] Comprobar que se busca un partido que no existe y lanza un error
     * @throws InputValidationException En caso de que haya algún error en la consulta
     * @throws InstanceNotFoundException En caso de que el partido no se encuentre en la base de datos
     * */
    @Test
    public void testFindNoExistentPartidoByID() throws InputValidationException, InstanceNotFoundException {
        // Añadimos el partido a la base de datos
        Partido addPartido = partidoService.addPartido(this.getValidPartido());
        Long partidoID = addPartido.getPartidoID();

        // Buscamos el partido creado
        assertEquals(addPartido, partidoService.findPartido(partidoID));

        // Buscamos un partido inexistente
        assertThrows(InstanceNotFoundException.class, () -> partidoService.findPartido(partidoID+5000L), "InstanceNotFoundException expected");

        // Eliminamos el partido creado
        removePartido(partidoID);

        // Buscamos el partido eliminado
        assertThrows(InstanceNotFoundException.class, () -> partidoService.findPartido(partidoID), "InstanceNotFoundException expected");
    }

    /**
     * Buscar un partido entre dos fechas
     * [FUNC-1] Agregar partidos a la base de datos
     * [FUNC-2] Partidos buscados entre dos fechas
     * @throws InputValidationException En caso de que algún parámetros ea incorrecto
     * */
    @Test
    public void testFindPartidosByDate() throws InputValidationException {
        Partido partido1 = getValidPartido();   // Partido válido 1
        Partido partido2 = getValidPartido();   // Partido válido 2
        Partido partido3 = getValidPartido();   // Partido válido 2
        Partido partido4 = getValidPartido();   // Partido válido 3

        LocalDateTime date = toLocalDateTime("2024-01-01 02:02");

        // Datos de los partidos
        partido2.setFechaInicio(date.plusDays(5));
        partido3.setFechaInicio(date.plusMonths(1));
        partido4.setFechaInicio(date.plusDays(5).plusMonths(1));

        // Listas con las que comparar los partidos entre fechas
        List<Partido> addPartidos = new ArrayList<>();
        List<Partido> emptyList = new ArrayList<>();

        try{
            // Creamos los partidos en la base de datos
            partido1 = createPartido(partido1); // Partido 1
            partido2 = createPartido(partido2); // Partido 2
            partido3 = createPartido(partido3); // Partido 3
            partido4 = createPartido(partido4); // Partido 4

            addPartidos.add(partido1);  // Añadimos a la lista de partidos el partido 1

            // Buscamos el primer partido
            assertEquals(addPartidos, partidoService.findPartidos(date.minusYears(1).toLocalDate(), date.plusDays(10).toLocalDate()));

            addPartidos.add(partido2);  // Añadimos a la lista de partidos el partido 2

            // Buscamos los tres primeros partidos
            assertEquals(addPartidos, partidoService.findPartidos(date.minusYears(1).toLocalDate(), date.plusMonths(1).plusDays(1).toLocalDate()));

            addPartidos.add(partido3);  // Añadimos a la lista de partidos el partido 3
            addPartidos.add(partido4);  // Añadimos a la lista de partidos el partido 3

            // Buscamos una lista de partidos que no coincida
            assertEquals(emptyList, partidoService.findPartidos(date.plusMonths(2).toLocalDate(), date.plusYears(1).toLocalDate()));

        } catch (InstanceNotFoundException e) {
            e.printStackTrace();

        } finally {
            removePartido(partido1.getPartidoID());
            removePartido(partido2.getPartidoID());
            removePartido(partido3.getPartidoID());
            removePartido(partido4.getPartidoID());
        }
    }

    /**
     * Buscar partidos por fechas incorrectas
     * [FUNC-1] Agregar partidos a la base de datos
     * [FUNC-2] Se buscan partidos entre fechas incorrectas para el testeo de las excepciones
     * @throws InputValidationException En caso de que algún parámetros ea incorrecto
     * */
    @Test
    public void testFindPartidosByWrongDate() throws InputValidationException {
        Partido partido1 = getValidPartido();   // Partido válido 1
        Partido partido2 = getValidPartido();   // Partido válido 2

        LocalDateTime date = this.toLocalDateTime("2024-01-01 02:02").withNano(0);

        // Datos de los partidos
        partido1.setFechaInicio(date);
        partido2.setFechaInicio(date.plusDays(5));

        // Listas con las que comparar los partidos entre fechas
        List<Partido> addPartidos = new ArrayList<>();

        try{
            // Creamos los partidos en la base de datos
            partido1 = createPartido(partido1); // Partido 1
            partido2 = createPartido(partido2); // Partido 2

            addPartidos.add(partido1);  // Añadimos a la lista de partidos el partido 1
            addPartidos.add(partido2);  // Añadimos a la lista de partidos el partido 2

            // Miramos que salte una excepcion cuando las fechas son incorrectas
            assertThrows(InputValidationException.class, () -> this.getPartidosBetweenDates(null, date.minusYears(1).toLocalDate()));
            assertThrows(InputValidationException.class, () -> this.getPartidosBetweenDates(date.plusDays(10).toLocalDate(), null));
            assertThrows(InputValidationException.class, () -> this.getPartidosBetweenDates(null, null));
            assertThrows(InputValidationException.class, () -> this.getPartidosBetweenDates(date.plusDays(10).toLocalDate(), date.minusYears(1).toLocalDate()));

            // Buscamos los partidos que se encuentran en la base de datos
            assertEquals(addPartidos, this.getPartidosBetweenDates(date.minusYears(1).toLocalDate(), date.plusDays(10).toLocalDate()));

        } catch (InstanceNotFoundException e) {
            e.printStackTrace();

        } finally {
            removePartido(partido1.getPartidoID());
            removePartido(partido2.getPartidoID());
        }
    }

    /**
     * Comprar tickets de un partido válido
     * [FUNC-1] Dar de alta un partido (para recoger las entradas)
     * [FUNC-4] Comprar entradas para un partido
     * @throws InputValidationException En caso de que haya algún error en la consulta
     * @throws InstanceNotFoundException En caso de que el partido no se encuentre en la base de datos
     * @throws BuyTooLateException En caso de que las fechas no sean adecuadas
     * */
    @Test
    public void testBuyTicketsForValidPartido() throws InputValidationException, InstanceNotFoundException, InvalidCardException, BuyTooLateException {
        // Crear un partido válido con algunas entradas disponibles
        Partido partido = null;             // Partido
        Entrada entrada, searchedEntrada;   // Entrada, entrada buscada
        Long boughtEntrada;                 // ID de la entrada

        try {
            // Añadimos el partido a la base de datos
            partido = partidoService.addPartido(this.getValidPartido());

            // Crear una entrada para el partido con entradas disponibles
            entrada = getValidEntrada(partido.getPartidoID());

            // Comprar las entradas para el partido con entradas disponibles
            boughtEntrada = partidoService.buyTickets(entrada);

            // Obtenemos el resultado de la búsqueda de la entrada
            searchedEntrada = searchEntrada(boughtEntrada);

            // Verificar que las entradas se han comprado correctamente
            assertEquals(entrada.getPartidoID(), searchedEntrada.getPartidoID());
            assertEquals(entrada.getEmail(), searchedEntrada.getEmail());
            assertEquals(entrada.getNumeroEntradas(), searchedEntrada.getNumeroEntradas());
            assertEquals(entrada.getNumeroTarjeta(), searchedEntrada.getNumeroTarjeta());

            // Verifica que las entradas vendidas se han actualizado
            assertEquals(5, partidoService.findPartido(partido.getPartidoID()).getEntradasVendidas());
            removeEntrada(boughtEntrada);

        } catch (MaxTicketsReachedException e) {
            e.printStackTrace();

        } finally {
            assert partido != null;
            removePartido(partido.getPartidoID());
        }
    }

    /**
     * Comprar tickets de un partido inexistente
     * [FUNC-4] Compra entradas de un partido inexistente
     * */
    @Test
    public void testBuyTicketsForInvalidPartido() {
        // Crear una entrada para el partido con entradas disponibles
        Entrada entrada = getValidEntrada(500L);

        // Comprar las entradas para el partido con entradas disponibles
        assertThrows(InstanceNotFoundException.class, () -> partidoService.buyTickets(entrada), "InstanceNotFoundException expected");
    }

    /**
     * Busca entradas para un usuario válido
     * Comprueba que lanza las excepciones en caso de que el formato sea el incorrecto
     * [FUNC-5] Obtener todas las compras realizadas de un usuario
     * @throws InputValidationException En caso de error en los parámetros
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido del cual hacer la compra
     * @throws BuyTooLateException En caso de que la fecha de compra no sea apropiada
     * */
    @Test
    public void testFindEntradas() throws InputValidationException, InstanceNotFoundException, InvalidCardException, BuyTooLateException {
        Partido partido1, partido2, partido3;           // Partidos en la base de datos
        List<Entrada> entradaList = new ArrayList<>();  // Lista de entradas que compra el usuario

        // Obtenemos los partidos
        partido1 = this.getValidPartido();
        partido2 = this.getValidPartido();
        partido3 = this.getValidPartido();

        // Seteamos el nombre de los visitantes para que no sean el mismo
        partido1.setNombreVisitante("visitante 1");
        partido2.setNombreVisitante("visitante 2");
        partido3.setNombreVisitante("visitante 3");

        try {
            // Insertamos los partidos en la base de datos
            partido1 = partidoService.addPartido(partido1);
            partido2 = partidoService.addPartido(partido2);
            partido3 = partidoService.addPartido(partido3);

            // Entradas solicitadas por el usuario
            Entrada entrada1 = this.getValidEntrada(partido1.getPartidoID());
            Entrada entrada2 = this.getValidEntrada(partido2.getPartidoID());
            Entrada entrada3 = this.getValidEntrada(partido3.getPartidoID());

            // Datos de las entradas
            entrada2.setNumeroEntradas(4);

            // Compra de las entradas
            entrada1.setEntradaID(this.buyEntrada(entrada1));
            entrada2.setEntradaID(this.buyEntrada(entrada2));

            // Añadimos las entradas a la lista de entradas
            entradaList.add(entrada1);
            entradaList.add(entrada2);

            // Hacemos la consulta de las entradas
            assertEquals(entradaList, partidoService.findEntradas(entrada1.getEmail()));

            // Cambiamos el email de la entrada 2 y la eliminamos de la lista
            entrada2.setEmail("randomMail@randomMail.com");
            updateEntrada(entrada2);
            entradaList.remove(entrada2);

            // Hacemos la consulta de la primera entrada
            assertEquals(entradaList, partidoService.findEntradas(entrada1.getEmail()));

            // Comprobar el máximo de entradas
            entrada3.setNumeroEntradas(1000000000);
            assertThrows(MaxTicketsReachedException.class, () -> entrada3.setEntradaID(this.buyEntrada(entrada3)), "MaxTicketsReachedException expected");

            // Eliminamos las entradas de la base de datos que se han podido crear
            this.removeEntrada(entrada1.getEntradaID());
            this.removeEntrada(entrada2.getEntradaID());

        } catch (MaxTicketsReachedException e) {
            e.printStackTrace();

        } finally {
            this.removePartido(partido1.getPartidoID());
            this.removePartido(partido2.getPartidoID());
            this.removePartido(partido3.getPartidoID());
        }
    }

    /**
     * Buscar entradas para un mail con el formato incorrecto
     * [FUNC-5] Intentar buscar una entrada con los datos del email erróneos
     * @throws InputValidationException En caso de que los parámetros sean incorrectos
     * @throws BuyTooLateException En caso de que las fechas no sean las correctas
     * @throws InstanceNotFoundException En caso de no encontrar las entradas a buscar
     * */
    @Test
    public void testFindEntradasFromInvalidEmailFormat() throws InputValidationException, BuyTooLateException, InvalidCardException, InstanceNotFoundException, MaxTicketsReachedException {
        Partido partido = getValidPartido();        // Partido al que hacer la consulta
        Entrada validEntrada = null;                // Primeras entradas reservadas para el partido
        Long partidoID=null, validEntradaID=null;   // IDs de todos los elementos

        // Una vez creado el partido y las entradas asociadas al partido
        try {
            // Añadimos el partido a la base de datos
            partido = partidoService.addPartido(partido);
            partidoID = partido.getPartidoID();

            // Añadimos la entrada 1
            validEntrada = getValidEntrada(partidoID);

            // Obtenemos el ID de la entrada realizando la compra
            validEntradaID = this.buyEntrada(validEntrada);

            // Comprobamos que las excepciones con el formato del email funcionen
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail(""), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("@."), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("@test."), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("test@."), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("@.com"), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("@"), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("."), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("test@.com"), "InputValidationException expected");
            assertThrows(InputValidationException.class, () -> this.searchEntradasByEmail("test@test."), "InputValidationException expected");

            // Comprobamos que devuelve una lista vacía en caso de que se
            // busquen entradas para un email que no existe
            assertTrue(searchEntradasByEmail("badEmail@badEmailTest.test").isEmpty());

        // Eliminamos las entradas y el partido de la base de datos
        } finally {
            assert validEntradaID != null;
            assert partidoID != null;
            removeEntrada(validEntradaID);
            removePartido(partidoID);
        }
    }

    /**
     * Comprobar que al pasarle el formato correcto, pero inexistente en la base de datos, se devuelve una lista vacía
     * @throws InputValidationException En caso de que algún parámetro sea incorrecto
     * @throws BuyTooLateException En caso de que las fechas introducidas no sean adecuadas
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido o las entradas a buscar
     * @throws MaxTicketsReachedException En caso de hacer una reserva superando el máximo de tickets
     * */
    @Test
    public void testFindEntradasFromInvalidRandomEmail() throws InputValidationException, InstanceNotFoundException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException {
        Partido partido = getValidPartido();    // Partido al que hacer la consulta
        Long validEntradaID = null;             // IDs de todos los elementos

        // Una vez creado el partido y las entradas asociadas al partido
        try {
            partido = partidoService.addPartido(partido);                               // Añadimos el partido a la base de datos
            validEntradaID = this.buyEntrada(getValidEntrada(partido.getPartidoID()));  // Compramos las entradas

            // Comprobamos que devuelve una lista vacía en caso de que se busquen entradas para un email que no existe
            assertTrue(this.searchEntradasByEmail("randomMail@randomMail.com").isEmpty());

        // Eliminamos las entradas y el partido de la base de datos
        } finally {
            removeEntrada(validEntradaID);
            removePartido(partido.getPartidoID());
        }
    }

    /**
     * Buscar entradas para una tarjeta de crédito con el formato incorrecto
     * [FUNC-1] Añadir el partido con el cual hacer las consultas de las entradas
     * [FUNC-5] Reservar entradas con el formato de la tarjeta de crédito incorrecto
     * @throws InputValidationException En caso de que haya error con los parámetros
     * */
    @Test
    public void testFindEntradasFromInvalidCreditCard() throws InputValidationException {
        Partido partido = null; // Partido al que hacer la consulta
        Entrada entrada = null; // Primeras entradas reservadas para el partido
        Long partidoID = null;  // IDs de todos los elementos

        // Una vez creado el partido y las entradas asociadas al partido
        try {
            // Añadimos el partido a la base de datos
            partido = this.createPartido(getValidPartido());
            partidoID = partido.getPartidoID();

            // Añadimos la entrada
            entrada = this.getValidEntrada(partidoID);

            // Copia de la entrada
            Entrada finalInvalidEntrada = entrada;

            // Comprobamos el formato de la targeta a la hora de realizar la compra
            assertThrows(InputValidationException.class, () -> finalInvalidEntrada.setNumeroTarjeta(""), "InputValidationException expected");
            finalInvalidEntrada.setNumeroTarjeta("aaaaaaaaaaaaaaaa");
            assertThrows(InvalidCardException.class, () -> this.buyEntrada(finalInvalidEntrada), "InputValidationException expected");
            finalInvalidEntrada.setNumeroTarjeta("1234 1234 1234 12");
            assertThrows(InvalidCardException.class, () -> this.buyEntrada(finalInvalidEntrada), "InputValidationException expected");
            finalInvalidEntrada.setNumeroTarjeta("________________");
            assertThrows(InvalidCardException.class, () -> this.buyEntrada(finalInvalidEntrada), "InputValidationException expected");
            finalInvalidEntrada.setNumeroTarjeta("                ");
            assertThrows(InvalidCardException.class, () -> this.buyEntrada(finalInvalidEntrada), "InputValidationException expected");

        // Eliminamos las entradas y el partido de la base de datos (en caso de llegar aqui, la entrada nunca se llegaría a comprar)
        } finally {
            assert partidoID != null;
            removePartido(partidoID);
        }
    }

    /**
     * [FUNC-1] Añadir el partido con el cual hacer las consultas de las entradas
     * [FUNC-5] Reservar las entradas con un número de entradas inapropiado
     * @throws InputValidationException En caso de qye haya problemas con los parametros
     * */
    @Test
    public void testFindEntradasFromExceededEntries() throws InputValidationException {
        Partido partido = null; // Partido al que hacer la consulta
        Entrada entrada = null; // Primeras entradas reservadas para el partido
        Long partidoID = null;  // IDs de todos los elementos

        // Una vez creado el partido y las entradas asociadas al partido
        try {
            // Añadimos el partido a la base de datos
            partido = this.createPartido(getValidPartido());
            partidoID = partido.getPartidoID();

            // Añadimos la entrada
            entrada = this.getValidEntrada(partidoID);
            entrada.setNumeroEntradas(1000000000);

            // Copia de la entrada
            Entrada finalInvalidEntrada = entrada;

            // Comprobamos el formato de la targeta a la hora de realizar la compra
            assertThrows(MaxTicketsReachedException.class, () -> this.buyEntrada(finalInvalidEntrada), "MaxTicketsReachedException expected");

        // Eliminamos las entradas y el partido de la base de datos (en caso de llegar aqui, la entrada nunca se llegaría a comprar)
        } finally {
            assert partidoID != null;
            removePartido(partidoID);
        }
    }

    /**
     * Marcar entrada como recogida
     * Marcar como entregada cuando ya está como entregada
     * [FUNC-1] Creación del partido
     * [FUNC-4] Reservar una entrada para un partido
     * [FUNC-6] Marcar como recogida la entrada y no marcar como entregada una entrada cuando ya está como entregada
     * @throws InputValidationException En caso de que haya algún error con los parametros
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido del cual hacer la reserva de entradas
     * @throws MaxTicketsReachedException En caso de que las entradas a reservar superen el máximo
     * @throws BuyTooLateException En caso de que las fechas no sean adecuadas
     * @throws AlreadyTakenException En caso de que se marque como entregada cuando ya ha sido entregada
     * */
    @Test
    public void testSetEntradaRecogida() throws InputValidationException, InstanceNotFoundException, MaxTicketsReachedException, BuyTooLateException, AlreadyTakenException {
        Partido partido = null; // Partido sobre el que hacer la reserva de entradas
        Entrada entrada = null; // La entrada en sí
        Long entradaID = null;  // El ID de la entrada

        try{
            // Creamos el partido y los datos de la entrada para hacer la comprobación
            partido = this.createPartido(this.getValidPartido());
            entrada = getValidEntrada(partido.getPartidoID());
            entradaID = this.buyEntrada(entrada);

            // Marcamos la entrada como recogida
            this.makeEntryAsPickup(entradaID, entrada.getNumeroTarjeta());

            // Comprobamos los parametros de la entrada
            assertEquals(entrada.getPartidoID(), this.searchEntrada(entradaID).getPartidoID());
            assertEquals(entrada.getEmail(), this.searchEntrada(entradaID).getEmail());
            assertEquals(entrada.getNumeroEntradas(), this.searchEntrada(entradaID).getNumeroEntradas());
            assertEquals(entrada.getNumeroTarjeta(), this.searchEntrada(entradaID).getNumeroTarjeta());

            // Comprobamos el estado de la entrada
            assertTrue(this.searchEntrada(entradaID).getEstado());

            // Copias de entrada y entradaID
            Long finalEntradaID = entradaID;
            Entrada finalEntrada = entrada;

            // Comprobar que no se puede marcar como recogida una entrada ya recogida
            assertThrows(AlreadyTakenException.class, () -> this.makeEntryAsPickup(finalEntradaID, finalEntrada.getNumeroTarjeta()), "AlreadyTakenException expected");

        } catch (InvalidCardException e) {
            e.printStackTrace();

        } finally {
            assert entradaID != null;
            assert partido != null;
            this.removeEntrada(entradaID);
            this.removePartido(partido.getPartidoID());
        }
    }

    /**
     * Marcar una entrada inexistente
     * [FUNC-1] Creación del partido
     * [FUNC-4] Reservar una entrada para un partido
     * [FUNC-6] Marcar como recogida la entrada y no marcar como entregada una entrada cuando ya está como entregada
     * @throws InputValidationException En caso de que haya algún error con los parametros
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido del cual hacer la reserva de entradas
     * @throws InvalidCardException En caso de que la tarjeta introducida sea errónea
     * @throws BuyTooLateException En caso de que las fechas no sean adecuadas
     * @throws MaxTicketsReachedException En caso de que las entradas a reservar superen el máximo
     * */
    @Test
    public void testMakeUnexistentEntryAsMakeup() throws InstanceNotFoundException, InputValidationException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException {
        Partido partido = null; // Partido sobre el que hacer la reserva de entradas
        Entrada entrada = null; // La entrada en sí
        Long entradaID = null;  // El ID de la entrada

        try{
            // Creamos el partido y los datos de la entrada para hacer la comprobación
            partido = this.createPartido(this.getValidPartido());
            entrada = this.getValidEntrada(partido.getPartidoID());
            entradaID = this.buyEntrada(entrada);

            // Copia de los objetos entrada y entradaID
            Long finalEntradaID = entradaID;
            Entrada finalEntrada = entrada;

            // Marcamos la entrada como recogida
            assertThrows(InstanceNotFoundException.class, () -> this.makeEntryAsPickup(finalEntradaID+1, finalEntrada.getNumeroTarjeta()), "");

        } finally {
            assert entradaID != null;
            this.removeEntrada(entradaID);
            this.removePartido(partido.getPartidoID());
        }
    }

    /**
     * [FUNC-6] Comprobar que las excepciones a la hora de marcar las entradas es correcta
     * @throws InstanceNotFoundException En caso de que no se encuentre la entrada a buscar
     * @throws InputValidationException En caso de que haya algún error con los parámetros
     * @throws InvalidCardException En caso de que la tarjeta de crédito sea incorrecta
     * @throws BuyTooLateException En caso de que la facha de la entrada sea erronea
     * @throws MaxTicketsReachedException En caso de superar los tickets permitidos
     * */
    @Test
    public void testMakeBadEntryAsMakeup() throws InstanceNotFoundException, InputValidationException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException {
        Partido partido = null; // Partido sobre el que hacer la reserva de entradas
        Entrada entrada = null; // La entrada en sí
        Long entradaID=null;    // El ID de la entrada

        String email = null;                    // Email
        String creditCard = null;               // Tarjeta de crédito
        LocalDateTime fechaCompra = null;       // Fecha de la compra

        try{
            // Creamos el partido y los datos de la entrada para hacer la comprobación
            partido = this.createPartido(this.getValidPartido());
            entrada = this.getValidEntrada(partido.getPartidoID());
            entradaID = this.buyEntrada(entrada);
            entrada.setEntradaID(entradaID);

            // Copia de los objetos entrada y entradaID
            Long finalEntradaID = entradaID;
            Entrada finalEntrada = entrada;

            // Modificamos el email de la entrada para que salte la excepción
            creditCard = entrada.getNumeroTarjeta();
            entrada.setNumeroTarjeta("0000000000000000");

            // Intentamos marcar la entrada como recogida
            assertThrows(InvalidCardException.class, () -> this.makeEntryAsPickup(finalEntradaID, finalEntrada.getNumeroTarjeta()), "InvalidCardException expected");

            // Reasignamos el email y cambiamos la tarjeta de crédito para que salte el error
            entrada.setNumeroTarjeta(creditCard);
            entrada.setEstado(false);
            this.updateEntrada(entrada);

            email = entrada.getEmail();
            entrada.setEmail("random@random.com");


            // Intentamos marcar la entrada como recogida
            assertDoesNotThrow(() -> this.makeEntryAsPickup(finalEntradaID, finalEntrada.getNumeroTarjeta()));

            // Volvemos a asignar la tarjeta de crédito y modificamos la fecha de compra
            entrada.setEmail(email);
            entrada.setEstado(false);
            this.updateEntrada(entrada);

            fechaCompra = entrada.getFechaCompra();
            entrada.setFechaCompra(this.toLocalDateTime("2000-08-15 18:30"));

            // Intentamos marcar la entrada como recogida
            assertDoesNotThrow(() -> this.makeEntryAsPickup(finalEntradaID, finalEntrada.getNumeroTarjeta()));

            // Volvemos a asignar la fecha de la compra
            entrada.setFechaCompra(fechaCompra);
            entrada.setEstado(false);
            this.updateEntrada(entrada);

            // Ahora no debería haber excepciones
            assertDoesNotThrow(() -> this.makeEntryAsPickup(finalEntradaID, finalEntrada.getNumeroTarjeta()));

        } finally {
            assert entradaID != null;
            assert partido != null;
            this.removeEntrada(entradaID);
            this.removePartido(partido.getPartidoID());
        }
    }
}