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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class runs tests on HashTable.java to ensure correct functionality.
 * 
 * @author Robin
 */
public class HashTableTest {

    HashTable<Integer, String> test; // HashTable that will be used for each test in this class
    
    /**
     * Initializes a new HashTable with initial capacity = 20 and load factor threshold = .8 before 
     * each test executes
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
      test = new HashTable<>(); // initial capacity = 20, load factor threshold = .8
    }

    /**
     * Sets the HashTable to null after each test executes ot ensure none of the data overlaps 
     * between tests
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
      test = null;
    }
    
    /** 
     * Tests that a HashTable returns an integer code
     * indicating which collision resolution strategy 
     * is used.
     *
     * 1 OPEN ADDRESSING: linear probe
     * 2 OPEN ADDRESSING: quadratic probe
     * 3 OPEN ADDRESSING: double hashing
     * 4 CHAINED BUCKET: array list of array lists
     * 5 CHAINED BUCKET: array list of linked lists
     * 6 CHAINED BUCKET: array list of binary search trees
     * 7 CHAINED BUCKET: linked list of array lists
     * 8 CHAINED BUCKET: linked list of linked lists
     * 9 CHAINED BUCKET: linked list of of binary search trees
     */
    @Test
    public void test000_collision_scheme() {
      int scheme = test.getCollisionResolution();
      if (scheme < 1 || scheme > 9)
        fail("collision resolution must be indicated with 1-9");
    }
        
    /**
     * Tests that insert(null,null) throws IllegalNullKeyException
     */
    @Test
    public void test001_IllegalNullKey() {
      try {
        test.insert(null, null); // attempting to insert null key
        fail("should not be able to insert null key");
      } catch (IllegalNullKeyException e) { 
        // expected. do nothing
      } catch (Exception e) { // wrong exception thrown
        fail("insert null key should not throw exception " + e.getClass().getName());
      }
    }
    
    /**
     * Tests that inserting 2 nodes with the same key throws DuplicateKeyException
     */
    @Test
    public void test002_DuplicateKey() {
      try {
        test.insert(4, "hi");
        test.insert(4, "hi"); // attempting to insert duplicate key
        fail("should not be able to insert Duplicate key");
      } catch (DuplicateKeyException e) {
        // expected. do nothing
      } catch (Exception e) { // wrong exception thrown
        fail("insert duplicate key should not throw exception " + e.getClass().getName());
      }
    }

    /**
     * Tests that calling get on a non-existent node throws KeyNotFoundException
     */
    @Test
    public void test003_KeyNotFound() {
      try {
        test.insert(3, "hi");
        test.get(2); // atempting to remove a node that is not in the data structure
        fail("Attempting to remove a non-existent key does not throw any exception");
      } catch (KeyNotFoundException e) { 
        // expected. do nothing
      } catch (Exception e) { // wrong exception thrown
        fail("get non-existent key should not throw exception " +  e.getClass().getName());
      }
    }

    /**
     * Tests that inserting 2 HashNodes with the same index plots them both at the same index
     * in a linked list
     */
    @Test
    public void test004_HashNodes_with_same_index() {
      try {
        test.insert(4, "first");
        test.insert(24, "second");
      } catch (Exception e) { // exception incorrectly thrown
        fail("Inserting 2 integers with the same hashIndex should not throw exception " +  
            e.getClass().getName());
      }
      // checking numKeys was correctly updated
      assertEquals(2, test.numKeys(), "Incorrect number of keys in HashTables.");
      // checking head of linked list 
      assertEquals(24, (int)test.getHashNodeAtIndex(4).key,"Node at head of linked list should have "
          + "a key of 24");
      // checking second element of the linked list
      assertEquals(4, (int)test.getHashNodeAtIndex(4).next.key, "Second node in linked list should "
          + "have a key of 4");
    }

    /**
     * Tests that inserting 5 nodes then removing all 5 nodes correctly works
     */
    @Test
    public void test005_insert_5_nodes_remove_5() {
      try {
        //insert 5 nodes
        test.insert(1, "one");
        test.insert(2, "two");
        test.insert(3, "three");
        test.insert(4, "four");
        test.insert(5, "five");
        
        // check numKeys is now 5
        assertEquals(5, test.numKeys(), "numKeys should be 5 after inserting 5 HashNodes");
        
        // remove all 5 nodes
        test.remove(1);
        test.remove(2);
        test.remove(3);
        test.remove(4);
        test.remove(5);
        
        // check numKeys is now 0
        assertEquals(0, test.numKeys(), "numKeys should be 0 after removing all HashNodes");
        
      } catch (Exception e) { // exception incorrectly thrown
        fail("Inserting and removing 5 HashNodes should not throw exception " + e.getClass().getName());
      }
    }

