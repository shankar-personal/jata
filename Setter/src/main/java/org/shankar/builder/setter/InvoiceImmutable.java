package org.shankar.builder.setter;
public class InvoiceImmutable {
	  public static class Builder {
	   private String productName;
	   private String model;
	   private String serialNumber;
	   private double price;
	   private String vendor;

	   public Builder(String name, double price) {
	     this.productName = name;
	     this.price = price;
	   }

	   public InvoiceImmutable build() {
	     // any pre-creation validation here
	     InvoiceImmutable result = new InvoiceImmutable(productName, price);
	     result.model = model;
	     result.serialNumber = serialNumber;
	     result.vendor = vendor;
	     return result;
	   }

	   public Builder manufacturer(String value) {
	     this.vendor = value;
	     return this;
	   }

	   public Builder serialNumber(String value) {
	     this.serialNumber = value;
	     return this;
	   }

	   public Builder model(String value) {
	     this.model = value;
	     return this;
	   }
	  }

	 private String productName;
	 private String model;
	 private String serialNumber;
	 private double price;
	 private String vendor;


	 private InvoiceImmutable(String name, double price) {
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
	}
