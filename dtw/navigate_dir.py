import os

file_path = './raw_data/'
file_path_appended = ''

# Splits about 70-30 train and test
for folder in sorted(os.listdir(file_path)):
	file_path_appended = file_path + folder + "/"
	arr_split = [None] * len(os.listdir(file_path_appended))
	# Training data
	i = 0
	while (i < int(0.7 * len(os.listdir(file_path_appended)))):
		arr_split[i] = 'train'
		i += 1
	# Test data
	while (i < len(os.listdir(file_path_appended))):
		arr_split[i] = 'test'
		i += 1
	
	# Going through data
	i = 0
	while (i < len(os.listdir(file_path_appended))):	
		item = sorted(os.listdir(file_path_appended))[i]
		print(item)
		# t_or_t = raw_input("Train or test? ")
		# while (t_or_t.lower() != 'train' and t_or_t.lower() != 'test'):
		#	t_or_t = raw_input("That was not valid. Train or test? ")
		t_or_t = arr_split[i]
		print(t_or_t.upper())
		os.system("python ./DataCleanup.py csv " + "\"" + file_path_appended + item + "\"" + " " + t_or_t)
		print("====================================================")
		i += 1
	 
