CREATE TABLE client
(
	user_id integer PRIMARY KEY,
	email varchar(50),
	password varchar(50),
	name varchar(50),
	surname varchar(50),
	latitude float,
	longitude float,
	online integer
);

INSERT INTO client VALUES
(
	1,
	'ana@yahoo.com',
	'ana',
	'Ana',
	'Banana',
	5.4,
	1.2,
	1
);

INSERT INTO client VALUES
(
        2,
        'mimi@yahoo.com',
        'mimi',
        'Mimi',
        'Portocala',
        6.9,
        8.1,
        1
);
