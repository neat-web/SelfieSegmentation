# SelfieSegmentation
This project provides an example implementation of the ML Kit Selfie Segmentation API in an Android app.

## Installation
Just clone the repo, open the project in Android Studio and run the project.
For better performance run it on a physical device instead of AVD.

## Features
The user can choose a front and a background image from his library. Then the image is processed and three modes can be selected.

### Default mode
Displays the chosen front image.

<img src="/doc/default.png" width="300">

### Mask mode
Shows the front image and highlights the detected background of the segmentation mask as a green overlay.

<img src="/doc/mask.png" width="300">

### Custom background mode
Shows the front image and uses the detected segmentation mask to overlay the background image.

<img src="/doc/custom_bg.png" width="300">

## Limitations
Since processing of a large image can take quite some time a selected image is resized to the current screen width.
Furthermore, a background image is stretched to the same size as the current front image to simplify the overlay creation.
