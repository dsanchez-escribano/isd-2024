package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientPartidoDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientPartidoDtoConversor {
        public static ObjectNode toObjectNode(ClientPartidoDto partido) throws IOException {
            ObjectNode partidoObject = JsonNodeFactory.instance.objectNode();

            if (partido.getPartidoID() != null) {
                partidoObject.put("partidoId", partido.getPartidoID());
            }
            partidoObject.put("partidoId", partido.getPartidoID()).
                    put("nombreVisitante", partido.getNombreVisitante()).
                    put("fechaInicio", partido.getFechaInicio().toString()).
                    put("maximoEntradas", partido.getMaximoEntradas()).
                    put("entradasVendidas" , partido.getEntradasVendidas()).
                    put("precio", partido.getPrecio());

            return partidoObject;
        }

        public static ClientPartidoDto toClientPartidoDto(InputStream jsonPartido) throws ParsingException {
            try {
                ObjectMapper objectMapper = ObjectMapperFactory.instance();
                JsonNode rootNode = objectMapper.readTree(jsonPartido);
                if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                    throw new ParsingException("Unrecognized JSON (object expected)");
                } else {
                    return toClientPartidoDto(rootNode);
                }
            } catch (ParsingException ex) {
                throw ex;
            } catch (Exception e) {
                throw new ParsingException(e);
            }
        }

        public static List<ClientPartidoDto> toClientPartidoDtos(InputStream jsonPartido) throws ParsingException {
            try {

                ObjectMapper objectMapper = ObjectMapperFactory.instance();
                JsonNode rootNode = objectMapper.readTree(jsonPartido);
                if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                    throw new ParsingException("Unrecognized JSON (array expected)");
                } else {
                    ArrayNode partidosArray = (ArrayNode) rootNode;
                    List<ClientPartidoDto> partidoDtos = new ArrayList<>(partidosArray.size());
                    for (JsonNode partidoNode : partidosArray) {
                        partidoDtos.add(toClientPartidoDto(partidoNode));
                    }

                    return partidoDtos;
                }
            } catch (ParsingException ex) {
                throw ex;
            } catch (Exception e) {
                throw new ParsingException(e);
            }
        }

        private static ClientPartidoDto toClientPartidoDto(JsonNode partidoNode) throws ParsingException {
            if (partidoNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode partidoObject = (ObjectNode) partidoNode;

                JsonNode partidoIdNode = partidoObject.get("partidoId");
                Long partidoId = (partidoIdNode != null) ? partidoIdNode.longValue() : null;

                String nombreVisitante = partidoObject.get("nombreVisitante").textValue().trim();
                String strFechaInicio = partidoObject.get("fechaInicio").textValue().trim();
                LocalDateTime fechaInicio = LocalDateTime.parse(strFechaInicio);
                int maximoEntradas = partidoObject.get("entradasMaximas").intValue();
                int entradasVendidas = partidoObject.get("entradasVendidas").intValue();
                float precio = partidoObject.get("precio").floatValue();

                return new ClientPartidoDto(partidoId,nombreVisitante, fechaInicio, maximoEntradas,entradasVendidas,
                        precio);
            }
        }
}
