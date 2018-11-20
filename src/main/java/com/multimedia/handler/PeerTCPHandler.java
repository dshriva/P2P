package com.multimedia.handler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/*
 * created by divya at 10/1/2018
 */
public class PeerTCPHandler {
    //
    public void createPeerSocket(int port , InetAddress ip) {
        System.out.println("Peer says : ");

        Socket sock = null;
        try {
            sock = new Socket(ip, 3000);
            // reading from keyboard (keyRead object)
            //
            /*
            BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
            // sending to client (pwrite object)
            OutputStream ostream = sock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true);
*/          byte[] b = new byte[20002];
            // receiving from server ( receiveRead  object)
            InputStream istream = sock.getInputStream();
            FileOutputStream fr = new FileOutputStream("C:\\Users\\divya\\Desktop\\AI\\received\\imagerec.jpeg");
            istream.read(b,0,b.length);

            fr.write(b,0,b.length);
            //BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

            //System.out.println("Start the chitchat, type and press Enter key");
/*
            String receiveMessage, sendMessage;
            while (true) {
                sendMessage = keyRead.readLine();  // keyboard reading
                pwrite.println(sendMessage);       // sending to server
                pwrite.flush();                    // flush the data
                if ((receiveMessage = receiveRead.readLine()) != null) //receive from server
                {
                    System.out.println(receiveMessage); // displaying at DOS prompt
                }
            }

            */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

