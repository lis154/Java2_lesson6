package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by i.lapshinov on 15.06.2018.
 */
public class ClientHeader { // реализация

    private Server server;
    private Socket socket;

    DataInputStream in;
    DataOutputStream out;

    public ClientHeader(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream((socket.getOutputStream()));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                    while(true)
                    {
                        String str = in.readUTF(); // читаем то что прислал клиент
                        if (str.equals("/end")) {
                            out.writeUTF("/serverclosed");
                            break;
                        }
                      //  System.out.println("Клиент  прислал " + str);
                        server.broadcastMsg(str);
                       // out.writeUTF(str); // отправляем сообщение
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {

        try {
            out.writeUTF(msg);  // отправляем сообщение
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
