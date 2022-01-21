package edu.course.dbcourse.beans;

import edu.course.dbcourse.Role;
import edu.course.dbcourse.User;
import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.Set;

@ManagedBean(name = "user")
@SessionScoped
public class UserBean {


    private @Getter @Setter User user;

    private @Getter @Setter Set<Role> roles;

    public int getId(){
        if(user != null)
            return user.getMyID();
        return -1;
    }

    public String getName(){
        return user.getMyName();
    }

    public String getSurname(){
        return user.getMySurname();
    }

    public String getDivision(){
        return user.getMyDivisionName();
    }

    public String exit(){
        this.user = null;
        return "authentication";
    }

}
