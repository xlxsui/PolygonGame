package entity;

public class Edge {
    public int key;
    public String operation;

    public Edge(int key, String operation) {
        this.key = key;
        this.operation = operation;
    }

    public Edge(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return operation;
    }
}
