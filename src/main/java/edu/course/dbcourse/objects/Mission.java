package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
public class Mission {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String address;

    @Getter
    @NonNull
    private Akudama target;

    @Getter
    @NonNull
    private Rank rank;

    @Getter
    @NonNull
    private Policeman missionCommander;

    @Getter
    @NonNull
    private Policemen_on_duty policemen_on_duty;

    @Getter
    @NonNull
    private Machines_on_duty machines_on_duty;

    private static final String INSERT = "insert into Mission(Address, Commander," +
            "Rank_id, Akudama_id) values('%s', %d, %d, %d)";

    private static final String SELECT_WHERE_ADDRESS_COMMANDER_AKUDAMA = "select * from " +
            "Mission where Address = '%s' and Commander = %d and Akudama_id = %d " +
            "and in_process = true";

    private static final String UPDATE_IN_PROCESS_WHERE_ID = "update Mission set " +
            "in_process = false where id = %d";

    private static final String SELECT_WHERE_ID = "select * from Mission where" +
            "id = %d";

    private static final String SELECT_NEXT = "select * from Mission where" +
            "id > %d and in_process = true order by id limit 1";

    private static final String SELECT_WHERE_COMMANDER_AND_IN_PROCESS = "select id from Mission where " +
            "commander = %d and in_process = true";

    private static final String UPDATE_SUCCESS_WHERE_ID = "update Mission set success = true where " +
            "id = %d";

