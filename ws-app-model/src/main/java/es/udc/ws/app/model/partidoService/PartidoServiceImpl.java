package es.udc.ws.app.model.partidoService;

import es.udc.ws.app.model.Entrada.Entrada;
import es.udc.ws.app.model.Entrada.SqlEntradaDao;
import es.udc.ws.app.model.Entrada.SqlEntradaDaoFactory;
import es.udc.ws.app.model.Partido.Partido;
import es.udc.ws.app.model.Partido.SqlPartidoDao;
import es.udc.ws.app.model.Partido.SqlPartidoDaoFactory;
import es.udc.ws.app.model.partidoService.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static es.udc.ws.app.model.util.ModelConstants.*;


public class PartidoServiceImpl implements PartidoService {

    // ===== Atributos =====
    private final DataSource dataSource;
    private SqlPartidoDao partidoDao;
    private SqlEntradaDao entradaDao;


    // ===== Constructor =====
    public PartidoServiceImpl(){
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        partidoDao = SqlPartidoDaoFactory.getDao();
        entradaDao = SqlEntradaDaoFactory.getDao();
    }


    // ====== Métodos privados =====
    // Validación del partido
    private void validatePartido(Partido partido) throws InputValidationException {
        PropertyValidator.validateMandatoryString("nombreVisitante", partido.getNombreVisitante());
        PropertyValidator.validateNotNegativeLong("maximoEntradas", Long.valueOf(partido.getMaximoEntradas()));
        PropertyValidator.validateNotNegativeLong("entradasVendidas", Long.valueOf(partido.getEntradasVendidas()));
        PropertyValidator.validateDouble("precio", partido.getPrecio(), 0, MAX_PRICE);
    }

    // Validación del email
    private void validarEmail(String email) throws InputValidationException {
        // Patrón que debe seguir el mail para ser válido
        String pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        // En caso de que el mail introducido no siga el patrón establecido
        // saltará la excepción InputValidationException
        if(!Pattern.compile(pattern).matcher(email).matches())
            throw new InputValidationException("Invalid email format: ["+ email + "]");
    }

    // Validación del número de tarjeta
    private void validarTarjeta(String numeroTarjeta, Long entradaID) throws InvalidCardException {
        // Patrón que debe seguir el numero de targeta para ser válido
        String pattern = "[0-9]{16}";

        // En caso de que el numero de tarjeta introducido no siga el patrón establecido
        // saltará la excepción InputValidationException
        if(!Pattern.compile(pattern).matcher(numeroTarjeta).matches())
            throw new InvalidCardException(entradaID);
    }

