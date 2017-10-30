# DTW using MotionML Library

## Use in CS 4605 Fistbump Project
This library performs pattern recognition via Dynamic Time Warping (DTW) using various Python algorithms in libraries. So the assumption is that Python is installed on the machine you are using. 

All credit goes to [this GitHub repository](https://github.com/llvll/motionml) for providing the base of the source code. It was added upon and expanded upon by our group.

Regarding setup, there are a few steps that need to be taken.

1) Data must be obtained from the raw data. To do this, the "DataCleanup.py" needs to be run on every file in the "raw_data" directory, specifying whether you want the data specified to be "train" or "test" data. This can be done with a simple bash script or Python script. In fact, I wrote one out. So, to do this all you have to do is execute the following command:

```sh
python ./navigate_dir.py
```

2) All of the necessary modules for the actual program can be installed via the "dependencies.py" file, and will setup on either Windows, Mac, or Linux operating systems. To set up the dependencies, open a Terminal in the directory with the source code and execute the following command:

```sh
python ./dependencies.py
```

3) Afterward, you can just run the program. This is done by doing the following command on Windows or Mac:

```sh
python ./motion_rec_demo.py
```

or the following command on Linux:

```sh
python3 ./motion_rec_demo.py
```

For more information about how this library works, consult the next section: "Motion pattern recognition using KNN-DTW and classifiers from TinyLearn".


## Motion pattern recognition using KNN-DTW and classifiers from TinyLearn

This is a domain-specific example of using TinyLearn module for recognizing (classifying) the motion patterns according to the supplied accelerometer data. 

The following motion patterns are included into this demo:

* Walking
* Sitting down on a chair
* Getting up from a bed
* Drinking a glass
* Descending stairs
* Combing hair
* Brushing teeth

The accelerometer data is based on the following public dataset from UCI: https://archive.ics.uci.edu/ml/datasets/Dataset+for+ADL+Recognition+with+Wrist-worn+Accelerometer

Dynamic Time Warping (DTW) and K-Nearest Neighbors (KNN) algorithms for machine learning are used
to demonstrate labeling of the varying-length sequences with accelerometer data. Such algorithms can be applied to time series classification or other cases, which require matching / training sequences with unequal lengths.

Scikit-Learn doesn't have any DTW implementations, so a custom class has been implemented (KnnDtwClassifier)
as a part of TinyLearn module.

DTW is slow by default, taking into account its quadratic complexity, that's why we're speeding up the classification
using an alternative approach with histograms and CommonClassifier from TinyLearn.

IPython Notebook (.ipynb file) is included for step-by-step execution of a demo application with extra comments. The code was modified for our particular project.

This code is using TinyLearn framework, which simplifies the classification tasks with Python and the following modules:

* Scikit-Learn
* Pandas
* OpenCV
* Other libraries, like FastDTW 

TinyLearn framework is still at an early development stage. Please use with caution and feel free to ask any questions: oleg.v.puzanov@gmail.com
