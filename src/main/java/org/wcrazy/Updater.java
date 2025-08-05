package org.wcrazy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private final JavaPlugin plugin;
    private final String githubUser;
    private final String githubRepo;

    public Updater(JavaPlugin plugin, String githubUser, String githubRepo) {
        this.plugin = plugin;
        this.githubUser = githubUser;
        this.githubRepo = githubRepo;
    }

    public void checkForUpdate() {
        if (!plugin.getConfig().getBoolean("updater")) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + githubUser + "/" + githubRepo + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "Updater/" + plugin.getName());

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                reader.close();

                JSONObject release = new JSONObject(json.toString());
                String latestVersion = release.getString("tag_name").replace("v", "").trim();
                String currentVersion = plugin.getDescription().getVersion().trim();
                String releaseUrl = release.getString("html_url");

                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    plugin.getLogger().warning("=======================================");
                    plugin.getLogger().warning(plugin.getName() + " is outdated!");
                    plugin.getLogger().warning("Current version: " + currentVersion);
                    plugin.getLogger().warning("Latest version: " + latestVersion);
                    plugin.getLogger().warning("Download the latest version here: " + releaseUrl);
                    plugin.getLogger().warning("=======================================");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates.");
                e.printStackTrace();
            }
        });
    }
}

