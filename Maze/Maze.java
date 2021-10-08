// *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
//                              (~˘▾˘)~    How to Play    ~(˘▾˘~)                   
// *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
// when bigBang() is run, the maze is produced with the player (red dot) in the top-leftmost Cell
// this is considered the starting point of the maze, destination is the bottom-rightmost Cell.
//
//    - To move the player: ---------------------------------------------------- use arrow keys
//    - To run a depth-first search: ------------------------------------------- press the "D" key
//    - To run a breadth-first search:------------------------------------------ press the "B" key
//    - To reset the board: ---------------------------------------------------- press the "R" key
//    - Hexagonalizifier ٩(*❛⊰❛)～❤: -------------------------------------------- press the "H" key
//
// when the destination is reached, either by the player or by the search algorithm, the solution
// to the maze is displayed. Note that the player is disable after either search is run, or the 
// game is won. The game can be reset at any time.


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import tester.*;
import javalib.impworld.*;
import javalib.worldcanvas.WorldCanvas;

import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;


//////////////////////////////////////////////////////////////////////////////////////////////////

//Is this edge weighted less than the other? (for use in mergesort)
class LessWeight implements Comparator<Edge> {
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }

}

class ArrayUtils {

  //In ArrayUtils
  //EFFECT: Sorts the provided list according to the given comparator
  <T> void mergesort(ArrayList<T> arr, Comparator<T> comp) {
    // Create a temporary array
    ArrayList<T> temp = new ArrayList<T>();
    // Make sure the temporary array is exactly as big as the given array
    for (int i = 0; i < arr.size(); i = i + 1) {
      temp.add(arr.get(i));
    }
    mergesortHelp(arr, temp, comp, 0, arr.size());
  }

  //EFFECT: Sorts the provided list in the region [loIdx, hiIdx) according to the given comparator
  //        Modifies both lists in the range [loIdx, hiIdx)
  <T> void mergesortHelp(ArrayList<T> source, ArrayList<T> temp, Comparator<T> comp,
      int loIdx, int hiIdx) {
    if (hiIdx - loIdx <= 1) {
      return; // nothing to sort
    }
    // Step 1: find the middle index
    int midIdx = (loIdx + hiIdx) / 2;
    // Step 2: recursively sort both halves
    mergesortHelp(source, temp, comp, loIdx, midIdx);
    mergesortHelp(source, temp, comp, midIdx, hiIdx);
    // Step 3: merge the two sorted halves
    merge(source, temp, comp, loIdx, midIdx, hiIdx);
  }

  //Merges the two sorted regions [loIdx, midIdx) and [midIdx, hiIdx) from source
  //into a single sorted region according to the given comparator
  //EFFECT: modifies the region [loIdx, hiIdx) in both source and temp
  <T> void merge(ArrayList<T> source, ArrayList<T> temp, Comparator<T> comp,
      int loIdx, int midIdx, int hiIdx) {
    int curLo = loIdx;   // where to start looking in the lower half-list
    int curHi = midIdx;  // where to start looking in the upper half-list
    int curCopy = loIdx; // where to start copying into the temp storage
    while (curLo < midIdx && curHi < hiIdx) {
      if (comp.compare(source.get(curLo), source.get(curHi)) <= 0) {
        // the value at curLo is smaller, so it comes first
        temp.set(curCopy, source.get(curLo));
        curLo = curLo + 1; // advance the lower index
      }
      else {
        // the value at curHi is smaller, so it comes first
        temp.set(curCopy, source.get(curHi));
        curHi = curHi + 1; // advance the upper index
      }
      curCopy = curCopy + 1; // advance the copying index
    }
    // copy everything that's left -- at most one of the two half-lists still has items in it
    while (curLo < midIdx) {
      temp.set(curCopy, source.get(curLo));
      curLo = curLo + 1;
      curCopy = curCopy + 1;
    }
    while (curHi < hiIdx) {
      temp.set(curCopy, source.get(curHi));
      curHi = curHi + 1;
      curCopy = curCopy + 1;
    }
    // copy everything back from temp into source
    for (int i = loIdx; i < hiIdx; i = i + 1) {
      source.set(i, temp.get(i));
    }
  }



}

interface ICollection<T> {
  //adds to the worklist
  abstract void add(T obj);

  //removes from the worklist and returns the data
  T remove();

  public boolean isEmpty();
}

class Stack<T> implements ICollection<T> {
  Deque<T> worklist;

  Stack() {
    worklist = new Deque<T>();
  }

  public void add(T data) {
    this.worklist.addAtHead(data);
  }

  public T remove() {
    return this.worklist.removeFromHead();
  }

  public boolean isEmpty() {
    return this.worklist.isEmpty();
  }
}

class Queue<T> implements ICollection<T> {

  Deque<T> worklist;

  Queue() {
    worklist = new Deque<T>();
  }

  public void add(T data) {
    this.worklist.addAtTail(data);
  }

  public T remove() {
    return this.worklist.removeFromHead();
  }

  public boolean isEmpty() {
    return this.worklist.isEmpty();
  }
}


//////////////////////////////////////////////////////////////////////////////////////////////////


//To represent each node of the graph

class Vertex {
  //Representing position in the ArrayList
  int x;
  int y;

  //Representing the ways one could move from this Vertex
  ArrayList<Edge> outEdges;

  //Initial constructor, outEdges initially is empty
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;

    this.outEdges = new ArrayList<Edge>();
  }

  //Initial constructor, outEdges initially is empty
  Vertex(int x, int y, String name) {
    this.x = x;
    this.y = y;

    this.outEdges = new ArrayList<Edge>();
  }



}

//represents the undirected graph edges that connect our nodes
//Edge weight randomly generated simply using nextInt
class Edge {
  Vertex v1;
  Vertex v2;
  int weight;

  Edge(Vertex v1, Vertex v2, int weight) {
    this.v1 = v1;
    this.v2 = v2;
    this.weight = weight;
  }

  //does this edge connect the same two nodes as that?
  public boolean sameLocation(Edge that) {
    return ((that.v1 == this.v2 && that.v2 == this.v1) 
        || (that.v1 == this.v1 && that.v2 == this.v2));
  }

  //gives the vertex on the opposite end of this edge
  public Vertex opposite(Vertex v) {
    if (v.equals(v1)) {
      return v2;
    }
    if (v.equals(v2)) {
      return v1;
    }
    throw new IllegalArgumentException("Edge does not connect the specified vertex");
  }

  @Override
  //direction and weight does not matter when checking equality of Edges
  public boolean equals(Object obj) {
    if (obj instanceof Edge) {
      Edge that = (Edge) obj;
      return this.sameLocation(that)
          && this.weight == that.weight;
    }

    return false;
  }

  @Override
  public int hashCode() {
    int hash = 33;
    hash = hash * this.weight + (this.v1 != null ? v1.hashCode() : 0) 
        + (this.v2 != null ? v2.hashCode() : 0);
    return hash;
  }
}

