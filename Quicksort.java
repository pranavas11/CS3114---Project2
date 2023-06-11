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

import java.io.IOException;

/**
 * Quicksort class contains the main method.
 * @author      Pranav Prabhu
 * @version     06/10/23
 */

public class Quicksort {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Error: Please provide the data file name, number of buffers, and stat file as arguments.");
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
        BufferPool.Stats.stats(dataFile, "statistics.txt", startTime, endTime);
    }
}