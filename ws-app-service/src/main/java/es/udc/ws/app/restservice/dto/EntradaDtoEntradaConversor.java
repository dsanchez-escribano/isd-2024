package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.Entrada.Entrada;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntradaDtoEntradaConversor {

    public static List<RestEntradaDto> toRestEntradaDtos(List<Entrada> entradas) {
        List<RestEntradaDto> entradasDtos = new ArrayList<>(entradas.size());
        for (int i = 0; i < entradas.size(); i++) {
            Entrada entrada = entradas.get(i);
            entradasDtos.add(toRestEntradaDto(entrada));
        }
        return entradasDtos;
    }

    public static Entrada toEntrada(RestEntradaDto e) {
        LocalDateTime fechaCompra = LocalDateTime.parse(e.getFechaCompra());
        return new Entrada(
                e.getEntradaID(),
                e.getEmail(),
                e.getUltimosDigitosTarjeta(), // Utilizamos el nuevo método para obtener los últimos 4 dígitos
                e.getNumeroEntradas(),
                fechaCompra,
                e.getEstado(),
                e.getPartidoID());
    }

    public static RestEntradaDto toRestEntradaDto(Entrada e) {
        return new RestEntradaDto(
                e.getEntradaID(),
                e.getEmail(),
                obtenerUltimosDigitosTarjeta(e.getNumeroTarjeta()),
                e.getNumeroEntradas(),
                e.getFechaCompra().toString(),
                e.getEstado(),
                e.getPartidoID());
    }

    private static String obtenerUltimosDigitosTarjeta(String numeroTarjeta) {
        if (numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            return numeroTarjeta.substring(numeroTarjeta.length() - 4);
        }
        // Manejar casos donde el númeroTarjeta no tenga al menos 4 dígitos
        return null;
    }
}

