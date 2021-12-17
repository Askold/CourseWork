package org.example.Models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class EntityWrapper<T> {
    @ElementList(inline = true)
    private List<T> data;

    public EntityWrapper(List<T> data) {
        this.data = data;
    }

    public EntityWrapper() { }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EntityList{" +
                "data=" + data +
                '}';
    }
}
