package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static game.Util.*;

public class GameServer {

    private final Game game;

    private final int port;

    private List<Player> players;

    public GameServer(Game game, int port) {
        this.game = game;
        this.port = port;
        this.players = new LinkedList<>();
    }

    public void start() {
        new Thread(() -> {
            try {
                // create server socket
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.printf("[Waiting for connection at port %d ...]", port);

                while (true) {
                    // accept player
                    Socket playerSocket = serverSocket.accept();

                    // retrieve i/o streams
                    DataInputStream is = new DataInputStream(playerSocket.getInputStream());
                    DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

                    // ask for username
                    writeWithThread(os, "[Welcome to Go Fish! Please enter your name]: ");

                    String username = is.readUTF();

                    writeWithThread(os, String.format("[Hello, %s!]", username));

                    new Thread(new PlayerHandler(username, is, os)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Game getGame() {
        return game;
    }

    public int getPort() {
        return port;
    }

    public List<String> players() {
        return players.stream().map(Player::getName).collect(Collectors.toList());
    }

    private class PlayerHandler extends Player implements Runnable {

        DataInputStream is;
        DataOutputStream os;

        PlayerHandler(String name, DataInputStream is, DataOutputStream os) {
            super(name);
            this.is = is;
            this.os = os;

            players.add(this);
        }

        @Override
        public void run() {

        }
    }
}
