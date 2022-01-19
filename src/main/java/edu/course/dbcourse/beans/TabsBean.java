package edu.course.dbcourse.beans;

import edu.course.dbcourse.Role;
import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.Set;

@ManagedBean(name = "tabs")
@RequestScoped
public class TabsBean {
    @ManagedProperty(value = "#{user.roles}")
    private @Getter @Setter Set<Role> roles;

    @ManagedProperty(value = "#{param.pageId}")
    private @Getter @Setter String pageId;

    public String displayPage(){
        if(pageId == null)
            return "welcome";
        for (Role role:
                roles) {
            if(role.getAvailablePages().containsKey(pageId))
                return role.getAvailablePages().get(pageId);
        }
        return "welcome";
    }
}
