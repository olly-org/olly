CREATE TABLE sessions (
    id              UUID        NOT NULL,
    tenant_id       UUID        NOT NULL,
    join_code       VARCHAR(8)  NOT NULL,
    live_join_code  VARCHAR(8),
    kind            VARCHAR(64) NOT NULL,
    state           VARCHAR(16) NOT NULL,
    version         INTEGER     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    closed_at       TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_sessions PRIMARY KEY (id),
    CONSTRAINT fk_sessions_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT uq_sessions_tenant_id_live_join_code UNIQUE (tenant_id, live_join_code),
    CONSTRAINT ck_sessions_state CHECK (state IN ('OPEN', 'CLOSED')),
    CONSTRAINT ck_sessions_live_join_code CHECK (
        (state = 'OPEN' AND live_join_code IS NOT NULL AND live_join_code = join_code)
        OR (state <> 'OPEN' AND live_join_code IS NULL)
    )
);
