package org.shankar.il.builder.caller;

public class InvoiceLiterate {

	public static class Caller {
		private String productName;

		private String model;

		private String serialNumber;

		private double price;

		private String vendor;

		private InvoiceLiterate invoiceMut;

		public synchronized void callEnd() {

			invoiceMut.model = model;
			invoiceMut.serialNumber = serialNumber;
			invoiceMut.vendor = vendor;
			invoiceMut.price = price;
			invoiceMut.productName =productName;
		}

		private Caller self(InvoiceLiterate invoiceMut) {
			this.invoiceMut = invoiceMut;
			return this;
		}


		public Caller name(String value) {
			this.productName = value;
			return this;
		}

		public Caller price(double value) {
			this.price = value;
			return this;
		}

		public Caller andPriceTo(double value) {
			this.price = value;
			return this;
		}

		public Caller changeVendorTo(String value) {
			this.vendor = value;
			return this;
		}

		public Caller vendor(String value) {
			this.vendor = value;
			return this;
		}

		public Caller serialNumber(String value) {
			this.serialNumber = value;
			return this;
		}

		public Caller model(String value) {
			this.model = value;
			return this;
		}
	}

	private String productName;

	private String model;

	private String serialNumber;

	private double price;

	private String vendor;

	private Caller setter = new Caller();

	/**
	 * Creates an immutable invoice instance.
	 */
	public InvoiceLiterate(String name, double price) {
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

	public Caller callBegin() {
		setter.self(this);
		return setter;
	}
}
