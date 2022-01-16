--Добвать в протокол данные о заполнившем лице
create or replace function fill_protocol() returns trigger as $$
    BEGIN
        NEW.Added_at := current_timestamp;
        return NEW;
    END;
$$ language plpgsql;

create or replace function set_policeman_on_mission() returns trigger as $$
    BEGIN
        update policemen set ready_status_id = (select id from ready_statuses where status = 'НА ЗАДАНИИ') where id = NEW.policeman_id;
        return NEW;
    END;
$$ language plpgsql;

create or replace function set_machine_on_mission() returns trigger as $$
    BEGIN
        update machines set machine_status_id = (select id from machine_statuses where status = 'НА ЗАДАНИИ') where id = NEW.machine_id;
        return NEW;    
    END;
$$ language plpgsql;

--Расчёт срока заключения преступника по всем его правонарушениям в соответсвии с кодексом
--create or replace function count_years_of_detention(_Akudama_id integer) returns interval as $$
    --DECLARE
        --years_of_detention interval;
    --BEGIN
        --select sum(Articles.Years_of_punishment) from Protocols inner join Articles on (Protocols.Article_id = Articles.ID) where Protocols.Akudama_id = _Akudama_id into years_of_detention;
        --return years_of_detention;
    --END;
--$$ language plpgsql;

--Внесение нового срока в таблицу
create or replace function recalculate_punishment () returns trigger as $$
    DECLARE
        punishment interval;
	max_rank integer;
    BEGIN
        select sum(Articles.Years_of_punishment) from Protocols inner join Articles on (Protocols.Article_id = Articles.ID) where Protocols.Akudama_id = NEW.Akudama_id into punishment;
	select max(Articles.Rank_id) from Protocols inner join Articles on (Protocols.Article_id = Articles.ID) where Protocols.Akudama_id = NEW.Akudama_id limit 1 into max_rank;
	update Akudama set years_of_detention = punishment, rank_id = max_rank where ID = NEW.Akudama_id;
        return NEW;
    END;
$$ language plpgsql;

--Добавление заключённых в тюрьму
create or replace function add_prisoner() returns trigger as $$
    DECLARE
        punishment interval;
    BEGIN
        select years_of_detention from Akudama where ID = NEW.Akudama_id into punishment;
        NEW.Detention_begin = current_timestamp;
        NEW.Detention_end = NEW.Detention_begin + punishment;
        return NEW;
    END;
$$language plpgsql;

create or replace function count_successful_missions(policeman integer) returns integer as $$
    DECLARE
        cnt integer;
    BEGIN
        select count(distinct Policemen_on_duty.Mission_id) from Policemen_on_duty inner join Mission on (Policemen_on_duty.Mission_id = Mission.ID) where Mission.Success = true and Policemen_on_duty.Policeman_id = policeman into cnt;
        return cnt;
    END;
$$ language plpgsql;

create or replace function promote(participant integer) returns void as $$
    DECLARE
        old_rank integer;
        new_rank integer;
        missions_to_promote integer = 10;
    BEGIN
        select Police_rank_id from Policemen where ID = participant into old_rank;
        select ID from Police_ranks where (select Sort from Ranks where ID = old_rank) < Sort order by Sort limit 1 into new_rank;
        if new_rank is not NULL then
            missions_to_promote := missions_to_promote * ((select Sort from Ranks where ID = new_rank) - 1);
            if count_successful_missions(participant) < missions_to_promote  then
                return;
            end if;
            update Policemen set Police_rank_id = new_rank where ID = participant;
        end if;
    END;
$$ language plpgsql;

create or replace function promote_participants() returns trigger as $$
    DECLARE
        participants integer[];
        id integer;
    BEGIN
        if NEW.Success then
            participants = array( select Policeman_id from Policemen_on_duty where Mission_id = NEW.ID );
            if array_length(participants, 1) < 1 then
                return NEW;
            end if;
            foreach id in array participants
            loop
                execute promote(id);
            end loop;
         end if;
         return NEW;
    END;
$$ language plpgsql;        

