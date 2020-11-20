package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CritterPet.Biome.BiomePinkForest.BiomeSection;
import Reika.DragonAPI.Instantiable.Worldgen.TerrainShaper;

public class PinkForestTerrainShaper extends TerrainShaper {

	//elevate rivers too

	@Override
	protected void generateColumn(World world, int x, int z, Random rand, BiomeGenBase biome) {
		BiomePinkForest bp = (BiomePinkForest)biome;
		//boolean river = bp == CritterPet.pinkriver;
		int river = bp.getRiverDelta(world, x, z);
		int up = bp.getUpthrust(world, x, z);
		int water = 0;
		BiomeSection sub = bp.getSubBiome(world, x, z);
		boolean thinDirt = false;
		if (sub == BiomeSection.STREAMS) {
			int delta = bp.getMiniCliffDelta(world, x, z);
			thinDirt |= delta > 0;
			up += delta;
		}
		else if (sub == BiomeSection.SWAMP) {
			int dep = bp.getSwampDepression(world, x, z);
			if (dep >= 2) {
				water = dep-1;
			}
			up -= dep*0;
		}
		int d = 4;
		int top = this.getTopNonAir(x, z);
		boolean edge = false;//world.getBiomeGenForCoords(x-d, z) != biome || world.getBiomeGenForCoords(x+d, z) != biome || world.getBiomeGenForCoords(x, z-d) != biome || world.getBiomeGenForCoords(x, z+d) != biome;
		if (edge) {
			/*
			int n = 0;
			int avg = 0;
			for (int dx = x-d; dx <= x+d; dx++) {
				for (int dz = z-d; dz <= z+d; dz++) {
					if (world.getBiomeGenForCoords(dx, dz) == biome) {
						boolean chunk = dx >> 4 == x >> 4 && dz >> 4 == z >> 4;
						int at = chunk ? this.getTopNonAir(dx, dz) : ReikaWorldHelper.getTopNonAirBlock(world, dx, dz, true);
						avg += at;
						n++;
					}
				}
			}
			avg /= n;
			 */
			int avg = Math.max((int)(90+6*bp.noise.borderHeightNoise.getValue(x, z)), top+3);
			up = avg+3-top;
		}
		Block at = this.getBlock(x, top, z);
		if (at != Blocks.water) {
			this.shiftVertical(x, z, up, Blocks.stone, 0);
			double f = top >= 90 ? 1 : (top-60)/30D;
			double road = f*bp.getRoadFactor(world, x, z);
			//if (road > 0 && (road >= 0.75 || rand2.nextDouble() < road*0.75)) {
			//if (road > 0 && road >= Math.abs(bp.noise.roadEdgeNoise.getValue(x, z)*0.75)) {
			if (water > 0 && false) {
				int y = this.getTopNonAir(x, z);
				for (int i = 0; i < water; i++) {
					this.setBlock(x, y+1+i, z, Blocks.water);
				}
			}
			int y = this.getTopNonAir(x, z);
			if (edge) {
				for (int i = 0; i < 32; i++)
					this.setBlock(x, y-i, z, Blocks.cobblestone);
			}
			else if (road > 0 && road >= 0.875 || rand.nextDouble() < road*0.6) { //was 0.875 and 0.75
				this.setBlock(x, y, z, Blocks.sand);
			}
			int dirtThickness = bp.getDirtThickness(world, x, z);
			if (thinDirt) {
				dirtThickness -= 2;
			}
			dirtThickness = Math.max(1, dirtThickness);
			for (int dt = 1; dt <= dirtThickness; dt++) {
				this.setBlock(x, y-dt, z, Blocks.dirt);
			}
			if (river > 0) {
				top = this.getTopNonAir(x, z);
				this.setBlock(x, top, z, Blocks.brick_block);
			}
		}
	}

	@Override
	protected boolean shouldClear() {
		return false;
	}

}
