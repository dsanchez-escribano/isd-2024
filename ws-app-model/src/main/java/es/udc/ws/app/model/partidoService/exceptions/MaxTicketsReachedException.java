package es.udc.ws.app.model.partidoService.exceptions;

public class MaxTicketsReachedException extends Exception {

    private Long partidoID;

    public MaxTicketsReachedException(Long partidoID){
        super("[PaID:" + partidoID + "] max tickets reached");
        this.partidoID = partidoID;
    }

    public Long getPartidoID() {
        return partidoID;
    }

    public void setPartidoID(Long partidoID) {
        this.partidoID = partidoID;
    }
}