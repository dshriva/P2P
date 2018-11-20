package com.multimedia.handler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * created by divya at 10/1/2018
 */
public class ServerTCPHandler {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public static void createTCPSocket(int serverPort, InetAddress ip) {
        System.out.println("Server says: ");
        ServerSocket serverSocket = null; // starts listening at the machine ip:port
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // peer needs to connect to server at port
        while (true) {
            try {
                System.out.println("TCP Socket for server created");

                Socket socket = serverSocket.accept();

                System.out.println("Yes!! Got a connection");

                //BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));

                FileInputStream fr = new FileInputStream("C:\\Users\\divya\\Desktop\\MS\\FilesTransfer\\image1.jpeg");
                byte[] b = new byte[20002];

                fr.read(b,0,b.length);

                OutputStream ostream = socket.getOutputStream();
                ostream.write(b,0,b.length);
                //PrintWriter pwrite = new PrintWriter(ostream, true);

                //InputStream istream = socket.getInputStream();
               // BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
/*
                String receiveMessage, sendMessage;
                while (true) {
                    if ((receiveMessage = receiveRead.readLine()) != null) {
                        System.out.println(receiveMessage);
                    }
                    sendMessage = bufReader.readLine();
                    pwrite.println(sendMessage);
                    pwrite.flush();
                }
*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

