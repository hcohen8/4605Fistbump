# Copyright (c) 2015, Oleg Puzanov
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright notice,
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

"""
Demo script for the motion patterns recognition based on the accelerometer data from UCI:
https://archive.ics.uci.edu/ml/datasets/Dataset+for+ADL+Recognition+with+Wrist-worn+Accelerometer

Dynamic Time Warping (DTW) and K-Nearest Neighbors (KNN) algorithms for machine learning are used
to demonstrate labeling of the varying-length sequences. It can be applied to time series classification or
other cases, which require matching / training sequences with unequal lengths.

Scikit-Learn doesn't have any DTW implementations, so a custom class has been implemented (KnnDtwClassifier)
as a part of TinyLearn module.

DTW is slow by default, taking into account its quadratic complexity, that's why we're speeding up the classification
using an alternative approach with histograms and CommonClassifier from TinyLearn.
"""

from tinylearn import KnnDtwClassifier
from tinylearn import CommonClassifier
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os
import warnings

warnings.filterwarnings("ignore")

train_labels = []
test_labels = []
train_data_raw = []
train_data_hist = []
test_data_raw = []
test_data_hist = []

# Utility function for normalizing numpy arrays
def normalize(v):
    norm = np.linalg.norm(v)
    if norm == 0:
        return v
    return v / norm

# Loading all data for training and testing from TXT files
def load_data():
    for d in os.listdir("data"):
        for f in os.listdir(os.path.join("data", d)):
            if f.startswith("TRAIN"):
                train_labels.append(d)
                tr = normalize(np.ravel(pd.read_csv(os.path.join("data", d, f),
                                                    delim_whitespace=True,
                                                    header=None)))
                train_data_raw.append(tr)
                train_data_hist.append(np.histogram(tr, bins=20)[0])
            else:
                test_labels.append(d)
                td = normalize(np.ravel(pd.read_csv(os.path.join("data", d, f),
                                                delim_whitespace=True,
                                                header=None)))
                test_data_raw.append(td)
                test_data_hist.append(np.histogram(td, bins=20)[0])

# Let's plot several selected histograms for the train data
def plot_histograms():
    title = train_labels[0]
    for i in range (0, len(train_data_raw)):
        if (train_labels[i] != title or i == len(train_data_raw) - 1):
            #plt.show()
            title = train_labels[i] 
        hist, bins = np.histogram(train_data_raw[i], bins=20)
        width = 0.7 * (bins[1] - bins[0])
        center = (bins[:-1] + bins[1:]) / 2
        plt.title(train_labels[i])
        plt.bar(center, hist, align='center', width=width)


# Demonstration of KnnDtwClassifier and CommonClassifier for motion pattern recognition
def demo_classifiers():
    # Raw sequence labeling with KnnDtwClassifier and KNN=1
    clf1 = KnnDtwClassifier(1)
    clf1.fit(train_data_raw, train_labels)
    
    dtw_pred = []
    dtw_actual = []
    for index, t in enumerate(test_data_raw):
        if (str(clf1.predict(t)[0]) == 'Fistbump'):
		print(str(clf1.predict(t)[0]))
	else:
		print('No Result')
        dtw_pred.append(str(test_labels[index]))
        dtw_actual.append(str(clf1.predict(t)[0]))
    labels = os.listdir("data")
    #print("Confusion matrix for KnnDtwClassifier: \n")
    #print("Labels: %s\n" % (str(labels)))
    #print(confusion_matrix(dtw_actual, dtw_pred, labels=labels))
    #print("\n")
    #print("===============================================================")

if __name__ == "__main__":
    load_data()
    plot_histograms()
    demo_classifiers()
