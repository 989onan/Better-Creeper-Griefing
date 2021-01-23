package com.bettercreepergriefing;

import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bettercreepergriefing.config.ConfigLoaderAndReader;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;



@Mod.EventBusSubscriber(modid = MainBetterCreeperGriefing.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@Mod("bettercreepergriefing")
public class MainBetterCreeperGriefing {
	public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "bettercreepergriefing";
    
    
    public MainBetterCreeperGriefing() {
    	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,ConfigLoaderAndReader.config);
    	ConfigLoaderAndReader.loadConfig(ConfigLoaderAndReader.config, FMLPaths.CONFIGDIR.get().resolve("mainbettercreeper-config.toml").toString());
    	
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    
    //all of this below is how the mod overrides the creeper explosion, changes the explosion behavior, and then
    //does the explosion.
	@SubscribeEvent
	public void creeperoverride(EntityMobGriefingEvent event) {
		//LOGGER.info("checked Griefing!");
		
		if (event.getEntity().getType() == EntityType.CREEPER) {
			event.setResult(Result.DENY);
			BlockPos pos = event.getEntity().getPosition();
			
			
			
			// COPIED FROM EXPLOSION AND MODIFIED:
			CreeperEntity creeper = (CreeperEntity) event.getEntity();
			Map<PlayerEntity, Vector3d> playerKnockbackMap = Maps.newHashMap();
			World world = creeper.getEntityWorld();
			float h = creeper.isCharged() ? 2.0F : 1.0F;
			float size = 3F * h;
			if (creeper.serializeNBT().contains("ExplosionRadius", 99)) {
				size = creeper.serializeNBT().getByte("ExplosionRadius")*h;
			}

			for (int j = 0; j < 16; ++j) {
				for (int k = 0; k < 16; ++k) {
					for (int l = 0; l < 16; ++l) {
						if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
							double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
							double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
							double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
							double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
							d0 = d0 / d3;
							d1 = d1 / d3;
							d2 = d2 / d3;
							float f = size * (0.7F + world.rand.nextFloat() * 0.6F);
							double d4 = creeper.getPosX();
							double d6 = creeper.getPosY();
							double d8 = creeper.getPosZ();

							for (@SuppressWarnings("unused")
							float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
								BlockPos blockpos = new BlockPos(d4, d6, d8);
								BlockState blockstate = world.getBlockState(blockpos);
								if (blockstate.getBlock() == Blocks.FARMLAND) {
									world.setBlockState(blockpos, Blocks.DIRT.getDefaultState());
								} else if (blockstate.getBlock() == Blocks.STONE && 0 == world.getRandom().nextInt(11)) {// block captured in explosion is stone
									
									world.setBlockState(blockpos, Blocks.COBBLESTONE.getDefaultState());//set block to cobblestone
							
									
									world.addEntity(new FallingBlockEntity(world, blockpos.getX(),
											blockpos.getY(), blockpos.getZ(), world.getBlockState(blockpos)));// spawn
																														// falling
																														// cobblestone

								} else if (blockstate.getBlock() == Blocks.ACACIA_LEAVES
										|| blockstate.getBlock() == Blocks.BIRCH_LEAVES
										|| blockstate.getBlock() == Blocks.DARK_OAK_LEAVES
										|| blockstate.getBlock() == Blocks.JUNGLE_LEAVES
										|| blockstate.getBlock() == Blocks.OAK_LEAVES
										|| blockstate.getBlock() == Blocks.SPRUCE_LEAVES) {// if block captured is a
																						// leaf	 type make it disappear.
									creeper.world.removeBlock(blockpos, false);
									if (0 == world.getRandom().nextInt(21)) {// one in 20 chance
										world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());// set that fire.
									}
								}

								d4 += d0 * (double) 0.3F;
								d6 += d1 * (double) 0.3F;
								d8 += d2 * (double) 0.3F;
							}
						}
					}
				}
			}
			// grab entities in explosion
			float f2 = size * 2.0F;
			int k1 = MathHelper.floor(pos.getX() - (double) f2 - 1.0D);
			int l1 = MathHelper.floor(pos.getX() + (double) f2 + 1.0D);
			int i2 = MathHelper.floor(pos.getY() - (double) f2 - 1.0D);
			int i1 = MathHelper.floor(pos.getY() + (double) f2 + 1.0D);
			int j2 = MathHelper.floor(pos.getZ() - (double) f2 - 1.0D);
			int j1 = MathHelper.floor(pos.getZ() + (double) f2 + 1.0D);
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(creeper,
					new AxisAlignedBB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
			Vector3d vector3d = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
			for (int k2 = 0; k2 < list.size(); ++k2) {
				Entity entity = list.get(k2);
				if (!entity.isImmuneToExplosions() && entity.getEntity().getType() != EntityType.FALLING_BLOCK) {
					double d12 = (double) (MathHelper.sqrt(entity.getDistanceSq(vector3d)) / (f2*ConfigLoaderAndReader.damagemultiplier.get())); //this is where that damage multiplier config is used.
					if (d12 <= 1.0D) {
						double d5 = entity.getPosX() - pos.getX();
						double d7 = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - pos.getY();
						double d9 = entity.getPosZ() - pos.getZ();
						double d13 = (double) MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
						if (d13 != 0.0D) {
							d5 = d5 / d13;
							d7 = d7 / d13;
							d9 = d9 / d13;
							double d14 = (double) Explosion.getBlockDensity(vector3d, entity);
							double d10 = (1.0D - d12) * d14;
							entity.attackEntityFrom(DamageSource.causeExplosionDamage(creeper), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)));
							double d11 = d10;
							if (entity instanceof LivingEntity) {
								d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity) entity, d10);
							}

							entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
							if (entity instanceof PlayerEntity) {
								PlayerEntity playerentity = (PlayerEntity) entity;
								if (!playerentity.isSpectator()
										&& (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
									playerKnockbackMap.put(playerentity, new Vector3d(d5 * d10, d7 * d10, d9 * d10));
								}
							}

						}
					}
				}
			}
		}
	}
}
