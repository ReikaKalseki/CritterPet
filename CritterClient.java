/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.CritterPet.Biome.RedBambooRenderer;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class CritterClient extends CritterCommon {

	public static final SpiderRenderer critter = new SpiderRenderer();

	private static final RedBambooRenderer bamboo = new RedBambooRenderer();

	private ItemSpriteSheetRenderer items = new ItemSpriteSheetRenderer(CritterPet.instance, CritterPet.class, "Textures/items.png");

	@Override
	public void registerSounds() {

	}

	@Override
	public void registerRenderers() {
		for (int i = 0; i < CritterType.critterList.length; i++) {
			CritterType s = CritterType.critterList[i];
			if (s.isAvailable()) {
				Render r = s.getRenderInstance();
				if (r != null)
					RenderingRegistry.registerEntityRenderingHandler(s.entityClass, r);
			}
		}

		MinecraftForgeClient.registerItemRenderer(CritterPet.tool, items);

		bambooRender = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(bambooRender, bamboo);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
