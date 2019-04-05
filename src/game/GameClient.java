package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import static game.Util.writeInt;
import static game.Util.writeString;

public class GameClient {

//	private GameServer server;
	private Socket socket;

	public GameClient() throws IOException {
	    this(GameServer.getIP(), GameServer.getPort());
    }

    /**
     * Create a game client with the given host name and port.
     *
     * @param host  the host name or IP address
     * @param port  the port number
     * @throws IOException  for any errors regards to creating the socket
     */
	public GameClient(String host, int port) throws IOException {
		socket = new Socket(host, port);
		start();
	}

	private void start() {
		Game game = GameServer.getGame();

		try {
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			DataInputStream is = new DataInputStream(socket.getInputStream());

			Scanner input = new Scanner(System.in);

			System.out.print(is.readUTF()); // welcoming message

			// send player name to server
//			writeString(os, GUI.getUserName());
			String playerName = input.nextLine();
			writeString(os, playerName);

			is.readInt(); // get signal from server
			Player me = GameServer.getGame().findPlayer(playerName);
			List<Integer> hand = me.getHand();

			System.out.println("Your hand: " + hand);
			while (!game.isEnded()) {

				if ((hand.size() != 0) && (game.nextPlayer().equals(me))) {
					System.out.printf("My hand: %s\n", me.getHand());
					int cardsRecv;

					do {
						System.out.println("[It's my turn!]");
						System.out.println(is.readUTF());

//						String playerChoice = GUI.getPlayerChoice();
						String playerChoice = input.nextLine();
						writeString(os, playerChoice);

						System.out.println(is.readUTF());

//						int card = Integer.parseInt(GUI.getCardValue());
						int card = input.nextInt();
						writeInt(os, card);

						cardsRecv = is.readInt();
						System.out.printf("[Received %d %d's]\n", cardsRecv, card);

                        input.nextLine();
					} while (cardsRecv != 0);

                    System.out.println("[Go Fish!]");
				}
			}

			is.close();
			os.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public GameServer getServer() {
		return server;
	}*/
}