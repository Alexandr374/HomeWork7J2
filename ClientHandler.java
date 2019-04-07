package com.company;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//ClientHandler имплементируем от Runnable, для создания потоков и реализации через run
public class ClientHandler implements Runnable{
    //создаем переменную хранящюю количество подключенных клиентов
    private static int clientCount = 0;
    private Server server;
    private Socket clientSocket;
    //Переменная для хранения исходящей информации
    private PrintWriter outMessage;
    //Для исходящей информации
    private Scanner inMessage;
    private String nick;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            clientCount++;
            this.nick = "User" + Integer.toString(clientCount);
            //Инициализируем сервер и сокет
            this.server = server;
            this.clientSocket = clientSocket;
            //инициализируем outMessage и inMessage
            this.outMessage = new PrintWriter(clientSocket.getOutputStream());
            this.inMessage = new Scanner(clientSocket.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        //закрываем все потоки, если клиент вышел
        }finally {
            this.close();
        }

    }

    private void close() {
        //Реализиуем метод который удаляет клиента из списка clients
        server.removeClientFromServer(this);
        clientCount--;
        server.sendMsgToAllClient("Количество клиентов в чате: " + clientCount);
    }

    public void sendMsg(String msg){
        outMessage.println(msg);
        outMessage.flush();

    }
//Метод для постоянной прослушки сообщений от клиентов
    @Override
    public void run() {
        try {
            server.sendMsgToAllClient("Подключился новый клиент");
            //Если клиент написал сообщение, оно прослушивается и отправляется всем клиентам
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMsg = inMessage.nextLine();
                    System.out.println(clientMsg);

                    if (clientMsg.equalsIgnoreCase("EXIT")) {
                        break;
                    }
                    server.sendMsgToAllClient(clientMsg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.close();
        }

    }

    public String getNick() {
        return nick;
    }
}
