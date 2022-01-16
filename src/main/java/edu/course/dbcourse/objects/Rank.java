package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class Rank {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String rank;

    private static final String SELECT_WHERE_ID = "select * from Ranks where " +
            "id = %d";

    public static Rank getRankById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                String _rank = rs.getString("Rank");
                rs.close();
                return new Rank(_id, _rank);
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
