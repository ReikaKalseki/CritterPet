/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Registry;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.item.Item;
import Reika.CritterPet.CritterClient;
import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Entities.TameFire;
import Reika.CritterPet.Entities.TameHeatScar;
import Reika.CritterPet.Entities.TameHedge;
import Reika.CritterPet.Entities.TameKing;
import Reika.CritterPet.Entities.TameMazeSlime;
import Reika.CritterPet.Entities.TameSlime;
import Reika.CritterPet.Entities.TameVanilla;
import Reika.CritterPet.Entities.TameWisp;
import Reika.DragonAPI.ModList;

public enum CritterType {

	VANILLA("Spider", TameVanilla.class, null, 16, 1, "/Reika/CritterPet/Textures/vanilla.png", 0x775533, 0xcc0000, Item.rottenFlesh),
	HEATSCAR("Heatscar Spider", TameHeatScar.class, ModList.NATURA, 50, 3.25F, "/Reika/CritterPet/Textures/heatscar.png", 0x771100, 0x331100, Item.blazePowder),
	KING("King Spider", TameKing.class, ModList.TWILIGHT, 60, 2, "/Reika/CritterPet/Textures/king.png", 0x774400, 0xffdd00, Item.ingotGold),
	HEDGE("Hedge Spider", TameHedge.class, ModList.TWILIGHT, 20, 1, "/Reika/CritterPet/Textures/hedge.png", 0x053305, 0x229922, Item.melon),
	SLIME("Slime Beetle", TameSlime.class, ModList.TWILIGHT, 25, 0.8F, "", 0x78BF5A, 0x1A330F, Item.slimeBall),
	FIRE("Fire Beetle", TameFire.class, ModList.TWILIGHT, 25, 0.8F, "", 0xEC872C, 0x383540, Item.fireballCharge),
	MAZE("Maze Slime", TameMazeSlime.class, ModList.TWILIGHT, 32, 3, "", 0x656F66, 0x859289, Item.brick),
	WISP("Wisp", TameWisp.class, ModList.THAUMCRAFT, 22, 1, "", 0xFF19FB, 0xFFBDFD, Item.glowstone);

	public final Class entityClass;
	public final ModList sourceMod;
	public final int maxHealth;
	public final String texture;
	public final float size;
	public final int eggColor1;
	public final int eggColor2;
	public final Item tamingItem;
	public final String name;
	private static final HashMap<CritterType, Integer> mappings = new HashMap();

	public static final CritterType[] critterList = values();

	private CritterType(String name, Class c, ModList mod, int health, float size, String tex, int c1, int c2, Item i) {
		entityClass = c;
		sourceMod = mod;
		maxHealth = health;
		texture = tex;
		this.size = size;
		eggColor1 = c1;
		eggColor2 = c2;
		tamingItem = i;
		this.name = name;
	}

	public int getEntityID() {
		return mappings.get(this);
	}

	public void initializeMapping(int id) {
		if (mappings.containsKey(this)) {
			int old = mappings.get(this);
			CritterPet.logger.logError("Attempted to reregister "+this+" with ID "+id+", when it was already registered to "+old);
		}
		mappings.put(this, id);
	}

	public boolean isAvailable() {
		return sourceMod != null ? sourceMod.isLoaded() : true;
	}

	public Render getRenderInstance() {
		try {
			switch(this) {
			case SLIME:
				Class c1 = Class.forName("twilightforest.client.renderer.entity.RenderTFSlimeBeetle");
				Class c2 = Class.forName("twilightforest.client.model.ModelTFSlimeBeetle");
				Constructor c = c1.getConstructor(ModelBase.class, float.class);
				return (Render)c.newInstance(c2.newInstance(), 0.625F);
			case FIRE:
				Class c3 = Class.forName("twilightforest.client.renderer.entity.RenderTFGenericLiving");
				Class c4 = Class.forName("twilightforest.client.model.ModelTFFireBeetle");
				Constructor cb = c3.getConstructor(ModelBase.class, float.class, String.class);
				return (Render)cb.newInstance(c4.newInstance(), 0.625F, "firebeetle.png");
			case MAZE:
				Class c5 = Class.forName("twilightforest.client.renderer.entity.RenderTFMazeSlime");
				Constructor cc = c5.getConstructor(ModelBase.class, ModelBase.class, float.class);
				return (Render)cc.newInstance(new ModelSlime(16), new ModelSlime(0), 0.625F);
			case WISP:
				Class c6 = Class.forName("thaumcraft.client.renderers.entity.RenderWisp");
				return (Render)c6.newInstance();
			default:
				return CritterClient.critter;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
