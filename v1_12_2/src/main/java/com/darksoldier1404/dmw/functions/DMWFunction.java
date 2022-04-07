package com.darksoldier1404.dmw.functions;

import com.darksoldier1404.dmw.MultiWorld;
import com.darksoldier1404.dmw.enums.GeneratorType;
import com.darksoldier1404.dmw.generators.FlatGenerator;
import com.darksoldier1404.dmw.generators.VoidGenerator;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class DMWFunction {

    public static void createWorld(CommandSender sender, String name, String type, boolean generateStructures, int height) {
        WorldCreator wc = new WorldCreator(name);
        GeneratorType gt = GeneratorType.valueOf(type.toUpperCase());
        if (gt == GeneratorType.VOID) {
            VoidGenerator vg = new VoidGenerator();
            wc.generator(vg);
            wc.type(WorldType.FLAT);
            wc.generateStructures(generateStructures);
            World w = wc.createWorld();
            init(sender, name, w, type, wc, generateStructures);
            return;
        }
        if (gt == GeneratorType.NORMAL) {
            wc.type(WorldType.NORMAL);
            wc.generateStructures(generateStructures);
            World w = wc.createWorld();
            init(sender, name, w, type, wc, generateStructures);
            return;
        }
        if (gt == GeneratorType.FLAT) {
            wc.type(WorldType.FLAT);
            FlatGenerator fg = new FlatGenerator(height);
            wc.generator(fg);
            wc.generateStructures(generateStructures);
            World w = wc.createWorld();
            init(sender, name, w, type, wc, generateStructures);
            return;
        }
        if (gt == GeneratorType.END) {
            wc.environment(World.Environment.THE_END);
            wc.generateStructures(generateStructures);
            World w = wc.createWorld();
            init(sender, name, w, type, wc, generateStructures);
            return;
        }
        if (gt == GeneratorType.NETHER) {
            wc.environment(World.Environment.NETHER);
            wc.generateStructures(generateStructures);
            World w = wc.createWorld();
            init(sender, name, w, type, wc, generateStructures);
        }
    }

    public static void init(CommandSender sender, String name, World w, String type, WorldCreator wc, boolean generateStructures) {
        try {
            w.setSpawnLocation(0, 65, 0);
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".Generator", type);
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".WorldType", wc.type().getName());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".GenerateStructures", generateStructures);
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.X", w.getSpawnLocation().getBlockX());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.Y", w.getSpawnLocation().getBlockY());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.Z", w.getSpawnLocation().getBlockZ());
            MultiWorld.customWorlds.put(name, w);
            Bukkit.getWorlds().add(w);
            sender.sendMessage(MultiWorld.prefix + name + " 커스텀 월드를 생성했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveConfig();
    }

    public static void deleteWorld(CommandSender sender, String name) {
        if (!MultiWorld.customWorlds.containsKey(name)) {
            sender.sendMessage(MultiWorld.prefix + "해당 월드는 존재하지 않거나 커스텀 월드가 아닙니다.");
            return;
        }
        World w = MultiWorld.customWorlds.get(name);
        Location defaultWorldLoc = Bukkit.getWorlds().get(0).getSpawnLocation();
        for (Player p : w.getPlayers()) {
            p.teleport(defaultWorldLoc);
        }
        for (Chunk c : w.getLoadedChunks()) {
            c.unload();
        }
        Bukkit.unloadWorld(w, false);
        File f = new File(w.getWorldFolder().getPath());
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory()) {
                    for (File file2 : file.listFiles()) {
                        if (file2.isDirectory()) {
                            for (File file3 : file2.listFiles()) {
                                file3.delete();
                            }
                        }
                        file2.delete();
                    }
                }
                file.delete();
            }
        }
        f.delete();
        MultiWorld.customWorlds.remove(name);
        MultiWorld.config.set("Settings.CustomWorlds." + name, null);
        sender.sendMessage(MultiWorld.prefix + name + " 커스텀 월드를 삭제했습니다.");
        saveConfig();
    }


    public static void loadAllCustomWorlds() {
        if (!(MultiWorld.config.getConfigurationSection("Settings.CustomWorlds") == null)) {
            for (String key : MultiWorld.config.getConfigurationSection("Settings.CustomWorlds").getKeys(false)) {
                World w = Bukkit.createWorld(new WorldCreator(key));
                Bukkit.getWorlds().add(w);
                Location loc = new Location(w, MultiWorld.config.getInt("Settings.CustomWorlds." + key + ".SpawnLocation.X"), MultiWorld.config.getInt("Settings.CustomWorlds." + key + ".SpawnLocation.Y"), MultiWorld.config.getInt("Settings.CustomWorlds." + key + ".SpawnLocation.Z"));
                w.setSpawnLocation(loc);
                MultiWorld.customWorlds.put(key, w);
                MultiWorld.getInstance().getLogger().info("커스텀 월드 " + key + "를 로드했습니다.");
            }
        }
    }

    public static void saveConfig() {
        ConfigUtils.savePluginConfig(MultiWorld.getInstance(), MultiWorld.config);
    }

    public static void displayWorldInfo(CommandSender sender, String name) throws IOException {
        if (!MultiWorld.customWorlds.containsKey(name)) {
            sender.sendMessage(MultiWorld.prefix + "해당 월드는 존재하지 않거나 커스텀 월드가 아닙니다.");
            return;
        }
        World w = MultiWorld.customWorlds.get(name);
        sender.sendMessage(MultiWorld.prefix + "커스텀 월드 " + name + " 정보");
        sender.sendMessage(MultiWorld.prefix + "월드 타입 : " + w.getWorldType().getName());
        sender.sendMessage(MultiWorld.prefix + "월드 스폰지점 : " + w.getSpawnLocation().getBlockX() + "," + w.getSpawnLocation().getBlockY() + "," + w.getSpawnLocation().getBlockZ());
        sender.sendMessage(MultiWorld.prefix + "월드 시드 : " + w.getSeed());
        FileTime time = Files.readAttributes(Path.of(w.getWorldFolder().getPath()), BasicFileAttributes.class).creationTime();
        sender.sendMessage(MultiWorld.prefix + "월드 생성시간 : " + new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(new Date(time.toMillis())));
        sender.sendMessage(MultiWorld.prefix + "월드 청크 갯수 : " + w.getLoadedChunks().length);
        sender.sendMessage(MultiWorld.prefix + "월드 엔티티 갯수 : " + w.getEntities().size());
        sender.sendMessage(MultiWorld.prefix + "월드 난이도 : " + w.getDifficulty());
        sender.sendMessage(MultiWorld.prefix + "월드 플레이어 목록 : " + w.getPlayers().stream().map(Player::getName).collect(Collectors.joining(", ")));
    }

    public static void setDefaultSpawnLocation(Player p) {
        String name = p.getWorld().getName();
        if (MultiWorld.customWorlds.containsKey(name)) {
            World w = MultiWorld.customWorlds.get(name);
            w.setSpawnLocation(p.getLocation());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.X", w.getSpawnLocation().getBlockX());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.Y", w.getSpawnLocation().getBlockY());
            MultiWorld.config.set("Settings.CustomWorlds." + name + ".SpawnLocation.Z", w.getSpawnLocation().getBlockZ());
            p.sendMessage(MultiWorld.prefix + "월드 스폰지점을 설정했습니다.");
            saveConfig();
        } else {
            p.sendMessage(MultiWorld.prefix + "커스텀 월드가 아닙니다.");
        }
    }

    public static void changeDifficult(CommandSender sender, String name, String difficult) {
        if (!MultiWorld.customWorlds.containsKey(name)) {
            sender.sendMessage(MultiWorld.prefix + "해당 월드는 존재하지 않거나 커스텀 월드가 아닙니다.");
            return;
        }
        World w = MultiWorld.customWorlds.get(name);
        Difficulty dif = Difficulty.valueOf(difficult.toUpperCase());
        w.setDifficulty(dif);
        MultiWorld.config.set("Settings.CustomWorlds." + name + ".Difficulty", dif.toString());
        sender.sendMessage(MultiWorld.prefix + "월드 난이도를 " + dif + "로 변경했습니다.");
        saveConfig();
    }

    public static void changeGameRule(CommandSender sender, String name, String rule, String value) {
        if (!MultiWorld.customWorlds.containsKey(name)) {
            sender.sendMessage(MultiWorld.prefix + "해당 월드는 존재하지 않거나 커스텀 월드가 아닙니다.");
            return;
        }
        World w = MultiWorld.customWorlds.get(name);
        if (Arrays.stream(w.getGameRules()).toList().contains(rule)) {
            w.setGameRuleValue(rule, value);
            sender.sendMessage(MultiWorld.prefix + "월드 게임룰 " + rule + "을 " + value + "로 변경했습니다.");
        } else {
            sender.sendMessage(MultiWorld.prefix + "존재하지 않는 게임룰 입니다.");
        }
        saveConfig();
    }
}
