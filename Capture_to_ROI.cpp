/*
 * Capture_to_ROI.cpp
 *
 *  Created on: Mar 5, 2013
 *      Author: ubuntu
 */

#include <cv.h>
#include <highgui.h>
int main(int argc, char** argv)
{
IplImage* src;
IplImage* tmp;
int x = 230;
int y = 400;
int width = 400;
int height = 100;
if( (src=cvLoadImage(argv[1],1) ) != 0 )
{
	//int add = atoi(argv[6]);
	cvSetImageROI(src, cvRect(x,y,width,height));
	tmp = cvCreateImage(cvGetSize(src),src->depth,src->nChannels);
	cvCopy(src,tmp,NULL);
	cvResetImageROI(src);
	src = cvCloneImage(tmp);
	printf("Original dimensions after crop: %dx%d\n", tmp->width, tmp->height);

	//cvAddS(src, cvScalar(add),src);
	cvResetImageROI(src);
	cvNamedWindow("Roi_Add", 1 );
	cvShowImage( "Roi_Add", src );
	cvWaitKey();
}
return 0;
}
