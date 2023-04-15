package net.povstalec.sgjourney;

import java.util.function.BiFunction;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.render.ClassicStargateRenderer;
import net.povstalec.sgjourney.client.render.MilkyWayStargateRenderer;
import net.povstalec.sgjourney.client.render.PegasusStargateRenderer;
import net.povstalec.sgjourney.client.render.PlasmaProjectileRenderer;
import net.povstalec.sgjourney.client.render.SandstoneSymbolRenderer;
import net.povstalec.sgjourney.client.render.StoneSymbolRenderer;
import net.povstalec.sgjourney.client.render.TransportRingsRenderer;
import net.povstalec.sgjourney.client.render.UniverseStargateRenderer;
import net.povstalec.sgjourney.client.screens.BasicInterfaceScreen;
import net.povstalec.sgjourney.client.screens.ClassicDHDScreen;
import net.povstalec.sgjourney.client.screens.CrystalInterfaceScreen;
import net.povstalec.sgjourney.client.screens.DHDCrystalScreen;
import net.povstalec.sgjourney.client.screens.MilkyWayDHDScreen;
import net.povstalec.sgjourney.client.screens.NaquadahGeneratorScreen;
import net.povstalec.sgjourney.client.screens.PegasusDHDScreen;
import net.povstalec.sgjourney.client.screens.RingPanelScreen;
import net.povstalec.sgjourney.client.screens.ZPMHubScreen;
import net.povstalec.sgjourney.client.screens.config.ConfigScreen;
import net.povstalec.sgjourney.config.StargateJourneyConfig;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.init.EntityInit;
import net.povstalec.sgjourney.init.EventInit;
import net.povstalec.sgjourney.init.FeatureInit;
import net.povstalec.sgjourney.init.FluidInit;
import net.povstalec.sgjourney.init.FluidTypeInit;
import net.povstalec.sgjourney.init.GalaxyInit;
import net.povstalec.sgjourney.init.ItemInit;
import net.povstalec.sgjourney.init.MenuInit;
import net.povstalec.sgjourney.init.MiscInit;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.init.StructureInit;
import net.povstalec.sgjourney.init.TabInit;
import net.povstalec.sgjourney.init.VillagerInit;
import net.povstalec.sgjourney.stargate.Galaxy;
import net.povstalec.sgjourney.stargate.PointOfOrigin;
import net.povstalec.sgjourney.stargate.SolarSystem;
import net.povstalec.sgjourney.stargate.Symbols;
import net.povstalec.sgjourney.world.biomemod.BiomeModifiers;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StargateJourney.MODID)
public class StargateJourney
{
    public static final String MODID = "sgjourney";
    public static final String EMPTY = MODID + ":empty";
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public StargateJourney()
    {
    	IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	ItemInit.register(eventBus);
        BlockInit.register(eventBus);
        FluidInit.register(eventBus);
        FluidTypeInit.register(eventBus);
        BlockEntityInit.register(eventBus);
        MenuInit.register(eventBus);
        VillagerInit.register(eventBus);
        FeatureInit.register(eventBus);
        StructureInit.register(eventBus);
        BiomeModifiers.register(eventBus);
        EntityInit.register(eventBus);
        SoundInit.register(eventBus);
        
        GalaxyInit.register(eventBus);
        
        eventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> 
        {
            event.dataPackRegistry(Symbols.REGISTRY_KEY, Symbols.CODEC, Symbols.CODEC);
            event.dataPackRegistry(PointOfOrigin.REGISTRY_KEY, PointOfOrigin.CODEC, PointOfOrigin.CODEC);
            event.dataPackRegistry(SolarSystem.REGISTRY_KEY, SolarSystem.CODEC, SolarSystem.CODEC);
            event.dataPackRegistry(Galaxy.REGISTRY_KEY, Galaxy.CODEC, Galaxy.CODEC);
        });
        
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(Layers::registerLayers);
        eventBus.addListener(TabInit::onRegisterModTabs);
        eventBus.addListener(TabInit::addCreative);
        
		//StargateJourneyConfig.loadConfig(StargateJourneyConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("sgjourney-client.toml").toString());
		//StargateJourneyConfig.loadConfig(StargateJourneyConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("sgjourney-server.toml").toString());
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StargateJourneyConfig.CLIENT_CONFIG, "sgjourney-client.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StargateJourneyConfig.COMMON_CONFIG, "sgjourney-common.toml");
		
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, 
				() -> new ConfigScreenHandler.ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>()
				{
					@Override
					public Screen apply(Minecraft mc, Screen screen)
					{
						return new ConfigScreen(screen);
					}
				}));
        
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(MiscInit::registerCommands);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event)
    {
    	event.enqueueWork(() -> 
    	{
    		PacketHandlerInit.register();
    		VillagerInit.registerPOIs();
    	});
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());

        	MenuScreens.register(MenuInit.BASIC_INTERFACE.get(), BasicInterfaceScreen::new);
        	MenuScreens.register(MenuInit.CRYSTAL_INTERFACE.get(), CrystalInterfaceScreen::new);
            
        	MenuScreens.register(MenuInit.RING_PANEL.get(), RingPanelScreen::new);

        	MenuScreens.register(MenuInit.DHD_CRYSTAL.get(), DHDCrystalScreen::new);
        	MenuScreens.register(MenuInit.MILKY_WAY_DHD.get(), MilkyWayDHDScreen::new);
        	MenuScreens.register(MenuInit.PEGASUS_DHD.get(), PegasusDHDScreen::new);
        	MenuScreens.register(MenuInit.CLASSIC_DHD.get(), ClassicDHDScreen::new);

        	MenuScreens.register(MenuInit.NAQUADAH_GENERATOR.get(), NaquadahGeneratorScreen::new);

        	MenuScreens.register(MenuInit.ZPM_HUB.get(), ZPMHubScreen::new);
        	
        	EntityRenderers.register(EntityInit.JAFFA_PLASMA.get(), PlasmaProjectileRenderer::new);
        	
        	BlockEntityRenderers.register(BlockEntityInit.SANDSTONE_SYMBOL.get(), SandstoneSymbolRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.STONE_SYMBOL.get(), StoneSymbolRenderer::new);
        	
        	BlockEntityRenderers.register(BlockEntityInit.TRANSPORT_RINGS.get(), TransportRingsRenderer::new);

        	BlockEntityRenderers.register(BlockEntityInit.UNIVERSE_STARGATE.get(), UniverseStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.MILKY_WAY_STARGATE.get(), MilkyWayStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.PEGASUS_STARGATE.get(), PegasusStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.CLASSIC_STARGATE.get(), ClassicStargateRenderer::new);
        }
    }
    
}
