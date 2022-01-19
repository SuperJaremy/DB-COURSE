package edu.course.dbcourse.beans;

import edu.course.dbcourse.Role;
import edu.course.dbcourse.User;
import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Human;
import edu.course.dbcourse.objects.Mission;
import edu.course.dbcourse.objects.Policeman;
import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.Set;

@ManagedBean(name = "authentication")
@RequestScoped
public class AuthBean {

    @ManagedProperty(value = "#{user}")
    private @Setter UserBean user;

    @ManagedProperty(value = "#{database}")
    private @Setter DataBaseBean db;

    private @Getter @Setter int id;

    private @Getter boolean authenticationSucceed = false;

    public void checkUser(){
        Human human = Human.getHumanById(db, id);
        if(human != null){
            Policeman policeman = Policeman.getPolicemanByHumanId(db, human);
            if(policeman != null) {
                authenticationSucceed = true;
                user.setUser(policeman);
                setUserRoles(policeman);
            }
        }
    }

    private void setUserRoles(Policeman policeman){
        Set<Role> roles = Role.getRolesByDivision(user.getDivision());
        if(Mission.checkCommander(db ,policeman))
            roles.add(Role.MISSION_COMMANDER);
        user.setRoles(roles);
    }

}
