
create table news (
    id int unsigned not null auto_increment,
    title varchar(255) not null,
    body text not null,
    date_created datetime not null,
    primary key ( id )
);
