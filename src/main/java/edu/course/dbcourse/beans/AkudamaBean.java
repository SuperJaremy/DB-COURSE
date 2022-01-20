package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Akudama;
import edu.course.dbcourse.objects.Human;
import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;


@ManagedBean(name = "akudama")
@RequestScoped
public class AkudamaBean{

    @ManagedProperty(value = "#{database}")
    private @Setter
    DataBaseBean db;

    private @Getter @Setter String name;

    private @Getter @Setter String surname;

    private @Getter String message;

    public void addAkudama(){
        Human human = Human.getHumanByNameSurname(db, name, surname);
        String NO_HUMAN = "Человек с такими данными не найден";
        String DB_ERROR = "Не удалось обновить базу";
        String SUCCESS = "Акудама добавлен";
        if(human == null) {
            message = NO_HUMAN;
            return;
        }
        if(!Akudama.addAkudamaByHuman(db, human)) {
            message = DB_ERROR;
            return;
        }
        message = SUCCESS;
    }

}
