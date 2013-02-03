#include "LaneFinder.h"
using namespace std;

void AnnotateLane(IplImage* imgColor, char *text, CvPoint pt1){

	// Line variables
	CvPoint pt2 = cvPoint(275,15);
	CvScalar yellow = CV_RGB(200,200,18);
	int thickness = 2;
	int connectivity = 8;

	// Circle variables
	int radius = 30;
	CvScalar blue = CV_RGB(0,160,160);

	// Text variables
	double hscale = 0.5;
	double vscale = 0.4;
	double shear = 0.2;
	int thickness2 = 1;
	int line_type = 1;

	CvFont font1;
	cvInitFont(&font1,CV_FONT_HERSHEY_DUPLEX,hscale,vscale,shear,thickness,line_type);

	// cvLine(imgColor,pt1,pt2,yellow,thickness,connectivity);
	// cvCircle(imgMid,pt2,radius,blue,thickness,connectivity);
	cvRectangle(imgColor,pt2,cvPoint(505,45),yellow,thickness,connectivity);
	cvPutText(imgColor,text,cvPoint((imgColor->width / 2)-20,35),&font1,blue);
	
}

/**
 * To demonstrate how the function for right lane boundary extraction is to be used
 */
int main(int args, char *argv[])
{
	
	// Check that the input file exists
	struct stat stFileInfo;
	int intStat = stat(argv[1],&stFileInfo);
	if (intStat != 0) {
		cout << "Error: Input file " << argv[1] << " could not be found\n";
		return(1);
	}

	IplImage* imgInTmp = cvLoadImage(argv[1], 0);//this is the image you want to process
	//displayImage(imgInTmp, "find right lane on this image, klick image to proceed");
	//you could use it as is, but here I downscale it, for faster processing
	CvRect myRoi = cvRect(0,334, imgInTmp->width, imgInTmp->height);
	cvSetImageROI(imgInTmp, myRoi);
	IplImage* imgIn = createImage(0, imgInTmp->width/2, (imgInTmp->height-334)/2);
	cvResize(imgInTmp, imgIn);
	cvReleaseImage(&imgInTmp);
	// displayImage(imgIn, "this is the downscaled image, klick image to proceed");

	//the function call to extractRightLane() can display an output image, if
	//the debug flag (showOutput) is set to true
	//if you set the debug flag to true, this image shows what was extracted
	IplImage* imgMid = createColorImage(imgIn->width, imgIn->height);
	IplImage* imgOut = createColorImage(imgIn->width, imgIn->height);

	//will contain the extracted lane boundaries
	vector<CvPoint> lineLeft; 		 
	vector<CvPoint> lineRight; 		 
	//the debug flag
	bool showOutput = true;

	//this object is needed for the lane extraction, initialize it with size of the image to be processed
	LaneFinder *myLaneFinder = new LaneFinder(imgIn->width, imgIn->height);
	//extractLine() expects initial search intervall where the line you are looking
	//for starts. This is a left boundary (horizontal (x-)image coordinate)  for the
	//bottom row in the image and a right boundary.

	int searchLimitLeft = 0;
	int searchLimitRight = imgIn->width / 2;
	//the last two integers in this function call, 70 and 10 specify "fiddle parameters", which 
	//can be adjusted according to your needs.
	//If the found line has a length of < 70 it is discarded.
	//Furthermore, sometimes it is not possible to find the real starting point of the line, instead
	//the line starts somewhere more up in the image. In this case it is automatically extrapolated
	//to go towards the image bottom. The extrapolation is done by computing a line equation to the part of the lane
	//close to the image bottom. For that two points are necessary, the starting point of the found line
	//and another point on the found line. It must be specified how many pixels difference this other
	//point should have, in this case I use 10.
	myLaneFinder->extractLine(imgIn, imgMid, lineLeft, searchLimitLeft, searchLimitRight, showOutput, 70, 10);
	cout<<"Sending data to bus...\n";
	if (myLaneFinder->publishCurve(lineLeft)==-1){
	  char *outfilename = "leftlane.txt";
	  cout<<"calling write Curve to file:" << outfilename << "\n";
	  myLaneFinder->writeCurve(lineLeft, outfilename);
	}

	char *mybuff = (char *) malloc(100);
	sprintf(mybuff,"Left Lane : %d pixels",lineLeft.size());
	AnnotateLane(imgMid,mybuff,cvPoint(250,60));
	free(mybuff);

	displayImage(imgMid, "the extracted left lane, klick image to proceed");
	searchLimitLeft = imgIn->width / 2;
	searchLimitRight = imgIn->width;

	//the last two integers in this function call, 70 and 10 specify "fiddle parameters", which 
	//can be adjusted according to your needs.
	//If the found line has a length of < 70 it is discarded.
	//Furthermore, sometimes it is not possible to find the real starting point of the line, instead
	//the line starts somewhere more up in the image. In this case it is automatically extrapolated
	//to go towards the image bottom. The extrapolation is done by computing a line equation to the part of the lane
	//close to the image bottom. For that two points are necessary, the starting point of the found line
	//and another point on the found line. It must be specified how many pixels difference this other
	//point should have, in this case I use 10.
	myLaneFinder->extractLine(imgIn, imgOut, lineRight, searchLimitLeft, searchLimitRight, showOutput, 70, 10);
	cout<<"The length of the detected curve is: "<<lineRight.size()<<" pixels. "<<endl;
	//you can write the extracted curve to a file
	//each row in this file is: x-coordinate y-coordinate
	//of a lane pixel

	cout<<"Sending data to bus...\n";
	if (myLaneFinder->publishCurve(lineRight)==-1){
	  char *outfilename = "rightlane.txt";
	  cout<<"calling write Curve to file:" << outfilename << "\n";
	  myLaneFinder->writeCurve(lineRight, outfilename);
	}

	mybuff = (char *) malloc(100);
	sprintf(mybuff,"Left %d Right : %d pixels",lineLeft.size(),lineRight.size());
	AnnotateLane(imgOut,mybuff,cvPoint(250,60));
	free(mybuff);

	displayImage(imgOut, "the extracted right lane, klick image to proceed");

	//delete the extracted lane 
	lineRight.clear();
	lineLeft.clear();

	//clean up
	delete myLaneFinder;
	cvReleaseImage(&imgIn);
	cvReleaseImage(&imgOut);
}

