/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.SpiderPet.Registry.SpiderType;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SpiderClient extends SpiderCommon {

	public static final SpiderRenderer spider = new SpiderRenderer();

	private ItemSpriteSheetRenderer items = new ItemSpriteSheetRenderer(SpiderPet.instance, SpiderPet.class, "Textures/items.png");

	@Override
	public void registerSounds() {

	}

	@Override
	public void registerRenderers() {
		for (int i = 0; i < SpiderType.spiderList.length; i++) {
			SpiderType s = SpiderType.spiderList[i];
			RenderingRegistry.registerEntityRenderingHandler(s.entityClass, spider);
		}

		MinecraftForgeClient.registerItemRenderer(SpiderPet.tool.itemID, items);
	}

	// Override any other methods that need to be handled differently client side.

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
