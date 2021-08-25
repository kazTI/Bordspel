package boardgame;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 *	Deze klasse is een vakje en bevat de kleur en het nummer.
 *	@author Thomas Graafland
 *	@version 1
 */
public class Space
	{
		protected java.util.List<Piece> pieces = new ArrayList<Piece>();
		protected int number = 0;

		protected Color color = Color.WHITE;

		protected int xPos = 0;
		protected int yPos = 0;
		protected int width = 0;
		protected int height = 0;

		/**
		 *	De constructor
		 *	@param number Het nummer van het vakje.
		 */
		public Space(int number)
			{
				setNumber(number);

				int colorNumber = number % 10;
				float a = (float) 0.15;

				switch(colorNumber)
					{
						case 0:
							setColor(new Color(1, 0, 0, a));
							break;
						case 1:
							setColor(new Color(0, 1, 0, a));
							break;
						case 2:
							setColor(new Color(0, 0, 1, a));
							break;
						case 3:
							setColor(new Color(1, 1, 0, a));
							break;
						case 4:
							setColor(new Color(1, 0, 1, a));
							break;
						case 5:
							setColor(new Color(0, 1, 1, a));
							break;
						case 6:
							setColor(new Color(1, 1, 1, a));
							break;
						case 7:
							setColor(new Color(1, (float)0.5, 0, a));
							break;
						case 8:
							setColor(new Color(1, 0, (float)0.5, a));
							break;
						case 9:
							setColor(new Color((float)0.5, 1, 0, a));
							break;
					}
			}

		/**
		 *	Set het formaat en de positie van het vakje.
		 *	@param xPos De x positie van het vakje
		 *	@param yPos De y positie van het vakje
		 *	@param width De breedte van het vakje
		 *	@param height De hoogte van het vakje
		 */
		public void setSize(int xPos, int yPos, int width, int height)
			{
				setXpos(xPos);
				setYpos(yPos);
				setWidth(width);
				setHeight(height);
			}

		/**
		 *	Set de kleur van het vakje.
		 *	@param color De kleur van het vakje.
		 */
		public void setColor(Color color)
			{
				this.color = color;
			}

		/**
		 *	Geeft de kleur van dit vakje.
		 *	@return De kleur van het vakje.
		 */
		public Color getColor()
			{
				return color;
			}

		/**
		 *	Haalt een pion weg van het vakje.
		 *	@param piece De pion die weggehaald wordt.
		 *	@return De pion die weggehaald is.
		 *	@throws Exception Als de pion niet op dit vakje staat.
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
		 *	Checkt of de pion op dit vakje staat.
		 *	@param piece De pion waarvan wordt gekeken of hij op dit vakje staat.
		 *	@return True als de pion op dit vakje staat, anders false.
		 */
		public boolean containsPiece(Piece piece)
			{
				if (pieces.indexOf(piece) != -1)
					{
						return true;
					}
				return false;
			}
		/**
		 *	Zet een pion neer op dit vakje.
		 *	@param piece De pion om neer te zetten op dit vakje.
		 */
		public void addPiece(Piece piece)
			{
				pieces.add(piece);
			}

		/**
		 *	Geeft het nummer van dit vakje
		 *	@return Het nummer van dit vakje
		 */
		public int getNumber()
			{
				return number;
			}
		/**
		 *	Set het nummer van dit vakje.
		 *	@param number Het nieuwe nummer van dit vakje.
		 */
		public void setNumber(int number)
			{
				this.number = number;
			}

		/**
		 *	Geeft de x positie van het vakje.
		 *	@return De x positie van het vakje.
		 */
		public int getXpos()
			{
				return xPos;
			}
		/**
		 *	Set de x positie van het vakje.
		 *	@param xPos De nieuwe x positie van het vakje.
		 */
		public void setXpos(int xPos)
			{
				this.xPos = xPos;
			}
		/**
		 *	Geeft de y positie van het vakje.
		 *	@return De y positie van het vakje.
		 */
		public int getYpos()
			{
				return yPos;
			}
		/**
		 *	Set de y positie van het vakje.
		 *	@param yPos De nieuwe y positie van het vakje.
		 */
		public void setYpos(int yPos)
			{
				this.yPos = yPos;
			}
		/**
		 *	Geeft de breedte van het vakje.
		 *	@return De breedte van het vakje.
		 */
		public int getWidth()
			{
				return width;
			}
		/**
		 *	Set de breedte van het vakje.
		 *	@param width De nieuwe breedte van het vakje.
		 */
		public void setWidth(int width)
			{
				this.width = width;
			}
		/**
		 *	Geeft de hoogte van het vakje.
		 *	@return De hoogte van het vakje.
		 */
		public int getHeight()
			{
				return height;
			}
		/**
		 *	Set de hoogte van het vakje.
		 *	@param height De nieuwe hoogte van het vakje.
		 */
		public void setHeight(int height)
			{
				this.height = height;
			}

	}

	