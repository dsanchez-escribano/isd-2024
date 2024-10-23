package es.udc.ws.app.model.partidoService.exceptions;

public class BuyTooLateException extends Exception {

    private Long partidoID;

    public BuyTooLateException(Long partidoID) {
        super("[PaID:" + partidoID + "] buy too late");
        this.partidoID = partidoID;
    }

    public Long getPartidoID() {
        return partidoID;
    }

    public void setPartidoID(Long partidoID) {
        this.partidoID = partidoID;
    }
}