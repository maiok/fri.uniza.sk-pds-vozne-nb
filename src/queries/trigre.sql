-- Sekvencie
CREATE SEQUENCE zamestnanec_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE spolocnost_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE typ_spolocnosti_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE typ_vozna_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE vozen_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE vyradeny_vozen_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE stanica_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE suciastka_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE typ_suciastky_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE oprava_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE typ_opravy_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE kontrola_id_seq
START WITH 1
INCREMENT BY 1;
/

CREATE SEQUENCE typ_kontroly_id_seq
START WITH 1
INCREMENT BY 1;
/

-- triggre pre autoinkrement

CREATE OR REPLACE TRIGGER zamestnanec_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."ZAMESTNANEC"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_ZAMESTNANCA" IS NULL
      THEN
        SELECT ZAMESTNANEC_ID_SEQ.nextval
        INTO :NEW."ID_ZAMESTNANCA"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER spolocnost_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."SPOLOCNOST"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_SPOLOCNOSTI" IS NULL
      THEN
        SELECT SPOLOCNOST_ID_SEQ.nextval
        INTO :NEW."ID_SPOLOCNOSTI"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER typ_spolocnosti_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."TYP_SPOLOCNOSTI"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_TYPU_SPOLOCNOSTI" IS NULL
      THEN
        SELECT TYP_SPOLOCNOSTI_ID_SEQ.nextval
        INTO :NEW."ID_TYPU_SPOLOCNOSTI"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER typ_vozna_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."TYP_VOZNA"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_TYPU_VOZNA" IS NULL
      THEN
        SELECT TYP_VOZNA_ID_SEQ.nextval
        INTO :NEW."ID_TYPU_VOZNA"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER vozen_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."VOZEN"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_VOZNA" IS NULL
      THEN
        SELECT VOZEN_ID_SEQ.nextval
        INTO :NEW."ID_VOZNA"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER vyradeny_vozen_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."VYRADENY_VOZEN"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_VYRAD_VOZEN" IS NULL
      THEN
        SELECT VYRADENY_VOZEN_ID_SEQ.nextval
        INTO :NEW."ID_VYRAD_VOZEN"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER kontrola_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."KONTROLA"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_KONTROLY" IS NULL
      THEN
        SELECT KONTROLA_ID_SEQ.nextval
        INTO :NEW."ID_KONTROLY"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER typ_kontroly_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."TYP_KONTROLY"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_TYPU_KONTROLY" IS NULL
      THEN
        SELECT TYP_OPRAVY_ID_SEQ.nextval
        INTO :NEW."ID_TYPU_KONTROLY"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER oprava_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."OPRAVA"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_OPRAVY" IS NULL
      THEN
        SELECT OPRAVA_ID_SEQ.nextval
        INTO :NEW."ID_OPRAVY"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER typ_opravy_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."TYP_OPRAVY"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_TYPU_OPRAVY" IS NULL
      THEN
        SELECT TYP_OPRAVY_ID_SEQ.nextval
        INTO :NEW."ID_TYPU_OPRAVY"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER stanica_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."STANICA"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_STANICE" IS NULL
      THEN
        SELECT STANICA_ID_SEQ.nextval
        INTO :NEW."ID_STANICE"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER suciastka_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."SUCIASTKA"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_SUCIASTKY" IS NULL
      THEN
        SELECT SUCIASTKA_ID_SEQ.nextval
        INTO :NEW."ID_SUCIASTKY"
        FROM dual;
      END IF;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER typ_suciastky_id_trigger
  BEFORE INSERT
  ON "SYSTEM"."TYP_SUCIASTKY"
  FOR EACH ROW
  BEGIN
    IF INSERTING
    THEN
      IF :NEW."ID_TYPU_SUCIASTKY" IS NULL
      THEN
        SELECT TYP_SUCIASTKY_ID_SEQ.nextval
        INTO :NEW."ID_TYPU_SUCIASTKY"
        FROM dual;
      END IF;
    END IF;
  END;
/

