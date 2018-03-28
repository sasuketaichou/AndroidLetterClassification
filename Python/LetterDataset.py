from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import numpy as np
from sklearn.model_selection import train_test_split
from PIL import Image
import os
import sys
import tarfile
from six.moves import urllib

class LetterDataset:
    def __init__(self):
        
        #download
        def maybe_download_and_extract():

            destination_dir = 'dataset'
            URL = ' http://yaroslavvb.com/upload/notMNIST/notMNIST_small.tar.gz'

            if not os.path.exists(destination_dir):
                os.makedirs(destination_dir)

            filename = URL.split('/')[-1]
            filepath = os.path.join(destination_dir, filename)
        
            if not os.path.exists(filepath):
                def _progress(count, block_size, total_size):
                    sys.stdout.write('\r>> Downloading %s %.1f%%' % (filename,float(count * block_size) / float(total_size) * 100.0))
                    sys.stdout.flush()
                    
                filepath, _ = urllib.request.urlretrieve(URL, filepath, _progress)
                print()
                statinfo = os.stat(filepath)
                print('Successfully downloaded', filename, statinfo.st_size, 'bytes.')
            
            extracted_dir = os.path.join(destination_dir, 'notMNIST_small')
            
            if not os.path.exists(extracted_dir):
                tarfile.open(filepath, 'r:gz').extractall(destination_dir)

        #trigger
        maybe_download_and_extract()       
        images, labels = [], []

        for i, letter in enumerate(['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']):
            directory = 'dataset/notMNIST_small/%s/' % letter
            files = os.listdir(directory)
            label = np.array([0]*10)
            label[i] = 1
            for file in files:
                try:
                    im = Image.open(directory+file)
                except:
                    print ("Skip a corrupted file: " + file)
                    continue
                pixels = np.array(im.convert('L').getdata())
                images.append(pixels/255.0)
                labels.append(label)
          
        train_images, test_images, train_labels, test_labels = \
            train_test_split(images, labels, test_size=0.2, random_state=0)
        
        class train:
            def __init__(self):
                self.images = []
                self.labels = []
                self.batch_counter = 0
                
            def next_batch(self, num):
                if self.batch_counter + num >= len(self.labels):
                    batch_images = self.images[self.batch_counter:]
                    batch_labels = self.labels[self.batch_counter:]
                    left = num - len(batch_labels)
                    batch_images.extend(self.images[:left])
                    batch_labels.extend(self.labels[:left])
                    self.batch_counter = left
                else:
                    batch_images = self.images[self.batch_counter:self.batch_counter+num]
                    batch_labels = self.labels[self.batch_counter:self.batch_counter+num]                  
                    self.batch_counter += num
                    
                return (batch_images, batch_labels)
                    
        class test:
            def __init__(self):
                self.images = []
                self.labels = []
                
        self.train = train()
        self.test = test()
                
        self.train.images = train_images
        self.train.labels = train_labels
        self.test.images = test_images
        self.test.labels = test_labels
