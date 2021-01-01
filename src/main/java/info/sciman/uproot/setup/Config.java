package info.sciman.uproot.setup;

import com.google.common.base.Predicates;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static ForgeConfigSpec SERVER_CONFIG;

    // Actual parameters
    public static ForgeConfigSpec.IntValue MAX_VEIN_SIZE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_ID_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> TOOL_ID_LIST;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        // Setup server config
        SERVER_BUILDER.push(CATEGORY_GENERAL);
        MAX_VEIN_SIZE = SERVER_BUILDER.defineInRange("maxVeinSize",64,1,1024);

        // Define block and tool list
        BLOCK_ID_LIST = SERVER_BUILDER.defineList("blockList",ConfigDefaults.DEFAULT_BLOCK_LIST, Predicates.alwaysTrue());
        TOOL_ID_LIST = SERVER_BUILDER.defineList("toolList",ConfigDefaults.DEFAULT_TOOL_LIST, Predicates.alwaysTrue());

        SERVER_BUILDER.pop();

        // Actually build it
        SERVER_CONFIG = SERVER_BUILDER.build();
    }



}
