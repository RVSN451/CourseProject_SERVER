package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final long MAIN_THREAD_SLEEP = 2000L;
    static final Logger log = LoggerFactory.getLogger(App.class);
    private static boolean stopApp = false;

    public static void main(String[] args) {

        Thread.currentThread().setName("MAIN_THREAD");

        Server server = new Server();
        server.setName("SERVER_THREAD");
        server.setDaemon(true);
        server.start();

        while (!stopApp) {
            try {
                Thread.sleep(MAIN_THREAD_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopApplication() {
        log.error("Сервер завершил работу.");
        stopApp = true;
    }


}
