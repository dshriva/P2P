package com.multimedia.handler;

import com.multimedia.domain.Peer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/*
 * created by divya at 10/1/2018
 */
public class PeerTCPHandler {
    //
    public void createPeerSocket(int port, InetAddress ip) {
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
*/
            byte[] b = new byte[20002];
            // receiving from server ( receiveRead  object)
            InputStream istream = sock.getInputStream();
            FileOutputStream fr = new FileOutputStream("C:\\Users\\divya\\Desktop\\AI\\received\\imagerec.jpeg");
            istream.read(b, 0, b.length);

            fr.write(b, 0, b.length);
            System.out.println("Image received at time : " + System.currentTimeMillis());
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

    public void createDirectory(InetAddress ip) {
        String path = "C:\\Files" + File.separator + "a.txt";
// Use relative path for Unix systems
        File f = new File(path);

        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Set<String> listFilesForFolder(final File folder, InetAddress ip, String id, int port, Set<String> set) {

        for (final File fileEntry : folder.listFiles()) {


                set.add(fileEntry.getName());

        }
        return set;
    }


}
