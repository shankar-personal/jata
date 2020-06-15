package org.jata.write.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author snarayan
 *
 */
public class BoxWithMultiDimensionalNonPrimitiveArray implements Shape, Size, Persistable {


	private String name;

	private int width;
	private int length;

	private Latch latch;


	private int height;

	private Set<Integer> contentIds;


	private Person[][][] people;

	private Set sides;


	public BoxWithMultiDimensionalNonPrimitiveArray() {
		width = 10;

	}

	/**
	 * @param width
	 * @param length
	 * @param height
	 */
	public BoxWithMultiDimensionalNonPrimitiveArray(int width, int length, int height) {
		this.width = width;
		this.length = length;
		this.height = height;
	}


	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#setHeight(int)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#getLength()
	 */
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#setLength(int)
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Size#setWidth(int)
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Shape#move(int, int, int)
	 */
	public void move(int toX, int toY, int toZ) {

	}


	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Persistable#getPersistentIdentifier()
	 */
	public long getPersistentIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see com.informatica.dynamic.sample.Persistable#setPersistentIdentifier(long)
	 */
	public void setPersistentIdentifier(long id) {
		// TODO Auto-generated method stub

	}

	public long getVolume(Long paramLength, Long paramWidth, Long paramHeight) {
		return paramLength*paramWidth*paramHeight;
	}

	public long getPrimitiveVolume(long paramLength, long paramWidth, long paramHeight) {
		return paramLength*paramWidth*paramHeight;
	}

	public Set<Integer> getContentIds() {
		return contentIds;
	}

	public void setContentIds(Set<Integer> contentIds) {
		this.contentIds = contentIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Latch getLatch() {
		return latch;
	}

	public void setLatch(Latch latch) {
		this.latch = latch;
	}

	public Set getSides() {
		return sides;
	}

	public void setSides(Set sides) {
		this.sides = sides;
	}

	public Person[][][] getPeople() {
		return people;
	}

	public void setPeople(Person[][][] people) {
		this.people = people;
	}



}
