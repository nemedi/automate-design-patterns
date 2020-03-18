package demo.builder;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("demo.builder.Buildable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuildableProcessor extends AbstractProcessor {

	public BuildableProcessor() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		try {
			annotations
				.stream()
				.filter(annotation -> roundEnvironment.getElementsAnnotatedWith(annotation) != null)
				.flatMap(annotation -> roundEnvironment.getElementsAnnotatedWith(annotation).stream())
				.filter(element -> ElementKind.CLASS.equals(element.getKind()))
				.map(element -> (TypeElement) element)
				.forEach(typeElement -> writeBuilderFile(typeElement));
			return true;
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error: " + e.getMessage());
			return false;
		}
	}

	private void writeBuilderFile(TypeElement typeElement) {
		try {
			String className = typeElement.getQualifiedName().toString();
			int lastDotIndex = className.lastIndexOf('.');
			String classSimpleName = className.substring(lastDotIndex + 1);
			String builderClassName = Builder.class.getName();
			String builderClassSimpleName = builderClassName.substring(builderClassName.lastIndexOf('.') + 1);
			String thisBuilderClassName = className + "Builder";
			JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(thisBuilderClassName);
			String thisBuilderClassSimpleName = thisBuilderClassName.substring(lastDotIndex + 1);
			String template = "";
			try (Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("BuilderTemplate.java"))) {
				scanner.useDelimiter("\\A");
				if (scanner.hasNext()) {
					template = scanner.next();
				}
			}
			String packageName = lastDotIndex > 0 ? className.substring(0, lastDotIndex) : null;
			template = replaceBlock(template, "_package:begin", "_package:end",
					packageName != null
					? getBlock(template, "_package:begin", "_package:end").replace("$packageName", packageName)
					: "");
			template = template.replace("$className", className)
					.replace("$classSimpleName", classSimpleName)
					.replace("$builderClassName", builderClassName)
					.replace("$builderClassSimpleName", builderClassSimpleName)
					.replace("$thisBuilderClassName", thisBuilderClassName)
					.replace("$thisBuilderClassSimpleName", thisBuilderClassSimpleName);
			List<VariableElement> fields = typeElement.getEnclosedElements()
					.stream()
					.filter(element -> ElementKind.FIELD.equals(element.getKind()))
					.map(element -> (VariableElement) element)
					.collect(Collectors.toList());
			String setterTemplate = getBlock(template, "_setter:begin", "_setter:end");
			String getterTemplate = getBlock(template, "_getter:begin", "_getter:end");
			StringBuilder setterBuilder = new StringBuilder();
			StringBuilder getterBuilder = new StringBuilder();
			for (VariableElement field : fields) {
				String fieldType = field.asType().toString();
				if (fieldType.startsWith("java.lang.")) {
					fieldType = fieldType.substring("java.lang.".length());
				}
				String fieldName = field.getSimpleName().toString();
				setterBuilder.append(setterTemplate.replace("$fieldName", fieldName)
						.replace("$fieldType", fieldType));
				getterBuilder.append(getterTemplate.replace("$fieldName", fieldName)
						.replace("$fieldType", fieldType));
			}
			template = replaceBlock(template, "_setter:begin", "_setter:end", setterBuilder.toString());
			template = replaceBlock(template, "_getter:begin", "_getter:end", getterBuilder.toString());
			fields.forEach(System.out::println);
			try (PrintWriter writer = new PrintWriter(builderFile.openWriter())) {
				writer.print(template);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	private static String getBlock(String template, String beginLandmark, String endLandmark) {
		int beginLandmarkIndex = template.indexOf(beginLandmark);
		int endLandmarkIndex = template.indexOf(endLandmark);
		return beginLandmarkIndex > -1 && endLandmarkIndex > beginLandmarkIndex
				? template.substring(beginLandmarkIndex + beginLandmark.length(), endLandmarkIndex)
				: template;
	}
	
	private static String replaceBlock(String template, String beginLandmark, String endLandmark, String replacement) {
		int beginLandmarkIndex = template.indexOf(beginLandmark);
		int endLandmarkIndex = template.indexOf(endLandmark);
		return beginLandmarkIndex > -1 && endLandmarkIndex > beginLandmarkIndex
				? new StringBuilder()
						.append(template.substring(0, beginLandmarkIndex))
						.append(replacement)
						.append(template.substring(endLandmarkIndex + endLandmark.length()))
						.toString()
				: template;
	}

}
