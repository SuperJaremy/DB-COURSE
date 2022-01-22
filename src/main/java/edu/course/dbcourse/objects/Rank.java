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
public class Rank {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String rank;

    private static final String SELECT_WHERE_ID = "select * from Ranks where " +
            "id = %d";

    private static final String SELECT = "select * from Ranks";

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

    public static List<Rank> getAllRanks(@NonNull Database db){
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess() || res.getResultSet() == null)
            return null;
        ResultSet rs = res.getResultSet();
        List<Rank> ranks = new ArrayList<>();
        try{
            while(rs.next()){
                int id = rs.getInt("id");
                String rank = rs.getString("rank");
                if(rank != null)
                    ranks.add(new Rank(id, rank));
            }
            return ranks;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
