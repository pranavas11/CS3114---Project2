import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

/**
 * Buffer pool class
 * @author      Pranav Prabhu
 * @version     06/08/2023
 */
public class BufferPool {
    private RandomAccessFile file;
    private int numOfBlocks;
    private Node start;
    private Node end;
    private final static int BLOCK_SIZE  = 4096;
    private final static int RECORD_SIZE = 4;
    
    /**
     * Stats class
     */
    public static class Stats {
        private static long cacheHits = 0;
        private static int diskReads = 0;
        private static int diskWrites = 0;
        
        /**
         * Write all statistics output text file.
         * @param   dataFile     file to perform sorting on
         * @param   statFileName output file
         * @param   startTime    timer starts
         * @param   endTime      timer ends
         * @throws  IOException
         */
        public static void stats(String dataFile, String statFileName,
            long startTime, long endTime) throws IOException {
            // write disk stats to a text file
            RandomAccessFile statsFile = new
                RandomAccessFile(statFileName, "rw");
            statsFile.seek(statsFile.length());
            statsFile.writeBytes("Sort on " + dataFile);
            statsFile.writeBytes("\nCache Hits: " + cacheHits);
            statsFile.writeBytes("\nDisk Reads: " + diskReads);
            statsFile.writeBytes("\nDisk Writes: " + diskWrites);
            statsFile.writeBytes("\nTime is " + (endTime - startTime) + "\n");
            statsFile.close();
        }
    }
    
    /**
     * Buffer class
     */
    public class Buffer {
        private RandomAccessFile file;
        private byte[] data;
        private int block;
        private int pos;
        private boolean dirtyFlag;
        
        /**
         * Buffer class constructor (create new buffer)
         * @param   diskFile    RandomAccessFile
         * @param   blockNum    block number
         */
        public Buffer(RandomAccessFile diskFile, int blockNum) {
            this.data = new byte[BLOCK_SIZE];
            this.file = diskFile;
            this.block = blockNum;
            this.pos = blockNum * BLOCK_SIZE;
            this.dirtyFlag = false;
        }
        
        /**
         * Set the data (after swapping) at current position
         * @param   newData     data value
         * @param   position    position in buffer
         */
        public void setData(byte[] newData, int position) {
            for (int i = 0; i < RECORD_SIZE; i++) {
                data[position * RECORD_SIZE + i] = newData[i];
            }
            this.dirtyFlag = true;
        }
        
        /**
         * Get the block.
         * @return  block
         */
        public int getBlock() {
            return this.block;
        }
        
        /**
         * Get data at the current position from buffer.
         * @param   position    position number
         * @return  array of data of a given range
         */
        public byte[] getData(int position) {
            // increment cache and return byte[] data of selected range
            Stats.cacheHits++;
            return Arrays.copyOfRange(data, position * RECORD_SIZE,
                position * RECORD_SIZE + RECORD_SIZE);
        }
        
        /**
         * Read data from the disk.
         * @throws IOException
         */
        public void read() throws IOException {
            // read data from disk at current position
            data = new byte[BLOCK_SIZE];
            file.seek(this.pos);
            file.read(data);
            Stats.diskReads++;
        }
        
        /**
         * Write data back to the disk.
         * @throws IOException
         */
        public void write() throws IOException {
            // write data to disk if buffer is dirty
            if (dirtyFlag) {
                file.seek(this.pos);
                file.write(data);
                dirtyFlag = false;
                Stats.diskWrites++;
            }
        }
    }
    
    /**
     * Node class (defines a doubly linked list) to store buffer data.
     */
    static class Node {
        private Node prev;
        private Node next;
        private Buffer buffer;
        
        /**
         * Node class constructor
         * @param   previous    previous pointer
         * @param   next        next pointer
         * @param   buffer      buffer item
         */
        public Node(Node previous, Node next, Buffer buffer) {
            this.prev = previous;
            this.next = next;
            this.buffer = buffer;
        }
    }
    
    /**
     * BufferPool class constructor
     * @param   file            RandomAccessFile
     * @param   bufferCount  max buffer count
     * @throws  IOException
     */
    public BufferPool(File file, int bufferCount) throws IOException {
        this.file = new RandomAccessFile(file, "rw");
        this.numOfBlocks = (int)(this.file.length() -
            RECORD_SIZE) / BLOCK_SIZE + 1;
        Node newNode = new Node(null, null, null);
        this.start = newNode;
        this.end = newNode;
        
        // create a doubly linked list of nodes for managing buffers
        for (int i = 1; i < bufferCount; i++) {
            this.end.next = new Node(this.end, null, null);
            this.end = this.end.next;
        }
    }
    
