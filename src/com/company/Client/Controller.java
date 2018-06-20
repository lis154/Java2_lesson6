package com.company.Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller {

    @FXML
    TextArea textAreaFields;

    @FXML
    TextField textField;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPannel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;  // устанавливаем связь с FXML

    private boolean isAuthorized; // флаг видимости


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    public void setAutorized (boolean isAuthorized)
    {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized)
        {
            upperPannel.setVisible(true);
            upperPannel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
        else
        {
            upperPannel.setVisible(false);
            upperPannel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }

    }


    public void connect(){
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAutorized(false); // не видим ничего кроме панели авторизации
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                    while (true)
                    {
                        String str = in.readUTF(); // считываем строчку с потока

                        if (str.startsWith("/authok")) // если пришел ответ ок
                        {
                            setAutorized(true); // покажи нам панель отправки сообщений и проваливаемся ниже
                            break;
                        } else
                        {
                            textAreaFields.appendText(str + "\n");
                        }

                    }
                        while (true) // работаем непосредственно с отправкой сообщения
                        {
                            String str = in.readUTF();
                            if (str.equals("/serverclosed")) {
                                break;
                            }
                            textAreaFields.appendText(str + "\n");
                        }


                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMsg()
    {

        try {
            out.writeUTF(textField.getText()); // отправляем на вывод
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //    textAreaFields.appendText(textField.getText() + "\n");
    //    textField.clear();
    //    textField.requestFocus();
    }

    public void tryToAuth()
    {
        if (socket == null || socket.isClosed())
        {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
