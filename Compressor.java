import java.util.*;
import java.io.*;

/*
 * Compressor
 *
 * Uses Huffmann Encoding to compress and decompress a given file.
 *
 * @author Caroline Tornquist, Dartmouth CS 10, Oct 2019 edited June 2020
 */


public class Compressor {
    HTree tree;                                                 //final huffmann tree
    Map<Character, Integer> frequencyMap;                       //maps characters to their frequencies
    Map<Character, String> codeMap;                             //maps character to their binary codes
    PriorityQueue<HTree> queue;                                 //queue of huffmann trees
    BufferedReader input;                                       //for reading input file
    BufferedWriter output;                                      //for writing to output file
    BufferedBitReader bitInput;                                 //reading individual bits
    BufferedBitWriter bitOutput;                                //writing individual bits
    boolean debugFlag = false;                                  //printing debugging statements
    static String file = "USConstitution.txt";                  //update to absolute pathname

    /*
     * Constructor. Opens files for reading/writing.
     */
    public Compressor(String filename) {
        frequencyMap = new TreeMap<>();
        codeMap = new TreeMap<>();
        queue = new PriorityQueue<HTree>();

        //creating buffered reader to read input
        try {
            input = new BufferedReader(new FileReader(filename));
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Unable to open input file");
            return;
        }

        //creating buffer writer to write output file
        try {
            output = new BufferedWriter(new FileWriter(changeFilenameDecompress(filename)));
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to open output file");
            return;
        }

        //opening bit writer
        try {
            bitOutput = new BufferedBitWriter(changeFilenameCompress(filename));
        } catch (Exception e) {
            System.out.println("Unable to open bit writer");
            return;
        }

        if (input != null && output != null && bitOutput != null) {
            generateMap();
            generatePQ();
            generateHuffmann();
            generateCodeMap();
            compress(file);
            decompress(file);
            closeEverything();
        }
    }

