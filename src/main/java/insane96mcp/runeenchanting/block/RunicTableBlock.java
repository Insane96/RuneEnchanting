package insane96mcp.runeenchanting.block;

import com.mojang.serialization.MapCodec;
import insane96mcp.runeenchanting.menu.RunicTableMenu;
import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

///Stores runes in unlimited quantity and lets players apply/remove them from tools through a GUI.
public class RunicTableBlock extends BaseEntityBlock {
    public static final MapCodec<RunicTableBlock> CODEC = simpleCodec(RunicTableBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public RunicTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicTableBlockEntity(pos, state);
    }

    ///Right-clicking with a rune stores the whole held stack
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!RunicTableMenu.isStorableRune(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof RunicTableBlockEntity runicTable))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        Holder<Rune> rune = stack.get(REDataComponents.STORED_RUNE);
        runicTable.addRune(rune, stack.getCount());
        //Doesn't consume the stack in creative
        stack.consume(stack.getCount(), player);
        level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof RunicTableBlockEntity runicTable && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(runicTable);
            //Sent after the open screen packet, otherwise the client would discard it
            if (serverPlayer.containerMenu instanceof RunicTableMenu menu)
                menu.syncStorage(serverPlayer);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof RunicTableBlockEntity runicTable)
            runicTable.dropContents(level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
