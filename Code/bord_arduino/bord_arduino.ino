#include <SoftwareSerial.h>

#include "Piece.h"

#define maxNrOfPlayers 4
#define physicalPlayer 0

#define XIN1_1  2
#define XIN1_2  3
#define XIN1_3  4
#define XIN1_4  5

#define XIN2_1  6
#define XIN2_2  7
#define XIN2_3  8
#define XIN2_4  9

#define YIN1_1  22
#define YIN1_2  24
#define YIN1_3  26
#define YIN1_4  28

#define YIN2_1  30
#define YIN2_2  32
#define YIN2_3  34
#define YIN2_4  36
#define magnet 52

///////// Dobbelsteen /////////
SoftwareSerial BTSerial(11, 10); // TX | RX
bool getDigit = false;
int nummers[3] = { -1, -1, -1};
int counter = 0;
int aantalOgen = -1;


///////// Input /////////
char inputChar = 0;
String inputString = "";

///////// Spelers bijhouden /////////
int nrOfPlayers = 0;
Piece *players[maxNrOfPlayers];

///////// Spelverloop bijhouden /////////
int currentTurn = 0;
int playerOnTurn = 1;
boolean playersTurnPassed = false;

void setup()
  {
    Serial.begin(9600);               //Open verbinding met GUI
    BTSerial.begin(38400);            //Open verbinding met de dobbelsteen
    
    pinMode(XIN1_1, OUTPUT); 
    pinMode(XIN1_2, OUTPUT); 
    pinMode(XIN1_3, OUTPUT); 
    pinMode(XIN1_4, OUTPUT); 
    
    pinMode(XIN2_1, OUTPUT); 
    pinMode(XIN2_2, OUTPUT); 
    pinMode(XIN2_3, OUTPUT); 
    pinMode(XIN2_4, OUTPUT); 
    
    pinMode(YIN1_1, OUTPUT);
    pinMode(YIN1_2, OUTPUT); 
    pinMode(YIN1_3, OUTPUT); 
    pinMode(YIN1_4, OUTPUT); 
    
    pinMode(YIN2_1, OUTPUT); 
    pinMode(YIN2_2, OUTPUT); 
    pinMode(YIN2_3, OUTPUT); 
    pinMode(YIN2_4, OUTPUT); 
    pinMode(magnet, OUTPUT);

    
    ////////////////// Voeg de fysieke speler toe als eerste speler ///////////////////
    nrOfPlayers = 1;
    players[nrOfPlayers-1] = new Piece("Opa", "Red", 100, 100);
  }

