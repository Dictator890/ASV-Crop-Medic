package com.example.CropMedic.LocalData

import com.example.CropMedic.R

class LocalConstantData {

	fun get_plantcategories():HashMap<String,Int>{
		val plantcategories=HashMap<String,Int>()
		plantcategories["Apple"] = R.drawable.ic_apple
		plantcategories["Cherry"] = R.drawable.ic_cherry
		plantcategories["Corn"] = R.drawable.ic_corn
		plantcategories["Grape"] = R.drawable.ic_grapes
		plantcategories["Peach"] = R.drawable.ic_peach
		plantcategories["PepperBell"] = R.drawable.ic_capsicum
		plantcategories["Potato"] = R.drawable.ic_potato
		plantcategories["Strawberry"] = R.drawable.ic_strawberry
		plantcategories["Tomato"] = R.drawable.ic_tomato
		return  plantcategories
	}

	fun gethowitwork_data():ArrayList<String>{
		val data=ArrayList<String>()
		data.add("Choose the input to the application.Click on the left green button to open the camera and click the picture or click on the right green button to choose the saved image from the storage of your phone.")
		data.add("If you choose the camera then click the picture of the plant leaf in bright light such that the leaf is neither in the shadow and neither in very bright light.")
		data.add("If you have chosen then file chooser go to the folder where the image is and if you have clicked it from the phone camera then click on the left menu and go to images and choose from there.")
		data.add("Choose the plant which is in the image so that the application knows what is to be tested.Make sure to choose it correctly")
		data.add("Now leave everything on us.The application will process the image and tell if your precious plant has any disease.If it is detected then it will tell you the details about handling of the disease or else if the plant is disease free according to our application then it will say nothing has found.")
		return data
	}

	fun getFAQ():HashMap<String,String>{
		val map=HashMap<String,String>();
		map["What is CropMedic?"]="CropMedic is an AI powered application that can detect any infectious disease to the crop by just looking at an image of it.It is highly accurate and can quickly detect the disease on your mobile.Moreover once the application has loaded all data on first start you can even use it without an internet connection.It will just require a connection after some time. "
		map["How frequently is an internet connection required?"]="Maximum of 2 months can be the interval between two internet connection to the application.Even a low speed connection is enough as it downloads everything by itself and the downloads are very small."
		map["Are there any conditions on image given to application?"]="You need the image of the leaf of the crop and make sure it is in enough brightness."
		map["How long does it take to get the results?"]="It usually takes only about 1-3 seconds to process the image and get the results."
		map["Can I completely rely only on these results?"]="This application is developed as a fast solution for disease detection but is not an alternative to experts.Also the information given as result is highly accurate which can act as a guide to solve your disease problem.For medicines please take a second opinion once."
		return map
	}
}