package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.ServerSocket;
;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Singleton
public class Server extends Thread {
    public final static Logger log = LoggerFactory.getLogger(Server.class);
    public static final List<OnlineClient> ONLINE_CLIENTS = new CopyOnWriteArrayList<>();
    private static final int port = 8080;

    @Override
    public void run() {
        log.info("Сервер запущен на порту - " + port + ".");
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                OnlineClient newClient = new OnlineClient(socket);
                ONLINE_CLIENTS.add(newClient);
                Thread client = new Thread(newClient);
                client.setName("Client" + socket.getInetAddress() + "_" + socket.getPort());
                client.setDaemon(true);
                client.start();
                log.info("Пользователь {} присоединился к чату. Socket: {}" +
                        ". Количество пользователей в сети: {}.", newClient.getClientID(), socket, ONLINE_CLIENTS.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Сервер завершил работу.");
        }
    }
}
