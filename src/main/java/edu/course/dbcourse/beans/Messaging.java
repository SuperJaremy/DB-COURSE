package edu.course.dbcourse.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public interface Messaging {
    default void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
