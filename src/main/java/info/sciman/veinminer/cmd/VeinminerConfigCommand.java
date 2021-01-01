package info.sciman.veinminer.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import info.sciman.veinminer.VeinminerMod;
import info.sciman.veinminer.setup.Config;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Objects;

public class VeinminerConfigCommand  {

    // What can we do to a list?
    enum ListChangeBehaviour {
        ADD,
        REMOVE
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdVeinminer = dispatcher.register(
                Commands.literal("vm").requires(cs -> cs.hasPermissionLevel(2))

                        .then(Commands.literal("blocklist")
                            .then(Commands.literal("add")
                                .then(Commands.argument("block",ItemArgument.item())
                                    .executes(cs -> modifyBlocklist(cs,ListChangeBehaviour.ADD))))
                            .then(Commands.literal("remove")
                                .then(Commands.argument("block",ItemArgument.item())
                                    .executes(cs -> modifyBlocklist(cs,ListChangeBehaviour.REMOVE))))
                            .executes(cs -> displayBlockList(cs)))

                        .then(Commands.literal("toollist")
                            .then(Commands.literal("add")
                                .then(Commands.argument("tool",ItemArgument.item())
                                    .executes(cs -> modifyToolList(cs,ListChangeBehaviour.ADD))))
                            .then(Commands.literal("remove")
                                .then(Commands.argument("tool",ItemArgument.item())
                                    .executes(cs -> modifyToolList(cs,ListChangeBehaviour.REMOVE))))
                            .executes(cs -> displayToolList(cs)))

                        .then(Commands.literal("reload")
                            .executes((cs) -> {VeinminerMod.reloadBlockAndToolList(); cs.getSource().sendFeedback(new StringTextComponent("Reloading Block/Tool list from file"),false);return 0;}))

                        .then(Commands.literal("maxsize")
                            .executes((cs) -> {cs.getSource().sendFeedback(new StringTextComponent("Current max vein size is " + Config.MAX_VEIN_SIZE.get()),true); return 0;})
                            .then(Commands.argument("size",IntegerArgumentType.integer(1,1024))
                                .executes(cs -> setMaxVeinSize(cs))))

        );
    }

    // Display the blocklist
    private static int displayBlockList(CommandContext<CommandSource> cs) {
        TextComponent msg = new StringTextComponent("Current Blocklist:\n");
        for (Block b : VeinminerMod.blockList) {
            msg.append(new TranslationTextComponent(b.getTranslationKey()));
            msg.append(new StringTextComponent(", "));
        }
        cs.getSource().sendFeedback(msg,false);

        return 0;
    }
    private static int displayToolList(CommandContext<CommandSource> cs) {
        TextComponent msg = new StringTextComponent("Current Toollist:\n");
        for (Item i : VeinminerMod.toolList) {
            msg.append(new TranslationTextComponent(i.getTranslationKey()));
            msg.append(new StringTextComponent(", "));
        }
        cs.getSource().sendFeedback(msg,false);

        return 0;
    }

    // Set the maximum size of a vein of ore
    private static int setMaxVeinSize(CommandContext<CommandSource> cs) {
        int size = IntegerArgumentType.getInteger(cs,"size");
        cs.getSource().sendFeedback(new StringTextComponent("Set max vein size to " + size),true);
        Config.MAX_VEIN_SIZE.set(size);
        return 1;
    }

    // Modify the list of approved tools
    private static int modifyToolList(CommandContext<CommandSource> cs, ListChangeBehaviour mod) {
        ItemInput itemInput = ItemArgument.getItem(cs,"tool");
        Item item = itemInput.getItem();
        if (item != null) {

            List<String> toolIdList = (List<String>) Config.TOOL_ID_LIST.get();

            if (mod == ListChangeBehaviour.ADD) {
                // Add to list
                VeinminerMod.toolList.add(item);

                // Add to config
                toolIdList.add(item.getRegistryName().toString());

                // Send reply
                cs.getSource().sendFeedback(new StringTextComponent("Added ")
                        .append(new TranslationTextComponent(item.getTranslationKey()))
                        .append(new StringTextComponent(" to tool list")),true);
            }else{
                // Add to list
                VeinminerMod.toolList.remove(item);

                // Remove from config
                toolIdList.remove(Objects.requireNonNull(item.getRegistryName()).toString());

                // Send reply
                cs.getSource().sendFeedback(new StringTextComponent("Removed ")
                        .append(new TranslationTextComponent(item.getTranslationKey()))
                        .append(new StringTextComponent(" from tool ist")),true);
            }
            Config.TOOL_ID_LIST.set(toolIdList);
        }
        return 1;
    }

    // Add or remove from bloclist
    private static int modifyBlocklist(CommandContext<CommandSource> cs, ListChangeBehaviour mod) {
        ItemInput itemInput = ItemArgument.getItem(cs,"block");
        Item item = itemInput.getItem();
        if (item.getItem() != null) {
            // Figure out if we're adding a block
            Block block = Block.getBlockFromItem(item.getItem());
            if (block != Blocks.AIR) {

                List<String> blockIdList = (List<String>) Config.BLOCK_ID_LIST.get();

                if (mod == ListChangeBehaviour.ADD) {
                    VeinminerMod.blockList.add(block);

                    // Add to config
                    blockIdList.add(Objects.requireNonNull(block.getRegistryName()).toString());

                    // Send reply
                    cs.getSource().sendFeedback(new StringTextComponent("Added ")
                            .append(new TranslationTextComponent(block.getTranslationKey()))
                            .append(new StringTextComponent(" to block list")),true);
                } else {
                    VeinminerMod.blockList.remove(block);

                    // Remove from config
                    blockIdList.remove(block.getRegistryName().toString());

                    // Send reply
                    cs.getSource().sendFeedback(new StringTextComponent("Removed ")
                            .append(new TranslationTextComponent(block.getTranslationKey()))
                            .append(new StringTextComponent(" from block list")),true);
                }
                Config.BLOCK_ID_LIST.set(blockIdList);
            }else{
                cs.getSource().sendFeedback(new StringTextComponent("Please specify a block"),false);
            }
        }
        return 1;
    }

}
