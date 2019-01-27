import smbus
import time
import requests
import datetime
bus = smbus.SMBus(1)

#This is the address we setup in the Arduino Program
address = 0x12
#send value to arduino
def writeNumber(value):
  bus.write_byte(address,value)
  return -1
#Get value from Arduino
def askValues():
  writeNumber(1)
  number1 = readNumber()*(2**8)
  time.sleep(1)
  writeNumber(2)
  number2 = readNumber()
  return (number1+number2)
  
#add recorded value to db every 30s
while True:
  data = askValues()
  res1= 9100.0
  todayTime=datetime.datetime.now()
  buffer= data * 3.3
  Vout= (buffer)/1024.0
  buffer= (3.3/Vout) -1
  R2= res1 * buffer
  v2=round(R2,2)
  print "resistance: ", R2
  value = {"day": todayTime.day, "month": todayTime.month, "year": todayTime.year, "hour": todayTime.hour, "minutes": todayTime.minute, "value":v2 }
  resp = requests.post('https://api.mlab.com/api/1/databases/raspberry/collections/resistance?apiKey=vQNIw4w4UJJ_VG-6_dt96XZc7ieICcAa', json=value)
  if resp.status_code != 200:
    print(resp.status_code)
  print('Created value. ID: {}'.format(resp.json()["_id"]))
  time.sleep(30)