    /**
     * Get the number of blocks.
     * @return  number of blocks
     */
    public int getNumOfBlocks() {
        return this.numOfBlocks;
    }
    
    /**
     * Get values from a particular block inside buffer.
     * @param   block   block from buffer
     * @return  a buffer value
     * @throws  IOException
     */
    public Buffer getBuffer(int block) throws IOException {
        Node curr = start;
        
        // traverse the doubly linked list to find the
        // buffer with the specified block ID
        while (curr != null) {
            // check if the current node has a buffer
            // and its block number matches the given block
            if (curr.buffer != null && curr.buffer.getBlock() == block) {
                // if the current node is the start node, return its buffer
                if (curr == start) {
                    return curr.buffer;
                }
                else {
                    // reorder the list to move the current node to the start
                    curr.prev.next = curr.next;
                    if (curr.next != null) {
                        curr.next.prev = curr.prev;
                    }
                    else {
                        // end of the linked list
                        end = curr.prev;
                    }
                    
                    curr.prev = null;
                    curr.next = start;
                    start.prev = curr;
                    start = curr;
                    return curr.buffer; // return the buffer of the current node
                }
            }
            curr = curr.next;   // move to the next node
        }
        
        // if the buffer with the specified block is not found,
        // create a new buffer and read its contents
        Buffer newBuffer = new Buffer(this.file, block);
        newBuffer.read();
        curr = end;
        
        // traverse the list in reverse to find an empty
        // or non-dirty buffer node
        while (curr != null) {
            if (curr.buffer == null || !curr.buffer.dirtyFlag) {
                // assign the new buffer to the current node
                curr.buffer = newBuffer;
                
                // if the current node is the start node, return its buffer
                if (curr == start) {
                    return curr.buffer;
                }
                else {
                    // reorder the list to move the current node to the start
                    curr.prev.next = curr.next;
                    if (curr.next != null) {
                        curr.next.prev = curr.prev;
                    }
                    else {
                        // end of the linked list
                        end = curr.prev;
                    }
                    
                    curr.prev = null;
                    curr.next = start;
                    start.prev = curr;
                    start = curr;
                    return curr.buffer; // return the buffer of the current node
                }
            }
            curr = curr.prev;   // move to the previous node
        }
        
        // write the contents of the buffer at the end node
        // and assign the new buffer to it
        end.buffer.write();
        end.buffer = newBuffer;
        
        // if the end node is also the start node, return its buffer
        if (end == start) {
            return end.buffer;
        }
        else {
            // reorder the linked list to move the end node to
            // the start and return the buffer of the start node
            end.prev.next = null;
            end.next = start;
            start.prev = end;
            start = end;
            end = end.prev;
            start.prev = null;
            return start.buffer;
        }
    }
        
    // VERSION 2 (without dirtyFlag)
    /*public Buffer getBuffer2(int block) throws IOException {
        Node curr = start;
        
        while (curr != null) {
            if (curr.buffer != null && curr.buffer.getBlock() == block) {
                if (curr == start) {
                    return curr.buffer;
                }
                else {
                    curr.prev.next = curr.next;
                    if (curr.next != null) {
                        curr.next.prev = curr.prev;
                    }
                    else {
                        end = curr.prev;
                    }
                    
                    curr.prev = null;
                    curr.next = start;
                    start.prev = curr;
                    start = curr;
                    return curr.buffer;
                }
            }
            curr = curr.next;
        }
        
        Buffer newBuffer = new Buffer(this.file, block);
        newBuffer.read();
        end.buffer = newBuffer;
        
        if (end == start) {
            return end.buffer;
        }
        else {
            end.prev.next = null;
            end.next = start;
            start.prev = end;
            start = end;
            end = end.prev;
            start.prev = null;
            return start.buffer;
        }
    }*/
    
    /**
     * Get the key from the buffer.
     * @param   index   index in buffer array
     * @return  key value
     * @throws  IOException
     */
    public short getKey(int index) throws IOException {
        // get the key at the specified position of the block
        int block = (index * RECORD_SIZE) / BLOCK_SIZE;
        int pos = (index * RECORD_SIZE) % BLOCK_SIZE;
        short key = ByteBuffer.wrap(getBuffer(block).data).getShort(pos);
        return key;
    }
    
    /**
     * Flush the buffer.
     * @throws IOException
     */
    public void flush() throws IOException {
        // flush the entire buffer pool
        Node curr = start;
        
        while (curr != null) {
            // loop the linked list and write empty data
            if (curr.buffer != null) {
                curr.buffer.write();
            }
            curr = curr.next;
        }
    }
}