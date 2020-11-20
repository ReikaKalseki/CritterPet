package Reika.CritterPet.Biome;

import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;

public class PinkForestNoiseData {

	public final long seed;

	final VoronoiNoiseGenerator sectionNoise;
	final SimplexNoiseGenerator upthrustNoise;
	final SimplexNoiseGenerator streamsMiniCliffNoise;
	final SimplexNoiseGenerator swampDepressionNoise;
	final SimplexNoiseGenerator roadNoise;
	//final SimplexNoiseGenerator roadEdgeNoise;
	final SimplexNoiseGenerator borderHeightNoise;
	final SimplexNoiseGenerator dirtThicknessNoise;
	//final SimplexNoiseGenerator riverNoise;
	final VoronoiNoiseGenerator riverNoise;

	PinkForestNoiseData(long s) {
		seed = s;
		sectionNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(seed*3/2).setFrequency(1/72D);//.addOctave(3.6, 0.05).addOctave(8.5, 0.02).addOctave(13, 0.005);
		sectionNoise.randomFactor = 0.8;
		sectionNoise.clampEdge = false;
		double df = 1/15D;//1/12D;//1/12D;
		double d = 13;//14;//9;//12;//4;
		sectionNoise.setDisplacementSimple(720+seed/3, df, 720+seed*3, df, d);
		upthrustNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed/5).setFrequency(1/720D); //was 16 then 36 then 72, then 144, then 1200
		streamsMiniCliffNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed*12).setFrequency(1/24D); //was 12, then 32
		roadNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed+24390).setFrequency(1/21D).addOctave(1.5, 0.42, 82);
		//roadEdgeNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed+87456).setFrequency(1/1D).addOctave(2.2, 0.19, 23);
		swampDepressionNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed-34589).setFrequency(1/40D).addOctave(3.1, 0.28, 22);
		borderHeightNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed-23897).setFrequency(1/64D);
		dirtThicknessNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed-87456).setFrequency(1/16D);
		//riverNoise = (SimplexNoiseGenerator)new SimplexNoiseGenerator(seed+345987).setFrequency(1/270D).addOctave(1.3, 0.37, 12);
		riverNoise = (VoronoiNoiseGenerator)new VoronoiNoiseGenerator(seed*51+283471123).setFrequency(1/180D); //was 150
		riverNoise.randomFactor = 0.9;
		riverNoise.calculateDistance = false;
		df = 1/50D*5;
		//riverNoise.setDisplacementSimple(78337221-seed/7, df, 245356-seed*7, df, 2*0+0.8);
	}

}
