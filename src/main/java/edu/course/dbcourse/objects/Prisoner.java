package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@AllArgsConstructor
public class Prisoner {

    @Getter
    private int id;

    @Getter
    @NonNull
    private Akudama akudama;

    @Getter
    @NonNull
    private LocalDateTime begin;

    @Getter
    @NonNull
    private LocalDateTime end;

    private static final String INSERT = "insert into Prison(Akudama_id) " +
            "values(%d)";

    private static final String SELECT_NEXT = "select * from Prison where " +
            "id > %d and detention_end > current_timestamp order by id limit 1";

    public static boolean AddPrisoner(@NonNull Database db, @NonNull Akudama akudama) {
        return (db.executeStatement(String.format(INSERT, akudama.getId()))).isSuccess();
    }

    public static Prisoner getNextPrisoner(@NonNull Database db, Prisoner prisoner) {
        int _id = prisoner == null ? 0 : prisoner.getId();
        Database.Result res = db.executeStatement(String.format(SELECT_NEXT, _id));
        if (!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                _id = rs.getInt("ID");
                int _akudama_id = rs.getInt("Akudama_id");
                LocalDateTime _detention_end = rs.getObject("Detention_end",
                        LocalDateTime.class);
                LocalDateTime _detention_begin = rs.getObject("Detention_begin",
                        LocalDateTime.class);
                rs.close();
                Akudama _akudama = Akudama.getAkudamaById(db, _akudama_id);
                if (_akudama != null && _detention_begin != null && _detention_end != null)
                    return new Prisoner(_id, _akudama, _detention_begin, _detention_end);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
