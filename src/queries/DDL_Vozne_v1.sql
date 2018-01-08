/*
Created: 28.11.2017
Modified: 5.12.2017
Model: Oracle 12c Release 1
Database: Oracle 12c
*/


-- Create user data types section -------------------------------------------------

CREATE OR REPLACE TYPE T_REC_HISTORIA_CENY
AS OBJECT (
  datum_od DATE,
  datum_do DATE,
  cena     NUMBER,
ORDER MEMBER FUNCTION tried_cenu (porovnat T_REC_HISTORIA_CENY)
  RETURN NUMBER
)
/
CREATE OR REPLACE TYPE BODY T_REC_HISTORIA_CENY
IS
  ORDER MEMBER FUNCTION tried_cenu (porovnat T_REC_HISTORIA_CENY)
    RETURN NUMBER IS
    BEGIN
      IF porovnat.cena > self.cena
      THEN RETURN 1;
      ELSIF porovnat.cena < self.cena
        THEN RETURN -1;
      ELSE RETURN 0;
      END IF;
    END;
END;
/

CREATE OR REPLACE TYPE T_HISTORIA_CENY
AS TABLE OF T_REC_HISTORIA_CENY;
/

-- Create tables section -------------------------------------------------

-- Table Osoba

CREATE TABLE Osoba (
  rod_cislo     CHAR(10) NOT NULL,
  meno          VARCHAR2(30),
  priezvisko    VARCHAR2(30),
  dat_narodenia DATE,
  adresa_osoby  VARCHAR2(50),
  kontakt_osoby VARCHAR2(15)
)
/

-- Add keys for table Osoba

ALTER TABLE Osoba
  ADD CONSTRAINT Key1 PRIMARY KEY (rod_cislo)
/

-- Table and Columns comments section

COMMENT ON COLUMN Osoba.rod_cislo IS 'd'
/

-- Table Zamestnanec

CREATE TABLE Zamestnanec (
  id_zamestnanca    INTEGER NOT NULL,
  rod_cislo         CHAR(10),
  id_spolocnosti    INTEGER,
  datum_prijatia    DATE    NOT NULL,
  datum_prepustenia DATE
)
/

-- Add keys for table Zamestnanec

ALTER TABLE Zamestnanec
  ADD CONSTRAINT Key2 PRIMARY KEY (id_zamestnanca)
/

-- Table Spolocnost

CREATE TABLE Spolocnost (
  id_spolocnosti      INTEGER NOT NULL,
  id_typu_spolocnosti INTEGER,
  nazov_spolocnosti   VARCHAR2(30),
  adresa_spolocnosti  VARCHAR2(50),
  kontakt_spolocnosti VARCHAR2(15)
)
/

-- Add keys for table Spolocnost

ALTER TABLE Spolocnost
  ADD CONSTRAINT Key3 PRIMARY KEY (id_spolocnosti)
/

-- Table Typ_Spolocnosti

CREATE TABLE Typ_Spolocnosti (
  id_typu_spolocnosti INTEGER NOT NULL,
  typ_spolocnosti     CHAR(20)
)
/

-- Add keys for table Typ_Spolocnosti

ALTER TABLE Typ_Spolocnosti
  ADD CONSTRAINT Key4 PRIMARY KEY (id_typu_spolocnosti)
/

-- Table Vozen

CREATE TABLE Vozen (
  id_vozna             INTEGER NOT NULL,
  id_typu_vozna        INTEGER,
  id_vlastnika         INTEGER,
  id_vyrobcu           INTEGER,
  id_domovskej_stanice INTEGER NOT NULL,
  cena                 NUMBER
)
/

-- Add keys for table Vozen

ALTER TABLE Vozen
  ADD CONSTRAINT Key5 PRIMARY KEY (id_vozna, id_domovskej_stanice)
/

-- Table Typ_Vozna

CREATE TABLE Typ_Vozna (
  id_typu_vozna  INTEGER NOT NULL,
  typ_vozna      VARCHAR2(30),
  cena           NUMBER  NOT NULL,
  odpis_percenta NUMBER,
  foto_vozna     BLOB    NOT NULL
)
/

-- Add keys for table Typ_Vozna

ALTER TABLE Typ_Vozna
  ADD CONSTRAINT Key6 PRIMARY KEY (id_typu_vozna, cena)
/

-- Table Stanica

CREATE TABLE Stanica (
  id_stanice      INTEGER NOT NULL,
  nazov_stanice   VARCHAR2(30),
  adresa_stanice  VARCHAR2(30),
  kontakt_stanice VARCHAR2(30)
)
/

-- Add keys for table Stanica

ALTER TABLE Stanica
  ADD CONSTRAINT Key7 PRIMARY KEY (id_stanice)
/

-- Table Kontrola

CREATE TABLE Kontrola (
  id_kontroly      INTEGER NOT NULL,
  id_typu_kontroly INTEGER,
  id_vozna         INTEGER,
  popis_kontroly   VARCHAR2(100),
  id_stanice       INTEGER
)
/

