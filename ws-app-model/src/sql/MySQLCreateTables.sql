-- ----------------------------------------------------------------------------
-- Model : To use as normal database
-- ----------------------------------------------------------------------------
DROP TABLE Entrada;
DROP TABLE Partido;

-- -------------------------------------PARTIDO--------------------------------
CREATE TABLE Partido(
    partidoID BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
    nombreVisitante VARCHAR(255) COLLATE latin1_bin NOT NULL,
    fechaInicio DATETIME NOT NULL,
    fechaAlta DATETIME NOT NULL,
    maximoEntradas INT NOT NULL,
    entradasVendidas INT NOT NULL,
    precio FLOAT NOT NULL,

    CONSTRAINT PK_ID PRIMARY KEY (partidoID),
    CONSTRAINT validarMaximoEntradas CHECK (maximoEntradas >= 0),
    CONSTRAINT validarEntradasVendidas CHECK (entradasVendidas >= 0 AND entradasVendidas <= maximoEntradas)
) ENGINE = InnoDB;

-- -------------------------------------ENTRADA--------------------------------
CREATE TABLE Entrada(
    entradaID BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
    email VARCHAR(255) COLLATE latin1_bin NOT NULL,
    numeroTarjeta VARCHAR(16) NOT NULL,
    numeroEntradas BIGINT NOT NULL,
    fechaCompra DATETIME NOT NULL,
    estado BOOLEAN NOT NULL,
    partidoID BIGINT NOT NULL,

    CONSTRAINT PK_ID PRIMARY KEY (entradaID),
    CONSTRAINT FK_P_ID FOREIGN KEY (partidoID) REFERENCES Partido(partidoID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT validarNumeroEntradas CHECK (numeroEntradas > 0)
) ENGINE = InnoDB;
