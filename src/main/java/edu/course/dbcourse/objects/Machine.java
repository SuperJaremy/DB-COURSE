package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class Machine {

    @Getter
    private int id;

    @Getter
    private int serialNumber;

    @Getter
    @NonNull
    private MachineType type;

    private static final String SELECT_WHERE_ID = "select * from Machines where " +
            "id = %d";

    private static final String SELECT_WHERE_SERIAL_NUMBER = "select * from Machines where " +
            "Serial_number = %d";

    private static final String INSERT = "insert into Machines(Serial_number, " +
            "Equipment_type_id) values(%d, %d)";

    private static final String UPDATE_STATUS_WHERE_ID = "update Machines set " +
            "Machine_status_id = %d where id = %d";

    public static Machine getMachineById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID,
                id));
        return retrieveMachine(db, res);
    }

    public static Machine getMachineBySerialNumber(@NonNull Database db, int serialNumber) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_SERIAL_NUMBER,
                serialNumber));
        return retrieveMachine(db, res);
    }

    private static Machine retrieveMachine(@NonNull  Database db, @NonNull Database.Result res){
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id = rs.getInt("ID");
                int _serial_number = rs.getInt("Serial_number");
                int _machine_type_id = rs.getInt("Machine_type_id");
                rs.close();
                MachineType _type = MachineType.getMachineTypeById(db, _machine_type_id);
                if (_type != null)
                    return new Machine(_id, _serial_number, _type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public static boolean AddNewMachine(@NonNull Database db, int serialNumber,
                                        @NonNull MachineType type) {
        Database.Result res = db.executeStatement(String.format(INSERT, serialNumber,
                type.getId()));
        return res.isSuccess();
    }

    public static boolean changeMachineStatus(@NonNull Database db,
                                              @NonNull Equipment eq,
                                              @NonNull MachineStatus status) {
        Database.Result res = db.executeStatement(String.format(UPDATE_STATUS_WHERE_ID,
                status.getId(), eq.getId()));
       return res.isSuccess();
    }
}
