package Reika.CritterPet.Renders;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import Reika.CritterPet.Entities.Mod.TameSpitter;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.Satisforestry.Satisforestry;
import Reika.Satisforestry.Blocks.BlockPowerSlug;
import Reika.Satisforestry.Blocks.BlockPowerSlug.TilePowerSlug;
import Reika.Satisforestry.Render.ModelPowerSlug;
import Reika.Satisforestry.Render.RenderSpitter;


public class RenderTameSpitter extends RenderSpitter {

	private final ModelPowerSlug model = new ModelPowerSlug();
	private TilePowerSlug tile = new TilePowerSlug();

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		super.doRender(e, par2, par4, par6, par8, ptick);

		if (e.riddenByEntity == null) {
			TameSpitter s = (TameSpitter)e;
			int slug = s.getSlugTier();
			if (slug >= 0) {
				ReikaTextureHelper.bindTexture(Satisforestry.class, "Textures/powerslug.png");
				//tile = new TilePowerSlug(slug);
				//tile.setNoSpawns();
				tile.worldObj = e.worldObj;
				tile.xCoord = MathHelper.floor_double(e.posX);
				tile.yCoord = MathHelper.floor_double(e.posY+1+e.getMountedYOffset());
				tile.zCoord = MathHelper.floor_double(e.posZ);
				//tile.updateEntity();
				int c = BlockPowerSlug.getColor(slug);
				GL11.glPushMatrix();
				GL11.glTranslated(par2, par4+1.6+s.getMountedYOffset(), par6);
				GL11.glRotated(180, 1, 0, 0);
				GL11.glRotated(e.rotationYaw, 0, 1, 0);
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				ReikaRenderHelper.disableEntityLighting();
				//GL11.glShadeModel(GL11.GL_SMOOTH);
				ReikaRenderHelper.disableLighting();
				GL11.glColor4f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F, 1F);
				model.renderAll(tile);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
				EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			}
		}
	}

	@Override
	protected void preRenderCallback(EntityLivingBase e, float ptick) {
		super.preRenderCallback(e, ptick);
	}

}
