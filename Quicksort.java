import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Quicksort {
    public static void main(String[] args) throws IOException {
        Scanner input = null;
        
        if (args.length != 3) {
            System.out.println("Error: Please provide the data file name, number of buffers, and stat file as arguments.");
            return;
        }
        
        try {
            input = new Scanner(new File(args[0]));
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred. No such file exists. Please try again!");
            return;
        }
        
        String dataFile = args[0];
        long numOfBuffers = Integer.parseInt(args[1]);
        String statFile = args[2];
    }
}