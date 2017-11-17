import os
import platform

# Linux dependencies
if (platform.system() == 'Linux'):
	os.system("sudo apt-get install build-essential python3-dev python3-setuptools python3-numpy python3-scipy libatlas-dev libatlas3gf-base")
	os.system("sudo apt-get install libfreetype6-dev libpng-dev")
	os.system("sudo apt-get install pkg-config")
	os.system("sudo apt-get install python3-pip")
	os.system("sudo pip3 install numpy")
	os.system("sudo pip3 install fastdtw")
	os.system("sudo pip3 install sklearn")
	os.system("sudo pip3 install scipy")
	os.system("sudo pip3 install matplotlib")
	os.system("sudo pip3 install pandas")

# Other OS we don't support on server
else:
	print('Sorry, this operating system is not supported.')
	exit()
