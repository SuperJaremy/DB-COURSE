package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class Article {

    @Getter
    private int id;

    @Getter
    @NonNull
    private Rank rank;

    @Getter
    @NonNull
    private PGInterval punishment;

    @Getter
    @NonNull
    private String text;

    private static final String SELECT_WHERE_ID = "select * from Articles where " +
            "id = %d";

    private static final String SELECT_NEXT = "select * from Articles where " +
            "id > %d order by id limit 1";

    public static Article getArticleById(@NonNull Database db, int id) {
        Database.Result res = db.executeStatement(String.format(SELECT_WHERE_ID, id));
        return retrieveArticle(db, res);
    }

    public static Article getNextArticle(@NonNull Database db, Article article){
        int _id = article == null ? 0 : article.getId();
        Database.Result res = db.executeStatement(String.format(SELECT_NEXT, _id));
        return retrieveArticle(db, res);
    }

    private static Article retrieveArticle(@NonNull Database db, Database.Result res){
        try {
            ResultSet rs = res.getResultSet();
            if (res.isSuccess() && rs.next()) {
                int _id = rs.getInt("ID");
                String _text = rs.getString("Text");
                PGInterval _punishment = (PGInterval) rs.getObject("Years_of_punishment");
                int rank_id = rs.getInt("Rank_id");
                rs.close();
                Rank _rank = Rank.getRankById(db, rank_id);
                if (_rank != null)
                    return new Article(_id, _rank, _punishment, _text);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
