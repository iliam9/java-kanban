package structures;

public class Node<E> {
    Node<E> next;
    E data;
    Node<E> prev;

    Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