create or replace function clean_fired_and_dead() returns void as $$
    DECLARE
        to_clean integer[];
    BEGIN
        to_clean = array(select Policemen.ID from Policemen inner join Ready_statuses on (Policemen.Ready_status_id = Ready_statuses.id) where Status in ('УВОЛЕН', 'МЕРТВ'));
        update Policeman set is_working = false where ID = any(to_clean);
    END;
$$ language plpgsql;

create or replace function equip_policemen(_policemen integer[], mission integer) returns void as $$
    DECLARE
        equipment_types integer[];
        equipment integer[];
        equip integer;
        eq integer;
        division integer;
        i integer;
        policeman integer;
    BEGIN
        if array_length(_policemen, 1) < 1 then
            return;
        end if;
        foreach policeman in array _policemen
        loop
            equipment = array[]::int[];
            select Division_id from Policemen where ID = policeman into division;
            equipment_types = array(select distinct Equipment_type_id from Equip_by_division where Division_id = division);
            i := 0;
            foreach equip in array equipment_types
            loop
                select Armory.ID from Armory inner join Equipment_statuses on (Armory.Equipment_status_id = Equipment_statuses.ID) where Armory.Equipment_type_id = equip and Equipment_statuses.Status = 'ГОТОВО' limit 1 into eq;
                equipment := array_append(equipment, eq);
            end loop;
            if array_length(equipment, 1) < 1 then
                return;
            end if;
            foreach equip in array equipment
            loop
                insert into Policemen_on_duty values(mission, equip, policeman);
                update Armory set Equipment_status_id = (select ID from Equipment_statuses where Status = 'ЗАНЯТО') where ID = equip;
            end loop;
        end loop;
    END;
$$ language plpgsql;

create or replace function assign_machines(_machines integer[], _mission integer) returns void as $$
    DECLARE
        machine integer;
    BEGIN
        if array_length(_machines, 1) < 1 then
            return;
        end if;
        foreach machine in array _machines
        loop
            insert into Machines_on_duty(Mission_id, Machine_id) values (_mission, machine);
        end loop;
    END;
$$ language plpgsql;

create or replace function transit() returns integer as $$
    DECLARE
        cnt integer := 0;
        executioners_threshold integer := 20;
        swat_threshold integer := 150;
        soilders integer;
        _bottom_line integer;
        candidates integer[];
        candidate integer;
        id_to integer;
        id_from integer;
    BEGIN
        select count(*) from Policemen inner join Divisions on (Policemen.Division_id = Divisions.ID) where Divisions.Name = 'Палачи' and Policemen.is_working into soilders;
        if soilders < executioners_threshold then
            select ID from Divisions where Name = 'Отдел спецопераций' into id_from;
            select ID, Bottom_line from Divisions where Name = 'Палачи' into id_to, _bottom_line;
            candidates := array(select Policemen.ID from Policemen inner join Police_ranks on (Policemen.Police_rank_id = Police_ranks.ID) where (Police_ranks.Sort >= _bottom_line) and (Policemen.Division_id = id_from) and Policemen.is_working order by Police_ranks.Sort desc);
            if array_length(candidates, 1) > 0 then
                foreach candidate in array candidates
                loop
                    if candidate = (select Boss from Divisions where ID = id_from) then
                        continue;
                    end if;
                    update Policemen set Division_id = id_to where ID = candidate;
                    soilders := soilders + 1;
                    cnt := cnt + 1;
                    exit when soilders = executioners_threshold;
                end loop;
            end if;
        end if;
        select count(*) from Policemen inner join Divisions on (Policemen.Division_id = Divisions.ID) where Divisions.Name = 'Отдел спецопераций' and Policemen.is_working into soilders;
        if soilders < executioners_threshold then
            select ID from Divisions where Name = 'Патрульный отдел' into id_from;
            select ID, Bottom_line from Divisions where Name = 'Отдел спецопераций' into id_to, _bottom_line;
            candidates := array( select Policemen.ID from Policemen inner join Police_ranks on (Policemen.Police_rank_id = Police_ranks.ID) where Police_ranks.Sort >= _bottom_line and Policemen.Division_id = id_from and Policemen.is_working  order by Police_ranks.Sort);
            if array_length(candidates, 1) > 0 then
                foreach candidate in array candidates
                loop
                    if candidate = (select Boss from Divisions where ID = id_from) then
                            continue;
                    end if;
                    update Policemen set Division_id = id_to where ID = candidate;
                    soilders := soilders + 1;
                    cnt := cnt + 1;
                    exit when soilders = executioners_threshold;
                end loop;
            end if;
        end if;
        return cnt;
    END;
