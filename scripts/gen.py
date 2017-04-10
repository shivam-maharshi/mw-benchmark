import sys

out = "C:\Shivam\Work\WorkSpace\mw-benchmark\scripts\out.csv"

outf = open(out, 'w')

i = 25
while i <= 500:
    for j in range (1, 58):
        outf.write((str)(i) + '\n')
        j = j + 1
    i += 25
    
outf.close()