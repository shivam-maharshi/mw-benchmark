import sys

args = sys.argv

in_file = ""
out_file = ""
lines = []

for arg in args:
    try:
        if (arg.strip().startswith("-in")):
            in_file = arg.strip().split("in=")[1]
        elif (arg.strip().startswith("-out")):
            out_file = arg.strip().split("out=")[1]
    except:
        print ("Error in command. Retry this command: python resource.py -in=/home/in.txt -out=/home/out.csv")

try:        
    if in_file and out_file:
        with open(in_file) as inf:
            lines = inf.readlines()
    else:
        print ("Error in command. Retry this command: python resource.py -in=/home/in.txt -out=/home/out.csv")
        sys.exit()
        
    outf = open(out_file, 'w')
    
    del lines[:2]
        
    for line in lines:
        r = line.split(" ")
        if r[10] is not "0":
            oline = r[10] + ',' + r[25] + ',' + r[26] + ',' + r[35] + ',' + r[36]
            outf.write(oline + '\n')
        
    outf.close();
except Exception as e:
    print ("Error in command. Retry this command: python resource.py -in=/home/in.txt -out=/home/out.csv")