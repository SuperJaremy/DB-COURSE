package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class Policeman {

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

    public static boolean AddPoliceman(@NonNull Database db,
                                       @NonNull Human human,
                                       @NonNull Division division,
                                       @NonNull PoliceRank rank) {
        return (db.executeStatement(String.format(INSERT, human.getId(),
                division.getId(), rank.getId()))).isSuccess();
    }

    public static Policeman getPolicemanById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if(!res.isSuccess())
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
}
