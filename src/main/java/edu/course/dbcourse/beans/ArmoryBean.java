package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Equipment;
import edu.course.dbcourse.objects.EquipmentStatus;
import edu.course.dbcourse.objects.EquipmentType;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

@ManagedBean(name = "armory")
@RequestScoped
public class ArmoryBean {

    @ManagedProperty(value = "#{database}")
    private @Setter DataBaseBean db;

    private @Getter @Setter int serial_number;

    private @Getter List<EquipmentType> types;

    private @Getter List<EquipmentStatus> statuses;

    private EquipmentType type;

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

    private EquipmentStatus status;

    public String getStatus(){
        if(status == null)
            return "";
        return status.getName();
    }

    public void setStatus(String _status){
        statuses.stream().filter((x)->x.getName().equals(_status)).findFirst()
                .ifPresent(equipmentStatus -> this.status = equipmentStatus);
    }

    @PostConstruct
    public void init(){
        types = EquipmentType.getAllEquipmentTypes(db);
        statuses = EquipmentStatus.getAllEquipmentStatuses(db);
    }

    public void addEquipment(){
        if(!Equipment.AddNewEquipment(db, serial_number, type)) {
            addMessage = "Ошибка";
            return;
        }
        addMessage = "Добавлено";
    }

    public void changeStatus(){
        Equipment equipment = Equipment.getEquipmentBySerialNumber(db, serial_number);
        if(equipment == null) {
            changeMessage = "Ошибка";
            return;
        }
        if(!Equipment.changeEquipmentStatus(db, equipment, status)){
            changeMessage = "Ошибка";
            return;
        }
        changeMessage = "Изменено";
    }
}
