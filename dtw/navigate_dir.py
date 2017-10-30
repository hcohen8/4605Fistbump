import os

file_path = './raw_data/'

for item in sorted(os.listdir(file_path)):
	print(item)
	t_or_t = raw_input("Train or test? ")
	while (t_or_t.lower() != 'train' and t_or_t.lower() != 'test'):
		t_or_t = raw_input("That was not valid. Train or test? ")
	os.system("python ./DataCleanup.py " + "\"" + file_path + item  + "\"" + " " + t_or_t)
	print("====================================================")
 
