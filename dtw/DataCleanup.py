import csv
import os
import sys
import numpy
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
	csv_file = csvfile.split("/").pop()
	year = 	csv_file[36:40]
	month = str(strptime(csv_file[16:19],'%b').tm_mon).zfill(2)
	day = csv_file[20:22]
	hour = csv_file[23:25]
	minute = csv_file[26:28]
	second = csv_file[29:31]

	if (t_or_t.lower() == 'train'):
		new_file = "./data/Fistbump/TRAIN-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + "fistbump.txt"
	elif (t_or_t.lower() == 'test'):
		new_file = "./data/Fistbump/TEST-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + "fistbump.txt"
	else:
		panic("Parameter passed in not 'test' or 'train'!")

	# Reading accelerometer data from old csv file
	f = open(csvfile, 'rb')
	reader = csv.reader(f)
	txt_rows = []
	for row in reader:
		txt_rows.append(row[1] + " " + row[2] + " " + row[3])
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
	

####################################
# Main logic of module
####################################

# Check arguments
if (len(sys.argv) != 3):
	panic("Usage: python ./DataCleanup.py <csvfile-path> [test|train]")

# Defining our files
csvfile = sys.argv[1]
t_or_t = sys.argv[2]

# x, y, and z arrays for accelerometer data for csv file
csvToText(csvfile, t_or_t)


