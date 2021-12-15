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
    @Element(name = "Clients")
    private List<Client> clients;

    public Trainer(long id, String name, String surname) {
        super(id, name, surname);
    }

    public Trainer(String name, String surname, List<Client> clients) {
        super(name, surname);
        this.clients = clients;
    }

    public Trainer(String name, String surname) {
        super(name, surname);
    }

    public Trainer() {
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(getClients(), trainer.getClients());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClients());
    }

    @Override
    public String toString() {
        return "Trainer{" + super.toString()
                +"clients=" + clients +
                "} " ;
    }

}
