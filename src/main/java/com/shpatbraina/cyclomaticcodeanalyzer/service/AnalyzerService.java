package com.shpatbraina.cyclomaticcodeanalyzer.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.shpatbraina.cyclomaticcodeanalyzer.utils.CyclomaticCalculator;
import com.shpatbraina.cyclomaticcodeanalyzer.utils.IfCheckAnalyzer;
import com.shpatbraina.cyclomaticcodeanalyzer.utils.InputValuesGenerator;
import com.shpatbraina.cyclomaticcodeanalyzer.utils.TestCasesNumberCalculator;
import org.springframework.stereotype.Service;
/**
 * Per kalkulim te kompleksitetit ciklomatik si reference kam perdorur linkun ne vijim:
 *
 * https://perso.ensta-paris.fr/~diam/java/online/notes-java/principles_and_practices/complexity/complexity-java-method.html
 *
 * */
@Service
public class AnalyzerService {

    private JavaParser javaParser = new JavaParser();

    public Integer calculateCyclomaticComplexity(String method) throws Exception {

        ParseResult<MethodDeclaration> parseResult = javaParser.parseMethodDeclaration(method);

        MethodDeclaration methodDeclaration = parseResult.getResult().orElseThrow(() -> new Exception("no results"));
        CyclomaticCalculator cyclomaticCalculator = new CyclomaticCalculator();
        return cyclomaticCalculator.countCyclomaticComplexity(methodDeclaration);
    }

    public String generateInputValues(String method) throws Exception {

        ParseResult<MethodDeclaration> parseResult = javaParser.parseMethodDeclaration(method);
        MethodDeclaration methodDeclaration = parseResult.getResult().orElseThrow(() -> new Exception("no results"));
        InputValuesGenerator inputValuesGenerator = new InputValuesGenerator();
        return inputValuesGenerator.generateValues(methodDeclaration);
    }

    public Integer calculateMaxNumberOfTestCases(String method) throws Exception {
        ParseResult<MethodDeclaration> parseResult = javaParser.parseMethodDeclaration(method);
        MethodDeclaration methodDeclaration = parseResult.getResult().orElseThrow(() -> new Exception("no results"));
        TestCasesNumberCalculator testCasesNumberCalculator = new TestCasesNumberCalculator();
        return testCasesNumberCalculator.calculateMaxNumberOfTestCases(methodDeclaration);
    }

    public String analyzeIfChecks(String method) throws Exception {
        ParseResult<MethodDeclaration> parseResult = javaParser.parseMethodDeclaration(method);
        MethodDeclaration methodDeclaration = parseResult.getResult().orElseThrow(() -> new Exception("no results"));
        IfCheckAnalyzer ifCheckAnalyzer = new IfCheckAnalyzer();
        return ifCheckAnalyzer.analyze(methodDeclaration);
    }
}