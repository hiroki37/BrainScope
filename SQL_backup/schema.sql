/* Create Tables */

CREATE TABLE neuron
(
	id serial NOT NULL,
	title varchar NOT NULL,
	content text,
	neuron_level int NOT NULL,
	create_date date NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE config
(
	scope_address int NOT NULL,
	scope_size int NOT NULL
);