//the union/find data structure, specified to work on Graph
class UnionFind {
  HashMap<Integer , Integer> representatives;
  ArrayList<Edge> edgesInTree;
  ArrayList<Edge> worklist;

  UnionFind(Graph g) {
    this.edgesInTree = new ArrayList<Edge>();
    this.worklist = new ArrayList<Edge>();

    new ArrayUtils().mergesort(g.edges, new LessWeight());
    this.worklist.addAll(g.edges);

    this.representatives = new HashMap<Integer, Integer>(g.nodes.size());

    for (Vertex v: g.nodes) {
      //initially all nodes are mapped to themselves
      representatives.put(v.hashCode(), v.hashCode());
    }
  }

  //applys unionFind to the graph - Produces list of minimum spanning edges
  public ArrayList<Edge> apply() {

    while (nSelfRef() > 1) {
      Edge nextEdge = worklist.remove(0);//the next smallest edge
      if (this.find(nextEdge.v1.hashCode())
          == this.find(nextEdge.v2.hashCode())) { 
        //don't include this edge in the tree, because it would create a loop.
      } else {
        union(nextEdge);
      }
    }
    return this.edgesInTree;
  }

  //EFFECTS: merges the two nodes of this edge into the same tree
  void union(Edge edge) {
    this.edgesInTree.add(edge);
    representatives.replace(find(edge.v1.hashCode()), this.find(edge.v2.hashCode()));
  }

  //finds the representative for the grouping of this node
  int find(int nodecode) {
    if (selfRef(nodecode)) {
      return nodecode;
    } else {
      return find(representatives.get(nodecode));
    }
  }

  //Is this node's representative itself?
  boolean selfRef(int nodecode) {
    return representatives.get(nodecode) == nodecode;
  }

  //gets the number of nodes that are currently mapped to themselves
  //should start as n and end at 1
  int nSelfRef() {
    int count = 0;
    for (int key: this.representatives.keySet()) {
      if (selfRef(key)) { 
        count++; 
      }
    }
    return count;
  }
}

//iterates through a graph path from a specified vertex to another.
class GraphSearch implements Iterator<Vertex> {
  Graph g;
  Vertex from;
  Vertex to;
  ArrayList<Vertex> alreadySeen;
  HashMap<Vertex, Vertex> cameFrom;
  ICollection<Vertex> worklist;
  boolean complete;

  GraphSearch(Graph g, Vertex from, Vertex to, ICollection<Vertex> worklist) {
    this.g = g;
    if (!this.g.nodes.contains(from) || !this.g.nodes.contains(to)) {
      throw new IllegalArgumentException("Cannot get path -- Nodes not in graph");
    }
    this.from = from;
    this.to = to;
    this.alreadySeen = new ArrayList<Vertex>();
    this.worklist = worklist;
    this.cameFrom = new HashMap<Vertex, Vertex>();

    // Initialize the worklist with the from vertex
    this.worklist.add(this.from);
    this.complete = false;
  }

  //can this search find more vertices?
  public boolean hasNext() {
    return !complete && !worklist.isEmpty();
  }

  //next iteration through the search
  public Vertex next() {
    if (this.hasNext()) {
      Vertex next = worklist.remove();
      if (next.equals(to)) {
        this.complete = true;
        return next; // Success!
      }
      else if (this.alreadySeen.contains(next)) {
        // we've already seen this one, get the next
        return this.next();
      }
      else {
        // add all the neighbors of next to the worklist for further processing
        for (Edge e : next.outEdges) {
          Vertex neighbor = e.opposite(next);
          this.worklist.add(neighbor);
          this.cameFrom.putIfAbsent(neighbor, next);
        }
        // add next to alreadySeen, since we're done with it
        alreadySeen.add(next);
        return next;
      }
    } else {
      throw new RuntimeException("Cannot get next element of path -- No such path");
    }
  }

  //reconstructs the shortest path found by this search
  Iterator<Vertex> reconstruct() {
    if (!complete) {
      throw new RuntimeException("Cannot reconstruct; path is not complete.");
    }
    ArrayList<Vertex> path = new ArrayList<Vertex>();
    Vertex next = to;
    path.add(next);
    while (!next.equals(from)) {
      next = this.cameFrom.get(next);
      path.add(next);
    }
    return path.iterator();
  }
}

//To represent the graph of Cells
class Graph implements Iterable<Vertex> {
  //The list of nodes
  ArrayList<Vertex> nodes;
  ArrayList<Edge> edges;

  Graph() {
    this.nodes = new ArrayList<Vertex>();
    this.edges = new ArrayList<Edge>();
  }

  //for testing
  Graph(ArrayList<Vertex> nodes, ArrayList<Edge> edges) {
    this.nodes = nodes;
    this.edges = edges;
  }

  //implements Kruskal's algorithm for constructing minimum spanning trees
  //EFFECTS:converts this graph into a minimum spanning tree
  void minimumSpanning() {
    this.edges = new UnionFind(this).apply();
    for (Edge e: this.edges) {
      e.v1.outEdges.add(e);
      e.v2.outEdges.add(e);
    }
  }

  //sorts the list of edges (using mergesort)
  void sortEdges() {
    new ArrayUtils().mergesort(edges, new LessWeight());
  }

  //gives depth-first Iterator
  GraphSearch dfsIter(Vertex from, Vertex to) {
    return new GraphSearch(this, from, to , new Stack<Vertex>());
  }

  //gives breadth-first Iterator
  GraphSearch bfsIter(Vertex from, Vertex to) {
    return new GraphSearch(this, from, to, new Queue<Vertex>());
  }

  //defaults breadth first, in case we wanted to use foreach on Graph
  public Iterator<Vertex> iterator() {
    return bfsIter(nodes.get(0), this.nodes.get(this.nodes.size() - 1));
  }
}

class BoardGraph extends Graph {

  //The size of the board graph, and thus the size of the maze
  int boardSizeX;
  int boardSizeY;

  BoardGraph(int boardSizeX, int boardSizeY) {
    super();
    this.boardSizeX = boardSizeX;
    this.boardSizeY = boardSizeY;
    initVertices();
    initEdges();
  }

  //Fills the graph with Vertices  
  void initVertices() {
    this.nodes = new ArrayList<Vertex>();
    for (int i = 0; i < this.boardSizeX; i++) {
      for (int j = 0; j < this.boardSizeY; j++) {
        this.nodes.add(new Vertex(i, j));
      }
    }
  }

  //links all Vertices by Edges
  void initEdges() {
    this.edges = new ArrayList<Edge>();
    for (Vertex v: this.nodes) {
      if (v.x + 1 < boardSizeX) {
        Edge horiz = new Edge(v, this.nodeAt(v.x + 1, v.y), 1);
        this.edges.add(horiz);
      }
      if (v.y + 1 < boardSizeY) {
        Edge vert = new Edge(v, this.nodeAt(v.x, v.y + 1), 1);
        this.edges.add(vert);
      }
    }
  }


