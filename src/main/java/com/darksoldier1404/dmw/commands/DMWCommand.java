package com.darksoldier1404.dmw.commands;

import com.darksoldier1404.dmw.MultiWorld;
import com.darksoldier1404.dmw.functions.DMWFunction;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class DMWCommand implements CommandExecutor, TabCompleter {
    private String prefix = MultiWorld.prefix;



    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(prefix + "/dmw create <world> <type> <GS> <기본높이> - 월드를 생성합니다.");
            sender.sendMessage(prefix + "NORMAL, NETHER, END, FLAT, VOID");
            sender.sendMessage(prefix + "기본 높이: (default: 64), 정수만 입력.");
            sender.sendMessage(prefix + "기본 높이 설정 해당 : FLAT");
            sender.sendMessage(prefix + "GS = 구조물 생성 여부 (true, false)");
            sender.sendMessage(prefix + "/dmw tp <world> - 해당 월드로 이동합니다.");
            sender.sendMessage(prefix + "/dmw delete <world> - 해당 월드를 삭제합니다. (!!!복구 불가!!!)");
            sender.sendMessage(prefix + "/dmw info <world> - 해당 월드 정보를 보여줍니다.");
            sender.sendMessage(prefix + "/dmw dif <world> <난이도> - 해당 월드의 난이도를 설정합니다.");
            sender.sendMessage(prefix + "/dmw rule <world> <게임룰> <값> - 해당 월드의 게임룰을 설정합니다.");
            sender.sendMessage(prefix + "/dmw spawn - 서있는 위치를 해당 월드의 스폰지점으로 설정합니다.");
            sender.sendMessage(prefix + "/dmw list - 월드 목록을 보여줍니다.");
            return false;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if(!sender.hasPermission("dmw.create")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "생성할 월드 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                sender.sendMessage(prefix + "생성할 월드의 타입을 입력해주세요.");
                return false;
            }
            if (args.length == 3) {
                sender.sendMessage(prefix + "생성할 월드의 구조물 생성 여부를 입력해주세요.");
                return false;
            }
            if(args.length == 4) {
                sender.sendMessage(prefix + "기본 높이를 입력해주세요. (default: 64)");
                return false;
            }
            if (args.length == 5) {
                try {
                    DMWFunction.createWorld(sender, args[1], args[2], Boolean.parseBoolean(args[3]), Integer.parseInt(args[4]));
                    return false;
                } catch (Exception e) {
                    sender.sendMessage(prefix + "명령어를 다시 확인해주세요.");
                    return false;
                }
            }
        }
        if (args[0].equalsIgnoreCase("tp")) {
            if(!sender.hasPermission("dmw.tp")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "이동할 월드 이름을 입력해주세요.");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "플레이어만 이동할 수 있습니다.");
                return false;
            }
            Player p = (Player) sender;
            if (args.length == 2) {
                if (MultiWorld.customWorlds.containsKey(args[1])) {
                    p.teleport(MultiWorld.customWorlds.get(args[1]).getSpawnLocation());
                    return false;
                } else {
                    sender.sendMessage(prefix + "해당 월드가 존재하지 않습니다.");
                    return false;
                }
            }
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if(!sender.hasPermission("dmw.delete")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "삭제할 월드 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                DMWFunction.deleteWorld(sender, args[1]);
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("info")) {
            if(!sender.hasPermission("dmw.info")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix + "월드 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                if (MultiWorld.customWorlds.containsKey(args[1])) {
                    try {
                        DMWFunction.displayWorldInfo(sender, args[1]);
                    } catch (IOException e) {
                        sender.sendMessage(prefix + "월드 정보를 불러오는데 실패했습니다, 폴더가 존재하지 않습니다.");
                    }
                    return false;
                }
            }
        }
        if(args[0].equalsIgnoreCase("spawn")) {
            if(!sender.hasPermission("dmw.spawn")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if(!(sender instanceof Player)) {
                sender.sendMessage(MultiWorld.prefix + "플레이어만 스폰지점을 설정할 수 있습니다.");
                return false;
            }
            DMWFunction.setDefaultSpawnLocation((Player) sender);
            return false;
        }
        if(args[0].equalsIgnoreCase("dif")) {
            if(!sender.hasPermission("dmw.dif")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if(args.length == 1) {
                sender.sendMessage(prefix + "월드를 입력해주세요.");
                return false;
            }
            if(args.length == 2) {
                sender.sendMessage(prefix + "난이도를 입력해주세요.");
                return false;
            }
            if(args.length == 3) {
                DMWFunction.changeDifficult(sender, args[1], args[2]);
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("list")) {
            if(!sender.hasPermission("dmw.list")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            sender.sendMessage(prefix + "월드 목록입니다.");
            MultiWorld.customWorlds.keySet().forEach(sender::sendMessage);
            return false;
        }
        if(args[0].equalsIgnoreCase("rule")) {
            if(!sender.hasPermission("dmw.rule")) {
                sender.sendMessage(prefix + "당신은 이 명령어를 사용할 권한이 없습니다.");
                return false;
            }
            if(args.length == 1) {
                sender.sendMessage(prefix + "월드를 입력해주세요.");
                return false;
            }
            if(args.length == 2) {
                sender.sendMessage(prefix + "게임룰을 입력해주세요.");
                return false;
            }
            if(args.length == 3) {
                sender.sendMessage(prefix + "값을 입력해주세요.");
                return false;
            }
            DMWFunction.changeGameRule(sender, args[1], args[2], args[3]);
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "tp", "delete", "info", "spawn", "list", "dif");
        }
        if (args.length == 2) {
            return MultiWorld.customWorlds.keySet().stream().collect(Collectors.toList());
        }
        if (args.length == 3) {
            if(args[0].equalsIgnoreCase("dif")) {
                return Arrays.asList("EASY", "NORMAL", "HARD", "PEACEFUL");
            }
            if(args[0].equalsIgnoreCase("rule")) {
                ArrayList<String> list = new ArrayList<>();
                for(GameRule rule : GameRule.values()) {
                    list.add(rule.getName());
                }
                return list;
            }
        }
        return null;
    }
}
