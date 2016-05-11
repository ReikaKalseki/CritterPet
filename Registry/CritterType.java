/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet.Registry;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import Reika.CritterPet.CritterClient;
import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Entities.TameFire;
import Reika.CritterPet.Entities.TameHeatScar;
import Reika.CritterPet.Entities.TameHedge;
import Reika.CritterPet.Entities.TameKing;
import Reika.CritterPet.Entities.TameMazeSlime;
import Reika.CritterPet.Entities.TameMistWolf;
import Reika.CritterPet.Entities.TameSilverfish;
import Reika.CritterPet.Entities.TameSlime;
import Reika.CritterPet.Entities.TameVanilla;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum CritterType {

	VANILLA("Spider", TameVanilla.class, null, 16, 1, "/Reika/CritterPet/Textures/vanilla.png", 0x775533, 0xcc0000, Items.rotten_flesh),
	HEATSCAR("Heatscar Spider", TameHeatScar.class, ModList.NATURA, 50, 3.25F, "/Reika/CritterPet/Textures/heatscar.png", 0x771100, 0x331100, Items.blaze_powder),
	KING("King Spider", TameKing.class, ModList.TWILIGHT, 60, 2, "/Reika/CritterPet/Textures/king.png", 0x774400, 0xffdd00, Items.gold_ingot),
	HEDGE("Hedge Spider", TameHedge.class, ModList.TWILIGHT, 20, 1, "/Reika/CritterPet/Textures/hedge.png", 0x053305, 0x229922, Items.melon),
	SLIME("Slime Beetle", TameSlime.class, ModList.TWILIGHT, 25, 0.8F, "", 0x78BF5A, 0x1A330F, Items.slime_ball),
	FIRE("Fire Beetle", TameFire.class, ModList.TWILIGHT, 25, 0.8F, "", 0xEC872C, 0x383540, Items.fire_charge),
	MAZE("Maze Slime", TameMazeSlime.class, ModList.TWILIGHT, 32, 3, "", 0x656F66, 0x859289, Items.brick),
	//WISP("Wisp", TameWisp.class, ModList.THAUMCRAFT, 22, 1, "", 0xFF19FB, 0xFFBDFD, Items.glowstone_dust),
	MISTWOLF("Mist Wolf", TameMistWolf.class, ModList.TWILIGHT, 32, 2F, "", 0x6D2C1F, 0xC1B064, Items.porkchop),
	SILVERFISH("Silverfish", TameSilverfish.class, null, 8, 0.25F, "/Reika/CritterPet/Textures/silverfish.png", 3158064, 7237230, Blocks.stonebrick);

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

	private CritterType(String name, Class c, ModList mod, int health, float size, String tex, int c1, int c2, Block i) {
		this(name, c, mod, health, size, tex, c1, c2, Item.getItemFromBlock(i));
	}

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

	@SideOnly(Side.CLIENT)
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
				//case WISP:
				//	Class c7 = Class.forName("thaumcraft.client.renderers.entity.RenderWisp");
				//	return (Render)c7.newInstance();
			case MISTWOLF:
				Class c6 = Class.forName("twilightforest.client.renderer.entity.RenderTFMistWolf");
				Constructor cd = c6.getConstructor(ModelBase.class, ModelBase.class, float.class);
				return (Render)cd.newInstance(new ModelWolf(), new ModelWolf(), 0.625F);
			case SILVERFISH:
				return ReikaEntityHelper.getEntityRenderer(EntitySilverfish.class);
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
