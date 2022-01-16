create table Statuses ( ID serial primary key, Status text not null unique);

create table People ( ID serial primary key, Surname text, Name text not null unique, Status_id integer references Statuses(ID) on delete restrict on update cascade not null, Birth_date timestamp not null, Death_date timestamp, check ( Birth_date < Death_date));


create table Police_ranks ( ID serial primary key, Rank text not null unique, Sort integer not null);

create table Ready_statuses ( ID serial primary key, Status text not null unique);

create table Divisions ( ID serial primary key, Name text not null, Boss integer unique, Bottom_line integer);

create table Policemen ( ID serial primary key, Human_id integer references People(ID) on delete restrict on update cascade not null, Division_id integer references Divisions(ID) on delete restrict on update cascade not null, Police_rank_id integer references Police_ranks(ID) not null, Ready_status_id integer references Ready_statuses(ID) on delete restrict on update cascade not null, is_working boolean not null default true);

alter table Divisions add constraint div_bos foreign key (Boss) references Policemen(ID) on update cascade;

create table Ranks ( ID serial primary key, Rank varchar(4) not null unique, Division_id integer references Divisions(ID) on delete restrict on update cascade not null, Sort integer not null);

create table Akudama ( ID serial primary key, Human_id integer references People(id) on delete restrict on update cascade not null unique, Rank_id integer references Ranks(ID) on delete restrict on update cascade not null, Years_of_detention interval default '0 days' not null);

create table Prison ( ID serial primary key, Akudama_id integer references Akudama(ID) on update cascade not null, Detention_begin timestamp not null, Detention_end timestamp not null, check (Detention_begin < Detention_end));

create table Articles ( ID serial primary key, Text text not null unique, Years_of_punishment interval not null, Rank_id integer references Ranks(ID) on delete restrict on update cascade not null);


create table Protocols (ID serial primary key, Akudama_id integer references Akudama(ID) on update cascade not null, Article_id integer references Articles(ID) on update cascade not null, Added_by integer references Policemen(ID) on update cascade, Added_at timestamp not null);

create table Machine_statuses ( ID serial primary key, Status text not null unique);

create table Machine_types ( ID serial primary key, Type text not null unique);

create table Machines ( ID serial primary key, Serial_number integer not null, Machine_type_id integer references Machine_types(ID) on delete restrict on update cascade not null, Machine_status_id integer references Machine_statuses(ID) on delete restrict on update cascade not null);

create table Equipment_types ( ID serial primary key, Type text not null unique);

create table Equip_by_division ( Division_id integer references Divisions(ID) on delete cascade on update cascade, Equipment_type_id integer references Equipment_types(ID) on delete restrict on update cascade, primary key (Division_id, Equipment_type_id) );

create table Equipment_statuses ( ID serial primary key, Status text not null unique );

create table Armory ( ID serial primary key, Serial_number integer not null, Equipment_type_id integer references Equipment_types(ID) on delete restrict on update cascade not null, Equipment_status_id integer references Equipment_statuses on delete restrict on update cascade not null);

create table Mission ( ID serial primary key, Address text not null, Commander integer references Policemen(ID) not null, Rank_id integer references Ranks(ID) on delete restrict on update cascade not null, Success boolean DEFAULT false, Akudama_id integer references Akudama(ID) not null, in_process boolean default true not null);

create table Machines_on_duty ( Mission_id integer references Mission(ID) on delete restrict on update cascade, Machine_id integer references Machines(ID) on update cascade, primary key (Mission_id, Machine_id));

create table Policemen_on_duty ( Mission_id integer references Mission(ID) on delete restrict on update cascade, Equipment_id integer references Armory(ID) on delete restrict on update cascade, primary key (Mission_id, Equipment_id), Policeman_id integer references Policemen(ID) );