    // ===== Métodos públicos =====
    /**
     * [FUNC-1] Añade un partido a la base de datos
     * @param partido Partido a añadir en la base de datos
     * @throws InputValidationException En caso de que surja algún problema en la hora de la creación
     * @return Partido con el ID asignado por la base de datos
     * */
    @Override
    public Partido addPartido(Partido partido) throws InputValidationException {
        this.validatePartido(partido);

        // Validamos la fecha del partido con la fecha actual
        if(partido.getFechaInicio().isBefore(LocalDateTime.now().withNano(0)))
            throw new InputValidationException("Fecha en el pasado");

        partido.setFechaAlta(LocalDateTime.now().withNano(0));
        partido.setEntradasVendidas(0);

        try (Connection connection = dataSource.getConnection()) {
            try {
                // Preparamos la conexión
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Creamos el partido en la base de datos
                Partido createdMatch = partidoDao.create(connection, partido);

                // Hacemos commit de la conexión
                connection.commit();

                // Retornamos el partido devuelto al crearlo
                return createdMatch;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);

            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;

            // Antes de retornar el dato, cerramos la sesión con la base de datos
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-2] Busca partidos entre dos fechas
     * @param date1 Primera fecha del rando
     * @param date2 Segunda fecha del rango
     * @throws InputValidationException En caso de que los parametros sean incorrectos
     * @throws InstanceNotFoundException En caso de que no se encuentren partidos
     * @return Lista con los partidos entre ambas fechas
     * */
    @Override
    public List<Partido> findPartidos(LocalDate date1, LocalDate date2) throws InputValidationException, InstanceNotFoundException {
        // Comprobación de la primera fecha
        if(date1 == null)
            throw new InputValidationException("Invalida date1: [null]");

        // Comprobación de la segunda fecha
        if(date2 == null)
            throw new InputValidationException("Invalida date2: [null]");

        // Comprobar que la segunda fecha es posterior a la primera
        if(date2.isBefore(date1))
            throw new InputValidationException("Invalid temporaly date: date2(" + date2 + ") before date1(" + date1 + "]");

        // Intentamos obtener los datos en una conexión a la base de datos
        try (Connection connection = dataSource.getConnection()) {
            try {
                return partidoDao.findByDates(connection, date1, date2);
            } finally {
                // En caso de ser exitoso, que cierre la conexión para liberar recursos
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-3] Busca un partido por el ID de partido
     * @param partidoId ID del partido a buscar
     * @throws InstanceNotFoundException En caso de que no se encuentre el partido en la base de datos
     * @return Partido con el ID asignado
     * */
    @Override
    public Partido findPartido(Long partidoId) throws InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            try {
                return partidoDao.find(connection, partidoId);
            } finally {
                // Nos aseguramos que antes de retornar el partido la conexión se cierra
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-4] Compra tickets con la información de la entrada dada
     * @param entrada Datos de la entrada con los que hacer la reserva
     * @throws InstanceNotFoundException En caso de que haya problemas a la hora de hacer la reserva en la base de datos
     * @throws InputValidationException En caso de que ciertos valores de la entrada sean erróneos
     * @throws BuyTooLateException En caso de que las fechas de reserva y de partido no sean adecuadas
     * @throws MaxTicketsReachedException En caso de que supere o no baya entradas suficientes para la reserva a realizar
     * @return ID de la entrada dada
     * */
    @Override
    public Long buyTickets(Entrada entrada) throws InstanceNotFoundException, InputValidationException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException {
        int freePartidoEntries = 0;   // Entradas que faltan por ser reservadas

        // Validar los datos de la entrada
        this.validarEmail(entrada.getEmail());           // Validamos el email
        this.validarTarjeta(entrada.getNumeroTarjeta(), entrada.getEntradaID()); // Validamos el numero de tarjeta

        // Datos iniciañes de la entrada a la hora de la creación
        entrada.setFechaCompra(LocalDateTime.now().withNano(0));
        entrada.setEstado(false);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Comprobamos que el evento existe
                Partido partido = partidoDao.find(connection, entrada.getPartidoID());

                // Comprobamos si intenta responder después de la fecha límite
                if (partido.getFechaInicio().isBefore(entrada.getFechaCompra()))
                    throw new BuyTooLateException(partido.getPartidoID());

                // Comprobamos si el número máximo de entradas ya se ha alcanzado
                // o si el número de entradas supera las entradas posibles
                freePartidoEntries = partido.getMaximoEntradas() - partido.getEntradasVendidas();
                if (freePartidoEntries <= 0 || entrada.getNumeroEntradas() > freePartidoEntries)
                    throw new MaxTicketsReachedException(partido.getPartidoID());

                // Actualizamos las entradas del partido
                partido.setEntradasVendidas(partido.getEntradasVendidas() + entrada.getNumeroEntradas());

                // Actualizamos las entradas del partido dentro de la base de datos
                partidoDao.update(connection, partido);

                // Añadimos las entradas a la base de datos de las entradas
                Entrada newEntrada = entradaDao.create(connection, entrada);

                // Commit
                connection.commit();

                return newEntrada.getEntradaID();

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);

            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;

            // Cerramos la conexión con la base de datos para no perder recursos
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-5] Busca las entradas asociadas a un email dado
     * @param email Email de las entradas a buscar
     * @throws InputValidationException En caso de que los datos introducidos no sean válidos
     * @return Lista de entradas asociadas a un email
     * */
    @Override
    public List<Entrada> findEntradas(String email) throws InputValidationException {
        // Validar el email
        this.validarEmail(email);
        try (Connection connection = dataSource.getConnection()) {
            try {
                return entradaDao.findByEmail(connection, email);
            } finally {
                // Nos aseguramos que los recursos de la conexión queden libres
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [FUNC-6] Marcar entradas como recogidas en la base de datos
     * @param entradaID ID de la entrada
     * @param creditCardNumber Número de la tarjeta de crédito
     * @throws InstanceNotFoundException En caso de que haya problemas a la hora de marcar la entrada como recogida
     * @throws InputValidationException En caso de que ciertos valores de la entrada sean erróneos
     * @throws InvalidCardException Número de la tarjeta de crédito no válida
     * @throws AlreadyTakenException En caso de que ya se haya recogido la entrada
     * */
    @Override
    public void marcarEntradaRecogida(Long entradaID, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, InvalidCardException, AlreadyTakenException {
        // Validar el número de tarjeta
        this.validarTarjeta(creditCardNumber, entradaID);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Buscar las entradas asociadas al ID de entrada
                Entrada entrada = entradaDao.find(connection, entradaID);

                // Si ya están entregadas
                if (entrada.getEstado())
                    throw new AlreadyTakenException(entradaID);

                // En caso de que no coincida el número de tarjeta dado con el número de tarjeta de la entrada
                if (!entrada.getNumeroTarjeta().equals(creditCardNumber))
                    throw new InvalidCardException(entradaID);

                entrada.setEstado(true);
                entradaDao.update(connection, entrada);
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);

            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;

            // Cerramos la conexión para asegurarnos que los recursos no queden asignados
            } finally {
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}