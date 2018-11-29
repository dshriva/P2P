package com.multimedia.handler;

import com.multimedia.domain.Peer;
import com.multimedia.domain.Server;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

import static com.multimedia.domain.Server.membershipMap;
import static com.multimedia.domain.Server.fileMap;
import static com.multimedia.util.NetworkConstants.K;
import static com.multimedia.util.NetworkConstants.M;

/*
 * created by divya at 10/1/2018
 */
public class ServerUDPHandler {
    public static Logger _logger = Logger.getLogger(String.valueOf(ServerUDPHandler.class));

    public void receiveInfoFromPeer(DatagramSocket serverSocket) {
        while (true) {
            byte[] b = new byte[1024];
            DatagramPacket incomingData = new DatagramPacket(b, b.length);
            try {
                serverSocket.receive(incomingData);
                ByteArrayInputStream byteIn = new ByteArrayInputStream(incomingData.getData());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                try {
                    Peer peer = (Peer) in.readObject();

                    peer.setLastSeen(System.currentTimeMillis());
                    peer.setActive(true);
                    if (!membershipMap.containsKey(peer.getId())) {
                        membershipMap.put(peer.getId(), peer);
                    } else {
                        membershipMap.get(peer.getId()).setLastSeen(System.currentTimeMillis());
                        membershipMap.get(peer.getId()).setActive(true);
                    }

                    if(! peer.getFileSet().isEmpty()) {
                        for(String file : peer.getFileSet()){
                            if(! fileMap.containsKey(file)) {
                                Set<Integer> nodes = new HashSet<Integer>();
                                nodes.add(peer.getPort());
                                fileMap.put(file,nodes);
                            } else {
                                fileMap.get(file).add(peer.getPort());
                            }
                        }
                    }



                    //Printing map
                   /* System.out.println("------------------------------------------------");
                    for (Map.Entry<String, Peer> entrySet : membershipMap.entrySet()) {
                        _logger.debug(entrySet.getKey() + " --> " + entrySet.getValue());
                        System.out.println(entrySet.getKey() + " --> " + entrySet.getValue());
                    }
                    System.out.println("------------------------------------------------");
                   */

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //System.out.println("Data received from peer" + info);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void scheduleFailureDetection() {
        Timer periodicTimer = new Timer();
        periodicTimer.schedule(failureDetection(), 0, M *K);
    }

    public void startReceiverThread(final ServerUDPHandler serverUDPHandler, DatagramSocket serverSocket) {
        final DatagramSocket finalServerSocket = serverSocket;
        Runnable runnable = new Runnable() {
            public void run() {
                serverUDPHandler.receiveInfoFromPeer(finalServerSocket);
            }
        };
        Thread t1 = new Thread(runnable);
        t1.start();
    }

    static TimerTask failureDetection() {
        TimerTask failureDetection = new TimerTask() {
            @Override
            public void run() {

                //System.out.println("Detecting Failures");
                _logger.trace("Detecting Failures");
                for (Map.Entry<String, Peer> entrySet : membershipMap.entrySet()) {
                    long currentTime = System.currentTimeMillis();
                    // String currPeerId = entrySet.getKey();
                    if ((currentTime - (M *K)) > entrySet.getValue().getLastSeen()) {
                        if (entrySet.getValue().isActive()) {
                            System.out.println("Marking Peer " + entrySet.getValue() + " as dead");
                            _logger.info("Marking Peer " + entrySet.getValue() + " as dead");
                            //entrySet.getValue().setActive(false);
                            membershipMap.remove(entrySet.getKey());
                        }
                    }
                    //System.out.println("Post failure detection :" + entrySet.getKey() + " --> " + entrySet.getValue());
                }
            }
        };
        return failureDetection;
    }

}