-- Add keys for table Kontrola

ALTER TABLE Kontrola
  ADD CONSTRAINT Key8 PRIMARY KEY (id_kontroly)
/

-- Table Typ_Kontroly

CREATE TABLE Typ_Kontroly (
  id_typu_kontroly INTEGER NOT NULL,
  typ_kontroly     VARCHAR2(30),
  cena_kontroly    NUMBER  NOT NULL
)
/

-- Add keys for table Typ_Kontroly

ALTER TABLE Typ_Kontroly
  ADD CONSTRAINT Key9 PRIMARY KEY (id_typu_kontroly)
/

-- Table Vyradeny_Vozen

CREATE TABLE Vyradeny_Vozen (
  id_vyrad_vozen INTEGER NOT NULL,
  id_vozna       INTEGER,
  id_stanice     INTEGER
)
/

-- Add keys for table Vyradeny_Vozen

ALTER TABLE Vyradeny_Vozen
  ADD CONSTRAINT Key10 PRIMARY KEY (id_vyrad_vozen)
/

-- Table Oprava

CREATE TABLE Oprava (
  id_opravy      INTEGER NOT NULL,
  id_kontroly    INTEGER,
  id_typu_opravy INTEGER,
  popis_opravy   VARCHAR2(100)
)
/

-- Add keys for table Oprava

ALTER TABLE Oprava
  ADD CONSTRAINT Key11 PRIMARY KEY (id_opravy)
/

-- Table Typ_Opravy

CREATE TABLE Typ_Opravy (
  id_typu_opravy INTEGER NOT NULL,
  typ_opravy     VARCHAR2(30),
  cena_opravy    NUMBER  NOT NULL
)
/

-- Add keys for table Typ_Opravy

ALTER TABLE Typ_Opravy
  ADD CONSTRAINT Key12 PRIMARY KEY (id_typu_opravy)
/

-- Table Suciastka

CREATE TABLE Suciastka (
  id_suciastky      INTEGER NOT NULL,
  id_typu_suciastky INTEGER,
  id_dodavatela     INTEGER,
  cena_suciastky    NUMBER  NOT NULL
)
/

-- Add keys for table Suciastka

ALTER TABLE Suciastka
  ADD CONSTRAINT Key13 PRIMARY KEY (id_suciastky)
/

-- Table Typ_Suciastky

CREATE TABLE Typ_Suciastky (
  id_typu_suciastky INTEGER         NOT NULL,
  typ_suciastky     VARCHAR2(30),
  historia_ceny     T_HISTORIA_CENY NOT NULL
)
/

-- Add keys for table Typ_Suciastky

ALTER TABLE Typ_Suciastky
  ADD CONSTRAINT Key14 PRIMARY KEY (id_typu_suciastky)
/

-- Table Oprava_Suciastka

CREATE TABLE Oprava_Suciastka (
  id_opravy      INTEGER NOT NULL,
  id_zamestnanca INTEGER NOT NULL,
  id_suciastky   INTEGER,
  oprava_od      DATE    NOT NULL,
  oprava_do      DATE
)
/

-- Add keys for table Oprava_Suciastka

ALTER TABLE Oprava_Suciastka
  ADD CONSTRAINT Key15 PRIMARY KEY (id_opravy, id_zamestnanca, oprava_od)
/

-- Table Kontrola_Zamestnanec

CREATE TABLE Kontrola_Zamestnanec (
  id_zamestnanca INTEGER NOT NULL,
  id_kontroly    INTEGER NOT NULL,
  kontrola_od    DATE    NOT NULL,
  kontrola_do    DATE
)
/

-- Add keys for table Kontrola_Zamestnanec

ALTER TABLE Kontrola_Zamestnanec
  ADD CONSTRAINT Key16 PRIMARY KEY (id_zamestnanca, id_kontroly, kontrola_od)
/

-- Table Vozen_Stanica

CREATE TABLE Vozen_Stanica (
  id_vozna     INTEGER NOT NULL,
  id_stanice   INTEGER NOT NULL,
  v_stanici_od DATE    NOT NULL,
  v_stanici_do DATE
)
/

-- Add keys for table Vozen_Stanica

ALTER TABLE Vozen_Stanica
  ADD CONSTRAINT Key18 PRIMARY KEY (id_vozna, id_stanice, v_stanici_od)
/

-- Create indexes for tables section -------------------------------------------------

CREATE INDEX IX_Relationship9
  ON Zamestnanec (rod_cislo)
/
CREATE INDEX IX_Relationship1
  ON Zamestnanec (id_spolocnosti)
/
CREATE INDEX IX_Relationship26
  ON Spolocnost (id_typu_spolocnosti)
/
CREATE INDEX IX_Relationship30
  ON Vozen (id_typu_vozna, cena)
/
CREATE INDEX IX_Relationship31
  ON Vozen (id_vlastnika)
/
CREATE INDEX IX_Relationship33
  ON Vozen (id_vyrobcu)
