/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CritterPet.CritterPet;

public class DecoratorPinkForest extends BiomeDecorator {

	private final WorldGenRedBamboo redBambooGenerator = new WorldGenRedBamboo();

	public DecoratorPinkForest() {
		super();
	}

	@Override
	protected void genDecorations(BiomeGenBase biome) {
		super.genDecorations(biome);

		int j = chunk_X + randomGenerator.nextInt(16) + 8;
		int k = chunk_Z + randomGenerator.nextInt(16) + 8;

		BiomePinkForest forest = (BiomePinkForest)biome;
		redBambooGenerator.setFrequency(forest.getSubBiome(currentWorld, j, k));
		redBambooGenerator.generate(currentWorld, randomGenerator, j, currentWorld.getTopSolidOrLiquidBlock(j, k), k);
	}

	@Override
	public void decorateChunk(World par1World, Random par2Random, BiomeGenBase biome, int par3, int par4) {
		if (currentWorld != null) {
			CritterPet.logger.logError("Already decorating!!");
		}
		else {
			currentWorld = par1World;
			randomGenerator = par2Random;
			chunk_X = par3;
			chunk_Z = par4;
			this.genDecorations(biome);
			currentWorld = null;
			randomGenerator = null;
		}
	}


}
