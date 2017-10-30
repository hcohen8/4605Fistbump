import os
import platform

# Windows dependencies
if (platform.system() == 'Windows'):
	os.system("pip install numpy")
	os.system("pip install fastdtw")
	os.system("pip install sklearn")
	os.system("pip install scipy")
	os.system("pip install matplotlib")
	os.system("pip install pandas")

# Linux dependencies
elif (platform.system() == 'Linux'):
	os.system("sudo pip3 install numpy")
	os.system("sudo pip3 install fastdtw")
	os.system("sudo pip3 install sklearn")
	os.system("sudo pip3 install scipy")
	os.system("sudo pip3 install matplotlib")
	os.system("sudo pip3 install pandas")

# Mac dependencies
elif (platform.system() == 'Darwin'):
	os.system("sudo pip install numpy")
	os.system("sudo pip install fastdtw")
	os.system("sudo pip install sklearn")
	os.system("sudo pip install scipy")
	os.system("sudo pip install matplotlib")
	os.system("sudo pip install pandas")

# Something else is up
else:
	print('Sorry, this operating system is not supported.')
	exit()
