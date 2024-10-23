package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.Partido.Partido;
import es.udc.ws.app.model.partidoService.PartidoServiceFactory;
import es.udc.ws.app.restservice.dto.PartidoDtoPartidoConversor;
import es.udc.ws.app.restservice.dto.RestPartidoDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestPartidoDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.exceptions.ParsingException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class PartidoServlet extends RestHttpServletTemplate {


   @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws InputValidationException, IOException, InstanceNotFoundException {
        if(req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            LocalDate fechaFin;

            try {
                fechaFin = LocalDate.parse(ServletUtils.getMandatoryParameter(req, "fechaInicio"));
            } catch(DateTimeParseException e) {
                throw new InputValidationException("Fecha inválida");
            }

            List<Partido> partido = PartidoServiceFactory.getService().findPartidos(LocalDate.now(), fechaFin);

            List<RestPartidoDto> partidoDtos = PartidoDtoPartidoConversor.toRestPartidoDtos(partido);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestPartidoDtoConversor.toArrayNode(partidoDtos), null);
        } else {
            Long id = ServletUtils.getIdFromPath(req, "partido");

            Partido partido = PartidoServiceFactory.getService().findPartido(id);

            RestPartidoDto partidoDto = PartidoDtoPartidoConversor.toRestPartidoDto(partido);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestPartidoDtoConversor.toObjectNode(partidoDto), null);
        }
    }


    //Corresponde al Caso de Uso 1 - addPartido
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path != null && path.length() > 0) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: " + "invalid path " + path)),
                    null);
            return;
        }
        RestPartidoDto partidoDto;
        try {
            partidoDto = JsonToRestPartidoDtoConversor.toRestPartidoDto(req.getInputStream());
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, AppExceptionToJsonConversor
                    .toInputValidationException(new InputValidationException(ex.getMessage())), null);
            return;
        }
        Partido partido = PartidoDtoPartidoConversor.toPartido(partidoDto);
        try {
            partido = PartidoServiceFactory.getService().addPartido(partido);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    AppExceptionToJsonConversor.toInputValidationException(ex), null);
            return;
        }
        partidoDto = PartidoDtoPartidoConversor.toRestPartidoDto(partido);

        String raceURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + partido.getPartidoID();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", raceURL);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestPartidoDtoConversor.toObjectNode(partidoDto), headers);
    }


}


