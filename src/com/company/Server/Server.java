package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by i.lapshinov on 15.06.2018.
 */
public class Server { // подключение

    public Vector<ClientHeader> clients;

    public Server() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;


        try {
            AuthService.connect();
            server  = new ServerSocket(8189);
            System.out.println("Сервер запущен");



            while(true)
            {
                socket = server.accept(); // сработает как толко подключится
                System.out.println("клиент подключен");
                //clients.add(new ClientHeader(this, socket)); // добавляем клиента в коллекцию
                new ClientHeader(this, socket);
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
            AuthService.disconnect();
        }
    }

    public void broadcastMsg(String msg)
    {
        for(ClientHeader o: clients)
        {
            o.sendMsg(msg);
        }
    }

    public void sendMesgOnliOnne(String name, String msg)
    {
        for(ClientHeader o: clients)
        {
            if (o.getNick().equals(name)) {
                o.sendMsg(msg);
            }
        }
    }



   public void subscribe(ClientHeader client) // добавляем клиента
   {
       clients.add(client);
   }

   public void unsubscriber (ClientHeader clinet)
   {
       clients.remove(clinet);
   }
}
