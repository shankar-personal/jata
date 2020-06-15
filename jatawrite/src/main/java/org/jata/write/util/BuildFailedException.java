package org.jata.write.util;

import java.util.List;

/**
 * @author snarayan
 *
 */
public class BuildFailedException extends Exception {

	List<Throwable> internalExceptions = null;

	public BuildFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BuildFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public BuildFailedException(String arg0, List<Throwable> arg1) {
		super(arg0, arg1.get(0));
		this.internalExceptions = arg1;
		// TODO Auto-generated constructor stub
	}

	public BuildFailedException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BuildFailedException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public List<Throwable> getInternalExceptions() {
		return internalExceptions;
	}

}
