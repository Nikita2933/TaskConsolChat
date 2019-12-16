import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Handler {
    static class add_user implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            OutputStream os = t.getResponseBody();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String a = new String(bytes);
            JSONObject obj = (JSONObject) JSONValue.parse(a);
            String username = (String) obj.get("username");
            is.close();
            boolean check_name = Database.Check_user_name(username);
            if (!check_name) {
                String send = "404 chat name duplicate \"" + username + "\" ";
                t.sendResponseHeaders(404, send.length());
                os.write(send.getBytes());
            } else {
                int name = Database.add_user_bd(username);
                String send = "Your user id: " + name + " ";
                t.sendResponseHeaders(200, send.length());
                os.write(send.getBytes());
            }
            os.close();
        }
    }

    static class chats_add implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            OutputStream os = t.getResponseBody();
            JSONParser parser = new JSONParser();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String a = new String(bytes);
            JSONObject jsonObject;
            String name = null;
            JSONArray jsonArray;
            String id1 = null;
            String id2 = null;
            try {
                jsonObject = (JSONObject) parser.parse(a);
                name = (String) jsonObject.get("name");
                jsonArray = (JSONArray) jsonObject.get("users");
                id1 = (String) jsonArray.get(0);
                id2 = (String) jsonArray.get(1);
            } catch (ParseException e) {
                e.printStackTrace();
                String b = "400 Bad Request  ";
                t.sendResponseHeaders(400, b.length());
                os.write(b.getBytes());
                os.close();
            }
            System.out.println(name);
            boolean user1 = Database.Check_user(id1);
            boolean user2 = Database.Check_user(id2);
            boolean chat = Database.Check_chat_name(name);
            System.out.println(id2);
            if (!user1) {
                String b = "404 User 1 not found ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
                os.close();
            }
            if (!user2) {
                String b = "404 User 2 not found ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
                os.close();
            }
            if (!chat) {
                String b = "404 chat name duplicate \"" + "\"";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
                os.close();
            } else
                try {
                    int idChat = Database.add_chat_bd(name, id1, id2);
                    if (idChat == 0) {
                        String send = ("404 Bad Request ");
                        t.sendResponseHeaders(404, send.length());
                        os.write(send.getBytes());
                        os.close();
                        is.close();
                    }
                    String send = (" Your chat id: " + idChat + " ");
                    t.sendResponseHeaders(200, send.length());
                    os.write(send.getBytes());
                    os.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    static class add_message implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            OutputStream os = t.getResponseBody();
            JSONParser parser = new JSONParser();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String a = new String(bytes);
            JSONObject jsonObject;
            String chatId = null;
            String authorId = null;
            String text = null;
            try {
                jsonObject = (JSONObject) parser.parse(a);
                chatId = (String) jsonObject.get("chat");
                authorId = (String) jsonObject.get("author");
                text = (String) jsonObject.get("text");
            } catch (Exception e) {
                e.printStackTrace();
                String b = "400 Bad Request  ";
                t.sendResponseHeaders(404, b.length());
                System.out.println(a);
                os.write(b.getBytes());
                os.close();
            }
            boolean chatid = Database.Check_chat(chatId);
            boolean userid = Database.Check_user(authorId);
            if (!userid) {
                String b = "404 User_id invalid ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
                os.close();
            }
            if (!chatid) {
                String b = "404 Chat_id invalid ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
                os.close();
            }
            if (userid && chatid) {
                try {
                    int idChat = Database.add_message(chatId, authorId, text);
                    String send = (" Your message id: " + idChat + " ");
                    t.sendResponseHeaders(200, send.length());
                    os.write(send.getBytes());
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class get_chat implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            OutputStream os = t.getResponseBody();
            JSONParser parser = new JSONParser();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String a = new String(bytes);
            JSONObject jsonObject;
            is.close();
            String userid = null;
            try {
                jsonObject = (JSONObject) parser.parse(a);
                userid = (String) jsonObject.get("user");

            } catch (Exception e) {
                e.printStackTrace();
                String b = "404 Bad Request ";
                t.sendResponseHeaders(404, b.length());
                System.out.println(a);
                os.write(b.getBytes());
                os.close();
            }


            boolean user = Database.Check_user(userid);
            if (!user) {
                String b = "404 User_id invalid ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
            } else {
                StringBuilder data = Database.get_chat(userid);
                t.sendResponseHeaders(200, data.length());
                os.write(data.toString().getBytes());
                os.close();
            }
            os.close();
        }
    }

    static class get_messages implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            OutputStream os = t.getResponseBody();
            JSONParser parser = new JSONParser();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String a = new String(bytes);
            JSONObject jsonObject;
            String chatid = null;
            try {
                jsonObject = (JSONObject) parser.parse(a);
                chatid = (String) jsonObject.get("chat");

            } catch (Exception e) {
                e.printStackTrace();
                String b = "404 Bad Request ";
                t.sendResponseHeaders(404, b.length());
                System.out.println(a);
                os.write(b.getBytes());
                os.close();
            }

            boolean chat = Database.Check_chat(chatid);
            if (!chat) {
                String b = "404 Chat_id invalid ";
                t.sendResponseHeaders(404, b.length());
                os.write(b.getBytes());
            } else {
                StringBuilder data = Database.get_messages(chatid);
                t.sendResponseHeaders(200, data.length());
                os.write(data.toString().getBytes());
                os.close();
            }
            os.close();

        }
    }
}

