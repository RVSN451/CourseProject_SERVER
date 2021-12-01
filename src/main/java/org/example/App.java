package org.example;

public class App {
    static final int PORT = 8080;

    public static void main(String[] args) {


        Server server = new Server();
        server.setName("MainServer");
        server.setDaemon(true);
        server.start();

        while (true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
