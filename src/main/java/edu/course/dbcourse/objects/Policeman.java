package edu.course.dbcourse.objects;

import edu.course.dbcourse.User;
import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@AllArgsConstructor
public class Policeman implements User {

    @Getter
    private int id;

    @Getter
    @NonNull
    private Human human;

    @Getter
    @NonNull
    private Division division;

    @Getter
    @NonNull
    private PoliceRank policeRank;

    private static final String INSERT = "insert into Policemen(Human_id, Division_id," +
            "Police_rank_id) values(%d, %d, %d)";

    private static final String SELECT_WHERE_ID = "select * from Policemen where " +
            "id = %d";

    private static final String SELECT_WHERE_HUMAN_ID = "select * from Policemen where " +
            "human_id = %d";

    private static final String UPDATE_STATUS_WHERE_ID = "update Policemen set Ready_status_id " +
            "= %d where id = %d";

    public static boolean addPoliceman(@NonNull Database db,
                                       @NonNull Human human,
                                       @NonNull Division division,
                                       @NonNull PoliceRank rank) {
        return (db.executeStatement(String.format(INSERT, human.getId(),
                division.getId(), rank.getId()))).isSuccess();
    }

    public static Policeman getPolicemanById(@NonNull Database db, int id) {
        return retrievePoliceman(db, db.executeStatement(String.format(SELECT_WHERE_ID, id)));

    }

    public static Policeman getPolicemanByHumanId(@NonNull Database db, @NonNull Human human) {
        return retrievePoliceman(db, db.executeStatement(String.format(SELECT_WHERE_HUMAN_ID, human.getId())));
    }

    private static Policeman retrievePoliceman(@NonNull Database db, Database.Result res) {
        if (!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                int _human_id = rs.getInt("Human_id");
                int _division_id = rs.getInt("Division_id");
                int _police_rank_id = rs.getInt("Police_rank_id");
                rs.close();
                Human _human = Human.getHumanById(db, _human_id);
                Division _division = Division.getDivisionById(db, _division_id);
                PoliceRank _police_rank = PoliceRank.getPoliceRankById(db, _police_rank_id);
                if (_human != null && _division != null && _police_rank != null)
                    return new Policeman(_id, _human, _division, _police_rank);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static boolean changePolicemanStatus(@NonNull Database db, @NonNull Policeman policeman,
                                                @NonNull ReadyStatus readyStatus) {
        return (db.executeStatement(String.format(UPDATE_STATUS_WHERE_ID, readyStatus.getId(),
                policeman.getId()))).isSuccess();
    }

    @Override
    public int getMyID() {
        return id;
    }

    @Override
    public String getMyName() {
        return human.getName();
    }

    @Override
    public String getMySurname() {
        return human.getSurname();
    }

    @Override
    public String getMyDivisionName() {
        return division.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policeman policeman = (Policeman) o;
        return id == policeman.id && human.equals(policeman.human) && division.equals(policeman.division) && policeRank.equals(policeman.policeRank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, human, division, policeRank);
    }
}
