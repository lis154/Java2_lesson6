package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Created by i.lapshinov on 15.06.2018.
 */
public class ClientHeader { // реализация

    private Server server;
    private Socket socket;

    DataInputStream in;
    DataOutputStream out;

    String nick = null;

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

                        if (str.startsWith("/auth")) // начинается ли строчка с /auth
                        {
                            String[] tockens = str.split(" ");
                            String newNick = AuthService.getNickByLoginAndPass(tockens[1], tockens[2]);
                            if (newNick != null && AuthService.stateLogin(tockens[1]))
                            {
                                sendMsg("/authok");
                                nick = newNick;
                                server.subscribe(ClientHeader.this);
                                AuthService.setTrueOnClient(tockens[1]);
                                break;
                            }
                            else
                            {
                                sendMsg("неверный логин/пароль");
                            }
                        }

                    }

                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/end")) {
                                AuthService.setFalseOnClient(ClientHeader.this.getNick());
                                out.writeUTF("/serverclosed");
                                break;
                            }
                            if (str.startsWith("/w"))
                            {
                                String[] message = str.split(" ");
                                String name = message[1];
                                String mesg = "";
                                for (int i = 2; i < message.length; i++) {
                                    mesg = mesg + message[i];
                                }
                                server.sendMesgOnliOnne(name, mesg);
                            }
                            else {
                                server.broadcastMsg(nick + ":" + str);
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscriber(ClientHeader.this);
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

    public Socket getSocket() {
        return socket;
    }

    public String getNick() {
        return nick;
    }
}
