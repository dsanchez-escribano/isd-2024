package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.dto.ServerException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {
    public static Exception fromNotFoundErrorCode(InputStream content) {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(content);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }

            String errorType = rootNode.get("errorType").textValue();
            if (errorType.equals("InstanceNotFound")) {
                return toInstanceNotFoundException(rootNode);
            }

            throw new ParsingException("Unrecognized error type: " + errorType);

        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }


    public static Exception fromBadRequestErrorCode(InputStream content) {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(content);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }

            String errorType = rootNode.get("exceptionType").textValue();
            switch(errorType){
                case "InputValidation":
                    return toInputValidationException(rootNode);
                case "AlreadyTaken":
                case "InvalidCard":
                case "BuyTooLate":
                case "MaxTicketsReached":
                    return toServerException(rootNode);
                default:
                    throw new ParsingException("Unrecognized error type: " + errorType);
            }

        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromGoneErrorCode(InputStream content) {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(content);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            }

            String errorType = rootNode.get("errorType").textValue();
            switch(errorType){
                case "RaceFull":
                case "InscriptionClosed":
                    return toServerException(rootNode);
                default:
                    throw new ParsingException("Unrecognized error type: " + errorType);
            }

        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ServerException toServerException(JsonNode rootNode){
        String errorType = rootNode.get("errorType").textValue();
        String message = rootNode.get("message").textValue();
        return new ServerException(errorType + ": " + message);
    }
}
