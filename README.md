


![Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release suitable for the public.](https://www.repostatus.org/badges/latest/wip.svg)
# CubeRD

CubeRD is a Rubik's Cube Solver and timer that implements opencv's template recognition and kociemba's algorithm for optimally solving the popular Rubik's Cube puzzle.  Users can take a picture of all the faces of the cube, and get a near-optimal step-by-step solution within seconds. Additionally, for  experienced speed cubers we have included a timer that contains all the features you would expect from a competitive timer: scrambles, averages, history of all solves, penalties such DNF/ +2, and much more. 


# Screenshots
![preview](https://user-images.githubusercontent.com/45882589/72864486-30d73480-3ca2-11ea-8f96-cc995ff4af79.png)
*This is currently a work in progress and the UI would suffer changes in the future.*

##  Tech/framework used
**Built with**
-   Android Studio
-   OpenCV

## Installation
*Apks would be released once we get to a stable version. The following is a tutorial of how to get the source code running on Android Studio*

Download project using:

    git clone https://github.com/BrianUribe6/CubeRD.git

Open Android Studio and then click on file > open, and navigate to the project's folder, select the folder and click OK. 

Dowload OpenCV 4.1.0 library through their official [link](https://sourceforge.net/projects/opencvlibrary/files/4.1.0/opencv-4.1.0-android-sdk.zip/download). 

 - Extract the contents of the zip.
 - In Android Studio click **file > project structure**.
 - Under the **modules** tab click on **+**.
 - Click on **Import graddle project**.
 - Navigate to the OpenCV folder and select the folder named **sdk** .
 - Change the module name to **OpenCvLibrary410** and hit finish.
## Credits
 - [min2phase](https://github.com/cs0x7f/min2phase) 
 - [opencv](https://opencv.org)
