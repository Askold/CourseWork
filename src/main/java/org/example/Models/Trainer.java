package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Trainer extends User {
    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Experience")
    private int workExperience;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Rating")
    private int rating;

    public Trainer(long id, String name, String surname) {
        super(id, name, surname);
    }

    public Trainer(String name, String surname) {
        super(name, surname);
    }

    public Trainer(String name, String surname, int workExperience, int rating) {
        super(name, surname);
        this.workExperience = workExperience;
        this.rating = rating;
    }



    public Trainer() {
    }

    public int getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(int workExperience) {
        this.workExperience = workExperience;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return getWorkExperience() == trainer.getWorkExperience() && getRating() == trainer.getRating();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWorkExperience(), getRating());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Trainer{");
        sb.append("workExperience=").append(workExperience);
        sb.append(", rating=").append(rating);
        sb.append('}');
        return sb.toString();
    }
}
