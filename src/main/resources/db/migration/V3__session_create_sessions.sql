CREATE TABLE sessions (
    id          UUID        NOT NULL,
    tenant_id   UUID        NOT NULL,
    join_code   VARCHAR(8)  NOT NULL,
    kind        VARCHAR(64) NOT NULL,
    state       VARCHAR(16) NOT NULL,
    version     INTEGER     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    closed_at   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_sessions PRIMARY KEY (id),
    CONSTRAINT fk_sessions_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT uq_sessions_tenant_id_join_code UNIQUE (tenant_id, join_code),
    CONSTRAINT ck_sessions_state CHECK (state IN ('OPEN', 'CLOSED'))
);
