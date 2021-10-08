import tester.Tester;
import java.util.function.*;

class Deque<T> {

  //Does not change
  Sentinel<T> header;

  Deque() {

    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {

    this.header = header;
  }

  //Returns the size of this Deque
  int size() {
    return this.header.sizeHelp(this.header.next);
  }

  //Effects: adds a new node to the head of the deque
  Deque<T> addAtHead(T data) {
    this.header.addNext(data);
    return this;
  }

  //Effects: adds a new node to the tail of the deque
  Deque<T> addAtTail(T data) {
    this.header.addPrev(data);
    return this;
  }

  //Effects: removes a node from the head of the deque
  T removeFromHead() {
    return this.header.next.removeFromHeadHelp(this.header);
  }

  //Effects: removes a node from the tail of the deque
  T removeFromTail() {
    return this.header.prev.removeFromTailHelp(this.header);
  }

  //Effects: removes the given node from the deque
  void removeNode(ANode<T> rm) {
    rm.remove();
  }

  //returns the first node to return true for the passed predicate
  ANode<T> find(Predicate<T> pred) {
    return this.header.next.find(pred);
  }

  //is this deque empty?
  boolean isEmpty() {
    return this.header.next.equals(this.header);
  }

}

abstract class ANode<T> {
  //refers to the next node in list
  ANode<T> next;

  //refers to the previous node in list
  ANode<T> prev;

  //Effects: links this node to the provided next and prev nodes, 
  // and in those nodes links back to this.
  void fixLinks(ANode<T> next, ANode<T> prev) {
    this.fixNext(next);
    this.fixPrev(prev);
  }

  //Effects: links the provided node as the previous to this node
  void fixPrev(ANode<T> prev) {
    this.prev = prev;
    prev.next = this;
  }

  //Effects: links the previous Node as the next to this node
  void fixNext(ANode<T> next) {
    this.next = next;
    next.prev = this;
  } 

  //adds a node next to this one;
  void addNext(T data) {
    this.next = new Node<T>(data, this.next, this);
  }

  //adds a node previous to this one
  void addPrev(T data) {
    this.prev = new Node<T>(data, this, this.prev);
  }

  //removes the node next to this one;
  abstract void remove(); 

  //Finds the first node that passes the Preidcate
  abstract ANode<T> find(Predicate<T> pred);

  //Helper for Remove from head
  abstract T removeFromHeadHelp(Sentinel<T> header);

  //Helper for remove from tail
  abstract T removeFromTailHelp(Sentinel<T> header);

}

class Sentinel<T> extends ANode<T> {

  //"next" node refers to first node in list

  //"prev" node refers to last node in list

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  //Checks if the Deque is composed of two Sentinels and then 
  // counts nodes
  int sizeHelp(ANode<T> cur) {
    if (this.equals(cur)) {
      return 0;
    } else {
      return 1 + sizeHelp(cur.next);
    }
  }

  //
  T removeFromHeadHelp(Sentinel<T> header) {

    //because this list is empty
    throw new RuntimeException();    
  }

  //
  T removeFromTailHelp(Sentinel<T> header) {

    //because this list is empty
    throw new RuntimeException();    
  }

  //
  ANode<T> find(Predicate<T> pred) {
    return this;
  }

  //Do not remove the Sentinel!!
  void remove() {
    //Senitnel<T> must inherit all methods from ANode<T>
  }

}

class Node<T> extends ANode<T> { 

  //the data contents of this node
  T data;

  Node(T data) {

    this.data = data;
    this.next = null;
    this.prev = null;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    this(data);
    if (prev == null || next == null) {
      throw new IllegalArgumentException();
    }
    this.fixLinks(next, prev);
  }

  T removeFromHeadHelp(Sentinel<T> header) {

    //removes me from the list
    this.next.fixPrev(header);
    return this.data;
  }

  T removeFromTailHelp(Sentinel<T> header) {

    //removes me from the list
    this.prev.fixNext(header);
    return this.data;
  }

  ANode<T> find(Predicate<T> pred) {
    if (pred.test(data)) {
      return this;
    } else {
      return this.next.find(pred);
    }
  }

  void remove() {
    this.next.fixPrev(this.prev);
  }

}

class ExamplesDeque {

  Deque<String> deque1; 
  Deque<String> deque2;
  Deque<String> deque3;
  Deque<Integer> intDeque;

  void init() {
    deque1 = new Deque<String>();
    deque2 = new Deque<String>();
    deque3 = new Deque<String>();
    intDeque = new Deque<Integer>();

    deque2
      .addAtHead("def")
      .addAtHead("cde")
      .addAtHead("bcd")
      .addAtHead("abc");
    deque3
      .addAtHead("lamb")
      .addAtHead("little")
      .addAtHead("a")
      .addAtHead("had")
      .addAtHead("Mary");

    intDeque
      .addAtTail(1)
      .addAtTail(2)
      .addAtTail(3)
      .addAtTail(4)
      .addAtTail(5);

  }

