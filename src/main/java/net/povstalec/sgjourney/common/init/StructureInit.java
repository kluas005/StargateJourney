package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.structures.BuriedStargate;
import net.povstalec.sgjourney.common.structures.Cartouche;
import net.povstalec.sgjourney.common.structures.City;
import net.povstalec.sgjourney.common.structures.CommonStargate;
import net.povstalec.sgjourney.common.structures.GoauldTemple;
import net.povstalec.sgjourney.common.structures.JaffaHouse;
import net.povstalec.sgjourney.common.structures.StargateOutpost;
import net.povstalec.sgjourney.common.structures.StargatePedestal;
import net.povstalec.sgjourney.common.structures.StargateTemple;
import net.povstalec.sgjourney.common.structures.StargateVoidStructure;

public class StructureInit
{
	public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, StargateJourney.MODID);

	public static final DeferredHolder<StructureType<?>, StructureType<?>> COMMON_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("common_stargate", () -> typeConvert(CommonStargate.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> STARGATE_OUTPOST =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_outpost", () -> typeConvert(StargateOutpost.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> STARGATE_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_temple", () -> typeConvert(StargateTemple.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> STARGATE_PEDESTAL =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_pedestal", () -> typeConvert(StargatePedestal.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> BURIED_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("buried_stargate", () -> typeConvert(BuriedStargate.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> STARGATE_VOID_STRUCTURE =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_void_structure", () -> typeConvert(StargateVoidStructure.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> GOAULD_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("goauld_temple", () -> typeConvert(GoauldTemple.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> CITY =
            DEFERRED_REGISTRY_STRUCTURE.register("city", () -> typeConvert(City.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> CARTOUCHE =
            DEFERRED_REGISTRY_STRUCTURE.register("cartouche", () -> typeConvert(Cartouche.CODEC));
	
	public static final DeferredHolder<StructureType<?>, StructureType<?>> JAFFA_HOUSE =
            DEFERRED_REGISTRY_STRUCTURE.register("jaffa_house", () -> typeConvert(JaffaHouse.CODEC));
	
	private static <S extends Structure> StructureType<S> typeConvert(MapCodec<S> codec)
	{
        return () -> codec;
    }
	
	public static void register(IEventBus eventBus)
	{
		DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
	}
}
