/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import Reika.CritterPet.Entities.EntitySpiderBase;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class SpiderRenderer extends RenderSpider {

	@Override
	protected void preRenderCallback(EntityLivingBase elb, float par2) {
		if (elb instanceof EntitySpiderBase) {
			float sc = ((EntitySpiderBase) elb).getScaleFactor();
			GL11.glScalef(sc, sc, sc);

			((EntitySpiderBase) elb).bindTexture();
		}
	}

	@Override
	protected void func_147906_a(Entity par1EntityLivingBase, String par2Str, double par3, double par5, double par7, int par9)
	{
		if (MinecraftForgeClient.getRenderPass() != 1)
			return;

		EntitySpiderBase critter = (EntitySpiderBase)par1EntityLivingBase;

		double d3 = par1EntityLivingBase.getDistanceSqToEntity(renderManager.livingPlayer);

		if (d3 <= par9 * par9)
		{
			FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)par3 + 0.0F, (float)par5 + par1EntityLivingBase.height + 0.5F, (float)par7);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-f1, -f1, f1);
			GL11.glScaled(2, 2, 2);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			Tessellator tessellator = Tessellator.instance;
			CritterType type = critter.getBaseCritter();
			byte b0 = (byte)(-4-6*type.size);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tessellator.startDrawingQuads();

			String[] s = par2Str.split("'s ");

			int alpha = critter.isSitting() ? 127 : 192;
			int text_alpha = critter.isSitting() ? 127 : 255;

			text_alpha = text_alpha << 24;

			int j = fontrenderer.getStringWidth(par2Str) / 2;
			tessellator.setColorRGBA(0, 0, 0, alpha);
			int d = 0;
			if (s.length > 1) {
				d = 10;
				j = fontrenderer.getStringWidth(s[0]) / 2;
				j = Math.max(j, fontrenderer.getStringWidth(s[1]) / 2);
				j += 4;
				b0 -= 8;
			}
			tessellator.addVertex(-j - 1, -1 + b0, 0.0D);
			tessellator.addVertex(-j - 1, 8 + b0+d, 0.0D);
			tessellator.addVertex(j + 1, 8 + b0+d, 0.0D);
			tessellator.addVertex(j + 1, -1 + b0, 0.0D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Minecraft.getMinecraft().entityRenderer.disableLightmap(1);

			if (s.length > 1) {
				s[0] += "'s";
				fontrenderer.drawString(s[0], -fontrenderer.getStringWidth(s[0]) / 2, b0, 0x00ffffff+text_alpha);
				fontrenderer.drawString(s[1], -fontrenderer.getStringWidth(s[1]) / 2, b0+9, 0x00ffffff+text_alpha);
			}
			else
				fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, b0, 0x00ffffff+text_alpha);

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			//fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, b0, -1);
			Minecraft.getMinecraft().entityRenderer.enableLightmap(1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	@Override
	protected void renderModel(EntityLivingBase elb, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		if (MinecraftForgeClient.getRenderPass() == 0) {

			CritterType type = ((TamedMob)elb).getBaseCritter();
			if (elb instanceof EntitySpiderBase)
				((EntitySpiderBase)elb).bindTexture();

			if (!elb.isInvisible())
			{
				boolean sit = elb instanceof EntitySpiderBase && ((EntitySpiderBase)elb).isSitting();
				if (sit) {
					ModelSpider s = (ModelSpider)mainModel;
				}
				mainModel.render(elb, par2, par3, par4, par5, par6, par7);
			}
			else if (!elb.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer))
			{
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				BlendMode.DEFAULT.apply();
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
				mainModel.render(elb, par2, par3, par4, par5, par6, par7);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
			}
			else
			{
				mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, elb);
			}
		}
	}
}