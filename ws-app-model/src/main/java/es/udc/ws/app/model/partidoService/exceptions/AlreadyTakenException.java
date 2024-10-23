package es.udc.ws.app.model.partidoService.exceptions;

public class AlreadyTakenException extends Exception {

    private Long entradaID;

    public AlreadyTakenException(Long entradaID) {
        super("[EnID:" + entradaID + "] already taken");
        this.entradaID = entradaID;
    }

    public Long getEntradaID() {
        return entradaID;
    }

    public void setEntradaID(Long entradaID) {
        this.entradaID = entradaID;
    }
}
