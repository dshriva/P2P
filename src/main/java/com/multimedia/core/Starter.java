package com.multimedia.core;

import com.multimedia.domain.Peer;
import com.multimedia.domain.Server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.*;


import static com.multimedia.util.NetworkConstants.*;

/*
 * created by divya at 9/29/2018
 */

public class Starter {
    public static Logger _logger = Logger.getLogger(String.valueOf(Starter.class));


    public static void main(String args[]) {
        BasicConfigurator.configure();
        System.out.println("Welcome to Peer to Peer project");
        onBoard();
    }

    private static void onBoard() {
        boolean flag = true;

        while (flag) {
            try {
                System.out.println("Press 1 to join the network");
                //_logger.info("Press 1 to join the network");

                System.out.println("Press 2 to exit");
               // _logger.info("Press 2 to exit");

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                String command = reader.readLine();
                char c = command.charAt(0);
                switch (c) {
                    case '1':
                        System.out.println("Great! You're on board!");
                       // _logger.info("Great! You're on board!");
                        int port = portValidation();
                        if (port == -1) {
                            System.err.println("Invalid Port. Exiting");
                           // _logger.error("Invalid Port. Exiting");
                            return;
                        }

                        createNode(port);
                        flag = false;
                        break;

                    case '2':
                        System.out.println("Thanks for your time. We hope to see you soon again.");
                       // _logger.info("Thanks for your time. We hope to see you soon again.");
                        return;

                    default:
                        System.out.println("Invalid Option.");
                       // _logger.error("Thanks for your time. We hope to see you soon again.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("Error in processing your entry. Pls try again.");
              //  _logger.error("Error in processing your entry. Pls try again.");
                //e.printStackTrace();
            }

        }
    }

    private static void createNode(int port) {

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String id = ip + ":" + System.currentTimeMillis();
        int serverPort = 3000;
        Timer periodicTimer = new Timer();

        System.out.println("Enter the preferred log level from the choices :'d' --> debug, 't' --> trace, 'e' --> error, 'd' --> debug, 'f' --> fatal):");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            char input = br.readLine().charAt(0);

            switch(input){
                case 'i' : initLogging('i'); break;
                case 't' : initLogging('t'); break;
                case 'd' : initLogging('d'); break;
                case 'e' : initLogging('e'); break;
                case 'f' : initLogging('f'); break;
                default:   break;
            }

        } catch (IOException e) {
            _logger.error("Failed to Initialize logging", e);
            System.err.println("Failed to Initialize logging");
        }

        if (port == 3000) {
            serverPort = port;
            final Server server = new Server(ip, id, serverPort);

            //System.out.println("You are Server :" + server.toString());
            _logger.trace("You are Server :" + server.toString());
            server.setActive(true);
            server.setLastSeen(System.currentTimeMillis());
            DatagramSocket serverSocket = null;
            try {
                serverSocket = new DatagramSocket(serverPort, ip);
                server.scheduleFailureDetection();
                //System.out.println("Server socket created");
                _logger.trace("Server socket created");

                server.startReceiverThread(server, serverSocket);

                while (true) {
                    try {
                        System.out.println("Press 1 to see list of machines the network");
                        //_logger.info("Press 1 to join the network");

                        System.out.println("Press 2 to exit");
                        // _logger.info("Press 2 to exit");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String command = reader.readLine();

                        char c = command.charAt(0);
                        switch (c) {
                            case '1':
                                System.out.println("Here is the list of current peers!");
                                for (Map.Entry<String, Peer> entrySet : server.getMembershipMap().entrySet()) {
                                    System.out.println(entrySet.getValue().toString());
                                }
                                System.out.println();
                                break;

                            case '2':
                                default:
                                    System.out.println("Bye");
                                    break;

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (SocketException e) {
                System.err.println("This port is already in use. Please retry with another port.");
            }
        } else {
            Peer peer = new Peer(ip, id, port);
            //System.out.println("You are " + peer.toString());
            _logger.trace("You are " + peer.toString());
            peer.setLastSeen(System.currentTimeMillis());
            peer.setActive(true);
            try {
                DatagramSocket peerSocket = new DatagramSocket(port, ip);
                periodicTimer.schedule(pingServer(peerSocket, serverPort, peer, ip), 0, K);

            } catch (SocketException e) {
                System.err.println("This port is already in use. Please retry with another port.");
            }

        }
    }


    private static TimerTask pingServer(final DatagramSocket peerSocket, final int serverPort, final Peer peerInfo, final InetAddress ip) {
        TimerTask pingServer = new TimerTask() {
            @Override
            public void run() {

                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = null;
                try {
                    out = new ObjectOutputStream(byteOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.writeObject(peerInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] buf = null;
                buf = byteOut.toByteArray();
                int length = buf.length;

               // System.out.println("Start pinging Server");
                _logger.trace("Start pinging Server");
                DatagramPacket response = new DatagramPacket(buf, length, ip, serverPort);
                try {
                    peerSocket.send(response);
                    _logger.trace("peer Info sent to server");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        return pingServer;
    }

    private static int portValidation() throws IOException {
        System.out.println("Enter your port id (between 3000-3100)");
        //_logger.info("Enter your port id (between 3000-3100)");
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            String portIdStr = read.readLine();
            int portId = Integer.parseInt(portIdStr);

            if (portId < 3000 || portId > 3100) {
                return -1;
            }
            return portId;
        } catch (NumberFormatException nfe) {
            //nfe.printStackTrace();
            return -1;
        }

    }

    public static boolean initLogging(char level) {
        try {

            PatternLayout lyt = new PatternLayout("[%-5p] %d %c.class %t %m%n");
            RollingFileAppender rollingFileAppender = new RollingFileAppender(lyt, "Peer To Peer" + level + "-" + ".log");
            rollingFileAppender.setLayout(lyt);
            rollingFileAppender.setName("LOGFILE");
            rollingFileAppender.setMaxFileSize("100MB");
            rollingFileAppender.activateOptions();
            Logger.getRootLogger().addAppender(rollingFileAppender);
            _logger.trace("Log level passed is " + level);
            switch (level) {
                case 'f':
                    Logger.getRootLogger().setLevel(Level.FATAL);
                    break;
                case 'e':
                    Logger.getRootLogger().setLevel(Level.ERROR);
                    break;
                case 'i':
                    Logger.getRootLogger().setLevel(Level.INFO);
                    break;
                case 'd':
                    Logger.getRootLogger().setLevel(Level.DEBUG);
                    break;
                case 't':
                    Logger.getRootLogger().setLevel(Level.TRACE);
                    break;
                default:
                    Logger.getRootLogger().setLevel(Level.ERROR);
                    break;
            }

            return true;
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return false;
        }
    }
}
