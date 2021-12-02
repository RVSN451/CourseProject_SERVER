package org.example;


import org.slf4j.Logger;

import javax.inject.Singleton;
import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Singleton
public class Server extends Thread {
    public static final Logger log = App.log;
    public static final List<OnlineClient> ONLINE_CLIENTS = new CopyOnWriteArrayList<>();
    private static final File setting = new File("src/main/resources/setting.txt");
    private static BufferedInputStream bis;

    static {
        try {
            bis = new BufferedInputStream(new FileInputStream(setting), 200);
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }

    public static int settingPort(BufferedInputStream bis) throws IOException {
        StringBuilder bisString = new StringBuilder();
        int i;
        while ((i = bis.read()) != -1) {
            bisString.append((char) i);
        }
        return Integer.parseInt(bisString.toString().replace("port=", ""));
    }

    @Override
    public void run() {
        int port = 0;
        try {
            port = settingPort(bis);
        } catch (NullPointerException nullPointerException) {
            log.error("Файл setting.txt не найден. " + nullPointerException);
            App.stopApplication();
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Ошибка чтения файла setting.txt.");
            App.stopApplication();
        } catch (IOException e) {
            log.error("Ошибка чтения файла setting.txt." + e);
            App.stopApplication();
        }
        try (final var serverSocket = new ServerSocket(port)) {
            if (port > 1023 && port < 65536) {
                log.info("Сервер запущен на порту - " + port + ".");
            }

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
            App.stopApplication();
        }
    }
}
