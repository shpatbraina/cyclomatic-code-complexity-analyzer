package com.shpatbraina.cyclomaticcodeanalyzer.utils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
/**
 * Per kalkulim te kompleksitetit ciklomatik si reference kam perdorur linkun ne vijim:
 *
 * https://perso.ensta-paris.fr/~diam/java/online/notes-java/principles_and_practices/complexity/complexity-java-method.html
 *
 * */
public class InputValuesGenerator {

    private Random random = new Random();
    private StringBuilder output = new StringBuilder();

    public String generateValues(MethodDeclaration method) {

        generateValues(method.getParameters());
        return output.toString().stripLeading();
    }

    private void generateValues(NodeList<Parameter> parameters) {
        parameters.forEach(parameter -> {
            output.append("\n\nGenerated values for input \"" + parameter.toString() + "\" to be tested are: ");
            if(parameter.getType().isArrayType()){
                generateArrayTypeValues(parameter.getType().asArrayType());
            }
            else if(parameter.getType().isPrimitiveType()) {
                generatePrimitiveTypeValues(parameter.getType().asPrimitiveType());
            }
            else if(parameter.getType().isClassOrInterfaceType()) {
                generateClassOrInterfaceValues(parameter.getType().asClassOrInterfaceType());
            }
        });
    }

