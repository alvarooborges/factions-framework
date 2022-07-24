package net.hyze.factions.framework.misc.supportblocks;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hyze.core.spigot.misc.blockdrops.BlockDropsManager;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.obsidiandestroyer.api.CustomBlock;
import net.hyze.obsidiandestroyer.api.ObsidianDestroyerAPI;
import net.hyze.obsidiandestroyer.enumerations.TimerState;
import net.hyze.obsidiandestroyer.managers.ChunkManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

@Getter
public enum SupportBlock {
    REGENERATION(
            new MaterialData(Material.REDSTONE_BLOCK),
            2,
            new BlockVector(3, 3, 3),
            "Bloco de Regeneração",
            new String[]{
                "Faz com que todos os blocos em",
                "até de 3 blocos de distância",
                "regenerem 1 ponto de proteção",
                "a cada 10 segundos."
            }
    ),
    REINFORCEMENT(
            new MaterialData(Material.SLIME_BLOCK),
            5,
            new BlockVector(1, 1, 1),
            "Bloco de Reforço",
            new String[]{
                "Faz com que todos os blocos em",
                "um raio de 1 bloco tenham a sua",
                "durabilidade aumentada em 50%."
            }
    ),
    ABSORPTION(
            new MaterialData(Material.PRISMARINE),
            3,
            new BlockVector(5, 5, 5),
            "Bloco de Absorção",
            new String[]{
                "Faz com que todos os blocos em",
                "um raio de 5 blocos tenham 20%",
                "de chance de ignorar o dano de",
                "uma explosão."
            }
    );

    private final MaterialData data;
    private final int maxDurability;
    private final Vector area;
    private final String displayName;
    private final String[] description;

    SupportBlock(MaterialData data, int maxDurability, Vector area, String displayName, String[] description) {
        this.data = data;
        this.maxDurability = maxDurability;
        this.area = area;
        this.displayName = displayName;
        this.description = description;

        BlockDropsManager.registerHandler(data.getItemType(), (Block block, Player player, ItemStack tool) -> {
            SupportBlock supportBlock = SupportBlockManager.getInstance().getBlock(block.getLocation());

            if (supportBlock != null) {
                return Lists.newArrayList(supportBlock.getCustomItem().asItemStack());
            }

            if (tool != null) {
                return Lists.newArrayList(block.getDrops(tool));
            }

            return Lists.newArrayList(block.getDrops());
        });

        ObsidianDestroyerAPI.addCustomBlock(new CustomBlock() {
            @Override
            public Material getMaterial() {
                return data.getItemType();
            }

            @Override
            public int getDurability(Location location) {
                return data.getData();
            }

            @Override
            public boolean canDrop(Location loc, EntityExplodeEvent event) {
                return false;
            }

            @Override
            public boolean onExplode(Location loc, EntityExplodeEvent event) {
                return true;
            }
        });
    }

    public CustomItem getCustomItem() {
        return new ProtectionBlockItem();
    }

    public class ProtectionBlockItem extends CustomItem {

        public ProtectionBlockItem() {
            super(SupportBlock.this.name().toLowerCase() + "-protection-block-item");
        }

        @Override
        public ItemBuilder getItemBuilder() {
            return ItemBuilder.of(data.getItemType())
                    .name(getDisplayName())
                    .data(data.getData())
                    .glowing(true)
                    .lore(description);
        }

        @Override
        public String getDisplayName() {
            return ChatColor.GOLD + displayName;
        }

        @Subscribe
        public void on(BlockPlaceEvent event) {
            if (event.isCancelled()) {
                return;
            }

            if (SupportBlockManager.getInstance() == null) {
                event.setCancelled(true);
                return;
            }

            SupportBlockManager.getInstance().addBlock(event.getBlock().getLocation(), SupportBlock.this);

            ChunkManager.startNewTimer(event.getBlock(), Math.max(0, SupportBlock.this.getMaxDurability() - 1), TimerState.INACTIVE);

            Message.INFO.send(event.getPlayer(), "Você colocou um " + SupportBlock.this.getDisplayName() + ".");
        }
    }
}
