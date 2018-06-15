package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by i.lapshinov on 15.06.2018.
 */
public class Server { // подключение

    private Vector<ClientHeader> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;


        try {
            server  = new ServerSocket(8189);
            System.out.println("Сервер запущен");



            while(true)
            {
                socket = server.accept(); // сработает как толко подключится
                System.out.println("клиент подключен");
                clients.add(new ClientHeader(this, socket)); // добавляем клиента в коллекцию
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg)
    {
        for(ClientHeader o: clients)
        {
            o.sendMsg(msg);
        }
    }
}
