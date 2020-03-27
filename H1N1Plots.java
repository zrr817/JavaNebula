package com.climatedev.covid;
//Data visualization with Nebula plug ins

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//Import packages
import org.eclipse.draw2d.*;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class H1N1Plots {
	
public static void main(String[] args) {
		
		//Data source
		List<Book> stocks = readBooksFromCSV("H1N1_DJI.csv");
		for (Book b : stocks) {
			System.out.println(b);
		}
		
		//Create arrays that can hold output
				double[] opening;
				double[] closing;
				int len = stocks.size();
				opening = new double[len];
				closing = new double[len];
				int j = 0;
				for (Book x : stocks) {
					double o_value = x.getOpen();
					double c_value = x.getClose();
					opening[j] = o_value;
					closing[j] = c_value;
					j++;
				}
				
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
		xyGraph.setTitle("DJIA PPS During H1N1 Outbreak");
		
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
		double h_drop = ((opening[0] - min)/opening[0]) * 100;
		System.out.println("Peak to trough drop: " + h_drop + "%");
		
		//Add axis parameters
		xyGraph.getPrimaryXAxis().setRange(0, len);
		xyGraph.getPrimaryYAxis().setRange(min-100,max+100);
		xyGraph.getPrimaryYAxis().setFormatPattern("$0000.00");
		xyGraph.getPrimaryXAxis().setDateEnabled(true);
	    xyGraph.getPrimaryXAxis().setTimeUnit(Calendar.DATE);
		xyGraph.getPrimaryXAxis().setFormatPattern("yyyy-MM-dd");
		xyGraph.getPrimaryXAxis().setMajorGridStep(7);
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
		
	}
	
	private static List<Book> readBooksFromCSV(String fileName) {
		List<Book> stocks = new ArrayList<>();
		Path pathToFile = Paths.get("C:\\Users\\zrr81\\Downloads\\COVID-19_Analysis\\H1N1_DJI.csv");
		
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
