import java.util.Random;

// Node class to represent each element in the skip list
class Node {
int value;        // Value of the node
Node next;        // Pointer to the next node at the same level
Node down;        // Pointer to the node at the lower level

// Constructor for Node with value, next, and down pointers
Node(int val, Node nextPtr, Node downPtr) {
this.value = val;
this.next = nextPtr;
this.down = downPtr;
}
}

// SkipList class to manage the skip list operations
public class SkipList {
private Node topHead;            // Pointer to the top level's head node
private int maxLevels;           // Maximum number of levels in the skip list
private Random random;           // Random number generator for coin flips

// Function to create a new node with given value, next, and down pointers
private Node createNode(int value, Node next, Node down) {
return new Node(value, next, down);
}

// Insert a new node after the given node in the same level
private void insertAfter(Node prev, Node newNode) {
newNode.next = prev.next;
prev.next = newNode;
}

// Remove the node after the given node in the same level
private void removeAfter(Node prev) {
if (prev != null && prev.next != null) {
prev.next = prev.next.next;
}
}

// Constructor for SkipList with specified maximum levels (default = 16)
public SkipList(int maxLevel) {
this.maxLevels = maxLevel;
this.random = new Random();
this.topHead = createNode(Integer.MAX_VALUE, null, null);  // Create the top head node with max value

// Create multiple levels by adding new head nodes pointing down to the previous head
for (int i = 1; i < maxLevels; i++) {
topHead = createNode(Integer.MAX_VALUE, null, topHead);
}
}

// Function to search for a key in the skip list
public boolean find(int key) {
Node current = topHead;  // Start from the top level
while (current != null) {
// Move down if the next node's value is greater than the key or there's no next node
if (current.next == null || current.next.value > key) {
                current = current.down;
}
// Key found
else if (current.next.value == key) {
return true;
}
// Move right to the next node at the same level
else {
current = current.next;
}
}
return false;  // Key not found
}

// Function to add a new key to the skip list
public void add(int key) {
Node[] update = new Node[maxLevels];  // Array to keep track of nodes at each level where we need to insert
Node current = topHead;
int level = maxLevels - 1;

// Traverse down the levels to find where to insert the node
while (current != null) {
if (current.next == null || current.next.value > key) {
update[level] = current;  // Keep track of the current node at this level
current = current.down;
level--;
} else if (current.next.value == key) {
return;  // Key already exists, return to prevent duplicates
} else {
current = current.next;
}
}

// Always insert the node at level 0
Node downNode = null;
Node newNode = createNode(key, update[0].next, null);  // Create a new node for level 0
insertAfter(update[0], newNode);
downNode = newNode;

// Promote the node to higher levels based on random coin flips
int levelToInsert = 1;
while (levelToInsert < maxLevels && random.nextBoolean()) {
if (update[levelToInsert] == null) {
topHead = createNode(Integer.MAX_VALUE, null, topHead);  // Add a new level
update[levelToInsert] = topHead;
}

newNode = createNode(key, update[levelToInsert].next, downNode);  // Create node for higher level
insertAfter(update[levelToInsert], newNode);
downNode = newNode;  // Link the node with the lower level node
levelToInsert++;
}
}

// Function to remove a key from the skip list
public void remove(int key) {
Node[] update = new Node[maxLevels];  // Array to keep track of nodes at each level where we need to remove
Node current = topHead;
int level = maxLevels - 1;

 // Traverse down the levels to find the node to remove
while (current != null) {
if (current.next == null || current.next.value >= key) {
update[level] = current;  // Keep track of the current node at this level
current = current.down;
level--;
} else {
current = current.next;
}
}

// Remove the node from each level where it exists
for (int i = 0; i < maxLevels; i++) {
if (update[i] != null && update[i].next != null && update[i].next.value == key) {
removeAfter(update[i]);  // Remove the node after the tracked node
}
}
}

// Function to display the structure of the skip list
public void display() {
System.out.println("Skiplist Structure:");
Node currentLevel = topHead;
int level = maxLevels - 1;

// Traverse through each level and display the nodes
while (currentLevel != null) {
System.out.print("Level " + level + ": ");
Node currentNode = currentLevel;
while (currentNode != null) {
System.out.print(currentNode.value + " ");
currentNode = currentNode.next;
}
System.out.println();
currentLevel = currentLevel.down;  // Move to the next lower level
level--;
}
}

public static void main(String[] args) {
SkipList skiplist = new SkipList(16);

// Adding some keys to the skip list
skiplist.add(10);
skiplist.add(20);
skiplist.add(30);
skiplist.add(40);  // Testing additional inserts

// Display the skip list structure
skiplist.display();

// Testing search functionality
System.out.println("Searching for 10: " + skiplist.find(10));
System.out.println("Searching for 15: " + skiplist.find(15));

// Testing remove functionality
skiplist.remove(20);
skiplist.display();
}
}
