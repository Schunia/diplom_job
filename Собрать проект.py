import os
path = os.getcwd()
cmd = 'cd /D ' + path
print(cmd)
os.system(cmd)
os.system('mvnw package')
