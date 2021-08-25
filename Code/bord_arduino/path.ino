#include "Piece.h" 


float getSlope(Piece *p){  
  return (((float)destinationY - (float)p->getYPosition())
         / ((float)destinationX - (float)p->getXPosition()));
}

float getPerpendicular(Piece *p){ //R1 * R2 = -1
  return ((-1) / getSlope(p));
}

float getB(Piece *p){   //B = ((Ax-Y) *-1) 
  return (((getSlope(p) *(float)p->getXPosition())
            - (float)p->getYPosition())
              *-1);
}

float getBPoint(Piece *line, Piece *point){   //B = ((Ax-Y) *-1) 
  return (((getPerpendicular(line) *(float)point->getXPosition())
            - (float)point->getYPosition())
              *-1);
}

float* getInterception(Piece *line, Piece *point){ //line ant dot
  float* interception = new float[2];

  interception[0] = ((getBPoint(line, point) - getB(line))
                     / (getSlope(line) - getPerpendicular(line)));  //X
  interception[1] = getSlope(line) * interception[0] + getB(line);  //Y

  return interception;

}

float getDistancePieces(Piece *p, float interception[2]){
  float deltaX = (pow(p->getXPosition() - interception[0], 2));
  float deltaY = (pow(p->getYPosition() - interception[1], 2));

  return  sqrt(deltaX + deltaY);
  }
