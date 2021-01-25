![# Are you wearing Face Mask? Let's detect using HUAWEI Face Detection ML Kit and AI engine MindSpore](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/image1.png)

 

## Introduction


In this demo, we will show how to integrate Huawei ML Kit (Face Detection) and powerful AI engine MindSpore Lite in an android application to detect in real-time either the users are wearing masks or not. Due to Covid-19, the face mask is mandatory in many parts of the world. Considering this fact, the use case has been created with an option to remind the users with audio commands.

  
## Pre-Requisites

  

Before getting started, we need to train our model and generate .ms file. For that, I used  **HMS Toolkit plugin**  of Android Studio. If you are migrating from Tensorflow, you can convert your model from .tflite to .ms using the same plugin.

  

The dataset used for this article is from  **Kaggle** (link is provided in the references). It provided 5000 images for both cases. It also provided some testing and validation images to test our model after being trained.


  ## Development

1. Camera View to get frames
2. Convert each frame into MLFrame and pass it to ML Kit Face detection service to identify faces.
3. Once we get the face, pass the cropped bitmap to MindSpore trained model to determine either the face is with or without Mask.
4. Based on our logical accuracy percentage, we will draw red and green boxes as an overlay on top of camera view.
5. There is also an option to turn on or off sound commends to remind users to wear masks if they aren't.


Building smart solutions with AI capabilities is much easy with HUAWEI mobile services (HMS) ML Kit and AI engine MindSpore Lite. Considering different situations, the use cases can be developed for all industries including but not limited to transportation, manufacturing, agriculture and construction.

Having said that, we used Face Detection ML Kit and AI engine MindSpore to develop Face Mask detection feature. The on-device open capabiltiies of HMS provided us highly efficient and optimized results. Individual or Multiple users without Mask can be detected from far in realtime. This is applicable to be used in public places, offices, malls or at any entrance.

  

## Run the Application

  

Download this repo code. Build the project, run the application and test on any Huawei phone. In this demo, We used Huawei Mate30 for testing purposes.



![Loading Animation](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R5.gif)


![Help Bottom Sheet](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R6.gif)


![With Mask](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R1.jpg)


![Without Mask](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R2.jpg)


![Multiple faces](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R3.jpg)


![Real-time detection](https://github.com/yasirtahir/DetectFaceMask/raw/main/images/R4.gif)

## Point to Ponder

1.  Make sure to add all the permissions like _**WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, ACCESS_NETWORK_STATE and ACCESS_WIFI_STATE**_.  
    
2.  Make sure to add aaptOptions in the app-level build.gradle file aftering adding .ms and labels.txt files in the assets folder. If you miss this, you might get  **Load model failed**.  
    
3.  Always use animation libraries like Lottie to enhance UI/UX in your application. We also used  **OwlBottomSheet** for the help bottom sheet.
    
4.  The performance of **model is directly propotional to the number of training inputs**. Higher the number of inputs, higher will be accuracy to yield better results. In our article, we used 5000 images for each case. You can add as many as possible to improve the accuracy.
    
5.  MindSpore Lite provides output as callback. Make sure to design your use case while considering this fact.
    
6.  If you have Tensorflow Lite Model file (.tflite), you can convert it to .ms using the HMS Toolkit plugin.
    
7.  HMS Toolkit plugin is very powerful. It supports converting MindSpore Lite and HiAI models. MindSpore Lite supports TensorFlow Lite and Caffe and HiAI supports TensorFlow, Caffe, CoreML, PaddlePaddle, ONNX, MxNet and Keras.
    
8.  If you want to use Tensorflow with HMS ML Kit, you can also implement that. I have created another demo where I put the processing engine as dynamic. You can check the link in the references section.
    


## References


### HUAWEI ML Kit (Face Detection) Official Documentation:  

[https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/face-detection-0000001050038170-V5](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/face-detection-0000001050038170-V5)

  

### HUAWEI HMS Toolkit AI Create Official Documentation:  

[https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/ai-create-0000001055252424](https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/ai-create-0000001055252424)

  

### HUAWEI Model Integration Official Documentation:  

[https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/model-integration-0000001054933838](https://developer.huawei.com/consumer/en/doc/development/Tools-Guides/model-integration-0000001054933838)  

  

### MindSpore Lite Documentation:  

https://www.mindspore.cn/tutorial/lite/en/r1.1/index.html  

  

### MindSpore Lite Code Repo:  

https://gitee.com/mindspore/mindspore/tree/master/model_zoo/official/lite/image_classification  

  

### Kaggle Dataset Link:  

https://www.kaggle.com/ashishjangra27/face-mask-12k-images-dataset  

  

### Lottie Android Documentation:  

http://airbnb.io/lottie/#/android

### Tensorflow as a processor with HMS ML Kit:
[https://github.com/yasirtahir/HuaweiCodelabsJava/tree/main/HuaweiCodelabs/app/src/main/java/com/yasir/huaweicodelabs/fragments/mlkit/facemask/tensorflow](https://github.com/yasirtahir/HuaweiCodelabsJava/tree/main/HuaweiCodelabs/app/src/main/java/com/yasir/huaweicodelabs/fragments/mlkit/facemask/tensorflow)

