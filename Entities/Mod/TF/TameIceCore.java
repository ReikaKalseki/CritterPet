package Reika.CritterPet.Entities.Mod.TF;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.CritterPet.Entities.Base.EntityFlyingBase;
import Reika.CritterPet.Registry.CritterType;

public class TameIceCore extends EntityFlyingBase {

	public TameIceCore(World world) {
		super(world, CritterType.ICECORE);
	}

	@Override
	protected void applyAttackEffects(EntityLivingBase e) {
		e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
	}

	@Override
	public boolean canBeHurtBy(DamageSource dsc) {
		return true;
	}

	@Override
	protected float getAttackDamage() {
		return 5;
	}

	@Override
	protected double getCoastingHeight() {
		return 0.25;
	}

}
