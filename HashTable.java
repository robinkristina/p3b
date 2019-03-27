//import HashTable.HashNode;

////////////////////ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
//Title: p3a_project
//Course: CS 400 Spring 2019
//Lecture: 001
//
//Author: Robin Stauffer
//Email: rstauffer@wisc.edu
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
//Students who get help from sources other than their partner must fully
//acknowledge and credit those sources of help here. Instructors and TAs do
//not need to be credited here, but tutors, friends, relatives, room mates,
//strangers, and others do. If you received no outside help from either type
//of source, then please explicitly indicate NONE.
//
//Persons: NONE
//Online Sources: NONE
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

/**
 * This class implements a generic hash table, storing key,value pairs as HashNode<K, V> objects
 * 
 * The hashing algorithm used was very standard. It is simply the (positive) hashCode provided by the 
 *  <K key> Object, mod table size. (key.hashCode() % table size)
 *  
 * To handle collisions, this class implements a bucket scheme by creating the HashTable as an
 *  array of linked lists. Each node added to the data structure that has the same hash index as 
 *  an already existing hashNode is added at that index, as the new head of the linked list stored there.
 *  
 * @author Robin
 *
 * @param <K> generic type for the key's stored in this data structure
 * @param <V> generic type for the values stored in this data structure
 */
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {
    
    private HashNode<K, V>[] hashArray; // an array of linked lists to hold all HashNodes
    private double loadFactorThreshold; // specifies when the HashTable should be resized
    private int numKeys; // number of keys currently held in the HashTable
    
    /**
     * This class creates an Object that holds a key and value, as well as a reference to another
     * HashNode Object. It is used to create linked lists within the hash table
     * @author Robin
     *
     * @param <K> key of the HashNode
     * @param <V> value of the HashNode
     */
    @SuppressWarnings("hiding")
    protected class HashNode <K, V> {
      K key;
      V value;
      HashNode<K, V> next; // next HashNode in the linked list
      
      /**
       * Default HashNode constructor (always going to be created with these 2 arguments)
       * @param key - key of the HashNode
       * @param value - value of the HashNode
       */
      HashNode(K key, V value) {
        this.key = key;
        this.value = value;
        next = null;
      }
    }
    
    /**
     * Default no-argument constructor for a HashTable. Initializes an empty table with size 20, 
     * and a load factor threshold of .8
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
      hashArray = (HashNode<K, V>[]) new HashNode[20];
      loadFactorThreshold = 0.8;
      numKeys = 0;
    }
    
    /**
     * Constructor for HashTable. Allows user to set the initial capacity of the table as well as
     * the load factor threshold
     * 
     * @param initialCapacity - initial capacity of the Hashtable
     * @param loadFactorThreshold - lft of the Hashtable
     */
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, double loadFactorThreshold) {
      hashArray = (HashNode<K, V>[]) new HashNode[initialCapacity];
      this.loadFactorThreshold = loadFactorThreshold;
      numKeys = 0;
    }
    
    /**
     * Add a key, value pair to the data structure and increases the number of keys
     * 
     * @param key - key of the HashNode being added
     * @param value - value of the HashNode being added
     * 
     * @throws IllegalNullKeyException if the key is null
     * @throws DuplicateKeyException if the key is already in the data structure
     */
    @Override
    public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {
      
      // check for a null key
      if (key == null)
        throw new IllegalNullKeyException();
      
      int hashIndex = Math.abs(key.hashCode()) % getCapacity(); // find hash index
      HashNode<K, V> head = hashArray[hashIndex]; // reference to the linked list at hash index
      
      // traverse through each node of the linked list to check for a duplicate key
      while (head != null) {
        if (head.key.equals(key))
          throw new DuplicateKeyException();
        head = head.next;
      }
      
      HashNode<K, V> newNode = new HashNode<K, V>(key, value); // create new HashNode
      head = hashArray[hashIndex]; // reset "head" reference to first node of linked list
      
      // add the new node to the head of the linked list and update numKeys
      newNode.next = head;
      hashArray[hashIndex] = newNode;
      numKeys++;
      
      // check if the hash table needs to be resized
      if (getLoadFactor() >= getLoadFactorThreshold()) 
        resizeHashTable(); 
    }
    
    /**
     * Resize the hash table when the load factor threshold is exceeded. Every HashNode of the 
     * current hash table must be rehashed to ensure they can still be located
     * 
     * @throws IllegalNullKeyException if key is null
     * @throws DuplicateKeyException if key already exists within the data structure
     */
    @SuppressWarnings("unchecked")
    private void resizeHashTable() throws IllegalNullKeyException, DuplicateKeyException {
      
      // reference to the old hash table
      HashNode<K, V>[] oldArray = (HashNode<K, V>[]) hashArray;
      
      // create and initialize a new, larger hash table
      int newCapacity = (2 * getCapacity()) + 1;
      hashArray = (HashNode<K, V>[]) new HashNode[newCapacity];
      numKeys = 0;
      
      // traverse through the old hash table, adding every HashNode to the new hash table
      for (int i = 0; i < oldArray.length; ++i) {
        HashNode<K, V> head = oldArray[i];
        while (head != null) {
          insert(head.key, head.value);
          head = head.next;
        }
      }
    }
    
    /**
     * Removs a key, value pair from the hash table and decrease the number of keys.
     * 
     * @param key - key of HashNode to be removed
     * @return true if removal is successful, false otherwise
     * 
     * @throws IllegalNullKeyException if key is null
     */
    @Override
    public boolean remove(K key) throws IllegalNullKeyException {
      
      // check for null key
      if (key == null)
        throw new IllegalNullKeyException();
      
      int hashIndex = Math.abs(key.hashCode()) % getCapacity(); // find hash index
      HashNode<K, V> head = hashArray[hashIndex]; // reference to the linked list at hash index
      
      // no linked list at the hash index, so the specified HashNode does not exist
      if (head == null)
        return false;
      
      // check if the specified key matches the first node in the linked list
      if (head.key.equals(key)) {
        hashArray[hashIndex] = head.next;
        numKeys--;
        return true;
      }
      
      // check if the specified key matches any other node in the linked list
      while (head.next != null) {
        if (head.next.key.equals(key)) {
          head.next = head.next.next;
          numKeys--;
          return true;
        } head = head.next;
      }
      return false; // reached end of the linked list, so the specified HashNode does not exist
    }
    
    /**
     * Return the value associated with the specified key
     * 
     * @param key - key of the HashNode being looked for
     * @return value of the specified HashNode
     * 
     * @throws IllegalNullKeyException if key is null
     * @throws KeyNotFoundException if key is not found
     */
    @Override
    public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
      
      // check for null key
      if (key == null)
        throw new IllegalNullKeyException();
      
      int hashIndex = Math.abs(key.hashCode()) % getCapacity(); // find hash index
      HashNode<K, V> head = hashArray[hashIndex]; // reference to the linked list at hash index
      
      // iterate through linked list at given index to find a match
      while (head != null) {
        if (head.key.equals(key)) {
          return head.value;
        } head = head.next;
      }
      // reached end of the linked list, so the specified HashNode does not exist. throw exception
      throw new KeyNotFoundException();
    }
    
    /**
     * Return the number of key,value pairs in the data structure
     * 
     * @return the number of key,value pairs in the data structure
     */
    @Override
    public int numKeys() {
      return numKeys;
    }
    
    /**
     * Return the load factor threshold that was passed into the constructor when creating the 
     * instance of the HashTable.
     * 
     * @return the load factor threshold
     */
    @Override
    public double getLoadFactorThreshold() {
      return loadFactorThreshold;
    }
    
    /**
     * Return the current load factor for this hash table
     * 
     * @return load factor = number of items / current table size
     */
    @Override
    public double getLoadFactor() {
      return ((double)numKeys / hashArray.length);
    }
  
    /**
     * Return the current Capacity (table size) of the hash table array.
     * 
     * @return current capacity of the HashTable
     */
    @Override
    public int getCapacity() {
      return hashArray.length;
    }
    
    /**
     * Specify which method is being used deal with hash table collisions
     * 
     * @return 5 to indicate I am implementing an array list of linked lists
     */
    @Override
    public int getCollisionResolution() {
      return 5;
    }

    /**
     * Returns the hash index for a HashNode witht he specified index. Only used for testing the 
     * HashTable class
     * 
     * @param key - key of hash index being looked up
     * @return hash index of specified HashNode
     */
    public int getHashIndex(K key) {
      return Math.abs(key.hashCode()) % getCapacity();
    }

    /**
     * Returns the HashNode at the specified hash index. Used for testing the HashTable class
     * 
     * @param hashIndex - index of the HashNode being returned
     * @return HashNode at specified index
     */
    public HashNode<K, V> getHashNodeAtIndex(int hashIndex) {
      return hashArray[hashIndex];
    }
}

