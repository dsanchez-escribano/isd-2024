package es.udc.ws.app.client.service.dto;

import es.udc.ws.util.exceptions.InputValidationException;

public class ClientEntradaDto {

    private Long entradaID;             // Id de Entrada
    private String email;               // email
    private String ultimosDigitosTarjeta;  // Solo almacena los últimos 4 dígitos de la tarjeta
    private int numeroEntradas;         // número de entradas compradas
    private String fechaCompra;          // Fecha de la compra
    private boolean estado;             // Estado de la compra
    private Long partidoID;             // Id de Partido al que hace referencia

    public ClientEntradaDto(Long entradaID, String email, String ultimosDigitosTarjeta, int numeroEntradas, String fechaCompra, boolean estado, Long partidoID) {
        this.entradaID = entradaID;
        this.email = email;
        this.ultimosDigitosTarjeta = ultimosDigitosTarjeta;
        this.numeroEntradas = numeroEntradas;
        this.fechaCompra = fechaCompra;
        this.estado = estado;
        this.partidoID = partidoID;
    }

    public Long getEntradaID() {
        return entradaID;
    }

    public String getEmail() {
        return email;
    }

    public String getUltimosDigitosTarjeta() {
        return ultimosDigitosTarjeta;
    }

    public int getNumeroEntradas() {
        return numeroEntradas;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public boolean getEstado() {
        return estado;
    }

    public Long getPartidoID() {
        return partidoID;
    }

    //* =====> Setters de la clase
    public void setEntradaID(Long entradaID) throws InputValidationException {
        if (entradaID != null && entradaID >= 0)
            this.entradaID = entradaID;
        else
            throw new InputValidationException("Invalid argument: [" + entradaID + "]");
    }

    public void setEmail(String email) throws InputValidationException {
        if (email != null && !email.equals(""))
            this.email = email;
        else
            throw new InputValidationException("Invalid argument: [" + email + "]");
    }

    public void setUltimosDigitosTarjeta(String ultimosDigitosTarjeta) throws InputValidationException {
        if (ultimosDigitosTarjeta != null && !ultimosDigitosTarjeta.equals(""))
            this.ultimosDigitosTarjeta = ultimosDigitosTarjeta;
        else
            throw new InputValidationException("Invalid argument: [" + ultimosDigitosTarjeta + "]");
    }

    public void setPartidoID(Long partidoID) throws InputValidationException {
        if (partidoID != null && partidoID >= 0)
            this.partidoID = partidoID;
        else
            throw new InputValidationException("Invalid argument: [" + partidoID + "]");
    }

    public void setNumeroEntradas(int numeroEntradas) throws InputValidationException {
        if (numeroEntradas >= 0)
            this.numeroEntradas = numeroEntradas;
        else
            throw new InputValidationException("Invalid argument: [" + numeroEntradas + "]");
    }

    public void setFechaCompra(String fechaCompra) throws InputValidationException {
        if (fechaCompra != null)
            this.fechaCompra = fechaCompra;
        else
            throw new InputValidationException("Invalid argument: [" + fechaCompra + "]");
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }


}
