package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Feedback {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis();

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Date")
    private String date;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Comments")
    private List<String> comments;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Estimates")
    private List<Estimate> estimates;

    public Feedback(long id, String date, List<String> comments, List<Estimate> estimates) {
        this.id = id;
        this.date = date;
        this.comments = comments;
        this.estimates = estimates;
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

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<Estimate> getEstimates() {
        return estimates;
    }

    public void setEstimates(List<Estimate> estimates) {
        this.estimates = estimates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        Feedback feedback = (Feedback) o;
        return getId() == feedback.getId() && getDate().equals(feedback.getDate()) && Objects.equals(getComments(),
                feedback.getComments()) && Objects.equals(getEstimates(), feedback.getEstimates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getComments(), getEstimates());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Feedback{");
        sb.append("id=").append(id);
        sb.append(", date='").append(date).append('\'');
        sb.append(", comments=").append(comments == null ? "null" : Arrays.asList(comments).toString());
        sb.append(", estimates=").append(estimates == null ? "null" : Arrays.asList(estimates).toString());
        sb.append('}');
        return sb.toString();
    }

    enum Estimate{
        VERY_EASY,
        EASY,
        COMMON,
        NORMAL,
        HARD,
        VERY_HARD,
        IMPOSSIBLE
    }
}
