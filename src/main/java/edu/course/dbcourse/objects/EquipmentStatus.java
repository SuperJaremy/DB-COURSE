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
public class EquipmentStatus {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String name;

    private static final String SELECT = "select * from Equipment_statuses";

    public static List<EquipmentStatus> getAllEquipmentStatuses(@NonNull Database db) {
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null) {
                List<EquipmentStatus> list = new ArrayList<>();
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _name = rs.getString("Status");
                    if (_name != null)
                        list.add(new EquipmentStatus(_id, _name));
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
