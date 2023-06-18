import java.io.IOException;
import student.TestCase;

/**
 * @author      Pranav Prabhu
 * @version     06/14/2023
 */
public class QSortTest extends TestCase {
    /**
     * Test the sort() method in QSort class..
     */
    public void testSort() throws IOException {
        QSort qsort = new QSort("test.txt", 10);
        qsort.sort(-1, -5);
        qsort.sort(1, -5);
        qsort.sort(10, 5);
        assertNotNull(qsort);
    }
}