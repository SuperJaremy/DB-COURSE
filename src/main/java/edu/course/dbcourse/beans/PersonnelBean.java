package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

@ManagedBean(name = "personnel")
@RequestScoped
public class PersonnelBean implements Messaging{

    private final String ERROR = "Ошибка";

    private final String INFO = "Успех";

    @ManagedProperty(value = "#{database}")
    private @Setter
    DataBaseBean db;

    private @Getter @Setter int id;

    private @Getter
    @Setter String name;

    private @Getter @Setter String surname;

    private @Getter
    List<Division> divisions;

    private @Getter List<PoliceRank> ranks;

    private @Getter List<ReadyStatus> statuses;

    private Division division;

    public String getDivision(){
        if(division == null)
            return "";
        return division.getName();
    }

    public void setDivision(String _division){
        divisions.stream().filter((x)-> x.getName().equals(_division)).findFirst()
                .ifPresent(equipmentType -> this.division = equipmentType);
    }

    private ReadyStatus status;

    public String getStatus(){
        if(status == null)
            return "";
        return status.getStatus();
    }

    public void setStatus(String _status){
        statuses.stream().filter((x)->x.getStatus().equals(_status)).findFirst()
                .ifPresent(readyStatus -> this.status = readyStatus);
    }

    private PoliceRank rank;

    public String getRank(){
        if(rank == null)
            return "";
        else
            return rank.getRank();
    }

    public void setRank(String _rank){
        ranks.stream().filter((x)->x.getRank().equals(_rank)).findFirst()
                .ifPresent(policeRank -> this.rank = policeRank);
    }

    @PostConstruct
    public void init(){
        divisions = Division.getAllDivisions(db);
        statuses = ReadyStatus.getAllReadyStatuses(db);
        ranks = PoliceRank.getAllPoliceRanks(db);
    }

    public void addPoliceman(){
        if(name == null){
            addError(ERROR, "Поле 'Имя' обязательно для заполнения");
            return;
        }
        Human human = Human.getHumanByNameSurname(db, name, surname);
        if(human == null){
            addError(ERROR,"Человек с такими данными не найден");
            return;
        }
        if(division == null){
            addError(ERROR, "Поле 'Дивизия' обязательно для заполнения");
            return;
        }
        if(rank == null){
            addError(ERROR, "Поле 'Ранг' обязательно для заполнения");
            return;
        }
        if(!Policeman.addPoliceman(db, human, division, rank)){
            addError(ERROR, "Отсутствует соединение");
            return;
        }
        addInfo(INFO, "Сотрудник добавлен");
        division = null;
        name = null;
        surname = null;
    }

    public void changeStatus(){
        if(status == null){
            addError(ERROR,"Поле 'Статус' обязательно для заполнения");
            return;
        }
        Policeman policeman = Policeman.getPolicemanById(db, id);
        if(policeman == null) {
            addError(ERROR, "Полицейский с таким ID не найден");
            return;
        }
        if(!Policeman.changePolicemanStatus(db, policeman, status)){
            addError(ERROR, "Отсутствует соединение");
            return;
        }
        addInfo(INFO, "Статус сотрудника изменён");
        status = null;
        id = 0;
    }
}
