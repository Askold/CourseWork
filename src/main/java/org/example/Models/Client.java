package org.example.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends User {
    @CsvBindByName
    @CsvBindByPosition(position = 3)
    @Element(name = "Age")
    private int age;

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    @Element(name = "Weight")
    private int weight;

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    @Element(name = "Height")
    private int height;

    public Client(String name, String surname, int age, int weight, int height) {
        super(name, surname);
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public Client() {

    }

    public Client(long id) {
        this.setId(id);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return getAge() == client.getAge() && getWeight() == client.getWeight() && getHeight() == client.getHeight();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAge(), getWeight(), getHeight());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Client{");
        sb.append("age=").append(age);
        sb.append(", weight=").append(weight);
        sb.append(", height=").append(height);
        sb.append('}');
        return sb.toString();
    }
}