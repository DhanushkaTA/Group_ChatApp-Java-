import lk.ijse.chat_app.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try {
            Server server=new Server(new ServerSocket(4000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}