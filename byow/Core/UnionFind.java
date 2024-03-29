package byow.Core;

import java.util.ArrayList;
import java.util.Arrays;

public class UnionFind {
    private int[] structure;

    /* Creates a UnionFind data structure holding n vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind(int n) {
        structure = new int[n];
        Arrays.fill(structure, -1);
    }

    /* Throws an exception if v1 is not a valid index. */
    private void validate(int vertex) {
        if (vertex >= structure.length || vertex < 0) {
            throw new IllegalArgumentException(vertex + " is not a valid index");
        }
    }

    /* Returns the size of the set v1 belongs to. */
    private int sizeOf(int v1) {
        validate(v1);
        return -parent(find(v1));
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    private int parent(int v1) {
        validate(v1);
        return structure[v1];
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean connected(int v1, int v2) {
        validate(v1);
        validate(v2);
        return find(v1) == find(v2);
    }

    /* Connects two elements v1 and v2 together. v1 and v2 can be any valid
       elements, and a union-by-size heuristic is used. If the sizes of the sets
       are equal, tie break by connecting v1's root to v2's root. Unioning a
       vertex with itself or vertices that are already connected should not
       change the sets but may alter the internal structure of the data. */
    public void union(int v1, int v2) {
        validate(v1);
        validate(v2);
        int rootV1 = find(v1);
        int rootV2 = find(v2);
        if (rootV1 == rootV2) {
            return;
        }
        if (structure[rootV1] < structure[rootV2]) {
            //Connect 1 to 2
            structure[rootV1] += structure[rootV2];
            structure[rootV2] = rootV1;
        } else {
            //connect 2 to 1
            structure[rootV2] += structure[rootV1];
            structure[rootV1] = rootV2;
        }
    }

    public boolean allConnected() {
        return sizeOf(0) == structure.length;
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. */
    private int find(int vertex) {
        validate(vertex);
        if (parent(vertex) < 0) {
            return vertex;
        }
        ArrayList<Integer> toCompress = new ArrayList<>();
        int root = vertex;
        while (structure[root] >= 0) {
            toCompress.add(root);
            root = parent(root);
        }
        for (int i : toCompress) {
            structure[i] = root;
        }
        return root;
    }

}
