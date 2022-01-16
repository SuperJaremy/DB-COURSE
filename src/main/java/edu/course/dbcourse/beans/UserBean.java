package edu.course.dbcourse.beans;

import edu.course.dbcourse.User;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "user")
@SessionScoped
public class UserBean {
    User user;
}