  //gets the node at position x and y
  Vertex nodeAt(int x, int y) {
    if (x * boardSizeY + y < this.nodes.size() && x * boardSizeY + y >= 0) {
      return this.nodes.get(x * boardSizeY + y);
    } else {
      //returns a node outside of board -- this fits with the implementation
      return new Vertex(-1, -1);
    }
  }

  //gets a depth first graph search iterator that goes from first node to last
  GraphSearch dfsIter() {
    return super.dfsIter(this.nodes.get(0), this.nodes.get(this.nodes.size() - 1));
  }

  //gets a breadth first graph search iterator that goes from first node to last
  GraphSearch bfsIter() {
    return super.bfsIter(this.nodes.get(0), this.nodes.get(this.nodes.size() - 1));
  }
}

//hexagonal version of our BoardGraph
class HexGraph extends BoardGraph {

  HexGraph(int boardSizeX, int boardSizeY) {
    super(boardSizeX, boardSizeY);
  }
  
  //links all Vertices by Edges
  void initEdges() {
    this.edges = new ArrayList<Edge>();
    for (Vertex v: this.nodes) {
      if (v.x + 1 < boardSizeX) {
        Edge horiz = new Edge(v, this.nodeAt(v.x + 1, v.y), 1);
        this.edges.add(horiz);
      }
      if (v.y + 1 < boardSizeY) {
        Edge vert = new Edge(v, this.nodeAt(v.x, v.y + 1), 1);
        this.edges.add(vert);
        
        //connects to a second node below if hexagonal
        if (v.x + 1 < boardSizeX) {
          Edge vert2 = new Edge(v, this.nodeAt(v.x + 1, v.y + 1), 1);
          this.edges.add(vert2);
        }
      }
    }
  }
  
}

//Starts upper left, stops lower right
//Maze class handles graphical methods and gameplay methods
class Maze extends World {

  //A constant representing the size of the cell.
  public static final int CELL_SIZE = 20;

  //The size of the game, and thus the size of the maze
  int boardSizeX;
  int boardSizeY;

  Random rand;

  //underlying graph to compute maze path
  BoardGraph mazeGraph;

  //iterators for the searching and solving algorithms
  GraphSearch search;
  Iterator<Vertex> solve;


  //the visual representation of the board of maze cells
  ArrayList<ArrayList<Cell>> mazeBoard;

  //the position of the player 
  Posn player;

  boolean searchPhase;
  boolean solvePhase;
  boolean playerWin;
  boolean hex;

  Maze(BoardGraph graph) {
    this.rand = new Random();
    this.mazeGraph = graph;
    this.randomizeEdgeWeights();
    this.mazeGraph.minimumSpanning();
    this.boardSizeX = mazeGraph.boardSizeX;
    this.boardSizeY = mazeGraph.boardSizeY;

    this.mazeBoard = new ArrayList<ArrayList<Cell>>();
    this.buildBoard();

    this.player = new Posn(0, 0);
    this.searchPhase = false;
    this.solvePhase = false;
    this.playerWin = false;
  }

  Maze(int boardSizeX, int boardSizeY, Random rand) {
    this.boardSizeX = boardSizeX;
    this.boardSizeY = boardSizeY;
    this.reset();
  }

  //resets the maze to starting conditions
  void reset() {
    this.rand = new Random();
    this.mazeBoard = new ArrayList<ArrayList<Cell>>();
    this.mazeGraph = new BoardGraph(this.boardSizeX, this.boardSizeY);
    this.randomizeEdgeWeights();
    this.mazeGraph.minimumSpanning();
    this.buildBoard();
    this.player = new Posn(0, 0);
    this.searchPhase = false;
    this.solvePhase = false;
    this.playerWin = false;
    this.hex = false;
  }

