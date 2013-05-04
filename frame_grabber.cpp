/*
 *
 */
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <vector>
#include <stdio.h>

using namespace cv;
using namespace std;

//hide the local functions in an anon namespace
namespace
{
void help(char** av)
    {
        cout << "\nThis program just gets you frames from video\n"
        "Usage:\n./" << av[0] << " <video device number>\n" << "q,Q,esc -- quit\n"
        << "space   -- save frame\n\n"
        << "\tTo find the video device number, try ls /dev/video* \n"
        << "\tYou may also pass a video file, like my_vide.avi instead of a device number"
        << "\n"
        << endl;
    }

	int process(VideoCapture& capture)
    {
        int n = 0;
        char filename[200];
        string window_name = "video | q or esc to quit";
        cout << "press space to save a picture. q or esc to quit" << endl;
        namedWindow(window_name, CV_WINDOW_KEEPRATIO); //resizable window;
        Mat frame;
        for (;;)
        {
            capture >> frame;
            if (frame.empty())
                break;

        }
        return 0;
    }

}

int main(int ac, char** av)
{

    if (ac != 2)
    {
        help(av);
        return 1;
    }
    std::string arg = av[1];
    VideoCapture capture(arg); //try to open string, this will attempt to open it as a video file
    if (!capture.isOpened()) //if this fails, try to open as a video camera, through the use of an integer param
        capture.open(atoi(arg.c_str()));
    if (!capture.isOpened())
    {
        cerr << "Failed to open a video device or video file!\n" << endl;
        help(av);
        return 1;
    }
    return process(capture);
}
