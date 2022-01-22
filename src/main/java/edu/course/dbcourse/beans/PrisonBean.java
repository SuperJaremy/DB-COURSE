package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Akudama;
import edu.course.dbcourse.objects.Prisoner;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "prison")
@ViewScoped
public class PrisonBean implements Messaging {
    @ManagedProperty("#{database}")
    private @Setter
    DataBaseBean db;

    private @Getter @Setter int akudamaId;

    private List<Prisoner> allPrisoners;
    private @Getter
    List<Prisoner> currentFrame;

    private @Getter
    int currentPage = 1;

    private final int PRISONERS_ON_PAGE = 10;

    private @Getter
    boolean canMoveForward;

    private @Getter
    boolean canMoveBackward;

    @PostConstruct
    public void init() {
        canMoveForward = true;
        canMoveBackward = false;
        allPrisoners = new ArrayList<>();
        currentFrame = new ArrayList<>(PRISONERS_ON_PAGE);
        Prisoner prisoner = null;
        for (int i = 0; i < PRISONERS_ON_PAGE; i++) {
            prisoner = Prisoner.getNextPrisoner(db, prisoner);
            if (prisoner != null) {
                allPrisoners.add(prisoner);
                currentFrame.add(prisoner);
            } else {
                canMoveForward = false;
                return;
            }
            if(currentPage*PRISONERS_ON_PAGE >= allPrisoners.size() &&
                    Prisoner.getNextPrisoner(db, allPrisoners.get(allPrisoners.size() - 1)) == null)
                canMoveForward = false;
        }
    }

    public void addAkudama(){
        Akudama akudama = Akudama.getAkudamaById(db, akudamaId);
        if(akudama == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Акудама с таким ID не найден");
            return;
        }
        if(!Prisoner.AddPrisoner(db,akudama)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Отсутствует соединение");
            return;
        }
        addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Заключённый добавлен");
        currentPage = 1;
        init();
        akudamaId = 0;
    }

    public void moveForward() {
        if (!canMoveForward)
            return;
        if (allPrisoners.size() <= currentPage * PRISONERS_ON_PAGE) {
            Prisoner prisoner = allPrisoners.get(allPrisoners.size() - 1);
            List<Prisoner> newCurrentFrame = new ArrayList<>();
            int i = 0;
            for (; i < PRISONERS_ON_PAGE; i++) {
                prisoner = Prisoner.getNextPrisoner(db, prisoner);
                if (prisoner != null) {
                    allPrisoners.add(prisoner);
                    newCurrentFrame.add(prisoner);
                } else {
                    canMoveForward = false;
                    break;
                }
            }
            if (i > 0) {
                currentFrame = newCurrentFrame;
                currentPage++;
                canMoveBackward = true;
            }
        }
        else{
            currentFrame = new ArrayList<>();
            int border = Math.min(PRISONERS_ON_PAGE, allPrisoners.size() - currentPage * PRISONERS_ON_PAGE);
            int base = currentPage*PRISONERS_ON_PAGE;
            for (int i = 0; i < border; i++) {
                currentFrame.add(allPrisoners.get(i + base));
            }
            canMoveForward = border == PRISONERS_ON_PAGE;
            canMoveBackward = currentPage > 1;
            currentPage++;
        }
        if(currentPage*PRISONERS_ON_PAGE >= allPrisoners.size() &&
                Prisoner.getNextPrisoner(db, allPrisoners.get(allPrisoners.size() - 1)) == null)
            canMoveForward = false;
    }

    public void moveBackward() {
        if (!canMoveBackward || currentPage <= 1)
            return;
        currentPage--;
        canMoveForward = true;
        currentFrame = new ArrayList<>();
        for (int i = 0; i < PRISONERS_ON_PAGE; i++) {
            currentFrame.add(allPrisoners.get((currentPage - 1) * PRISONERS_ON_PAGE + i));
        }
        canMoveBackward = currentPage > 1;
    }
}
