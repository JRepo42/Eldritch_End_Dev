package elocindev.eldritch_end.entity.faceless;

import elocindev.eldritch_end.EldritchEnd;
import elocindev.eldritch_end.client.particle.EldritchParticles;
import elocindev.eldritch_end.registry.SoundEffectRegistry;
import mod.azure.azurelib.ai.pathing.AzureNavigation;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.*;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("resource")
public class FacelessEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private final ServerBossBar bossBar;
    private float animationProgressTicks = 0;
    private float animationDuration = 40;

    /** shadow surge variables **/
    private float shadowSurgeProgress = 0;
    private float shadowSurgeDuration = 94;
    private float firstImpactTicks = 43;
    private float secondImpactTicks = 52;
    private float thirdImpactTicks = 63;

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                // TODO: REPLACE THIS WITH CUSTOM FACELESS CONFIG
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEffectRegistry.GROWL_EVENT;
    }

    private static final float SURGE_RADIUS = 16f;
    private static final float DARKNESS_RANGE = 15f;
    private static final int SURGE_RATE_TICKS = 100;

    public FacelessEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar)(new ServerBossBar(Text.of("\uC892"), Color.PURPLE, Style.PROGRESS))
            .setDarkenSky(true)
            .setThickenFog(true);
        navigation = new AzureNavigation(this, world);
        this.setStepHeight(1.0F);
        this.animationProgressTicks = 0;
    }

    /*
    private void shadowSurge(PlayerEntity target) {
        if (target == null || target.distanceTo(this) < SURGE_RADIUS) return;
        EldritchEnd.LOGGER.info(String.valueOf(target.distanceTo(this)));
        if (!this.getWorld().isClient) {
            ParticleUtils.sendParticlesToAll(this, "teleportationRing");
            ParticleUtils.sendParticlesToAll(target, "teleportationRing");

            target.teleport(this.getX() + 2, this.getY(), this.getZ() + 2);
        }
    }


    private void curse(PlayerEntity target) {
        if (target == null || Math.abs(this.getPos().y - target.getPos().y) < 3 || target.getWorld().isClient) return;
        target.damage(target.getDamageSources().generic(), target.getMaxHealth() * 0.25f);
        ParticleUtils.sendParticlesToAll(target, "distanceWarningParticles");
    }


     */

    private void shadowSurge() {
        this.setStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, (int) shadowSurgeDuration, 255, false, false, false), null);
        EldritchParticles.playEffek("shadowsurge", this.getWorld(), this.getPos(),
                true, 0.30F).bindOnEntity(this);
    }

    private void meleeLogic() {
        if (animationProgressTicks < animationDuration) animationProgressTicks++;
        if (animationProgressTicks == 1) this.getWorld().playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEffectRegistry.PUNCH_EVENT, this.getSoundCategory(), 1F, 1.0f);
        if (animationProgressTicks == 3) performKeyframeAttack(this.getTarget());
    }

    private void shadowSurgeLogic() {
        if (shadowSurgeProgress < shadowSurgeDuration) {
            shadowSurgeProgress++;
        } else {
            shadowSurgeProgress = 0;
        }

        if (shadowSurgeProgress == 0) shadowSurge();
        else if (shadowSurgeProgress == firstImpactTicks) shadowSurgeAttack();
        else if (shadowSurgeProgress == secondImpactTicks) shadowSurgeAttack();
        else if (shadowSurgeProgress == thirdImpactTicks) shadowSurgeAttack();
    }

    private void shadowSurgeAttack() {
        float missingHealth = (this.getMaxHealth() - this.getHealth()) / 2;
        for (PlayerEntity playerEntity: this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(4), entity -> true)) {
            playerEntity.damage(this.getDamageSources().generic(), missingHealth / 3f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;
        meleeLogic();
        shadowSurgeLogic();

        /*
        if (this.age % SURGE_RATE_TICKS == 0) {
            for (PlayerEntity playerEntity: this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(SURGE_RADIUS*1.5f), entity -> true)) {

            }
        }

         */

        /* float missingHealth = (this.getMaxHealth() - this.getHealth()) / 2; */
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseAnim", 5, event -> {
            return event.setAndContinue(
                    event.isMoving() ? RawAnimation.begin().thenLoop("walk")
                    : RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(new AnimationController<>(this, "attackAnim", event -> PlayState.CONTINUE)
                .triggerableAnim("slam",
                RawAnimation.begin().then("slam", Animation.LoopType.PLAY_ONCE)));
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (target.getWorld().isClient) return false;
        if (this.animationProgressTicks == animationDuration) {
            this.animationProgressTicks = 0;
            triggerAnim("attackAnim", "slam");
            this.handSwinging = false;
        }
        EldritchEnd.LOGGER.info("Attack!");
        return false;
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
        //this.setTarget(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void checkDespawn() {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.discard();
        } else {
            this.despawnCounter = 0;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }


    private void performKeyframeAttack(Entity target) {
        if (target != null && !target.getWorld().isClient) {
            target.damage(this.getDamageSources().mobAttack(this), this.getAttackDamage());
        }
    }
}