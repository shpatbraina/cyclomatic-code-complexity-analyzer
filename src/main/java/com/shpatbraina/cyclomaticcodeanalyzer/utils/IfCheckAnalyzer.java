package com.shpatbraina.cyclomaticcodeanalyzer.utils;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class IfCheckAnalyzer {

    private StringBuilder results;

    public String analyze(MethodDeclaration method) {
        results = new StringBuilder();
        results.append("\n\n Analyzing IF Checks \n");
        innerAnalyze(method.getBody().get());
        return results.toString();
    }

    private void innerAnalyze(BlockStmt body) {
        body.getStatements().forEach(statement -> {

            if (statement.isIfStmt()) {
                countIf(statement);
                innerAnalyze(statement.asIfStmt().getThenStmt().asBlockStmt());
            } else if (statement.isForStmt() || statement.isDoStmt() || statement.isWhileStmt()) {
                countIf(statement);
                innerAnalyze(((NodeWithBody) statement).getBody().asBlockStmt());
            }
        });
    }

    private void countIf(Statement statement) {

        if (statement.isIfStmt()) {
            countConditions(statement.asIfStmt().getCondition());
            if (statement.asIfStmt().getElseStmt().isPresent()) {
                countIf(statement.asIfStmt().getElseStmt().get());
            }
        }
        else if(statement.isForStmt()) {
            if(statement.asForStmt().getCompare().isPresent()) {
                countConditions(statement.asForStmt().getCompare().get());
            }
        }
    }

    private void countConditions(Expression expression) {
        results.append("\n expression: ").append(expression.toString());
        if(expression.isBinaryExpr()) {
            if (expression.asBinaryExpr().getLeft().isBinaryExpr()) {
                countConditions(expression.asBinaryExpr().getLeft());
            } else if (expression.asBinaryExpr().getRight().isBinaryExpr()) {
                countConditions(expression.asBinaryExpr().getRight());
            } else {
                Operator operator = expression.asBinaryExpr().getOperator();
                Expression left = expression.asBinaryExpr().getLeft();
                Expression right = expression.asBinaryExpr().getRight();
                analyzeCondition(left, right, operator);
            }
        }
        else if(expression.isMethodCallExpr()) {
            analyzeMethodCall(expression);
        }
    }

    private void analyzeMethodCall(Expression expression) {

        results.append("\n");

        if(expression.isMethodCallExpr()) {
            results.append(expression.asMethodCallExpr().getScope().get().asNameExpr().getName().asString())
                    .append(" should be ")
                    .append(expression.asMethodCallExpr().getName().asString())
                    .append(" to ");
            if(expression.asMethodCallExpr().getArguments().get(0).isStringLiteralExpr()) {
                results
                        .append(expression.asMethodCallExpr().getArguments().get(0).asStringLiteralExpr().asString())
                        .append("\n");
            }
            else if(expression.asMethodCallExpr().getArguments().get(0).isNameExpr()) {
                results.append(expression.asMethodCallExpr().getArguments().get(0).asNameExpr().getName().asString())
                        .append("\n");
            }
        }
    }

    private void analyzeCondition(Expression left, Expression right, Operator operator){

        //Integer
        if(left.isIntegerLiteralExpr() && right.isIntegerLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should not be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal or greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal or less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
        }
        else if(left.isNameExpr() && right.isIntegerLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isIntegerLiteralExpr() && right.isNameExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be greater than ")
                        .concat(right.asNameExpr().getName().asString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be less than ")
                        .concat(right.asNameExpr().getName().asString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to or greater than ")
                        .concat(right.asNameExpr().getName().asString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal or less than ")
                        .concat(right.asNameExpr().getName().asString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isFieldAccessExpr() && right.isIntegerLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal or greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isIntegerLiteralExpr() && right.isFieldAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should not be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be less than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to or greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal or less than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isArrayAccessExpr() && right.isIntegerLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or greater than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                Integer i = Integer.parseInt(right.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(right.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isIntegerLiteralExpr() && right.isArrayAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                Integer j = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should not be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be less than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal to or greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asIntegerLiteralExpr().asNumber().toString().concat(" should be equal or less than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Integer i = Integer.parseInt(left.asIntegerLiteralExpr().asNumber().toString()) - 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(left.asIntegerLiteralExpr().asNumber().toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        //Double
        if(left.isDoubleLiteralExpr() && right.isDoubleLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should not be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal or greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal or less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
        }
        else if(left.isNameExpr() && right.isDoubleLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double j = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isDoubleLiteralExpr() && right.isNameExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                Double j = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be greater than ")
                        .concat(right.asNameExpr().getName().asString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be less than ")
                        .concat(right.asNameExpr().getName().asString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to or greater than ")
                        .concat(right.asNameExpr().getName().asString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal or less than ")
                        .concat(right.asNameExpr().getName().asString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asNameExpr().getName().asString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isFieldAccessExpr() && right.isDoubleLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double j = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should not be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal or greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isDoubleLiteralExpr() && right.isFieldAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                Double j = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should not be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be less than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to or greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal or less than ")
                        .concat(right.asFieldAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asFieldAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isArrayAccessExpr() && right.isDoubleLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double j = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should not be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or greater than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                Double i = right.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + left.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        else if(left.isDoubleLiteralExpr() && right.isArrayAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                Double j = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(j.toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should not be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be less than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to or greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() + 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal or less than ")
                        .concat(right.asArrayAccessExpr().toString()));
                Double i = left.asDoubleLiteralExpr().asDouble() - 1;
                results.append("\n Positive case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(Double.toString(left.asDoubleLiteralExpr().asDouble())));
                results.append("\n Negative case: " + right.asArrayAccessExpr().toString().concat(" = ")
                        .concat(i.toString()));
            }
        }
        //String
        if(left.isStringLiteralExpr() && right.isStringLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + Double.toString(left.asDoubleLiteralExpr().asDouble()).concat(" should not be equal to ")
                        .concat(Double.toString(right.asDoubleLiteralExpr().asDouble())));
            }
        }
        else if(left.isNameExpr() && right.isStringLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            }
        }
        else if(left.isStringLiteralExpr() && right.isNameExpr()) {
            if (operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            } else if (operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
        }
        else if(left.isFieldAccessExpr() && right.isStringLiteralExpr()) {
            if (operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            } else if (operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            }
        }
        else if(left.isStringLiteralExpr() && right.isFieldAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should not be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
        }
        else if(left.isArrayAccessExpr() && right.isStringLiteralExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asStringLiteralExpr().asString()));
            }
        }
        else if(left.isStringLiteralExpr() && right.isArrayAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asStringLiteralExpr().asString().concat(" should not be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
        }
        //NameExpr
        else if(left.isNameExpr() && right.isNameExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to or greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
        }
        else if(left.isNameExpr() && right.isFieldAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be less than ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to or greater than ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or less than ")
                        .concat(right.asFieldAccessExpr().toString()));
            }
        }
        else if(left.isFieldAccessExpr() && right.isNameExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal to or greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asFieldAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
        }
        else if(left.isArrayAccessExpr() && right.isNameExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should not be equal to ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or greater than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asArrayAccessExpr().toString().concat(" should be equal or less than ")
                        .concat(right.asNameExpr().getName().asString()));
            }
        }
        else if(left.isNameExpr() && right.isArrayAccessExpr()){
            if(operator.equals(Operator.EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.NOT_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should not be equal to ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.GREATER)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.LESS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be less than ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.GREATER_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal to or greater than ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
            else if(operator.equals(Operator.LESS_EQUALS)) {
                results.append("\n" + left.asNameExpr().getName().asString().concat(" should be equal or less than ")
                        .concat(right.asArrayAccessExpr().toString()));
            }
        }
        results.append("\n");
    }
}