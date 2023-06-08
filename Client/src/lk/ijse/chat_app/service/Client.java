package lk.ijse.chat_app.service;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import lk.ijse.chat_app.controller.AppController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {
    private Socket remoteSocket=null;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public Client(Socket remoteSocket,String clientUserName){
        this.remoteSocket=remoteSocket;
        this.clientUserName=clientUserName;
        try {
            this.bufferedReader=new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(remoteSocket.getOutputStream()));
            this.dataOutputStream=new DataOutputStream(remoteSocket.getOutputStream());
            this.dataInputStream=new DataInputStream(remoteSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error!!! Can't creat client");
            closeServer();
            throw new RuntimeException(e);
        }
        sendUserName(clientUserName);
    }

    public void closeServer(){
        try {
            if (remoteSocket != null){
                remoteSocket.close();
            }
            if (bufferedReader!=null){
                bufferedReader.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
        }catch (IOException e){
            System.out.println("Error!!! closing creat client");
            throw new RuntimeException(e);
        }
    }

    public void sendUserName(String clientUserName){
        try {
            bufferedWriter.write(clientUserName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error!!! Can't sent Client Username");
            closeServer();
            throw new RuntimeException(e);
        }
    }

    public void sentMessage(String messageToSent) {
        try {
//            bufferedWriter.write(messageToSent);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();
            dataOutputStream.writeUTF(messageToSent);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Error!!! Can't sent message to group");
            closeServer();
            throw new RuntimeException(e);
        }
    }

    public void receiveMessage(VBox vbox_messages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (remoteSocket.isConnected()){
                    try {
//                        String  receiveMessage= bufferedReader.readLine();
                        String receiveMessage=dataInputStream.readUTF();

                        if (receiveMessage.equals("iMg*->")){
//                            BufferedInputStream bufferedInputStream=new BufferedInputStream(remoteSocket.getInputStream());
////                            java.awt.Image image = ImageIO.read(bufferedInputStream);
//                            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);
////                            ImageIcon imageIcon = new ImageIcon(read);
//
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//
//                            byte[] array = byteArrayOutputStream.toByteArray();
//                            Image image = new Image(new ByteArrayInputStream(array));

//                            AppController.receiveImage(image,vbox_messages);
                            System.out.println("received image to client");
                            String username=dataInputStream.readUTF();

                            byte[] size = new byte[256];
                            dataInputStream.read(size);
                            int imgSize = ByteBuffer.wrap(size).asIntBuffer().get();

                            byte[] imageContent = new byte[imgSize];
                            dataInputStream.read(imageContent);

                            Image image = new Image(new ByteArrayInputStream(imageContent));
                            AppController.receiveImage(image,vbox_messages,username);

                            
                        }else {
                            AppController.receiveMessage(receiveMessage,vbox_messages);
                        }

                    } catch (IOException e) {
                        System.out.println("Error!!! Can't read received message from client");
                        closeServer();
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

//    public void sentImage(Image file) {
//
//        try {
//            FileInputStream fileInputStream=new FileInputStream(file.getAbsolutePath());
//            DataOutputStream dataOutputStream=new DataOutputStream(remoteSocket.getOutputStream());
//
//            byte[] sentImage=new byte[(int) file.length()];
//            fileInputStream.read(sentImage);
//
//            sentMessage("iMg*->");
//
//            dataOutputStream.writeInt(sentImage.length);
//            dataOutputStream.write(sentImage);
//            dataOutputStream.flush();
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
////        try {
////
////            BufferedImage image = ImageIO.read(file);
////            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////            ImageIO.write(image, "jpg", byteArrayOutputStream);
////
////            OutputStream outputStream = remoteSocket.getOutputStream();
////
////            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
////
////            sentMessage("iMg*->");
////
////            outputStream.write(size);
////            outputStream.write(byteArrayOutputStream.toByteArray());
////            outputStream.flush();
////
////        }catch (IOException e) {
////            throw new RuntimeException(e);
////        }
//
//
//    }

    public void sentImage(File file,String clientUserName) {
        try {
//            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(remoteSocket.getOutputStream());
//
//            Image imageToSent=new Image(file.toURI().toString());
//
//            BufferedImage bufferedImage=new BufferedImage(
//                    (int) imageToSent.getWidth(), (int) imageToSent.getHeight(),BufferedImage.TYPE_INT_ARGB);
////            ImageIO.write(bufferedImage,"jpg",bufferedOutputStream);

            BufferedImage bufferedImage = ImageIO.read(file);

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

            byte[] size=ByteBuffer.allocate(256).putInt(byteArrayOutputStream.size()).array();

            sentMessage("iMg*->");
            sentMessage(clientUserName);
            dataOutputStream.write(size);
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            dataOutputStream.flush();

            /**FileInputStream fileInputStream=new FileInputStream(file.getAbsolutePath());
            byte[] imageContentArray=new byte[(int) file.length()];
            fileInputStream.read(imageContentArray);

            sentMessage("iMg*->");
            sentMessage(clientUserName);
            dataOutputStream.writeInt(imageContentArray.length);
            dataOutputStream.write(imageContentArray);
            dataOutputStream.flush();*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
