package io.wdsj.asw.bukkit.listener;

import io.wdsj.asw.bukkit.setting.PluginSettings;
import io.wdsj.asw.bukkit.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;

import java.util.List;

import static io.wdsj.asw.bukkit.AdvancedSensitiveWords.*;
import static io.wdsj.asw.bukkit.util.TimingUtils.addProcessStatistic;
import static io.wdsj.asw.bukkit.util.Utils.messagesFilteredNum;

public class BroadCastListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBroadCast(BroadcastMessageEvent event) {
        if (!isInitialized || !settingsManager.getProperty(PluginSettings.CHAT_BROADCAST_CHECK)) return;
        String originalMessage = event.getMessage();
        if (settingsManager.getProperty(PluginSettings.PRE_PROCESS)) originalMessage = originalMessage.replaceAll(Utils.getPreProcessRegex(), "");
        long startTime = System.currentTimeMillis();
        List<String> censoredWordList = sensitiveWordBs.findAll(originalMessage);
        if (!censoredWordList.isEmpty()) {
            messagesFilteredNum.getAndIncrement();
            String processedMessage = sensitiveWordBs.replace(originalMessage);
            if (settingsManager.getProperty(PluginSettings.CHAT_METHOD).equalsIgnoreCase("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(processedMessage);
            }
            if (settingsManager.getProperty(PluginSettings.LOG_VIOLATION)) {
                Utils.logViolation("Broadcast(IP: None)(Chat)(BroadCast)", originalMessage + censoredWordList);
            }
            long endTime = System.currentTimeMillis();
            addProcessStatistic(endTime, startTime);
        }
    }
}
