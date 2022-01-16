package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;


@AllArgsConstructor
public class Human {

    @Getter
    private int id;

    @Getter
    @NonNull
    private String name;

    @Getter
    private String surname;

    private static final String SELECT_WHERE_ID = "select * from People where id = %d";
    private static final String SELECT_WHERE_NAME_SURNAME = "select * from People where " +
            "Name = '%s' and Surname = %s";

    public static Human getHumanById(@NonNull Database db, int id) {
        return retrieveHuman(db.executeStatement(String.format(SELECT_WHERE_ID, id)));
    }

    public static Human getHumanByNameSurname(@NonNull Database db, @NonNull String name, String surname) {
        if (surname != null)
            surname = "'" + surname + "'";
        return retrieveHuman(db.executeStatement(String.format(SELECT_WHERE_NAME_SURNAME,
                name, surname)));
    }


    private static Human retrieveHuman(Database.Result res) {
        if(!res.isSuccess())
            return null;
        ResultSet rs = res.getResultSet();
        try {
            if (rs != null && rs.next()) {
                int _id;
                String _name;
                String _surname;
                _id = rs.getInt(1);
                _name = rs.getString(2);
                _surname = rs.getString(3);
                rs.close();
                return new Human(_id, _name, _surname);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

}
