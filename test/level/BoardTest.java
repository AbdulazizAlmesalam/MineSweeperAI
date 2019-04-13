package level;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BoardTest {

    private BoardLevel BoardLevel;

    @Before
    public void setUp() throws Exception {
        this.BoardLevel = new BoardLevel(8,5);
    }

    @Test
    public void sizeTest() {
        assertEquals("Width", 8, this.BoardLevel.getWidth());
        assertEquals("Height", 5, this.BoardLevel.getHeight());
    }

    @Test
    public void bombCountTest() {
        assertEquals("Init bomb count", 0, this.BoardLevel.getcount());
        this.BoardLevel.addBomb(0,0);
        assertEquals("After adding one", 1, this.BoardLevel.getcount());
        this.BoardLevel.addBomb(1,0);
        this.BoardLevel.addBomb(7,4);
        this.BoardLevel.addBomb(2,4);
        this.BoardLevel.addBomb(7,1);
        assertEquals("After adding 5", 5, this.BoardLevel.getcount());
        this.BoardLevel.addBomb(7,4);
        this.BoardLevel.addBomb(1,0);
        this.BoardLevel.addBomb(7,1);
        assertEquals("Adding to same positions", 5, this.BoardLevel.getcount());
    }

    @Test
    public void containsTest() {
        for (int i = 0; i < this.BoardLevel.getWidth(); i++) {
            for (int j = 0; j < this.BoardLevel.getHeight(); j++) {
                assertFalse("No bomb: (" + i + "," + j + ")", this.BoardLevel.hasBomb(i, j));
            }
        }
        this.BoardLevel.addBomb(5,2);
        assertTrue("When contains", this.BoardLevel.hasBomb(5,2));
    }
}