/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package CrackNetherSeed;


import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.ChunkSeeds;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import kaptainwutax.seedutils.mc.seed.WorldSeed;

import java.util.*;

public class DebrisCrack {
    public static final int BOUND = 112;

    public static void main(String[] args) {
        ChunkRand rand = new ChunkRand();


        Map<BPos, Integer> calls = new HashMap<>();
        ArrayList<BPos> bPos = new ArrayList<>();

        //this is the hashed worldseed of the nether dimension
        //It can be obtained by going to the nether with seed cracker mod
        long hashedSeed = WorldSeed.toHash(4161023112907999473L);

        //These are positions of single ore ancient debris veins that are spawning above y level 24
        //This is because there are two seperate decorators that spawn it
        //The other decorator generates from y 0 to that layer so we are sure we're looking at the right one
        //if we have a decorator above y 24
        //dont use veins that are near crimson and warped forests. they use a different salt.
        bPos.add(new BPos(9891,108,819));
        bPos.add(new BPos(9904,90,947));
        bPos.add(new BPos(9830,113,854));
        bPos.add(new BPos(9816,112,904));
        bPos.add(new BPos(9816,113,978));
        bPos.add(new BPos(9771,114,1013));
        bPos.add(new BPos(9721,116,972));
        bPos.add(new BPos(9678,116,967));
        bPos.add(new BPos(9592,111,950));
        bPos.add(new BPos(9586,106,1040));

        bPos.forEach(pos -> {
            int i = pos.getX() >> 4;
            int j = pos.getZ() >> 4;
            int k = pos.getY() - 8;

            calls.put(new BPos(i*16,0,j*16),k);
        });


        int decoBits = Long.numberOfTrailingZeros(BOUND);
        int bits = 16 + 17 + decoBits - 4;
        System.out.println("Starting search for hashed world seed [" + hashedSeed + "]" + " with " + calls.size() + " calls.");
        System.out.println("Looking for valid " + bits + " lower bits...");
        long start = System.nanoTime();

        List<Map.Entry<BPos, Integer>> entries = new ArrayList<>(calls.entrySet());

        List<Long> successes = new ArrayList<>();

        for(long lowerBits = 0; lowerBits < 1L << bits; lowerBits++) {
            boolean good = true;

            for(Map.Entry<BPos, Integer> entry: entries) {
                rand.setDecoratorSeed(lowerBits,entry.getKey().getX(), entry.getKey().getZ(),16,7,MCVersion.v1_16_2);
                rand.nextInt(16);
                rand.nextInt(16);
                int lo = rand.nextInt(112);
                if(lo % (1L << decoBits) != entry.getValue() % (1L << decoBits)) {
                    good = false;
                    break;
                }
            }

            if(good) {
                System.out.println("Found [" + lowerBits + "].");
                successes.add(lowerBits);
            }
        }

        System.out.println("Took " + (System.nanoTime() - start) / 1_000_000_000.0D + " seconds.");
        System.out.println("start looking for world seeds with "+successes.size()+" lower bit combinations");
        for(long success:successes) {
            for (long i = success; i < (1L<<48);i = i + (1L << 33)) {
                StructureSeed.toRandomWorldSeeds(i).forEach(s -> {
                    if(WorldSeed.toHash(s) == hashedSeed) {
                        System.out.println(s);
                    }
                });
            }
        }

        System.out.println("end");
    }
}
