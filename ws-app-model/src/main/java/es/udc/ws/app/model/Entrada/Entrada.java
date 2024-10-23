package es.udc.ws.app.model.Entrada;

import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;

public class Entrada{

    // ===== Atributos =====
    private Long entradaID;             // Id de Entrada
    private String email;               // email
    private String numeroTarjeta;       // número de targeta
    private int numeroEntradas;         // número de entradas compadas
    private LocalDateTime fechaCompra;  // Fecha de la compra
    private boolean estado;             // Estado de la compra
    private Long partidoID;             // Id de Partido al que hace referencia

    // ===== Constructor =====
    public Entrada(Long partidoID, String email, String numeroTarjeta, int numeroEntradas){

        this.email = email;
        this.numeroTarjeta = numeroTarjeta;
        this.numeroEntradas = numeroEntradas;
        this.partidoID = partidoID;
    }

    /* Empleamos el constructor superior para optimizar código
    public Entrada(Long entradaID, String email, String numeroTarjeta, int numeroEntradas, LocalDateTime fechaCompra, boolean estado, Long partidoID){
        this(partidoID, email, numeroTarjeta, numeroEntradas);
        this.entradaID = entradaID;
        this.fechaCompra = ((fechaCompra != null)? fechaCompra.withNano(0) : null);
        this.estado = estado;
    }*/


    public Entrada(Long entradaID, String email, String numeroTarjeta, int numeroEntradas, LocalDateTime fechaCompra, boolean estado, Long partidoID) {

        this.entradaID=entradaID;
        this.email=email;
        this.numeroTarjeta=numeroTarjeta;
        this.numeroEntradas=numeroEntradas;
        this.fechaCompra=fechaCompra;
        this.estado=estado;
        this.partidoID=partidoID;

    }


    // ===== Métodos privados =====
    private int getInfOfLocalDateTime(LocalDateTime ldt){
        return ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getDayOfYear() + ldt.getHour() + ldt.getMinute() + ldt.getSecond();
    }

    // =====> Getters de la clase
    public Long getEntradaID(){
        return this.entradaID;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public String getNumeroTarjeta(){
        return this.numeroTarjeta;
    }
    
    public Long getPartidoID(){
        return this.partidoID;
    }
    
    public int getNumeroEntradas(){
        return this.numeroEntradas;
    }
    
    public LocalDateTime getFechaCompra(){
        return this.fechaCompra;
    }
    
    public boolean getEstado(){
        return this.estado;
    }


    // =====> Setters de la clase
    public void setEntradaID(Long entradaID) throws InputValidationException {
        if(entradaID != null && entradaID >= 0)
            this.entradaID = entradaID;
        else
            throw new InputValidationException("Invalid argument: [" + entradaID  + "]");
    }
    
    public void setEmail(String email) throws InputValidationException {
        if(email != null && !email.equals(""))
            this.email = email;
        else
            throw new InputValidationException("Invalid argument: [" + email  + "]");
    }
    
    public void setNumeroTarjeta(String numeroTarjeta) throws InputValidationException {
        if(numeroTarjeta != null && !numeroTarjeta.equals(""))
            this.numeroTarjeta = numeroTarjeta;
        else
            throw new InputValidationException("Invalid argument: [" + numeroTarjeta  + "]");
    }
    
    public void setPartidoID(Long partidoID) throws InputValidationException {
        if(partidoID != null && partidoID >= 0)
            this.partidoID = partidoID;
        else
            throw new InputValidationException("Invalid argument: [" + partidoID  + "]");
    }
    
    public void setNumeroEntradas(int numeroEntradas) throws InputValidationException {
        if(numeroEntradas >= 0)
            this.numeroEntradas = numeroEntradas;
        else
            throw new InputValidationException("Invalid argument: [" + numeroEntradas + "]");
    }
    
    public void setFechaCompra(LocalDateTime fechaCompra) throws InputValidationException {
        if(fechaCompra != null)
            this.fechaCompra = fechaCompra;
        else
            throw new InputValidationException("Invalid argument: [" + fechaCompra + "]");
    }
    
    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // =====>  hashCode
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + Math.toIntExact(this.entradaID);
        result = prime * result + ((this.email == null)? 0 : Integer.parseInt(this.email));
        result = prime * result + ((this.numeroTarjeta == null)? 0 : Integer.parseInt(this.numeroTarjeta));
        result = prime * result + Math.toIntExact(this.partidoID);
        result = prime * result + this.numeroEntradas;
        result = prime * result + ((this.fechaCompra == null)? 0 : this.getInfOfLocalDateTime(this.fechaCompra));
        result = prime * result + ((this.estado)? 1 : 0);

        return result;
    }

    // =====> Equals
    @Override
    public boolean equals(Object obj){
        Entrada newEntrada = (Entrada) obj;
        if(this == newEntrada) return true;
        if(newEntrada == null) return false;
        if(getClass() != newEntrada.getClass()) return false;
 
        boolean check = (this.entradaID.equals(newEntrada.getEntradaID()));
        check |= this.email.equals(newEntrada.getEmail());
        check |= this.numeroTarjeta.equals(newEntrada.getNumeroTarjeta());
        check |= this.partidoID.equals(newEntrada.getPartidoID());
        check |= this.numeroEntradas == newEntrada.getNumeroEntradas();
        check |= this.fechaCompra.equals(newEntrada.getFechaCompra());
        check |= this.estado == newEntrada.getEstado();

        return check;
    }

    // =====> toString
    @Override
    public String toString(){
        return "Entrada{" +
                "entradaID=" + this.entradaID +
                ",email=" + this.email +
                ",numeroTarjeta=" + this.numeroTarjeta +
                ",numeroEntradas=" + this.numeroEntradas +
                ",fechaCompra=" + this.fechaCompra +
                ",estado=" + this.estado +
                ",partidoID=" + this.partidoID +
                "}";
    }
}