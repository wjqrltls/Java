package event.texture;

import java.sql.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Texture extends JavaPlugin {
    private Connection connection;
    public String host, database, username, password, eventTable, itemTable;
    public int port;

    @Override
    public void onEnable() {

        loadConfig();
        mysqlSetup();

        getCommand("이벤트추가").setExecutor(new CommandManager());
        getCommand("이벤트활성화").setExecutor(new CommandManager());
        getCommand("이벤트비활성화").setExecutor(new CommandManager());
        getCommand("이벤트아이템").setExecutor(new CommandManager());
        getCommand("이벤트보상").setExecutor(new CommandManager());
        getCommand("이벤트목록").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        try { // using a try catch to catch connection errors (like wrong sql password...)
            if (connection!=null && !connection.isClosed()){ // checking if connection isn't null to
                // avoid receiving a nullpointer
                connection.close(); // closing the connection field variable.
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void mysqlSetup() {
        host = this.getConfig().getString("host");
        port = this.getConfig().getInt("port");
        database = this.getConfig().getString("database");
        username = this.getConfig().getString("username");
        password = this.getConfig().getString("password");
        eventTable = this.getConfig().getString("eventTable");
        itemTable = this.getConfig().getString("itemTable");

        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            setConnection(
                    DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/?user=" + this.username + "&password=" + this.password));
            Statement st = this.connection.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + this.database);
            st.executeUpdate("USE " + this.database);
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.eventTable + "(TYPE varchar(50), CODE int primary key, ENABLE tinyint)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.itemTable + "(ITEMNAME varchar(50), CUSTOMNAME varchar(50) primary key, LORE varchar(300), TYPE varchar(50))");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}