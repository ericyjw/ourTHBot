import telebot
import time
from telebot import types
from telebot import util

bot_token = '593031383:AAHhl5lTr8_Rk36LwsMLYte6DoEByRxHR_c'

bot = telebot.TeleBot(bot_token)


@bot.message_handler(commands=['calendar'])
def send_welcome(message):
   	f = open("calendar.txt", 'r')
    	lines = f.readlines()
    	length = len(lines)
    	for i in range(length):
        	lines[i] = lines[i].strip()
    	i=0;maxTen=0;
    	while (i<length and maxTen < 15):
		stringz = ""
		stringz = stringz + lines[i][:-1] + '\n'		       
        	tempNum = lines[i].split(" ")[-1]
        	for z in range(int(tempNum)):
            		i=i+1
            		maxTen=maxTen+1
			stringz = stringz + lines[i] + '\n'
		bot.send_message(message.chat.id, '{}\n'.format(stringz))	    		
        	i=i+1
    	f.close()
  
        
while (True):
	try:
		bot.polling()	
	except Exception:
		time.sleep(15)
