General Note:
=============
After creating the JAR files, I've imported the JAR files into Eclipse again. 
I've checked that there's no compilation error. I will lose 50% if that's not the case.
We used Eclipse (both windows & linux environment) to work on this assignment.  You will
need Eclipse to run the code.



Instructions:
=============
1) Open Eclipse

2) Go to File menu and select Create new Java Application

3) Give it a project name: geoip_service

4) Be sure to select JRE (environment 1.6 or later)

5) Goto the Package Explorer on the left panel and click on src

6) Right click and select Import

7) Once in the import window, expand the General folder and choose Archive File

8) click Browse, beside the textfield of 'From archive file:' and locate the the .jar file

9) Once you have it located, just select it and open

10) and click finish

11) In your src folder under Package Explorer, there should be two packages: 
	Provider.GoogleMapsStatic
	Provider.GoogleMapsStatic.TestUI

12) Open up the 2nd package Provider.GoogleMapsStatic.TestUI and you should see MySample.java file

13) Double click on MySample.java to open the file

14) Click on the Green run button on the top or just right click on the MySample.java file and 
    select run as 'Java Application'




Explore and Comprehend the Source Code.
===============================================
1. Explore and comprehend the following methods in the MapLookup class:

    getMap( )
    getURI( )
    getDataFromURI( ) 


## getMap() returns URI String to retrieve map image from Google.  
## There are 5 different constructors of getMap() with different parameters.
## The maximum size width & height is 512 pixels.
##  Zoom level ranges from 0 to 19.  The default was set to 14, but we changed it to 12 in our code.

1)
public static String getMap(double lat, double lon) {
  return getMap(lat, lon, SizeMax, SizeMax);
}

## This method takes a double value of lattitude and longitude, and returns a 
## string containg the latitude, longitude and Map size dimentions of width 
## and height (SizeMax, SizeMax). 

------

2)
public static String getMap(double lat, double lon, int sizeW, int sizeH) {
  return getMap(lat, lon, sizeW, sizeH, ZoomDefault);
}

## This method takes in a 2 double values - the latitude and longitude, and 2 
## integer values, the size width and size height for the border and returns a 
## string that also includes zoomDefault parameter

------

3)
public static String getMap(double lat, double lon, int sizeW, int sizeH, int zoom) {
  return _map.getURI(lat, lon, sizeW, sizeH, zoom);
}

## This method takes in two doubles (the lattitude and longitude), 
## and 3 integers (sizeW, sizeH, zoom) and returns a string object 
## from _map.getURI(lat, long, sizeW, sizeH, zoom) method

------

4)
public static String getMap(double lat, double lon, int sizeW, int sizeH, MapMarker... markers) {
  return _map.getURI(lat, lon, sizeW, sizeH, markers);
}

## This method takes in 5 parameters and returns the string
## it returns the String object from _map.getURI.

-------

5)
public static String getMap(double lat, double lon, MapMarker... markers) {
  return getMap(lat, lon, SizeMax, SizeMax, markers);
}

## takes in 3 parameters, 2 doubles (latitude and longitude), and MapMarker object)
## returns a string using getMap() method from #4 (above).

-------------------------------------------------------------------------------------------------------

## getURI() -- this method enables the API to get the universal Resource identifier that translates the 
## string object into an image, espeically google maps.  The method returns a String object type.
## a drop-in replacement for StringBuffer -- allows .append & .insert method

public String getURI(double lat, double lon, int sizeW, int sizeH, MapMarker... markers) {
  _validateParams(sizeW, sizeH, ZoomDefault);

 // generate the URI
  StringBuilder sb = new StringBuilder();	
  sb.append(GmapStaticURI);



  // size key
  sb.
      append("?").
      append(SizeKey).append("=").append(sizeW).append(SizeSeparator).append(sizeH);

  // markers key
  sb.
      append("&").
      append(MarkerUtils.toString(markers));

  // maps key
  sb.
      append("&").
      append(GmapLicenseKey).append("=").append(GmapLicense);


  return sb.toString();
}
---------------------------------------------------------------------------------------------------------

## getDataFromURI( ): To turn the URI into a GIF use the getDataFromURI(String) method
## The return type is a static Bytebuffer object.  I basically takes int he uri String
## from the getURI method (above) and retrieves the map using the get httpClient get method

/** use httpclient to get the data */
public static ByteBuffer getDataFromURI(String uri) throws IOException {

  GetMethod get = new GetMethod(uri);

  try {
    new HttpClient().executeMethod(get);
    return new ByteBuffer(get.getResponseBodyAsStream());
  }
  finally {
    get.releaseConnection();
  }

}

