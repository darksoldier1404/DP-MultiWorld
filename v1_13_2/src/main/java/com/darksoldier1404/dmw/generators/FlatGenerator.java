package com.darksoldier1404.dmw.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FlatGenerator extends ChunkGenerator {

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return Collections.emptyList();
    }

    private final int currentHeight;

    public FlatGenerator(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunk = createChunkData(world);
        generator.setScale(0.005D);
        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                chunk.setBlock(X, currentHeight + 3, Z, Material.GRASS_BLOCK);
                chunk.setBlock(X, currentHeight + 2, Z, Material.DIRT);
                for (int i = currentHeight + 1; i > 0; i--)
                    chunk.setBlock(X, i, Z, Material.STONE);
                chunk.setBlock(X, currentHeight, Z, Material.BEDROCK);
            }
        }
        return chunk;
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, currentHeight+4, 0);
    }
}

