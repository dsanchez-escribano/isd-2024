package es.udc.ws.app.client.service.dto;

import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public class ClientPartidoDto {
    private Long partidoID;            // Id de partido
    private String nombreVisitante;    // Nombre del equipo visitante
    private LocalDateTime fechaInicio;        // Fecha/Hora inicio del partido
    private int maximoEntradas;        // Número máximo de entradas permitidas para el partido
    private int entradasVendidas;      // Número de entradas vendidas
    private Float precio;              // Precio de las entradas del partido

    public ClientPartidoDto(Long partidoID, String nombreVisitante, LocalDateTime fechaInicio, int maximoEntradas, int entradasVendidas, Float precio) {
        this.partidoID = partidoID;
        this.nombreVisitante = nombreVisitante;
        this.fechaInicio = fechaInicio;
        this.maximoEntradas = maximoEntradas;
        this.entradasVendidas = entradasVendidas;
        this.precio = precio;

    }

    public ClientPartidoDto(String nombreVisitante, LocalDateTime fechaInicio, int maximoEntradas, int entradasVendidas, Float precio) {
        this.nombreVisitante = nombreVisitante;
        this.fechaInicio = fechaInicio;
        this.maximoEntradas = maximoEntradas;
        this.entradasVendidas = entradasVendidas;
        this.precio = precio;

    }


    public Long getPartidoID() {

        return partidoID;
    }

    public String getNombreVisitante() {

        return nombreVisitante;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public int getMaximoEntradas() {
        return maximoEntradas;
    }

    public int getEntradasVendidas() {
        return entradasVendidas;
    }

    public Float getPrecio() {
        return precio;
    }

    // =====> Setters de la clase
    public void setNombreVisitante(String nombreVisitante) throws InputValidationException {
        if (nombreVisitante != null && !nombreVisitante.equals(""))
            this.nombreVisitante = nombreVisitante;
        else
            throw new InputValidationException("Invalid argument: [" + nombreVisitante + "]");
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }


    public void setMaximoEntradas(int maximoEntradas) throws InputValidationException {
        if (maximoEntradas >= 0)
            this.maximoEntradas = maximoEntradas;
        else
            throw new InputValidationException("Invalid argument: [" + maximoEntradas + "]");
    }

    public void setEntradasVendidas(int entradasVendidas) throws InputValidationException {
        if (entradasVendidas >= 0)
            this.entradasVendidas = entradasVendidas;
        else
            throw new InputValidationException("Invalid argument: [" + entradasVendidas + "]");
    }

    public void setPrecio(Float precio) throws InputValidationException {
        if (precio >= 0F)
            this.precio = precio;
        else
            throw new InputValidationException("Invalid argument: [" + precio + "]");
    }


    //  =====> HashCode

    @Override
    public int hashCode() {
        return Objects.hash(partidoID, nombreVisitante, fechaInicio, maximoEntradas, entradasVendidas, precio);
    }

    // =====> Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientPartidoDto that)) return false;
        return getMaximoEntradas() == that.getMaximoEntradas() && getEntradasVendidas() == that.getEntradasVendidas() && Objects.equals(getPartidoID(), that.getPartidoID()) && Objects.equals(getNombreVisitante(), that.getNombreVisitante()) && Objects.equals(getFechaInicio(), that.getFechaInicio()) && Objects.equals(getPrecio(), that.getPrecio());
    }
}