    public static Mission addNewMission(@NonNull Database db,
                                        @NonNull String address,
                                        @NonNull Policeman commander,
                                        @NonNull Rank rank,
                                        @NonNull Akudama akudama) {
        Database.Result res = db.executeStatement(String.format(INSERT,
                address, commander.getId(), rank.getId(), akudama.getId()));

        if (res.isSuccess()) {
            res = db.executeStatement(String.format(SELECT_WHERE_ADDRESS_COMMANDER_AKUDAMA,
                    address, commander.getId(), akudama.getId()));
            if (!res.isSuccess())
                return null;
            ResultSet rs = res.getResultSet();
            try {
                if (rs != null && rs.next()) {
                    int _id = rs.getInt("ID");
                    Mission mission = new Mission(_id, address, akudama, rank, commander,
                            new Policemen_on_duty(new LinkedHashSet<>()),
                            new Machines_on_duty(new LinkedHashSet<>()));
                    List<Policeman> firstPoliceman = new ArrayList<>(1);
                    firstPoliceman.add(commander);
                    if (mission.assignPolicemen(db, firstPoliceman))
                        return mission;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public boolean endMission(@NonNull Database db) {
        return (db.executeStatement(String.format(UPDATE_IN_PROCESS_WHERE_ID,
                this.id))).isSuccess();
    }

    public boolean akudamaCaught(@NonNull Database db){
        return (db.executeStatement(String.format(UPDATE_SUCCESS_WHERE_ID, this.id)).isSuccess());
    }

    public static Mission getMissionById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        return retrieveMission(db, res);
    }

    public static Mission getNextMission(@NonNull Database db, Mission mission) {
        int _id = mission == null ? 0 : mission.getId();
        Database.Result res = db.executeStatement(String.format(SELECT_NEXT, _id));
        return retrieveMission(db, res);
    }

    private static Mission retrieveMission(@NonNull Database db, Database.Result res) {
        if (!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                String _address = rs.getString("Address");
                int _commander_id = rs.getInt("Commander");
                int _rank_id = rs.getInt("Rank_id");
                int _akudama_id = rs.getInt("Akudama_id");
                rs.close();
                Policeman _commander = Policeman.getPolicemanById(db, _commander_id);
                Rank _rank = Rank.getRankById(db, _rank_id);
                Akudama _target = Akudama.getAkudamaById(db, _akudama_id);
                Policemen_on_duty _policemen_on_duty = Policemen_on_duty.getPolicemenByMissionId(db, _id);
                Machines_on_duty _machines_on_duty = Machines_on_duty.getMachinesByMissionId(db, _id);
                if (_address != null && _commander != null && _rank != null && _target != null
                        && _policemen_on_duty != null && _machines_on_duty != null)
                    return new Mission(_id, _address, _target, _rank, _commander,
                            _policemen_on_duty, _machines_on_duty);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static List<Mission> getMissionsWithCommander(@NonNull Database db, @NonNull Policeman policeman) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_COMMANDER_AND_IN_PROCESS, policeman.getId()));
        if (res.isSuccess() && res.getResultSet() != null) {
            ResultSet rs = res.getResultSet();
            try {
                List<Mission> missions = new ArrayList<>();
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _address = rs.getString("Address");
                    int _commander_id = rs.getInt("Commander");
                    int _rank_id = rs.getInt("Rank_id");
                    int _akudama_id = rs.getInt("Akudama_id");
                    Policeman _commander = Policeman.getPolicemanById(db, _commander_id);
                    Rank _rank = Rank.getRankById(db, _rank_id);
                    Akudama _target = Akudama.getAkudamaById(db, _akudama_id);
                    Policemen_on_duty _policemen_on_duty = Policemen_on_duty.getPolicemenByMissionId(db, _id);
                    Machines_on_duty _machines_on_duty = Machines_on_duty.getMachinesByMissionId(db, _id);
                    if (_address != null && _commander != null && _rank != null && _target != null
                            && _policemen_on_duty != null && _machines_on_duty != null)
                        missions.add(new Mission(_id, _address, _target, _rank, _commander,
                                _policemen_on_duty, _machines_on_duty));
                }
                if(missions.isEmpty())
                    return null;
                return missions;
            } catch (SQLException e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public boolean assignPolicemen(@NonNull Database db, @NonNull List<Policeman> policemen) {
        return this.policemen_on_duty.assignPolicemen(db, this.id, policemen);
    }

    public boolean assignMachines(@NonNull Database db, @NonNull List<Machine> machines) {
        return machines_on_duty.assignMachines(db, this.id, machines);
    }

    @AllArgsConstructor
    private static class Policemen_on_duty {

        @NonNull
        @Getter
        private Set<Policeman> policemen;

        private static final String SELECT_WHERE_MISSION_ID = "select * from" +
                "Policemen_on_duty where Mission_id = %d";

        private static final String INSERT_POLICEMAN = "select equip_policemen(" +
                "ARRAY%s, %d)";

        private static Policemen_on_duty getPolicemenByMissionId(@NonNull Database db, int mission_id) {
            Database.Result res = db.executeStatement(String.format(SELECT_WHERE_MISSION_ID,
                    mission_id));
            if (!res.isSuccess())
                return null;
            ResultSet rs = res.getResultSet();
            if (rs != null) {
                try {
                    List<Integer> _policemen_ids = new ArrayList<>();
                    while (rs.next()) {
                        _policemen_ids.add(rs.getInt("Policeman_id"));
                    }
                    rs.close();
                    Set<Policeman> _policemen = new LinkedHashSet<>();
                    for (Integer i :
                            _policemen_ids) {
                        Policeman _policeman = Policeman.getPolicemanById(db, i);
                        if (_policeman != null)
                            _policemen.add(_policeman);
                    }
                    return new Policemen_on_duty(_policemen);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            return null;
        }

        private boolean assignPolicemen(@NonNull Database db, int mission_id,
                                        @NonNull List<Policeman> policemen) {
            int[] policemenIds = policemen.stream()
                    .filter((Policeman policeman) -> !this.policemen.contains(policeman))
                    .peek((Policeman policeman) -> this.policemen.add(policeman))
                    .mapToInt(Policeman::getId).toArray();
            Database.Result res = db.executeStatement(String.format(INSERT_POLICEMAN,
                    Arrays.toString(policemenIds), mission_id));
            return res.isSuccess();

        }
    }

    @AllArgsConstructor
    private static class Machines_on_duty {

        @NonNull
        @Getter
        private Set<Machine> machines;

        private static final String SELECT_WHERE_MISSION_ID = "select * from" +
                "Machines_on_duty where Mission_id = %d";

        private static final String INSERT_MACHINE = "insert into Machines_on_duty" +
                "(Mission_id, Machine_id) values(%d, %d)";

        private static Machines_on_duty getMachinesByMissionId(@NonNull Database db,
                                                               int mission_id) {
            Database.Result res = db.executeStatement(String.format(SELECT_WHERE_MISSION_ID,
                    mission_id));
            if (!res.isSuccess())
                return null;
            ResultSet rs = res.getResultSet();
            if (rs != null) {
                try {
                    List<Integer> _machines_ids = new ArrayList<>();
                    while (rs.next()) {
                        _machines_ids.add(rs.getInt("Policeman_id"));
                    }
                    rs.close();
                    Set<Machine> _machines = new LinkedHashSet<>();
                    for (Integer i :
                            _machines_ids) {
                        Machine _machine = Machine.getMachineById(db, i);
                        if (_machine != null)
                            _machines.add(_machine);
                    }
                    return new Machines_on_duty(_machines);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            return null;
        }

        private boolean assignMachines(@NonNull Database db, int mission_id,
                                       @NonNull List<Machine> machines) {
            for (Machine i :
                    machines) {
                if (!this.machines.contains(i)) {
                    Database.Result res = db.executeStatement(String.format(INSERT_MACHINE, mission_id, i.getId()));
                    if (!res.isSuccess())
                        return false;
                    this.machines.add(i);
                }
            }
            return true;
        }
    }
}
