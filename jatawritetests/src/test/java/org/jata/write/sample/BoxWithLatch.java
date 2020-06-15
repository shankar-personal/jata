package org.jata.write.sample;

/**
 * @author snarayan
 *
 */
public class BoxWithLatch  {

	private int width;
	private int length;
	private int height;
	private Latch latch;

	/**
	 * @return the latch
	 */
	public Latch getLatch() {
		return latch;
	}



	/**
	 * @param latch the latch to set
	 */
	public void setLatch(Latch latch) {
		this.latch = latch;
	}



	public BoxWithLatch() {
		super();
	}



	public BoxWithLatch(int width, int length, int height) {
		this.width = width;
		this.length = length;
		this.height = height;
	}



	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getLength() {
		return length;
	}


	public void setLength(int length) {
		this.length = length;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}

}
