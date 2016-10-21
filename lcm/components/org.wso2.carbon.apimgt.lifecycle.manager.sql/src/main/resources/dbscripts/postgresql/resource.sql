
CREATE SEQUENCE LC_DEFINITIONS_SEQUENCE START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS LC_DEFINITIONS(
            LC_ID INTEGER DEFAULT nextval('lc_definitions_sequence'),
            LC_NAME VARCHAR(255),
            LC_CONTENT BYTEA,
            UNIQUE (ID),
            PRIMARY KEY (LC_NAME)
);

CREATE TABLE IF NOT EXISTS LC_DATA(
            LC_STATE_ID VARCHAR(36) NOT NULL ,
            LC_DEFINITION_ID INTEGER ,
            LC_STATUS VARCHAR(255),
            UNIQUE (LC_STATE_ID),
            PRIMARY KEY (LC_STATE_ID),
            FOREIGN KEY (LC_DEFINITION_ID) REFERENCES LC_DEFINITIONS(ID) ON DELETE CASCADE
);
