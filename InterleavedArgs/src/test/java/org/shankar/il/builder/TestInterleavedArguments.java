/**
 *
 */
package org.shankar.il.builder;

import junit.framework.Assert;

import org.junit.Test;
import org.shankar.il.builder.caller.InvoiceLiterate;



/**
 * @author snarayan
 *
 */
public class TestInterleavedArguments {

	private static final double NEW_PRICE = 100.5;


	/**
	 * The idea here is to provide a way to use arguments interleaved in the method name.
	 * Ideally, the following call:
	 * <em>
	 * invoiceLiterate.callBegin().changeVendorTo("Acme").andPriceTo(NEW_PRICE).callEnd();
	 * </em>
	 * would actually be:
	 *  <em>
	 * invoiceLiterate.changeVendorTo("Acme")andPriceTo(NEW_PRICE);
	 * </em>
	 *
	 * By interleaving arguments with the method name, the readability is dramatically improved.
	 * The method call reads more like a proper english sentence.
	 *
	 * Unfortunately, to support interleaved arguments you need to change the language spec. Sadly,
	 * I am not that powerful.
	 *
	 * This is a poor man's realization of interleaved arguments. It has some redundant code that
	 * diminishes readability. It is realized using a variation of the Builder pattern introduced
	 * by Joshua Bloch.
	 *
	 */
	@Test
	public void tryCall() {
		InvoiceLiterate invoiceLiterate = new InvoiceLiterate("Keyboard", 25.5);

		invoiceLiterate.callBegin().changeVendorTo("Acme").andPriceTo(NEW_PRICE).callEnd();
		double price = invoiceLiterate.getPrice();
		Assert.assertEquals(price, NEW_PRICE);

	}

}
