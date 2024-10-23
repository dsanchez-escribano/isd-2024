package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientPartidoService;
import es.udc.ws.app.client.service.ClientPartidoServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEntradaDto;
import es.udc.ws.app.client.service.dto.ClientPartidoDto;
import es.udc.ws.app.client.service.dto.ServerException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientPartidoService clientPartidoService =
                ClientPartidoServiceFactory.getService();
        if ("-addMatch".equalsIgnoreCase(args[0])) {
            validateArgs(args, 6, new int[]{3, 4, 5});

            // [add] PartidoServiceClient -a <nombreVisitante> <fechaInicio> <maximoEntradas> <entradasVendidas> <precio>

            try {
                Long partidoId = clientPartidoService.addPartido(new ClientPartidoDto(
                        args[1], LocalDateTime.parse(args[2]), Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]), Float.valueOf(args[5])));

                System.out.println("Partido " + partidoId + " created sucessfully");

            } catch (NumberFormatException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findMatches".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{});

            // [find] PartidoServiceClient -f <fecha>

            try {
                List<ClientPartidoDto> partidos = clientPartidoService.findByDate(LocalDate.now(), LocalDate.parse(args[1]));
                System.out.println("Found " + partidos.size() +
                        " match(es) with dates between now and '" + args[1] + "'");
                for (int i = 0; i < partidos.size(); i++) {
                    ClientPartidoDto partidoDto = partidos.get(i);
                    System.out.println("Id: " + partidoDto.getPartidoID() +
                            ", Nombre Visitante: " + partidoDto.getNombreVisitante() +
                            ", Fecha: " + partidoDto.getFechaInicio() +
                            ", Maximo de entradas: " + partidoDto.getMaximoEntradas() +
                            ", Entradas vendidas: " + partidoDto.getEntradasVendidas() +
                            ", Precio: " + partidoDto.getPrecio());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }


        } else if ("-findMatch".equals(args[0])) {
            validateArgs(args, 2, new int[]{1});

            // [get Partido] PartidoServiceClient -p <partidoId>

            try {
                ClientPartidoDto partidoDto = clientPartidoService.findPartido(Long.parseLong(args[1]));
                System.out.println("Id: " + partidoDto.getPartidoID() +
                        ", Nombre Visitante: " + partidoDto.getNombreVisitante() +
                        ", Fecha: " + partidoDto.getFechaInicio() +
                        ", Maximo de entradas" + partidoDto.getMaximoEntradas() +
                        ", Entradas vendidas: " + partidoDto.getEntradasVendidas() +
                        ", Precio: " + partidoDto.getPrecio());
            } catch (InstanceNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else if ("-buy".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[]{1,4});

            // [buytickets] PartidoServiceClient -t <partidoId> <email> <creditCardNumber> <numeroEntradas>

            try {
                Long entradaId = clientPartidoService.buytickets(Long.parseLong(args[1]), args[2], args[3],Integer.parseInt(args[4]));
                System.out.println("Entrada para el partido " + args[1] +
                        " comprada con éxito con el ID de entrada " +
                        entradaId);
            } catch (NumberFormatException | InputValidationException | InstanceNotFoundException |
                     ServerException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-findPurchases".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{});

            // [findEntradas] PartidoServiceClient -e <email>

            try {
                List<ClientEntradaDto> entradas = clientPartidoService.findEntradas(args[1]);
                System.out.println("Encontradas " + entradas.size() +
                        " entrada(s) para el usuario con email '" + args[1] + "'");
                for (int i = 0; i < entradas.size(); i++) {
                    ClientEntradaDto entradaDto = entradas.get(i);
                    System.out.println("ID de Entrada: " + entradaDto.getEntradaID() +
                            ", Número de Entradas: " + entradaDto.getNumeroEntradas() +
                            ", Estado: " + (entradaDto.getEstado() ? "Recogida" : "No recogida") +
                            ", Partido ID: " + entradaDto.getPartidoID());
                }
            } catch (InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-collect".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[]{1});

            // [marcarEntradaRecogida] PartidoServiceClient -m <entradaId> <creditCardNumber>

            try {
                clientPartidoService.marcarEntradaRecogida(Long.parseLong(args[1]), args[2]);
                System.out.println("Entrada con ID " + args[1] + " marcada como recogida correctamente.");
            } catch (NumberFormatException | InputValidationException | InstanceNotFoundException |
                     ServerException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        }
    }
    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if(expectedArgs != args.length) {
            printUsageAndExit();
        }
        for(int i = 0 ; i< numericArguments.length ; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch(NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }



    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [add]    PartidoServiceClient -addMatch <nombreVisitante> <fechaInicio> <maximoEntradas> <entradasVendidas> <precio>\n" +
                "    [find] PartidoServiceClient -findMatches <fecha>\n" +
                "    [get] PartidoServiceClient -findMatch <partidoId>\n" +
                "    [buytickets] PartidoServiceClient -buy <partidoId> <email> <creditCardNumber>" +
                "    [findEntradas] PartidoServiceClient -findPurchases <email>\n" +
                "    [marcarEntradaRecogida] PartidoServiceClient -collect <entradaId> <creditCardNumber>\n");
    }

}