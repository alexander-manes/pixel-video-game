package byow.Core;

import byow.Core.WorldGeneration.WorldGenerator;
import org.junit.Test;

public class WorldGeneratorTests {

    @Test
    public void worldTest() {
        for (int i = 0; i < 500; i++) {
            int height = (int) (Math.random() * 1000) + 20;
            int width = (int) (Math.random() * 1000) + 20;
            long seed = (long) (Math.random() * 854775806) + 10;

            WorldGenerator worldGenerator = new WorldGenerator(height, width, seed);
            worldGenerator.getWorld();
        }
    }


}
