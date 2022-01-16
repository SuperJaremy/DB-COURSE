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
public class EquipmentType {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String type;

    private static final String SELECT_WHERE_ID = "select * from Equipment_types where " +
            "id = %d";

    private static final String SELECT = "select * from Equipment_types";

    public static EquipmentType getEquipmentTypeById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt(1);
                String _type = rs.getString(2);
                if (_type != null)
                    return new EquipmentType(_id, _type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static List<EquipmentType> getAllEquipmentTypes(@NonNull Database db) {
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null) {
                List<EquipmentType> types = new ArrayList<>();
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _type = rs.getString("Type");
                    types.add(new EquipmentType(_id, _type));
                }
                rs.close();
                return types;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