  void testSize(Tester t) {
    init();

    t.checkExpect(deque1.size(), 0);
    t.checkExpect(deque2.size(), 4);
    t.checkExpect(deque3.size(), 5);

  }

  void testFixLinks(Tester t) {
    init();
    
    Node<String> node1;
    Node<String> node2;
    Node<String> node3;
    node1 = new Node<String>("abc");
    node2 = new Node<String>("bcd");
    node3 = new Node<String>("cde");

    node2.fixLinks(node3, node1);

    t.checkExpect(node1.next, node2);
    t.checkExpect(node2.prev, node1);
    t.checkExpect(node2.next, node3);
    t.checkExpect(node3.prev, node2);
  }

  void testAddAtHead(Tester t) {
    init();

    t.checkExpect(deque1.addAtHead("0123").header.next, 
        new Node<String>("0123", deque1.header, deque1.header));
    t.checkExpect(deque2.addAtHead("zyx").header.next, 
        new Node<String>("zyx", deque2.header.next.next, deque2.header));
    t.checkExpect(deque3.addAtHead("when").header.next, 
        new Node<String>("when", deque3.header.next.next, deque3.header));
  }

  void testAddAtTail(Tester t) {
    init();

    t.checkExpect(deque1.addAtTail("let's gooo!").header.prev, 
        new Node<String>("let's gooo!", deque1.header, deque1.header));
    t.checkExpect(deque2.addAtTail("ghi").header.prev, 
        new Node<String>("ghi", deque2.header, deque2.header.prev.prev));
    t.checkExpect(deque3.addAtTail("whose").header.prev, 
        new Node<String>("whose", deque3.header, deque3.header.prev.prev));
  }

  void testRemoveFromHead(Tester t) {
    init();

    t.checkException(new RuntimeException(), deque1, "removeFromHead");
    t.checkExpect(deque2.removeFromHead(), "abc");
    t.checkExpect(deque3.removeFromHead(), "Mary");
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque3.size(), 4);
    t.checkExpect(deque2.header.next, new Node<String>("bcd", 
        deque2.header.next.next, deque2.header));
    t.checkExpect(deque3.header.next, new Node<String>("had", 
        deque3.header.next.next, deque3.header));
  }

  void testRemoveFromTail(Tester t) {
    init();

    t.checkException(new RuntimeException(), deque1, "removeFromTail");
    t.checkExpect(deque2.removeFromTail(), "def");
    t.checkExpect(deque3.removeFromTail(), "lamb");
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque3.size(), 4);
    t.checkExpect(deque2.header.prev, 
        new Node<String>("cde", deque2.header, deque2.header.prev.prev));
    t.checkExpect(deque3.header.prev, 
        new Node<String>("little", deque3.header, deque3.header.prev.prev));
  }

  void testFind(Tester t) {
    init();
    Predicate<String> pred1;
    Predicate<String> pred2;
    Predicate<String> pred3;

    pred1 = (s -> s.equals("Mary"));
    pred2 = (s -> s.equals("cde"));
    pred3 = (s -> s.length() == 6);

    t.checkExpect(deque1.find(pred1), deque1.header);
    t.checkExpect(deque2.find(pred1), deque2.header);
    t.checkExpect(deque3.find(pred1), deque3.header.next);

    t.checkExpect(deque1.find(pred2), deque1.header);
    t.checkExpect(deque2.find(pred2), deque2.header.prev.prev);
    t.checkExpect(deque3.find(pred2), deque3.header);

    t.checkExpect(deque1.find(pred3), deque1.header);
    t.checkExpect(deque2.find(pred3), deque2.header);
    t.checkExpect(deque3.find(pred3), deque3.header.prev.prev);

  }

  void testRemoveNode(Tester t) {
    init();
    deque1.removeNode(deque1.header);//tries to remove Sentinel
    deque2.removeNode(deque2.header.next.next);//removes "bcd"
    deque3.removeNode(deque3.header.prev);//removes "lamb"

    intDeque.removeNode(intDeque.header.next.next.next);//should remove '3'
    intDeque.removeNode(intDeque.header.prev.prev.prev);//should remove '2'

    t.checkExpect(deque1.header, deque1.header.next); //should not throw exception
    t.checkExpect(deque2.find(s -> s.equals("bcd")), deque2.header);
    t.checkExpect(deque3.find(s -> s.equals("lamb")), deque3.header);

    t.checkExpect(intDeque.find(n -> n == 2), intDeque.header);
    t.checkExpect(intDeque.find(n -> n == 3), intDeque.header);
    t.checkExpect(intDeque.find(n -> n == 1), intDeque.header.next);
  }
}
