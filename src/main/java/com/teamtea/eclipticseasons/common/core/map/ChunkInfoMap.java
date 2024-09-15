package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasonsMod;
import net.minecraft.core.BlockPos;

public class ChunkInfoMap {
    public static final int TYPE_BIOME = 1;
    public static final int TYPE_HEIGHT = 0;

    private final short[][] matrix = new short[MapChecker.ChunkSize][MapChecker.ChunkSize];
    private final short[][] biomes = new short[MapChecker.ChunkSize][MapChecker.ChunkSize];
    private final Object[][] lockArray = new Object[MapChecker.ChunkSize][MapChecker.ChunkSize];
    final int x;
    final int z;
    final short minY;

    public ChunkInfoMap(int x, int z, int minY) {
        this.minY = (short) minY;
        this.x = x;
        this.z = z;
        EclipticSeasonsMod.logger(String.format("Create new Height Map with [%s, %s]", x, z));
        for (int i = 0; i < MapChecker.ChunkSize; i++) {
            for (int j = 0; j < MapChecker.ChunkSize; j++) {
                biomes[i][j] = -1;
                matrix[i][j] = this.minY;
                lockArray[i][j] = new Object();
            }
        }
        EclipticSeasonsMod.logger(String.format("End create [%s, %s]", x, z));
    }

    public int getHeight(int x, int z) {
        x = MapChecker.getChunkValue(x);
        z = MapChecker.getChunkValue(z);
        return matrix[x][z];
    }

    public int getBiome(int x, int z) {
        x = MapChecker.getChunkValue(x);
        z = MapChecker.getChunkValue(z);
        return biomes[x][z];
    }

    public int getHeight(BlockPos pos) {
        return getHeight(pos.getX(), pos.getZ());
    }

    public int getBiome(BlockPos pos) {
        return getBiome(pos.getX(), pos.getZ());
    }

    public int updateHeight(int x, int z, int y) {
        x = MapChecker.getChunkValue(x);
        z = MapChecker.getChunkValue(z);
        int old;
        synchronized (lockArray[x][z]) {
            old = matrix[x][z];
            matrix[x][z] = (short) y;
        }
        return old;
    }

    public int updateBiome(int x, int z, int id) {
        x = MapChecker.getChunkValue(x);
        z = MapChecker.getChunkValue(z);
        int old;
        synchronized (lockArray[x][z]) {
            old = biomes[x][z];
            biomes[x][z] = (short) id;
        }
        return old;
    }

    public int updateBiome(BlockPos pos, int id) {
        return updateBiome(pos.getX(), pos.getZ(), id);
    }

    public int updateHeight(BlockPos pos, int y) {
        return updateHeight(pos.getX(), pos.getZ(), y);
    }


    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }


}
