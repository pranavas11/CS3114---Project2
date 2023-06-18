import java.util.Random;
import java.io.IOException;
import student.TestCase;

/**
 * @author  Pranav Prabhu
 * @version 06/10/2023
 */

public class QuicksortTest extends TestCase {
    /**
     * Sets up the tests that follow. In general, used for initialization
     */
    public void setUp() {
        // Nothing Here
    }
    
    /**
     * Test method 1 for the main() method.
     */
    public void testMain1() throws IOException {
        String[] args = {};
        Quicksort sort = new Quicksort();
        sort.main(args);
        assertNotNull(sort);
    }
    
    /**
     * Test method 2 for the main() method.
     */
    public void testMain2() throws IOException {
        String[] args = {"test"};
        Quicksort sort = new Quicksort();
        sort.main(args);
        assertNotNull(sort);
    }
    
    /**
     * Test method 3 for the main() method.
     */
    public void testMain3() throws IOException {
        String[] testArgs1 = {"1", "test1", "5"};
        Quicksort sort1 = new Quicksort();
        assertNotNull(sort1);
        sort1.generateFile(testArgs1);
        String[] args1 = {"test1", "5", "stat"};
        sort1.main(args1);
        
        String[] testArgs2 = {"2", "test2", "10"};
        Quicksort sort2 = new Quicksort();
        assertNotNull(sort2);
        sort2.generateFile(testArgs2);
        String[] args2 = {"test2", "10", "stat"};
        sort2.main(args2);
        
        String[] testArgs3 = {"1", "test3", "1"};
        Quicksort sort3 = new Quicksort();
        assertNotNull(sort3);
        sort3.generateFile(testArgs3);
        String[] args3 = {"test3", "1", "stat"};
        sort3.main(args3);
                        
        assertEquals("5", args1[1]);
        assertEquals("10", args2[1]);
        assertEquals("1", args3[1]);
    }

    /**
     * Test method for getRandomNum() method.
     */
    public void testGetRandomNum() throws IOException {
        Random random = new Random();
        random.setSeed(5);
        Quicksort sort = new Quicksort();
        assertNotNull(sort.getRandomNum(26));
    }
}