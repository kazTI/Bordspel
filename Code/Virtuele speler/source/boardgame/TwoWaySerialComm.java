package boardgame;


/*	
 * 	
 *	
 */




import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *	Deze code is een combinatie van de code onder andere gevonden op:
 *	<br><a href="http://rxtx.qbang.org/wiki/index.php/Event_based_two_way_Communication">http://rxtx.qbang.org/wiki/index.php/Event_based_two_way_Communication</a>
 *	<br><a href="https://playground.arduino.cc/Interfacing/Java">https://playground.arduino.cc/Interfacing/Java</a>
 *	<br>
 *	<br>Deze klasse doet het uitlezen en schrijven naar de Arduino.
 */
public class TwoWaySerialComm
	{
		private BoardConnection board;

		private static final String PORT_NAMES[] = {"COM3", "COM6", "COM5", "COM7", "COM13"};    //Mogelijke COM poorten van mijn laptop
		private static final int TIME_OUT = 2000;										//Milliseconds to block while waiting for port open 
		private static final int DATA_RATE = 9600;      								//Default bits per second for COM port.
		
		public TwoWaySerialComm(BoardConnection board)
		{
			super();
			this.board = board;
		}
		
		void connect (/* String portName */) throws Exception
			{
				CommPortIdentifier portIdentifier = null;
				for (String portName  : PORT_NAMES)
					{
						try
							{
								portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
								System.out.println("Using " + portName);
							}
						catch (Exception e)
							{
								System.out.println(portName + " not available");
								//e.printStackTrace();
							}
						
					}
				if (portIdentifier == null)
					{
						throw new Exception("No Arduino connected");
					}
				
				if ( portIdentifier.isCurrentlyOwned() )
					{
						System.out.println("Error: Port is currently in use");
					}
				else
					{
						CommPort commPort = portIdentifier.open(this.getClass().getName(), TIME_OUT);
						
						if ( commPort instanceof SerialPort )
							{
								SerialPort serialPort = (SerialPort) commPort;
								serialPort.setSerialPortParams(	DATA_RATE,
																SerialPort.DATABITS_8,
																SerialPort.STOPBITS_1,
																SerialPort.PARITY_NONE);
								
								InputStream in = serialPort.getInputStream();
								OutputStream out = serialPort.getOutputStream();
								
								board.createOutputStream(out);
								
								serialPort.addEventListener(new SerialReader(in));
								serialPort.notifyOnDataAvailable(true);

							}
						else
							{
								System.out.println("Error: Only serial ports are handled by this example.");
							}
					}
			}
		
		/**
		 * Handles the input coming from the serial port. A new line character
		 * is treated as the end of a block in this example. 
		 */
		public /*static*/ class SerialReader implements SerialPortEventListener 
			{
				private InputStream in;
				private byte[] buffer = new byte[1024];
				
				public SerialReader ( InputStream in )
					{
						this.in = in;
					}
				
				public void serialEvent(SerialPortEvent arg0)
					{
						int data;
					  
						try
							{
								int len = 0;
								while ( ( data = in.read()) > -1 )
									{
										if ( data == '\n' )
											{
												break;
											}
										buffer[len++] = (byte) data;
									}
								board.executeBoardFunction(new String(buffer,0,len));
							}
						catch ( IOException e )
							{
								e.printStackTrace();
								System.exit(-1);
							}
					}


			}
	}