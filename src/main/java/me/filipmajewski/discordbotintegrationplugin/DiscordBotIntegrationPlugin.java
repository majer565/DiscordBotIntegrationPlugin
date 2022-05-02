package me.filipmajewski.discordbotintegrationplugin;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import me.filipmajewski.discordbotintegrationplugin.spigot_events.MessageReceivedEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordBotIntegrationPlugin extends JavaPlugin {

    private final String DISCORD_BOT_TOKEN = getConfig().getString("discord_bot_token");
    private boolean isDiscordBotConnected;
    private GatewayDiscordClient client;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            client = DiscordClientBuilder.create(DISCORD_BOT_TOKEN).build().login().block();
            isDiscordBotConnected = true;
            System.out.println("\u001b[32m" + "[DiscordBotIntegrationPlugin] Successfully loaded discord bot.\u001b[0m");
        } catch(Exception e) {
            System.out.println("\u001b[31m" + "[DiscordBotIntegrationPlugin] Error! You did not provide correct discord"
                    + " token! Edit token in config file.\u001b[0m"
            );
            getServer().getPluginManager().disablePlugin(this);
        }

        if(isDiscordBotConnected) {
            Guild guild = client.getGuildById(Snowflake.of(getConfig().getString("discord_guild_ID"))).block();
            MessageChannel channel = (MessageChannel) guild.getChannelById(Snowflake.of(getConfig().getString("discord_messages_textchannel_ID"))).block();

            getServer().getPluginManager().registerEvents(
                    new MessageReceivedEvent(channel),
                    this
            );
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(isDiscordBotConnected) {
            client.logout();
            isDiscordBotConnected = false;
            System.out.println("[DiscordBotIntegrationPlugin] Logging out...");
        }
    }
}
