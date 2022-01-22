package edu.course.dbcourse.beans;


import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Akudama;
import edu.course.dbcourse.objects.Article;
import edu.course.dbcourse.objects.Human;
import edu.course.dbcourse.objects.Policeman;
import lombok.Getter;
import lombok.Setter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "protocol")
@ViewScoped
public class ProtocolBean {

    @ManagedProperty(value = "#{database}")
    private @Setter
    DataBaseBean db;

    @ManagedProperty(value = "#{user}")
    private @Setter UserBean user;

    private @Getter @Setter String name;

    private @Getter @Setter String surname;

    private @Getter @Setter int akudamaId;

    private @Getter @Setter boolean NameSurname = true;

    private @Getter @Setter int articleId;

    public void sendProtocol(){
        Policeman policeman = Policeman.getPolicemanById(db, user.getId());
        if(policeman == null)
            return;
        Akudama akudama;
        if(this.NameSurname) {
            if(name == null){
                addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Поле 'Имя' обязательно для заполнения");
            }
            Human human = Human.getHumanByNameSurname(this.db, this.name, this.surname);
            if(human == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Человек с такими данными не найден");
                return;
            }
            akudama = Akudama.getAkudamaByHuman(db, human);
        }
        else
            akudama = Akudama.getAkudamaById(db, akudamaId);
        Article article = Article.getArticleById(db, articleId);
        if(article == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Нет статьи с таким ID");
            return;
        }
        if(akudama == null){
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Такого человека нет в списке Акудама");
            return;
        }
        if(!akudama.makeProtocol(db, article, policeman)){
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Ошибка соединения");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Протокол внесён");
    }

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
