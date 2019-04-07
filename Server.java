package com.company;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    static final int PORT = 8666;
    List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        //создаем клиентский сокет и серверный
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        //Оборачиваем в try catch чтобы поймат ошибку при создании сервера
        try{
            // Создаем сервер
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            //Бесконечный цикл, который ожидает когда подключится новый клиент,
            // при подключении клиента,
            // создаем нового client в ClientHandler с параметрами сервера (clientSocket, this)
            while (true){
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                //добавдяем нашего клиента в список clients
                clients.add(client);
                //создаем отдельный поток для ClientHandler,
                // чтобы подключения всех клиентов могла происходить паралельно
                new Thread(client).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                clientSocket.close();
                System.out.println("Сервер закончил свою работу");
                serverSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendMsgToAllClient(String msg){
        for (ClientHandler client : clients){
            client.sendMsg(msg);
        }

    }
//Для отправки определленному клиенту по нику
    public void sendMsgToSomeClient(String clientNick, String msg){
        for (ClientHandler client : clients){
            if(client.getNick().equals(clientNick)){
                client.sendMsg(msg);
                break;
            }
        }


    }

    public void removeClientFromServer(ClientHandler client){
        clients.remove(client);
    }
}
