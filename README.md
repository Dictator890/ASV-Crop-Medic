# Crop-Medic

This project aims at building an android application to detect crop diseases using Deep Learning and on device processing.The application scans the input image and 
the DL model processess the image and fetches the information of the detected disease from the database.The main aim is not to replace the experts but to add a helping
hand to them.The farmers or the user can scan the plant leaf by himself and can check which disease has affected the  plant.This is pretty useful as it reduces the loads
of recognizing disease and will greatly reduce the time span for disease recognition.This application also provides the recommended medicines and steps to handle the disease
which can act as a guide for solving the disease.

# Dataset
The dataset used for training the network is Plant Leaf Disease.This dataset was inconsistent and 1000 images oer label were extracted so as to keep the similar quantity of examples for each label.

# Neural Network
The Neural Network was developed using Tensorflow and Keras.A custom Siamese Network was developed with the input being of shape 224x224.The base network is MobileNetV2 without the top layers.Instead a Dense Layer has been added on top of it which generates a Tensor of shape [batch_size,144].Triplet Semi Hard Loss is used during the training of the model.The input images are normalized between 0 and 1.

The model input must be normalized between 0 and 1.It has an output embeddings of 144.After the output the L2 Norm is calculated and if it is less than 0.5 then it is identified as matched.

#  Database
## Firebase
On the back-end side we have used firebase to store the details of the crop disease which contains the image,precaution,symptom,embeddings,etc which are highlty useful for image processing.It also allowed us to cache the data on the device so the application can perform seamlessly even when there is no internet connection.

## Local Database
We have used local database to store the history of the crops scanned previously by the application and their time stamps.For the SQLite database interaction we have used the Room Persistence Library.The database only store the predicted label and the timestamp. The  viewmodel coroutine has been used for performing the database operations.

# Application
The Android application is the front end where the user can interact with the system.It is developed using Kotlin Language for the development and have used latest libraries.

## Camera Activity
We have used custom camera  screen as per our requirements using the latest CameraX library from Kotlin Jetpack,

##  File Chooser
In the file chooser activity we have called the system intents to start the system file chooser.It allows only image files to be selected such as jpeg and png.

## Plant Category chooser
The user must choose from which category the input image is so as to classify the data effectively.In future this will be replaced a
Neural Network.

## Processor and Result
In the processor the model memory will be allocated the image will be preprocessed and then inferred using the model. Everything happens with error checks.After the detection if any disesase is found the result is shown on the screen with all the details.

The code is very simple to read and a basic version of model is bundeled with the application.If any doubts please mail us.We will soon post the notebook for the 
model.
