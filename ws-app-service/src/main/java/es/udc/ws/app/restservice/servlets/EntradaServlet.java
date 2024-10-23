package es.udc.ws.app.restservice.servlets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.Entrada.Entrada;
import es.udc.ws.app.model.partidoService.PartidoServiceFactory;
import es.udc.ws.app.model.partidoService.exceptions.AlreadyTakenException;
import es.udc.ws.app.model.partidoService.exceptions.BuyTooLateException;
import es.udc.ws.app.model.partidoService.exceptions.InvalidCardException;
import es.udc.ws.app.model.partidoService.exceptions.MaxTicketsReachedException;
import es.udc.ws.app.restservice.dto.EntradaDtoEntradaConversor;
import es.udc.ws.app.restservice.dto.RestEntradaDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEntradaDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntradaServlet extends RestHttpServletTemplate {


    // Corresponde al Caso de Uso 5 - findAllEntradasFromUser
    // GET /entrada?user=email
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path != null && path.length() > 0) {
            // Si hay algo tras /entrada -> Bad Request
            ServletUtils.writeServiceResponse(resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(new InputValidationException("Invalid arguments " + path)),
                    null);
            return;
        }
        String email = req.getParameter("email");
        List<Entrada> modelEntradas = null;
        try {
            modelEntradas = PartidoServiceFactory.getService().findEntradas(email);
            List<RestEntradaDto> restEntradas = new ArrayList<>();
            for (Entrada entrada : modelEntradas) {
                restEntradas.add(EntradaDtoEntradaConversor.toRestEntradaDto(entrada));
            }

            ServletUtils.writeServiceResponse(resp,
                    HttpServletResponse.SC_OK,
                    JsonToRestEntradaDtoConversor.toArrayNode(restEntradas),
                    null);
        } catch (InputValidationException e) {
            ServletUtils.writeServiceResponse(resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(e),
                    null);
            return;
        }
    }


    // Método POST para los casos de uso 4 y 6
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path==null){
            doCompraEntradas(req, resp);
        }
       else if (path.contains("/")||path.contains("")) {
            // Caso de uso 6 - Recogida de entradas
            doRecogerEntradas(req, resp);
        } else {
            ServletUtils.writeServiceResponse(resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(new InputValidationException("Invalid arguments " + path)),
                    null);

        }
    }


    // Método para el Caso de Uso 4 - Compra de entradas
    private void doCompraEntradas(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String inputPartidoIDStr = req.getParameter("partidoID");
            if (inputPartidoIDStr == null) {
                throw new InputValidationException("PartidoID cannot be null or empty");
            }
            String inputEmail = req.getParameter("email");
            if (inputEmail == null) {
                throw new InputValidationException("Email cannot be null or empty");
            }
            String inputCreditCard = req.getParameter("numeroTarjeta");
            if (inputCreditCard == null) {
                throw new InputValidationException("numeroTarjeta cannot be null nor empty");
            }

            String inputNumEntradasStr = req.getParameter("numeroEntradas");


            int inputNumeroEntradas = Integer.parseInt(inputNumEntradasStr);
            Long inputPartidoID = Long.parseLong(inputPartidoIDStr);
            LocalDateTime inputFechaCompra = LocalDateTime.now();
            Boolean inputEstado = false;


            Entrada Entrada1 = new Entrada(inputPartidoID, inputEmail, inputCreditCard, inputNumeroEntradas);
            Long Entrada1_ID = PartidoServiceFactory.getService().buyTickets(Entrada1);
            Entrada Entrada2 = new Entrada(Entrada1_ID,inputEmail,inputCreditCard,inputNumeroEntradas,inputFechaCompra,inputEstado,inputPartidoID);

            RestEntradaDto EntradaDto = EntradaDtoEntradaConversor.toRestEntradaDto(Entrada2);


            String entradaURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + EntradaDto.getEntradaID();

            // Crear cabeceras y escribir respuesta
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", entradaURL);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                    JsonToRestEntradaDtoConversor.toObjectNode(EntradaDto), headers);

        } catch (NumberFormatException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(
                            new InputValidationException(e.getLocalizedMessage())), null);

        } catch (InstanceNotFoundException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    AppExceptionToJsonConversor.toInstanceNotFoundException(e), null);


        } catch (BuyTooLateException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_GONE,
                    AppExceptionToJsonConversor.toBuyTooLateException(e), null);

        } catch (InputValidationException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(e), null);


        } catch (MaxTicketsReachedException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_GONE,
                    AppExceptionToJsonConversor.toMaxTicketsReachedException(e), null);

        } catch (InvalidCardException e) {
            throw new RuntimeException(e);
        }
    }


    // Método para el Caso de Uso 6 - Recogida de entradas
    private void doRecogerEntradas(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Extraer parámetros de la solicitud
        Long entradaID = null;
        String numeroTarjeta = null;

        try {
            entradaID = ServletUtils.getMandatoryParameterAsLong(req,"entradaID");
            numeroTarjeta = req.getParameter("numeroTarjeta");

        } catch (NumberFormatException e) {
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            ObjectNode exceptionJson = AppExceptionToJsonConversor.toInputValidationException(
                    new InputValidationException("Invalid Request: " + "parameter 'entradaID' or 'numeroTarjeta' is invalid."));
            ServletUtils.writeServiceResponse(resp, statusCode, exceptionJson, null);
            return;
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }

        // Marcar la entrada como recogida
        try {
            PartidoServiceFactory.getService().marcarEntradaRecogida(entradaID, numeroTarjeta);
            // Escribir respuesta exitosa
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK, null, null);


        } catch (InputValidationException e) {
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            ObjectNode exceptionJson = AppExceptionToJsonConversor.toInputValidationException(e);
            ServletUtils.writeServiceResponse(resp, statusCode, exceptionJson, null);
        } catch (InstanceNotFoundException e) {
            int statusCode = HttpServletResponse.SC_NOT_FOUND;
            ObjectNode exceptionJson = AppExceptionToJsonConversor.toInstanceNotFoundException(e);
            ServletUtils.writeServiceResponse(resp, statusCode, exceptionJson, null);
        } catch (InvalidCardException e) {
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            ObjectNode exceptionJson = AppExceptionToJsonConversor.toInvalidCardException(e);
            ServletUtils.writeServiceResponse(resp, statusCode, exceptionJson, null);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }
    }





}

