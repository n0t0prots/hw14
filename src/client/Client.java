package client;
import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static volatile boolean exit = false;

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.startClient();
    }

    private void startClient() {
        try {
            try {
                clientSocket = new Socket("localhost", 4004);
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


                ReadMsg readMsg = new ReadMsg();
                readMsg.start();
                WriteMsg writeMsg = new WriteMsg();
                writeMsg.start();

                writeMsg.join();
                readMsg.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                out.close();

            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    private class ReadMsg extends Thread {
        @Override
        public void run() {

            String str;
            try {
                while (!exit) {
                    str = in.readLine();
                    System.out.println(str);
                    if (str.equals("stop")) {

                        exit = true;
                    }
                }
            } catch (IOException e) {

            }
        }
    }

    private class WriteMsg extends Thread {

        @Override
        public void run() {
            while (!exit) {
                String userWord;
                try {
                    userWord = reader.readLine();
                    if (userWord.equals("stop")) {
                        out.write("stop" + "\n");
                        exit = true;
                    } else {
                        out.write(userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}