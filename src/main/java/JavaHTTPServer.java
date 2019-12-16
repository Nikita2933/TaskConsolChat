import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class JavaHTTPServer {


    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(9000), 0);

        server.createContext("/users/add", new Handler.add_user());
        server.createContext("/chats/add", new Handler.chats_add());
        server.createContext("/messages/add", new Handler.add_message());
        server.createContext("/chats/get", new Handler.get_chat());
        server.createContext("/messages/get", new Handler.get_messages());
        server.setExecutor(null);
        server.start();


    }

}



