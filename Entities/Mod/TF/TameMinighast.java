package Reika.CritterPet.Entities.Mod.TF;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntityFlyingBase;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class TameMinighast extends EntityFlyingBase {

	public TameMinighast(World world) {
		super(world, CritterType.MINIGHAST);
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		ReikaEntityHelper.knockbackEntity(this, e, 1);
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return true;
	}

	@Override
	protected float getAttackDamage() {
		return 2;
	}

	@Override
	protected double getCoastingHeight() {
		return 1.2;
	}

}
