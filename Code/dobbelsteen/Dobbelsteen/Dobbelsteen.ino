#include<Wire.h>
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11);

const int MPU_addr=0x68;
int16_t AcX,AcY,AcZ,Tmp,GyX,GyY,GyZ;

int minVal=265;
int maxVal=402;

double x;
double y;
double z;
 


void setup(){
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  BTSerial.begin(38400);
  Serial.begin(38400);
}
void loop(){
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  AcX=Wire.read()<<8|Wire.read();
  AcY=Wire.read()<<8|Wire.read();
  AcZ=Wire.read()<<8|Wire.read();
    int xAng = map(AcX,minVal,maxVal,-90,90);
    int yAng = map(AcY,minVal,maxVal,-90,90);
    int zAng = map(AcZ,minVal,maxVal,-90,90);

       x= RAD_TO_DEG * (atan2(-yAng, -zAng)+PI);
       y= RAD_TO_DEG * (atan2(-xAng, -zAng)+PI);
       z= RAD_TO_DEG * (atan2(-yAng, -xAng)+PI);

/*
       BTSerial.write("x: ");
       BTSerial.println(x);
       BTSerial.write("y: ");
       BTSerial.println(y);
       BTSerial.println("");
*/

if(x < 205 && x > 175 && y < 215 && y > 185) BTSerial.write("1");
if(x < 330 && x > 280 && y < 100 && y > 70) BTSerial.write("2");
if(x < 105 && x > 75 && y < 285 && y > 255) BTSerial.write("3");
if(x < 290 && x > 260 && y < 305 && y > 275) BTSerial.write("4");
if(x < 370 && x > 340 && y < 290 && y > 255 || x < 20 && x > 0 && y < 290 && y > 255) BTSerial.write("5");
if(x < 375 && x > 345 && y < 360 && y > 330 || x < 2 && x > 0 && y < 360 && y > 330) BTSerial.write("6");


     delay(1000);
}
