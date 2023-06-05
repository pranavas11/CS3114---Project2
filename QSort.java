/**
 * 
 */

/**
 * @author pranavas11
 *
 */
public class QSort {
    /**
     * 
     */
    public QSort() {
        // parameter: buffer pool
        // TODO Auto-generated constructor stub
    }
    
    int partition(Comparable[] A, int left, int right, Comparable pivot) {
        while (left <= right) { // Move bounds inward until they meet
          while (A[left].compareTo(pivot) < 0) left++;
          while ((right >= left) && (A[right].compareTo(pivot) >= 0)) right--;
          if (right > left) swap(A, left, right); // Swap out-of-place values
        }
        return left;            // Return first position in right partition
      }

      int findpivot(Comparable[] A, int i, int j) { return (i+j)/2; }
      
      public void swap(Comparable[] A, int i, int j) {
          // implememnt swap
      }
      
      //public
      /*
       * 1. method to return a key given a byte array record
       *        - private short key (key comes from byte buffer; takes a byte array record and you wrap that record using wrap() builtin method in byte buffer. Then call the method getShort() which returns a key.)
       * 
       * 2. boolean checkDuplicates: returns true if all key values from positions from i to j are equal to the given pivot
       * 3. 
       */
      

      void quicksort(Comparable[] A, int i, int j) { // Quicksort
        int pivotindex = findpivot(A, i, j);  // Pick a pivot
        swap(A, pivotindex, j);               // Stick pivot at end
        // k will be the first position in the right subarray
        int k = partition(A, i, j-1, A[j]);
        swap(A, k, j);                        // Put pivot in place
        if ((k-i) > 1) quicksort(A, i, k-1);  // Sort left partition
        if ((j-k) > 1) quicksort(A, k+1, j);  // Sort right partition
      }
}