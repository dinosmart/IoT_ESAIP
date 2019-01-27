#include <Wire.h>

#define SLAVE_ADDRESS 0x04
int diode = A0; 
unsigned int a;
int mask = 0xFF, receivedData;
unsigned char toSend = 0,choix;
void setup() 
{
  pinMode(13, OUTPUT);
  Serial.begin(9600); // start serial for output
  
  // initialize i2c as slave
  Wire.begin(SLAVE_ADDRESS);
  // define callbacks for i2c communication
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);
  Serial.println("Ready!");
}

void loop() 
{ 
  delay(100);
}



// callback for sending data
void receiveData(int byteCount)
{
  while(Wire.available())
  {
    receivedData = Wire.read();
    if (receivedData == 0)
    {
      a = analogRead(diode);
      Serial.println(a);
      toSend = a & mask;
      a = a >> 8;
    }
    else if (receivedData == 1)
    {
      choix = 0;
    }
    else if (receivedData == 2)
    {
      choix = 1;
    }
  }
}
void sendData()
{
  if(choix == 0)
  {
    Wire.write(toSend);
  }
  else if(choix == 1)
  {
    Wire.write(a);
  }
}
