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
#0.931640625q-0.33642578125q0.011962890625q-1.097561001777649q0.18292683362960815q0.3353658616542816_0.939697265625q-0.350830078125q0.017822265625q-1.6158536672592163q-0.792682945728302q-0.24390244483947754_0.9326171875q-0.35009765625q0.017578125q-0.7012195587158203q-0.4878048896789551q-0.8536585569381714_0.940673828125q-0.357177734375q0.019287109375q-0.4573170840740204q-0.6402438879013062q-0.5792683362960815_0.93994140625q-0.344970703125q0.019775390625q0.5792683362960815q-0.7317073345184326q0.18292683362960815_0.940185546875q-0.34912109375q0.02197265625q-0.060975611209869385q0.9146341681480408q-0.3658536672592163_0.9345703125q-0.3515625q0.013916015625q0.09146341681480408q0.9756097793579102q-1.7073171138763428_0.938720703125q-0.351318359375q0.01220703125q-2.1341464519500732q1.158536672592163q0.030487805604934692_0.930908203125q-0.345947265625q0.01416015625q-2.195122003555298q0.9451220035552979q-0.9756097793579102_0.927978515625q-0.349609375q0.01611328125q-2.77439022064209q1.432926893234253q0.5792683362960815_0.935302734375q-0.3564453125q0.0185546875q-1.737804889678955q-0.5792683362960815q3.170731782913208_0.914306640625q-0.340576171875q-4.8828125E-4q-6.310975551605225q2.682926893234253q6.371951580047607_0.917236328125q-0.3759765625q0.03515625q-2.865853786468506q0.5182926654815674q-0.8536585569381714_0.95361328125q-0.42822265625q0.049560546875q-0.6402438879013062q-0.4573170840740204q5.853658676147461_1.106689453125q-0.74169921875q0.199951171875q-17.621952056884766q14.420732498168945q63.44512176513672_0.89892578125q-0.879638671875q0.306640625q-17.926830291748047q61.09756088256836q156.6768341064453_0.474365234375q-1.072265625q0.055419921875q-105.64024353027344q80.88414764404297q209.78659057617188_-0.056884765625q-1.064453125q-0.2236328125q-106.70732116699219q16.40243911743164q229.54269409179688_-0.476318359375q-0.77685546875q-0.566650390625q-4.2682929039001465q-109.39024353027344q187.04269409179688_-0.6787109375q0.264892578125q-0.19580078125q92.46951293945313q-112.6524429321289q124.54268646240234_-0.505615234375q1.037353515625q0.21826171875q-24.14634132385254q-95.45732116699219q28.780488967895508_-0.179443359375q1.3134765625q0.118896484375q-183.9634246826172q-19.60365867614746q-95.48780822753906_0.21142578125q-0.26123046875q-0.632080078125q-254.23780822753906q89.90853881835938q-280.0914611816406_-1.086669921875q3.07861328125q-2.511962890625q52.04268264770508q254.60366821289063q170.5792694091797_0.303955078125q-1.000732421875q-0.15673828125q11.890244483947754q-50.97561264038086q-128.75_0.29345703125q-0.89306640625q-0.449951171875q90.82317352294922q-25.457317352294922q80.60975646972656_0.106689453125q-0.09912109375q-0.083984375q69.54268646240234q-24.60365867614746q-42.317073822021484_0.152099609375q-0.274658203125q-0.06884765625q-54.26829528808594q31.25q-113.87195587158203_0.48193359375q-0.8818359375q-0.466796875q-102.19512176513672q89.81707763671875q-141.4329376220703_0.881103515625q-1.035400390625q-0.432373046875q-71.82926940917969q88.59756469726563q-150.85366821289063_1.14013671875q-0.9111328125q-0.135498046875q-14.085366249084473q31.09756088256836q-130.3048858642578_0.999267578125q-0.6728515625q0.038818359375q-3.5975611209869385q-5.670732021331787q-6.280488014221191_0.96728515625q-0.429931640625q-0.072021484375q10.060976028442383q2.6219513416290283q50.853660583496094_1.01953125q-0.3154296875q-0.179443359375q
    # Naming setup
    now = datetime.datetime.now()
    year =  str(now.year)
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
    txt_rows = data_string.split("_")
    
    # Creating file
    g = open(new_file, "w+")
    g.close()

    # Writing into output txt file
    i = 0
    while (i < len(txt_rows)):  
        with open(new_file, 'a') as g:
            txt_row = txt_rows[i].split("q")
            txt_row_string = " ".join(txt_row)
            g.write(txt_row_string + "\n")
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


