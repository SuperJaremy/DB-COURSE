create function insert_values() returns void as $$
declare
alive_id integer;
human_base integer;
execs integer;
lowest_rank integer;
rank_base integer;
ready_id integer;
policemen_base integer;
cnt_articles integer;
article_base integer;
cnt_akudama integer;
akudama_base integer;
lawyers integer[];
equip_base integer;
machine_base integer;
default_rank integer;
default_status integer;
begin
insert into Statuses(Status) values ('ЖИВОЙ'), ('МЕРТВ');

select ID from Statuses where Status = 'ЖИВОЙ' into alive_id;

for i in 1..900 loop
    insert into People(Surname, Name, Status_id, Birth_date) values (random()::text, random()::text, alive_id, current_timestamp);
end loop;

select min(ID) from People into human_base;

human_base := human_base - 1;

insert into Divisions(Name) values ('Палачи'), ('Отдел спецопераций'), ('Патрульный отдел'), ('Технический отдел'), ('Юридический отдел'), ('Тюремный персонал'),
                                   ('Управляющий отдел');

select ID from Divisions where Name = 'Палачи' into execs;

insert into Ranks(Rank, Division_id, Sort) values ('D', execs+3, 1), ('C', execs+3, 2), ('B', execs+2, 3), ('A', execs+1, 4), ('S', execs, 5);

select ID from Ranks where Sort = (select min(Sort) from Ranks) into lowest_rank;

for i in 1..200 loop
    insert into Akudama(Human_id, Rank_id, Years_of_detention) values (i + human_base, lowest_rank, '0 years');
end loop;

for i in 1..100 loop
    insert into Articles(Text, Years_of_punishment, Rank_id) values (random()::text, make_interval(years := round(random()*100)::integer, months := round(random()*11)::integer), round(random()*4)::integer + lowest_rank);
end loop;

insert into Ready_statuses(Status) values ('ГОТОВ'), ('РАНЕН'), ('ОТПУСК'), ('МЕРТВ'), ('НА ЗАДАНИИ'), ('ОТСТРАНЕН');

select ID from Ready_statuses where Status = 'ГОТОВ' into ready_id;

insert into Police_ranks(Rank, Sort) values ('Рядовой', 1), ('Сержант', 2), ('Прапорщик', 3), ('Лейтенант', 4), ('Капитан', 5), ('Майор', 6);


select ID from Police_ranks where Sort = (select min(Sort) from Police_ranks) into rank_base;

update Divisions set Bottom_line = rank_base + 5 where Name = 'Палачи';

update Divisions set Bottom_line = rank_base + 3 where Name = 'Отдел спецопераций';

select count(*) from Akudama into cnt_akudama;

rank_base := rank_base - 1;

for i in 201..220 loop
    insert into Policemen(Human_id, Division_id, Police_rank_id, Ready_status_id) values (i + human_base, execs, round(random())::integer + 5 + rank_base, ready_id);
end loop;

for i in 221..370 loop
    insert into Policemen(Human_id, Division_id, Police_rank_id, Ready_status_id) values (i + human_base, execs + 1, round(random())::integer + 3 + rank_base, ready_id);
end loop; 

for i in 371..700 loop
    insert into Policemen(Human_id, Division_id, Police_rank_id, Ready_status_id) values (i + human_base, execs + 2, round(random())::integer + 1 + rank_base, ready_id);
end loop; 

for i in 701..900 loop
    insert into Policemen(Human_id, Division_id, Police_rank_id, Ready_status_id) values (i + human_base, (i % 4) + execs + 3, round(random())::integer * 5 + 1 + rank_base, ready_id);
end loop; 

select min(ID) from Policemen into policemen_base;

update Divisions set Boss = policemen_base where ID = execs;

update Divisions set Boss = policemen_base + 20 where ID = execs + 1;

update Divisions set Boss = policemen_base + 170 where ID = execs + 2;

update Divisions set Boss = policemen_base + 501 where ID = execs + 5;

update Divisions set Boss = policemen_base + 500 where ID = execs + 4;

update divisions set boss = policemen_base + 502 where ID = execs + 6;

update divisions set boss = policemen_base + 503 where ID = execs + 3;

