import java.sql.*;
import java.util.Date;

public class Database {
    public static Connection con;

    public static Statement stmt;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?serverTimezone=UTC", "root", "1478");
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int add_user_bd(String name) {
        Date date = new Date();
        String time = date.toString();
        int a = 0;
        try {
            String query = "INSERT INTO chat.user ( username, created_at)  VALUES ('" + name + "', '" + time + "');";
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            a = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public static int add_chat_bd(String NameChat, String userID1, String userID2) {
        Date date = new Date();
        String time = date.toString();
        int a = 0;
        String query = "INSERT INTO chat.chat ( name, created_at)  VALUES ('" + NameChat + "', '" + time + "');";
        try {
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            a = rs.getInt(1);
            String m2m1user = "INSERT INTO chat.chat_user_map ( id_user, id_chat) VALUES ( " + userID1 + "," + a + " );";
            String m2m2user = "INSERT INTO chat.chat_user_map ( id_user, id_chat) VALUES ( " + userID2 + "," + a + " );";
            stmt.executeUpdate(m2m1user);
            stmt.executeUpdate(m2m2user);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public static int add_message(String chatID, String userID, String text) {
        Date date = new Date();
        String time = date.toString();
        int a = 0;
        String query = "INSERT INTO chat.message (chat, author, text, created_at) VALUES " +
                "( " + chatID + " , " + userID + " , '" + text + "', '" + time + "' )";
        try {
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            a = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public static StringBuilder get_chat(String userID) {
        ResultSet rs; //cписок всех чатов со всеми полями, отсортированный по времени создания последнего сообщения в чате (от позднего к раннему)
        StringBuilder builder = new StringBuilder();
        String query = "SELECT chat.id, name, created_at FROM chat.chat_user_map " +
                "JOIN chat.chat ON chat.id = chat.chat_user_map.id_chat " +
                "LEFT JOIN " +
                "(SELECT chat, max(created_at) AS last_msg_created FROM chat.message GROUP BY chat) " +
                "last_msg ON last_msg.chat = chat.id " +
                "WHERE id_user = " + userID + " " +
                "ORDER BY last_msg_created DESC;";
        try {
            rs = stmt.executeQuery(query);
            builder.append("ID  |   Chat_name    |  Created_at  ").append("\n");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String crated_at = rs.getString(3);
                builder.append(id).append("  |  ").append(name).append("  |  ").append(crated_at).append(" | ").append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static StringBuilder get_messages(String chatID) {
        ResultSet rs;
        StringBuilder builder = new StringBuilder();
        String query = "SELECT * FROM chat.message WHERE chat =" + chatID + " ORDER BY created_at;";

        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return builder;
        }
        try {
            builder.append("ID|Chat_id|Author_id|    text  |  Created_at ").append("\n");
            while (rs.next()) {
                int id = rs.getInt(1);
                int chat_id = rs.getInt(2);
                int author_id = rs.getInt(3);
                String text = rs.getString(4);
                String Created_at = rs.getString(5);
                builder.append(id).append(" |  ").append(chat_id).append("   |     ")
                        .append(author_id).append("  | ").append(text).append("   | ").append(Created_at).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static boolean Check_user(String user) {
        boolean rs = false;
        try {
            String query = "SELECT id FROM chat.user WHERE id = " + user + ";";
            ResultSet user_id = stmt.executeQuery(query);
            if (user_id.next()) {
                rs = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static boolean Check_chat(String chatId) {
        boolean rs = false;
        try {
            String query = "SELECT id FROM chat.chat WHERE id = " + chatId + ";";
            ResultSet chat_id = stmt.executeQuery(query);
            if (chat_id.next()) {
                rs = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static boolean Check_user_name(String name) {
        boolean rs = true;
        try {
            String query = "SELECT username FROM chat.user WHERE username = '" + name + "' ;";
            ResultSet username = stmt.executeQuery(query);
            if (username.next()) {
                rs = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static boolean Check_chat_name(String user) {
        boolean rs = true;
        try {
            String query = "SELECT name FROM chat.chat WHERE name = '" + user + "' ;";
            ResultSet chatname = stmt.executeQuery(query);
            if (chatname.next()) {
                rs = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

}