$$ language plpgsql;

create type reinforcements as (_machines_ integer[], _policeman_ integer[]);

create or replace function choose_pair(division integer) returns reinforcements as $$
    DECLARE
        first_person int;
        second_person int;
        chosen int[];
        supl reinforcements;
        supl2 reinforcements;
    BEGIN
        if (select Name from Divisions where ID = division) = 'Технический отдел' then
            chosen := array(select Machines.ID from Machines inner join Machine_statuses on (Machines.Machine_status_id = Machine_statuses.ID) inner join Machine_types on (Machines.Machine_type_id = Machine_types.ID) where Machine_types.Type = 'Патрульный робот' and Machine_statuses.Status = 'ГОТОВО' limit 5);
            if array_length(chosen, 1) > 0 then
                update Machines set Machine_status_id = (select ID from Machine_statuses where Status = 'НА ЗАДАНИИ') where ID = any(chosen);
            end if;
            return row(chosen, array[]::int[]);
        end if;
        select Policemen.ID from Policemen inner join Police_ranks on (Policemen.Police_rank_id = Police_ranks.ID) inner join Ready_statuses on (Policemen.Ready_status_id = Ready_statuses.ID ) where Ready_statuses.Status = 'ГОТОВ' and Policemen.Division_id = division order by Police_ranks.Sort limit 1 into first_person;
        if first_person is null then
            for i in 1..4
            loop
                supl := choose_pair( division + 1 );
                supl2._machines_ := supl2._machines_ || supl._machines_;
                supl2._policeman_ := supl2._policeman_ || supl._policeman_;
            end loop;
            return supl2;
        end if;
        update Policemen set Ready_status_id = (select ID from Ready_statuses where Status = 'НА ЗАДАНИИ') where ID = first_person;
        select Policemen.ID from Policemen inner join Police_ranks on (Policemen.Police_rank_id = Police_ranks.ID) inner join Ready_statuses on (Policemen.Ready_status_id = Ready_statuses.ID ) where Ready_statuses.Status = 'ГОТОВ' and Policemen.Division_id = division order by Police_ranks.Sort desc limit 1 into second_person;
        if second_person is null then
            supl := choose_pair(division +1 );
            supl2 := choose_pair(division +1 );
            return row(supl._machines_||supl2._machines_, first_person || supl._policeman_ || supl2._policeman_);
        end if;
        update Policemen set Ready_status_id = (select ID from Ready_statuses where Status = 'НА ЗАДАНИИ') where ID = second_person;
        return row(array[]::int[], array[first_person, second_person]);
    END;
$$ language plpgsql;
        

create or replace function call_reinforcements(_mission integer) returns void as $$
    DECLARE
        mission_rank integer;
        mission_difficulty integer;
        division integer;
        _reinforcements reinforcements;
    BEGIN
        if not (select exists(select 1 from Mission where id=_mission)) then
            raise exception 'no_data_found';
        end if;
        select Rank_id from Mission where ID = _mission into mission_rank;
        select Sort from Ranks where ID = mission_rank into mission_difficulty;
        if mission_difficulty < (select Max(Sort) from Ranks) then
            update Mission set Rank_id = (select ID from Ranks where Sort = (mission_difficulty+1)) where ID = _mission;
            select Rank_id from Mission where ID = _mission into mission_rank;
        end if;
        select Division_id from Ranks where ID = mission_rank into division;
        _reinforcements := choose_pair(division);
        execute equip_policemen(_reinforcements._policeman_, _mission);
        execute assign_machines(_reinforcements._machines_, _mission);
    END;
$$ language plpgsql;
