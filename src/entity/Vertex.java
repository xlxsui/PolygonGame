package entity;

import java.io.Serializable;

public class Vertex implements Serializable {
    public int key;
    public int value;

    public Vertex(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
