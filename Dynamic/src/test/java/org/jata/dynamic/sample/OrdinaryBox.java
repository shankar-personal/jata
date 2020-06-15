package org.jata.dynamic.sample;

import java.util.Set;

/**
 * @author snarayan
 *
 */
public class OrdinaryBox {

	private int width;

	private int length;

	private int height;

	private Set<Integer> contentIds;

	public OrdinaryBox() {
		width = 10;

	}

	/**
	 * @param width
	 * @param length
	 * @param height
	 */
	public OrdinaryBox(int width, int length, int height) {
		this.width = width;
		this.length = length;
		this.height = height;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#setHeight(int)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#getLength()
	 */
	public int getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#setLength(int)
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Size#setWidth(int)
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.informatica.dynamic.sample.Shape#move(int, int, int)
	 */
	public void move(int toX, int toY, int toZ) {

	}

	public long getVolume(Long paramLength, Long paramWidth, Long paramHeight) {
		return paramLength * paramWidth * paramHeight;
	}

	public static long getStaticVolume(Long paramLength, Long paramWidth, Long paramHeight) {
		return paramLength * paramWidth * paramHeight;
	}

	public static long getStaticMax() {
		return 1000L;
	}

	public long getPrimitiveVolume(long paramLength, long paramWidth,
			long paramHeight) {
		return paramLength * paramWidth * paramHeight;
	}

	public Set<Integer> getContentIds() {
		return contentIds;
	}

	public void setContentIds(Set<Integer> contentIds) {
		this.contentIds = contentIds;
	}


	public Long getTopAreaOne(Long... argument) {
		assert argument.length == 2;
		return argument[0].longValue()*argument[1].longValue();
	}


	public Long getTopArea(Long arg1, Long... argument) {
		assert argument.length == 1;
		return arg1.longValue()*argument[0].longValue();
	}

	public Long getTopAreaTwo(Long... argument) {
		assert argument.length == 2;
		return argument[0].longValue()*argument[1].longValue();
	}


	public Long getTopAreaTwo(Long arg1, Long... argument) {
		assert argument.length == 1;
		return arg1.longValue()*argument[0].longValue();
	}

}
