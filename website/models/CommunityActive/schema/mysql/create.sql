
create table communityactive (
	id int unsigned not null primary key auto_increment,
	skey char(32) not null unique,
	ip varchar(19) not null,
	title varchar(255) not null,
	tagline varchar(255) not null,
	basepath varchar(50) null,
	port smallint not null,
	version varchar(20) not null,
	requiresLogin bit(1) not null,
	dateUpdated datetime not null
);
