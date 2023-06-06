/**
 * Implementation of Quicksort on the buffered pool.
 * @author      Pranav Prabhu
 * @version     06/06/2023
 */

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.File;

public class QSort {
    private BufferPool buffer;
    private int diskSize;
    private int numOfBuffers;
    private final int BLOCK_SIZE = 4096;
    
    /**
     * QSort class constructor
     */
    public QSort(String fileName, int numBuffers) {
        // parameter: buffer pool
        File file = new File(fileName);
        this.buffer = new BufferPool(file, numBuffers);
        this.numOfBuffers = numBuffers;
    }
    
    int partition(Comparable[] A, int left, int right, Comparable pivot) {
        // move bounds inward until they meet
        while (left <= right) {
            while (A[left].compareTo(pivot) < 0) left++;
            while ((right >= left) && (A[right].compareTo(pivot) >= 0)) right--;
            if (right > left) swap(A, left, right); // swap out-of-place values
        }
        return left;    // return first position in right partition
      }

      int findpivot(Comparable[] A, int i, int j) {
          return (i + j) / 2;
      }
      
      public void swap(Comparable[] A, int i, int j) {
          Comparable temp = A[i];
          A[i] = A[j];
          A[j] = temp;
      }
      
      //public
      /*
       * 1. method to return a key given a byte array record
       *        - private short key (key comes from byte buffer; takes a byte array record and you wrap that record using wrap() builtin method in byte buffer. Then call the method getShort() which returns a key.)
       * 
       * 2. boolean checkDuplicates: returns true if all key values from positions from i to j are equal to the given pivot
       */

      void quicksort(Comparable[] A, int i, int j) {
          int pivotindex = findpivot(A, i, j);      // pick a pivot
          swap(A, pivotindex, j);                   // stick pivot at end
          
          // k will be the first position in the right subarray
          int k = partition(A, i, j-1, A[j]);
          swap(A, k, j);                            // put pivot in place
          if ((k-i) > 1) quicksort(A, i, k-1);      // sort left partition
          if ((j-k) > 1) quicksort(A, k+1, j);      // sort right partition
      }
      
      public short getKey(byte[] record) {
          ByteBuffer buffer = ByteBuffer.wrap(record);
          return buffer.getShort();
      }
}