package es.udc.ws.app.model.partidoService;

import es.udc.ws.app.model.Entrada.Entrada;
import es.udc.ws.app.model.Partido.Partido;
import es.udc.ws.app.model.partidoService.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface PartidoService {
    // [FUNC-1] Añade un partido a la base de datos
    Partido addPartido(Partido partido) throws InputValidationException;

    // [FUNC-3] Encuentra partidos entre dos fechas
    List<Partido> findPartidos(LocalDate date1, LocalDate date2) throws InputValidationException, InstanceNotFoundException;

    // [FUNC-4] Compra los tickets dados por la entrada
    Long buyTickets(Entrada entrada) throws InstanceNotFoundException, InputValidationException, InvalidCardException, BuyTooLateException, MaxTicketsReachedException;

    // [FUNC-5] Busca las entradas asignadas a un email dado
    List<Entrada> findEntradas(String email) throws InputValidationException;

    // [FUNC-2] Busca un partido por ID de partido
    Partido findPartido(Long partidoID) throws InstanceNotFoundException;

    // [FUNC-6] Establa¡ece la entrada como recogida
    void marcarEntradaRecogida(Long entradaID, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, InvalidCardException, AlreadyTakenException;
}
