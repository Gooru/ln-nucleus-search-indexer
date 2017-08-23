CREATE TABLE gut_based_resource_suggest (
    id SERIAL,
    competency_internal_code character varying(255) NOT NULL,
    competency_display_code character varying(500),
    micro_competency_internal_code character varying(255),
    micro_competency_display_code character varying(500),
    performance_range character varying(20) NOT NULL,
    ids_to_suggest text[],
    CONSTRAINT gbrs_pkey PRIMARY KEY (id)
);

ALTER TABLE gut_based_resource_suggest OWNER TO nucleus;

ALTER TABLE gut_based_resource_suggest ADD CONSTRAINT gbrs_c_mc_perf_unique_idx UNIQUE (competency_internal_code, micro_competency_internal_code, performance_range);

