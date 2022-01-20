package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class Equipment {

    @Getter
    private int id;

    @Getter
    private int serialNumber;

    @Getter
    @NonNull
    private EquipmentType type;

    private static final String SELECT_WHERE_SERIAL_NUMBER = "select * from Armory where " +
            "Serial_number = %d";

    private static final String INSERT = "insert into Armory(Serial_number, " +
            "Equipment_type_id) values(%d, %d)";

    private static final String UPDATE_STATUS_WHERE_ID = "update Armory set " +
            "Equipment_status_id = %d where id = %d";

    public static Equipment getEquipmentBySerialNumber(@NonNull Database db, int serialNumber) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_SERIAL_NUMBER,
                serialNumber));
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                int _serialNumber = rs.getInt("Serial_number");
                int _type_id = rs.getInt("Equipment_type_id");
                rs.close();
                EquipmentType _type = EquipmentType.getEquipmentTypeById(db, _type_id);
                if (_type != null)
                    return new Equipment(_id, _serialNumber, _type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static boolean AddNewEquipment(@NonNull Database db, int serialNumber,
                                          @NonNull EquipmentType type) {
        Database.Result res = db.executeStatement(String.format(INSERT, serialNumber,
                type.getId()));
       return res.isSuccess();
    }

    public static boolean changeEquipmentStatus(@NonNull Database db,
                                                @NonNull Equipment eq,
                                                @NonNull EquipmentStatus status) {
        Database.Result res = db.executeStatement(String.format(UPDATE_STATUS_WHERE_ID,
                status.getId(), eq.getId()));
        return res.isSuccess();
    }
}
