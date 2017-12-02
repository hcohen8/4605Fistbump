import os

f = open('./test.txt', 'r+')
arr = f.readlines()
i = 0
while (i < len(arr)):
	arr[i] = arr[i].replace('\n', '')
	i += 1
print('\\n'.join(arr))
f.close()
exit()