=====================================================================================================
=====================================================================================================

2. Explain how the StringBuffer object is used in the MapLookup class.

 StringBuilder sb = new StringBuilder();	
  sb.append(GmapStaticURI);

## this API did not use StringBuffer class, but the StringBuilder class
## According to my researching and findings, the StringBuilder can perform 
## large volumes of iteration at a faster pace than StringBuffer because
## there are no synchronization and thread-safety involved.
## StringBuilder is used to append immutable strings into one large string 
## object.  Strings are usually immutable, but with StringBuilder, strings
## can by pass security checks.

## In this API, the Stringbuilder object is used to append certain constants,
## such as the delimiter characters ", =, +, &" along with the URI string
## to retrieve the map image from google maps server


=====================================================================================================
=====================================================================================================

3. Explain how "static final" is used in the MapLookup class.

public static final String GmapStaticURI = "http://maps.google.com/staticmap";
public static final String GmapLicenseKey = "key";

## The common misconception about static final is that is used for global variables, 
## because it used to instantiate constant values, e.g. public static final double PI = 3.1415
## However in arrays, they can be modified, so not necessarily 100% immutable.
## A final variable can only be initilized once, using an assignment operator or initializer.
## Furthermore, final class cannot be extended or overriden; so their values cannot change once set
## In this API, static final is synonomous to constants, but they are used to minimize complexity
## and also to communicate their intent -- which is 'they're simple in behaviour'.

=====================================================================================================
=====================================================================================================

4. Explain how BufferedImage is used in SampleApp.java.
import java.awt.image.BufferedImage;
private BufferedImage _img;
        _img = ImageUtils.toCompatibleImage(ImageIO.read(data.getInputStream()));
        sout("converted downloaded data to image...");

 JLabel imgLbl = new JLabel(new ImageIcon(_img));

## According to docs.oracle.com, the public class bufferedImage is a sub class of java.awt.image
## which describes an image with an accessible buffer of image data.  It includes size, colour,
## coordinates (e.g. x=0, y=0 for initial pt of reference).  The Buffered Image is comprised of
## ColorModel and a Raster of image data.
## In this API, BufferedImage is used to download an image using the data.getInputStream() method 
## off of Google Maps server, given the URI parameters.

=====================================================================================================
=====================================================================================================

5. Explain how the Runnable interface is used here.

private void sout(final String s) {
  Runnable soutRunner = new Runnable() {

## The runnable interface is intended for any classes who are intended to be run as a thread. 
## Runnable interface DOES NOT extend to thread class.  The class must define a method called run().
## Since this API is not intended for multithread processing, the runnable interface is most suitable.
## The run() method is actually used to dump status info to the text area.

=====================================================================================================
=====================================================================================================

6. Explain the functionality of the following classes and how they are used.


    org.apache.commons.httpclient.HttpClient
    Task.Support.CoreSupport.ByteBuffer 


import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/** use httpclient to get the data */
public static ByteBuffer getDataFromURI(String uri) throws IOException {

  GetMethod get = new GetMethod(uri);

  try {
    new HttpClient().executeMethod(get);
    return new ByteBuffer(get.getResponseBodyAsStream());
  }
  finally {
    get.releaseConnection();
  }

}

## Sets up the http client such that the GTTP method can be executed. In this example, we see the API
## using the get method to retrieve the data from the URI string.



import Task.Support.CoreSupport.*;

## This package allows the user of ByteBuffer sub class to be used.  The ByteBuffer is basically a container
## for bytes of retrieved from the InputStream.  So the URI string and image codes are read into a byte buffer

=====================================================================================================
=====================================================================================================


7. Explore and comprehend the SampleApp class. 

## The SampleApp extends JFrame class contains a series of methods, anonymous inner classes to put everything we've
## discussed above to work, including the GUI operations of JButton, JCombo boxes, Listeners, and eventhandlers.
## There are a few stages that are executed before the application gets running.  
##    1) It initializes all necessary variables, objects, and parameters
##    2) It performs some value validation, to be sure they are the correct parameters and values
##		e.g. zoom values are only in the range from 0-19, maximum image size is 512pixels x 512 pixels
##    3) With the parameters provided, e.g. coordinates, size, zoom value, and default frame, the SampleApp class 
##		then uses the HttpClient to send off the URI string via the stringbuilder & stringbuffers.  
##		These strings consists of characters that eventually retrieves the image off of Google map server
##		using the data input stream 
##    4) The various listeners and event handlers respond to the user's actions, e.g. mouse_clicks, getMap, zoom, quit, etc.






















