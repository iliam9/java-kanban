package additionalStructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomLinkedList<T> {

    private Node<T> head;

    private Node<T> tail;

    private int size = 0;

    public Node<T> linkLast(T element) {

        final Node<T> oldTail = tail;
        final Node<T> newNode;

        newNode = new Node<>(oldTail, element, null);

        if (size == 0) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        tail = newNode;
        size++;

        return newNode;
    }

    public void delete(Node<T> element) {

        if (Objects.isNull(element))
            return;

        if (size == 1) {
            head = null;
            tail = null;
        }

        Node<T> prevNode = element.prev;
        Node<T> nextNode = element.next;

        if (Objects.nonNull(prevNode)) {
            prevNode.next = nextNode;

        } else {
            head = nextNode;
        }

        if (Objects.nonNull(nextNode)) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        size--;

    }

    public List<T> getList() {
        List<T> list = new ArrayList<>();

        Node<T> iterator = head;
        while (Objects.nonNull(iterator)) {
            list.add(iterator.data);
            iterator = iterator.next;
        }

        return list;
    }

    public int size() {
        return this.size;
    }

}

