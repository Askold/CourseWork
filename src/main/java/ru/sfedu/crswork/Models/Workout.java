package ru.sfedu.crswork.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public class Workout implements Serializable {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis() + new Random().nextInt(10000);

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Feedback")
    private long feedback;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Type")
    private WorkoutType type;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Client")
    private long client;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Trainer")
    private long trainer;

    public Workout(long id) {
        this.id = id;
    }

    public Workout(long id, long feedback, WorkoutType type, long client, long trainer) {
        this.id = id;
        this.feedback = feedback;
        this.type = type;
        this.client = client;
        this.trainer = trainer;
    }

    public Workout() {
    }

    public Workout(  WorkoutType type, long client, long trainer) {
        this.type = type;
        this.client = client;
        this.trainer = trainer;
    }

    public Workout(long id, WorkoutType type, long client, long trainer) {
        this.id = id;
        this.type = type;
        this.client = client;
        this.trainer = trainer;
    }

    public Workout(long id, WorkoutType type, long client, long trainer, long feedback) {
        this.id = id;
        this.feedback = feedback;
        this.type = type;
        this.client = client;
        this.trainer = trainer;
    }

    public Workout(WorkoutType type, long client, long trainer, long feedback) {
        this.feedback = feedback;
        this.type = type;
        this.client = client;
        this.trainer = trainer;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getFeedback() {
        return feedback;
    }

    public void setFeedback(long feedback) {
        this.feedback = feedback;
    }

    public WorkoutType getType() {
        return type;
    }

    public void setType(WorkoutType type) {
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
        if (!(o instanceof Workout)) return false;
        Workout workout = (Workout) o;
        return getId() == workout.getId() && getFeedback() == workout.getFeedback() && getClient() == workout.getClient() && getTrainer() == workout.getTrainer() && getType() == workout.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFeedback(), getType(), getClient(), getTrainer());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Workout{");
        sb.append("id=").append(id);
        sb.append(", feedback=").append(feedback);
        sb.append(", type=").append(type);
        sb.append(", client=").append(client);
        sb.append(", trainer=").append(trainer);
        sb.append('}');
        return sb.toString();
    }

    public enum WorkoutType{
        AEROBIC,
        STRENGTH,
        FLEXIBILITY,
        BALANCE
    }
}
