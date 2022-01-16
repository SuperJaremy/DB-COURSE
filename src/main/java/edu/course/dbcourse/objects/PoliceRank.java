package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PoliceRank {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String rank;

    private static final String SELECT_WHERE_ID = "select * from Police_ranks where " +
            "id = %d";

    private static final String SELECT = "select * from Police_ranks";

    public static PoliceRank getPoliceRankById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                String _rank = rs.getString("Rank");
                rs.close();
                return new PoliceRank(_id, _rank);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static List<PoliceRank> getAllPoliceRanks(@NonNull Database db) {
        Database.Result res = db.executeStatement(SELECT);
        if (!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null) {
                List<PoliceRank> list = new ArrayList<>();
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _rank = rs.getString("Rank");
                    if (_rank != null)
                        list.add(new PoliceRank(_id, _rank));
                }
                rs.close();
                return list;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