void loop()
  {

    ////////////////// Lees de serial port ///////////////////
    if (Serial.available() > 0)
      {
        inputString = Serial.readString();
      }





    ////////////////// Verwerk input van GUIs ///////////////////
    if (inputString != "")
      {
        if (inputString.startsWith("New player:"))      //Voeg een nieuwe speler toe
          {
            int firstColonIndex = 10;
            int secondColonIndex = inputString.indexOf(":", firstColonIndex+1);
            int thirdColonIndex = inputString.indexOf(":", secondColonIndex+1);

            String playerName = inputString.substring(firstColonIndex+1, secondColonIndex);
            String playerColor = inputString.substring(secondColonIndex+1, thirdColonIndex);

            nrOfPlayers++;
            if (nrOfPlayers < maxNrOfPlayers)
              {
                Serial.println("Trying to add new player");
                players[nrOfPlayers-1] = new Piece(playerName.c_str(), playerColor.c_str(), 10, 10);
                playersTurnPassed = true;
                Serial.print("Added ");
                Serial.print(players[nrOfPlayers-1]->getName());
                Serial.println(" as new player");
              }
            else
              {
                nrOfPlayers--;
              }

            if (nrOfPlayers == 2)
              {
                playerOnTurn = 1;
              }

            for (int i = 0; i < nrOfPlayers-1; i++)       //Geef de bestaande spelers door aan de nieuwe speler
              {
                Serial.print("New player:");
                Serial.print(players[i]->getName());
                Serial.print(":");
                Serial.print(players[i]->getColor());
                Serial.println(":");
              }
          }
        else if (inputString.startsWith("Move player:"))        //Beweeg een speler
          {
            int firstColonIndex = 11;
            int secondColonIndex = inputString.indexOf(":", firstColonIndex+1);
            int thirdColonIndex = inputString.indexOf(":", secondColonIndex+1);
            int fourthColonIndex = inputString.indexOf(":", thirdColonIndex+1);

            String playerName = inputString.substring(firstColonIndex+1, secondColonIndex);
            int newX = ( inputString.substring(secondColonIndex+1, thirdColonIndex) ).toInt();    //De nieuwe x
            int newY = ( inputString.substring(thirdColonIndex+1, fourthColonIndex) ).toInt();    //De nieuwe y
              
            if (playerName == players[playerOnTurn]->getName())
              {
                move(playerOnTurn, newX, newY);
                Serial.print("Moved ");
                Serial.println(playerName);
                playersTurnPassed = true;
              }
          }
        inputString = "";                //Gooi string leeg voor nieuwe input
      }

    if (playersTurnPassed)
      {
        ////////////////// Update wie er aan de beurt is ///////////////////
        playerOnTurn++;
        if (playerOnTurn == nrOfPlayers)
          {
            playerOnTurn = 0;
            currentTurn++;
          }

        
        //for (int i = 0; i < nrOfPlayers; i++)
          //{
            ////////////////// Stuur de nieuwe posities door naar de GUIs ///////////////////
            Serial.println("New positions");
            for (int j = 1; j < nrOfPlayers; j++)
              {
                if (j != physicalPlayer)
                  {
                    Serial.print("Move player:");
                    Serial.print(players[j]->getName());
                    Serial.print(":");
                    Serial.print(players[j]->getXPosition());
                    Serial.print(":");
                    Serial.print(players[j]->getYPosition());
                    Serial.println(":");    
                  }
              }
            
            ////////////////// Stuur wie er aan de beurt is door naar de GUIs ///////////////////
            Serial.print("Update turn:");
            Serial.print(currentTurn);
            Serial.print(":");
            Serial.print(players[playerOnTurn]->getName());
            Serial.println(":");
          //}
        playersTurnPassed = false;
      }
    
    ////////////////// Speel beurt voor fysieke speler ///////////////////
    updateDice();
    if (playerOnTurn == physicalPlayer)
      {
        if (isNumberAvailable())
          {
            int movesForward = getDots();
            
            for (int i = 0; i < nrOfPlayers; i++)
              {
                if (i != physicalPlayer)
                  {
                    Serial.print("Move physical player:");
                    Serial.print(players[0]->getName());
                    Serial.print(":");
                    Serial.print(movesForward);
                    Serial.println(":");
                  }
              }
            playersTurnPassed = true;
          }
      }
}



boolean updateDice()      //Leest de waarde van de dobbelsteen
{
  if (BTSerial.available())
  {
    nummers[counter] = (BTSerial.read() - 48);

    if (nummers[0] == nummers[1] && nummers[1] == nummers[2])
    {
      aantalOgen = nummers[counter];
      counter = 0;
      getDigit = true;
    }
    counter ++;

    if (counter == 3)
    {
      counter = 0;
      //getDigit = false;
    }
  }
}

bool isNumberAvailable()        //Returned true als de dobbelsteen lang genoeg stil ligt om te weten welk nummer is gegooid. Anders false.
{
  if (aantalOgen == -1)
    {
      return false;
    }
  return getDigit;
}

int getDots()                   //Geeft het aantal ogen dat op de dobbelsteen bovenaan ligt.
  {
    getDigit = false;
    int ret = aantalOgen;
    aantalOgen = -1;
    nummers[0] = -1;
    nummers[1] = -1;
    nummers[2] = -1;
    return ret;
  }
