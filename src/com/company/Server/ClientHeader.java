package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by i.lapshinov on 15.06.2018.
 */
public class ClientHeader { // реализация

    private Server server;
    private Socket socket;

    DataInputStream in;
    DataOutputStream out;

    String nick = null;

    List<String> blackList;

    public ClientHeader(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream((socket.getOutputStream()));
            this.blackList = new ArrayList<String>();

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
                                if (!server.isNickBusy(newNick)) {
                                    sendMsg("/authok");
                                    nick = newNick;
                                    server.subscribe(ClientHeader.this);
                                    AuthService.setTrueOnClient(tockens[1]);
                                    break;
                                } else
                                {
                                    sendMsg ("Учетная запись используетсмя");
                                }
                            }
                            else
                            {
                                sendMsg("неверный логин/пароль");
                            }
                        }

                    }

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) { // блок отвечающий за служебные сообщения
                                if (str.equals("/end")) {
                                    AuthService.setFalseOnClient(ClientHeader.this.getNick());
                                    out.writeUTF("/serverclosed");
                                    break;
                                }
                                if (str.startsWith("/w")) {
                                    String[] message = str.split(" ", 3);
                                    String name = message[1];
                                    server.sendMesgOnliOnne(ClientHeader.this, name, message[2]);
                                }

                                if (str.startsWith("/blacklist "))
                                {
                                    String[] tokens = str.split(" ");
                                    blackList.add(tokens[1]);
                                    sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                                }
                            }
                            else {
                                server.broadcastMsg(ClientHeader.this,nick + ":" + str);
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

    public boolean chackBlackList (String nick)
    {
        return  blackList.contains(nick);
    }
}
