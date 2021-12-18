package ru.sfedu.crswork.Models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Element;

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

    @CsvBindByName
    @CsvBindByPosition(position = 6)
    @Element(name = "Awaiting")
    private boolean awaiting = true;

    public Client(String name, String surname, int age, int weight, int height) {
        super(name, surname);
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public Client(long id, String name, String surname, int age, int weight, int height, boolean awaiting) {
        super(id, name, surname);
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.awaiting = awaiting;
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

    public boolean isAwaiting() {
        return awaiting;
    }

    public void setAwaiting(boolean awaiting) {
        this.awaiting = awaiting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return getAge() == client.getAge() && getWeight() == client.getWeight()
                && getHeight() == client.getHeight() && isAwaiting() == client.isAwaiting();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAge(), getWeight(), getHeight(), isAwaiting());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Client{");
        sb.append("age=").append(age);
        sb.append(", weight=").append(weight);
        sb.append(", height=").append(height);
        sb.append(", awaiting=").append(awaiting);
        sb.append('}');
        return sb.toString();
    }
}
