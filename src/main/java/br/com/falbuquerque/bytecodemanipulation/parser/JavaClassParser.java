package br.com.falbuquerque.bytecodemanipulation.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

/**
 * Generates JavaClass classes from the given file.
 * 
 * @author Felipe Albuquerque
 */
public class JavaClassParser {

	private final String fileName;
	private List<JavaClass> javaClasses;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            the file from wich the JavaClasses will be generated
	 */
	public JavaClassParser(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Generates the JavaClass classes from the file.
	 * 
	 * @return the JavaClass classes created
	 * @throws IOException
	 *             if an I/O error has occurred
	 */
	@SuppressWarnings("resource")
	public List<JavaClass> generateJavaClasses() throws IOException {

		if (javaClasses == null) {
			final List<InputStream> inputStreams = new LinkedList<>();
			final List<String> classesNames = new LinkedList<>();

			if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
				final ZipFile file;

				if (fileName.endsWith(".jar")) {
					file = new JarFile(fileName);
				} else {
					file = new ZipFile(fileName);
				}

				final Enumeration<? extends ZipEntry> entries = file.entries();

				while (entries.hasMoreElements()) {
					final ZipEntry entry = entries.nextElement();
					final String className = entry.getName();

					if (className.endsWith(".class")) {
						inputStreams.add(file.getInputStream(entry));
						classesNames.add(className);
					}

				}

			} else {

				if (!fileName.endsWith(".class")) {
					throw new IllegalArgumentException("The file extension must be .class, .jar or .zip");
				}

			}

			if (!inputStreams.isEmpty()) {
				int posClass = 0;
				javaClasses = new ArrayList<>();

				for (final InputStream inputStream : inputStreams) {
					javaClasses.add(new ClassParser(inputStream, classesNames.get(posClass)).parse());
					posClass++;
				}

			} else {
				javaClasses = Collections.singletonList(new ClassParser(fileName).parse());
			}

		}

		return javaClasses;
	}

	/**
	 * Finds a java class by its full name.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return the class found, or <code>null</code> if any class was found
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 * @throws ClassNotFoundException
	 *             when the class specified can't be found
	 */
	public JavaClass getJavaClass(final String className) throws IOException, ClassNotFoundException {
		generateJavaClasses();

		JavaClass parameterClass = null;

		for (final JavaClass javaClass : javaClasses) {

			if (javaClass.getClassName().equals(className)) {
				parameterClass = javaClass;
				break;
			}

		}

		if (parameterClass == null) {
			throw new ClassNotFoundException();
		}

		return parameterClass;
	}

	/**
	 * Finds a class gen by the full name of its class.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return the class gen found, or <code>null</code> if any class was found
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 * @throws ClassNotFoundException
	 *             when the class specified can't be found
	 */
	public ClassGen getClassGen(final String className) throws IOException, ClassNotFoundException {
		return new ClassGen(getJavaClass(className));
	}

	/**
	 * Gets the methods from the class with the given name.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return the methods of the class
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 * @throws ClassNotFoundException
	 *             when the class specified can't be found
	 */
	public List<Method> getMethods(final String className) throws IOException, ClassNotFoundException {
		return Arrays.asList(getJavaClass(className).getMethods());
	}

	/**
	 * Gets the methods from all classes in the file.
	 * 
	 * @return the methods of all classes
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 */
	public List<Method> getMethods() throws IOException {
		generateJavaClasses();

		final List<Method> methods = new LinkedList<>();

		for (final JavaClass javaClass : javaClasses) {

			try {
				methods.addAll(getMethods(javaClass.getClassName()));
			} catch (final ClassNotFoundException classNotFoundException) {
				throw new UnexpectedException(classNotFoundException);
			}

		}

		return methods;
	}

	/**
	 * Gets the method gens from the class with the given name.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return the method gens of the class
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 * @throws ClassNotFoundException
	 *             when the class specified can't be found
	 */
	public List<MethodGen> getMethodGens(final String className) throws IOException, ClassNotFoundException {
		final JavaClass parameterClass = getJavaClass(className);
		final List<Method> methods = getMethods(className);
		final List<MethodGen> methodGens = new ArrayList<>(methods.size());
		final ClassGen clazz = new ClassGen(parameterClass);
		final ConstantPoolGen connstantPool = clazz.getConstantPool();

		for (final Method method : methods) {
			methodGens.add(new MethodGen(method, clazz.getClassName(), connstantPool));
		}

		return methodGens;
	}

	/**
	 * Gets the method gens from all classes of the file.
	 * 
	 * @return the method gens of all classes
	 * @throws IOException
	 *             when an error occurs when generating the classes
	 */
	public List<MethodGen> getMethodGens() throws IOException {
		generateJavaClasses();

		final List<MethodGen> methodGens = new LinkedList<>();

		for (final JavaClass javaClass : javaClasses) {

			try {
				methodGens.addAll(getMethodGens(javaClass.getClassName()));
			} catch (final ClassNotFoundException classNotFoundException) {
				throw new UnexpectedException(classNotFoundException);
			}

		}

		return methodGens;
	}

}
