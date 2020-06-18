# Huffmann-Encoding
Note: BuffferedBitReader/Writer written by Chris Bailey Kellog, Dartmouth Professor of Computer Science

### Overview  
Huffmann Encoding is a method to do lossless compressing of text files. Each character gets a binary code, and more frequent characters get codes that are shorter than less frequent ones. This program implements this method using a Binary Tree with characters as leaves, and their codes represented as traverals to the left and right. The number in each internal node gives the frequency of its subtree.
An example of a Huffmann Tree:   
![Tree](https://www.cs.dartmouth.edu/~cs10/hws/PS-3/Huff-tree2.png)   

### Major Methods
This list does not include a few methods to change filenames and open/close file pointers. 
```java
/*
 * Initialize a map with each character from the input file as keys and their frequencies values.
 *
 * Assumes the frequency map is already instantiated.
 */
 public void generateMap();

/*
 * Using the frequencies from the frequency map, initialize a priority queue of HTrees sorted by the value (frequency)
 * of the HTree.
 *
 * Assumes the priority queue is already instantiated.
 */
 public void generatePQ();
 
/*
 * Generates a Huffmann Tree by combining the nodes from the priority queue. Higher frequency characters are at
 * the top and lower frequency characters at the bottom. Will end up with one tree which is set equal to t
 * tree instance variable.
 *
 * Assumes the priority queue is created and initialized properly.
 */
 public void generateHuffmann();
 
  /**
   * Traverse the previously built Huffmann tree once, creating a Map that maps each character to its appropriate
   * code. Codes are created by starting with 0, and adding a 1 for each move to the right down the tree and
   * a 0 for each move to the left until you hit a leaf.
   *    
   * Assumes the Huffmann tree is created and initialized properly.
   */
   public void generateCodeMap();
   
   /*
     * Reads each character from the input file, finds its code from the code map, then writes the code to the
     * compressed file using the bit output writer.
     *
     * Assumes each file is opened properly, and the codeMap is created properly.
     */
    public void compress(String filename);
    
    /**
     * Reads the compressed file one bit at a time, traverses the Huffmann Tree to get each character and write
     * the appropriate character to the output file.
     *
     * Assumes the output file is opened correctly, and that the Huffmann Tree has been initialized correctly.
     */
    public void decompress(String filename);
```
