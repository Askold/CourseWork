package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.Serializable;
import java.util.Objects;

public class Exercise implements Serializable {

    @CsvBindByName
    @CsvBindByPosition(position = 0)
    @Attribute
    private long id = System.currentTimeMillis();

    @CsvBindByName
    @CsvBindByPosition(position = 1)
    @Element(name = "Name")
    private String name;

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    @Element(name = "Description")
    private String description;

    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Video")
    private String video;

    public Exercise(long id, String name, String description, String video) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.video = video;
    }

    public Exercise(long id) {
        this.id = id;
    }

    public Exercise(String name, String description, String video) {
        this.name = name;
        this.description = description;
        this.video = video;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return getId() == exercise.getId() && Objects.equals(getName(), exercise.getName()) && Objects.equals(getDescription(), exercise.getDescription()) && Objects.equals(getVideo(), exercise.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getVideo());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Exercise{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", video='").append(video).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
