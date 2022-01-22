package edu.course.dbcourse.objects;

import edu.course.dbcourse.db.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


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
    private static final String SELECT_WHERE_NAME = "select * from People where " +
            "Name = '%s' and Surname is null";
    private static final String SELECT_WHERE_NAME_SURNAME = "select * from People where " +
            "Name = '%s' and Surname = %s";

    public static Human getHumanById(@NonNull Database db, int id) {
        return retrieveHuman(db.executeStatement(String.format(SELECT_WHERE_ID, id)));
    }

    public static Human getHumanByNameSurname(@NonNull Database db, @NonNull String name, String surname) {
        String statement = SELECT_WHERE_NAME_SURNAME;
        if(surname == null || surname.equals(""))
            statement = SELECT_WHERE_NAME;
        return retrieveHuman(db.executeStatement(String.format(statement,
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
                _id = rs.getInt("id");
                _name = rs.getString("name");
                _surname = rs.getString("surname");
                rs.close();
                return new Human(_id, _name, _surname);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Human human = (Human) o;
        return id == human.id && name.equals(human.name) && Objects.equals(surname, human.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }
}
