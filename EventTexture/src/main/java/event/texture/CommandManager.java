package event.texture;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandManager implements CommandExecutor {
    DataBaseManager plugin = new DataBaseManager();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("이벤트추가")) {
            try {
                this.plugin.updateEvent(args[0], Integer.parseInt(args[1]));
                sender.sendMessage("이벤트를 성공적으로 추가하였습니다!");
            } catch (Exception var20) {
                sender.sendMessage("이벤트 추가에 실패했습니다.");
            }
        } else if (command.getName().equals("이벤트비활성화")) {
            try {
                this.plugin.disableEvent(args[0]);
                sender.sendMessage("이벤트를 성공적으로 비활성화 하였습니다!");
            } catch (Exception var19) {
                sender.sendMessage("이벤트 비활성화에 실패했습니다.");
            }
        } else if (command.getName().equals("이벤트활성화")) {
            try {
                this.plugin.enableEvent(args[0]);
                sender.sendMessage("이벤트를 성공적으로 활성화 하였습니다!");
            } catch (Exception var18) {
                sender.sendMessage("이벤트 활성화에 실패했습니다.");
            }
        } else if (command.getName().equals("이벤트아이템")) {
            Player p;
            ItemMeta meta;
            if (args[0].equals("추가")) {
                try {
                    p = (Player) sender;
                    meta = p.getInventory().getItemInMainHand().getItemMeta();
                    String item = p.getInventory().getItemInMainHand().getType().toString();
                    if (meta.hasLore()) {
                        List<String> l = meta.getLore();
                        String lore = StringUtils.join(l, "/");
                        this.plugin.updateEventItem(item, meta.getDisplayName(), lore, args[1]);
                    } else {
                        this.plugin.updateEventItem(item, meta.getDisplayName(), "", args[1]);
                    }

                    sender.sendMessage("이벤트아이템을 성공적으로 추가하였습니다!");
                } catch (Exception var17) {
                    sender.sendMessage("이벤트아이템 추가에 실패했습니다.");
                }
            } else if (args[0].equals("제거")) {
                try {
                    p = (Player) sender;
                    meta = p.getInventory().getItemInMainHand().getItemMeta();
                    this.plugin.deleteEventItem(meta.getDisplayName());
                    sender.sendMessage("이벤트아이템을 성공적으로 제거하였습니다!");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] serializedMember;
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(baos);

                        try {
                            oos.writeObject(p);
                            serializedMember = baos.toByteArray();
                        } catch (Throwable var14) {
                            try {
                                oos.close();
                            } catch (Throwable var13) {
                                var14.addSuppressed(var13);
                            }

                            throw var14;
                        }

                        oos.close();
                    } catch (Throwable var15) {
                        try {
                            baos.close();
                        } catch (Throwable var12) {
                            var15.addSuppressed(var12);
                        }

                        throw var15;
                    }

                    baos.close();
                    System.out.println(new String(serializedMember));
                } catch (Exception var16) {
                    sender.sendMessage("존재하지 않는 이벤트아이템 입니다.");
                }
            }
        } else if (command.getName().equals("이벤트보상") && args[0].equals("획득")) {
            try {
                ResultSet event = this.plugin.findEvent(args[1]);
                event.next();

                if (event.getString("TYPE") != null) {
                    if (0 == event.getInt("ENABLE")) {
                        sender.sendMessage("현재 진행되고 있지 않은 이벤트입니다.");
                        return true;
                    }

                    ResultSet items = this.plugin.getItem(args[1]);
                    items.next();
                    //sender.sendMessage(items.getString("ITEMNAME"));
                    Inventory inventory = Bukkit.createInventory((InventoryHolder) null, 9, event.getString("TYPE"));

                    for (int cnt = 0; cnt < 9 && items.next(); ++cnt) {
                        ItemStack item = new ItemStack(Material.valueOf(items.getString("ITEMNAME")));
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        meta.setDisplayName("[" + event.getString("TYPE") + "] " + items.getString("CUSTOMNAME"));
                        meta.setLore(Arrays.asList(items.getString("LORE").split("/")));
                        meta.setCustomModelData(event.getInt("CODE"));
                        item.setItemMeta(meta);
                        inventory.addItem(item);
                    }
                    Player p = (Player) sender;
                    p.openInventory(inventory);
                } else {
                    sender.sendMessage("존재하지 않는 이벤트입니다.");
                }
            } catch (SQLException var21) {
                sender.sendMessage("오류.");
            }
        } else if (command.getName().equals("이벤트목록")) {
            try {
                ResultSet eventlist = this.plugin.eventList();
                eventlist.next();
                sender.sendMessage("이벤트 목록 : ");
                while(eventlist.next()) {
                    sender.sendMessage(eventlist.getString("TYPE") + " ");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }
}