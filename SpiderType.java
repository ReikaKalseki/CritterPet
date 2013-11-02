/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.SpiderPet;

import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.SpiderPet.Entities.TameHeatScar;
import Reika.SpiderPet.Entities.TameHedge;
import Reika.SpiderPet.Entities.TameKing;
import Reika.SpiderPet.Entities.TameVanilla;

public enum SpiderType {

	VANILLA(TameVanilla.class, null, 16, 1, "/Reika/SpiderPet/Textures/vanilla.png", 0xffffff, 0x000000),
	HEATSCAR(TameHeatScar.class, ModList.NATURA, 50, 2, "/Reika/SpiderPet/Textures/heatscar.png", 0xaaaaaa, 0x666666),
	KING(TameKing.class, ModList.TWILIGHT, 30, 2, "/Reika/SpiderPet/Textures/king.png", 0x666666, 0xaaaaaa),
	HEDGE(TameHedge.class, ModList.TWILIGHT, 20, 1, "/Reika/SpiderPet/Textures/hedge.png", 0x000000, 0xffffff);

	public final Class entityClass;
	public final ModList sourceMod;
	public final int maxHealth;
	public final String texture;
	public final float size;
	public final int eggColor1;
	public final int eggColor2;

	public static final SpiderType[] spiderList = values();

	private SpiderType(Class c, ModList mod, int health, float size, String tex, int c1, int c2) {
		entityClass = c;
		sourceMod = mod;
		maxHealth = health;
		texture = tex;
		this.size = size;
		eggColor1 = c1;
		eggColor2 = c2;
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

}