    /**
     * Tests that HashTable resizes once load factor threshold is passed, and that the user
     * is still able to access all of the nodes that were inserted
     */
    @Test
    public void test006_resize_still_able_to_get_nodes() {
      try {
        for (int i = 0; i < 20; ++i) // insert 20 elements, causing hashtable to resize
          test.insert(i, "test");
        
        // check that numKeys and capacity have been correctly updated
        assertEquals(20, test.numKeys(), "numKeys was incorrectly updated");
        assertEquals(41, test.getCapacity(), "Hashtable capacity was not correctly increased");
        
        for (int i = 0; i < 20; ++i) // check that each node inserted is still reachable
          test.get(i); // if HashNode not found, KeyNotFoundException is thrown here
          
      } catch (Exception e) { // unexpected exception thrown
        fail("Calling get() on each HashNode in the HashTable after resing threw exception " +
            e.getClass().getName());
      }
    }

    /**
     * Tests that get() method returns the correct value for each key value
     */
    @Test
    public void test007_get_returns_correct_value() {
      try {
        // insert three elements
        test.insert(1, "one");
        test.insert(2, "two");
        test.insert(3, "three");
        test.insert(21, "twenty one"); // same hash index as first HashNode inserted
        
        // test that get() returns the correct value for each node
        assertEquals("one", test.get(1), "get(1) returned incorrect value");
        assertEquals("two", test.get(2), "get(2) returned incorrect value");
        assertEquals("three", test.get(3), "get(3) returned incorrect value");
        assertEquals("twenty one", test.get(21), "get(21) returned incorrect value");
        
      } catch (Exception e) { // unexpected exception thrown
        fail("Calling get() on 3 valid key values threw exception " + e.getClass().getName());
      }
    }

    /**
     * Tests that HashTable will still resize when all nodes inserted into the original HashTable 
     * share the same index
     */
    @Test
    public void test008_resize_when_all_nodes_have_same_index() {
      try {
        // insert 20 elements with same hashIndex, causing hashtable to resize
        for (int i = 0; i < 20; ++i) 
          test.insert(i + 20, "test");
        
        // check that numKeys and capacity have been correctly updated
        assertEquals(20, test.numKeys(), "numKeys was incorrectly updated");
        assertEquals(41, test.getCapacity(), "Hashtable capacity was not correctly increased");
        
        for (int i = 0; i < 20; ++i) // check that each node inserted is still reachable
          test.get(i + 20); // if HashNode not found, KeyNotFoundException is throen here
          
      } catch (Exception e) { // unexpected exception thrown
        fail("Calling get() on each HashNode in the HashTable after resing threw exception " +
            e.getClass().getName());
      }
    }
   
    /**
     * Tests that HashTable will resize more than once if necessary
     */
    @Test
    public void test009_resize_mulitple_times() {
      try {
        for (int i = 0; i < 20; ++i) // insert 20 elements, causing hash table to resize
          test.insert(i, "test");
        
        // check that numKeys and capacity have been correctly updated
        assertEquals(20, test.numKeys(), "numKeys was incorrectly updated");
        assertEquals(41, test.getCapacity(), "Hashtable capacity was not correctly increased");
        
        for (int i = 0; i < 41; ++i) // insert 41 elements, causing hash table to resize again
          test.insert(i + 50, "test"); 
        
        // check that numKeys and capacity have been correctly updated
        assertEquals(61, test.numKeys(), "numKeys was incorrectly updated");
        assertEquals(83, test.getCapacity(), "Hashtable capacity was not correctly increased");
          
      } catch (Exception e) { // unexpected exception thrown
        fail("Calling get() on each HashNode in the HashTable after resing threw exception " +
            e.getClass().getName());
      }
    }

    /**
     * Tests that once a node is removed, the get() method will no longer be able to reach that node
     */
    @Test
    public void test010_get_can_not_reach_removed_key() {
      try {
        // insert then remove a HashNode with key == 3
        test.insert(3, "hi");
        test.remove(3);
        
        test.get(3); // atempting to reach a node that was removed from the data structure
        fail("Attempting to remove a removed key does not throw any exception");
      } catch (KeyNotFoundException e) { 
        // expected. do nothing
      } catch (Exception e) { // wrong exception thrown
        fail("get() removed key should not throw exception " +  e.getClass().getName());
      }
    }
}
