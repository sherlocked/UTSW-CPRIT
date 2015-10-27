/**
 * Author: Harish Babu Arunachalam
 * Date: 10th August 2015
 * File Description: A Java class using Openslide-Java bindings to read aperio SVS image files. 
 * */
import java.io.*;

import org.openslide.OpenSlide;
import org.openslide.AssociatedImage;
import org.openslide.gui.OpenSlideView;
import java.util.Iterator;
import java.util.Map.*;
import java.util.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;


public class ReadSVS {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
			String folderPath = "/home/harish/Documents/Research/UTSW/dataset"; //new String("/home/harish/Documents/Research/UTSW/dataset/case6/");
			
			System.out.println("First program in OpenSlide.java");
			OpenSlide image = new OpenSlide(new File("/home/harish/Documents/Research/UTSW/dataset/Case3/Case 3 biopsy.svs"));
			System.out.println("Image level count is "+image.getLevelCount());
			
			Map<String,String> properties = image.getProperties();
			Iterator imageProperties = properties.entrySet().iterator();
			
			//Open a folder path using Show Dialog---------------
			
			//The folder contains the images for dataset----------
			JFileChooser jfc = new JFileChooser();
            jfc.setAcceptAllFileFilterUsed(false);
            jfc.setFileFilter(OpenSlide.getFileFilter());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = jfc.showDialog(null, "Open");
            if (result == JFileChooser.APPROVE_OPTION){
            
            	folderPath = jfc.getSelectedFile().getAbsolutePath();
            	System.out.println(jfc.getSelectedFile().getAbsolutePath());
            	
            }
          
			/*Commenting the below to use it later----------------*/ 
            
			//Iterate over the image properties map and print the values-------------*/
			for (Map.Entry<String, String> eachPair : properties.entrySet()){
				
				System.out.println(eachPair.getKey()+" : "+eachPair.getValue());
			}
			
			//Find all the Associated Images----------------------------
			Map<String,AssociatedImage> mapOfAssociatedImages = image.getAssociatedImages();
						
			System.out.println(" Number of images "+mapOfAssociatedImages.size());
			System.out.println(mapOfAssociatedImages.keySet());//.get("thumbnail"));
			
			//************Iterate over the SVS images in folderpath and save them as thumbnails 
//			ArrayList<File> svsFiles = new ArrayList();
			saveAsThumbnailImages(folderPath);
			
			//Create zoomable components from a given single SVS image
			JFrame jf = zoomableSlideView(image);
			
