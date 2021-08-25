
//The amount of steps needed for every wheel
float steps[4];
int _step = 0;

//The Pins to control the stepper motors, 8 pins because 2 motors need to be active at the same time, each motor uses 4 pins.
int Pin0;
int Pin1;
int Pin2;
int Pin3;
int Pin4;
int Pin5;
int Pin6;
int Pin7;
//1000steps = 37.5mm
//2000 = 75mm
//4000 = 150mm

unsigned long currentMillis;
long time;
boolean Direction;
boolean dir = true;

void motors(float* distance){

  convertToSteps(distance);
 
  Serial.println("X in mm");
  Serial.println(distance[0]);
  Serial.println("X in steps");
  Serial.println(steps[0]);
  Serial.println("Y in mm");
  Serial.println(distance[1]);
  Serial.println("Y in steps");
  Serial.println(steps[2]);

  activateMotors();
}

void convertToSteps(float* distance){

/*  Since there is some friction with the wheels on the ground due to the bars, the stepper motors and the magnet. I decided to measure the results instead of calculating it.
 *   1000steps == 37.5mm
 *   2000 steps == 75mm
 *   4000steps == 150mm
 *   1000steps == 37.5mm

*/
  steps[0] = (floor(distance[0]/0.0375));
  steps[1] = (floor((steps[0]*-1)));
  steps[2] = (floor(distance[1]/0.0375));
  steps[3] = (floor((steps[2]*-1)));

  }

void activateMotors(){              //time and direction known

/*  1 second == 4095steps 
 *  So the amount of steps / 4095 is the amound of time needed for the motors to be active. 
 */
float timeToMove1 = steps[0] / 4.095;
float timeToMove3 = steps[2] / 4.095;

//Change pins for the switch case method
Pin0 = XIN1_1;
Pin1 = XIN1_2;
Pin2 = XIN1_3;
Pin3 = XIN1_4;
Pin4 = XIN2_1;
Pin5 = XIN2_2;
Pin6 = XIN2_3;
Pin7 = XIN2_4;

GO(timeToMove1);

//Change pins for the switch case method
Pin0 = YIN1_1;
Pin1 = YIN1_2;
Pin2 = YIN1_3;
Pin3 = YIN1_4;
Pin4 = YIN2_1;
Pin5 = YIN2_2;
Pin6 = YIN2_3;
Pin7 = YIN2_4;

GO(timeToMove3);

Serial.println("Piece moved");
}

void GO(float timeToMove){

  if(timeToMove<0){
    dir=false;
  }else{
    dir=true;
  }  
  currentMillis = millis();
 
  while(millis() - currentMillis < abs(timeToMove)){ //Move the wheels until the time needed to move has surpassed realtime.
    goMove();
  }
}

void goMove(){
  /*  Pins 4,5,6,7 are the exact opposite of the switch case.
   *   So case 0: Pin0,1,2,3 == case 8: 5,6,7,8
   *   etc.
   */
 switch(_step){ 
   case 0: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, HIGH);

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, LOW);      

   break;  
   case 1: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, HIGH); 
     digitalWrite(Pin3, HIGH); 

     digitalWrite(Pin4, HIGH);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, HIGH);

   break;  
   case 2: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, HIGH); 
     digitalWrite(Pin3, LOW); 

     digitalWrite(Pin4, HIGH);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, LOW);
   break;  
   case 3: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, HIGH); 
     digitalWrite(Pin2, HIGH); 
     digitalWrite(Pin3, LOW);

     digitalWrite(Pin4, HIGH);  
     digitalWrite(Pin5, HIGH); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, LOW);

   break;  
   case 4: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, HIGH); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, LOW); 

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, HIGH); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, LOW); 


   break;  
   case 5: 
     digitalWrite(Pin0, HIGH);  
     digitalWrite(Pin1, HIGH); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, LOW);

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, HIGH); 
     digitalWrite(Pin6, HIGH); 
     digitalWrite(Pin7, LOW);
   break;  
     case 6: 
     digitalWrite(Pin0, HIGH);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, LOW);

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, HIGH); 
     digitalWrite(Pin7, LOW); 
     
   break;  
   case 7: 
     digitalWrite(Pin0, HIGH);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, HIGH);

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, HIGH); 
     digitalWrite(Pin7, HIGH); 
   break;  
   default: 
     digitalWrite(Pin0, LOW);  
     digitalWrite(Pin1, LOW); 
     digitalWrite(Pin2, LOW); 
     digitalWrite(Pin3, LOW);

     digitalWrite(Pin4, LOW);  
     digitalWrite(Pin5, LOW); 
     digitalWrite(Pin6, LOW); 
     digitalWrite(Pin7, HIGH);

   break;  
 } 
 if(dir){ 
   _step++; 
 }else{ 
   _step--; 
 } 
 if(_step>7){ 
   _step=0; 
 } 
 if(_step<0){ 
   _step=7; 
 } 
 delay(1); 
}
