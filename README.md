# MineSweeper

dbname: ltm_ptit

CREATE TABLE player (
	  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username varchar(255),
    password varchar(255),
    point float,
    avgPointOpp float,
    avgTime varchar(255),
    status varchar(255)
);
