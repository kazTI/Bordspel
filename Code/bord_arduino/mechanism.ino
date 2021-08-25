#include "Piece.h"

/*Destination of the Piece*/
float destinationX = 0;
float destinationY = 0;

/*Destination when new path needs to be calculated */
float subDestinationX = 0;
float subDestinationY = 0;
boolean pieceInterrupt = false;

/*The path in millimeters [0] = X, [1] = Y*/
float* moveDistance = new float[2];


void move(int playerNumber, int newX, int newY){  //Gets called by bord_aruino
  destinationX = newX;
  destinationY = newY;
  CheckPath(playerNumber, destinationX, destinationY);    //Checks if one of the Pieces is in the way of the path that will also be calculated.

  if(pieceInterrupt){                                     //If a Piece is in the way, a new path needs to be created
    while(pieceInterrupt){
        Serial.println("interrupt!");
        checkPath(playerNumber, subDestinationX, subDestinationY);    //The subDestinations are calculated in this method until one is found that is valid.
      
     }
     move(playerNumber, destinationX, destinationY);                           //Move the Piece    
        players[playerNumber]->setPosition(subDestinationX, subDestinationY);  //update the current location of the Piece after movement.
   }
    
}

void printPath(int playerNumber, int i, float* moveDistance, float* interception){
          Serial.println(moveDistance[0]);
          Serial.println(moveDistance[1]);

          Serial.println(interception[0]);
          Serial.println(interception[1]);

          Serial.println("Distance between piece " + (String)playerNumber  + " and piece " + (String)i + ": ");
          Serial.println(getDistancePieces(players[i], interception)); //line, point
  }

void checkPath(int playerNumber, int destinationX, int destinationY){   //object, 
  float* interception;

    for(int i=0; i < maxNrOfPlayers;i++){                                                                     //Check every piece 

//The formulas in pah.ino don't work when the Slope == 0. so for perfectly vertical and horizontal lines, something else needs to happen.

        if(players[playerNumber]->getXPosition() == destinationX){
          Serial.println("the line is between the player and the destination coords is vertical.");
          
          moveDistance[0] = 0;
          moveDistance[1] = abs(players[playerNumber]->getYPosition() - destinationY);//distance is delta Y

          interception[0] = players[playerNumber]->getXPosition();
          interception[1] = (players[i]->getYPosition());//distance is delta Y;

        }else if(players[playerNumber]->getYPosition() == destinationY){
          Serial.println("the line is between the player and the destination coords is horizontal.");
       
          moveDistance[0] = abs(players[playerNumber]->getXPosition() - destinationX);//distance is delta X
          moveDistance[1] = 0;
          
          interception[0] = players[playerNumber]->getYPosition();
          interception[1] = (players[i]->getXPosition());//distance is delta Y;
          
        }else{
          moveDistance[0] = abs(players[playerNumber]->getXPosition() - destinationX);
          moveDistance[1] = abs(players[playerNumber]->getYPosition() - destinationY);
          
          interception = (getInterception(players[playerNumber], players[i]));    //Calculate the interception coordinates.
        }
    
          printPath(playerNumber, i, moveDistance, interception);     //Optional print to the Serial Monitor.
    
      if (getDistancePieces(players[i], interception) < 5 && getDistancePieces(players[i], interception)!=0){   //If a Piece is in the way of the path
        Serial.println("a piece is too close to the path!");
        pieceInterrupt = true;

        if((getSlope(players[playerNumber]) > 1)){                                           //Y veranderen
            if(players[i]->getYPosition() < players[playerNumber]->getYPosition()){
              subDestinationY = players[playerNumber]->getYPosition() + 200;
            
            }else{
              subDestinationY = players[playerNumber]->getYPosition() - 200;
            
              }
            }else{                                                                          //X veranderen
              if(players[i]->getXPosition() < players[playerNumber]->getXPosition()){
                subDestinationX = players[playerNumber]->getXPosition() + 200;
              
              }else{
                subDestinationX = players[playerNumber]->getXPosition() - 200;
              
                }
             }
        break;
      }else{
        pieceInterrupt = false;
      }
   }
   if(!pieceInterrupt){ //Move the Piece and turn on the magnet to move it with.
       digitalWrite(magnet ,HIGH);
       motors(moveDistance);
       digitalWrite(magnet ,LOW);
      }
  }
