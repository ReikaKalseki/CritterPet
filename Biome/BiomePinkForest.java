package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.ChromatiCraft.API.Interfaces.DyeTreeBlocker;
import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Village gen is fucked
//gens with snow
public class BiomePinkForest extends BiomeGenBase implements DyeTreeBlocker {

	private final PinkTreeGenerator treeGen = new PinkTreeGenerator();
	private final GiantPinkTreeGenerator giantTreeGen = new GiantPinkTreeGenerator();
	private final RedBambooGenerator bambooGen = new RedBambooGenerator();

	private VoronoiNoiseGenerator sectionNoise;
	private SimplexNoiseGenerator sectionDisplacementNoiseX;
	private SimplexNoiseGenerator sectionDisplacementNoiseZ;
	private SimplexNoiseGenerator upthrustNoise;
	private SimplexNoiseGenerator streamsMiniCliffNoise;

	public BiomePinkForest(int id) {
		super(id);
		biomeName = "Pink Birch Forest";
		theBiomeDecorator.treesPerChunk = 6;
		theBiomeDecorator.grassPerChunk = 12;
		enableRain = true;
		enableSnow = true; //but melt the snow when it is daytime and sunny, and disallow all water freezing to ice

		temperature = 0.14F;
		rainfall = 0.65F;

		this.setHeight(new Height(1.75F, 0.25F));
	}

	@Override
	public float getFloatTemperature(int x, int y, int z) {
		return (float)ReikaMathLibrary.normalizeToBounds(super.getFloatTemperature(x, y, z), 0.1, 0.14, 0, 1);
	}

	@Override
	public boolean canSpawnLightningBolt() {
		return true;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new DecoratorPinkForest();
	}

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random rand) {
		return new WorldGenPinkGrass();
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random rand) {
		return rand.nextInt(4) == 0 ? giantTreeGen : treeGen;
	}

	@Override
	public void plantFlower(World world, Random rand, int x, int y, int z) {
		//plant bushes
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int x, int y, int z) { //less intense with Y
		//https://i.imgur.com/uj47T6U.jpg
		//https://i.imgur.com/ThGytyc.jpg
		//CD8988 to E0A29B to EDBFB1
		int min = 92;//64;
		int max = 112;//72;
		float f = 0;
		if (y >= max) {
			f = 1;
		}
		else if (y > min) {
			f = (y-min)/(float)(max-min);
		}
		return ReikaColorAPI.getModifiedHue(0xff0000, this.getSubBiome(Minecraft.getMinecraft().theWorld, x, z).ordinal()*120);//ReikaColorAPI.mixColors(0xEDBFB1, 0xCD8988, f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeFoliageColor(int x, int y, int z) { //less intense with Y
		//https://i.imgur.com/1Lu8tSU.jpg
		//https://i.imgur.com/bPN7Kqk.jpg
		//E95F84 through FA8FAA to FFB3D1
		int min = 108;//82;//64;
		int max = 150;//112;//128;
		float f = 0;
		if (y >= max) {
			f = 1;
		}
		else if (y > min) {
			f = (y-min)/(float)(max-min);
		}
		return ReikaColorAPI.mixColors(0xFFB3D1, 0xE95F84, f);
	}

	public BiomeSection getSubBiome(World world, int x, int z) {
		this.initNoise(world);
		double d = 14;//9;//12;//4;
		int avg = 0;
		int dd = 5;
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				double dx = x+i*dd+0.5+sectionDisplacementNoiseX.getValue(x+i*dd, z+k*dd)*d;
				double dz = z+k*dd+0.5+sectionDisplacementNoiseZ.getValue(x+i*dd, z+k*dd)*d;
				double val = sectionNoise.getValue(dx, dz);
				double n = ReikaMathLibrary.normalizeToBounds(val, 0, BiomeSection.list.length-0.001);
				int idx = MathHelper.floor_double(n);
				avg += idx;
			}
		}
		avg /= 9;
		return BiomeSection.list[avg];
	}

	private int getUpthrust(World world, int x, int z) {
		this.initNoise(world);
		return (int)Math.round(ReikaMathLibrary.normalizeToBounds(upthrustNoise.getValue(x, z), 0, 12));
	}

	private int getMiniCliffDelta(World world, int x, int z) {
		this.initNoise(world);
		double val = Math.abs(streamsMiniCliffNoise.getValue(x, z));
		double ret = Math.max(0, val-0.5)*12;
		return (int)Math.round(ret);
	}

	private void initNoise(World world) {
		if (sectionNoise == null || sectionNoise.seed != world.getSeed() || true) {
			sectionNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(world.getSeed()*3/2).setFrequency(1/72D);//.addOctave(3.6, 0.05).addOctave(8.5, 0.02).addOctave(13, 0.005);
			sectionNoise.randomFactor = 0.8;
			sectionNoise.clampEdge = false;
			double df = 1/12D;//1/12D;
			sectionDisplacementNoiseX = (SimplexNoiseGenerator)new SimplexNoiseGenerator(720+world.getSeed()/3).setFrequency(df);
			sectionDisplacementNoiseZ = (SimplexNoiseGenerator)new SimplexNoiseGenerator(720+world.getSeed()*3).setFrequency(df);
			upthrustNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(world.getSeed()/5).setFrequency(1/16D);
			streamsMiniCliffNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(world.getSeed()*12).setFrequency(1/12D);
		}
	}

	public static enum BiomeSection {
		FOREST,
		STREAMS,
		SWAMP;

		private static final BiomeSection[] list = values();
	}

}
