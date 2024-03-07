package com.cogsofcarminite;

import com.cogsofcarminite.blocks.MechanicalMinewoodCoreBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.data.tags.BlockTagGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CogsOfCarminite.MODID)
public class EventListener {
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
        MechanicalMinewoodCoreBlock.getReplacements().clear();

        //collect all tags
        for (TagKey<Block> tag : BuiltInRegistries.BLOCK.getTagNames().filter(location -> location.location().getNamespace().equals("forge")).toList()) {
            //check if the tag is a valid ore tag
            if (tag.location().getPath().contains("ores_in_ground/")) {
                //grab the part after the slash for use later
                String oreground = tag.location().getPath().substring(15);
                //check if a tag for ore grounds matches up with our ores in ground tag
                if (BuiltInRegistries.BLOCK.getTagNames().filter(location -> location.location().getNamespace().equals("forge")).anyMatch(blockTagKey -> blockTagKey.location().getPath().equals("ore_bearing_ground/" + oreground))) {
                    //add each ground type to each ore
                    BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "ore_bearing_ground/" + oreground))).get().forEach(ground ->
                            BuiltInRegistries.BLOCK.getTag(tag).get().forEach(ore -> {
                                //exclude ignored ores
                                if (!ore.value().defaultBlockState().is(BlockTagGenerator.ORE_MAGNET_IGNORE)) {
                                    MechanicalMinewoodCoreBlock.getReplacements().put(ore.value(), ground.value());
                                }
                            }));
                }
            }
        }

        //Gonna need to special case this one as it isn't covered by tags.
        //Ancient debris isn't exactly an ore, so it makes sense that the tag doesn't include it
        if (!Blocks.ANCIENT_DEBRIS.defaultBlockState().is(BlockTagGenerator.ORE_MAGNET_IGNORE) && !MechanicalMinewoodCoreBlock.getReplacements().containsKey(Blocks.ANCIENT_DEBRIS)) {
            MechanicalMinewoodCoreBlock.getReplacements().put(Blocks.ANCIENT_DEBRIS, Blocks.NETHERRACK);
        }
    }
}
