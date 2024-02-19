package com.cogsofcarminite;

import com.cogsofcarminite.client.ponder.CCPonderIndex;
import com.cogsofcarminite.data.*;
import com.cogsofcarminite.reg.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Mod(CogsOfCarminite.MODID)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogsOfCarminite {
    public static final String MODID = "cogsofcarminite";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate TWILIGHT_REGISTRATE = CreateRegistrate.create(CogsOfCarminite.MODID);

    static {
        TWILIGHT_REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public CogsOfCarminite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::init);
        modEventBus.addListener(EventPriority.LOW, CogsOfCarminite::gatherData);

        TWILIGHT_REGISTRATE.registerEventListeners(modEventBus);

        CCBlocks.register();
        CCItems.register();
        CCBlockEntities.register();

        CCCreativeModeTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CogsOfCarminite.onCtorClient(modEventBus, modEventBus));
    }

    public void init(FMLCommonSetupEvent evt) {
        CCPotatoProjectileTypes.register();
    }

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CogsOfCarminite::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        CCPartialBlockModels.init();
        CCPonderIndex.register();
    }

    public static void gatherData(GatherDataEvent event) {
        TWILIGHT_REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            CCPonderIndex.register();
            PonderLocalization.provideLang(CogsOfCarminite.MODID, langConsumer);
            PonderLocalization.provideRegistrateLang(TWILIGHT_REGISTRATE);
        });

        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        if (event.includeClient()) {
            CCLangGenerator.generateLang();
        }

        if (event.includeServer()) {
            CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
            ExistingFileHelper helper = event.getExistingFileHelper();

            CCProcessingRecipeGen.registerAll(gen, output);
            gen.addProvider(true, new CCStandardRecipeGen(output));
            BlockTagsProvider blockTagsProvider = new CCTags.Blocks(output, provider, helper);
            gen.addProvider(true, blockTagsProvider);
            gen.addProvider(true, new CCTags.Items(output, provider, blockTagsProvider.contentsGetter(), helper));
            gen.addProvider(true, CCWorldgenDataProvider.makeFactory(event.getLookupProvider()));
        }
    }

    public static ResourceLocation prefix(String id) {
        return new ResourceLocation(CogsOfCarminite.MODID, id);
    }
}
