package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.misc.LazyGet;
import com.teamtea.eclipticseasons.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;

import com.teamtea.eclipticseasons.EclipticSeasons;

import java.util.*;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164
// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {
    public static Map<ModelResourceLocation, BakedModel> models;
    public static
    LazyGet<BakedModel> snowOverlayLeaves =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyLeaves.getId(), "")));
    public static
    LazyGet<BakedModel> snowySlabBottom =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowySlab.getId(), "type=bottom,waterlogged=false")));
    public static
    LazyGet<BakedModel> snowOverlayBlock =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyBlock.getId(), "")));
    public static
    LazyGet<BakedModel> snowModel =
            LazyGet.of(() -> models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:snow_block"), "")));

    public static ModelResourceLocation snowy_fern = mrl("block/snowy_fern");
    public static ModelResourceLocation snowy_grass = mrl("block/snowy_grass");
    public static ModelResourceLocation snowy_large_fern_bottom = mrl("block/snowy_large_fern_bottom");
    public static ModelResourceLocation snowy_large_fern_top = mrl("block/snowy_large_fern_top");
    public static ModelResourceLocation snowy_tall_grass_bottom = mrl("block/snowy_tall_grass_bottom");
    public static ModelResourceLocation snowy_tall_grass_top = mrl("block/snowy_tall_grass_top");
    public static ModelResourceLocation snowy_dandelion = mrl("block/snowy_dandelion");
    public static ModelResourceLocation dandelion_top = mrl("block/dandelion_top");
    public static ModelResourceLocation overlay_2 = mrl("block/overlay_2");
    public static ModelResourceLocation snow_height2 = mrl("block/snow_height2");
    public static ModelResourceLocation snow_height2_top = mrl("block/snow_height2_top");
    public static ModelResourceLocation grass_flower = mrl("block/grass_flower");
    public static ModelResourceLocation butterfly1 = mrl("block/butterfly_blue");
    public static ModelResourceLocation butterfly2 = mrl("block/butterfly_magenta");
    public static ModelResourceLocation butterfly3 = mrl("block/butterfly_red");

    public static ModelResourceLocation mrl(String s) {
        return ModelResourceLocation.standalone(EclipticSeasons.rl(s));
    }


    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 * 5;


    public static boolean shouldCutoutMipped(BlockState state) {
        if (Minecraft.getInstance().level != null) {
            var onBlock = state.getBlock();
            if (!(onBlock instanceof FenceBlock)) {
                // if (onBlock instanceof SlabBlock || onBlock instanceof FarmBlock || onBlock instanceof DirtPathBlock || onBlock instanceof StairBlock
                //         || state.isSolidRender(Minecraft.getInstance().level, BlockPos.ZERO)) {
                //     return true;
                // }
                return true;
            }
        }
        return false;
    }

    public static void clearHeightMap() {
        updateLock=true;
        synchronized (ModelManager.RegionList) {
            ModelManager.RegionList.clear();
        }
        updateLock=false;
    }

    public static class ChunkHeightMap {
        private final int[][] matrix = new int[ChunkSize][ChunkSize];
        private final Object[][] lockArray = new Object[ChunkSize][ChunkSize];
        private final int x;
        private final int z;

        public ChunkHeightMap(int x, int z) {
            this.x = x;
            this.z = z;
            EclipticSeasons.logger(String.format("Create new Height Map with [%s, %s]", x, z));
            for (int i = 0; i < ChunkSize; i++) {
                for (int j = 0; j < ChunkSize; j++) {
                    matrix[i][j] = Integer.MIN_VALUE;
                    lockArray[i][j] = new Object();
                }
            }
            EclipticSeasons.logger(String.format("End create [%s, %s]", x, z));
        }

        public int getHeight(int x, int z) {
            x = getChunkValue(x);
            z = getChunkValue(z);
            return matrix[x][z];
        }

        public int getHeight(BlockPos pos) {
            return getHeight(pos.getX(), pos.getZ());
        }

        public int updateHeight(int x, int z, int y) {
            x = getChunkValue(x);
            z = getChunkValue(z);
            int old;
            synchronized (lockArray[x][z]) {
                old = matrix[x][z];
                matrix[x][z] = y;
            }
            return old;
        }

        public int updateHeight(BlockPos pos, int y) {
            return updateHeight(pos.getX(), pos.getZ(), y);
        }
    }

    // 获取chunk内部位置
    public static int getChunkValue(int i) {
        return i & (ChunkSizeLoc);
    }

    // 获取chunk位置
    public static int blockToSectionCoord(int i) {
        return i >> ChunkSizeAxis;
    }

    // TODO:内存更新，双链表+Hash，用LRU
    public static final ArrayList<ChunkHeightMap> RegionList = new ArrayList<>(4);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap = new IdentityHashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_1 = new IdentityHashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_GRASS = new IdentityHashMap<>(128);

    private static boolean updateLock;

    public static int getHeightOrUpdate(BlockPos pos, boolean shouldUpdate) {
        if (ClientConfig.Renderer.useVanillaCheck.get())
            return Integer.MIN_VALUE;
        int x = blockToSectionCoord(pos.getX());
        int z = blockToSectionCoord(pos.getZ());
        ChunkPos chunkPos = new ChunkPos(x, z);
        // var map = ChunkMap.getOrDefault(chunkPos, null);

        ChunkHeightMap map = null;
        // try{
        while (updateLock){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        // map = RegionList.stream()
        //         .filter(chunkHeightMap -> chunkHeightMap.x == x && chunkHeightMap.z == z)
        //         .findFirst()
        //         .orElse(null);
        // size add is dangerous
        for (int i = 0; i < RegionList.size(); i++) {
            var chunkHeightMap=RegionList.get(i);
                if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                    map = chunkHeightMap;
                    break;
                }
        }
        // }
        // catch (Exception e) {
        //     // e.printStackTrace();
        //     EclipticSeasons.logger(1223);
        //     // SimpleUtil.testTime(()->{});
        // }


        int h = 0;
        if (map != null) {
            h = map.getHeight(pos);
            if (h == Integer.MIN_VALUE || shouldUpdate) {
                var rh = Minecraft.getInstance().level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() - 1;
                if (ClientConfig.Renderer.underSnow.get()) {
                    var rmPos = new BlockPos.MutableBlockPos(pos.getX(), rh, pos.getZ());
                    var s = Minecraft.getInstance().level.getBlockState(rmPos);
                    while ((!(s.getBlock() instanceof LeavesBlock) && !s.isFaceSturdy(Minecraft.getInstance().level, rmPos, Direction.DOWN))) {
                        rh--;
                        s = Minecraft.getInstance().level.getBlockState(new BlockPos(pos.getX(), rh, pos.getZ()));
                        rmPos.move(Direction.DOWN, 1);
                    }
                }
                map.updateHeight(pos, rh);
                h = rh;
            }
            // return h;
        } else {

            updateLock = true;
            // ChunkMap.put(chunkPos, map);
            synchronized (RegionList) {
                boolean hasBuild = false;
                for (ChunkHeightMap chunkHeightMap : RegionList) {
                    if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                        hasBuild = true;
                        map = chunkHeightMap;
                        break;
                    }
                }
                if (!hasBuild) {
                    map = new ChunkHeightMap(x, z);
                    RegionList.add(map);
                }
            }
            updateLock = false;

            h = Minecraft.getInstance().level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() - 1;
            map.updateHeight(pos, h);
            // h = getHeightOrUpdate(pos, false);
            // return h;

        }
        return h;
    }

    public static final int FLAG_BLOCK = 1;
    public static final int FLAG_SLAB = 2;
    public static final int FLAG_STAIRS = 3;
    public static final int FLAG_LEAVES = 4;
    public static final int FLAG_GRASS = 5;
    public static final int FLAG_GRASS_LARGE = 501;
    public static final int FLAG_FARMLAND = 6;
    public static final List<Block> LowerPlant = List.of(Blocks.SHORT_GRASS, Blocks.FERN, Blocks.DANDELION);
    public static final List<Block> LARGE_GRASS = List.of(Blocks.TALL_GRASS, Blocks.LARGE_FERN);


    // 实际上这里之所以太慢还有个问题就是会一个方块访问七次
    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list) {
        // Minecraft.getInstance().level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,pos);
        // 不处理空列表，这代表着不处理这个方向
        // if (state.is(BlockTags.LEAVES) && !list.isEmpty()) {
        //     return List.of(new BakedQuadRetextured(list.get(0),
        //             ClientSetup.snowOverlayBlock.get().getQuads(null, Direction.UP, null).get(0).getSprite()));
        // }
        // if (true)return list;

        if (ClientConfig.Renderer.snowyWinter.get()
                && direction != Direction.DOWN
                && !list.isEmpty()) {

            var onBlock = state.getBlock();
            int flag = 0;
            if (onBlock instanceof LeavesBlock) {
                flag = FLAG_LEAVES;
            } else if ((state.isSolidRender(blockAndTintGetter, pos)
                    // state.isSolid()
                    || onBlock instanceof LeavesBlock
                    || (onBlock instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.TOP)
                    || (onBlock instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.TOP))) {
                flag = FLAG_BLOCK;
            } else if (onBlock instanceof SlabBlock) {
                flag = FLAG_SLAB;
            } else if (onBlock instanceof StairBlock) {
                flag = FLAG_STAIRS;
            } else if (LowerPlant.contains(onBlock)) {
                flag = FLAG_GRASS;
            } else if (LARGE_GRASS.contains(onBlock)) {
                flag = FLAG_GRASS_LARGE;
            } else if ((onBlock instanceof FarmBlock || onBlock instanceof DirtPathBlock) && direction == null) {
                flag = FLAG_FARMLAND;
            } else return list;

            int offset = 0;
            if (flag == FLAG_GRASS || flag == FLAG_GRASS_LARGE) {
                if (flag == FLAG_GRASS) {
                    offset = 1;
                }
                // 这里不忽略这个警告，因为后续会有优化
                else if (flag == FLAG_GRASS_LARGE) {
                    if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                        offset = 1;
                    } else {
                        offset = 2;
                    }
                }
            }


            boolean isLight = false;

            isLight = ClientConfig.Renderer.useVanillaCheck.get() && Minecraft.getInstance().level != null ?
                    Minecraft.getInstance().level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15
                    : getHeightOrUpdate(pos, false) == pos.getY() - offset;


            // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

            if (isLight) {
                if (onBlock != Blocks.SNOW_BLOCK
                        && shouldSnowAt(blockAndTintGetter, pos.below(offset), state, random, seed)) {
                    // DynamicLeavesBlock

                    boolean isFlowerAbove = false;
                    if ((flag == FLAG_BLOCK) && ClientConfig.Renderer.deeperSnow.get()) {
                        var bl = blockAndTintGetter.getBlockState(pos.above()).getBlock();
                        isFlowerAbove = bl instanceof FlowerBlock
                                || bl instanceof PinkPetalsBlock
                                || bl instanceof DoublePlantBlock
                                || bl instanceof SaplingBlock;

                        if (!isFlowerAbove) {
                            isFlowerAbove = random.nextInt(12) > 0;
                            // isFlowerAbove=true;
                        }
                    }
                    // isFlowerAbove=false;
                    var useMap = isFlowerAbove ? quadMap_1 : quadMap;
                    var cc = useMap.getOrDefault(list, null);
                    if (cc != null) {
                        return cc;
                    } else {
                        BakedModel snowModel = null;
                        BlockState snowState = null;
                        if (flag == FLAG_BLOCK) {
                            // snowModel = !isFlowerAbove ? snowOverlayBlock.get() : models.get(overlay_2);
                            // snowModel = snowOverlayBlock.get();
                            snowModel = models.get(BlockModelShaper.stateToModelLocation(EclipticSeasons.ModContents.snowyBlock.get().defaultBlockState()));
                        } else if (flag == FLAG_LEAVES) {
                            // snowModel = snowOverlayLeaves.get();
                            snowModel = models.get(BlockModelShaper.stateToModelLocation(EclipticSeasons.ModContents.snowyLeaves.get().defaultBlockState()));

                        } else if (flag == FLAG_SLAB) {
                            // snowModel = snowySlabBottom.get();
                            snowModel = models.get(BlockModelShaper.stateToModelLocation(EclipticSeasons.ModContents.snowySlab.get().defaultBlockState()));

                        } else if (flag == FLAG_STAIRS) {
                            snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                            // 楼梯的方向是无
                            snowModel = models.get(BlockModelShaper.stateToModelLocation(snowState));
                        } else if (flag == FLAG_GRASS) {
                            if (onBlock == Blocks.SHORT_GRASS) {
                                snowModel = models.get(snowy_grass);
                            } else if (onBlock == Blocks.FERN) {
                                snowModel = models.get(snowy_fern);
                            } else if (onBlock == Blocks.DANDELION) {
                                snowModel = models.get(snowy_dandelion);
                            } else snowModel = models.get(snowy_grass);
                        } else if (flag == FLAG_GRASS_LARGE) {
                            if (onBlock == Blocks.TALL_GRASS) {
                                snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                            } else if (onBlock == Blocks.LARGE_FERN) {
                                snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
                            } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                        } else if (flag == FLAG_FARMLAND) {
                            snowModel = models.get(snow_height2_top);
                            // snowModel = snowOverlayBlock.get();
                        }

                        if (snowModel != null) {
                            int size = list.size();
                            var snowList = snowModel.getQuads(snowState, direction, random);
                            ArrayList<BakedQuad> newList;

                            if (flag == FLAG_GRASS) {
                                newList = new ArrayList<>(snowList);
                            } else if (direction == Direction.UP) {
                                if (isFlowerAbove) {
                                    newList = new ArrayList<>();
                                    var layerState = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1);
                                    var layerBlock = models.get(BlockModelShaper.stateToModelLocation(layerState));
                                    layerBlock = models.get(snow_height2);

                                    for (Direction direction1 : List.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.UP)) {
                                        newList.addAll(layerBlock.getQuads(layerState, direction1, random));
                                    }
                                } else newList = new ArrayList<>(snowList);
                            } else {
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(list);
                                newList.addAll(snowList);
                            }

                            if (onBlock == Blocks.DANDELION) {
                                newList.addAll(models.get(dandelion_top).getQuads(null, null, null));
                            }

                            if (flag == FLAG_FARMLAND) {

                                for (Direction direction1 : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.UP)) {
                                    newList.addAll(snowModel.getQuads(null, direction1, random));
                                }
                            }

                            useMap.putIfAbsent(list, newList);
                            list = newList;


                        }
                    }
                } else if (direction == Direction.UP
                        && state.getBlock() instanceof GrassBlock
                        && random.nextInt(15) == 0
                ) {
                    var level = Minecraft.getInstance().level;
                    var solarTerm = SolarTerm.NONE;
                    int weight = 100;
                    if (level != null) {
                        solarTerm = SimpleUtil.getNowSolarTerm(level);
                        weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                    }
                    if (solarTerm.getSeason() == Season.SPRING
                            && random.nextInt(weight * 4) == 0
                            && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
                        var cc = quadMap_GRASS.getOrDefault(list, null);
                        if (cc != null) {
                            return cc;
                        } else {
                            BakedModel snowModel = models.get(grass_flower);
                            if (snowModel != null) {
                                int size = list.size();
                                var snowList = snowModel.getQuads(null, direction, null);
                                ArrayList<BakedQuad> newList;
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(list);
                                newList.addAll(snowList);
                                quadMap_GRASS.putIfAbsent(list, newList);
                                list = newList;
                            }
                        }
                    }
                }
            }


        }
        return list;
    }

    // TODO:感觉用随机表性能更高
    public static boolean shouldSnowAt(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random, long seed) {
        // Ecliptic.logger(SolarClientUtil.getSnowLayer() * 100, (seed&99));
        // Minecraft.getInstance().level.getBiome(pos);
        var biome = Minecraft.getInstance().level.getBiome(pos);
        // Minecraft.getInstance().level.getNoiseBiome()
        // SimpleUtil.testTime(()->{ Minecraft.getInstance().level.getBiome(pos);});
        if (WeatherManager.getSnowDepthAtBiome(Minecraft.getInstance().level, biome.value()) > Math.abs(seed % 100)) {
            return true;
        }

        return false;
        // >= random.nextInt(100));
    }

}
