package com.malsolo.code.manipulation.javaagent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.malsolo.code.manipulation.ImportantLog;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class ImportantLogTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		System.out.printf(">>>>> Transforming %s\n", className);

		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
		try {
			CtClass cclass = pool.get(className.replaceAll("/", "."));
			if (!cclass.isFrozen()) {
				for (CtMethod currentMethod : cclass.getDeclaredMethods()) {
					Annotation annotation = getAnnotation(currentMethod);
					if (annotation != null) {
						List<String> parameterIndexes = getParamIndexes(annotation);
						currentMethod.insertBefore(createJavaString(
								currentMethod, className, parameterIndexes));
					}
				}
			}
			return cclass.toBytecode();
		} catch (NotFoundException | IOException | CannotCompileException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private Annotation getAnnotation(CtMethod method) {
		MethodInfo mInfo = method.getMethodInfo();
		AnnotationsAttribute attInfo = (AnnotationsAttribute) mInfo.getAttribute(AnnotationsAttribute.invisibleTag);
		if (attInfo != null) {
			return attInfo.getAnnotation(ImportantLog.class.getName().replaceAll("/", "."));
		}
		return null;
	}

	private List<String> getParamIndexes(Annotation annotation) {
		ArrayMemberValue fields = (ArrayMemberValue) annotation.getMemberValue("fields");
		if (fields != null) {
			MemberValue[] values = fields.getValue();
			List<String> parameterIndexes = new ArrayList<>();
			for (MemberValue memberValue : values) {
				parameterIndexes.add(((StringMemberValue) memberValue).getValue());
			}
			return parameterIndexes;
		}
		return Collections.emptyList();
	}

	private String createJavaString(CtMethod currentMethod, String className,
			List<String> parameterIndexes) {
		StringBuilder sb = new StringBuilder();
		sb.append("{StringBuilder sb = new StringBuilder");
		sb.append("(\"A call was made to method '\");");
		sb.append("sb.append(\"");
		sb.append(currentMethod.getName());
		sb.append("\");sb.append(\"' on class '\");");
		sb.append("sb.append(\"");
		sb.append(className);
		sb.append("\");sb.append(\"'.\");");
		sb.append("sb.append(\"\\n	Important params:\");");
		for (String index : parameterIndexes) {
			int localVar = Integer.parseInt(index) + 1;
			sb.append("sb.append(\"\\n	Index: \");");
			sb.append("sb.append(\"");
			sb.append(index);
			sb.append("\");sb.append(\" value: \");");
			sb.append("sb.append($" + localVar + ");");
		}
		sb.append("System.out.println(sb.toString());}");

		return sb.toString();
	}

}
