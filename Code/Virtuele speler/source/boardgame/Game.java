package boardgame;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 *	Deze klasse is het spel. Bij instantieren van deze klasse worden alle  benodigde componenten ook geinstantieerd en begint het spel.
 *	Zolang de communicatie nog werkt via TwoWaySerialComm kan er maar 1 instantie zijn van deze klasse.
 *	@author Thomas Graafland
 *	@version 1
 */

public class Game extends JFrame
	{
		private BoardConnection connection;						//De verbinding met de Arduino
		private Board board;									//JPanel met daarop het bord en alle pionnen
		private JPanel controlPanel = new JPanel();				//JPanel met daarop alle componenten voor bijhouden van het spelverloop
		private JPanel scoreboard = new JPanel();				//JPanel met de huidige beurt en een lijst van alle spelers, hun kleuren en wie er aan de beurt is
		private JButton nextTurnButton = new JButton();			//Knop die ingedrukt moet worden als speler zijn pion gelopen heeft en de beurt naar de volgende speler mag.
		private Dice dice = new Dice(false);					//Klasse die het gooien van de dobbelsteen regelt

		private int lastPosition = 0;							//Integer met daarin de positie van de speler in de voorgaande beurt. Nodig om te controleren of de speler wel het juiste aantal vakjes heeft bewogen

		/**
		 *	De constructor
		 *	@param name De naam van de JFrame wordt: Ganzenbord + name
		 */
		public Game(String name)
			{
				super("Ganzenbord " + name);
				setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);//Sluit applicatie wanneer kruisje wordt ingedrukt
				setSize(1518, 947);													//Maak frame 1518px breed en 947px hoog. Dit maakt de ruimte tussen de borders exact 1500x900px voor het gemak.
				setResizable(false);												//Resizable moet op false, omdat de positie van de pionnen nog niet wordt aangepast bij resizing
				setLocationRelativeTo ( null );										//Zet frame in het midden van het scherm
				setVisible(true);													//Maak frame zichtbaar


				setLayout(new BorderLayout(0,0));


				board = new Board();												//Maak het bord aan
				add(board, BorderLayout.CENTER);




				updateControlpanel();												//Maak de controlpanel aan
				repaint();


				connection = new BoardConnection(this);								//Maak verbinding met de Arduino

				playerCreationDialog();												//Laat speler naam en kleur kiezen

				System.out.println("Game created");
			}


		/**
		 *	Bij aanroepen van deze functie krijgt de gebruiker de dialoog voor het aanmaken van zijn speler object. Gedrag bij meerdere malen deze functie aanroepen is niet bekend, dus roep vooralsnog slechts eenmaal aan.
		 */
		private void playerCreationDialog()
			{
				boolean nameChosen = false;											//Boolean voor bijhouden of er een correcte naam is gekozen.
				boolean colorChosen = false;										//Boolean voor bijhouden of er een kleur gekozen is.	
				String playerName = "";
				String playerColor = "";

				while(!(nameChosen && colorChosen))									//Opnieuw blijven proberen tot zowel naam als kleur klopt
					{
						if (!nameChosen)
							{														//Dialoog waar gebruik zijn naam invoert
		                    	playerName = (String)JOptionPane.showInputDialog(	this,
		                    														"Voor uw naam in:",
		                    														"Nieuwe speler toevoegen",
		                    														JOptionPane.DEFAULT_OPTION);
								if (!playerName.equals(""))
									{
										nameChosen = true;
									}
							}
						if (!colorChosen)
							{														//Dialoog waar gebruiker mag kiezen uit opties in array 'possibilities'
								Object[] possibilities = {"Yellow"/*, "Red"*/, "Blue", "Green"};	//Rood vooralsnog geen optie, omdat op de arduino gehardcode is dat de fysieke speler rood is

								playerColor = (String)JOptionPane.showInputDialog(	this,
		                    												"Kies uw kleur:",
		                    												"Nieuwe speler toevoegen",
		                    												JOptionPane.PLAIN_MESSAGE,
		                    												null,
		                    												possibilities,
		                    												possibilities[0]);
								

								if (!playerColor.equals(""))
									{
										colorChosen = true;
									}
							}

						System.out.println("Speler: " + playerName + ", " + playerColor);

						if (nameChosen && colorChosen)
							{
								board.setPlayerName(playerName);					//Board gebruikt nu opgegeven naam als naam van de speler die op deze GUI speelt
								connection.addPlayer(playerName, playerColor);		//Stuur gegevens van deze speler naar de arduino
								addLocalPlayer(playerName, playerColor);
							}
						
					}
			}

		/**
		 *	Maakt lokaal een Piece object aan voor de opgegeven speler.
		 *	@param name De naam van de speler toe toegevoegd moet worden.
		 *	@param color De kleur die de pion van de speler moet hebben. Mogelijkheden zijn "Blue", "Red", "Green" en "Yellow"
		 */

		public synchronized void addLocalPlayer(String name, String color)
			{
				Piece aPiece = null;
				System.out.println("name = " + name + " en color = " + color);

				if (color.startsWith("Blue"))										//Startposities voor verschillende kleuren zijn nu nog gehardcode
					{
						aPiece = new Piece(name, Color.BLUE, 280, 790);
					}
				else if (color.startsWith("Red"))
					{
						aPiece = new Piece(name, Color.RED, 280, 760);
					}
				else if (color.startsWith("Green"))
					{
						aPiece = new Piece(name, Color.GREEN, 250, 790);
					}
				else if (color.startsWith("Yellow"))
					{
						aPiece = new Piece(name, Color.YELLOW, 250, 760);
					}

				if (aPiece != null)
					{
						try
							{

								board.addPiece(aPiece);								//Voeg de pion toe aan het bord
								System.out.println("Added new player locally");

								updateControlpanel();
								repaint();
							}
						catch (Exception e)
							{
								e.printStackTrace();
							}
					}
			}

		/**
		 *	Update op de GUI wie er aan de beurt is en hoeveelste beurt het is.
		 *	@param currentTurn Hoeveelste beurt het is.
		 *	@param playersTurn Naam van de speler wiens beurt het nu is.
		 */
		public synchronized void updateStatus(int currentTurn, String playersTurn)
			{
				System.out.println("Updating status");
				
				board.setCurrentTurn(currentTurn);

				for (Piece piece : board.getAllPieces())							//Loop door Pieces heen om te kijken welke speler nodig is
					{
						if (piece.getName().equals( playersTurn ))					//Maak het de beurt van de juiste speler
							{
								board.setTurnOfPlayer(piece);
							}
					}
				


				
				updateControlpanel();
			}

		/**
		 *	Update de positie van een pion op de GUI.
		 *	@param movedPlayer	Naam van de speler die bewogen moet worden.
		 *	@param newX			De nieuwe x positie van de speler, in mm op het fysieke bord.
		 *	@param newY			De nieuwe y positie van de speler, in mm op het fysieke bord.
		 */
		public synchronized void updatePosition(String movedPlayer, int newX, int newY)
			{
				System.out.println("Moving a player");
				for (Piece piece : board.getAllPieces())							//Loop door Pieces heen om te kijken welke speler nodig is
					{
						if (piece.getName().equals( movedPlayer ))
							{														//Converteer de positie van het bord naar de benodigde positie op de GUI
								Dimension newLocation = board.convertBoardToGUIPosition(new Dimension(newX, newY));

								try
									{
										int spaceBeforeMove = board.getSpaceNumber(piece);

																							//Verplaats de pion, maar zet de pion weer terug als het vakje niet tussen 0 en het aantal vakjes valt
										board.putPieceDown(piece, board.getCurrentTurn(), newLocation);

										if (board.getSpaceNumber(piece) > board.nrOfSpaces || board.getSpaceNumber(piece) < 0)
											{
												board.putPieceDown(piece, board.getCurrentTurn(), spaceBeforeMove);
											}

										updateControlpanel();
									}
								catch (Exception e)
									{
										System.out.println("Not moving piece");
										e.printStackTrace();
									}
							}
						
					}

				checkForWinner();													//Check of er al een winnaar is.
			}

		/**
		 *	Update de plek van de fysieke speler op de GUI.
		 *	@param name			De naam van de fysieke speler.
		 *	@param movesForward	Het aantal vajkes dat de fysieke speler vooruit moet bewegen.
		 */
		public synchronized void updatePhysicalPlayer(String name, int movesForward)
			{
				for (Piece piece : board.getAllPieces())							//Loop door Pieces heen om te kijken welke speler nodig is
					{
						if (piece.getName().equals(name))
							{
								int spaceBeforeMove = board.getSpaceNumber(piece);

																					//Het vakje waar de speler naar toe moet.
								int newSpace = board.getSpaceNumber(piece) + movesForward;


								if (newSpace > board.nrOfSpaces || newSpace < 0)	//Beweeg de speler alleen als het nieuwe vakje tussen 0 en het hoogste vakje valt
									{
										System.out.println("Test1: " + spaceBeforeMove);
										board.putPieceDown(piece, board.getCurrentTurn(), spaceBeforeMove);
									}
								else
									{
										System.out.println("Test2: " + newSpace);
										board.putPieceDown(piece, board.getCurrentTurn(), newSpace);
									}

								updateControlpanel();
							}
					}
				checkForWinner();													//Check of er al een winnaar is.
			}

		/**
		 *	Maakt het scoreboard met daarop de huidige beurt en een lijst van alle spelers, hun kleuren en wie er aan de beurt is
		 *	@return De JPanel met daarop beurt en spelers.
		 */
		private synchronized JPanel createScoreboard()
			{
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(6,1));								//Zes plekken op de JPanel: 1 voor de beurt, 5 voor mogelijke spelers
				panel.setBorder(BorderFactory.createLineBorder(Color.black));

																					//JLabel met daarop de huidige beurt
				JLabel turnLabel = new JLabel("Beurt " + board.getCurrentTurn(), SwingConstants.CENTER);
				turnLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
				//turnLabel.setBorder(BorderFactory.createLineBorder(Color.black));
				turnLabel.setBackground(new Color(190, 225, 225));
				turnLabel.setOpaque(true);
				panel.add(turnLabel);

																					//Maak JPanels met kleuren en namen van spelers
				for (Piece piece : board.getAllPieces())
					{
						JPanel namePanel = new JPanel();
						namePanel.setLayout(new BorderLayout());
						namePanel.setBorder(BorderFactory.createLineBorder(Color.black));

						JPanel colorPanel = new JPanel();
						colorPanel.setPreferredSize(new Dimension(50,50));
						colorPanel.setBackground(piece.getColor());
						colorPanel.setOpaque(true);
						namePanel.add(colorPanel, BorderLayout.WEST);

						JLabel label = new JLabel(piece.getName(), SwingConstants.CENTER);
						label.setFont(new Font("SansSerif", Font.PLAIN, 20));

						try
							{														//Witte achtergrond voor speler die aan de beurt is, grijze achtergrond voor de andere spelers
								if (piece.getName().equals( board.getPlayerOnTurn().getName() ))
									{
										label.setBackground(Color.WHITE);
									}
								else
									{
										label.setBackground(Color.LIGHT_GRAY);
									}
							}
						catch (Exception e)
							{
								e.printStackTrace();
							}

						label.setOpaque(true);
						namePanel.setBorder(BorderFactory.createLineBorder(Color.black));
						namePanel.add(label, BorderLayout.CENTER);

						panel.add(namePanel);
					}

				return panel;
			}

		/**
		 *	Update de panel met daarop alle gegevens over het spelverloop.
		 */
		private synchronized void updateControlpanel()
			{
				controlPanel.removeAll();											//Maak controlPanel leeg
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();

				controlPanel.setLayout(gridbag);
				controlPanel.setPreferredSize(new Dimension(300, 0));
				add(controlPanel, BorderLayout.EAST);

				scoreboard = createScoreboard();									//Maak scoreboard aan en voeg toe

				c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 2;
				c.weightx = 0.5;
				c.weighty = 0.7;
				c.fill = GridBagConstraints.BOTH;
				gridbag.setConstraints(scoreboard, c);

				controlPanel.add(scoreboard);



				boolean turn = false;												//Check of speler aan de beurt is en geef dit door aan de dobbelsteen
				try
					{
						if (board.isPlayerPiece(board.getPlayerOnTurn()))
							{
								turn = true;
							}
					}
				catch (Exception e)
					{
						e.printStackTrace();
					}
				
				dice = new Dice(turn);

				c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 1;
				c.gridwidth = 2;
				c.weightx = 0.5;
				c.weighty = 0.7;
				c.fill = GridBagConstraints.BOTH;
				gridbag.setConstraints(dice, c);
				controlPanel.add(dice);


				JButton nextTurnButton = new JButton("Volgende beurt");
																					//Voeg alleen actionListener toe aan knop voor volgende beurt als het daadwerkelijk de beurt is van de speler.
				if(turn)
					{
						nextTurnButton.addActionListener(new NextTurnListener());
						nextTurnButton.setBackground(Color.WHITE);
					}
				else
					{
						nextTurnButton.setBackground(Color.LIGHT_GRAY);
					}

				nextTurnButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
				nextTurnButton.setBorder(BorderFactory.createLineBorder(Color.black));
				c = new GridBagConstraints();
				c.gridx = 1;
				c.gridy = 2;
				c.gridwidth = 1;
				c.weightx = 0.5;
				c.weighty = 0.1;
				c.fill = GridBagConstraints.BOTH;
				//c.anchor = GridBagConstraints.LINE_END;
				gridbag.setConstraints(nextTurnButton, c);
				controlPanel.add(nextTurnButton);



				controlPanel.repaint();
				controlPanel.revalidate();
				revalidate();
				repaint();
			}

		/**
		 *	Check of er een winnaar is en zo ja, geef dit door aan de gebruiker.
		 */
		private void checkForWinner()
			{
				if (board.anyoneFinished())
					{
						Piece winner = board.getWinner();
						if (winner != null)
							{
								JOptionPane.showMessageDialog(this, "Winnaar is " + winner.getName());
							}
						else
							{
								System.out.println("Winner = null");
							}
					}
			}

		/**
		 *	Klasse voor het luisteren of de gebruiker de beurt wil doorgeven aan de volgende speler.
		 */
		private class NextTurnListener implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
					{
						Piece piece = board.getPlayerPiece();
						
						Dimension newLocation;
																					//Beweeg speler alleen als het aantal ogen tussen 1 en 6 is, of de nieuwe plek groter is dan het aantal vakjes
						if ((dice.getNumber() > 0 && dice.getNumber() <= 6) || lastPosition+dice.getNumber() > board.nrOfSpaces)
							{														//Verplaats de speler naar de nieuwe plek als het aantal ogen gelopen kan worden.
								if (lastPosition + dice.getNumber() == board.getSpaceNumber(piece) && board.canMove(piece))
									{
										
																					//Verplaats pion alleen als nieuwe plek niet hoger is dan het hoogste aantal vakjes
										if (lastPosition + dice.getNumber() > board.nrOfSpaces)
											{
												board.putPieceDown(piece, board.getCurrentTurn(), lastPosition);
											}
										else
											{
												lastPosition = board.getSpaceNumber(piece);
											}
										

										newLocation = board.convertGUIToBoardPosition(new Dimension( piece.getXpos(), piece.getYpos() ));
										connection.movePlayer(piece.getName(), newLocation);	//Geef de nieuwe positie door aan de Arduino


									}
								else
									{												//Zet de pion terug op zijn oude plek
										board.putPieceDown(piece, board.getCurrentTurn(), lastPosition);								
									}
								

								checkForWinner();									//Check of er al een winnaar is.s
							}
					}
			}
	}