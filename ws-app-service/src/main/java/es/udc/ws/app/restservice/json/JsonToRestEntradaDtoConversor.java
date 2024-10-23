package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEntradaDto;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.List;

public class JsonToRestEntradaDtoConversor {

    public static ObjectNode toObjectNode(RestEntradaDto entrada) {

        ObjectNode entradaNode = JsonNodeFactory.instance.objectNode();

        if (entrada != null) {
            entradaNode.put("entradaID", entrada.getEntradaID());
            entradaNode.put("email", entrada.getEmail())
                    .put("numeroTarjeta", entrada.getUltimosDigitosTarjeta())
                    .put("numeroEntradas", entrada.getNumeroEntradas())
                    .put("fechaCompra", entrada.getFechaCompra())
                    .put("estado", entrada.getEstado())
                    .put("partidoID", entrada.getPartidoID());
        }

        return entradaNode;
    }

    public static ArrayNode toArrayNode(List<RestEntradaDto> entradas) {

        ArrayNode entradasNode = JsonNodeFactory.instance.arrayNode();
        for (RestEntradaDto entradaDto : entradas) {
            ObjectNode entradaObject = toObjectNode(entradaDto);
            entradasNode.add(entradaObject);
        }

        return entradasNode;
    }

    public static RestEntradaDto toServiceEntradaDto(InputStream jsonEntrada) throws ParsingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonEntrada);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode entradaObject = (ObjectNode) rootNode;

                JsonNode entradaIdNode = entradaObject.get("entradaID");
                Long entradaID = (entradaIdNode != null) ? entradaIdNode.longValue() : null;

                String email = entradaObject.get("email").textValue().trim();
                String ultimosDigitosTarjeta = entradaObject.get("numeroTarjeta").textValue().trim();
                int numeroEntradas =  entradaObject.get("numeroEntradas").intValue();
                String fechaCompra = entradaObject.get("fechaCompra").textValue().trim();
                boolean estado = entradaObject.get("estado").booleanValue();
                Long partidoID = entradaObject.get("partidoID").longValue();

                return new RestEntradaDto(entradaID, email, ultimosDigitosTarjeta,
                        numeroEntradas, fechaCompra, estado, partidoID);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}