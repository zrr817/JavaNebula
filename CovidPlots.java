package com.climatedev.covid;

//Data visualization with Nebula plug ins

//Import packages
import org.eclipse.draw2d.*;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.File;
import com.climatedev.ndimageio.awt.Graphics;
import com.climatedev.ndimageio.awt.Graphics2D;
import com.climatedev.ndimageio.awt.image.BufferedImage;
import com.climatedev.ndimageio.imageio.ImageIO;

public class CovidPlots {
	
	public static void main(String[] args) {
		
		//Data source
		List<Book> stocks = readBooksFromCSV("COVID_DJI.csv");
		for (Book b : stocks) {
			System.out.println(b);
		}
		
		//Create arrays that can hold output
				double[] opening;
				double[] closing;
				String[] time;
				int len = stocks.size();
				opening = new double[len];
				closing = new double[len];
				time = new String[len];
				int j = 0;
				for (Book x : stocks) {
					double o_value = x.getOpen();
					double c_value = x.getClose();
					String d_value = x.getDate();
					opening[j] = o_value;
					closing[j] = c_value;
					time[j] = d_value;
					j++;
				}
				
				//Modify time array to have proper formatting
				//DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				//String[] formattedDate;
				//formattedDate = new String[len];
				//for (int i = 0; i < len; i++) {
					//LocalDateTime myDateObj = LocalDateTime.parse(time[i]);
					//formattedDate[i] = myDateObj.toString();
				//}
				
				//System.out.println("Dates: " + formattedDate);
				
				//Create temp x array
				double[] temp;
				temp = new double[len];
				for (int i = 0; i < len; i++) {
					temp[i] = i;
				}

		//Create shell window
		final Shell shell = new Shell();
		shell.setSize(300, 250);
		shell.open();
		
		//Define axis for plot
		
		//Create bridge between Lightweight system, SWT, and draw2d
		final LightweightSystem lws = new LightweightSystem(shell);
		XYGraph xyGraph = new XYGraph();
		xyGraph.setTitle("DJIA PPS During COVID-19 Outbreak");
		
		//Set Axis Bounds
		double min = opening[0];
		for(int i = 0; i < len; i++) {
			if(opening[i] < min) {
				min = opening[i];
			}
		}
		double max = opening[0];
		for(int i = 0; i < len; i++) {
			if(opening[i] > max) {
				max = opening[i];
			}
		}
		xyGraph.getPrimaryXAxis().setRange(0, len);
		xyGraph.getPrimaryYAxis().setRange(min-100,max+100);
		xyGraph.getPrimaryXAxis().setTitle("Day");
		xyGraph.getPrimaryYAxis().setTitle("Price Per Share");
		
		//Plot graph
		lws.setContents(xyGraph);
		
		//Create trace data providers
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(len);
		traceDataProvider.setCurrentXDataArray(temp);
		traceDataProvider.setCurrentYDataArray(opening);
		traceDataProvider2.setBufferSize(len);
		traceDataProvider2.setCurrentXDataArray(temp);
		traceDataProvider2.setCurrentYDataArray(closing);
		
		
		//Build graph
		Trace trace = new Trace("Opening DJI Values", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);
		Trace trace2 = new Trace("Closing DJI Values", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider2);
		trace.setPointStyle(PointStyle.CIRCLE);
		trace.setTraceColor(ColorConstants.red);
		trace2.setPointStyle(PointStyle.CIRCLE);
		trace2.setTraceColor(ColorConstants.darkGreen);
		trace2.setLineWidth(0);
		
		//Draw
		xyGraph.addTrace(trace);
		xyGraph.addTrace(trace2);
		
		//Keep shell open
		Display display = Display.getDefault();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		//Save image as jpeg
		try {
			BufferedImage image = new BufferedImage(300, 250, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2d = image.createGraphics();
			shell.getPaint(graphics2d);
			ImageIO.write(image, "jpg", new File("C:\\Users\\zrr81\\Downloads\\COVID-19_Analysis\\COVID-19_Chart.jpg"));
			}
		catch (Exception e) {
			System.out.println("Oops");
		}
		
	}
	
	private static List<Book> readBooksFromCSV(String fileName) {
		List<Book> stocks = new ArrayList<>();
		Path pathToFile = Paths.get("C:\\Users\\zrr81\\Downloads\\COVID-19_Analysis\\COVID_DJI.csv");
		
		//Try/catch IO exception for input file stream
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            //Initialize counter to skip header
			int count = 0;
			String line = br.readLine();
			while(line != null) {
				//Tell the reader to split the text when it hits the delimiter
				String[] attributes = line.split(",");
				Book book = createBook(attributes);
				stocks.add(book);
				count ++;
				line = br.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return stocks;
	}
	
	//Import data into array "Book"	
	private static Book createBook(String[] metadata) {
			String day = metadata[0];
			double opener = Double.parseDouble(metadata[1]);
			double closer = Double.parseDouble(metadata[4]);
			return new Book(day, opener, closer);
	}
}
