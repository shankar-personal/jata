package org.shankar.builder.setter;

public class InvoiceMutableAlternate {
	public static class Setter {
		private String productName;

		private String model;

		private String serialNumber;

		private double price;

		private String vendor;

		private InvoiceMutableAlternate invoiceMutable;

		public Setter(InvoiceMutableAlternate invoiceMut) {
			this.invoiceMutable = invoiceMut;
		}

		public synchronized void setEnd() {

			invoiceMutable.model = model;
			invoiceMutable.serialNumber = serialNumber;
			invoiceMutable.vendor = vendor;
			invoiceMutable.price = price;
			invoiceMutable.productName =productName;
		}

		public Setter name(String value) {
			this.productName = value;
			return this;
		}

		public Setter price(double value) {
			this.price = value;
			return this;
		}

		public Setter manufacturer(String value) {
			this.vendor = value;
			return this;
		}

		public Setter serialNumber(String value) {
			this.serialNumber = value;
			return this;
		}

		public Setter model(String value) {
			this.model = value;
			return this;
		}
	}

	private String productName;

	private String model;

	private String serialNumber;

	private double price;

	private String vendor;

	private Setter setter;


	public InvoiceMutableAlternate(String name, double price) {
		this.productName = name;
		this.price = price;

		// this is a thisEscape as described in the book Java Concurrency In Practice.
		setter = new Setter(this);
	}


	public String getVendor() {
		return vendor;
	}

	public String getModel() {
		return model;
	}

	public String getProductName() {
		return productName;
	}

	public double getPrice() {
		return price;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public Setter setBegin() {
		return setter;
	}
}
