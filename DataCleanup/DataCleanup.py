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

def listDifference(arr1, arr2):
	arr_sim = []
	for (element1, element2) in zip(arr1, arr2):
		arr_sim.append(abs(element1 - element2))
	ret_val = float(numpy.mean(arr_sim))
	return ret_val
	
def averageDifference(sim1, sim2, *args):
	arr_sim = []
	arr_sim.append(sim1)
	arr_sim.append(sim2)
	for arg in args:
		arr_sim.append(arg)
	ret_val = float(numpy.mean(arr_sim))
	return ret_val

####################################
# Main logic of module
####################################

# Check arguments
if (len(sys.argv) != 3):
	panic("Usage: python ./DataCleanup <csvfile-path1> <csvfile-path2>")

# Defining our files
csvfile1 = sys.argv[1]
csvfile2 = sys.argv[2]

# x, y, and z arrays for accelerometer data for first csv file
x_array_accel1 = csvArrayAppend(csvfile1, 'x', 'accelerometer')
y_array_accel1 = csvArrayAppend(csvfile1, 'y', 'accelerometer')
z_array_accel1 = csvArrayAppend(csvfile1, 'z', 'accelerometer')

# x, y, and z arrays for gyroscope data for first csv file
x_array_gyro1 = csvArrayAppend(csvfile1, 'x', 'gyroscope')
y_array_gyro1 = csvArrayAppend(csvfile1, 'y', 'gyroscope')
z_array_gyro1 = csvArrayAppend(csvfile1, 'z', 'gyroscope')

# x, y, and z arrays for accelerometer data for second csv file
x_array_accel2 = csvArrayAppend(csvfile2, 'x', 'accelerometer')
y_array_accel2 = csvArrayAppend(csvfile2, 'y', 'accelerometer')
z_array_accel2 = csvArrayAppend(csvfile2, 'z', 'accelerometer')

# x, y, and z arrays for gyroscope data for second csv file
x_array_gyro2 = csvArrayAppend(csvfile2, 'x', 'gyroscope')
y_array_gyro2 = csvArrayAppend(csvfile2, 'y', 'gyroscope')
z_array_gyro2 = csvArrayAppend(csvfile2, 'z', 'gyroscope')

# Individual Difference
x_accel_diff = listDifference(x_array_accel1, x_array_accel2)
y_accel_diff = listDifference(y_array_accel1, y_array_accel2)
z_accel_diff = listDifference(z_array_accel1, z_array_accel2)
x_gyro_diff = listDifference(x_array_gyro1, x_array_gyro2)
y_gyro_diff = listDifference(y_array_gyro1, y_array_gyro2)
z_gyro_diff = listDifference(z_array_gyro1, z_array_gyro2)


print("X-coordinate accelerometer difference: %f" % x_accel_diff)
print("Y-coordinate accelerometer difference: %f" % y_accel_diff)
print("Z-coordinate accelerometer difference: %f" % z_accel_diff)
print("X-coordinate gyroscope difference: %f" % x_gyro_diff)
print("Y-coordinate gyroscope difference: %f" % y_gyro_diff)
print("Z-coordinate gyroscope difference: %f" % z_gyro_diff)

# Accelerometer and Gyroscope Difference
accel_diff = averageDifference(x_accel_diff, y_accel_diff, z_accel_diff)
gyro_diff = averageDifference(x_gyro_diff, y_gyro_diff, z_gyro_diff)
print("Accelerometer difference: %f" % accel_diff)
print("Gyroscope difference: %f" % gyro_diff)

# Total Difference
total_diff = averageDifference(accel_diff, gyro_diff)
print("Total difference: %f" % total_diff)
