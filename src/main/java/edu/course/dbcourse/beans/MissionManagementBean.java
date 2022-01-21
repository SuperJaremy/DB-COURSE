package edu.course.dbcourse.beans;


import edu.course.dbcourse.User;
import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Mission;
import edu.course.dbcourse.objects.Policeman;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;

@ManagedBean(name = "missionManagement")
@ViewScoped
public class MissionManagementBean {

    @ManagedProperty(value = "#{database}")
    private @Setter
    DataBaseBean db;

    @ManagedProperty(value = "#{user}")
    private @Setter
    UserBean user;

    private @Getter
    List<Mission> currentMissions;


    private @Getter Map<Integer, String> caughtMessages;

    @PostConstruct
    public void init(){
        Policeman policeman = Policeman.getPolicemanById(db, user.getId());
        if(policeman == null)
            return;
        currentMissions = Mission.getMissionsWithCommander(db, policeman);
        if(currentMissions != null){
           caughtMessages = new HashMap<>();
            for (Mission i:
                 currentMissions) {
                caughtMessages.put(i.getId(), "");
            }
        }
    }

    public String callReinforcements(int missionID){
        Optional<Mission> missionOptional = currentMissions.stream().
                filter((mission -> mission.getId() == missionID)).findFirst();
        Mission mission = null;
        if(missionOptional.isPresent())
            mission = missionOptional.get();
        if(mission == null)
            return "";
        if(!mission.callReinforcements(db))
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "");
        addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Подкрепления вызваны");
        return "";
    }

    public void akudamaCaught(int missionID){
        Optional<Mission> missionOptional = currentMissions.stream().
                filter((mission -> mission.getId() == missionID)).findFirst();
        Mission mission = null;
        if(missionOptional.isPresent())
            mission = missionOptional.get();
        if(mission == null)
            return;
        if(!mission.akudamaCaught(db))
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "");
        addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Акудама захвачен");
    }

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

}
