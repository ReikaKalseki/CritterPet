package Reika.SpiderPet;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.SpiderPet.Entities.TameHeatScar;
import Reika.SpiderPet.Entities.TameHedge;
import Reika.SpiderPet.Entities.TameKing;
import Reika.SpiderPet.Entities.TameVanilla;

public class TamingController {

	public static void TameSpider(EntityLiving e, EntityPlayer ep) {
		World world = ep.worldObj;
		SpiderType s = getType(e);
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null)
			return;
		if (is.itemID == s.tamingItem.itemID) {
			EntitySpiderBase es = null;
			switch(s) {
			case HEATSCAR:
				es = new TameHeatScar(world);
				break;
			case HEDGE:
				es = new TameHedge(world);
				break;
			case KING:
				es = new TameKing(world);
				break;
			case VANILLA:
				es = new TameVanilla(world);
				break;
			default:
				return;
			}
			es.setOwner(ep);
			e.setDead();
			if (!world.isRemote) {
				world.spawnEntityInWorld(es);
			}
			if (!ep.capabilities.isCreativeMode)
				is.stackSize--;
		}
	}

	public static SpiderType getType(EntityLiving e) {
		return null;
	}

}
