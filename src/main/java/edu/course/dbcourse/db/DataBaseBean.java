package edu.course.dbcourse.db;

import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@ManagedBean(name = "database")
@ApplicationScoped
public class DataBaseBean implements Database {

    @Setter
    private int MAX_CONNECTIONS;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String dataBaseUrl;

    private BlockingQueue<Connection> connections;

    @PostConstruct
    public void init() throws SQLException {
        connections = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            Connection connection = DriverManager.getConnection(dataBaseUrl, username, password);
            connections.add(connection);
        }
    }

    @PreDestroy
    public void destroy() throws SQLException{
        for (Connection i:
             connections) {
            i.close();
        }
    }

    public Result executeStatement(String statement){
        try {
            Connection connection = connections.take();
            Statement _statement = connection.createStatement();
            ResultSet result;
            boolean success = false;
            if(!_statement.execute(statement)) {
                if(_statement.getUpdateCount() > 0)
                    success = true;
                result = null;
            }
            else {
                result = _statement.getResultSet();
                success = true;
            }
            connections.put(connection);
            return new Result(result, success);
        }
        catch (InterruptedException | SQLException e){
            System.out.println(e.getMessage());
            return new Result(null, false);
        }
    }

}
