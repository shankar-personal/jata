package org.jata.sample.app;

import java.lang.annotation.Annotation;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.jata.read.JataReader;
import org.jata.write.sample.annot.SampleAnnot;

public class TestAnnotationReader extends TestCase {

	public void testJataRead() {

		String annotTemplateClassName = org.jata.write.sample.BoxWithLatch.class
				.getName();

		String versionString = "0.0.1";
		String projectName = "";
		List<Annotation> annotations = JataReader.readAnnotations(
				SampleAnnot.class, annotTemplateClassName, projectName, versionString,
				 null);
		Assert.assertTrue(annotations.size() == 2);
		SampleAnnot sampleAnnotationOne = (SampleAnnot)annotations.get(0);
		SampleAnnot sampleAnnotationTwo = (SampleAnnot)annotations.get(1);

		Assert.assertTrue(sampleAnnotationOne.id().equals("xyz")
				|| sampleAnnotationOne.id().equals("abc"));

		Assert.assertTrue(sampleAnnotationTwo.id().equals("xyz")
				|| sampleAnnotationTwo.id().equals("abc"));

		if (sampleAnnotationTwo.id().equals("xyz")) {
			Assert.assertTrue(sampleAnnotationOne.id().equals("abc"));
		}
		if (sampleAnnotationOne.id().equals("xyz")) {
			Assert.assertTrue(sampleAnnotationTwo.id().equals("abc"));
		}
	}

	public void testJataReadByUUID() {
		String annotTemplateClassName = org.jata.write.sample.BoxWithLatch.class
				.getName();

		String versionString = "0.0.1";
		String projectName = "";
		String uuidString = "a44fff90-c872-4a18-babd-9481db7c30b2";

		List<Annotation> annotations = JataReader.readAnnotations(
				SampleAnnot.class, annotTemplateClassName, projectName, versionString,
				 uuidString);
		Assert.assertTrue(annotations.size() == 1);
		SampleAnnot sampleAnnotationOne = (SampleAnnot)annotations.get(0);
		Assert.assertTrue(sampleAnnotationOne.id().equals("xyz"));

		String secoundUuidString = "09fa92cb-1d6b-4b46-ac3e-9cae2696dd03";
		List<Annotation> secondAnnotations = JataReader.readAnnotations(
				SampleAnnot.class, annotTemplateClassName, projectName, versionString,
			    secoundUuidString);
		Assert.assertTrue(secondAnnotations.size() == 1);
		SampleAnnot sampleAnnotationTwo = (SampleAnnot)secondAnnotations.get(0);
		Assert.assertTrue(sampleAnnotationTwo.id().equals("abc"));
	}
}
