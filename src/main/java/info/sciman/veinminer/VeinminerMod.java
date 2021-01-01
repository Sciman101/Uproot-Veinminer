package info.sciman.veinminer;

import info.sciman.veinminer.cmd.VeinminerConfigCommand;
import info.sciman.veinminer.setup.Config;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("veinminer")
public class VeinminerMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static Set<Block> blockList = new HashSet<>();
    public static Set<Item> toolList = new HashSet<>();

    public VeinminerMod() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,() -> Pair.of(()-> FMLNetworkConstants.IGNORESERVERONLY,(a,b)->true));

        // Setup config file
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        // Register commands
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        // Register actual veinmine event
        MinecraftForge.EVENT_BUS.register(new VeinminerEventHandler());
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        reloadBlockAndToolList();
    }

    public static void reloadBlockAndToolList() {
        LOGGER.debug("Reloading block/tool list");
        blockList.clear();
        for (String bid : Config.BLOCK_ID_LIST.get()) {
            blockList.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bid)));
        }
        toolList.clear();
        for (String bid : Config.TOOL_ID_LIST.get()) {
            toolList.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(bid)));
        }
    }

    // Register config command
    private void onRegisterCommands(RegisterCommandsEvent event) {
        VeinminerConfigCommand.register(event.getDispatcher());
    }
}
