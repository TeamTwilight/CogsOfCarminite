package com.cogsofcarminite.reg;

import com.cogsofcarminite.client.renderers.items.CarminiteMagicLogItemRenderer;
import com.cogsofcarminite.items.BlockFilterItem;
import com.cogsofcarminite.items.CarminiteGearGadgetItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static com.cogsofcarminite.CogsOfCarminite.TWILIGHT_REGISTRATE;
import static com.simibubi.create.AllTags.AllItemTags;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("SameParameterValue")
public class CCItems {

    public static final ItemEntry<Item> IRONWOOD_SHEET = taggedIngredient("ironwood_sheet", AllItemTags.PLATES.tag);
    public static final ItemEntry<Item> KNIGHTMETAL_SHEET = taggedIngredient("knightmetal_sheet", AllItemTags.PLATES.tag);
    public static final ItemEntry<Item> FIERY_SHEET = taggedIngredient("fiery_sheet", AllItemTags.PLATES.tag);
    public static final ItemEntry<Item> IRONWOOD_NUGGET = taggedIngredient("ironwood_nugget", Tags.Items.NUGGETS);

    public static final ItemEntry<? extends SequencedAssemblyItem> INCOMPLETE_GEAR_GADGET = TWILIGHT_REGISTRATE.item("incomplete_gear_gadget", a -> new SequencedAssemblyItem(a) {
                @Override
                @OnlyIn(Dist.CLIENT)
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(SimpleCustomRenderer.create(this, new CarminiteMagicLogItemRenderer(CCPartialBlockModels.INCOMPLETE_FLYWHEEL)));
                }
            })
            .model(AssetLookup.itemModelWithPartials())
            .register();

    public static final ItemEntry<CarminiteGearGadgetItem> CARMINITE_GEAR_GADGET = TWILIGHT_REGISTRATE.item("carminite_gear_gadget", CarminiteGearGadgetItem::new)
            .model(AssetLookup.itemModelWithPartials())
            .register();

    public static final ItemEntry<BlockFilterItem> BLOCK_ATTRIBUTE_FILTER = TWILIGHT_REGISTRATE.item("block_attribute_filter", BlockFilterItem::new).register();

    public static void register() { }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        return TWILIGHT_REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    private static ItemEntry<Item> ingredient(String name) {
        return TWILIGHT_REGISTRATE.item(name, Item::new)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return TWILIGHT_REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }
}
