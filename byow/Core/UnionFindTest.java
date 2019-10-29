package byow.Core;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class UnionFindTest {

    @Test
    public void allConnectedTest() {
        UnionFind unionFind = new UnionFind(10);
        unionFind.union(0, 1);
        assertFalse(unionFind.allConnected());
        unionFind.union(1, 2);
        assertFalse(unionFind.allConnected());
    }
}
