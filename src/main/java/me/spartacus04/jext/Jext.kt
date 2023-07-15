package me.spartacus04.jext

import me.spartacus04.jext.command.CommandsRegistrant
import me.spartacus04.jext.config.ConfigData.Companion.CONFIG
import me.spartacus04.jext.config.ConfigData.Companion.PLUGIN
import me.spartacus04.jext.config.ConfigManager
import me.spartacus04.jext.config.LanguageManager
import me.spartacus04.jext.config.LanguageManager.Companion.DISABLED_MESSAGE
import me.spartacus04.jext.config.LanguageManager.Companion.ENABLED_MESSAGE
import me.spartacus04.jext.config.LanguageManager.Companion.UPDATE_DETECTED
import me.spartacus04.jext.integrations.IntegrationsManager
import me.spartacus04.jext.listener.ListenersRegistrant
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * The class `Jext` is the main class of the plugin. It extends the `JavaPlugin` class, which is the main class of all
 * Bukkit plugins.
 *
 * @constructor Creates a new Jext plugin.
 */
@Suppress("unused")
class Jext : JavaPlugin() {

    /**
     * The function loads the plugin and sends an enabled message, but disables the plugin and prints the stack trace if an
     * exception occurs.
     */
    override fun onEnable() {
        try {
            load()
            Bukkit.getConsoleSender().sendMessage(ENABLED_MESSAGE)
        } catch (e: Exception) {
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
        }
    }

    /**
     * The function sends a message to the console when the plugin is disabled.
     */
    override fun onDisable() {
        Bukkit.getConsoleSender().sendMessage(DISABLED_MESSAGE)
    }

    /**
     * The `load()` function is responsible for initializing and setting up various components of the plugin. Here's a breakdown of what it does:
     * It sets the `PLUGIN` variable to the current instance of the plugin.
     * It loads the server version.
     * It registers the Jext namespace.
     * It loads the configuration.
     * It loads the language.
     * It registers the integrations.
     * It registers the commands.
     * It registers the listeners.
     * It checks for updates.
     * It starts metrics if allowed.
     */
    private fun load() {
        PLUGIN = this
        ServerVersion.load(this)
        JextNamespace.registerNamespace(this)
        ConfigManager.load(this)
        LanguageManager.load(this)
        IntegrationsManager.registerIntegrations(this)
        CommandsRegistrant.registerCommands(this)
        ListenersRegistrant.registerListeners(this)


        Updater(this).getVersion {
            if(it != description.version) {
                Bukkit.getConsoleSender().sendMessage(UPDATE_DETECTED)
            }
        }

        // Start metrics if allowed
        if(!CONFIG.ALLOW_METRICS) return

        val metrics = Metrics(this, 16571)

        metrics.addCustomChart(SimplePie("juke_gui") {
            when(CONFIG.JUKEBOX_BEHAVIOUR) {
                "legacy-gui" -> return@SimplePie "Legacy GUI"
                "gui" -> return@SimplePie "GUI"
                else -> return@SimplePie "Vanilla"
            }
        })
    }
}