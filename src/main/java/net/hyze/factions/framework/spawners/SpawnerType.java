package net.hyze.factions.framework.spawners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.factions.framework.spawners.entities.CustomMagmaCube;
import net.hyze.factions.framework.spawners.entities.CustomSlime;
import net.hyze.factions.framework.spawners.entities.CustomWitherSkeleton;
import net.hyze.factions.framework.spawners.properties.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum SpawnerType {

    MAGMA_CUBE("Cubo de Magma", EntityType.MAGMA_CUBE, HeadTexture.ENTITY_MAGMA_CUBE, new MagmaCubeProperties()) {
        @Override
        public Entity createEntity(Location location) {
            EntityMagmaCube magmaCube = new CustomMagmaCube(((CraftWorld) location.getWorld()).getHandle());

            magmaCube.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

            return magmaCube.getBukkitEntity();
        }
    },
    GOLEM("Golem", EntityType.IRON_GOLEM, HeadTexture.ENTITY_IRON_GOLEM, new GolemProperties()),
    PIG_ZOMBIE("Porco Zumbi", EntityType.PIG_ZOMBIE, HeadTexture.ENTITY_PIG_ZOMBIE, new PigZombieProperties()),
    BLAZE("Blaze", EntityType.BLAZE, HeadTexture.ENTITY_BLAZE, new BlazeProperties()),
    SKELETON("Esqueleto", EntityType.SKELETON, HeadTexture.ENTITY_SKELETON, new SkeletonProperties()) {
        @Override
        public boolean match(Entity entity) {

            if (entity.getType() != EntityType.SKELETON) {
                return false;
            }

            Skeleton skeleton = (Skeleton) entity;

            return skeleton.getSkeletonType() == Skeleton.SkeletonType.NORMAL;
        }

        @Override
        public boolean match(CreatureSpawner creatureSpawner) {
            if (creatureSpawner.getSpawnedType() != EntityType.SKELETON) {
                return false;
            }

            CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;

            TileEntityMobSpawner spawner = craftCreatureSpawner.getTileEntity();

            NBTTagCompound compound = new NBTTagCompound();
            spawner.b(compound);

            if (!compound.hasKeyOfType("SpawnData", 10)) {
                return true;
            }

            NBTTagCompound spawnerType = compound.getCompound("SpawnData");

            if (!spawnerType.hasKeyOfType("SkeletonType", 99)) {
                return true;
            }

            return spawnerType.getInt("SkeletonType") == 0;
        }
    },
    WITHER_SKELETON("Esqueleto Wither", EntityType.SKELETON, HeadTexture.ENTITY_WITHER_SKELETON, new WitherSkeletonProperties()) {
        @Override
        public void setup(NBTTagCompound compound) {
            NBTTagCompound spawnData = new NBTTagCompound();
            spawnData.setInt("SkeletonType", 1);
            compound.set("SpawnData", spawnData);
        }

        @Override
        public boolean match(Entity entity) {

            if (entity.getType() != EntityType.SKELETON) {
                return false;
            }

            Skeleton skeleton = (Skeleton) entity;

            return skeleton.getSkeletonType() == Skeleton.SkeletonType.WITHER;
        }

        @Override
        public boolean match(CreatureSpawner creatureSpawner) {
            if (creatureSpawner.getSpawnedType() != EntityType.SKELETON) {
                return false;
            }

            CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;

            TileEntityMobSpawner spawner = craftCreatureSpawner.getTileEntity();

            NBTTagCompound compound = new NBTTagCompound();
            spawner.b(compound);

            if (!compound.hasKeyOfType("SpawnData", 10)) {
                return false;
            }

            NBTTagCompound spawnerType = compound.getCompound("SpawnData");

            if (!spawnerType.hasKeyOfType("SkeletonType", 99)) {
                return false;
            }

            return spawnerType.getInt("SkeletonType") == 1;
        }

        @Override
        public Entity createEntity(Location location) {
            net.minecraft.server.v1_8_R3.Entity slime = new CustomWitherSkeleton(((CraftWorld) location.getWorld()).getHandle());

            slime.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

            return slime.getBukkitEntity();
        }
    },
    ZOMBIE("Zumbi", EntityType.ZOMBIE, HeadTexture.ENTITY_ZOMBIE, new ZombieProperties()),
    SPIDER("Aranha", EntityType.SPIDER, HeadTexture.ENTITY_SPIDER, new SpiderProperties()),
    CAVE_SPIDER("Aranha da Caverna", EntityType.CAVE_SPIDER, HeadTexture.ENTITY_CAVE_SPIDER, new CaveSpiderProperties()),
    SLIME("Slime", EntityType.SLIME, HeadTexture.ENTITY_SLIME, new SlimeProperties()) {
        @Override
        public Entity createEntity(Location location) {
            EntitySlime slime = new CustomSlime(((CraftWorld) location.getWorld()).getHandle());

            slime.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

            return slime.getBukkitEntity();
        }
    },
    MUSHROOM_COW("CoguVaca", EntityType.MUSHROOM_COW, HeadTexture.ENTITY_MOOSHROOM_COW, new MushroomCowProperties()),
    SHEEP("Ovelha", EntityType.SHEEP, HeadTexture.ENTITY_SHEEP, new SheepProperties()),
    COW("Vaca", EntityType.COW, HeadTexture.ENTITY_COW, new CowProperties()),
    WITHER("Wither", EntityType.WITHER, HeadTexture.ENTITY_WITHER_SKELETON, new WitherProperties());

    static {
        NMS.registerCustomEntity(CustomSlime.class, EntitySlime.class);
        NMS.registerCustomEntity(CustomWitherSkeleton.class, EntitySkeleton.class);
        NMS.registerCustomEntity(CustomMagmaCube.class, EntityMagmaCube.class);
    }

    private final String rawDisplayName;
    private final EntityType entityType;
    private final HeadTexture icon;
    private final SpawnerProperties properties;

    @Getter(lazy = true)
    private final SpawnerItem customItem = buildSpawnerItem();

    public Entity createEntity(Location location) {
        return ((CraftWorld) location.getWorld()).createEntity(location, getEntityType().getEntityClass()).getBukkitEntity();
    }

    public SpawnerItem buildSpawnerItem() {
        return new SpawnerItem(this);
    }

    public String getDisplayName() {
        return "&eGerador de " + this.getRawDisplayName();
    }

    public double modifyHealth(double health) {
        return properties.modifyHealth(health);
    }

    public List<ItemStack> getDrops(Player killer) {
        return properties.getDrops(killer);
    }

    public int getExp(LivingEntity entity, Player killer) {
        CraftEntity craftEntity = (CraftEntity) entity;

        EntityLiving living = (EntityLiving) craftEntity.getHandle();
        return living.getExpReward();
    }

    public void setup(NBTTagCompound compound) {

    }

    public boolean match(Entity entity) {
        return entity.getType() == entityType;
    }

    public boolean match(CreatureSpawner creatureSpawner) {
        return creatureSpawner.getSpawnedType() == entityType;
    }

    private static final EnumSet<SpawnerType> SPAWNER_TYPES = EnumSet.allOf(SpawnerType.class);

    public static SpawnerType from(Entity entity) {
        for (SpawnerType spawnerType : SPAWNER_TYPES) {
            if (spawnerType.match(entity)) {
                return spawnerType;
            }
        }

        return null;
    }

    public static SpawnerType from(CreatureSpawner creatureSpawner) {
        for (SpawnerType spawnerType : SPAWNER_TYPES) {
            if (spawnerType.match(creatureSpawner)) {
                return spawnerType;
            }
        }

        return null;
    }
}
