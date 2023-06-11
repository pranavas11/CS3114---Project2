/**
 * @author      Pranav Prabhu
 * @version     06/08/2023
 */

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.File;

public class BufferPool {
    private RandomAccessFile file;
    private int numOfBlocks;
    private Node start;
    private Node end;
    //private int counter;
    private int maxBufferCount;
    
    /**
     * Stats class
     * @author      Pranav Prabhu
     * @version     06/08/2023
     */
    static class Stats {
        private static String statFile;
        private static long cacheHits = 0;
        private static int diskReads = 0;
        private static int diskWrites = 0;
        private static long time = 0;
                                
        public static void stats(String dataFile, String statFileName, long startTime, long endTime) throws IOException {
            RandomAccessFile statsFile = new RandomAccessFile(statFileName, "rw");
            statsFile.seek(statsFile.length());
            statsFile.writeBytes("\nSort on " + dataFile);
            statsFile.writeBytes("\nCache Hits: " + cacheHits);
            statsFile.writeBytes("\nDisk Reads: " + diskReads);
            statsFile.writeBytes("\nDisk Writes: " + diskWrites);
            statsFile.writeBytes("\nTime is " + (endTime - startTime));
            statsFile.close();
        }
    }
    
    public class Buffer {
        private RandomAccessFile file;
        private byte[] data;
        private int block;
        private int pos;
        private boolean dirtyFlag;
        
        public Buffer(RandomAccessFile diskFile, int blockNum) {
            this.data = new byte[4096];
            this.file = diskFile;
            this.block = blockNum;
            this.pos = blockNum * 4096;
            this.dirtyFlag = false;
        }
        
        public void setData(byte[] newData, int pos) {
            for (int i = 0; i < 4; i++) {
                data[pos * 4 + i] = newData[i];
            }
            this.dirtyFlag = true;
        }
        
        public int getBlock() {
            return this.block;
        }
        
        public byte[] getData(int pos) {
            Stats.cacheHits++;
            return Arrays.copyOfRange(data, pos * 4, pos * 4 + 4);
        }
        
        public void read() throws IOException {
            data = new byte[4096];
            file.seek(this.pos);
            file.read(data);
            Stats.diskReads++;
        }
        
        public void write() throws IOException {
            if (dirtyFlag) {
                file.seek(this.pos);
                file.write(data);
                dirtyFlag = false;
                Stats.diskWrites++;
            }
        }
    }
    
    static class Node {
        private Node prev;
        private Node next;
        private Buffer buffer;
        
        public Node(Node previous, Node next, Buffer buffer) {
            this.prev = previous;
            this.next = next;
            this.buffer = buffer;
        }
    }
    
    /**
     * BufferPool class constructor
     * @throws IOException
     */
    public BufferPool(File file, int maxBufferCount) throws IOException {
        this.file = new RandomAccessFile(file, "rw");
        this.numOfBlocks = (int)(this.file.length() - 4) / 4096 + 1;
        //this.counter = 0;
        this.maxBufferCount = maxBufferCount;
        Node newNode = new Node(null, null, null);
        this.start = newNode;
        this.end = newNode;
        
        for (int i = 1; i < maxBufferCount; i++) {
            this.end.next = new Node(this.end, null, null);
            this.end = this.end.next;
        }
    }
            
    public int getNumOfBlocks() {
        return this.numOfBlocks;
    }
            
    public Buffer getBuffer(int block) throws IOException {
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

        curr = end;
        while (curr != null) {
            if (curr.buffer == null || !curr.buffer.dirtyFlag) {
                curr.buffer = newBuffer;
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
            curr = curr.prev;
        }
        end.buffer.write();
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
        
    public short getKey(int index) throws IOException {
        int block = (index * 4) / 4096;
        int pos = (index * 4) % 4096;
        short key = ByteBuffer.wrap(getBuffer(block).data).getShort(pos);
        return key;
    }
    
    public void flush() throws IOException {
        // flush the buffer pool
        Node curr = start;
        
        while (curr != null) {
            if (curr.buffer != null) {
                curr.buffer.write();
            }
            curr = curr.next;
        }
    }
}