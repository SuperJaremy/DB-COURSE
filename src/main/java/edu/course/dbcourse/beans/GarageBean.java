package edu.course.dbcourse.beans;


import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

@ManagedBean(name = "garage")
@RequestScoped
public class GarageBean {

    @ManagedProperty(value = "#{database}")
    private @Setter
    DataBaseBean db;

    private @Getter
    @Setter int serial_number;

    private @Getter
    List<MachineType> types;

    private @Getter List<MachineStatus> statuses;

    private MachineType type;

    private @Getter String addMessage;

    private @Getter String changeMessage;

    public String getType(){
        if(type == null)
            return "";
        return type.getType();
    }

    public void setType(String _type){
        types.stream().filter((x)-> x.getType().equals(_type)).findFirst()
                .ifPresent(equipmentType -> this.type = equipmentType);
    }

    private MachineStatus status;

    public String getStatus(){
        if(status == null)
            return "";
        return status.getStatus();
    }

    public void setStatus(String _status){
        statuses.stream().filter((x)->x.getStatus().equals(_status)).findFirst()
                .ifPresent(equipmentStatus -> this.status = equipmentStatus);
    }

    @PostConstruct
    public void init(){
        types = MachineType.getAllMachineTypes(db);
        statuses = MachineStatus.getAllMachineStatuses(db);
    }

    public void addMachine(){
        if(!Machine.addNewMachine(db, serial_number, type)) {
            addMessage = "Ошибка";
            return;
        }
        addMessage = "Добавлено";
    }

    public void changeStatus(){
        Machine machine = Machine.getMachineBySerialNumber(db, serial_number);
        if(machine == null) {
            changeMessage = "Ошибка";
            return;
        }
        if(!Machine.changeMachineStatus(db, machine, status)){
            changeMessage = "Ошибка";
            return;
        }
        changeMessage = "Изменено";
    }

}
