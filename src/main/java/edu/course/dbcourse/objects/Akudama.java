package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@AllArgsConstructor
public class Akudama {

    @Getter
    private int id;

    @Getter
    @NonNull
    private Human human;

    @Getter
    @NonNull
    private Rank rank;

    private static final String INSERT_HUMAN_ID = "insert into Akudama(Human_id) " +
            "values(%d)";

    private static final String SELECT_WHERE_HUMAN_ID = "select * from Akudama where " +
            "Human_id = %d";

    private static final String SELECT_WHERE_ID = "select * from Akudama where " +
            "id = %d";

    private static final String INSERT_PROTOCOL = "insert into protocols(akudama_id, article_id," +
            "added_by) values(%d, %d, %d)";

    public static boolean addAkudamaByHuman(@NonNull Database db, @NonNull Human human) {
        int human_id = human.getId();
        Database.Result rs = db.executeStatement(String.format(INSERT_HUMAN_ID, human_id));
        return rs.isSuccess();
    }

    public static Akudama getAkudamaById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        return retrieveAkudama(db, res);
    }

    public static Akudama getAkudamaByHuman(@NonNull Database db, @NonNull Human human) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_HUMAN_ID,
                human.getId()));
        return retrieveAkudama(db, res);
    }

    private static Akudama retrieveAkudama(@NonNull Database db, @NonNull Database.Result res) {
        try {
            ResultSet rs = res.getResultSet();
            if (res.isSuccess() && rs.next()) {
                int _id = rs.getInt("ID");
                int _human_id = rs.getInt("Human_id");
                int _rank_id = rs.getInt("Rank_id");
                rs.close();
                Human _human = Human.getHumanById(db, _human_id);
                Rank _rank = Rank.getRankById(db, _rank_id);
                if (_human != null && _rank != null) {
                    return new Akudama(_id, _human, _rank);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public boolean makeProtocol(@NonNull Database db, @NonNull Article article, @NonNull Policeman policeman){
        return (db.executeStatement(String.format(INSERT_PROTOCOL, this.id, article.getId(), policeman.getId()))).isSuccess();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Akudama akudama = (Akudama) o;
        return id == akudama.id && human.equals(akudama.human) && rank.equals(akudama.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, human, rank);
    }
}
