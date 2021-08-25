package boardgame;

import java.awt.Color;

/**
 *	Deze klasse is de pion en bevat de naam, kleur en positie van de pion.
 */
public class Piece
	{
		private String name;
		private Color color;
		private int xPos = 0;
		private int yPos = 0;

		/**
		 *	De constructor.
		 *	@param name De naam van de speler.
		 *	@param color De kleur van de speler.
		 *	@param x De x positie van de pion.
		 *	@param y De y positie van de pion.
		 */
		public Piece(String name, Color color, int x, int y)
			{
				setName(name);
				setColor(color);
				setXpos(x);
				setYpos(y);
			}


		/**
		 *	Set de naam van de pion.
		 *	@param name De nieuwe naam van de pion.
		 */
		public void setName(String name)
			{
				this.name = name;
			}
		/**
		 *	Geeft de naam van de pion.
		 *	@return De naam van de pion.
		 */
		public String getName()
			{
				return name;
			}

		/**
		 *	Set de kleur van de pion.
		 *	@param color De nieuwe kleur van de pion.
		 */
		public void setColor(Color color)
			{
				this.color = color;
			}
		/**
		 *	Geeft de kleur van de pion.
		 *	@return De kleur van de pion.
		 */
		public Color getColor()
			{
				return color;
			}

		/**
		 *	Set de x positie van de pion.
		 *	@param xPos De nieuwe x positie van de pion.
		 */
		public void setXpos(int xPos)
			{
				this.xPos = xPos;
			}
		/**
		 *	Geeft de x positie van de pion.
		 *	@return De x positie van de pion.
		 */
		public int getXpos()
			{
				return xPos;
			}

		/**
		 *	Set de y positie van de pion.
		 *	@param yPos De nieuwe y positie van de pion.
		 */
		public void setYpos(int yPos)
			{
				this.yPos = yPos;
			}
		/**
		 *	Geeft de y positie van de pion.
		 *	@return De y positie van de pion.
		 */
		public int getYpos()
			{
				return yPos;
			}


	}