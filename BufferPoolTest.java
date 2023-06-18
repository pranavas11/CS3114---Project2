import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import student.TestCase;

/**
 * @author  Pranav Prabhu
 * @version 06/12/2023
 */
public class BufferPoolTest extends TestCase {
    /**
     * Sets up the tests that follow. In general, used for initialization
     */
    public void setUp() {
        // Nothing Here
    }
    
    /**
     * Test method 1 for getBuffer().
     * @throws IOException
     */
    public void testGetBuffer1() throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.writeInt(4096); // Write the number of blocks
        raf.write(new byte[4096]); // Write a dummy block of data
        raf.write(new byte[4096]); // Write another dummy block of data
        raf.close();
        
        BufferPool bp = new BufferPool(file, 3);
        BufferPool.Buffer b1 = bp.getBuffer(1);
        assertNotNull(b1);
        assertEquals(1, b1.getBlock());
        
        BufferPool.Buffer b2 = bp.getBuffer(2);
        assertNotNull(b2);
        assertEquals(2, b2.getBlock());
        
        BufferPool.Buffer b3 = bp.getBuffer(3);
        assertNotNull(b3);
        assertEquals(3, b3.getBlock());
        
        BufferPool.Buffer b4 = bp.getBuffer(3);
        assertNotNull(b4);
        assertEquals(3, b4.getBlock());
        
        BufferPool.Buffer b5 = bp.getBuffer(2);
        assertNotNull(b5);
        assertEquals(2, b5.getBlock());
        
        BufferPool.Buffer b6 = bp.getBuffer(1);
        assertNotNull(b6);
        assertEquals(1, b6.getBlock());
        
        file.delete();
    }
    
    /**
     * Test method 2 for getBuffer().
     * @throws IOException
     */
    public void testGetBuffer2() throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        // write the number of blocks (4 blocks x 4096 bytes each)
        raf.writeInt(16384);
        raf.write(new byte[4096]); // write a dummy block of data
        raf.write(new byte[4096]); // write another dummy block of data
        raf.write(new byte[4096]); // write another dummy block of data
        raf.write(new byte[4096]); // write another dummy block of data
        raf.close();
        
        BufferPool bp = new BufferPool(file, 3);
        BufferPool.Buffer buffer1 = bp.getBuffer(1);
        BufferPool.Buffer buffer2 = bp.getBuffer(2);
        BufferPool.Buffer buffer3 = bp.getBuffer(3);
        
        byte[] data = "Modified data".getBytes();
        buffer1.setData(data, 0); // set position 0
        buffer2.setData(data, 1); // set position 1
        buffer3.setData(data, 2); // set position 2
        // force a swap by requesting a buffer beyond the capacity of the pool
        BufferPool.Buffer buffer4 = bp.getBuffer(4);
        assertNotNull(buffer4);
        file.delete();
    }
    
    /**
     * Test method 3 for getBuffer().
     * @throws IOException
     */
    public void testGetBuffer3() throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        // write the number of blocks (1 block x 4096 bytes)
        raf.writeInt(4096);
        raf.write(new byte[4096]); // write a dummy block of data
        raf.write(new byte[4096]);
        raf.write(new byte[4096]);
        raf.write(new byte[4096]);
        raf.close();

        BufferPool bp = new BufferPool(file, 3);

        // invoke the getBuffer() method to make the first buffer clean
        BufferPool.Buffer buffer1 = bp.getBuffer(0);
        byte[] newData = {'E', 'D', 'C', 'B', 'A'};
                
        BufferPool.Buffer buffer2 = bp.getBuffer(1);
        buffer2.setData(newData, 1);
        buffer1 = bp.getBuffer(0);
        buffer2 = bp.getBuffer(2);
        
        bp.flush();
        
        // test for "if (curr.next != null)"
        buffer1 = bp.getBuffer(0);
        buffer1.setData(newData, 1);
        buffer1 = bp.getBuffer(1);
        buffer1 = bp.getBuffer(2);
        buffer1.setData(newData, 1);
        buffer1 = bp.getBuffer(3);

        assertNotNull(buffer1);
        assertNotNull(buffer2);
        file.delete();
    }
    
    /**
     * Test method 4 for getBuffer().
     * @throws IOException
     */
    public void testGetBuffer4() throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        // write the number of blocks (1 block x 4096 bytes)
        raf.writeInt(4096);
        raf.write(new byte[4096]); // write a dummy block of data
        raf.write(new byte[4096]);
        raf.close();
        
        BufferPool bp = new BufferPool(file, 1);
        BufferPool.Buffer buffer1 = bp.getBuffer(1);
        byte[] newData = {'E', 'D', 'C', 'B', 'A'};
        buffer1.setData(newData, 1);
        buffer1 = bp.getBuffer(0);
        
        assertNotNull(buffer1);
        file.delete();
    }
    
    /**
     * Test method for write() function in Buffer class.
     * @throws IOException
     */
    public void testWrite() throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        BufferPool bp = new BufferPool(file, 3);
        BufferPool.Buffer buffer = bp.getBuffer(0);
        buffer.write();

        byte[] testData = {0x01, 0x02, 0x03, 0x04};
        buffer.setData(testData, 0);
        buffer.write();
        
        buffer.read();
        assertNotNull(testData);
        raf.close();        
        file.delete();
    }
    
    /**
     * Test the flush method().
     * @throws IOException
     */
    public void testFlush()  throws IOException {
        File file = File.createTempFile("testfile", ".dat");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        BufferPool bp = new BufferPool(file, 3);
        bp.flush();
        bp.getBuffer(0);
        bp.getBuffer(1);
        bp.getBuffer(2);
        bp.getBuffer(3);
        assertNotNull(bp.getBuffer(5));
        bp.flush();
        bp.getBuffer(1);
        
        byte[] readData = new byte[4096];
        raf.seek(0);
        raf.read(readData, 0, 4096);
        
        assertNotNull(bp.getBuffer(0));
        assertNotNull(bp.getBuffer(3));
        raf.close();        
        file.delete();
    }
    
    /**
     * Test the stats() method in Stats class.
     * @throws IOException
     */
    public void testStats() throws IOException {
        BufferPool.Stats statistics = new BufferPool.Stats();
        statistics.stats("sample_input10a.dat", "stats.txt", 10, 5);
        assertNotNull(statistics);
    }
}