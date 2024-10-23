package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.Partido.Partido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidoDtoPartidoConversor {
    public static List<RestPartidoDto> toRestPartidoDtos(List<Partido> partidos) {
        List<RestPartidoDto> partidosDtos = new ArrayList<>(partidos.size());
        for (int i = 0; i < partidos.size(); i++) {
            Partido partido = partidos.get(i);
            partidosDtos.add(toRestPartidoDto(partido));
        }
        return partidosDtos;
    }

    public static Partido toPartido(RestPartidoDto p) {
        LocalDateTime fechaInicio = p.getFechaInicio();

        return new Partido(
                p.getPartidoID(),
                p.getNombreVisitante(),
                fechaInicio,
                p.getMaximoEntradas(),
                p.getEntradasVendidas(),
                p.getPrecio());

    }

    public static RestPartidoDto toRestPartidoDto(Partido p) {
        return new RestPartidoDto(
                p.getPartidoID(),
                p.getNombreVisitante(),
                p.getFechaInicio(),
                p.getMaximoEntradas(),
                p.getEntradasVendidas(),
                p.getPrecio());

    }
}
