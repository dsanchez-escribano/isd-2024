package es.udc.ws.app.model.Partido;

import es.udc.ws.util.exceptions.InputValidationException;
import java.time.LocalDateTime;

public class Partido {
    
    // ===== Atributos =====
    private Long partidoID;            // Id de partido
    private String nombreVisitante;    // Nombre del equipo visitante
    private LocalDateTime fechaInicio; // Fecha/Hora inicio del partido
    private LocalDateTime fechaAlta;   // Fecha/Hora alta del partido
    private int maximoEntradas;        // Número máximo de entradas permitidas para el partido
    private int entradasVendidas;      // Número de entradas vendidas
    private Float precio;              // Precio de las entradas del partido


    // ===== Constructores =====
    public Partido(String nombreVisitante, LocalDateTime fechaInicio, int maximoEntradas, Float precio) {
        this.nombreVisitante = nombreVisitante;
        this.fechaInicio = ((fechaInicio != null)? fechaInicio.withNano(0) : null);
        this.maximoEntradas= maximoEntradas;
        this.precio= precio;
    }

    // Empleamos el constructor superior para optimizar código de codigo
    public Partido(Long partidoID, String nombreVisitante, LocalDateTime fechaInicio, LocalDateTime fechaAlta, int maximoEntradas, int entradasVendidas, Float precio){
        this(nombreVisitante, fechaInicio, maximoEntradas, precio);
        this.partidoID = partidoID;
        this.fechaAlta = ((fechaAlta != null)? fechaAlta.withNano(0) : null);
        this.entradasVendidas = entradasVendidas;
        this.precio = precio;
    }

    public Partido(Long partidoID, String nombreVisitante, LocalDateTime fechaInicio, int maximoEntradas, int entradasVendidas, Float precio) {
        this.partidoID=partidoID;
        this.nombreVisitante=nombreVisitante;
        this.fechaInicio=fechaInicio;
        this.maximoEntradas=maximoEntradas;
        this.entradasVendidas=entradasVendidas;
        this.precio=precio;
    }


    // ===== Métodos privados =====
    private int getInfOfLocalDateTime(LocalDateTime ldt){
        return ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getDayOfYear() + ldt.getHour() + ldt.getMinute() + ldt.getSecond();
    }


    // =====> Getters de la clase
    public Long getPartidoID(){
        return this.partidoID;
    }

    public String getNombreVisitante() {
        return this.nombreVisitante;
    }
    
    public LocalDateTime getFechaInicio(){
        return this.fechaInicio;
    }
    
    public LocalDateTime getFechaAlta(){
        return this.fechaAlta;
    }
    
    public int getMaximoEntradas(){
        return this.maximoEntradas;
    }
    
    public int getEntradasVendidas(){
        return this.entradasVendidas;
    }
    
    public Float getPrecio(){
        return this.precio;
    }


    // =====> Setters de la clase
    public void setNombreVisitante(String nombreVisitante) throws InputValidationException {
        if(nombreVisitante != null && !nombreVisitante.equals(""))
            this.nombreVisitante = nombreVisitante;
        else
            throw new InputValidationException("Invalid argument: [" + nombreVisitante + "]");
    }

    public void setFechaInicio(LocalDateTime fechaInicio) throws InputValidationException {
        if(fechaInicio != null)
            this.fechaInicio = fechaInicio.withNano(0);
        else
            throw new InputValidationException("Invalid argument: [" + fechaInicio.toString() + "]");
    }
    
    public void setFechaAlta(LocalDateTime fechaAlta) throws InputValidationException {
        if(fechaAlta != null)
            this.fechaAlta = fechaAlta;
        else
            throw new InputValidationException("Invalid argument: [" + fechaAlta.toString() + "]");
    }
    
    public void setMaximoEntradas(int maximoEntradas) throws InputValidationException {
        if(maximoEntradas >= 0)
            this.maximoEntradas = maximoEntradas;
        else
            throw new InputValidationException("Invalid argument: [" + maximoEntradas + "]");
    }
    
    public void setEntradasVendidas(int entradasVendidas) throws InputValidationException {
        if(entradasVendidas >= 0)
            this.entradasVendidas = entradasVendidas;
        else
            throw new InputValidationException("Invalid argument: [" + entradasVendidas + "]");
    }
    
    public void setPrecio(Float precio) throws InputValidationException {
        if(precio >= 0F)
            this.precio = precio;
        else
            throw new InputValidationException("Invalid argument: [" + precio + "]");
    }


    //  =====> HashCode
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + Math.toIntExact(this.partidoID);
        result = prime * result + ((this.nombreVisitante == null)? 0 : Integer.parseInt(this.nombreVisitante));
        result = prime * result + ((this.fechaInicio == null)? 0 : this.getInfOfLocalDateTime(this.fechaInicio));
        result = prime * result + ((this.fechaAlta == null)? 0 : this.getInfOfLocalDateTime(this.fechaAlta));
        result = prime * result + this.maximoEntradas;
        result = prime * result + this.entradasVendidas;
        result = prime * result + Float.floatToIntBits(Math.round(this.precio));

        return result;
    }


    // =====> Equals
    @Override
    public boolean equals(Object obj){
        Partido newPartido = (Partido) obj;
        if(this == newPartido) return true;
        if(newPartido == null) return false;
        if(getClass() != newPartido.getClass()) return false;

        boolean check = this.partidoID.equals(newPartido.getPartidoID());
        check |= this.nombreVisitante.equals(newPartido.getNombreVisitante());
        check |= this.fechaInicio.equals(newPartido.getFechaInicio());
        check |= this.fechaAlta.equals(newPartido.getFechaAlta());
        check |= this.maximoEntradas == newPartido.getMaximoEntradas();
        check |= this.entradasVendidas == newPartido.getEntradasVendidas();
        check |= this.precio.equals(newPartido.getPrecio());

        return check;
    }


    // =====> ToString
    @Override
    public String toString(){
        return "Partido{"+
                "partidoID="+this.partidoID+
                ",nombreVisitante='"+this.nombreVisitante+"'"+
                ",fechaInicio="+this.fechaInicio+
                ",fechaAlta="+this.fechaAlta+
                ",maximoEntradas="+this.maximoEntradas+
                ",entradasVendidas="+this.entradasVendidas+
                ",precio="+this.precio+ "}";
    }
}