  //builds a board of Cells out of the vertices of the graph
  void buildBoard() {
    for (int i = 0; i < boardSizeX; i++) {
      mazeBoard.add(new ArrayList<Cell>());
      for (int j = 0; j < boardSizeY; j++) {
        Vertex v = mazeGraph.nodeAt(i, j);
        boolean edgetop = false;
        boolean edgeright = false;
        boolean edgebot = false;
        boolean edgeleft = false;
        for (Edge e: v.outEdges) {
          edgetop |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x, v.y - 1), 1));
          edgeright |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x + 1, v.y), 1));
          edgebot |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x, v.y + 1), 1));
          edgeleft |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x - 1, v.y), 1));
        }
        mazeBoard.get(i).add(new Cell(!edgetop, !edgeright, !edgebot, !edgeleft));
      }
    }
  }
  
  //converts the graph and the maze board into a hexagonal maze.
  void hexify() {
    this.rand = new Random();
    this.mazeBoard = new ArrayList<ArrayList<Cell>>();
    this.mazeGraph = new HexGraph(this.boardSizeX, this.boardSizeY);
    this.randomizeEdgeWeights();
    this.mazeGraph.minimumSpanning();
    
    for (int i = 0; i < boardSizeX; i++) {
      mazeBoard.add(new ArrayList<Cell>());
      for (int j = 0; j < boardSizeY; j++) {
        Vertex v = mazeGraph.nodeAt(i, j);
        boolean edgetop = false;
        boolean edgetopright = false;
        boolean edgeright = false;
        boolean edgebot = false;
        boolean edgebotleft = false;
        boolean edgeleft = false;
        for (Edge e: v.outEdges) {
          edgetop |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x - 1, v.y - 1), 1));
          edgetopright |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x, v.y - 1), 1));
          edgeright |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x + 1, v.y), 1));
          edgebot |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x + 1, v.y + 1), 1));
          edgebotleft |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x, v.y + 1), 1));
          edgeleft |= e.sameLocation(new Edge(v, mazeGraph.nodeAt(v.x - 1, v.y), 1));
        }
        mazeBoard.get(i).add(
            new HexCell(!edgetop, !edgetopright, !edgeright, !edgebot, !edgebotleft, !edgeleft));
      }
    }
    
    this.player = new Posn(-1, -1);
    this.searchPhase = false;
    this.solvePhase = false;
    this.playerWin = false;
    this.hex = true;
  }

  //draws the board and produces a worldScene
  WorldScene drawBoard() {
    WorldScene s;
    if (!hex) {
      s = new WorldScene(boardSizeX * CELL_SIZE, boardSizeY * CELL_SIZE);
      for (int i = 0; i < boardSizeX; i++) {
        for (int j = 0; j < boardSizeY; j++) {
          s.placeImageXY(mazeBoard.get(i).get(j).drawCell(), i * CELL_SIZE + CELL_SIZE / 2, 
              j * CELL_SIZE + CELL_SIZE / 2);
        }
      }
      s.placeImageXY(new CircleImage(CELL_SIZE / 2, OutlineMode.SOLID, Color.RED),
          player.x * CELL_SIZE + CELL_SIZE / 2, player.y * CELL_SIZE + CELL_SIZE / 2);
      if (playerWin) {
        s.placeImageXY(new TextImage("You Win!", CELL_SIZE * 2, Color.GREEN),
            boardSizeX * CELL_SIZE / 2, boardSizeY * CELL_SIZE / 2);
      }
    } else {
      double horizspacing = Math.sqrt(3) * CELL_SIZE / 2;
      s = new WorldScene(3 * boardSizeX * CELL_SIZE, 2 * boardSizeY * CELL_SIZE );
      for (int i = 0; i < boardSizeX; i++) {
        for (int j = 0; j < boardSizeY; j++) {
          s.placeImageXY(mazeBoard.get(i).get(j).drawCell(),
              (int) (2 * i * horizspacing + (boardSizeY - j) * horizspacing), 
              (int) (1.5 * j * CELL_SIZE + CELL_SIZE) - 1);
        }
      }
    }
    return s;
  }

  //assigns the weights of the edges of our maze Graph randomly
  void randomizeEdgeWeights() {
    for (Edge e: mazeGraph.edges) {
      e.weight = rand.nextInt();
    }
  }

  //makes each scene produced by big bang
  public WorldScene makeScene() {
    return this.drawBoard();
  }

  //handles each tick in bigBang
  public void onTick() {
    if (searchPhase && search.hasNext()) {
      Vertex next = search.next();
      this.mazeBoard.get(next.x).get(next.y).color = Color.CYAN;
      if (search.complete) {
        this.solve = search.reconstruct();
        this.searchPhase = false;
        this.solvePhase = true;
      }
    }
    if (solvePhase && solve.hasNext()) {
      Vertex next = solve.next();
      this.mazeBoard.get(next.x).get(next.y).color = Color.CYAN.darker();
    }
    if (player.x == boardSizeX - 1 && player.y == boardSizeY - 1) {
      this.playerWin = true;
      this.search = mazeGraph.bfsIter();
      while (!search.complete && search.hasNext()) {
        search.next(); 
      }
      this.solvePhase = true;
      this.solve = search.reconstruct();
      this.player = new Posn(-1, -1);
    }
  }

  //handles key events in big bang
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.reset();
    }
    if (key.equals("h")) {
      this.hexify();
    }
    if (key.equals("d") && !searchPhase && !solvePhase) {
      this.search = mazeGraph.dfsIter();
      this.searchPhase = true;
      player = new Posn(-1, -1);
    }
    if (key.equals("b") && !searchPhase && !solvePhase) {
      this.search = mazeGraph.bfsIter();
      this.searchPhase = true;
      this.player = new Posn(-1, -1);
    }
    if (!searchPhase && !solvePhase && !playerWin && !hex) {
      if (key.equals("up") 
          && !mazeBoard.get(player.x).get(player.y).topwall) {
        this.player.y --;
      }
      if (key.equals("down") 
          && !mazeBoard.get(player.x).get(player.y).botwall) {
        this.player.y ++;
      }
      if (key.equals("left") 
          && !mazeBoard.get(player.x).get(player.y).leftwall) {
        this.player.x --;
      }
      if (key.equals("right") 
          && !mazeBoard.get(player.x).get(player.y).rightwall) {
        this.player.x ++;
      }
    }
  }
}

//a visual cell of the board
class Cell {
  Color color;
  boolean topwall;
  boolean rightwall;
  boolean botwall;
  boolean leftwall;

  Cell(boolean top, boolean right, boolean bot, boolean left) {
    this.topwall = top;
    this.rightwall = right;
    this.botwall = bot;
    this.leftwall = left;
    this.color = Color.LIGHT_GRAY;
  }

  //produces a WorldImage of this Cell
  WorldImage drawCell() {
    WorldImage img =  new RectangleImage(Maze.CELL_SIZE, Maze.CELL_SIZE,
        OutlineMode.SOLID, color);
    if (topwall) {
      img = new AboveImage(new LineImage(new Posn(Maze.CELL_SIZE, 0), Color.BLACK), img);
    }
    if (rightwall) {
      img = new BesideImage(img, new LineImage(new Posn(0, Maze.CELL_SIZE), Color.BLACK));
    }
    if (botwall) {
      img = new AboveImage(img, new LineImage(new Posn(Maze.CELL_SIZE, 0), Color.BLACK));
    }
    if (leftwall) {
      img = new BesideImage( new LineImage(new Posn(0, Maze.CELL_SIZE), Color.BLACK), img);
    }
    img = img.movePinholeTo(new Posn(0, 0));
    return img;
  }
}

//a hexagonal version of Cell
class HexCell extends Cell {
  //needs an additional two walls:
  boolean toprightwall;//top-right
  boolean botleftwall;//bottom-left
  
  //walls are constructed in clockwise order, from top-left
  HexCell(boolean top, boolean top2, boolean right, boolean bot, boolean bot2, boolean left) {
    super(top, right, bot, left);
    this.toprightwall = top2;
    this.botleftwall = bot2;
  }
  
