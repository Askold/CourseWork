package org.example.Models;

import org.example.Constants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HistoryContent implements Serializable {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private long id = System.currentTimeMillis();
    private String className;
    private String createdDate = dtf.format(LocalDateTime.now());
    private String actor = Constants.DEFAULT_ACTOR;
    private String methodName;
    private String bean;
    private Status status = Status.SUCCESS;

    public HistoryContent(String className, String actor, String methodName, Status status) {
        this.className = className;
        this.actor = actor;
        setMethodName(methodName);
        setStatus(status);
    }

    public HistoryContent(String className, String methodName) {
        this.className = className;
        setMethodName(methodName);
    }

    public HistoryContent(String className) {
        this.className = className;
    }

    public HistoryContent() {
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HistoryContent{");
        sb.append("id=").append(id);
        sb.append(", className='").append(className).append('\'');
        sb.append(", createdDate='").append(createdDate).append('\'');
        sb.append(", actor='").append(actor).append('\'');
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", bean='").append(bean).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryContent)) return false;
        HistoryContent that = (HistoryContent) o;
        return getId() == that.getId() && getClassName().equals(that.getClassName()) && getCreatedDate().equals(that.getCreatedDate()) && getActor().equals(that.getActor()) && getMethodName().equals(that.getMethodName()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getClassName(), getCreatedDate(), getActor(), getMethodName(), getStatus());
    }

    public enum Status{
        SUCCESS,
        FAULT
    }

}

