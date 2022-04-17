package event.texture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseManager {
    Texture plugin = Texture.getPlugin(Texture.class);

    public DataBaseManager() {
    }

    public void updateEvent(String type, Integer code) {
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("INSERT INTO " + this.plugin.eventTable + "(TYPE, CODE, ENABLE) VALUES(?, ?, 1)");
            statement.setString(1, type);
            statement.setInt(2, code);
            statement.executeUpdate();
        } catch (SQLException var4) {
            var4.printStackTrace();
            System.out.println("이미 있는 이벤트입니다.");
        }

    }

    public void disableEvent(String type) {
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("UPDATE " + this.plugin.eventTable + " SET ENABLE = 0 WHERE TYPE = ?");
            statement.setString(1, type);
            statement.executeUpdate();
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public void enableEvent(String type) {
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("UPDATE " + this.plugin.eventTable + " SET ENABLE = 1 WHERE TYPE = ?");
            statement.setString(1, type);
            statement.executeUpdate();
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public void updateEventItem(String name, String cname, String lore, String event) {
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("INSERT INTO " + this.plugin.itemTable + "(ITEMNAME, CUSTOMNAME, LORE, TYPE) VALUES(?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, cname);
            statement.setString(3, lore);
            statement.setString(4, event);
            statement.executeUpdate();
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

    }

    public void deleteEventItem(String name) {
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("DELETE FROM " + this.plugin.itemTable + " WHERE CUSTOMNAME = ?");
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }
    public ResultSet eventList() throws SQLException {
        PreparedStatement eventList = this.plugin.getConnection().prepareStatement("Select TYPE FROM " + this.plugin.itemTable);
        return eventList.executeQuery();
    }

    public ResultSet findEvent(String event) throws SQLException {
        PreparedStatement eventType = this.plugin.getConnection().prepareStatement("SELECT * FROM " + this.plugin.eventTable + "WHERE TYPE = ?");
        eventType.setString(1, event);
        return eventType.executeQuery();
    }

    public ResultSet getItem(String event) throws SQLException {
        PreparedStatement eventType = this.plugin.getConnection().prepareStatement("SELECT * FROM " + this.plugin.itemTable + " WHERE TYPE = ?");
        eventType.setString(1, event);
        return eventType.executeQuery();
    }
}
