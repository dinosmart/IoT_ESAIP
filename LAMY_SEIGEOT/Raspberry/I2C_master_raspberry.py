import smbus
import time
from datetime import datetime
import requests

bus = smbus.SMBus(1)
address = 0x12

def writeNumber(value):
	bus.write_byte(address, value)
	return -1

def readNumber():
	number = bus.read_byte(address)
	return number

while True:
	writeNumber(0)
	time.sleep(1)

	writeNumber(1)
	time.sleep(1)

	number1 = readNumber()
	time.sleep(1)

	writeNumber(2)
	time.sleep(1)

	number2 = readNumber()

	if number2 == 3:
		number1=number1+768
	if number2 == 2:
		number1=number1+512
	if number2 == 1:
		number1=number1+256


	print "Voici la mesure" , number1
	print number2

	n = datetime.now()
	date = str(n.year) + "/"+ str(n.month) +"/"+  str(n.day)
	heure =  str(n.hour)+"h"+str(n.minute) +"min " + str(n.second)+"s"
	task = {"Temperature": number1 ,"Date":str(date),"Heure":str(heure)}
	resp = requests.post('https://api.mlab.com/api/1/databases/matpau/collections/mesure?apiKey=zRpsJsByf_zbFivaly8-C4pzMwAGP90q', json=task)
	if resp.status_code != 200:
	    print(resp.status_code)
	print('Created task. ID: {}'.format(resp.json()["_id"]))

	print(heure)
	print(date)
	time.sleep(10);
