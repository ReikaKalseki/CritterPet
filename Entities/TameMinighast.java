package Reika.CritterPet.Entities;

import Reika.CritterPet.Registry.CritterType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class TameMinighast extends EntityFlyingBase {

	public TameMinighast(World world) {
		super(world, CritterType.MINIGHAST);
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {

	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return true;
	}

	@Override
	protected float getAttackDamage() {
		return 0;
	}

}
