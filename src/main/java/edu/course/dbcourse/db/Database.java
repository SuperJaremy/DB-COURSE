package edu.course.dbcourse.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;

public interface Database {

    @AllArgsConstructor
    class Result{

        @Getter
        private ResultSet resultSet;

        @Getter
        private boolean success;

    }

     Result executeStatement(String statement);
}
