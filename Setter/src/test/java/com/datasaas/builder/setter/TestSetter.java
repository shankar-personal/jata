/**
 *
 */
package com.datasaas.builder.setter;

import junit.framework.Assert;

import org.junit.Test;
import org.shankar.builder.setter.InvoiceMutable;
import org.shankar.builder.setter.InvoiceMutableAlternate;


/**
 * @author snarayan
 *
 */
public class TestSetter {

	private static final double NEW_PRICE = 100.5;

	@Test
	public void trySetAlternate() {
		InvoiceMutableAlternate invoiceMutAlternate = new InvoiceMutableAlternate("Keyboard", 25.5);

		invoiceMutAlternate.setBegin().manufacturer("Acme").model("beta").price(NEW_PRICE).setEnd();

		double price = invoiceMutAlternate.getPrice();
		Assert.assertEquals(price, NEW_PRICE);

	}

	@Test
	public void trySet() {
		InvoiceMutable invoiceMut = new InvoiceMutable("Keyboard", 25.5);

		invoiceMut.setBegin().vendor("Acme").model("beta").price(NEW_PRICE).setEnd();

		double price = invoiceMut.getPrice();
		Assert.assertEquals(price, NEW_PRICE);

	}

}
