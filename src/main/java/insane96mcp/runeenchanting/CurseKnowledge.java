package insane96mcp.runeenchanting;

import insane96mcp.runeenchanting.runes.Rune;
import insane96mcp.runeenchanting.setup.REAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurseKnowledge {
    public static boolean isLearned(@Nullable Player player, Holder<Rune> curse) {
        if (player == null)
            return false;
        return player.getData(REAttachments.LEARNED_CURSES).contains(curse);
    }

    public static List<Holder<Rune>> getLearned(ServerPlayer player) {
        return player.getData(REAttachments.LEARNED_CURSES);
    }

    public static Map<Holder<Rune>, Integer> getProgressMap(ServerPlayer player) {
        return player.getData(REAttachments.CURSE_LEARN_PROGRESS);
    }

    public static int getProgress(ServerPlayer player, Holder<Rune> curse) {
        return getProgressMap(player).getOrDefault(curse, 0);
    }

    public static void addProgress(ServerPlayer player, Holder<Rune> curse, int amount) {
        if (amount == 0 || isLearned(player, curse))
            return;
        setProgress(player, curse, getProgress(player, curse) + amount);
    }

    ///Sets progress towards learning a curse to an absolute value. Learns the curse immediately if it reaches the threshold.
    public static void setProgress(ServerPlayer player, Holder<Rune> curse, int amount) {
        if (isLearned(player, curse))
            return;
        if (amount >= RuneFeature.curseLearnThreshold) {
            removeProgress(player, curse);
            learn(player, curse);
            return;
        }
        Map<Holder<Rune>, Integer> progress = new HashMap<>(getProgressMap(player));
        progress.put(curse, Math.max(amount, 0));
        player.setData(REAttachments.CURSE_LEARN_PROGRESS, progress);
    }

    private static void removeProgress(ServerPlayer player, Holder<Rune> curse) {
        Map<Holder<Rune>, Integer> current = getProgressMap(player);
        if (!current.containsKey(curse))
            return;
        Map<Holder<Rune>, Integer> progress = new HashMap<>(current);
        progress.remove(curse);
        player.setData(REAttachments.CURSE_LEARN_PROGRESS, progress);
    }

    public static void learn(ServerPlayer player, Holder<Rune> curse) {
        List<Holder<Rune>> learned = player.getData(REAttachments.LEARNED_CURSES);
        if (learned.contains(curse))
            return;
        learned = new ArrayList<>(learned);
        learned.add(curse);
        player.setData(REAttachments.LEARNED_CURSES, learned);
        removeProgress(player, curse);
        player.displayClientMessage(Component.translatable("curse_learned", curse.value().getNameComponent()), false);
        player.playSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1f);
    }

    ///Returns whether the curse was known before being forgotten.
    public static boolean forget(ServerPlayer player, Holder<Rune> curse) {
        List<Holder<Rune>> learned = player.getData(REAttachments.LEARNED_CURSES);
        removeProgress(player, curse);
        if (!learned.contains(curse))
            return false;
        learned = new ArrayList<>(learned);
        learned.remove(curse);
        player.setData(REAttachments.LEARNED_CURSES, learned);
        return true;
    }

    public static void resetAll(ServerPlayer player) {
        player.setData(REAttachments.LEARNED_CURSES, new ArrayList<>());
        player.setData(REAttachments.CURSE_LEARN_PROGRESS, new HashMap<>());
    }
}