    private void generateArrayTypeValues(ArrayType arrayType) {

        if(arrayType.getComponentType().asString().equals("byte")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Byte")) {
            byte[] array = new byte[Byte.MAX_VALUE];
            byte[] array2 = new byte[Byte.MAX_VALUE];
            random.nextBytes(array);
            random.nextBytes(array2);

            output.append("\nrandom array: = null;");
            output.append("\nrandom array: { " + array + "}");
            output.append("\nrandom array: { " + array2 + "}");
        }
        else if(arrayType.getComponentType().asString().equals("short")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Short")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextInt(Short.MAX_VALUE) + " }");
            output.append("\nrandom array: = { " + random.nextInt(Short.MAX_VALUE) + ", " + random.nextInt(Short.MAX_VALUE) + " }");
        }
        else if(arrayType.getComponentType().asString().equals("int")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Integer")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextInt(Integer.MAX_VALUE) + " }");
            output.append("\nrandom array: = { " + random.nextInt(Integer.MAX_VALUE) + ", " + random.nextInt(Integer.MAX_VALUE) + " }");
        }
        else if(arrayType.getComponentType().asString().equals("long")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Long")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextLong() + " }");
            output.append("\nrandom array: = { " + random.nextLong() + ", " + random.nextLong() + " }");
        }
        else if(arrayType.getComponentType().asString().equals("float")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Float")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextFloat() + " }");
            output.append("\nrandom array: = { " + random.nextFloat() + ", " + random.nextFloat() + " }");
        }
        else if(arrayType.getComponentType().asString().equals("double")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Double")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextDouble() + " }");
            output.append("\nrandom array: = { " + random.nextDouble() + ", " + random.nextDouble() + " }");
        }
        else if(arrayType.getComponentType().asString().equals("char")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Character")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + (char)(random.nextInt(26)) + " }");
            output.append("\nrandom array: = { " + (char)(random.nextInt(26)) + ", " + (char)(random.nextInt(26)) + " }");
        }
        else if(arrayType.getComponentType().asString().equals("boolean")
                || arrayType.getComponentType().asString().equalsIgnoreCase("Boolean")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + random.nextBoolean() + " }");
            output.append("\nrandom array: = { " + random.nextBoolean() + ", " + random.nextBoolean() + " }");
        }
        else if(arrayType.getComponentType().asString().equalsIgnoreCase("String")) {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { " + RandomStringUtils.randomAlphabetic(10) + " }");
            output.append("\nrandom array: = { " + RandomStringUtils.randomAlphabetic(10) + ", " + RandomStringUtils.randomAlphanumeric(10) + " }");
        }
        else {
            output.append("\nrandom array: = null");
            output.append("\nrandom array: = {}");
            output.append("\nrandom array: = { new " + arrayType.getComponentType().toString() +"() }");
            output.append("\nrandom array: = { new " + arrayType.getComponentType().toString() +"(), new "
                    + arrayType.getComponentType().toString() + "() }");
        }
    }

    private void generatePrimitiveTypeValues(PrimitiveType primitiveType) {

        if(primitiveType.getType().asString().equals("byte")) {
            byte[] array = new byte[Byte.MAX_VALUE];
            byte[] array2 = new byte[Byte.MAX_VALUE];
            random.nextBytes(array);
            random.nextBytes(array2);
            output.append("\nrandom byte: " + array);
            output.append("\nrandom byte: " + array2);
        }
        else if(primitiveType.getType().asString().equals("short")) {
            output.append("\nrandom short: " + random.nextInt(Short.MAX_VALUE));
            output.append("\nrandom short: " + random.nextInt(Short.MAX_VALUE));
        }
        else if(primitiveType.getType().asString().equals("int")) {
            output.append("\nrandom int: " + random.nextInt(Integer.MAX_VALUE));
            output.append("\nrandom int: " + random.nextInt(Integer.MAX_VALUE));
        }
        else if(primitiveType.getType().asString().equals("long")) {
            output.append("\nrandom long: " + random.nextLong());
            output.append("\nrandom long: " + random.nextLong());
        }
        else if(primitiveType.getType().asString().equals("float")) {
            output.append("\nrandom float: " + random.nextFloat());
            output.append("\nrandom float: " + random.nextFloat());
        }
        else if(primitiveType.getType().asString().equals("double")) {
            output.append("\nrandom double: " + random.nextDouble());
            output.append("\nrandom double: " + random.nextDouble());
        }
        else if(primitiveType.getType().asString().equals("char")) {
            output.append("\nrandom char: " + (char)(random.nextInt(26)));
            output.append("\nrandom char: " + (char)(random.nextInt(26)));
        }
        else if(primitiveType.getType().asString().equals("boolean")) {
            output.append("\nrandom boolean: " + random.nextBoolean());
            output.append("\nrandom boolean: " + random.nextBoolean());
        }
    }

    private void generateClassOrInterfaceValues(ClassOrInterfaceType classOrInterfaceType) {

        if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Byte")) {
            Byte b = Byte.parseByte(RandomStringUtils.randomAlphanumeric(5));
            Byte b1 = Byte.parseByte(RandomStringUtils.randomAlphanumeric(5));

            output.append("\nrandom Byte: null;");
            output.append("\nrandom Byte: " + b);
            output.append("\nrandom Byte: " + b1);
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Short")) {
            output.append("\nrandom Short: null");
            output.append("\nrandom Short: " + random.nextInt(Short.MAX_VALUE));
            output.append("\nrandom Short: " + random.nextInt(Short.MAX_VALUE));
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Integer")) {
            output.append("\nrandom Integer: null");
            output.append("\nrandom Integer: " + random.nextInt(Integer.MAX_VALUE));
            output.append("\nrandom Integer: " + random.nextInt(Integer.MAX_VALUE));
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Long")) {
            output.append("\nrandom Long: null");
            output.append("\nrandom Long: " + random.nextLong());
            output.append("\nrandom Long: " + random.nextLong());
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Float")) {
            output.append("\nrandom Float: null");
            output.append("\nrandom Float: " + random.nextFloat());
            output.append("\nrandom Float: " + random.nextFloat());
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Double")) {
            output.append("\nrandom Double: null");
            output.append("\nrandom Double: " + random.nextDouble());
            output.append("\nrandom Double: " + random.nextDouble());
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Character")) {
            output.append("\nrandom Character: null");
            output.append("\nrandom Character: " + (char)(random.nextInt(26)));
            output.append("\nrandom Character: " + (char)(random.nextInt(26)));
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("Boolean")) {
            output.append("\nrandom Boolean: null");
            output.append("\nrandom Boolean: " + random.nextBoolean());
            output.append("\nrandom Boolean: " + random.nextBoolean());
        }
        else if(classOrInterfaceType.getElementType().asString().equalsIgnoreCase("String")) {
            output.append("\nrandom String: null");
            output.append("\nrandom String: " + RandomStringUtils.randomAlphabetic(10));
            output.append("\nrandom String: " + RandomStringUtils.randomAlphanumeric(10));
        }
        else {
            output.append("\nrandom " + classOrInterfaceType.getElementType().toString() +": null");
            output.append("\nrandom " + classOrInterfaceType.getElementType().toString() +": = new "
                    + classOrInterfaceType.getElementType().toString() + "();");
        }
    }
}