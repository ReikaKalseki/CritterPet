package Reika.CritterPet.Biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.CritterPet.CritterClient;
import Reika.DragonAPI.Interfaces.ISBRH;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class RedBambooRenderer implements ISBRH {

	private final Random rand = new Random();
	private final Random randY = new Random();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		//rb.setRenderBoundsFromBlock(b);
		//rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
		int meta = world.getBlockMetadata(x, y, z);
		Tessellator v5 = Tessellator.instance;
		//double s = b.getBlockBoundsMaxX()-b.getBlockBoundsMinX();
		rand.setSeed(this.calcSeed(x, 0, z));
		rand.nextBoolean();
		randY.setSeed(this.calcSeed(x, y, z));
		randY.nextBoolean();

		v5.setBrightness(b.getMixedBrightnessForBlock(world, x, y, z));
		v5.setColorOpaque_I(0xffffff);

		int n = 1;
		if (rand.nextInt(4) == 0)
			n++;
		if (rand.nextInt(4) == 0)
			n++;
		boolean above = world.getBlock(x, y+1, z) == b;
		boolean below = world.getBlock(x, y-1, z) == b;
		int colorTop = ReikaColorAPI.mixColors(0x964335, 0xCC705B, randY.nextFloat());
		int colorBottom = below ? ReikaColorAPI.mixColors(0xE99396, 0xB06A6A, randY.nextFloat()) : ReikaColorAPI.mixColors(0xD6C8C7, 0xAF9199, randY.nextFloat());
		int dr = ReikaRandomHelper.getRandomBetween(0, 4, randY);
		for (int i = 0; i < dr; i++) {
			colorTop = ReikaColorAPI.getColorWithBrightnessMultiplier(colorTop, 0.8F);
			colorTop = ReikaColorAPI.getModifiedSat(colorTop, 1.2F);

			colorBottom = ReikaColorAPI.getColorWithBrightnessMultiplier(colorBottom, 0.9F);
			colorBottom = ReikaColorAPI.getModifiedSat(colorBottom, 1.1F);
		}
		for (int i = 0; i < n; i++) {
			double s = ReikaRandomHelper.getRandomBetween(0.0625, 0.125, rand);
			rb.setRenderBounds(0.5-s, 0, 0.5-s, 0.5+s, 1, 0.5+s);
			int br = ReikaRandomHelper.getRandomBetween(160, 255, rand);
			v5.setColorOpaque_I(ReikaColorAPI.GStoHex(br));
			double dx = ReikaRandomHelper.getRandomPlusMinus(0, 0.375, rand);
			double dz = ReikaRandomHelper.getRandomPlusMinus(0, 0.375, rand);
			if (!above) {
				v5.setColorOpaque_I(colorTop);
				rb.renderFaceYPos(b, x+dx, y, z+dz, rb.getBlockIcon(b, world, x, y, z, 1));
			}
			if (!below) {
				v5.setColorOpaque_I(colorBottom);
				rb.renderFaceYNeg(b, x+dx, y, z+dz, rb.getBlockIcon(b, world, x, y, z, 0));
			}

			IIcon bico = rb.getBlockIcon(b, world, x, y, z, ForgeDirection.WEST.ordinal());
			double d3 = bico.getInterpolatedU(rb.renderMinZ*16);
			double d4 = bico.getInterpolatedU(rb.renderMaxZ*16);
			double d5 = bico.getMinV();
			double d6 = bico.getMaxV();
			v5.setColorOpaque_I(colorTop);
			v5.addVertexWithUV(x+dx+0.5-s, y+1, z+dz+0.5+s, d4, d5);
			v5.addVertexWithUV(x+dx+0.5-s, y+1, z+dz+0.5-s, d3, d5);
			v5.setColorOpaque_I(colorBottom);
			v5.addVertexWithUV(x+dx+0.5-s, y, z+dz+0.5-s, d3, d6);
			v5.addVertexWithUV(x+dx+0.5-s, y, z+dz+0.5+s, d4, d6);
			//rb.renderFaceXNeg(b, x+dx, y, z+dz, rb.getBlockIcon(b, world, x, y, z, ForgeDirection.WEST.ordinal()));

			v5.setColorOpaque_I(colorBottom);
			v5.addVertexWithUV(x+dx+0.5+s, y, z+dz+0.5+s, d3, d6);
			v5.addVertexWithUV(x+dx+0.5+s, y, z+dz+0.5-s, d4, d6);
			v5.setColorOpaque_I(colorTop);
			v5.addVertexWithUV(x+dx+0.5+s, y+1, z+dz+0.5-s, d4, d5);
			v5.addVertexWithUV(x+dx+0.5+s, y+1, z+dz+0.5+s, d3, d5);

			d3 = bico.getInterpolatedU(rb.renderMinX*16);
			d4 = bico.getInterpolatedU(rb.renderMaxX*16);
			v5.setColorOpaque_I(colorTop);
			v5.addVertexWithUV(x+dx+0.5-s, y+1, z+dz+0.5-s, d4, d5);
			v5.addVertexWithUV(x+dx+0.5+s, y+1, z+dz+0.5-s, d3, d5);
			v5.setColorOpaque_I(colorBottom);
			v5.addVertexWithUV(x+dx+0.5+s, y, z+dz+0.5-s, d3, d6);
			v5.addVertexWithUV(x+dx+0.5-s, y, z+dz+0.5-s, d4, d6);

			v5.setColorOpaque_I(colorTop);
			v5.addVertexWithUV(x+dx+0.5+s, y+1, z+dz+0.5+s, d4, d5);
			v5.addVertexWithUV(x+dx+0.5-s, y+1, z+dz+0.5+s, d3, d5);
			v5.setColorOpaque_I(colorBottom);
			v5.addVertexWithUV(x+dx+0.5-s, y, z+dz+0.5+s, d3, d6);
			v5.addVertexWithUV(x+dx+0.5+s, y, z+dz+0.5+s, d4, d6);

			int n2 = 1;
			if (randY.nextInt(3) == 0)
				n2++;
			int nl = ReikaRandomHelper.getRandomBetween(0, 6, randY);
			for (int i0 = 0; i0 < nl; i0++) {
				double ang = ReikaRandomHelper.getRandomPlusMinus(i0*360D/nl, 90D/nl, randY);//Math.toRadians(randY.nextDouble()*360);
				double w = ReikaRandomHelper.getRandomPlusMinus(0.875, 0.125, randY);
				double h = ReikaRandomHelper.getRandomPlusMinus(1, 0.25, randY);
				double ax = w*Math.cos(ang);
				double az = w*Math.sin(ang);
				double dy = ReikaRandomHelper.getRandomPlusMinus(0.5, 0.25, randY);
				for (int i2 = 0; i2 < n2; i2++) {
					int leafColor = ReikaColorAPI.mixColors(0xFF9D9B, 0xAB3B47, randY.nextFloat());
					dr = ReikaRandomHelper.getRandomBetween(0, 3, randY);
					for (int di = 0; di < dr; di++) {
						leafColor = ReikaColorAPI.getColorWithBrightnessMultiplier(leafColor, 0.8F);
						leafColor = ReikaColorAPI.getModifiedSat(leafColor, 1.2F);
					}
					v5.setColorOpaque_I(leafColor);
					IIcon ico = BlockRedBamboo.getRandomLeaf(randY);
					float u = ico.getMinU();
					float v = ico.getMinV();
					float du = ico.getMaxU();
					float dv = ico.getMaxV();
					v5.addVertexWithUV(x+dx+0.5, 		y+dy-h/2, 	z+dz+0.5, 		u, v);
					v5.addVertexWithUV(x+dx+0.5+ax, 	y+dy-h/2, 	z+dz+0.5+az, 	du, v);
					v5.addVertexWithUV(x+dx+0.5+ax, 	y+dy+h/2, 	z+dz+0.5+az, 	du, dv);
					v5.addVertexWithUV(x+dx+0.5, 		y+dy+h/2, 	z+dz+0.5, 		u, dv);

					v5.addVertexWithUV(x+dx+0.5, 		y+dy+h/2, 	z+dz+0.5, 		u, dv);
					v5.addVertexWithUV(x+dx+0.5+ax, 	y+dy+h/2, 	z+dz+0.5+az, 	du, dv);
					v5.addVertexWithUV(x+dx+0.5+ax, 	y+dy-h/2, 	z+dz+0.5+az, 	du, v);
					v5.addVertexWithUV(x+dx+0.5, 		y+dy-h/2, 	z+dz+0.5, 		u, v);
				}
			}
		}
		return true;
	}

	private long calcSeed(int x, int y, int z) {
		return ChunkCoordIntPair.chunkXZ2Int(x, z) ^ y;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return CritterClient.bambooRender;
	}

}
