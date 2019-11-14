create table resource_index_delete_tracker (id SERIAL PRIMARY KEY, gooru_oid uuid NOT NULL, deleted_at timestamp without time zone DEFAULT timezone('UTC'::text, now()) NOT NULL, index_type character varying(16) NOT NULL);
create table collection_index_delete_tracker (id SERIAL PRIMARY KEY, gooru_oid uuid NOT NULL, deleted_at timestamp without time zone DEFAULT timezone('UTC'::text, now()) NOT NULL, index_type character varying(16) NOT NULL);

create table indexer_job_status (key text, status text, updated_at timestamp without time zone not null default timezone('UTC'::text, now()));
