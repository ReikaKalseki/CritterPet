package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CritterPet.Biome.BiomePinkForest.BiomeSection;
import Reika.DragonAPI.Instantiable.Worldgen.TerrainShaper;

public class PinkForestTerrainShaper extends TerrainShaper {

	@Override
	protected void generateColumn(World world, int x, int z, Random rand, BiomeGenBase biome) {
		BiomePinkForest bp = (BiomePinkForest)biome;
		int up = bp.getUpthrust(world, x, z);
		int water = 0;
		BiomeSection sub = bp.getSubBiome(world, x, z);
		if (sub == BiomeSection.STREAMS) {
			up += bp.getMiniCliffDelta(world, x, z);
		}
		else if (sub == BiomeSection.SWAMP) {
			int dep = bp.getSwampDepression(world, x, z);
			if (dep >= 2) {
				water = dep-1;
			}
			up -= dep;
		}
		up = 6;
		this.shiftVertical(x, z, up, Blocks.stone, 0);
		double road = bp.getRoadFactor(world, x, z);
		//if (road > 0 && (road >= 0.75 || rand2.nextDouble() < road*0.75)) {
		if (road > 0 && road >= Math.abs(bp.noise.roadEdgeNoise.getValue(x, z)))? {
			int y = this.getTopNonAir(x, z);
			this.setBlock(x, y, z, Blocks.sand);
		}
		if (water > 0 && false) {
			int top = this.getTopNonAir(x, z);
			for (int i = 0; i < water; i++) {
				this.setBlock(x, top+1+i, z, Blocks.water);
			}
		}
	}

	@Override
	protected boolean shouldClear() {
		return false;
	}

}