select min(ID) from Articles into article_base;

article_base := article_base - 1;

select count(*) from Articles into cnt_articles;

select min(ID) from Akudama into akudama_base;

akudama_base := akudama_base - 1;

lawyers = array(select ID from Policemen where Division_id = (select ID from Divisions where Name = 'Юридический отдел'));

for i in 1..1000 loop
    insert into Protocols(Akudama_id, Article_id, Added_by, Added_at) values (round(random()*(cnt_akudama - 1))::integer + akudama_base + 1, round(random()*(cnt_articles - 1))::integer + article_base + 1, lawyers[round(random()*(array_length(lawyers, 1) - 1) + 1)::integer], current_timestamp);
end loop;

--insert into Prison(Akudama_id, Detention_begin, Detention_end) values (1, to_timestamp('12.02.2014','DD.MM.YYYY'), to_timestamp('12.02.2084', 'DD.MM.YYYY'));

insert into Equipment_types(Type) values ('Пистолет'), ('Автоматическая винтовка'), ('Бронежилет'), ('Энергетический меч');

select min(ID) from Equipment_types into equip_base;

equip_base := equip_base - 1;

insert into Equip_by_division(Division_id, Equipment_type_id) values (execs, equip_base + 4), (execs + 1, equip_base + 2), (execs + 1, equip_base + 3), (execs + 2, equip_base + 1);

insert into Equipment_statuses(Status) values ('ГОТОВО'), ('РЕМОНТ'), ('УТЕРЯНО'), ('ЗАНЯТО');

select ID from Equipment_statuses where Status = 'ГОТОВО' into ready_id;

for i in 1..30 loop
    insert into Armory(Serial_number, Equipment_type_id, Equipment_status_id) values ((random()*100000)::integer, equip_base + 4, ready_id);
end loop;

for i in 1..225 loop
    insert into Armory(Serial_number, Equipment_type_id, Equipment_status_id) values ((random()*100000)::integer, equip_base + 2, ready_id), ((random()*100000)::integer, equip_base + 3,ready_id);
end loop;

for i in 1..495 loop
    insert into Armory(Serial_number, Equipment_type_id, Equipment_status_id) values ((random()*100000)::integer, equip_base + 1, ready_id);
end loop;

insert into Machine_Statuses(Status) values ('ГОТОВО'), ('РЕМОНТ'), ('НА ЗАДАНИИ');

select ID from Machine_Statuses where Status = 'ГОТОВО' into ready_id;

insert into Machine_Types(Type) values ('Танк'), ('Патрульный робот');

select min(ID) from Machine_Types into machine_base;

machine_base := machine_base - 1;

for i in 1..1000 loop
    insert into Machines(Serial_number, Machine_type_id, Machine_status_id) values ((random()*100000)::integer, machine_base + 2, ready_id);
end loop;

insert into Machines(Serial_number, Machine_type_id, Machine_status_id) values ((random()*100000)::integer, machine_base + 1, ready_id), ((random()*100000)::integer, machine_base + 1,ready_id), ((random()*100000)::integer, machine_base + 1,ready_id), ((random()*100000)::integer, machine_base + 1,ready_id), ((random()*100000)::integer, machine_base + 1,ready_id);

--insert into Mission(Address, Commander, Rank_id, Success) values ('Амфитеатр', 4, 5, false);

--insert into Policemen_on_duty(Mission_id, Equipment_id, Policeman_id) values (1, 4, 4);

--insert into Machines_on_duty(Mission_id, Machine_id) values (1, 9);

EXECUTE format('alter table Akudama alter column Rank_id set default %L'
             , (select id::text from Ranks order by Sort limit 1));

execute format('alter table Armory alter column Equipment_status_id set default %L',
(select ID::text from Equipment_statuses where Status = 'ГОТОВО'));

execute format ('alter table Machines alter column Machine_status_id set default %L',
(select ID::text from Machine_statuses where Status = 'ГОТОВО'));

execute format ('alter table Policemen alter column Ready_status_id set default %L',
(select ID::text from Ready_statuses where Status = 'ГОТОВ'));

end;
$$ language plpgsql;
select insert_values();
drop function insert_values();

