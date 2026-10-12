CREATE TABLE tenants (
    id          UUID         NOT NULL,
    slug        VARCHAR(64)  NOT NULL,
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_tenants PRIMARY KEY (id),
    CONSTRAINT uq_tenants_slug UNIQUE (slug)
);
