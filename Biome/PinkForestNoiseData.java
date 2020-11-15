package Reika.CritterPet.Biome;

import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;

public class PinkForestNoiseData {

	public final long seed;

	final VoronoiNoiseGenerator sectionNoise;
	final SimplexNoiseGenerator sectionDisplacementNoiseX;
	final SimplexNoiseGenerator sectionDisplacementNoiseZ;
	final SimplexNoiseGenerator upthrustNoise;
	final SimplexNoiseGenerator streamsMiniCliffNoise;
	final SimplexNoiseGenerator swampDepressionNoise;
	final SimplexNoiseGenerator roadNoise;
	final SimplexNoiseGenerator roadEdgeNoise;

	PinkForestNoiseData(long s) {
		seed = s;
		sectionNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(seed*3/2).setFrequency(1/72D);//.addOctave(3.6, 0.05).addOctave(8.5, 0.02).addOctave(13, 0.005);
		sectionNoise.randomFactor = 0.8;
		sectionNoise.clampEdge = false;
		double df = 1/15D;//1/12D;//1/12D;
		sectionDisplacementNoiseX = (SimplexNoiseGenerator)new SimplexNoiseGenerator(720+seed/3).setFrequency(df);
		sectionDisplacementNoiseZ = (SimplexNoiseGenerator)new SimplexNoiseGenerator(720+seed*3).setFrequency(df);
		upthrustNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed/5).setFrequency(1/16D);
		streamsMiniCliffNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed*12).setFrequency(1/12D);
		roadNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed+24390).setFrequency(1/21D).addOctave(1.5, 0.42, 82);
		roadEdgeNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed+87456).setFrequency(1/3D).addOctave(2.2, 0.19, 23);
		swampDepressionNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed-34589).setFrequency(1/40D).addOctave(3.1, 0.28, 22);
	}

}
