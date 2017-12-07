import os

for filename in os.listdir('./data/UNKNOWN'):
	os.system("rm -rf ./data/UNKNOWN/" + filename)
