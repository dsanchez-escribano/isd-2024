package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestPartidoDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public class JsonToRestPartidoDtoConversor {

    public static ObjectNode toObjectNode(RestPartidoDto partido) {

        ObjectNode partidoObject = JsonNodeFactory.instance.objectNode();

        partidoObject.put("partidoId", partido.getPartidoID()).
                put("nombreVisitante", partido.getNombreVisitante()).
                put("fechaInicio", partido.getFechaInicio().toString()).
                put("entradasMaximas", partido.getMaximoEntradas()).
                put("entradasVendidas" , partido.getEntradasVendidas()).
                put("precio", partido.getPrecio());

        return partidoObject;
    }

    public static ArrayNode toArrayNode(List<RestPartidoDto> partidos) {

        ArrayNode partidosNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < partidos.size(); i++) {
            RestPartidoDto partidoDto = partidos.get(i);
            ObjectNode partidoObject = toObjectNode(partidoDto);
            partidosNode.add(partidoObject);
        }

        return partidosNode;
    }

    public static RestPartidoDto toRestPartidoDto(InputStream jsonPartido) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonPartido);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode partidoObject = (ObjectNode) rootNode;

                JsonNode partidoIdNode = partidoObject.get("partidoID");
                Long partidoId = (partidoIdNode != null) ? partidoIdNode.longValue() : null;
                String nombreVisitante = partidoObject.get("nombreVisitante").textValue().trim();
                String strFechaInicio = partidoObject.get("fechaInicio").textValue().trim();
                LocalDateTime fechaInicio = LocalDateTime.parse(strFechaInicio);
                int maximoEntradas = partidoObject.get("maximoEntradas").intValue();
                int entradasVendidas = partidoObject.get("entradasVendidas").intValue();
                float precio = partidoObject.get("precio").floatValue();

                return new RestPartidoDto(partidoId, nombreVisitante, fechaInicio, maximoEntradas, entradasVendidas,precio);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
