package boardgame;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *	Deze klasse regelt het gooien van de dobbelsteen en de weergave daarvan.
 *	@author Thomas Graafland
  *	@version 1
 */
public class Dice extends JPanel
	{
		private int maxDiceDots = 6;											//Hoogste aantal ogen op de dobbelsteen.

		private int number = -1;												//Hierin wordt het gegooide aantal ogen opgeslagen.
		private boolean canThrow = false;										//True als er gegooid mag worden.

		private Color color = Color.WHITE;										//De achtergrondkleur

		private Random random = new Random();									//Random voor het bepalen van het gegooide aantal ogen
		private JLabel diceImage = null;										//Het plaatje van een dobbelsteen met het gegooide aantal ogen.
		private JButton throwButton = null;										//Als deze knop wordt ingedrukt, wordt de dobbelsteen gegooid.
		private ImageIcon image;												//Het plaatje van een dobbelsteen met het gegooide aantal ogen.

		/**
		 *	De constructor.
		 *	@param canThrow True als de gebruiker mag gooien, anders false.
		 */
		public Dice(boolean canThrow)
			{
				canThrow(canThrow);
				diceImage = new JLabel(new ImageIcon(new ImageIcon("../Dice.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
				updateVisuals();
			}

		/**
		 *	Set of de gebruiker kan gooien of niet
		 *	@param canThrow True als de gebruiker mag gooien, anders false.
		 */
		public void canThrow(boolean canThrow)
			{
				this.canThrow = canThrow;
			}

		/**
		 *	Update GUI zodat het juiste dobbelsteenplaatje wordt getoond.
		 */
		public void updateVisuals()
			{
				removeAll();													//Haal alles weg uit de GUI

				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();

				setLayout(gridbag);


				diceImage.setBorder(BorderFactory.createLineBorder(Color.black));//Voeg het plaatje van de dobbelsteen toe
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 0.5;
				c.weighty = 0.7;
				c.fill = GridBagConstraints.BOTH;
				gridbag.setConstraints(diceImage, c);
				add(diceImage);


				c = new GridBagConstraints();									//Voeg de button om te gooien toe
				throwButton = new JButton("Gooi");
				throwButton.addActionListener(new diceThrowListener());
				if (canThrow)													//Maak button wit als gebruiker mag gooien, anders grijs.
					{
						color = Color.WHITE;
					}
				else
					{
						color = Color.LIGHT_GRAY;
					}
				throwButton.setBackground(color);
				throwButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
				throwButton.setBorder(BorderFactory.createLineBorder(Color.black));
				c.gridx = 0;
				c.gridy = 1;
				c.weightx = 0.5;
				c.weighty = 0.3;
				c.fill = GridBagConstraints.BOTH;
				gridbag.setConstraints(throwButton, c);
				add(throwButton);
			}

		/**
		 *	Geeft het aantal ogen dat gegooid is met de dobbelsteen
		 *	@return Het aantal ogen
		 */
		public int getNumber()
			{
				return number;
			}

		/**
		 *	Klasse voor het luisteren naar de button voor het gooien
		 */
		private class diceThrowListener implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
					{															//Doe niks als de gebruiker niet mag gooien
						if (!canThrow)
							{
								System.out.println("Mag niet gooien");
								return;
							}
						number = random.nextInt(maxDiceDots) + 1;				//Random getal van 0 (inclusief) tot maximaal aantal ogen (exclusief)
																				//number is dus 0 tot en met een onder maximaal aantal ogen, dus +1 zodat het van 1 tot maximaal aantal ogen loopt

																				//Update het plaatje
						diceImage = new JLabel(new ImageIcon(new ImageIcon("../Dice" + number + ".png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));

						canThrow = false;										//Er mag niet nog een keer gegooid worden, zodat een speler niet kan blijven gooien tot hij het gewenste aantal ogen heeft

						System.out.println("Aantal ogen: " + number);

						updateVisuals();
						revalidate();
						repaint();
					}
			}
	}