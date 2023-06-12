import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;

/**
 * Quick Sort class
 * @author      Pranav Prabhu
 * @version     06/08/2023
 */
public class QSort {
    private BufferPool bp;
    private int numOfBuffers;
    private int size;
    
    /**
     * QSort class constructor
     * @param   fileName    data file name
     * @param   buffers     total buffers
     * @throws  IOException
     */
    public QSort(String fileName, int buffers) throws IOException {
        File file = new File(fileName);
        bp = new BufferPool(file, buffers);
        this.numOfBuffers = buffers;
        this.size = (bp.getNumOfBlocks() * 4096) / 4;
    }
    
    /**
     * Get the size of the buffer.
     * @return  size of buffer
     */
    public int getSize() {
        return this.size;
    }
    
    /**
     * Find pivot (middle value) for quicksort
     * @param   left    left value
     * @param   right   right value
     * @return  middle value of buffer
     * @throws IOException
     */
    public int pivot(int left, int right) throws IOException {
        return (left + right) / 2;
    }
    
    /**
     * Read the data from a given block using Big Endian
     * @throws IOException
     */
    public void readBlock() throws IOException {
        BufferPool.Buffer buffer;
        for (int i = 0; i < bp.getNumOfBlocks(); i++) {
            buffer = bp.getBuffer(i);
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.BIG_ENDIAN);
            
            for (int j = 0; j < 1024; j++) {
                byte[] currentRecord = buffer.getData(j);
                bb.put(currentRecord[0]);
                bb.put(currentRecord[1]);
                short key = bb.getShort(0);
                bb.clear();
                
                bb.put(currentRecord[2]);
                bb.put(currentRecord[3]);
                short value = bb.getShort(0);
                bb.clear();
                //System.out.println("bp.getKey(): " + bp.getKey(i * 1024 + j));
            }
        }
    }
    
    /**
     * Swap left and right buffer values for quick sort.
     * @param   low     left value
     * @param   high    right value
     * @throws  IOException
     */
    public void swap(int low, int high) throws IOException {
        // single block swap
        int lowBlock = low / 1024;
        int highBlock = high / 1024;
        int lowPos = low % 1024;
        int highPos = high % 1024;
        
        byte[] lowData = bp.getBuffer(lowBlock).getData(lowPos);
        byte[] highData = bp.getBuffer(highBlock).getData(highPos);
        bp.getBuffer(lowBlock).setData(highData, lowPos);
        bp.getBuffer(highBlock).setData(lowData, highPos);
    }
    
    /**
     * Paritition the buffer list for sorting.
     * @param   left    left index of array
     * @param   right   right index of array
     * @param   pivot   pivot point in array
     * @return  partitioned left
     * @throws IOException
     */
    public int partition(int left, int right, short pivot) throws IOException {
        while (left <= right) {
            while (bp.getKey(left) < pivot) {
                left++;
            }
            
            while (right >= left && bp.getKey(right) >= pivot) {
                right--;
            }
            
            if (right > left) {
                swap(left, right);
                left++;
                right--;
            }
        }
        
        return left;
    }
    
    /**
     * Sort function performs all the quick sort logic.
     * @param   i   left index
     * @param   j   right index
     * @throws IOException
     */
    public void sort(int i, int j) throws IOException {
        if (i >= 0 && j >= 0 && i < j) {
            int pivotIndex = pivot(i, j);
            swap(pivotIndex, j);
            int k = partition(i, j - 1, bp.getKey(j));
            swap(k, j);
            
            if ((k - i) > 1) {
                sort(i, k - 1);
            }
            
            if ((j - k) > 1) {
                sort(k + 1, j);
            }
        }
    }
    
    /**
     * Sort the entire file.
     * @throws IOException
     */
    public void quicksort() throws IOException {
        sort(0, getSize() - 1);
    }
    
    /**
     * Flush the buffer stream at the end.
     * @throws IOException
     */
    public void flush() throws IOException {
        bp.flush();
    }
}