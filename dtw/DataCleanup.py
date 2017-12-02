import csv
import os
import sys
import numpy
import datetime
from time import strptime

####################################
# Extra Functions
####################################

#" ./raw_data/" + "TRAIN-2015-10-05-" + string[23:25] + string[26:28] + string[29:31]) + "-fistbump.txt"

def panic(error_message):
	print(error_message)
	exit()

def csvToText(csvfile, t_or_t):
	# Naming setup
	csv_file = csvfile.split("/").pop().split("Data").pop()
	year = 	csv_file[24:28]
	month = str(strptime(csv_file[4:7],'%b').tm_mon).zfill(2)
	day = csv_file[8:10].zfill(2)
	hour = csv_file[11:13].zfill(2)
	minute = csv_file[14:16].zfill(2)
	second = csv_file[17:19].zfill(2)

	# Getting name of directory
	csv_dir = csvfile.split("/")[2]

	if (t_or_t.lower() == 'train'):
		new_file = "./data/" + csv_dir + "/TRAIN-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + csv_dir.lower() + ".txt"
	elif (t_or_t.lower() == 'test'):
		new_file = "./data/" + csv_dir + "/TEST-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + csv_dir.lower() + ".txt"
	else:
		panic("Parameter passed in not 'test' or 'train'!")

	# Reading accelerometer and gyroscope data from old csv file
	f = open(csvfile, 'rb')
	reader = csv.reader(f)
	txt_rows = []
	for row in reader:
		txt_rows.append(row[1] + " " + row[2] + " " + row[3] + " " + row[4] + " " + row[5] + " " + row[6])
	f.close()

	g = open(new_file, "w+")
	g.close()

	# Writing into output txt file
	i = 0
	while (i < len(txt_rows)):	
		with open(new_file, 'a') as g:
			g.write(txt_rows[i] + "\n")
		i = i+1
	g.close()
	
def stringToText(data_string, t_or_t):
	# Naming setup
	now = datetime.datetime.now()
	year = 	str(now.year)
	month = str(now.month).zfill(2)
	day = str(now.day).zfill(2)
	hour = str(now.hour).zfill(2)
	minute = str(now.minute).zfill(2)
	second = str(now.second).zfill(2)

	# Getting name of directory
	file_path = 'UNKNOWN'

	if (t_or_t.lower() == 'train'):
		new_file = "./data/" + file_path + "/TRAIN-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + file_path.lower() + ".txt"
	elif (t_or_t.lower() == 'test'):
		new_file = "./data/" + file_path + "/TEST-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + file_path.lower() + ".txt"
	else:
		panic("Parameter passed in not 'test' or 'train'!")

	# Reading accelerometer and gyroscope data from string
	txt_rows = data_string.split("\\n")

	g = open(new_file, "w+")
	g.close()

	# Writing into output txt file
	i = 0
	while (i < len(txt_rows)):	
		with open(new_file, 'a') as g:
			g.write(txt_rows[i] + "\n")
		i = i+1
	g.close()
	

####################################
# Main logic of module
####################################

# Check arguments
if (len(sys.argv) != 4):
	panic("Usage: python ./DataCleanup.py [csv|string] [<csvfile-path>|<string-of-text>] [test|train]")

# Defining
data = sys.argv[2]
t_or_t = sys.argv[3]


if (sys.argv[1] == 'csv'):
	# x, y, and z arrays for accelerometer data for csv file
	csvToText(data, t_or_t)	
elif (sys.argv[1] == 'string'):
	stringToText(data, t_or_t)
else:
	panic("Second parameter passed in was neither 'csv' nor 'string'. Please check your parameters.")


