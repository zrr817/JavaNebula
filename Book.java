package com.climatedev.covid;

class Book {
	//Define spreadsheet "Book"
		private String date;
		private double opener;
		private double closer;	
		
		//Encapsulation constructor
		public Book(String date, double opener, double closer) {
			this.date = date;
			this.opener = opener;
			this.closer = closer;
		}
		
		//Getter and setter methods methods
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		
		public double getOpen() {
			return opener;
		}
		public void setOpen(double opener) {
			this.opener = opener;
		}
		
		public double getClose() {
			return closer;
		}
		public void setClose(double closer) {
			this.closer = closer;
		}
		
		@Override
		//Output results to string
		public String toString() {
			return "Date: " + date + " Opening price: " + opener + " Closing price: " + closer;
		}
}
