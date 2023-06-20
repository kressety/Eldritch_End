package elocindev.eldritch_end.worldgen;

import com.mojang.serialization.Codec;

import elocindev.eldritch_end.worldgen.feature.SurfaceConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BiomeSurfaceGeneration extends Feature<SurfaceConfig> {
    public BiomeSurfaceGeneration(Codec<SurfaceConfig> configCodec) {
      super(configCodec);
    }

    public boolean canPlace(StructureWorldAccess world, BlockPos position) {
        return world.getBlockState(position).getBlock() == Blocks.END_STONE;
    }
   
    @Override
    public boolean generate(FeatureContext<SurfaceConfig> context) {
        boolean generated = false;
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
    
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
    
        SurfaceConfig config = context.getConfig();
        BlockState blockState = Registry.BLOCK.get(config.blockID()).getDefaultState();
    
        int centerX = world.getRandom().nextInt(16);
        int centerZ = world.getRandom().nextInt(16);
        int radius = world.getRandom().nextInt(10) + 6;
        BlockPos center = chunkPos.getCenterAtY(origin.getY());
    
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                BlockPos pos = new BlockPos(origin.getX() + x, 0, origin.getZ() + z);

                double distance = center.getSquaredDistance(pos.getX(), center.getY(), pos.getZ());
                if (distance > radius * radius) {
                    continue;
                }
    
                BlockPos topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
    
                if (canPlace(world, topPos.down())) {
                    world.setBlockState(topPos.down(), blockState, 3);
                    generated = true;
                }
            }
        }
    
        return generated;
    }
    
}