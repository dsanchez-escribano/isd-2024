package es.udc.ws.app.client.service.rest;

import es.udc.ws.app.client.service.ClientPartidoService;
import es.udc.ws.app.client.service.dto.ClientEntradaDto;
import es.udc.ws.app.client.service.dto.ClientPartidoDto;
import es.udc.ws.app.client.service.dto.ServerException;
import es.udc.ws.app.client.service.rest.json.JsonToClientEntradaDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientPartidoDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RestClientAppService implements ClientPartidoService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientPartidoService.endpointAddress";
    private String endpointAddress;

    @Override
    public Long addPartido(ClientPartidoDto partido) throws InputValidationException{

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "partido").
                    bodyStream(toInputStream(partido), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientPartidoDtoConversor.toClientPartidoDto(response.getEntity().getContent()).getPartidoID();

        } catch (InputValidationException e) {
            throw e;
        }
         catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ClientPartidoDto> findByDate(LocalDate now, LocalDate date1) throws InstanceNotFoundException, InputValidationException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "partido?fechaInicio="
                            + URLEncoder.encode(date1.toString(), "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientPartidoDtoConversor.toClientPartidoDtos(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ClientPartidoDto findPartido(Long id) throws InstanceNotFoundException{

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "partido/"
                            + URLEncoder.encode(id.toString(), "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientPartidoDtoConversor.toClientPartidoDto(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public Long buytickets(Long partidoId, String email, String creditCardNumber, int numeroEntradas) throws  InputValidationException, InstanceNotFoundException, ServerException{
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "entrada").
                    bodyForm(
                            Form.form().
                                    add("partidoID", Long.toString(partidoId)).
                                    add("email", email).
                                    add("numeroTarjeta", creditCardNumber).
                                    add("numeroEntradas",Integer.toString(numeroEntradas)).
                                    add("estado", Boolean.toString(false)).
                                    add("fechaCompra", LocalDate.now().toString()).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEntradaDtoConversor.toClientEntradaDto(
                    response.getEntity().getContent()).getEntradaID();

        } catch (InputValidationException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<ClientEntradaDto> findEntradas(String email) throws InputValidationException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "entrada?email="
                            + URLEncoder.encode(email, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEntradaDtoConversor.toClientEntradaDtos(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    public void marcarEntradaRecogida(Long entradaID, String creditCardNumber) throws InstanceNotFoundException, InputValidationException {

        try {

            String url = getEndpointAddress() + "entrada/?entradaID=" + entradaID.toString()
                    + "&numeroTarjeta=" + URLEncoder.encode(creditCardNumber, "UTF-8");

            ClassicHttpResponse response = (ClassicHttpResponse) Request
                    .post(url)
                    .execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientPartidoDto partido) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientPartidoDtoConversor.toObjectNode(partido));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode|| statusCode == 200) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
