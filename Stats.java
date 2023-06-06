/**
 * Stats class that prints out buffer statistics.
 * @author      Pranav Prabhu
 * @version     06/06/2023
 *
 */
public class Stats {
    private String file;
    private long cacheHits;
    private int diskReads;
    private int diskWrites;
    private long time;
    
    /**
     * Stats class constructor
     */
    public Stats() {
        this.file = "";
        this.cacheHits = 0;
        this.diskReads = 0;
        this.diskWrites = 0;
        this.time = 0;
    }
    
    public String getFile() {
        return this.file;
    }
    
    public long getCacheHits() {
        return this.getCacheHits();
    }
    
    public int getDiskReads() {
        return this.diskReads;
    }
    
    public int getDiskWrites() {
        return this.diskWrites;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public String stats() {
        String fileOutput = "\nSort on " + file;
        String cacheOutput = "\nCache Hits: " + cacheHits;
        String readOutput = "\nDisk Reads: " + diskReads;
        String writeOutput = "\nDisk Writes: " + diskWrites;
        String timeOutput = "\nTime is " + time;
        String statOutput = fileOutput + cacheOutput + readOutput + writeOutput + timeOutput;
        return statOutput;
    }
    
    /*
     * Sort on input10b.dat
     * Cache Hits: 314938
     * Disk Reads: 43
     * Disk Writes: 43
     * Time is 43
     */
}