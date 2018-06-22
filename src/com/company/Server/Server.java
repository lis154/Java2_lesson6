package com.company.Server;

import com.sun.deploy.util.SessionState;

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

    public void broadcastMsg(ClientHeader from ,String msg)
    {
        for(ClientHeader o: clients)
        {
            if (!o.chackBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }
    }

    public void sendMesgOnliOnne(ClientHeader from, String name, String msg)
    {
        for(ClientHeader o: clients)
        {
            if (o.getNick().equals(name)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + name + ": " + msg);
                return;
            }
            from.sendMsg("клиент с ником " + name + "не найден");
        }
    }



   public void subscribe(ClientHeader client) // добавляем клиента
   {
       clients.add(client);
       broadCastClientList();
   }

   public void unsubscriber (ClientHeader clinet) // удаляем клиента
   {

       clients.remove(clinet);
       broadCastClientList();
   }


   public boolean isNickBusy (String nick) // проверка занят ли клиента или нет
   {
       for (ClientHeader o: clients) {
           if (o.getNick().equals(nick))
           {
               return true;
           }

       }
       return false;
   }

   public void broadCastClientList(){ // оправляем список клиентов
       StringBuilder sb = new StringBuilder();
       sb.append("/clientlist ");
       for (ClientHeader o: clients){
           sb.append(o.getNick() + " ");
       }
       String out = sb.toString();
       for (ClientHeader o: clients)
       {
           o.sendMsg(out);
       }
   }

}
