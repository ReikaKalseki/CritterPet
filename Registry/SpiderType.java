/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet.Registry;

import java.util.HashMap;

import net.minecraft.item.Item;
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.SpiderPet.SpiderPet;
import Reika.SpiderPet.Entities.TameHeatScar;
import Reika.SpiderPet.Entities.TameHedge;
import Reika.SpiderPet.Entities.TameKing;
import Reika.SpiderPet.Entities.TameVanilla;

public enum SpiderType {

	VANILLA(TameVanilla.class, null, 16, 1, "/Reika/SpiderPet/Textures/vanilla.png", 0x775533, 0xcc0000, Item.rottenFlesh),
	HEATSCAR(TameHeatScar.class, ModList.NATURA, 100, 3.25F, "/Reika/SpiderPet/Textures/heatscar.png", 0x771100, 0x331100, Item.blazePowder),
	KING(TameKing.class, ModList.TWILIGHT, 60, 2, "/Reika/SpiderPet/Textures/king.png", 0x774400, 0xffdd00, Item.ingotGold),
	HEDGE(TameHedge.class, ModList.TWILIGHT, 20, 1, "/Reika/SpiderPet/Textures/hedge.png", 0x053305, 0x229922, Item.melon);

	public final Class entityClass;
	public final ModList sourceMod;
	public final int maxHealth;
	public final String texture;
	public final float size;
	public final int eggColor1;
	public final int eggColor2;
	public final Item tamingItem;
	private static final HashMap<SpiderType, Integer> mappings = new HashMap();

	public static final SpiderType[] spiderList = values();

	private SpiderType(Class c, ModList mod, int health, float size, String tex, int c1, int c2, Item i) {
		entityClass = c;
		sourceMod = mod;
		maxHealth = health;
		texture = tex;
		this.size = size;
		eggColor1 = c1;
		eggColor2 = c2;
		tamingItem = i;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	public int getEntityID() {
		return mappings.get(this);
	}

	public void initializeMapping(int id) {
		if (mappings.containsKey(this)) {
			int old = mappings.get(this);
			SpiderPet.logger.logError("Attempted to reregister "+this+" with ID "+id+", when it was already registered to "+old);
		}
		mappings.put(this, id);
	}

	@Override
	public String toString() {
		return this.getName()+" Spider";
	}

}
