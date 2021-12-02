package org.example;


import lombok.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class OnlineClient extends Thread {
    public static final Logger log = App.log;
    private static final List<OnlineClient> ONLINE_CLIENTS = Server.ONLINE_CLIENTS;


    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private UUID clientID;

    private String clientName;

    @Setter(AccessLevel.NONE)
    private PrintWriter out;

    @Setter(AccessLevel.NONE)
    private BufferedReader in;

    OnlineClient(Socket socket) {
        this.clientID = UUID.randomUUID();
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        try {

            clientName = in.readLine();
            message = "Пользователь " + clientName + " присоединился к чату." +
                    "\n\t\t\tКоличество пользователей в сети: " + ONLINE_CLIENTS.size();
            sendMessageToAllClients(message);
            log.info("Пользователю {} присвоено ИМЯ: '{}'.", clientID, clientName);


            while (true) {
                message = in.readLine();
                log.info("Пользователь {}: " + message, clientID);
                if ("/exit".equalsIgnoreCase(message)) {
                    message = "Пользователь " + clientName + " покинул чат." +
                            "\nКоличество пользователей в сети: " + (ONLINE_CLIENTS.size() - 1);
                    log.info("Пользователь {} с именем {} покинул чат. " +
                            "Количество пользователей в сети: {}.}", clientID, clientName, (ONLINE_CLIENTS.size() - 1));
                    sendMessageToAllClients(message);
                    ONLINE_CLIENTS.remove(this);

                    break;
                } else if (message != null) {
                    sendMessageToAllClients(message);
                }
            }
        } catch (SocketException socketException) {
            log.info("Пользователь {} с именем {} покинул чат. " +
                    "Количество пользователей в сети: {}.}", clientID, clientName, (ONLINE_CLIENTS.size() - 1));
            sendMessageToAllClients("Пользователь " + clientName + " покинул чат." +
                    "\nКоличество пользователей в сети: " + (ONLINE_CLIENTS.size() - 1));
            ONLINE_CLIENTS.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessageToAllClients(String message) {
        for (OnlineClient client : ONLINE_CLIENTS) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }

    private void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
}
