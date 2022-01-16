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
public class ReadyStatus {

    @Getter
    int id;

    @NonNull
    @Getter
    String status;

    private static final String SELECT = "select * from Ready_statuses";

    public static List<ReadyStatus> getAllReadyStatuses(@NonNull Database db) {
        Database.Result res = db.executeStatement(SELECT);
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        if (rs != null) {
            List<ReadyStatus> statuses = new ArrayList<>();
            try {
                while (rs.next()) {
                    int _id = rs.getInt("ID");
                    String _status = rs.getString("Status");
                    if (_status != null)
                        statuses.add(new ReadyStatus(_id, _status));
                }
                rs.close();
                return statuses;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }
}
