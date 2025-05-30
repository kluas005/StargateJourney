package net.povstalec.sgjourney.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.common.block_entities.tech.TransportRingsEntity;
import net.povstalec.sgjourney.common.init.ItemInit;

public class RingRemoteItem extends Item
{
	public RingRemoteItem(Properties properties)
	{
		super(properties);
	}
	
	protected List<TransportRingsEntity> getNearbyTransportRings(Level level, BlockPos blockPos, int maxDistance)
	{
		List<TransportRingsEntity> transporters = new ArrayList<TransportRingsEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = level.getChunk(blockPos.east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof TransportRingsEntity transportRings)
						transporters.add(transportRings);
				});
			}
		}
		
		return transporters;
	}
	
	private double distance(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public Optional<TransportRingsEntity> findNearestTransportRings(Level level, BlockPos blockPos, int maxDistance)
	{
		List<TransportRingsEntity> transporters = getNearbyTransportRings(level, blockPos, maxDistance);
		
		transporters.sort((stargateA, stargateB) ->
				Double.valueOf(distance(blockPos, stargateA.getBlockPos()))
				.compareTo(Double.valueOf(distance(blockPos, stargateB.getBlockPos()))));
		
		if(!transporters.isEmpty())
			return Optional.of(transporters.get(0));
		
		return Optional.empty();
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		
		if(player.isShiftKeyDown() && !level.isClientSide())
		{
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			
			if(offHandStack.is(ItemInit.RING_REMOTE.get()))
			{
				IItemHandler cap = offHandStack.getCapability(Capabilities.ItemHandler.ITEM);
				if(cap != null)
				{
					ItemStack returnStack;
					if(!mainHandStack.isEmpty())
						returnStack = cap.insertItem(0, mainHandStack, false);
					else
						returnStack = cap.extractItem(0, 1, false);
					
					player.setItemInHand(InteractionHand.MAIN_HAND, returnStack);
				}
				
			}
		}
		else if(!player.isShiftKeyDown())
		{
			/*ItemStack stack = player.getItemInHand(hand);
			if(!canActivate(stack))
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_memory_crystal").withStyle(ChatFormatting.BLUE), true);
			else
			{
				if(!level.isClientSide())
				{
					IItemHandler cap = stack.getCapability(Capabilities.ItemHandler.ITEM);
					if(cap != null)
					{
						ItemStack crystalStack = cap.getStackInSlot(0);
						
						int[] coordinates = null;
						
						for(int i = 0; i < MemoryCrystalItem.getMemoryListSize(crystalStack); i++)
				        {
				        	coordinates = MemoryCrystalItem.getCoordinatesAt(crystalStack, i);
				        	
				        	if(coordinates != null)
				        		break;
				        }
						
						if(coordinates != null)
						{
							BlockPos targetPos = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
							
							Optional<TransportRingsEntity> transportRings = findNearestTransportRings(level, player.blockPosition(), 16);
							if(transportRings.isPresent())
							{
								if(level.getBlockEntity(targetPos) instanceof TransportRingsEntity targetRings)
								{
									if(transportRings.get().canTransport() && targetRings.canTransport())
									{
										transportRings.get().activate(targetPos);
									}
									else
										player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.BLUE), true);
								}
							}
							else
								player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.BLUE), true);
						}
						else
							player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_coordinates").withStyle(ChatFormatting.BLUE), true);
					};
					
					
				}
			}*/
		}
		
		
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
	
	/*public static boolean canActivate(ItemStack stack)
	{
		if(stack.is(ItemInit.RING_REMOTE.get()))
		{
			Optional<Boolean> canActivate = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> !itemHandler.getStackInSlot(0).isEmpty());
			
			return canActivate.isPresent() ? canActivate.get() : false;
		}
		
		return false;
	}
	
	private int[] findFirstCoords(ItemStack memoryCrystal)
	{
		int[] address = new int[0];
		for(int i = 0; i < MemoryCrystalItem.getMemoryListSize(memoryCrystal); i++)
    	{
        	address = MemoryCrystalItem.getAddressAt(memoryCrystal, i);
        	
        	if(address.length > 0)
        		return address;
    	}
		return address;
	}
	
	@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
        {
        	ItemStack memoryCrystal = itemHandler.getStackInSlot(0);
        	
        	if(memoryCrystal.getItem() instanceof MemoryCrystalItem)
        	{
                tooltipComponents.add(Component.translatable("item.sgjourney.memory_crystal").withStyle(ChatFormatting.BLUE));

            	int[] firstCoords = findFirstCoords(memoryCrystal);
            	
            	if(firstCoords.length == 3)
            		tooltipComponents.add(Component.literal("X: " + firstCoords[0] + " Y: " + firstCoords[1] + " Z: " + firstCoords[2]).withStyle(ChatFormatting.YELLOW));
                
                super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        	}
        });

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }*/
	
	/*public BlockPos getNearestRings(CompoundTag nbtTag, BlockPos center, int maxDistance)
	{
		this.center = center;
		int[] distance = {maxDistance, maxDistance, maxDistance};
		this.distance = distance;

		this.tag = nbtTag.copy();
		Set<String> list = tag.getAllKeys();
		
		list.stream().forEach(this::ringsNetworking);
		System.out.println(list);
		System.out.println("Closest rings at X: " + target.getX() + " Y: " + target.getY() + " Z: " + target.getZ());
		
		return this.target;
	}
	
	private void ringsNetworking(String ringsID)
	{
		int targetX = tag.getIntArray(ringsID)[0];
		int targetY = tag.getIntArray(ringsID)[1];
		int targetZ = tag.getIntArray(ringsID)[2];
		
		int distanceX = Math.abs(targetX - center.getX());
		int distanceY = Math.abs(targetY - center.getY());
		int distanceZ = Math.abs(targetZ - center.getZ());
		
		if(distanceX <= distance[0] && distanceY <= distance[1] && distanceZ <= distance[2])
		{
			this.distance[0] = distanceX;
			this.distance[1] = distanceY;
			this.distance[2] = distanceZ;
			
			this.target = new BlockPos(targetX, targetY, targetZ);
		}
	}*/
	
	
	
	public static class ItemHandler extends ComponentItemHandler
	{
		public ItemHandler(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component)
		{
			super(parent, component, 1);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			return stack.is(ItemInit.MEMORY_CRYSTAL.get());
		}
	}
}
