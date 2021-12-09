/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import Reika.CritterPet.CritterClient;
import Reika.CritterPet.CritterPet;
import Reika.CritterPet.Entities.TameLavaSlime;
import Reika.CritterPet.Entities.TameSilverfish;
import Reika.CritterPet.Entities.TameSlime;
import Reika.CritterPet.Entities.TameVanilla;
import Reika.CritterPet.Entities.Mod.TameHeatScar;
import Reika.CritterPet.Entities.Mod.TameLumafly;
import Reika.CritterPet.Entities.Mod.TameSpitter;
import Reika.CritterPet.Entities.Mod.TameWispDummy;
import Reika.CritterPet.Entities.Mod.TF.TameFire;
import Reika.CritterPet.Entities.Mod.TF.TameHedge;
import Reika.CritterPet.Entities.Mod.TF.TameIceCore;
import Reika.CritterPet.Entities.Mod.TF.TameKing;
import Reika.CritterPet.Entities.Mod.TF.TameMazeSlime;
import Reika.CritterPet.Entities.Mod.TF.TameMinighast;
import Reika.CritterPet.Entities.Mod.TF.TameMistWolf;
import Reika.CritterPet.Entities.Mod.TF.TameSlimeBeetle;
import Reika.CritterPet.Interfaces.TamedMob;
import Reika.CritterPet.Renders.RenderCustomMagmaCube;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.Satisforestry.Entity.EntitySpitter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum CritterType {

	VANILLA("Spider",				TameVanilla.class,													CritterClass.SPIDER,	null,					16, 1,		"vanilla",		0x775533, 0xcc0000,	Items.rotten_flesh),
	HEATSCAR("Heatscar Spider",		TameHeatScar.class,													CritterClass.SPIDER,	ModList.NATURA,			50, 3.25F,	"heatscar",		0x771100, 0x331100,	Items.blaze_powder),
	KING("King Spider",				TameKing.class,														CritterClass.SPIDER,	ModList.TWILIGHT,		60, 2,		"king",			0x774400, 0xffdd00,	Items.gold_ingot),
	HEDGE("Hedge Spider",			TameHedge.class,													CritterClass.SPIDER,	ModList.TWILIGHT,		20, 1,		"hedge",		0x053305, 0x229922,	Items.melon),
	SLIMEBEETLE("Slime Beetle",		TameSlimeBeetle.class,												CritterClass.BEETLE,	ModList.TWILIGHT,		25, 0.8F,	"",				0x78BF5A, 0x1A330F, Items.slime_ball),
	FIREBEETLE("Fire Beetle",		TameFire.class,														CritterClass.BEETLE,	ModList.TWILIGHT,		25, 0.8F,	"",				0xEC872C, 0x383540, Items.fire_charge),
	MAZESLIME("Maze Slime",			TameMazeSlime.class,												CritterClass.SLIME,		ModList.TWILIGHT,		32, 3,		"",				0x656F66, 0x859289, Items.brick),
	WISP("Wisp",					"Reika.CritterPet.Entities.Mod.TameWisp",	TameWispDummy.class,	CritterClass.FLYING,	ModList.THAUMCRAFT,		22, 1,		"",				0xFF19FB, 0xFFBDFD, Items.glowstone_dust),
	MISTWOLF("Mist Wolf",			TameMistWolf.class,													CritterClass.WOLF,		ModList.TWILIGHT,		32, 2F,		"",				0x6D2C1F, 0xC1B064, Items.porkchop),
	SILVERFISH("Silverfish",		TameSilverfish.class,												CritterClass.BUG,		null,					8, 0.25F,	"silverfish",	3158064, 7237230,	Blocks.stonebrick),
	LAVASLIME("Lava Slime",			TameLavaSlime.class,												CritterClass.SLIME,		null,					32, 3,		"",				0x606020, 0xff6000, Items.magma_cream),
	MINIGHAST("Carminite Ghastling", TameMinighast.class,												CritterClass.FLYING,	ModList.TWILIGHT,		20, 1.5F,	"minighast",	0xf0f0f0, 0xB87878,	Items.redstone),
	SLIME("Slime",					TameSlime.class,													CritterClass.SLIME,		null,					32, 3F,		"",				0x416345, 0x57DB67,	Items.reeds),
	ICECORE("Ice Core", 			TameIceCore.class,													CritterClass.FLYING,	ModList.TWILIGHT,		20, 1,		"icecore",		0x0094FF, 0x00FFFF,	Items.snowball),
	LUMAFLY("Lumafly", 				TameLumafly.class,													CritterClass.FLYING,	ModList.CHROMATICRAFT,	12, 0.6F,	"",				0x8E4C3E, 0xFFB900,	Items.feather),
	SPITTER("Spitter", 				TameSpitter.class,													CritterClass.WOLF,		ModList.SATISFORESTRY,	20, 1F,		"",				0x9B8B53, 0xFFFD5A,	Items.gunpowder);

	public final CritterClass type;
	public final int classIndex;
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
	private static final HashMap<Item, CritterType> itemMappings = new HashMap();

	public static final CritterType[] critterList = values();

	private CritterType(String name, Class c, CritterClass cl, ModList mod, int health, float size, String tex, int c1, int c2, Block i) {
		this(name, c, cl, mod, health, size, tex, c1, c2, Item.getItemFromBlock(i));
	}

	private CritterType(String name, Class c, CritterClass cl, ModList mod, int health, float size, String tex, int c1, int c2, Item i) {
		this(name, null, c, cl, mod, health, size, tex, c1, c2, i);
	}

	private CritterType(String name, String c, Class cb, CritterClass cl, ModList mod, int health, float size, String tex, int c1, int c2, Item i) {
		try {
			entityClass = c != null && (mod == null || mod.isLoaded()) ? Class.forName(c) : cb;
		}
		catch (ClassNotFoundException e) {
			throw new RegistrationException(CritterPet.instance, "Could not find entity class for "+this.name()+"!", e);
		}
		sourceMod = mod;
		maxHealth = health;
		texture = tex != null ? "/Reika/CritterPet/Textures/"+tex+".png" : "";
		this.size = size;
		eggColor1 = c1;
		eggColor2 = c2;
		tamingItem = i;
		this.name = name;
		type = cl;
		classIndex = cl.getNextIndex();
	}

	public int getToolTextureIndex() {
		return type.getTextureRow()*16+classIndex;
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
		itemMappings.put(tamingItem, this);
	}

	public boolean isAvailable() {
		return sourceMod != null ? sourceMod.isLoaded() : true;
	}

	public TamedMob create(World world, Entity e) {
		try {
			TamedMob es = this.construct(world);
			((Entity)es).setLocationAndAngles(e.posX, e.posY, e.posZ, e.rotationYaw, e.rotationPitch);
			((EntityLivingBase)es).rotationYawHead = ((EntityLivingBase)e).rotationYawHead;
			this.specialCreate(e, es);
			return es;
		}
		catch (Exception e1) {
			return null;
		}
	}

	private void specialCreate(Entity src, TamedMob es) {
		switch(this) {
			case SPITTER:
				this.copySpitterType(src, es);
				break;
			default:
				break;
		}
	}

	@ModDependent(ModList.SATISFORESTRY)
	private void copySpitterType(Entity src, TamedMob es) {
		((TameSpitter)es).setSpitterType(((EntitySpitter)src).getSpitterType());
	}

	private TamedMob construct(World world) throws Exception {
		Constructor c = entityClass.getConstructor(World.class);
		return (TamedMob)c.newInstance(world);
	}

	@SideOnly(Side.CLIENT)
	public Render getRenderInstance() {
		try {
			switch(this) {
				case SLIMEBEETLE:
					Class c1 = Class.forName("twilightforest.client.renderer.entity.RenderTFSlimeBeetle");
					Class c2 = Class.forName("twilightforest.client.model.ModelTFSlimeBeetle");
					Constructor c = c1.getConstructor(ModelBase.class, float.class);
					return (Render)c.newInstance(c2.newInstance(), 0.625F);
				case FIREBEETLE:
					Class c3 = Class.forName("twilightforest.client.renderer.entity.RenderTFGenericLiving");
					Class c4 = Class.forName("twilightforest.client.model.ModelTFFireBeetle");
					Constructor cb = c3.getConstructor(ModelBase.class, float.class, String.class);
					return (Render)cb.newInstance(c4.newInstance(), 0.625F, "firebeetle.png");
				case MAZESLIME:
					Class c5 = Class.forName("twilightforest.client.renderer.entity.RenderTFMazeSlime");
					Constructor cc = c5.getConstructor(ModelBase.class, ModelBase.class, float.class);
					return (Render)cc.newInstance(new ModelSlime(16), new ModelSlime(0), 0.625F);
				case WISP:
					Class c7 = Class.forName("thaumcraft.client.renderers.entity.RenderWisp");
					return (Render)c7.newInstance();
				case MISTWOLF:
					Class c6 = Class.forName("twilightforest.client.renderer.entity.RenderTFMistWolf");
					Constructor cd = c6.getConstructor(ModelBase.class, ModelBase.class, float.class);
					return (Render)cd.newInstance(new ModelWolf(), new ModelWolf(), 0.625F);
				case SILVERFISH:
					return ReikaEntityHelper.getEntityRenderer(EntitySilverfish.class);
				case LAVASLIME:
					return new RenderCustomMagmaCube();
				case SLIME:
					return ReikaEntityHelper.getEntityRenderer(EntitySlime.class);
				case MINIGHAST:
					Class c8 = Class.forName("twilightforest.client.renderer.entity.RenderTFMiniGhast");
					Class c9 = Class.forName("twilightforest.client.model.ModelTFGhast");
					Constructor ccc = c8.getConstructor(ModelBase.class, float.class);
					return (Render)ccc.newInstance(c9.newInstance(), 0.625F);
				case ICECORE:
					Class c10 = Class.forName("twilightforest.client.renderer.entity.RenderTFIceShooter");
					return (Render)c10.newInstance();
				case LUMAFLY:
					Class c11 = Class.forName("Reika.ChromatiCraft.Render.Entity.RenderTunnelNuker");
					return (Render)c11.newInstance();
				case SPITTER:
					Class c12 = Class.forName("Reika.Satisforestry.Render.RenderSpitter");
					return (Render)c12.newInstance();
				default:
					return CritterClient.critter;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isValid(Entity e) {
		Class c = e.getClass();
		String n = c.getSimpleName();
		switch(this) {
			case VANILLA:
				return c == EntitySpider.class || n.equalsIgnoreCase("EntityLegacySpider");
			case FIREBEETLE:
				return n.equalsIgnoreCase("EntityTFFireBeetle");
			case HEATSCAR:
				return n.equalsIgnoreCase("HeatscarSpider");
			case HEDGE:
				return n.equalsIgnoreCase("EntityTFHedgeSpider");
			case KING:
				return n.equalsIgnoreCase("EntityTFKingSpider");
			case LAVASLIME:
				return c == EntityMagmaCube.class;
			case MAZESLIME:
				return n.equalsIgnoreCase("EntityTFMazeSlime");
			case MINIGHAST:
				return n.equalsIgnoreCase("EntityTFMiniGhast");
			case MISTWOLF:
				return n.equalsIgnoreCase("EntityTFMistWolf");
			case SILVERFISH:
				return c == EntitySilverfish.class;
			case SLIMEBEETLE:
				return n.equalsIgnoreCase("EntityTFSlimeBeetle");
			case WISP:
				return n.equalsIgnoreCase("EntityWisp");
			case ICECORE:
				return n.equalsIgnoreCase("EntityTFIceShooter");
			case SLIME:
				return c == EntitySlime.class;
			case LUMAFLY:
				return n.equals("EntityTunnelNuker");
			case SPITTER:
				return n.equals("EntitySpitter");
		}
		return false;
	}

}
