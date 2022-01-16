create trigger tprotocol before insert on Protocols for each row execute procedure fill_protocol();

create trigger tprotart after insert on Protocols for each row execute procedure recalculate_punishment();

create trigger tprisoner before insert on Prison for each row execute procedure add_prisoner();

create trigger tpromote after update on Mission for each row when (NEW.Success is distinct from OLD.Success) execute procedure promote_participants();

create trigger tsetpolice after insert on Policemen_on_duty for each row execute procedure set_policeman_on_mission();

create trigger tsetmachine after insert on Machines_on_duty for each row execute procedure set_machine_on_mission();
