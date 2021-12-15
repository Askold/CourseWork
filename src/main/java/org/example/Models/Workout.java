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
    @Element(name = "Exercises")
    private List<Long> exercises;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Weights")
    private List<Integer> weights;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Rounds")
    private List<Integer> rounds;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Repetitions")
    private List<Integer> repetitions;

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    @Element(name = "Feedback")
    private List<Long> feedbacks;

    public Workout(List<Integer> weights, List<Integer> rounds,
                   List<Integer> repetitions) {
        this.weights = weights;
        this.rounds = rounds;
        this.repetitions = repetitions;
    }

    public Workout(long id, List<Long> exercises, List<Integer> weights, List<Integer> rounds,
                   List<Integer> repetitions) {
        this.id = id;
        this.exercises = exercises;
        this.weights = weights;
        this.rounds = rounds;
        this.repetitions = repetitions;
    }

    public Workout() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getExercises() {
        return exercises;
    }

    public void setExercises(List<Long> exercises) {
        this.exercises = exercises;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public void setWeights(List<Integer> weights) {
        this.weights = weights;
    }

    public List<Integer> getRounds() {
        return rounds;
    }

    public void setRounds(List<Integer> rounds) {
        this.rounds = rounds;
    }

    public List<Integer> getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(List<Integer> repetitions) {
        this.repetitions = repetitions;
    }

    public List<Long> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Long> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workout)) return false;
        Workout workout = (Workout) o;
        return getId() == workout.getId() && Objects.equals(getExercises(), workout.getExercises()) && Objects.equals(getWeights(), workout.getWeights()) && Objects.equals(getRounds(), workout.getRounds()) && Objects.equals(getRepetitions(), workout.getRepetitions()) && Objects.equals(getFeedbacks(), workout.getFeedbacks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getExercises(), getWeights(), getRounds(), getRepetitions(), getFeedbacks());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Workout{");
        sb.append("id=").append(id);
        sb.append(", exercises=").append(exercises);
        sb.append(", weights=").append(weights);
        sb.append(", rounds=").append(rounds);
        sb.append(", repetitions=").append(repetitions);
        sb.append(", feedbacks=").append(feedbacks);
        sb.append('}');
        return sb.toString();
    }
}
