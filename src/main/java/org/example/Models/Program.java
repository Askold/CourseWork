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
    @Element(name = "Duration in weeks")
    private int durationInWeeks;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Description")
    private String description;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Type")
    private ProgramType type;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Client")
    private long client;

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    @Element(name = "Trainer")
    private long trainer;

    public Program(int durationInWeeks, String description, ProgramType type) {
        this.durationInWeeks = durationInWeeks;
        this.description = description;
        this.type = type;
    }



    public Program() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDurationInWeeks() {
        return durationInWeeks;
    }

    public void setDurationInWeeks(int durationInWeeks) {
        this.durationInWeeks = durationInWeeks;
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

    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }

    public long getTrainer() {
        return trainer;
    }

    public void setTrainer(long trainer) {
        this.trainer = trainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Program)) return false;
        Program program = (Program) o;
        return getId() == program.getId() && getDurationInWeeks() == program.getDurationInWeeks() && getClient() == program.getClient() && getTrainer() == program.getTrainer() && Objects.equals(getDescription(), program.getDescription()) && getType() == program.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDurationInWeeks(), getDescription(), getType(), getClient(), getTrainer());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Program{");
        sb.append("id=").append(id);
        sb.append(", durationInWeeks=").append(durationInWeeks);
        sb.append(", description='").append(description).append('\'');
        sb.append(", type=").append(type);
        sb.append(", client=").append(client);
        sb.append(", trainer=").append(trainer);
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
