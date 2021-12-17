package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Workout implements Serializable {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis();

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Program")
    private long program;

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    @Element(name = "Feedback")
    private long feedback;

    public Workout(long program, long feedback) {
        this.program = program;
        this.feedback = feedback;
    }

    public Workout(long id) {
        this.id = id;
    }

    public Workout() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgram() {
        return program;
    }

    public void setProgram(long program) {
        this.program = program;
    }

    public long getFeedback() {
        return feedback;
    }

    public void setFeedback(long feedback) {
        this.feedback = feedback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workout)) return false;
        Workout workout = (Workout) o;
        return getId() == workout.getId() && getProgram() == workout.getProgram() && getFeedback() == workout.getFeedback();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProgram(), getFeedback());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Workout{");
        sb.append("id=").append(id);
        sb.append(", program=").append(program);
        sb.append(", feedback=").append(feedback);
        sb.append('}');
        return sb.toString();
    }
}
