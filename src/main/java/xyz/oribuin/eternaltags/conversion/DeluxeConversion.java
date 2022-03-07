package xyz.oribuin.eternaltags.conversion;

import dev.rosewood.rosegarden.RosePlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.oribuin.eternaltags.manager.TagsManager;
import xyz.oribuin.eternaltags.obj.Tag;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DeluxeConversion extends ConversionPlugin {

    private final TagsManager manager;

    public DeluxeConversion(RosePlugin plugin) {
        super(plugin);
        this.manager = this.getPlugin().getManager(TagsManager.class);
    }

    @Override
    public Map<String, Tag> getPluginTags() {
        final Map<String, Tag> convertedTags = new HashMap<>();
        final FileConfiguration config = YamlConfiguration.loadConfiguration(this.getTagsFile());
        final ConfigurationSection section = config.getConfigurationSection("deluxetags");
        if (section == null)
            return convertedTags;

        section.getKeys(false)
                .stream()
                .filter(s -> !manager.checkTagExists(s))
                .forEach(key -> {
                    final Tag tag = new Tag(key, StringUtils.capitalize(key), section.getString(key + ".tag"));

                    if (section.get(key + ".description") != null)
                        tag.setDescription(Collections.singletonList(section.getString(key + ".description")));

                    if (section.getString(key + ".permission") != null)
                        tag.setPermission(section.getString(key + ".permission"));

                    if (section.get(key + ".order") != null)
                        tag.setOrder(section.getInt(key + ".order"));

                    convertedTags.put(key, tag);
                });

        return convertedTags;
    }

    @Override
    public String getPluginName() {
        return "DeluxeTags";
    }

    @Override
    public File getTagsFile() {
        return new File(this.getPluginsFolder(), "config.yml");
    }

}