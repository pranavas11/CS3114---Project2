/**
 * For this project, I implemented an external sorting algorithm for
 * binary data. The input data file will consist of many 4-byte records,
 * with each record consisting of two 2-byte (short) integer values in
 * the range 1 to 30,000. I sorted the file (in ascending order), using
 * a modified version of the Quicksort sorting algorithm.
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

//import java.io.IOException;
import java.io.*;
import java.util.*;

/**
 * Quicksort class contains the main method.
 * @author      Pranav Prabhu
 * @version     06/10/23
 */

public class Quicksort {
    /**
     * Main method
     * @param   args    CLI arguments
     * @throws  IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Error: Please provide the data"
                + " file name, number of buffers,"
                + "and stat file as arguments.");
            return;
        }
        
        // get the arguments from the command line
        String dataFile = args[0];
        int numOfBuffers = Integer.parseInt(args[1]);
        String statFile = args[2];
        
        // start recording time to perform quick sort
        long startTime = System.currentTimeMillis();
        QSort qsort = new QSort(dataFile, numOfBuffers);
        qsort.readBlock();
        qsort.quicksort();
        qsort.flush();
        long endTime = System.currentTimeMillis();      // end timer
        
        // append stats data to a text file
        BufferPool.Stats.stats(dataFile, statFile, startTime, endTime);
    }
    
    /**
     *  generate random long integer values for using generateFile() method
     * @param   num     a integer value
     * @return  long random integer
     */
    public int getRandomNum(int num) {
        Random value = new Random();
        int randomNum = Math.abs(value.nextInt()) % num;
        return randomNum;
    }
    
    /**
     * Generate random binary files for testing
     * @param   args    CLI arguments
     * @throws  IOException
     */
    public void generateFile(String[] args) throws IOException {
        short val;
        int option = Integer.parseInt(args[0]);
        DataOutputStream file = new
            DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(args[1])));
        int fileSize = Integer.parseInt(args[2]);
                
        // write ASCII character type values
        if (option == 1) {
            for (int i = 0; i < fileSize; i++) {
                for (int j = 0; j < 2048; j++) {
                    if ((j % 2) == 1) {
                        val = (short)(8224);
                    }
                    else {
                        val = (short)(getRandomNum(26) + 0x2041);
                    }
                    file.writeShort(val);
                }
            }
        }
        else {
            // write binary data to file
            for (int i = 0; i < fileSize; i++) {
                // max of short type is 2048
                for (int j = 0; j < 2048; j++) {
                    // generate random num and append val to binary file
                    val = (short)(getRandomNum(29999) + 1);
                    file.writeShort(val);
                }
            }
        }
                        
        file.flush();
        file.close();
    }
}