package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.example.Constants;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public class Exercise implements Serializable {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis() + new Random().nextInt(10000);;

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Name")
    private String name;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Description")
    private String description = Constants.SOME_DESCRIPTION;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Weight")
    private int weight;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Repetitions")
    private int repetitions;

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    @Element(name = "Rounds")
    private int rounds;

    @CsvBindByName
    @CsvBindByPosition(position = 6)
    @Element(name = "Workout")
    private long workout;

    public Exercise(String name, String description, int weight, int repetitions, int rounds) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.repetitions = repetitions;
        this.rounds = rounds;
    }

    public Exercise(String name, int weight, int repetitions, int rounds, long workout) {
        this.name = name;
        this.weight = weight;
        this.repetitions = repetitions;
        this.rounds = rounds;
        this.workout = workout;
    }

    public Exercise() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public long getWorkout() {
        return workout;
    }

    public void setWorkout(long workout) {
        this.workout = workout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return getId() == exercise.getId() && getWeight() == exercise.getWeight() && getRepetitions() == exercise.getRepetitions() && getRounds() == exercise.getRounds() && getWorkout() == exercise.getWorkout() && getName().equals(exercise.getName()) && Objects.equals(getDescription(), exercise.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getWeight(), getRepetitions(), getRounds(), getWorkout());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Exercise{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", weight=").append(weight);
        sb.append(", repetitions=").append(repetitions);
        sb.append(", rounds=").append(rounds);
        sb.append(", workout=").append(workout);
        sb.append('}');
        return sb.toString();
    }
}
