CREATE TABLE EGASSET_LOCATION (
	id BIGINT NOT NULL,
	zoneId CHARACTER VARYING(250) NOT NULL,
	revenueWard CHARACTER VARYING(250) NOT NULL,
	street CHARACTER VARYING(250),
	electionWard CHARACTER VARYING(250) NOT NULL,
	doorNo CHARACTER VARYING(250),
	pinCode CHARACTER VARYING(250) NOT NULL,
	locality BIGINT,
	block BIGINT,
	tenantId CHARACTER VARYING(250),
	createdBy CHARACTER VARYING(64) NOT NULL,
	createdDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lastModifiedBy CHARACTER VARYING(64),
    lastModifiedDate TIMESTAMP WITHOUT TIME ZONE,

	CONSTRAINT PK_EGASSET_LOCATION PRIMARY KEY (id)
);

CREATE SEQUENCE SEQ_EGASSET_LOCATION INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;