package es.udc.ws.app.model.Partido;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlPartidoDaoFactory {

    private final static String CLASS_NAME = "SqlPartidoDaoFactory.className";

    private static SqlPartidoDao dao = null;
    private SqlPartidoDaoFactory() {
    }

    private static SqlPartidoDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME);
            Class daoClass = Class.forName(daoClassName);
            return (SqlPartidoDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlPartidoDao getDao() {
        if (dao == null) {
            dao = getInstance();
        }
        return dao;
    }
}