  WorldImage drawCell() {
    WorldImage img =  new HexagonImage(Maze.CELL_SIZE + 2, OutlineMode.SOLID, color);
    WorldImage wallLine = new LineImage(new Posn(Maze.CELL_SIZE, 0), Color.BLACK)
        .movePinhole(0, Maze.CELL_SIZE * Math.sqrt(3) / 2);
    
    if (topwall) {
      WorldImage wallLine1 = wallLine;
      img = new OverlayImage(wallLine1, img);
    }
    if (toprightwall) {
      WorldImage wallLine2 = new RotateImage(wallLine, 60);
      img = new OverlayImage(wallLine2, img);
    }
    if (rightwall) {
      WorldImage wallLine3 = new RotateImage(wallLine, 120);
      img = new OverlayImage(wallLine3, img);
    }
    if (botwall) {
      WorldImage wallLine4 = new RotateImage(wallLine, 180);
      img = new OverlayImage(wallLine4, img);
    }
    if (botleftwall) {
      WorldImage wallLine5 = new RotateImage(wallLine, 240);
      img = new OverlayImage(wallLine5, img);
    }
    if (leftwall) {
      WorldImage wallLine6 = new RotateImage(wallLine, 300);
      img = new OverlayImage(wallLine6, img);
    }
    img = new RotateImage(img, -30);
    return img;
  
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////
class ExamplesMaze {

  Random rand;

  Vertex a;
  Vertex b;
  Vertex c;
  Vertex d;
  Vertex e;
  Vertex f;

  Edge ab;
  Edge bc;
  Edge ce;
  Edge ea;
  Edge eb;
  Edge bf;
  Edge fd;
  Edge cd;

  Graph g1;
  /* g1 should look like:
   * {a}---{b}---{f}
   *  |    /|     |
   *  |   / |     |
   *  |  /  |     |
   *  {e}--{c}---{d}
   */
  Graph g2;
  BoardGraph board1;
  BoardGraph board2;

  void initData() {
    rand = new Random(0);
    board1 = new BoardGraph(20, 15);
    board2 = new BoardGraph(4,3);
    a = new Vertex(1, 1, "a");
    b = new Vertex(2, 2, "b");
    c = new Vertex(3, 3, "c");
    d = new Vertex(1, 1, "d");
    e = new Vertex(2, 2, "e");
    f = new Vertex(3, 3, "f");
    ab = new Edge(a, b, 30);
    bc = new Edge(b, c, 40);
    ce = new Edge(c, e, 15);
    ea = new Edge(e, a, 50);
    eb = new Edge(e, b, 35);
    bf = new Edge(b, f, 50);
    fd = new Edge(f, d, 50);
    cd = new Edge(c, d, 25);

    //should look like the graph pictured in lecture 32
    g1 = new Graph(new ArrayList<Vertex>(Arrays.asList(a,b,c,d,e,f)),
        new ArrayList<Edge>(Arrays.asList(ab, bc, ce, ea, eb, bf, fd, cd)));
  }

  void testOnTick(Tester t) {
    Maze m = new Maze(20, 20, new Random());
    m.bigBang(3 * m.boardSizeX * Maze.CELL_SIZE,  
        3 * m.boardSizeY * Maze.CELL_SIZE,
        0.05);
  }

  void testInitVertices(Tester t) {
    initData();

    t.checkExpect(this.board1.nodes.size(), 300);
    t.checkExpect(this.board1.nodes.get(0), new Vertex(0, 0));

    t.checkExpect(this.board2.nodes.get(8), new Vertex(8 / 3, 8 % 3));
  }

  void testInitEdges(Tester t) {
    initData();
    BoardGraph hexboard = new HexGraph(3, 3);

    t.checkExpect(board1.edges.size(),
        (board1.boardSizeX - 1) * board1.boardSizeY 
        + board1.boardSizeX * (board1.boardSizeY - 1));
    t.checkExpect(board2.edges.size(), 17);
    t.checkExpect(hexboard.edges.size(), 16);

    t.checkExpect(board2.edges.get(0)
        .equals(new Edge(board2.nodes.get(0), board2.nodes.get(3), 1)), true);
    t.checkExpect(board2.edges.get(1)
        .equals(new Edge(board2.nodes.get(0), board2.nodes.get(1), 1)), true);

    t.checkExpect(board2.edges.get(2)
        .equals(new Edge(board2.nodes.get(1), board2.nodes.get(4), 1)), true);
    t.checkExpect(board2.edges.get(3)
        .equals(new Edge(board2.nodes.get(1), board2.nodes.get(2), 1)), true);
    
    t.checkExpect(hexboard.edges.get(0)
        .equals(new Edge(hexboard.nodes.get(0), hexboard.nodes.get(3), 1)), true);
    t.checkExpect(hexboard.edges.get(1)
        .equals(new Edge(hexboard.nodes.get(0), hexboard.nodes.get(1), 1)), true);
    t.checkExpect(hexboard.edges.get(2)
        .equals(new Edge(hexboard.nodes.get(0), hexboard.nodes.get(4), 1)), true);
  }

  void testSameLocation(Tester t) {
    initData();
    Edge e1 = new Edge(a, b, 1);
    Edge e2 = new Edge(b, c, 1);
    Edge e3 = new Edge(c, a, 1);
    Edge e4 = new Edge(a, c, 1);
    Edge e5 = new Edge(c, b, 1);

    t.checkExpect(e1.sameLocation(e2), false);
    t.checkExpect(e2.sameLocation(e3), false);
    t.checkExpect(e3.sameLocation(e4), true);
    t.checkExpect(e4.sameLocation(e3), true);
    t.checkExpect(e5.sameLocation(e2), true);
  }

  void testSortEdges(Tester t) {
    initData();
    g1.sortEdges();

    t.checkExpect(g1.edges, 
        new ArrayList<Edge>(Arrays.asList(ce, cd, ab, eb, bc, ea, bf, fd)));
  }

  void testMinimumSpanning(Tester t) {
    initData();
    g1.minimumSpanning();

    t.checkExpect(g1.edges.size(), g1.nodes.size() - 1);
  }

  void testUnionAndFind(Tester t) {
    initData();
    UnionFind uf = new UnionFind(g1);
    uf.union(ce);
    uf.union(cd);
    uf.union(ab);
    t.checkExpect(uf.edgesInTree, new ArrayList<Edge>(Arrays.asList(ce, cd, ab)));
    t.checkExpect(uf.find(a.hashCode()), b.hashCode());
    t.checkExpect(uf.find(b.hashCode()), b.hashCode());
    t.checkExpect(uf.find(c.hashCode()), d.hashCode());
    t.checkExpect(uf.find(d.hashCode()), d.hashCode());
    t.checkExpect(uf.find(e.hashCode()), d.hashCode());
    t.checkExpect(uf.find(f.hashCode()), f.hashCode());
  }

  void testVertexAt(Tester t) {
    initData();

    t.checkExpect(board2.nodeAt(2, 2).x, 2);
    t.checkExpect(board2.nodeAt(2, 2).y, 2);
    t.checkExpect(board1.nodeAt(7, 4).x, 7);
    t.checkExpect(board1.nodeAt(7, 4).y, 4);
  }

  void testDrawCell(Tester t) {
    Cell c1 = new Cell(true, false, true, true);
    WorldCanvas canvas  = new WorldCanvas(200, 200);
    WorldScene s = new WorldScene(200, 200);
    s.placeImageXY(c1.drawCell(), 100, 100);
    canvas.drawScene(s);
    //canvas.show();

  }

  void testDrawBoard(Tester t) {
    initData();
    Maze maze = new Maze(board1);
    WorldCanvas canvas  = new WorldCanvas(maze.boardSizeX * Maze.CELL_SIZE, 
        maze.boardSizeY * Maze.CELL_SIZE);
    canvas.drawScene(maze.drawBoard());
    //canvas.show();

  }

  void testLessWeight(Tester t) {
    initData();

    LessWeight lessWeight = new LessWeight();

    t.checkExpect(lessWeight.compare(this.ab, this.bc), -10);
    t.checkExpect(lessWeight.compare(this.bf, this.fd), 0);
    t.checkExpect(lessWeight.compare(this.fd, this.cd), 25);
  }

  void testMergeSort(Tester t) {
    initData();

    ArrayUtils au = new ArrayUtils();
    ArrayList<Edge> arrayList1 = new ArrayList<Edge>(Arrays.asList(this.ce, this.ab, this.bf, 
        this.bc, this.cd));
    au.mergesort(arrayList1, new LessWeight());

    t.checkExpect(arrayList1, new ArrayList<Edge>(Arrays.asList(this.ce, this.cd, this.ab, 
        this.bc, this.bf)));
  }

  void testMergeSortHelp(Tester t) {
    initData();

    ArrayUtils au = new ArrayUtils();
    ArrayList<Edge> arrayList1 = new ArrayList<Edge>(Arrays.asList(this.ce, this.ab, this.bf));
    au.mergesortHelp(arrayList1, arrayList1, new LessWeight(), 7, 5);

    t.checkExpect(arrayList1, new ArrayList<Edge>(Arrays.asList(new Edge(new Vertex(3,3), 
        new Vertex(2,2), 15), new Edge(new Vertex(1,1), new Vertex(2,2), 30), 
        new Edge(new Vertex(2,2), new Vertex(3,3), 50))));                        
  }

  void testMerge(Tester t) {
    initData();

    ArrayUtils au = new ArrayUtils();
    ArrayList<Edge> arrayList1 = new ArrayList<Edge>(Arrays.asList(this.ce, this.ab));
    ArrayList<Edge> arrayList2 = new ArrayList<Edge>(Arrays.asList(this.ab));

    au.merge(arrayList1, arrayList2, new LessWeight(), 0, 1, 0);

    t.checkExpect(arrayList1, new ArrayList<Edge>(Arrays.asList(new Edge(new Vertex(3,3), 
        new Vertex(2,2), 15), new Edge(new Vertex(1,1), new Vertex(2,2), 30))));

  }

  void testEquals(Tester t) {
    initData();

    t.checkExpect(this.ab.equals(this.ab), true);
    t.checkExpect(this.ab.equals(this.bc), false);
    t.checkExpect(this.ea.equals(this.bf), false);
    t.checkExpect(this.ea.equals(this.a), false);
  }

  void testHashCode(Tester t) {
    initData();

    t.checkExpect(this.ce.hashCode() == this.ab.hashCode(), false);
    t.checkExpect(this.bf.hashCode() == this.bc.hashCode(), false);
    t.checkExpect(this.ab.hashCode() == this.cd.hashCode(), false);
    t.checkExpect(this.bf.hashCode() == this.bf.hashCode(), true);
    t.checkExpect(this.ab.hashCode() 
        == 33 * this.ab.weight + this.ab.v1.hashCode() + this.ab.v2.hashCode(), true);
    t.checkExpect(this.bc.hashCode() 
        == 33 * this.bc.weight + this.bc.v1.hashCode() + this.bc.v2.hashCode(), true);
    t.checkExpect(this.bf.hashCode() 
        == 33 * this.bf.weight + this.bf.v1.hashCode() + this.bf.v2.hashCode(), true);
  }

  void testApply(Tester t) {
    initData();
    UnionFind uf = new UnionFind(this.g1);

    t.checkExpect(uf.edgesInTree.size(), 0);
    t.checkExpect(uf.representatives.size(), 6);
    t.checkExpect(uf.representatives.values().size(), 6);
    ArrayList<Integer> arrayListRep = new ArrayList<Integer>(uf.representatives.values());

    uf.apply();

    t.checkExpect(uf.edgesInTree.size(), 5);
    t.checkExpect(uf.representatives.size(), 6);
    t.checkExpect(uf.representatives.values().size(), 6);
    ArrayList<Integer> arrayListRepAfter = new ArrayList<Integer>(uf.representatives.values());

    t.checkExpect(arrayListRep.equals(arrayListRepAfter), false);

  }

  void testSelfRef(Tester t) {
    initData();

    BoardGraph bg = new BoardGraph(3, 3);    

    UnionFind uf = new UnionFind(bg);
    uf.representatives.put(uf.worklist.get(0).hashCode(), uf.worklist.get(0).hashCode());
    uf.representatives.put(uf.worklist.get(1).hashCode(), uf.worklist.get(0).hashCode());


    t.checkExpect(uf.selfRef(uf.worklist.get(0).hashCode()), true);
    t.checkExpect(uf.selfRef(uf.worklist.get(1).hashCode()), false);

  }

  void testNSelfRef(Tester t) {
    initData();
    UnionFind uf = new UnionFind(g1);
    uf.union(ce);
    uf.union(cd);
    uf.union(ab);

    t.checkExpect(uf.nSelfRef(), 3);

    uf.union(fd);

    t.checkExpect(uf.nSelfRef(), 2);

  }


  void testBuildBoard(Tester t) {
    initData();
    Maze maze = new Maze(board1);

    t.checkExpect(maze.mazeBoard.size(), 20);
    t.checkExpect(maze.mazeBoard.get(0).size(), 15);
    t.checkExpect(maze.mazeGraph.edges.size(), 299);
    t.checkExpect(maze.mazeGraph.nodes.size(), 300);

    Maze mazeBoard2 = new Maze(board2);

    t.checkExpect(mazeBoard2.mazeBoard.size(), 4);
    t.checkExpect(mazeBoard2.mazeBoard.get(0).size(), 3);
    t.checkExpect(mazeBoard2.mazeGraph.edges.size(), 11);
    t.checkExpect(mazeBoard2.mazeGraph.nodes.size(), 12);    

  }

  void testRandomizeEdgeWeights(Tester t) {
    initData();

    BoardGraph bg = new BoardGraph(2,2);

    t.checkExpect(bg.edges.get(1).weight, 1);
    t.checkExpect(bg.edges.get(2).weight, 1);

    Maze m = new Maze(bg);

    m.randomizeEdgeWeights();

    //Are the edge weights still the same
    t.checkExpect(bg.edges.get(1).weight != 1, true);
    t.checkExpect(bg.edges.get(2).weight != 1, true);

  }

  void testdfsIter(Tester t) {
    initData();
    g1.minimumSpanning();

    GraphSearch dfs = g1.dfsIter(a, f);
    while (dfs.hasNext()) {
      t.checkNoException(dfs, "next");
    }

    GraphSearch bfs = g1.bfsIter(a, f);
    while (bfs.hasNext()) {
      //t.checkNoException(bfs, "next");
      bfs.next();
    }
    t.checkExpect(bfs.complete, true);
    for (Vertex v: bfs.alreadySeen) {
      t.checkExpect(bfs.cameFrom.get(v) != null, true);
    }
  }


  void testOpposite(Tester t) {
    Edge e1 = new Edge(a, b, 1);
    Edge e2 = new Edge(b, c, 1);
    Edge e3 = new Edge(c, a, 1);

    t.checkExpect(e1.opposite(a), b);
    t.checkExpect(e1.opposite(b), a);
    t.checkExpect(e2.opposite(c), b);
    t.checkExpect(e2.opposite(b), c);
    t.checkExpect(e3.opposite(a), c);
    t.checkExpect(e3.opposite(c), a);
    t.checkException(new IllegalArgumentException("Edge does not connect the specified vertex"),
        e3, "opposite", b);
  }


  void testReset(Tester t) {
    Random r = new Random();
    Maze mmmm = new Maze(10,10,r);
    Graph oldGraph = mmmm.mazeGraph;
    ArrayList<ArrayList<Cell>> oldboard = mmmm.mazeBoard;

    mmmm.player = new Posn(3,3);
    mmmm.searchPhase = true;
    mmmm.solvePhase = true;
    mmmm.playerWin = true;
    mmmm.reset();

    t.checkExpect(mmmm.player.x == 0 && mmmm.player.y == 0, true);
    t.checkExpect(mmmm.searchPhase, false);
    t.checkExpect(mmmm.solvePhase, false);
    t.checkExpect(mmmm.playerWin, false);
    t.checkExpect(mmmm.rand == r, false);
    t.checkExpect(mmmm.mazeGraph == oldGraph, false);
    t.checkExpect(mmmm.mazeBoard == oldboard, false);
    t.checkExpect(mmmm.boardSizeX == 10 && mmmm.boardSizeY == 10, true);
  }

  void testReconstruct(Tester t) {
    Maze mmm = new Maze(10, 10, new Random());
    mmm.search = mmm.mazeGraph.bfsIter();
    while (!mmm.search.complete) {
      mmm.search.next(); 
    }
    mmm.solve = mmm.search.reconstruct();

    while (true) {
      Vertex v = mmm.solve.next();
      if (v == mmm.search.from) {
        t.checkExpect(v, mmm.search.from);
        break; 
        // if this line is not reached, will loop infinitely, and thus the behavior is not correct
      }
    }
  }

  void testPlayerWin(Tester t) {
    Maze mmm = new Maze(10, 10, new Random());
    mmm.player = new Posn(9,9);
    mmm.onTick();
    mmm.onTick();

    t.checkExpect(mmm.player.x == -1 && mmm.player.y == -1, true);
    t.checkExpect(mmm.solve != null, true);
    t.checkExpect(mmm.playerWin, true);

    WorldCanvas c = new WorldCanvas(10 * Maze.CELL_SIZE, 10 * Maze.CELL_SIZE);
    c.drawScene(mmm.drawBoard());
    //c.show();
    //is "You Win!" in green letters pictured in the middle of the board? --> yes
  }

  void testICollectionAddAndRemove(Tester t) {
    Stack<Integer> stack = new Stack<Integer>();
    Queue<Integer> queue = new Queue<Integer>();

    stack.add(1);
    stack.add(2);
    stack.add(3);
    stack.add(4);

    queue.add(1);
    queue.add(2);
    queue.add(3);
    queue.add(4);

    t.checkExpect(stack.remove(), 4);
    t.checkExpect(queue.remove(), 1);
  }

  void testAdd(Tester t) {

    Stack<Integer> s = new Stack<Integer>();
    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(s.isEmpty(), true);
    t.checkExpect(q.isEmpty(), true);

    s.add(1);
    q.add(1);

    t.checkExpect(s.isEmpty(), false);
    t.checkExpect(q.isEmpty(), false);
  }

  void testRemove(Tester t) {

    Stack<Integer> s = new Stack<Integer>();
    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(s.isEmpty(), true);
    t.checkExpect(q.isEmpty(), true);

    s.add(1);
    q.add(1);

    t.checkExpect(s.isEmpty(), false);
    t.checkExpect(q.isEmpty(), false);

    s.remove();
    q.remove();

    t.checkExpect(s.isEmpty(), true);
    t.checkExpect(q.isEmpty(), true);

  }

  void testIsEmpty(Tester t) {

    Stack<Integer> s = new Stack<Integer>();
    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(s.isEmpty(), true);
    t.checkExpect(q.isEmpty(), true);

    s.add(1);
    q.add(1);

    t.checkExpect(s.isEmpty(), false);
    t.checkExpect(q.isEmpty(), false);
  }

  void testAddStack(Tester t) {

    Stack<Integer> s = new Stack<Integer>();

    t.checkExpect(s.isEmpty(), true);

    s.add(1);
    s.add(1);

    t.checkExpect(s.isEmpty(), false);
  }

  void testRemoveStack(Tester t) {

    Stack<Integer> s = new Stack<Integer>();

    t.checkExpect(s.isEmpty(), true);

    s.add(1);
    s.add(1);

    t.checkExpect(s.isEmpty(), false);

    s.remove();

    t.checkExpect(s.isEmpty(), false);

    s.remove();

    t.checkExpect(s.isEmpty(), true);

  }

  void testIsEmptyStack(Tester t) {

    Stack<Integer> s = new Stack<Integer>();

    t.checkExpect(s.isEmpty(), true);

    s.add(5);

    t.checkExpect(s.isEmpty(), false);

    s.remove();

    t.checkExpect(s.isEmpty(), true);

  }

  void testAddQueue(Tester t) {

    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(q.isEmpty(), true);

    q.add(1);
    q.add(1);

    t.checkExpect(q.isEmpty(), false);
  }

  void testRemoveQueue(Tester t) {

    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(q.isEmpty(), true);

    q.add(1);
    q.add(1);

    t.checkExpect(q.isEmpty(), false);

    q.remove();

    t.checkExpect(q.isEmpty(), false);

    q.remove();

    t.checkExpect(q.isEmpty(), true);

  }

  void testIsEmptyQueue(Tester t) {

    Queue<Integer> q = new Queue<Integer>();

    t.checkExpect(q.isEmpty(), true);

    q.add(5);

    t.checkExpect(q.isEmpty(), false);

    q.remove();

    t.checkExpect(q.isEmpty(), true);

  }

  void testOnKeyEventBreadth(Tester t) {

    Maze m = new Maze(this.board1);

    t.checkExpect(m.searchPhase, false);

    //Should do nothing
    m.onKeyEvent("a");
    t.checkExpect(m.searchPhase, false);

    //Testing a breadth-first search b key press
    m.onKeyEvent("b");
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.player, new Posn(-1, -1));

    //Change player posn
    m.onKeyEvent("down");

    //Should reset player posn
    m.onKeyEvent("b");
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.player, new Posn(-1, -1));

  }

