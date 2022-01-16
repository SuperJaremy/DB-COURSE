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
public class MachineType {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String type;

    private static final String SELECT_WHERE_ID = "select * from Machine_types where " +
            "id = %d";

    private static final String SELECT = "select * from Machine_types";

    public static MachineType getMachineTypeById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                String _type = rs.getString("Type");
                rs.close();
                return new MachineType(_id, _type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static List<MachineType> getAllMachineTypes(@NonNull Database db) {
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null) {
                List<MachineType> types = new ArrayList<>();
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _type = rs.getString("Type");
                    types.add(new MachineType(_id, _type));
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
