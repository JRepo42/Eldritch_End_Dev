package elocindev.eldritch_end.entity.faceless;

import elocindev.eldritch_end.EldritchEnd;
import elocindev.eldritch_end.config.Configs;
import elocindev.eldritch_end.utils.ParticleUtils;
import mod.azure.azurelib.ai.pathing.AzureNavigation;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.constant.DefaultAnimations;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.*;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;

public class FacelessEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private final ServerBossBar bossBar;

    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private static final float ALLOWED_DISTANCE = 15f;
    private static final float SURGE_RADIUS = 15f;

    private int attackTicksLeft;

    public FacelessEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar)(new ServerBossBar(Text.of("\uC892"), Color.PURPLE, Style.PROGRESS))
            .setDarkenSky(true)
            .setThickenFog(true);
        navigation = new AzureNavigation(this, world);
        this.setStepHeight(1.0F);
    }

    private void shadowSurge(PlayerEntity target) {
        if (target == null || target.distanceTo(this) < ALLOWED_DISTANCE) return;
        if (!this.getWorld().isClient) {
            ParticleUtils.sendParticlesToAll(this, "teleportationRing");
            ParticleUtils.sendParticlesToAll(target, "teleportationRing");
            this.teleport(target.getX() + 2, target.getY(), target.getZ() + 2);

            float missingHealth = (this.getMaxHealth() - this.getHealth()) / 2;

            for (LivingEntity entity: target.getWorld().getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(SURGE_RADIUS), entity -> true)) {
                entity.damage(entity.getDamageSources().generic(), missingHealth);
            }
        }
    }

    private void curse(PlayerEntity target) {
        if (target == null || Math.abs(this.getPos().y - target.getPos().y)  < 3 || target.getWorld().isClient) return;
        target.damage(target.getDamageSources().generic(), target.getMaxHealth() * 0.25f);
        ParticleUtils.sendParticlesToAll(target, "distanceWarningParticles");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age % 20 == 0) {
            curse((PlayerEntity) this.getWorld().getClosestPlayer(this, 32));
        }
        if (this.age % 100 == 0) {
            shadowSurge((PlayerEntity) this.getWorld().getClosestPlayer(this, 32));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseAnim", 5, event -> {
            return event.setAndContinue(
                    event.isMoving() ? RawAnimation.begin().thenLoop("walk")
                    : RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(new AnimationController<>(this, "attackAnim", event -> PlayState.CONTINUE)
                .triggerableAnim("attack",
                RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE)));
    }

    @Override
    public boolean tryAttack(Entity target) {
        EldritchEnd.LOGGER.info("Attack!");
        triggerAnim("attackAnim", "attack");
        this.handSwinging = false;
        return super.tryAttack(target);
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

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
        // TODO: REPLACE THIS WITH CUSTOM FACELESS CONFIG
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { }

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
}