/
CREATE INDEX IX_Relationship27
  ON Kontrola (id_typu_kontroly)
/
CREATE INDEX IX_Relationship34
  ON Kontrola (id_vozna, id_stanice)
/
CREATE INDEX IX_Relationship21
  ON Vyradeny_Vozen (id_vozna, id_stanice)
/
CREATE INDEX IX_Relationship24
  ON Oprava (id_kontroly)
/
CREATE INDEX IX_Relationship28
  ON Oprava (id_typu_opravy)
/
CREATE INDEX IX_Relationship29
  ON Suciastka (id_typu_suciastky)
/
CREATE INDEX IX_Relationship32
  ON Suciastka (id_dodavatela)
/
CREATE INDEX IX_Relationship3
  ON Oprava_Suciastka (id_suciastky)
/



-- Create foreign keys (relationships) section -------------------------------------------------

ALTER TABLE Oprava_Suciastka
  ADD FOREIGN KEY (id_opravy) REFERENCES Oprava (id_opravy)
ON DELETE CASCADE
/


ALTER TABLE Kontrola_Zamestnanec
  ADD FOREIGN KEY (id_zamestnanca) REFERENCES Zamestnanec (id_zamestnanca)
ON DELETE CASCADE
/


ALTER TABLE Kontrola_Zamestnanec
  ADD FOREIGN KEY (id_kontroly) REFERENCES Kontrola (id_kontroly)
ON DELETE CASCADE
/


ALTER TABLE Vozen_Stanica
  ADD FOREIGN KEY (id_vozna, id_stanice) REFERENCES Vozen (id_vozna, id_domovskej_stanice)
ON DELETE CASCADE
/


ALTER TABLE Vozen_Stanica
  ADD FOREIGN KEY (id_stanice) REFERENCES Stanica (id_stanice)
ON DELETE CASCADE
/


ALTER TABLE Zamestnanec
  ADD FOREIGN KEY (rod_cislo) REFERENCES Osoba (rod_cislo)
ON DELETE SET NULL
/


ALTER TABLE Vyradeny_Vozen
  ADD FOREIGN KEY (id_vozna, id_stanice) REFERENCES Vozen (id_vozna, id_domovskej_stanice)
ON DELETE SET NULL
/


ALTER TABLE Oprava
  ADD FOREIGN KEY (id_kontroly) REFERENCES Kontrola (id_kontroly)
ON DELETE SET NULL
/


ALTER TABLE Spolocnost
  ADD FOREIGN KEY (id_typu_spolocnosti) REFERENCES Typ_Spolocnosti (id_typu_spolocnosti)
ON DELETE SET NULL
/


ALTER TABLE Kontrola
  ADD FOREIGN KEY (id_typu_kontroly) REFERENCES Typ_Kontroly (id_typu_kontroly)
ON DELETE SET NULL
/


ALTER TABLE Oprava
  ADD FOREIGN KEY (id_typu_opravy) REFERENCES Typ_Opravy (id_typu_opravy)
ON DELETE SET NULL
/


ALTER TABLE Suciastka
  ADD FOREIGN KEY (id_typu_suciastky) REFERENCES Typ_Suciastky (id_typu_suciastky)
ON DELETE SET NULL
/


ALTER TABLE Vozen
  ADD FOREIGN KEY (id_typu_vozna, cena) REFERENCES Typ_Vozna (id_typu_vozna, cena)
ON DELETE SET NULL
/


ALTER TABLE Vozen
  ADD FOREIGN KEY (id_vlastnika) REFERENCES Spolocnost (id_spolocnosti)
ON DELETE SET NULL
/


ALTER TABLE Suciastka
  ADD FOREIGN KEY (id_dodavatela) REFERENCES Spolocnost (id_spolocnosti)
ON DELETE SET NULL
/


ALTER TABLE Vozen
  ADD FOREIGN KEY (id_vyrobcu) REFERENCES Spolocnost (id_spolocnosti)
ON DELETE SET NULL
/


ALTER TABLE Kontrola
  ADD FOREIGN KEY (id_vozna, id_stanice) REFERENCES Vozen (id_vozna, id_domovskej_stanice)
ON DELETE CASCADE
/


ALTER TABLE Zamestnanec
  ADD FOREIGN KEY (id_spolocnosti) REFERENCES Spolocnost (id_spolocnosti)
ON DELETE CASCADE
/


ALTER TABLE Oprava_Suciastka
  ADD FOREIGN KEY (id_zamestnanca) REFERENCES Zamestnanec (id_zamestnanca)
ON DELETE CASCADE
/


ALTER TABLE Oprava_Suciastka
  ADD FOREIGN KEY (id_suciastky) REFERENCES Suciastka (id_suciastky)
ON DELETE CASCADE
/


ALTER TABLE Vozen
  ADD FOREIGN KEY (id_domovskej_stanice) REFERENCES Stanica (id_stanice)
ON DELETE SET NULL
/