  void testOnKeyEventDepth(Tester t) {

    Maze m = new Maze(this.board1);

    //Not in searchphase since b or d has not been pressed down yet
    t.checkExpect(m.searchPhase, false);

    //Should do nothing
    m.onKeyEvent("a");
    t.checkExpect(m.searchPhase, false);

    //Testing a dpeth-first search b key press
    m.onKeyEvent("d");
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.player, new Posn(-1, -1));

    //Change player posn
    m.onKeyEvent("down");

    //Should reset player posn
    m.onKeyEvent("d");
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.player, new Posn(-1, -1));


  }

  void testOnKeyEventPlayerMovement(Tester t) {

    Maze m = new Maze(this.board1);
    m.mazeBoard.get(0).get(0).botwall = false;
    m.mazeBoard.get(0).get(1).botwall = false;
    m.mazeBoard.get(1).get(0).botwall = false;
    m.mazeBoard.get(1).get(1).botwall = false;

    m.mazeBoard.get(0).get(1).topwall = false;
    m.mazeBoard.get(1).get(1).topwall = false;

    m.mazeBoard.get(1).get(0).leftwall = false;

    m.mazeBoard.get(0).get(0).rightwall = false;
    m.mazeBoard.get(0).get(1).rightwall = false;
    m.mazeBoard.get(1).get(0).rightwall = false;

    t.checkExpect(m.searchPhase, false);

    //Should do nothing, does nothing
    m.onKeyEvent("a");
    t.checkExpect(m.searchPhase, false);
    t.checkExpect(m.player, new Posn(0,0));



    //Testing player movement downwards in game
    m.onKeyEvent("down");
    t.checkExpect(m.player, new Posn(0,1));
    t.checkExpect(m.searchPhase, false);

    //Should not be able to move again!
    m.onKeyEvent("left");
    t.checkExpect(m.player, new Posn(0,1));



    //Testing player movement upwards in game
    m.onKeyEvent("up");
    t.checkExpect(m.player, new Posn(0,0));

    //Should not be able to move again!
    m.onKeyEvent("left");
    t.checkExpect(m.player, new Posn(0,0));



    //Testing player movement left in game
    m.onKeyEvent("right");
    t.checkExpect(m.player, new Posn(1,0));

    //Testing player movement left en jugo
    m.onKeyEvent("left");
    t.checkExpect(m.player, new Posn(0,0));

    //Should not be able to move again!
    m.onKeyEvent("left");
    t.checkExpect(m.player, new Posn(0,0));

  }

  void testOnKeyEventReset(Tester t) {

    Maze m = new Maze(this.board1);

    ArrayList<ArrayList<Cell>> originalAList = new ArrayList<ArrayList<Cell>>();

    boolean sameBoard = true;

    for (int i = 0; i < m.mazeBoard.size(); i++) {
      originalAList.add(new ArrayList<Cell>());
      for (int j = 0; j < m.mazeBoard.get(i).size(); j++) {
        originalAList.get(i).add(m.mazeBoard.get(i).get(j));
      }
    }

    //Arbitrary moves to ensure that pressing r brings the player back to the origin
    m.onKeyEvent("down");
    m.onKeyEvent("right");
    m.onKeyEvent("down");
    m.onKeyEvent("right");

    //Reset this maze
    m.onKeyEvent("r");

    //Compare the board!
    for (int i = 0; i < m.mazeBoard.size(); i++) {
      for (int j = 0; j < m.mazeBoard.get(i).size(); j++) {
        sameBoard &= originalAList.get(i).get(j).equals(m.mazeBoard.get(i).get(j));
      }
    }

    //Is the player back in the starting position?
    t.checkExpect(m.player, new Posn(0,0));

    //Are the boards the same (didn't reset if true)
    t.checkExpect(sameBoard, false);

    //Tests the states of the board
    t.checkExpect(m.searchPhase, false);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);

  }

  void testOnTickBreadth(Tester t) {
    initData();

    Maze m = new Maze(this.board1);

    //TESTING BREADTH FIRST
    //m.bigBang(m.boardSizeX * Maze.CELL_SIZE, m.boardSizeY * Maze.CELL_SIZE, 0.05);

    t.checkExpect(m.searchPhase, false);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);

    m.onKeyEvent("b");

    m.onTick();
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);
    t.checkExpect(m.mazeBoard.get(0).get(0).color, Color.CYAN);

    m.onTick();
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);
    t.checkExpect(m.mazeBoard.get(0).get(0).color.equals(Color.CYAN)
        && (m.mazeBoard.get(0).get(1).color.equals(Color.cyan)
            || m.mazeBoard.get(1).get(0).color.equals(Color.cyan)), true);    
  }

  void testOnTickDepth(Tester t) {
    //TESTING DEPTH FIRST
    //Building off of a previous test where "r" is confirmed to be working properly
    initData();
    Maze m = new Maze(this.board1);

    t.checkExpect(m.searchPhase, false);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);

    m.onKeyEvent("d");

    m.onTick();
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);
    t.checkExpect(m.mazeBoard.get(0).get(0).color, Color.CYAN);

    m.onTick();
    t.checkExpect(m.searchPhase, true);
    t.checkExpect(m.solvePhase, false);
    t.checkExpect(m.playerWin, false);
    t.checkExpect(m.mazeBoard.get(0).get(0).color.equals(Color.CYAN)
        && (m.mazeBoard.get(0).get(1).color.equals(Color.CYAN)
            || m.mazeBoard.get(1).get(0).color.equals(Color.CYAN)), true);
  }

  void testOnTickPlayerMovement(Tester t) {
    //TESTING PLAYER MOVEMENT
    initData();
    Maze m = new Maze(this.board1);

    //Break down walls between the noted cells to provide reliable testing
    m.mazeBoard.get(0).get(0).botwall = false;
    m.mazeBoard.get(0).get(1).botwall = false;
    m.mazeBoard.get(1).get(0).botwall = false;

    m.mazeBoard.get(0).get(0).topwall = false;
    m.mazeBoard.get(0).get(1).topwall = false;
    m.mazeBoard.get(1).get(0).topwall = false;

    m.mazeBoard.get(0).get(0).leftwall = false;
    m.mazeBoard.get(0).get(1).leftwall = false;
    m.mazeBoard.get(1).get(0).leftwall = false;

    m.mazeBoard.get(0).get(0).rightwall = false;
    m.mazeBoard.get(0).get(1).rightwall = false;
    m.mazeBoard.get(1).get(0).rightwall = false;


    t.checkExpect(m.player, new Posn(0,0));

    m.onKeyEvent("right");
    t.checkExpect(m.player, new Posn(1,0));

    m.onKeyEvent("down");
    t.checkExpect(m.player, new Posn(1,1));

    m.player = new Posn(m.boardSizeX - 1, m.boardSizeY - 1);

    m.onTick();
    t.checkExpect(m.playerWin, true);
    t.checkExpect(m.solvePhase, true);

    m.onTick();
    t.checkExpect(m.player, new Posn(-1, -1));

  }
  
  void testDrawHexCell(Tester t) {
    WorldImage hexImage = new HexCell(true, true, true, true, true, false).drawCell();
    WorldCanvas canvas  = new WorldCanvas(200, 200);
    WorldScene s = new WorldScene(200, 200);
    s.placeImageXY(hexImage, 100, 100);
    canvas.drawScene(s);
    canvas.show();
    //RotateImage seems to draw lines a bit wonky?
  }
  
  void testHexify(Tester t) {
    Maze hexyboi = new Maze(3, 3, new Random());
    hexyboi.hexify();
    
    t.checkExpect(hexyboi.mazeGraph instanceof HexGraph, true);
    for (ArrayList<Cell> col: hexyboi.mazeBoard) {
      for (Cell c: col) {
        t.checkExpect(c instanceof HexCell, true);
      }
    }
    t.checkExpect(hexyboi.hex, true);
  }
}
