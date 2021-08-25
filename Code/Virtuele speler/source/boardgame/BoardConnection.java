package boardgame;

import java.io.*;
import java.awt.*;

/**
 *	Deze klasse vormt de verbinding tussen TwoWaySerialComm, die de Arduino uitleest, en Game, die de input verder verwerkt.
 *	@author Thomas Graafland
 *	@version 1
 */
public class BoardConnection// extends Thread
	{
		private Game game;
		private PrintStream arduino = null;

		/**
		 *	De constructor
		 * @param game De game waarmee deze connection moet werken.
		 */
		public BoardConnection(Game game)
			{
				this.game = game;
				try
					{
						(new TwoWaySerialComm(this)).connect();
					}
				catch (Exception e)
					{
						System.exit(-1);
					}
				
			}

		/**
		 *	Opent de outputstream naar de arduino
		 *	@param outputstream De outputstream
		 */
		public void createOutputStream(OutputStream outputstream)
			{
				arduino = new PrintStream(outputstream);
			}

		/**
		 *	Schrijf gegevens van een nieuwe speler naar de Arduino.
		 *	@param name		De naam van de speler.
		 *	@param color	De kleur van de speler.
		 */
		public void addPlayer(String name, String color)
			{
				arduino.println("New player:" + name + ":" + color + ":");
			}

		/**
		 *	Geef de beurt door aan de Arduino.
		 *	@param turn De huidige beurt
		 *	@param playerTurn De naam van de speler die aan de beurt is.
		 */
		public void updateTurn(int turn, String playerTurn)
			{
				arduino.println("Update turn:" + turn + ":" + playerTurn + ":");
			}

		/**
		 *	Schrijf de nieuwe positie van een speler naar de Arduino.
		 *	@param name De naam van de speler die bewogen is.
		 *	@param newLocation De nieuwe locatie van de speler, in mm.
		 */
		public void movePlayer(String name, Dimension newLocation)
			{
				//System.out.println("Printing: '\nMove player:" + name + ":" + newLocation.getWidth() + ":" + newLocation.getHeight() + ":' to Arduino");
				arduino.println("Move player:" + name + ":" + newLocation.getWidth() + ":" + newLocation.getHeight() + ":");
			}

		/**
		 *	Gebruikt door TwoWaySerialComm. Roept de juiste functie van game aan, zodat spel wordt geupdate.
		 *	@param input De input van de Arduino.
		 */
		public void executeBoardFunction(String input)
			{
				System.out.println("input: " + input);

				String[] splits = input.split(":");

				if (input.startsWith("New player:"))
					{
						System.out.println("New player");
						game.addLocalPlayer(splits[1], splits[2]);
					}
				else if (input.startsWith("Update turn:"))
					{
						game.updateStatus(Integer.parseInt(splits[1]), splits[2]);
					}
				else if (input.startsWith("Move player:"))
					{
						//System.out.println("\nMoving player:" + splits[1] + " to " + splits[2] + "," + splits[3]);
						game.updatePosition(splits[1], Integer.parseInt(splits[2]), Integer.parseInt(splits[3]));
					}
				else if (input.startsWith("Move physical player:"))
					{
						System.out.println("\nMove physical player:" + splits[1] + " " + splits[2] + " spaces forward");
						game.updatePhysicalPlayer(splits[1], Integer.parseInt(splits[2]));
					}
			}
	}