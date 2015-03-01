
create database tetris;

use tetris;

create table score(
	scoreID integer not null auto_increment,
	playerName varchar(20),
	playerScore integer,
	playerLines integer,
	playerLevel integer,
	playerDifficulty integer,
	gameTime integer,
	primary key (scoreID)
);