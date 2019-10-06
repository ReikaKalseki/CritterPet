package Reika.CritterPet.Entities.Mod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Entity.EntityTunnelNuker;
import Reika.CritterPet.Entities.Base.EntityFlyingBase;
import Reika.CritterPet.Registry.CritterType;

public class TameLumafly extends EntityFlyingBase {

	public TameLumafly(World world) {
		super(world, CritterType.LUMAFLY);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		EntityTunnelNuker.doEntityTick(this);
	}

	@Override
	protected boolean alwaysFaceOwner() {
		return false;
	}

	@Override
	protected void updateRotation() {
		rotationYaw += Math.signum(System.identityHashCode(this))/8F;
		rotationYawHead = rotationYaw;
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

	@Override
	protected double getCoastingHeight() {
		return this.isSitting() ? 0.25 : 2;
	}

}
