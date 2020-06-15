package org.shankar.builder.setter;

public class InvoiceMutable {

	public static class Setter {
		private String productName;

		private String model;

		private String serialNumber;

		private double price;

		private String vendor;

		private InvoiceMutable invoiceMut;

		public synchronized void setEnd() {

			invoiceMut.model = model;
			invoiceMut.serialNumber = serialNumber;
			invoiceMut.vendor = vendor;
			invoiceMut.price = price;
			invoiceMut.productName =productName;
		}

		private Setter self(InvoiceMutable invoiceMut) {
			this.invoiceMut = invoiceMut;
			return this;
		}

		public Setter name(String value) {
			this.productName = value;
			return this;
		}

		public Setter price(double value) {
			this.price = value;
			return this;
		}

		public Setter vendor(String value) {
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

	private Setter setter = new Setter();

	/**
	 * Creates an immutable invoice instance.
	 */
	public InvoiceMutable(String name, double price) {
		this.productName = name;
		this.price = price;
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
		setter.self(this);
		return setter;
	}
}
