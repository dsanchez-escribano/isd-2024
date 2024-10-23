package es.udc.ws.app.model.Entrada;

import es.udc.ws.app.model.Partido.SqlPartidoDao;
import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlEntradaDaoFactory {

    private final static String CLASS_NAME = "SqlEntradaDaoFactory.className";

    private static SqlEntradaDao dao = null;
    private SqlEntradaDaoFactory() {
    }

    private static SqlEntradaDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME);
            Class daoClass = Class.forName(daoClassName);
            return (SqlEntradaDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlEntradaDao getDao() {
        if (dao == null) {
            dao = getInstance();
        }
        return dao;
    }
}

