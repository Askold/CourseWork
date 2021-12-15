package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Program implements Serializable {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis();

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Workouts")
    private List<Long> workouts;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Duration in weeks")
    private int durationInWeeks;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Workouts per week")
    private int workoutsPerWeek;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Description")
    private String description;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Type")
    private ProgramType type;

    public Program(int durationInWeeks, int workoutsPerWeek, String description,
                   ProgramType type) {
        this.durationInWeeks = durationInWeeks;
        this.workoutsPerWeek = workoutsPerWeek;
        this.description = description;
        this.type = type;
    }

    public Program() {
        this.durationInWeeks = 1;
        this.workoutsPerWeek = 3;
        this.description = "some description";
        this.type = ProgramType.AEROBIC;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Long> workouts) {
        this.workouts = workouts;
    }

    public int getDurationInWeeks() {
        return durationInWeeks;
    }

    public void setDurationInWeeks(int durationInWeeks) {
        this.durationInWeeks = durationInWeeks;
    }

    public int getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    public void setWorkoutsPerWeek(int workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Program)) return false;
        Program program = (Program) o;
        return getId() == program.getId() && getDurationInWeeks() == program.getDurationInWeeks() &&
                getWorkoutsPerWeek() == program.getWorkoutsPerWeek() && Objects.equals(getWorkouts(),
                program.getWorkouts()) && Objects.equals(getDescription(), program.getDescription()) &&
                getType() == program.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWorkouts(), getDurationInWeeks(), getWorkoutsPerWeek(),
                getDescription(), getType());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Program{");
        sb.append("id=").append(id);
        sb.append(", workouts=").append(workouts);
        sb.append(", durationInWeeks=").append(durationInWeeks);
        sb.append(", workoutsPerWeek=").append(workoutsPerWeek);
        sb.append(", description='").append(description).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }

    public enum ProgramType{
        AEROBIC,
        STRENGTH,
        FLEXIBILITY,
        BALANCE
    }
}
