package es.udc.ws.app.model.partidoService.exceptions;

public class InvalidCardException extends Exception {

    private Long entradaID;

    public InvalidCardException(Long entradaID) {
        super("[EnID:" + entradaID + "] invalid card");
        this.entradaID = entradaID;
    }

    public Long getEntradaID() {
        return entradaID;
    }

    public void setEntradaID(Long entradaID) {
        this.entradaID = entradaID;
    }
}