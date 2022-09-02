package plus.dragons.createenchantmentindustry.content.contraptions.enchantments;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import plus.dragons.createenchantmentindustry.entry.ModItems;

import java.util.function.Supplier;

public class EnchantingGuideEditPacket extends SimplePacketBase {

    private int index;
    private ItemStack itemStack;


    public EnchantingGuideEditPacket(int index, ItemStack enchantedBook) {
        this.index = index;
        itemStack = enchantedBook;
    }

    public EnchantingGuideEditPacket(FriendlyByteBuf buffer) {
        index = buffer.readInt();
        itemStack = buffer.readItem();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(index);
        buffer.writeItem(itemStack);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get()
                .enqueueWork(() -> {
                    ServerPlayer sender = context.get()
                            .getSender();
                    ItemStack mainHandItem = sender.getMainHandItem();
                    if (!ModItems.ENCHANTING_GUIDE_FOR_BLAZE.isIn(mainHandItem))
                        return;

                    CompoundTag tag = mainHandItem.getOrCreateTag();
                    tag.putInt("index",index);
                    tag.put("target",itemStack.serializeNBT());

                    sender.getCooldowns()
                            .addCooldown(mainHandItem.getItem(), 5);
                });
        context.get()
                .setPacketHandled(true);
    }
}
