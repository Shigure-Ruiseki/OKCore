package ruiseki.okcore.client.model;

import static com.gtnewhorizon.gtnhlib.GTNHLibConfig.modelCacheSize;
import static com.gtnewhorizon.gtnhlib.client.model.unbaked.MissingModel.MISSING_MODEL;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelDeserializer;
import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.JSONModel;
import com.gtnewhorizon.gtnhlib.concurrent.ThreadsafeCache;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.client.model.location.StateLocation;
import ruiseki.okcore.client.model.state.IStateModelMap;
import ruiseki.okcore.client.model.state.MissingState;
import ruiseki.okcore.client.model.state.StateDeserializer;
import ruiseki.okcore.event.resource.ResourceEvent;
import ruiseki.okcore.helper.Helpers;

public class ModelRegistry {

    public static ModelRegistry INSTANCE = new ModelRegistry();

    /// Getter for {@link BakedModel}s, obtained purely from the default path. We don't want to publicly expose the
    /// cache, modders can't be trusted with it :P
    public static BakedModel getBakedModel(IBlockState state) {
        if (state == null) return MISSING_MODEL.bake();
        return BLOCKSTATE_MODEL_CACHE.get(state);
    }

    /// Getter for {@link JSONModel}s - again, normal pipeline, but this grabs them pre-bake. See
    /// {@link ModelRegistry#getBakedModel(IBlockState)}
    public static JSONModel getJSONModel(ResourceLoc.ModelLoc loc) {
        return JSON_MODEL_CACHE.get(loc);
    }

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(IStateModelMap.class, new StateDeserializer())
        .registerTypeAdapter(JSONModel.class, new ModelDeserializer())
        .create();

    /// The first cache. Ideally, every request hits this and gets a baked model.
    private static final ThreadsafeCache<IBlockState, BakedModel> BLOCKSTATE_MODEL_CACHE = new ThreadsafeCache<>(
        modelCacheSize,
        s -> bakeModel((IBlockState) s),
        false);

    /// If the first cache misses, we hit this to get the state map, so we can figure out which model to bake.
    private static final ThreadsafeCache<StateLocation, IStateModelMap> STATE_MODEL_MAP_CACHE = new ThreadsafeCache<>(
        modelCacheSize,
        s -> ((StateLocation) s).load(() -> MissingState.MISSING_STATE_MAP, GSON),
        false);

    /// {@link JSONModel}s may be shared across several {@link BakedModel}s, so we cache them too. The cache ensures all
    /// loaded models are resolved and ready to bake.
    ///
    /// Note: the retriever was broken out into a function, because you can't read a field from within its own
    /// definition.
    private static final ThreadsafeCache<ResourceLoc.ModelLoc, JSONModel> JSON_MODEL_CACHE = new ThreadsafeCache<>(
        modelCacheSize,
        s -> loadAndResolveJSONModel((ResourceLoc.ModelLoc) s),
        false);

    private static BakedModel bakeModel(IBlockState state) {
        final Block block = state.getBlock();
        final IStateModelMap smm = getStateModelMap(block);

        // Caching this would be a little pointless, since an UnbakedModel here would map directly to the BakedModel
        // missing from the cache... that's why we're loading one from scratch. The JSONModel *used* by the UnbakedModel
        // will be cached, however.
        return smm.selectModel(state)
            .bake();
    }

    private static IStateModelMap getStateModelMap(Block block) {
        final ResourceLocation location = Helpers.getLocation(block);
        final var stateLocation = new StateLocation(location);
        return STATE_MODEL_MAP_CACHE.get(stateLocation);
    }

    private static JSONModel loadAndResolveJSONModel(ResourceLoc.ModelLoc loc) {
        return loc.load(() -> MISSING_MODEL, GSON)
            .resolveParents(JSON_MODEL_CACHE::get);
    }

    @SubscribeEvent
    public void onResourceReload(ResourceEvent.Pre event) {
        // Flush all caches, we may have new models
        BLOCKSTATE_MODEL_CACHE.clear();
        STATE_MODEL_MAP_CACHE.clear();
        JSON_MODEL_CACHE.clear();
    }
}
