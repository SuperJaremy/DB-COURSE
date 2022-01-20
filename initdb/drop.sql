drop table Prison cascade;
drop table Articles cascade;
drop table Ranks cascade;
drop table Akudama cascade;
drop table Protocols cascade;
drop table Divisions cascade;
drop table People cascade;
drop table Policemen cascade;
drop table Police_ranks cascade;
drop table Statuses cascade;
drop table Ready_statuses cascade;
drop table Equip_by_division cascade;
drop table Machine_types cascade;
drop table Machines cascade;
drop table Machine_statuses cascade;
drop table Policemen_on_duty cascade;
drop table Equipment_types cascade;
drop table Armory cascade;
drop table Machines_on_duty cascade;
drop table Equipment_statuses cascade;
drop table Mission cascade;
drop function set_policemen_on_mission();
drop function set_machine_on_mission();
drop function fill_protocol();
drop function recalculate_punishment ();
drop function add_prisoner();
drop function promote(participant integer);
drop function promote_participants();
drop function clean_fired_and_dead();
drop function equip_policemen(policemen integer[], mission integer);
drop function transit();
drop function choose_pair(division integer);
drop function call_reinforcements(mission integer);
drop function assign_machines(_machines integer[], _mission integer);
drop function count_successful_missions(policeman integer);
drop type reinforcements;