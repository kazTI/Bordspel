#ifndef PIECE_H
#define PIECE_H

//#include <WString.h>
//#include <iostream>
//using namespace std;


class Piece {
  protected:
    char playerName[20];
    char playerColor[10];
    int xPosition, yPosition;
  public:
    Piece(const char pName[], const char pColor[], int x, int y);
    void setName(const char pName[]);
    void setColor(const char pColor[]);
    void setPosition (int x, int y);
    char* getName();
    char* getColor();
    int getXPosition();
    int getYPosition();
 };

 

 
#endif
