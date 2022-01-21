package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class Division {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String name;

    @Getter
    private int boss_id;

    private static final String SELECT_WHERE_ID = "select * from Divisions where " +
            "id = %d";

    private static final String SELECT = "select * from Divisions";

    public static Division getDivisionById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if (res.isSuccess()) {
            ResultSet rs = res.getResultSet();
            try {
                if (rs != null && rs.next()) {
                    int _id = rs.getInt("ID");
                    String _name = rs.getString("Name");
                    int _boss_id = rs.getInt("Boss");
                    rs.close();
                    if (_name != null)
                        return new Division(_id, _name, _boss_id);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public static List<Division> getAllDivisions(@NonNull Database db){
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        if(rs != null){
            try{
                List<Division> divisions = new ArrayList<>();
                while(rs.next()){
                    int _id = rs.getInt("ID");
                    String _name = rs.getString("Name");
                    int _boss_id = rs.getInt("Boss");
                    if(_name != null)
                        divisions.add(new Division(_id, _name, _boss_id));
                }
                return divisions;
            } catch (SQLException e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return id == division.id && boss_id == division.boss_id && name.equals(division.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, boss_id);
    }
}
