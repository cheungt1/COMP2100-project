package game;

import java.io.*;
import java.net.Socket;

import static game.Util.*;

public class GameClient {
	private final static String serverHostIP = "";

	public static void main(String[] args) {
		try {
			Socket socket = new Socket(serverHostIP, GameServer.getPort());

			DataInputStream is = new DataInputStream(socket.getInputStream());
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());

			// send player name to server
			writeWithThread(os, GUI.getUserName());

			Player you = new Player(GUI.getUserName());

			while (Game.matched != 13) {
				if (Game.playerTurn() == you.getPlayerNum()) {
					int cardsRec = 0;
					do {
						String playerChoice = GUI.getPlayerChoice();
						int card = Integer.parseInt(GUI.getCardValue());

						String selection = playerChoice + " " + card;

						writeWithThread(os, selection);

						cardsRec = is.readInt();
					} while (cardsRec != 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
