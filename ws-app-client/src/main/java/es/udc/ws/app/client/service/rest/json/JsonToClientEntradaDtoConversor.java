package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEntradaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEntradaDtoConversor {

    public static ObjectNode toObjectNode(ClientEntradaDto entrada){
        throw new UnsupportedOperationException();
    }

    public static ClientEntradaDto toClientEntradaDto(InputStream jsonEntrada) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEntrada);
            return toClientEntradaDto (rootNode);
        } catch (Exception e) {

            throw new ParsingException(e);
        }
    }

    private static ClientEntradaDto toClientEntradaDto(JsonNode jsonNode) throws ParsingException{
        try{
            if (jsonNode.getNodeType() != JsonNodeType.OBJECT){
                throw new ParsingException("Unrecognized JSON (expected Object), got " + jsonNode.getNodeType().toString());
            }

            ObjectNode entradaObject = (ObjectNode) jsonNode;

            JsonNode entradaIdNode = entradaObject.get("entradaID");
            Long entradaID = (entradaIdNode != null) ? entradaIdNode.longValue() : null;
            String mail = entradaObject.get("email").textValue().trim();
            String ultimosDigitosTargeta = entradaObject.get("numeroTarjeta").textValue().trim();
            String fechaCompra = entradaObject.get("fechaCompra").textValue().trim();
            int numeroEntradas = entradaObject.get("numeroEntradas").intValue();
            boolean estado = entradaObject.get("estado").booleanValue();
            Long partidoID = entradaObject.get("partidoID").longValue();

            return new ClientEntradaDto(entradaID, mail, ultimosDigitosTargeta, numeroEntradas, fechaCompra, estado, partidoID);

        } catch (ParsingException ex){
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientEntradaDto> toClientEntradaDtos(InputStream content) throws ParsingException{
        try{
            JsonNode rootNode = ObjectMapperFactory.instance().readTree(content);

            if (rootNode.getNodeType() != JsonNodeType.ARRAY){
                throw new ParsingException("Unrecognised JSON (expected ARRAY), got "+rootNode.getNodeType().toString());
            }
            ArrayNode entradaArray = (ArrayNode) rootNode;
            List<ClientEntradaDto> entradaDtoList = new ArrayList<>();

            for (JsonNode jsonNode : entradaArray) {
                entradaDtoList.add(toClientEntradaDto(jsonNode));
            }
            return entradaDtoList;

        }catch (ParsingException ex){
            throw ex;
        }catch (Exception e){
            throw new ParsingException(e);
        }
    }
}


