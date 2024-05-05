import java.util.ArrayList;

public class Queue { // useful for storing connected nodes

    ArrayList<String> queue = new ArrayList<String>();

    public void enqueue(String s) {
        queue.add(s);
    }

    public String dequeue() {
        String s = queue.get(0);
        queue.remove(0);
        return s;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
