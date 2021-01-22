import os
import numpy as np
from PIL import Image

def ctrl_lim(num):
    #控制输入的数范围在0~255
    if num < 0:
        return 0
    if num > 255:
        return 255
    return num

def convolve(raw_matrix, new_matrix, kernel): 
    kernel_height = kernel.shape[0]
    kernel_width = kernel.shape[1]
    picture_height = raw_matrix.shape[0]
    picture_width = raw_matrix.shape[1]
    r = int((kernel.shape[0] - 1) / 2)
    for j in range(picture_height - kernel_height + 1):
        if j % 20 == 0:
            print("正在执行第" + str(j) + "行像素点")
        for i in range(picture_width - kernel_width + 1):
            raw_color = raw_matrix[j + r][i + r]
            #卷积
            max_color = 0
            for m in range(kernel_height):
                for n in range(kernel_width):
                    if raw_matrix[j + m][i + n] > max_color:
                        max_color = raw_matrix[j + m][i + n]
            #反色
            max_color = ctrl_lim(255 - max_color)
            #颜色减淡
            if raw_color == 255:
                new_color = 255
            elif max_color == 0:
                new_color = raw_color
            else :
                new_color = raw_color / (1 - max_color / 255)

            
            new_matrix[j][i] = ctrl_lim(new_color)

raw_pic_path = os.path.abspath('洛天依.bmp')
raw_pic = Image.open(raw_pic_path).convert('L')

width, height = raw_pic.size
new_pic = Image.new('L', (width - 2, height - 2), 255)

raw_matrix = np.array(raw_pic).reshape((height, width))
new_matrix = np.array(new_pic).reshape((height - 2, width - 2))
print(raw_matrix.shape)

kernel = np.zeros((5,5))

print("开始")
convolve(raw_matrix, new_matrix, kernel)
print("完毕")
new_im = Image.fromarray(new_matrix, 'L')
new_im.show()
new_im.save(os.path.abspath('result.bmp'))