    /*
     * Initialize a map with each character from the input file as keys and their frequencies values.
     *
     * Assumes the frequency map is already instantiated.
     */
    public void generateMap(){
        int c;
        try {
            while ((c = input.read()) != -1) {  //while there are characters left
                //if character has been read before, add one to its freq
                if (frequencyMap.containsKey((char)c)) {
                    if (debugFlag) System.out.println("creating frequency table");
                    frequencyMap.put((char)c, frequencyMap.get((char)c) +1);
                }
                //else add new slot with freq 1
                else {
                    if (debugFlag) System.out.println("adding first frequency");
                    frequencyMap.put((char)c, 1);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to read input file");
        }

        //closing input buffer
        try {
            input.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to close input file");
        }

    }


    /*
     * Using the frequencies from the frequency map, initialize a priority queue of HTrees sorted by the value (frequency)
     * of the HTree.
     *
     * Assumes the priority queue is already instantiated.
     */
    public void generatePQ() {
        //for each character in the frequency table, create a new (one node) tree and add it to the priority queue
        for (Character key: frequencyMap.keySet()){
            HTree<Character, Integer> pqTree = new HTree<Character, Integer>(key, frequencyMap.get(key));
            queue.add(pqTree);
        }
    }

    /*
     * Generates a Huffmann Tree by combining the nodes from the priority queue. Higher frequency characters are at
     * the top and lower frequency characters at the bottom. Will end up with one tree which is set equal to the
     * tree instance variable.
     *
     * Assumes the priority queue is created and initialized properly.
     */
    public void generateHuffmann() {
        while (queue.size() != 1) {
            //pop off the first two (lowest freq) elements
            HTree<Character, Integer> tree1 = queue.remove();
            HTree<Character, Integer> tree2 = queue.remove();

            //find sum of frequency
            int total = tree1.getValue() + tree2.getValue();

            //creating new tree with left/right nodes as ones we popped off
            HTree<Character, Integer> T = new  HTree<Character, Integer>(null, total, tree1, tree2);

            //adding back to priority queue
            queue.add(T);
        }
        //setting huffmann tree to tree instance var
        tree = queue.remove();
        if (debugFlag) System.out.println(tree);
    }

    /**
     * Traverse the previously built Huffmann tree once, creating a Map that maps each character to its appropriate
     * code. Codes are created by starting with 0, and adding a 1 for each move to the right down the tree and
     * a 0 for each move to the left until you hit a leaf.
     *
     * Assumes the Huffmann tree is created and initialized properly.
     */
    public void generateCodeMap() {
        String code = "";
        code = helperCodeMap(code, tree);

        //if the tree has just one node, set its code to 0
        if (!tree.hasLeft() && !tree.hasRight()){
            codeMap.put((char)tree.getKey(), "0");
        }
        if (debugFlag) {System.out.println(codeMap);}
    }

    /**
     * Helper method to do recursion from generateCodeMap.
     *
     * @return code - the binary code for that character
     */
    public String helperCodeMap(String code, HTree tree) {
        //recurse on left child
        if(tree.hasLeft()){
            if(tree.getLeft().isLeaf()){
                codeMap.put((char)tree.getLeft().getKey(), code + "0");
            }
            else {
                helperCodeMap(code + "0", (tree.getLeft()));
            }
        }

        //recurse on right child
        if(tree.hasRight()) {
            if (tree.getRight().isLeaf()) {
                codeMap.put((char) tree.getRight().getKey(), code +"1");
            } else {
                helperCodeMap(code + "1", (tree.getRight()));
            }
        }

        return code;
    }


    /*
     * Reads each character from the input file, finds its code from the code map, then writes the code to the
     * compressed file using the bit output writer.
     *
     * Assumes each file is opened properly, and the codeMap is created properly.
     */
    public void compress(String filename){
        int c;
        String code;

        //opening new input buffer
        try {
            input = new BufferedReader(new FileReader(filename));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Unable to open input file");
        }

        try {
            //while there are characters to read
            while ((c = input.read()) != -1){
                if (debugFlag) System.out.println("reading input " +c+ " "  + (char)c);

                code = codeMap.get((char)c);
                char[] chars = code.toCharArray();                  //changing from String to array of chars

                if (debugFlag) System.out.println(chars);

                //for each character, if is 1 write true, if is 0 write false
                for(char charac: chars){
                    if (charac == '1'){
                        if (debugFlag) System.out.println("writing 1");
                        bitOutput.writeBit(true);
                    }
                    else if (charac == '0'){
                        if (debugFlag) System.out.println("writing 0");
                        bitOutput.writeBit(false);
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Unable to read from file");
        }

        //closing bit output file
        try {
            bitOutput.close();
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Unable to close bit writer");
        }
    }

    /**
     * Reads the compressed file one bit at a time, traverses the Huffmann Tree to get each character and write
     * the appropriate character to the output file.
     *
     * Assumes the output file is opened correctly, and that the Huffmann Tree has been initialized correctly.
     */
    public void decompress(String filename) {
        //opening bit reader
        try {
            bitInput = new BufferedBitReader(changeFilenameCompress(filename));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Unable to open bit reader");
        }

        HTree<Character, Integer> temp = tree;              //making a pointer to start of the tree

        while (bitInput.hasNext()){
            boolean bit = true;                             //default initialize to true

            //read the next bit
            try {
                bit = bitInput.readBit();
            }
            catch (Exception e) {
                System.err.println(e);
                System.err.println("Can't read next bit");
            }

            //writing to the file if at a leaf, otherwise continuing to traverse the tree
            if (!tree.hasLeft() && !tree.hasRight()) {      //if tree only has one node
                try{
                    output.write(temp.getKey());
                }
                catch (Exception e) {
                    System.err.println(e);
                    System.err.println("Can't write to output file");
                }
            }
            else {
                //if bit is 1, set temp to right child. if bit is 0, set temp to left
                if (bit) {
                    temp = temp.getRight();
                }
                else {
                    temp = temp.getLeft();
                }

                //if it's a leaf, write the associated character to the output file
                if (temp.isLeaf()) {
                    try {
                        output.write(temp.getKey());
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Can't write to output file");
                    }
                    temp = tree;                            //reset to start of tree to read next character
                }
            }
        }

        //closing bit input
        try {
            bitInput.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Can't close bit reader");
        }
    }

    /**
     * Adds "_compressed.txt" to the end of the filename
     *
     * @return newName - the new filename
     */
    private String changeFilenameCompress(String filename){
        int endIdx = filename.indexOf('.');
        String baseName = filename.substring(0,endIdx);
        String newName = baseName + "_compressed.txt";
        return newName;
    }

    /**
     * Adds "_decompressed.txt" to the end of the filename
     *
     * @return newName - the new filename
     */
    private String changeFilenameDecompress(String filename){
        int endIdx = filename.indexOf('.');
        String baseName = filename.substring(0,endIdx);
        String newName = baseName + "_decompressed.txt";
        return newName;
    }


    /*
     * Closes the input and output buffered readers
     */
    public void closeEverything () {
        try {
            input.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to close input file");
        }

        try {
            output.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to close output file");
        }
    }


    public static void main(String[] args) {
        Compressor compress = new Compressor(file);
    }
}

