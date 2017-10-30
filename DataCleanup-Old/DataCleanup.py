import csv
import sys
import numpy

####################################
# Extra Functions
####################################

def panic(error_message):
	print(error_message)
	exit()

def csvArrayAppend(csvfile, letter, tp):
	f = open(csvfile, 'rb')
	reader = csv.reader(f)
	
	# Accelerometer or gyroscope check
	if (tp == 'accelerometer'):
		tp_var = 0
	elif (tp == 'gyroscope'):
		tp_var = 3
	else:
		panic("Parameter passed in not 'accelerometer' or 'gyroscope'!")
	
	# x, y, z check
	if (letter == 'x'):
		letter_var = 0
	elif (letter == 'y'):
		letter_var = 1
	elif (letter == 'z'):
		letter_var = 2
	else:
		panic("Parameter passed in not 'x', 'y', or 'z'!")

	arg_to_get = tp_var + letter_var + 1

	# Return array
	array_to_return = []
	for row in reader:
		array_to_return.append(float(row[arg_to_get]))
	f.close()
	return array_to_return

def magnitudeArray(x, y, z):
	magnitude_array = []
	for (element1, element2, element3) in zip(x, y, z):
		magnitude_array.append(float((element1**2 + element2**2 + element3**2)**(0.5)))
	return magnitude_array
	

####################################
# Main logic of module
####################################

# Check arguments
if (len(sys.argv) != 2):
	panic("Usage: python ./DataCleanup.py <csvfile-path>")

# Defining our files
csvfile = sys.argv[1]

# x, y, and z arrays for accelerometer data for csv file
x_array_accel = csvArrayAppend(csvfile, 'x', 'accelerometer')
y_array_accel = csvArrayAppend(csvfile, 'y', 'accelerometer')
z_array_accel = csvArrayAppend(csvfile, 'z', 'accelerometer')

# x, y, and z arrays for gyroscope data for csv file
x_array_gyro = csvArrayAppend(csvfile, 'x', 'gyroscope')
y_array_gyro = csvArrayAppend(csvfile, 'y', 'gyroscope')
z_array_gyro = csvArrayAppend(csvfile, 'z', 'gyroscope')

# magnitude array for accelerometer data for csv file
accel_array = magnitudeArray(x_array_accel, y_array_accel, z_array_accel)

# gyroscope array for accelerometer data for csv file
gyro_array = magnitudeArray(x_array_gyro, y_array_gyro, z_array_gyro)

# printing arrays for use in Java DTW
print(accel_array)
print(gyro_array)


