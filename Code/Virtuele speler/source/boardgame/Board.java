package boardgame;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *	Klasse die de posities op het bord en de weergave van het bord regelt.
 *	@author Thomas Graafalnd
 *	@version 1
 */
public class Board extends JPanel
	{
		/**
		 *	Het aantal vakjes op het bord.
		 */
		public final static int nrOfSpaces = 63;				//Pas dit getal aan om de hoeveelheid vakjes aan te passen. Code hoeft dan niet op andere plekken aangepast
		private final static int nrOfRows = 9;					//Het aantal rijen waarruit het bord bestaat
		private int spaceWidth = 60;							//De breedte van de vakjes in pixels
		private int spaceHeight = 60;							//De hoogte van de vakjes in pixels
		private int gap = 0;									//Het gat tussen de vakjes in pixels

																//Wordt gebruikt voor converteren positie GUI naar positie op fysieke bord
		private int guiFinishXPos = 606;						//De x positie van de finish op de GUI in pixels
		private int guiFinishYPos = 456;						//De y positie van de finish op de GUI in pixels
		private int boardFinishXpos = 250;						//De x positie van de finish op het fysieke bord in millimeter
		private int boardFinishYpos = 150;						//De y positie van de finish op het fysieke bord in millimeter

		private int numberFontSize = 14;						//Formaat van font van de nummers op de vakjes
		private int textFontSize = 30;							//Formaat van de tekst op de vakjes (start en finish)


		private Space[] spaces = new Space[nrOfSpaces+1];		//Array van vakjes
		//private int[] spacesPerRow = {4, 11, 14, 23, 30, 41, 50, 63};
		private int[] spacesPerRow = {2, 3, 10, 14, 23, 30, 41, 50, 63};	//De vakjes per rij, van Finish naar Start toe
		private boolean[] drawRow = new boolean[nrOfRows];					//Array van booleans of een rij wel of niet getekend moet worden op de GUi

																//Volgorde van deze array bepaald volgorde binnen een vakje
		private Color[] possiblePieceColors = {	Color.YELLOW,		// 1	 _______
												Color.RED,			// 2	| 1   2 |
												Color.BLUE,			// 3	|_3___4_|
												Color.GREEN};		// 4

		private java.util.List<Piece> pieces = new ArrayList<Piece>();	//Lijst met alle pieces
		private int pieceDiameter = 20;							//Diameter in pixels van de pionnen

		private String playerName = "Thomas";					//De naam van de gebruiker van deze GUI. Wordt gebruikt om Piece van gebruiker te returnen
		private int currentTurn = 0;							//Hoeveelste beurt het nu is
		private Piece turnOfPlayer;								//De speler die op dit moment aan de beurt is


		private final static int NORTH = 1;						//Richting waarin de huidige rij vakjes getekend moet worden 
		private final static int EAST = 2;						//Dit is de richting waarin de vakjes oplopen, dus als (direction==NORTH), dan worden de vakjes getekend 'richting het noorden', dus ieder volgend vakje boven de vorige
		private final static int SOUTH = 3;
		private final static int WEST = 4;

		/**
		 *	De constructor
		 */
		public Board()
			{
				Arrays.fill(drawRow, Boolean.FALSE);

				for (int i = 0; i < nrOfRows; i++)
					{
						if (nrOfSpaces >= spacesPerRow[i])
							{
								drawRow[i] = true;
							}
					}


				for (int i = 0; i <= nrOfSpaces; i++)			//Maak alle vakjes aan
					{
						spaces[i] = new Space(i);
					}


				Mouse mouse = new Mouse(this);					//Mouse listener voor het verplaatsen en neerzetten van de pionnen
				addMouseListener(mouse);
				addMouseMotionListener(mouse);

			}

		/**
		 *	Controleert of er iemand op het laatste vakje (de finish) staat.
		 *	@return True als iemand heeft gefinished, false als nog niemand op finish staat.
		 */
		public boolean anyoneFinished()
			{
				for (Piece piece : pieces)
					{											//Staat iemand op het laatste vakje?
						if (spaces[nrOfSpaces].containsPiece(piece))
							{
								return true;
							}
						
					}
				return false;
			}

		/**
		 *	Geeft de Piece van de speler die op finish staat.
		 *	@return Piece van de speler op finish. Null wanneer er niemand op de finish staat.
		 */
		public Piece getWinner()
			{
				if (anyoneFinished())							//Check of er iemand op finish staat
					{
						for (Piece piece : pieces)
							{
								if (spaces[nrOfSpaces].containsPiece(piece))
									{
										return piece;
									}
								
							}
					}
				return null;
			}

		/**
		 *	Pas de hudige beurt aan
		 *	@param turn De huidige beurt.
		 */
		public void setCurrentTurn(int turn)
			{
				currentTurn = turn;
			}
		/**
		 *	Geeft de huidige beurt.
		 *	@return De huidige beurt.
		 */
		public int getCurrentTurn()
			{
				return currentTurn;
			}

		/**
		 *	Geef de beurt aan deze pion.
		 *	@param piece De speler aan wie de beurt nu is.
		 */
		public void setTurnOfPlayer(Piece piece)
			{
				turnOfPlayer = piece;
			}
		/**
		 *	Geeft de pion terug die op dit moment aan de beurt is.
		 *	@return De pion die op dit moment aan de beurt is.
		 *	@throws Exception Als het niemands beurt is.
		 */
		public Piece getPlayerOnTurn() throws Exception
			{
				if (turnOfPlayer == null)
					{
						throw new Exception("Currently no one's turn...");
					}
				return turnOfPlayer;
			}

		/**
		 *	Voeg een nieuwe pion toe aan het spel. Pakt de x en y posities van de pion voor het neerzetten op een vakje.
		 *	@param piece De pion die toegevoegd moet worden.
		 *	@throws Exception Als naam of kleur al gekozen zijn.
		 */
		public void addPiece(Piece piece) throws Exception
			{
				for (Piece existingPiece : pieces)				//Gooi exceptions als naam of kleur al gekozen zijn.
					{
						if (existingPiece.getName().equals( piece.getName() ))
							{
								throw new Exception("Name already taken");
							}
						if (existingPiece.getColor() == piece.getColor())
							{
								throw new Exception("Color already taken");
							}
					}

				if (pieces.size() == 0)							//Maak het de beurt van deze speler als er nog geen andere spelers in het spel zitten.
					{
						setTurnOfPlayer(piece);
					}
				

				Piece actualPlayerOnTurn = getPlayerOnTurn();

				setTurnOfPlayer(piece);
				Dimension location = new Dimension(piece.getXpos(), piece.getYpos());
				System.out.println(piece.getName() + ": " + location.toString());
				putPieceDown(piece, currentTurn, location);

				setTurnOfPlayer(actualPlayerOnTurn);


				pieces.add(piece);								//Voeg pion toe aan de lijst met pionnen
			}
		/**
		 *	Geeft de pion die op de opgegeven locatie staat.
		 *	@param location	De plek waarvandaan de speler gereturned moet worden.
		 *	@return 		De pion die op de opgegeven location staat. Null als geen pion op deze plek staat.
		 */
		public Piece getPiece(Dimension location)
			{
				for (Piece piece : pieces)					//Kijk voor alle pionnen
					{
						
						int x1 = piece.getXpos();				//De linkerbovenhoek van de pion
						int x2 = piece.getXpos()+pieceDiameter;	//De rechterbovenhoek van de pion
						int y1 = piece.getYpos();				//De linkeronderhoek van de pion
						int y2 = piece.getYpos()+pieceDiameter;	//De rechteronderhoek van de pion
																		//Kijk of de opgegeven plek tussen de hoeken van de pionnen ligt.
						if (location.getWidth() > x1 && location.getWidth() < x2 && location.getHeight() > y1 && location.getHeight() < y2)
							{
								return piece;
							}
					}
				return null;
			}
		/**
		 *	Geeft een lijst met alle pionnen die aan het spel deelnemen.
		 *	@return Lijst van pionnen.
		 */
		public java.util.List<Piece> getAllPieces()
			{
				return pieces;
			}
		/**
		 *	Haal een pion weg van het bord.
		 *	@param piece	De pion die van het bord weggehaald moet worden.
		 *	@return 		De pion die weggehaald is
		 *	@throws Exception Als pion niet bestaat.
		 */
		public Piece removePiece(Piece piece) throws Exception
			{
				if (pieces.indexOf(piece) != -1)
					{
						return pieces.remove( pieces.indexOf(piece) );
					}
				throw new Exception("Piece not found");
			}

		/**
		 *	Set de naam van de gebruiker op deze waarde.
		 *	@param name De naam van de gebruiker.
		 */
		public void setPlayerName(String name)
			{
				playerName = name;
			}

		/**
		 *	Kijk of de meegegeven pion de pion van de gebruiker is.
		 *	@param piece	De pion waarvan wordt gekeken of het de pion van de gebruiker is
		 *	@return			Returned true wanneer de pion van de gebruiker is, anders false.
		 */
		public boolean isPlayerPiece(Piece piece)
			{
				if (playerName == null)
					{
						return false;
					}
				if (piece.getName().equals(playerName))
					{
						return true;
					}
				return false;
			}
		/**
		 *	Geeft de pion van de gebruiker.
		 *	@return De pion van de gebruiker.
		 */
		public Piece getPlayerPiece()
			{
				for (Piece piece : pieces)
					{
						if (piece.getName().equals(playerName))
							{
								return piece;
							}
					}
				return null;
			}

		/**
		 *	Check of de pion kan en mag bewegen.
		 *	@param piece De pion waarvan wordt gekeken of hij mag bewegen.
		 *	@return 	True als pion kan bewegen, anders false.
		 */
		public boolean canMove(Piece piece)
			{
				return true;
			}

		/**
		 *	Beweeg een pion. Deze functie zet een pion NIET neer op het vakje waar hij zich boven bevindt. ALleen maar te gebruiken voor het heen en weer slepen van de pion met de muis.
		 *	@param piece		De pion om te bewegen.
		 *	@param currentTurn	De huidige beurt.
		 *	@param location		De locatie waarnaartoe de pion bewogen moet worden.
		 */
		public void movePiece(Piece piece, int currentTurn, Dimension location)
			{

				piece.setXpos((int) location.getWidth());
				piece.setYpos((int) location.getHeight());
				repaint();
			}

		/**
		 *	Zet een pion neer op de meegegeven plek. In tegenstelling tot movePiece() beweegt deze functie de pion niet alleen, maar zet hij de pion ook op het vakje waarboven de pion staat.
		 *	@param piece		De pion om neer te zetten.
		 *	@param currentTurn	De huidige beurt.
		 *	@param location 	De locatie waarnaartoe de pion bewogen moet worden.
		 *	@throws Exception	Als pion niet neergezet kan worden.
		 */
		public void putPieceDown(Piece piece, int currentTurn, Dimension location) throws Exception
			{
				int x = (int) location.getWidth();// (location.getWidth() + 0.5*pieceDiameter);
				int y = (int) location.getHeight();//(location.getHeight() + 0.5*pieceDiameter);

				//System.out.println("Wanted location to put down: " + x + "," + y);

				if (isPieceLocationTaken(piece, new Dimension(x,y)))
					{
						System.out.println("Location taken already");
						throw new Exception("Location taken already");
					}


				for (Space space : spaces)		//Loop door alle vakjes heen
					{
												//De hoekpunten van het vakje
						int spacex1 = 0;			//Links boven
						int spacex2 = 0;			//Rechts boven
						int spacey1 = 0;			//Links onder
						int spacey2 = 0;			//Rechts onder

						if (piece.getColor() == possiblePieceColors[0])			//De vakjes zijn in 4 vlakken verdeeld. Een vlak per kleur.
							{
								spacex1 = space.getXpos();
								spacex2 = space.getXpos() + (int) (0.5*space.getWidth());
								spacey1 = space.getYpos();
								spacey2 = space.getYpos() + (int) (0.5*space.getHeight());
							}
						else if (piece.getColor() == possiblePieceColors[1])
							{
								spacex1 = space.getXpos() + (int) (0.5*space.getWidth());
								spacex2 = space.getXpos() + space.getWidth();
								spacey1 = space.getYpos();
								spacey2 = space.getYpos() + space.getHeight();
							}
						else if (piece.getColor() == possiblePieceColors[2])
							{
								spacex1 = space.getXpos() + (int) (0.5*space.getWidth());
								spacex2 = space.getXpos() + space.getWidth();
								spacey1 = space.getYpos() + (int) (0.5*space.getHeight());
								spacey2 = space.getYpos() + space.getHeight();
							}
						else if (piece.getColor() == possiblePieceColors[3])
							{
								spacex1 = space.getXpos();
								spacex2 = space.getXpos() + space.getWidth();
								spacey1 = space.getYpos() + (int) (0.5*space.getHeight());
								spacey2 = space.getYpos() + space.getHeight();
							}


													//Zet de pion neer op het vakje waarvan de pion tussen de 4 hoekpunten valt
						if (x > spacex1 && x < spacex2 && y > spacey1 && y < spacey2)
							{
								int currentLocation = getSpaceNumber(piece);
								if (currentLocation != -1)
									{
										spaces[currentLocation].removePiece(piece);
									}
								space.addPiece(piece);
								piece.setXpos((int) location.getWidth());
								piece.setYpos((int) location.getHeight());

								System.out.println("Moved " + piece.getName() + " from " + currentLocation + " to " + space.getNumber());
								repaint();
								return;
							}
					}
				throw new Exception("Can't put piece down at " + location.getWidth() + "," + location.getHeight());
			}

		/**
		 *	Zet een pion neer op de meegegeven plek. In tegenstelling tot movePiece() beweegt deze functie de pion niet alleen, maar zet hij de pion ook op het vakje waarboven de pion staat.
		 *	@param piece		De pion om neer te zetten.
		 *	@param currentTurn	De huidige beurt.
		 *	@param space 		Het nummer van het vakje waarop de pion neergezet moet worden.
		 */
		public void putPieceDown(Piece piece, int currentTurn, int space)// throws Exception
			{
				int currentLocation = getSpaceNumber(piece);
				if (currentLocation != -1)
					{
						try
							{
								spaces[currentLocation].removePiece(piece);
							}
						catch (Exception e)	
							{
								e.printStackTrace();
							}
					}
				spaces[space].addPiece(piece);


				if (piece.getColor() == possiblePieceColors[0])			//Zet de pion in het juiste vlak van het vakje
					{
						piece.setXpos((int) (spaces[space].getXpos() + (0.25*spaces[space].getWidth() -0.5*pieceDiameter)));
						piece.setYpos((int) (spaces[space].getYpos() + (0.25*spaces[space].getHeight() -0.5*pieceDiameter)));
					}
				else if (piece.getColor() == possiblePieceColors[1])
					{
						piece.setXpos((int) (spaces[space].getXpos() + (0.75*spaces[space].getWidth() -0.5*pieceDiameter)));
						piece.setYpos((int) (spaces[space].getYpos() + (0.25*spaces[space].getHeight() -0.5*pieceDiameter)));
					}
				else if (piece.getColor() == possiblePieceColors[2])
					{
						piece.setXpos((int) (spaces[space].getXpos() + (0.75*spaces[space].getWidth() -0.5*pieceDiameter)));
						piece.setYpos((int) (spaces[space].getYpos() + (0.75*spaces[space].getHeight() -0.5*pieceDiameter)));
					}
				else if (piece.getColor() == possiblePieceColors[3])
					{
						piece.setXpos((int) (spaces[space].getXpos() + (0.25*spaces[space].getWidth() -0.5*pieceDiameter)));
						piece.setYpos((int) (spaces[space].getYpos() + (0.75*spaces[space].getHeight() -0.5*pieceDiameter)));
					}
				
				System.out.println("Moved " + piece.getName() + " from " + currentLocation + " to " + spaces[space].getNumber());
				repaint();
			}

		/**
		 *	Vertelt of de meegegeven positie al in beslag is genomen door een andere pion.
		 *	@param excludePiece		De pion waarvan wordt gegeken of hij mag worden neergezet. Als deze pion op de aangegeven plek staat kan nog steeds false returned worden.
		 *	@param location			De plek waar op wordt gekeken of er een pion staat
		 *	@return					True als er een pion op deze plek staat die niet excludePiece is, anders false
		 */
		public boolean isPieceLocationTaken(Piece excludePiece, Dimension location)
			{
				for (Piece piece : pieces)
					{
						int pieceX = piece.getXpos();
						int pieceY = piece.getYpos();
						if ( (location.getWidth() >= pieceX) && (location.getWidth() <= pieceX+pieceDiameter) && (location.getHeight() >= pieceY) && (location.getHeight() <= pieceY+pieceDiameter) )
							{
								if (!piece.getName().equals( excludePiece.getName() ))
									{
										return true;
									}
							}
					}
				return false;
			}

		/**
		 *	Geeft het nummer van het vakje waarop de meegegeven pion staat.
		 *	@param piece	De pion waarvan het vakje wordt opgezocht.
		 *	@return			Het nummer van het vakje waarop de pion staat.
		 */
		public int getSpaceNumber(Piece piece)
			{
				for (Space space : spaces)
					{
						if (space.containsPiece(piece))
							{
								return space.getNumber();
							}
					}
				return -1;
			}

		/**
		 *	Rekent de meegegeven GUI positie om naar de bijbehorende positie op het bord.
		 *	@param d De positie die moet worden omgerekend naar bord positie.
		 *	@return	De omgerekende positie.
		 */
		public Dimension convertGUIToBoardPosition(Dimension d)
			{
				/*	Voor het omrekenen van de positie op de GUI in pixels naar de positie op het bord in mm gebruik ik de verhoudingen in de x en y richtingen.
				 *	Hiervoor wordt de positie van de Finish gebruikt als referentiepunt.
				 *
				 *	Bereken eerst de verhouding tussen de x positie van de finish op de GUI en die op het bord. Hiervoor geldt:
				 *	Dbx/Dgx = Fbx/Fgx
				 *	Waarbij Dbx is de x positie van het punt op het bord
				 *	Dgx is de x positie van het punt op de GUI
				 *	Fbx is de x positie van de Finish op het bord
				 *	Fgx is de x positie van de Finish op de GUI
				 *
				 *	Uit die functie volgt dat de x positie van het punt op het bord is:
				 *	Dbx = (Fbx/Fgx)*Dgx
				 */
				int newX = (int) ( (boardFinishXpos/guiFinishXPos) * d.getWidth() );

				/*	Doe vervolgens hetzelfde voor de verhouding in de y richting
				 */
				int newY = (int) ( (boardFinishYpos/guiFinishYPos) * d.getHeight() );

				return new Dimension(newX, newY);
			}

		/**
		 *	Rekent de meegegeven bord positie om naar de bijbehorende positie op de GUI.
		 *	@param d De positie die moet worden omgerekend naar GUI positie.
		 *	@return De omgerekende positie.
		 */
		public Dimension convertBoardToGUIPosition(Dimension d)
			{
				/*
				 *	Uit de formule beschreven in de functie 'convertGUIToBoardPosition'
				 *	Dbx/Dgx = Fbx/Fgx
				 *	Volgt dat 
				 *	Dgx = Dbx / (Fbx/Fgx)
				 */
				int newX = (int) ( d.getWidth() / (boardFinishXpos/guiFinishXPos) );

				/*	Doe vervolgens hetzelfde voor de verhouding in de y richting
				 */
				int newY = (int) ( d.getHeight() / (boardFinishYpos/guiFinishYPos) );
				return new Dimension(newX, newY);
			}

		/**
		 *	Rekent alle coordinaten van alle vakjes opnieuw uit. Begint met bij de finish en telt dan af tot de start. Houdt rekening met hoeveelheid vakjes en vakjes per rij.
		 *	Neemt spaceXpos en spaceYpos, geeft deze mee aan het juiste vakje en berekent dan de coordinaten van het volgende vakje relatief tot het vorige vakje.
		 *	Hoekvakjes krijgen als formaat spaceheight in beide richtingen. Normale vakjes zijn spaceWidth x spaceHeight. Finish is 3 x spaceHeight. Start is een hoekvakje.
		 */
		private void updateSpaceSizes()
			{
				int longestXRow = 1;													//Bereken wat de langste rij is, zodat formaat van vakjes hierop kan worden aangepast
				int longestYRow = 1;
				for (int r = nrOfRows-1; r >=0; r-=2)
					{
						if (drawRow[r])
							{
								longestXRow = spacesPerRow[r] - spacesPerRow[r-1] +1;
								break;
							}
					}
				for (int r = nrOfRows-2; r >0; r-=2)
					{
						if (drawRow[r])
							{
								longestYRow = spacesPerRow[r] - spacesPerRow[r-1] +1;
								break;
							}
					}




				int space = nrOfSpaces;
				int direction = WEST;													//Richting waarin de huidige rij vakjes getekend moet worden 
																						//Dit is de richting waarin de vakjes oplopen, dus als (direction==NORTH), dan worden de vakjes getekend 'richting het noorden', dus ieder volgend vakje boven de vorige
				int spaceXpos = guiFinishXPos;											//De beginpositie voor de vakjes. Begint bij de finish, tekent dan door naar de start.
				int spaceYpos = guiFinishYPos;


				spaces[space].setSize(spaceXpos, spaceYpos, spaceHeight*3, spaceWidth);	//Finish vakje
				spaceXpos -= spaceHeight;
				space--;






				for (int r = 0; r < nrOfRows; r++)										//Ga alle rijen een voor een af.
					{
						if (drawRow[r])
							{															//Hoekvakje
								spaces[space].setSize(spaceXpos, spaceYpos, spaceWidth, spaceWidth);

								if (direction == NORTH)									//Pas positie aan voor volgend vakje.
									{
										spaceYpos = spaceYpos - (spaceHeight + gap);
									}
								else if (direction == EAST)
									{
										spaceXpos = spaceXpos + (spaceWidth + gap);
									}
								else if (direction == SOUTH)
									{
										spaceYpos = spaceYpos + (spaceWidth + gap);
									}
								else if (direction == WEST)
									{
										spaceXpos = spaceXpos - (spaceHeight + gap);
									}
								space--;

								while ((nrOfSpaces - space) < spacesPerRow[r])
									{													//Vakjes tussen hoekvakken in
										if (direction == NORTH)
											{
												
												spaces[space].setSize(spaceXpos, spaceYpos, spaceWidth, spaceHeight);
												spaceYpos = spaceYpos - (spaceHeight + gap);
											}
										else if (direction == EAST)
											{
												
												spaces[space].setSize(spaceXpos, spaceYpos, spaceHeight, spaceWidth);
												spaceXpos = spaceXpos + (spaceHeight + gap);
											}
										else if (direction == SOUTH)
											{
												
												spaces[space].setSize(spaceXpos, spaceYpos, spaceWidth, spaceHeight);
												spaceYpos = spaceYpos + (spaceHeight + gap);
											}
										else if (direction == WEST)
											{
												
												spaces[space].setSize(spaceXpos, spaceYpos, spaceHeight, spaceWidth);
												spaceXpos = spaceXpos - (spaceHeight + gap);
											}

										space--;
									}

								if (direction == NORTH)					//Toevoeging om volgede hoekvakje op de juiste plek te krijgen
									{
										spaceYpos = spaceYpos - (spaceWidth - spaceHeight);
									}
								else if (direction == WEST)
									{
										spaceXpos = spaceXpos - (spaceWidth - spaceHeight);
									}
								
							}

						direction++;									//Pas tekenrichting aan voor de volgende rij.
						if (direction == 5)
							{
								direction = NORTH;
							}
					}
				//Start vakje
				spaces[space].setSize(spaceXpos, spaceYpos, spaceWidth, spaceWidth);
				

				//pieceDiameter = (int) spaceHeight/3;
			}

		/**
		 *	De aangepaste paintComponent methode die het tekenen van het bord doet.
		 */
		protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);       
					
				updateSpaceSizes();										//Update alle maten van alle onderdelen

				int stringX = 0;
				int stringY = 0;
				g.setFont(new Font("Dialog", Font.ITALIC, numberFontSize));

				for (Space space : spaces)								//Teken alle vakjes en alle vlakken waarin de vakjes zijn opgedeeld.
					{
						int spaceAlpha = 50; 							//Hoe licht (doorzichtig) de kleur is. Tussen 0 en 255.
						Color color = new Color(possiblePieceColors[0].getRed(),
												possiblePieceColors[0].getGreen(),
												possiblePieceColors[0].getBlue(),
												spaceAlpha);
						g.setColor(color);
						g.fillRect(	space.getXpos(),					//Teken vlakken
									space.getYpos(),
									(int) (0.5*space.getWidth()),
									(int) (0.5*space.getHeight()));


						color = new Color(possiblePieceColors[1].getRed(),
												possiblePieceColors[1].getGreen(),
												possiblePieceColors[1].getBlue(),
												spaceAlpha);
						g.setColor(color);
						g.fillRect(	space.getXpos() + (int) (0.5*space.getWidth()),
									space.getYpos(),
									(int) (0.5*space.getWidth()),
									(int) (0.5*space.getHeight()));


						color = new Color(possiblePieceColors[2].getRed(),
												possiblePieceColors[2].getGreen(),
												possiblePieceColors[2].getBlue(),
												spaceAlpha);
						g.setColor(color);
						g.fillRect(	space.getXpos() + (int) (0.5*space.getWidth()),
									space.getYpos() + (int) (0.5*space.getHeight()),
									(int) (0.5*space.getWidth()),
									(int) (0.5*space.getHeight()));


						color = new Color(possiblePieceColors[3].getRed(),
												possiblePieceColors[3].getGreen(),
												possiblePieceColors[3].getBlue(),
												spaceAlpha);
						g.setColor(color);
						g.fillRect(	space.getXpos(),
									space.getYpos() + (int) (0.5*space.getHeight()),
									(int) (0.5*space.getWidth()),
									(int) (0.5*space.getHeight()));





//						System.out.println("Space " + space.getNumber() + " from " + space.getXpos() + "," + space.getYpos() + " with size " + space.getWidth() + "," + space.getHeight());
						g.setColor(Color.BLACK);
						g.drawRect(space.getXpos(), space.getYpos(), space.getWidth(), space.getHeight());		//Teken het vakje

						stringX = space.getXpos()+5;
						stringY = space.getYpos()+15;
						g.drawString(Integer.toString(space.getNumber())+".", stringX, stringY);				//Zet het nummer op het vakje
					}


				g.setFont(new Font("Dialog", Font.PLAIN, textFontSize));

				stringX = (int) ( spaces[0].getXpos() + spaces[0].getWidth()*0.5 - textFontSize);				//Teken start vakje
				stringY = (int) ( spaces[0].getYpos() + spaces[0].getHeight()*0.5 + (textFontSize / 2));
				g.drawString("Start", stringX, stringY);

				stringX = (int) ( spaces[nrOfSpaces].getXpos() + spaces[nrOfSpaces].getWidth()*0.5 - 1.5*textFontSize);	//Teken finish vakje
				stringY = (int) ( spaces[nrOfSpaces].getYpos() + spaces[nrOfSpaces].getHeight()*0.5 + (textFontSize / 2));
				g.drawString("Finish", stringX, stringY);


				for (Piece piece : pieces)																		//Teken alle pionnen
					{
						g.setColor(piece.getColor());
						g.fillOval(piece.getXpos(), piece.getYpos(), pieceDiameter, pieceDiameter);
						g.setColor(Color.BLACK);
						g.drawOval(piece.getXpos(), piece.getYpos(), pieceDiameter, pieceDiameter);
					}

			}


		/**
		 *	De listener voor het slepen en plaatsen van de pionnen.
		 */
		private class Mouse implements MouseInputListener
			{
				private JPanel panel;
				private Dimension startLocation;
				private Piece selectedPiece;

				public Mouse (JPanel panel)
					{
						this.panel = panel;
					}

				public void mousePressed(MouseEvent e)					//ALs muis wordt ingedrukt, selecteer dan de pion op die plek.
					{
						Dimension clickLocation = new Dimension(e.getX(), e.getY());

						selectedPiece = getPiece(clickLocation);



						if (selectedPiece != null)
							{
								startLocation = new Dimension(selectedPiece.getXpos(), selectedPiece.getYpos());
							}
					}

				public void mouseReleased(MouseEvent e)					//Als de muis wordt losgelaten, plaats dan de pion op die positie
					{
						try
							{
								if (selectedPiece != null)
									{
										if (isPlayerPiece(selectedPiece) && isPlayerPiece(turnOfPlayer))		//Zet pion alleen neer als het de pion van de gebruiker is en de gebruiker aan de beurt is.
											{
												Dimension newLocation = new Dimension(e.getX(), e.getY());
												putPieceDown(selectedPiece, currentTurn, newLocation);
											}
										else
											{
												System.out.println("Player can't move this piece");
												throw new Exception("Selected piece not owned by player");
											}
										
									}

							}
						catch (Exception ex)
							{
								try
									{
										putPieceDown(selectedPiece, currentTurn, startLocation);
									}
								catch (Exception exc)
									{
										exc.printStackTrace();
										JOptionPane.showMessageDialog(panel, "An error occurred.");
									}
							}
					}

				public void mouseDragged(MouseEvent e)						//Beweeg de pion als de muis wordt bewogen.
					{
						try
							{
								if (selectedPiece != null)
									{
										Dimension newLocation = new Dimension(e.getX(), e.getY());
										movePiece(selectedPiece, currentTurn, newLocation);
									}

							}
						catch (Exception ex)
							{
								try
									{
										putPieceDown(selectedPiece, currentTurn, startLocation);
									}
								catch (Exception exc)
									{
										exc.printStackTrace();
										JOptionPane.showMessageDialog(panel, "An error occurred.");
									}
							}
					}

				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
				public void mouseMoved(MouseEvent e) {}
			}
	}

