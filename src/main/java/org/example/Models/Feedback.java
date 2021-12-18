package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.example.Constants;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Feedback {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis() + new Random().nextInt(10000);

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Date")
    private String date = LocalDate.now().toString();

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Comments")
    private String comment = Constants.SOME_COMMENTS;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Estimates")
    private Estimate estimate;

    public Feedback(Estimate estimate) {
        this.estimate = estimate;
    }

    public Feedback() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Estimate getEstimate() {
        return estimate;
    }

    public void setEstimate(Estimate estimate) {
        this.estimate = estimate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        Feedback feedback = (Feedback) o;
        return getId() == feedback.getId() && getDate().equals(feedback.getDate()) && Objects.equals(getComment(), feedback.getComment()) && getEstimate() == feedback.getEstimate();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getComment(), getEstimate());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Feedback{");
        sb.append("id=").append(id);
        sb.append(", date='").append(date).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", estimate=").append(estimate);
        sb.append('}');
        return sb.toString();
    }

    public enum Estimate{
        VERY_EASY,
        EASY,
        COMMON,
        NORMAL,
        HARD,
        VERY_HARD,
        IMPOSSIBLE
    }
}
