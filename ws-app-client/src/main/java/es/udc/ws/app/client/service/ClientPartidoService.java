package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientEntradaDto;
import es.udc.ws.app.client.service.dto.ClientPartidoDto;
import es.udc.ws.app.client.service.dto.ServerException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientPartidoService {
        public Long addPartido(ClientPartidoDto partido) throws InputValidationException;

        public List<ClientPartidoDto> findByDate(LocalDate now,LocalDate date) throws InstanceNotFoundException, InputValidationException;

        public ClientPartidoDto findPartido(Long id) throws InstanceNotFoundException;

        public Long buytickets(Long partidoId, String email, String creditCardNumber, int numeroEntradas) throws  InputValidationException, InstanceNotFoundException, ServerException;


        public List<ClientEntradaDto> findEntradas(String email) throws InputValidationException;

        void marcarEntradaRecogida(Long entradaID, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, ServerException;

}
