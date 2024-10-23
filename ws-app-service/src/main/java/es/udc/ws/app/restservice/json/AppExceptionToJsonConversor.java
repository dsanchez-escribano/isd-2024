package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.partidoService.exceptions.AlreadyTakenException;
import es.udc.ws.app.model.partidoService.exceptions.BuyTooLateException;
import es.udc.ws.app.model.partidoService.exceptions.InvalidCardException;
import es.udc.ws.app.model.partidoService.exceptions.MaxTicketsReachedException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class AppExceptionToJsonConversor {
    public static ObjectNode toInputValidationException(InputValidationException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "InputValidation").
                put("message", ex.getMessage());

        return exceptionJson;
    }

    public static ObjectNode toInstanceNotFoundException(InstanceNotFoundException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "InstanceNotFound").
                put("instanceId", ex.getInstanceId().toString()).
                put("instanceType", ex.getInstanceType());

        return exceptionJson;
    }

    public static ObjectNode toResponderAlreadyTakenException(AlreadyTakenException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "ResponderAlreadyTaken").
                put("IdEntrada", ex.getEntradaID());

        return exceptionJson;
    }

    public static ObjectNode toBuyTooLateException(BuyTooLateException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "BuyTooLate").
                put("IdEntrada", ex.getPartidoID());

        return exceptionJson;
    }

    public static ObjectNode toInvalidCardException(InvalidCardException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "InvalidCard").
                put("IdEntrada", ex.getEntradaID());

        return exceptionJson;
    }

    public static ObjectNode toMaxTicketsReachedException(MaxTicketsReachedException ex) {
        ObjectNode exceptionJson = JsonNodeFactory.instance.objectNode();

        exceptionJson.put("exceptionType", "MaxTicketsReached").
                put("IdPartido", ex.getPartidoID());

        return exceptionJson;
    }
}