			return;
	}

	//Method to create a zoomableSlideView for each of the OpenSlide images passed to the function
	//Parameters: OpenSlide image
	//Return: JFrame object that has OpenslideView object embedded in the contentpane
		
	private static JFrame zoomableSlideView(OpenSlide image){
		
		//------------------Create a JFrame object and pass it to the openslide function for adding openSlideViewer object
		JFrame jf = new JFrame("OpenSlide");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//AssociatedImage assImage = image.getAssociatedImages();
		//openOne(new File("/home/harish/Documents/Research/UTSW/dataset/case6/B4.svs"),jf);
		
		OpenSlideView imageSlideView = new OpenSlideView(image,true);
		jf.getContentPane().add(imageSlideView);

		jf.setSize(1024, 1024);
        jf.setVisible(true);
        
        return jf;
		
	}
	
	//Method to extract thumbnail images out of each SVS image given in a folder path
	//Parameters: Folderpath of the location containing SVS images <dataset>
	//return: void
	private static void saveAsThumbnailImages(String folderPath) throws IOException{
		
		//Populate list of files from the directory
		ArrayList<File> svsFiles = openFiles(folderPath);
		for (int i=0;i<svsFiles.size();i++){
			
			File eachFile = svsFiles.get(i);
			System.out.println(eachFile.getName());
			
			//------------Create an SVS openImage file
			OpenSlide eachSVSImage = new OpenSlide(eachFile);
			eachSVSImage.getAssociatedImages().get("thumbnail");
			
			//Save a thumbnail image as a png image
           try{
        	   
        	  //Get AssociatedImages from the SVS layered file
            AssociatedImage thumbnailImage = eachSVSImage.getAssociatedImages().get("thumbnail");//mapOfAssociatedImages.get("thumbnail");
            BufferedImage thumbImageJpeg = thumbnailImage.toBufferedImage();
            File jpegFile = new File(eachFile.getName().replace("svs", "png"));
            
            //Write it into a png image
            ImageIO.write(thumbImageJpeg, "png", jpegFile);
            }
            catch(IOException e){
            	System.out.println("Inside exception catch");
            	e.printStackTrace();
            }
			
		}
  	
	}
	
	//Method to Load all files in a folder
	//Build an arrayList of files and return the object
	//Accepts a folder path as an input
	private static ArrayList<File> openFiles(String folderPath){
		
		
		File folder = new File(folderPath);
		int numberOfFiles = folder.listFiles().length;
		ArrayList <File> fileList = new ArrayList<File>(numberOfFiles);
		
		for (File files : folder.listFiles()){
			//System.out.println("File : "+files.getName());
			if (files.getName().contains(".svs")){
				fileList.add(files);
			}
		}
		return fileList;
		
	}
	
	/*Method initially copied to understand the openslide functionalities.
	 * 1. Use OpenSlide class to open a new SVS image or any other image of openSlide format
	 * 2. Use OpenSlideView class to add zooming in and zooming out functionality to the OpenSlide file.
	 * 3. Use JFrame to create a window and use ContentPane().add() to add the OpenSlideView object which will bring in the Zoom properties*
	 * */
    /*private static void openOne(File file, JFrame jf) throws IOException {
        OpenSlide os;
        os = new OpenSlide(file);
        final OpenSlideView wv = new OpenSlideView(os, true);
        
        //Set the name of the Window of the image
        wv.setBorder(BorderFactory.createTitledBorder(file.getName()));
        jf.getContentPane().add(wv);

        final JLabel l = new JLabel(" Aperio SVS file ");
        // System.out.println("properties:");
        // System.out.println(os.getProperties());

        jf.getContentPane().add(l, BorderLayout.SOUTH);
        
        //Add listeners for mouse operation
        wv.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                long x = wv.getSlideX(e.getX());
                long y = wv.getSlideY(e.getY());
                l.setText("(" + x + "," + y + ")");
                //System.out.println("Mouse Moved "+x+" , "+y);
            }
        });
        //Add listeners for mouse operation
        wv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                l.setText(" ");
            }
        });
        
        
        //Set properties for image's window frame - Window title, Window image icon, Minimize, maximize and close
        for (AssociatedImage img : os.getAssociatedImages()
                .values()) {
        	//AssociatedImage img = os.getAssociatedImages().get("thumbnail");
            JFrame j = new JFrame(img.getName());					//Create a new JFrame
            j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	//Set JFrame property
            j.add(new JLabel(new ImageIcon(img.toBufferedImage()))); //Set an image icon
            j.pack();
            j.setVisible(true);
        }
        
        //Fetch all properties of the SVS image
        Map<String, String> properties = os.getProperties();
        List<Object[]> propList = new ArrayList<Object[]>();
        SortedSet<Entry<String, String>> sorted = new TreeSet<Entry<String, String>>(
                new Comparator<Entry<String, String>>() {
                    @Override
                    public int compare(Entry<String, String> o1,
                            Entry<String, String> o2) {
                        String k1 = o1.getKey();
                        String k2 = o2.getKey();
                        return k1.compareTo(k2);
                    }
                });
        sorted.addAll(properties.entrySet());
        
        //Populate the properties in to a list object
        for (Entry<String, String> e : sorted) {
            propList.add(new Object[] { e.getKey(), e.getValue() });
        }
        
        //Add the contents to a JTable object
        JTable propTable = new JTable(propList
                .toArray(new Object[1][0]), new String[] { "key",
                "value" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JFrame propFrame = new JFrame("properties");
        propFrame.add(new JScrollPane(propTable));
        propFrame.pack();
        propFrame.setVisible(true);

        /*
         * JFrame listFrame = new JFrame("selections");
         * listFrame.add(new JScrollPane(new JList(wv
         * .getSelectionListModel()))); listFrame.pack();
         * listFrame.setVisible(true);
         
    }*/
}
