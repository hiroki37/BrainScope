

/* Create Tables */

CREATE TABLE config
(
	present_id int NOT NULL,
	scope_size int NOT NULL
) WITHOUT OIDS;


CREATE TABLE neuron_pro
(
	id serial NOT NULL,
	title varchar,
	content text,
	neuron_level int NOT NULL,
	create_date date NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE tree_diagram
(
	ancestor int NOT NULL,
	descendant int NOT NULL
);