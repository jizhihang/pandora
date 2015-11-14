-- Database: dataset

-- DROP DATABASE dataset;

CREATE DATABASE dataset
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;

-- Table: images

-- DROP TABLE images;

CREATE TABLE images (
  id character varying NOT NULL,
  title character varying,
  subtitle character varying,
  latitude numeric,
  longitude numeric,
  width smallint NOT NULL,
  height smallint NOT NULL,
  descriptor numeric[] NOT NULL,
  copyright character varying,
  CONSTRAINT image_id_pk PRIMARY KEY (id)
) WITH (OIDS=FALSE);

ALTER TABLE images OWNER TO postgres;

-- Table: codebooks

-- DROP TABLE codebooks;

CREATE TABLE codebooks (
  id smallint NOT NULL,
  centroids numeric[][] NOT NULL,
  CONSTRAINT codebook_id_pk PRIMARY KEY (id)
) WITH (OIDS=FALSE);

ALTER TABLE codebooks OWNER TO postgres;

-- Table: reducers

-- DROP TABLE reducers;

CREATE TABLE reducers (
  id smallint NOT NULL,
  subspace numeric[][] NOT NULL,
  mean numeric[] NOT NULL,
  whiten boolean NOT NULL,
  CONSTRAINT reducer_id_pk PRIMARY KEY (id)
) WITH (OIDS=FALSE);

ALTER TABLE reducers OWNER TO postgres;

-- Function: l2(numeric[], numeric[])

-- DROP FUNCTION l2(numeric[], numeric[]);

CREATE OR REPLACE FUNCTION l2(numeric[], numeric[])
  RETURNS numeric AS
$BODY$
  DECLARE
    x ALIAS FOR $1;
    y ALIAS FOR $2;
    l int;
    u int;
    i int;
    sum numeric;
    dist numeric;
  BEGIN
    dist := 0.0;
    l := array_lower (y, 1);
    u := array_upper (y, 1);
    
    sum := 0.0;
    IF l IS NOT NULL THEN
      FOR i IN l .. u LOOP
        sum := sum + power((x[i]-y[i]), 2);
      END LOOP;
      dist := sqrt(sum);
    END IF;

    RETURN dist;
  END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION l2(numeric[], numeric[])
  OWNER TO postgres;

-- Operator: euclidean distance, l2

CREATE OPERATOR - (
 PROCEDURE = l2,
 LEFTARG = numeric[],
 RIGHTARG = numeric[]
);

-- Function: l1(numeric[], numeric[])

-- DROP FUNCTION l1(numeric[], numeric[]);

CREATE OR REPLACE FUNCTION l1(numeric[], numeric[])
  RETURNS numeric AS
$BODY$
  DECLARE
    x ALIAS FOR $1;
    y ALIAS FOR $2;
    l int;
    u int;
    i int;
    sum numeric;
    dist numeric;
  BEGIN
    dist := 0.0;
    l := array_lower (y, 1);
    u := array_upper (y, 1);
    
    sum := 0.0;
    IF l IS NOT NULL THEN
      FOR i IN l .. u LOOP
        sum := sum + abs(x[i]-y[i]);
      END LOOP;
      dist := sum;
    END IF;

    RETURN dist;
  END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION l1(numeric[], numeric[])
  OWNER TO postgres;

--Operator: manhattan distance, l1

CREATE OPERATOR ~ (
 PROCEDURE = l1,
 LEFTARG = numeric[],
 RIGHTARG = numeric[]
);
