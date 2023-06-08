package lk.ijse.chat_app;

import lk.ijse.chat_app.service.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket serverSocket;
    Socket localSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket=serverSocket;


        try {

            while (!serverSocket.isClosed()){
                this.localSocket=serverSocket.accept();
                System.out.println("New Client connected to server");

                ClientManager newClient=new ClientManager(localSocket);
                Thread thread=new Thread(newClient);
                thread.start();
            }

        }catch (IOException e){
            System.out.println(e.getMessage());
            System.out.println("Server couldn't creat !!!");
            e.printStackTrace();
            closeSever();
        }
    }

    public void closeSever(){

        try {
            if(serverSocket!=null){
                serverSocket.close();
            }
            if (localSocket!=null){
                localSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
