package net.povstalec.sgjourney.common.blocks.tech;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.HeavyNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.LiquidizerMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

public class HeavyNaquadahLiquidizerBlock extends AbstractNaquadahLiquidizerBlock
{
	public static final MapCodec<HeavyNaquadahLiquidizerBlock> CODEC = simpleCodec(HeavyNaquadahLiquidizerBlock::new);

	public HeavyNaquadahLiquidizerBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected MapCodec<HeavyNaquadahLiquidizerBlock> codec()
	{
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new HeavyNaquadahLiquidizerEntity(pos, state);
	}
	
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
        if(!level.isClientSide()) 
        {
    		BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if(blockEntity instanceof AbstractNaquadahLiquidizerEntity crystallizer) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.heavy_naquadah_liquidizer");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new LiquidizerMenu.HeavyLiquidNaquadah(windowId, playerInventory, blockEntity);
        			}
        		};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), HeavyNaquadahLiquidizerEntity::tick);
    }

	@Override
	public Block getDroppedBlock()
	{
		return BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get();
	}
}
