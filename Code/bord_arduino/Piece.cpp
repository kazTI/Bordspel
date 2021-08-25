#include "Piece.h"

#include <Arduino.h>
#include <WString.h>

Piece::Piece(const char pName[], const char pColor[], int x, int y) {
  /*Serial.print("Constructor: naam = ");
  Serial.println(*pName);*/
  setName(pName);
  setColor(pColor);
  setPosition(x, y);
  //cout << "Constructor: " << pName << endl;
}

void Piece::setName(const char pName[]) {
  /*Serial.print("Setter: naam = ");
  Serial.println(*pName);*/
  //cout << "Setter: " << pName << endl;
  //playerName = pName;
  strcpy(playerName, pName);

  //cout << "Field in setter: " << playerName << endl;
  /*Serial.print("Field: naam = ");
  Serial.println(*playerName);*/
}

void Piece::setColor(const char pColor[]) {
  //playerName = pColor;
  strcpy(playerColor, pColor);
}

void Piece::setPosition(int x, int y) {
  xPosition = x;
  yPosition = y;
}

char* Piece::getName() {
  //Serial.print("Getter: naam = ");
  //Serial.println(playerName);
  //cout << "Field in getter: " << playerName << endl;
  return playerName;
}

char* Piece::getColor() {
  return playerColor;
}

int Piece::getXPosition() {
  return xPosition;
}

int Piece::getYPosition() {
  return yPosition;
}
