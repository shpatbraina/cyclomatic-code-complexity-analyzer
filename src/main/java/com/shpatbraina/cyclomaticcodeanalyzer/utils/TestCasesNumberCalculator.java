package com.shpatbraina.cyclomaticcodeanalyzer.utils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * Per kalkulim te kompleksitetit ciklomatik si reference kam perdorur linkun ne vijim:
 *
 * https://perso.ensta-paris.fr/~diam/java/online/notes-java/principles_and_practices/complexity/complexity-java-method.html
 *
 * */
public class TestCasesNumberCalculator {

    private AtomicInteger ifCount = new AtomicInteger(0);
    private List ifStatements = new ArrayList<BinaryExpr>();
    private AtomicInteger innerIfCount = new AtomicInteger(0);
    private boolean innerIf = false;

    public Integer calculateMaxNumberOfTestCases(MethodDeclaration method) {

        countInner(method.getBody().get(), method.getParameters());
        if(innerIf) {
            return ifCount.get() * 2 - innerIfCount.get();
        }
        else {
            return ifCount.get() * 2;
        }
    }

    private void countInner(BlockStmt body, NodeList<Parameter> parameters) {
        body.getStatements().forEach(statement -> {

            if (statement.isIfStmt()) {
                countIf(statement, parameters);
                countInner(statement.asIfStmt().getThenStmt().asBlockStmt(), parameters);
            } else if (statement.isForStmt() || statement.isDoStmt() || statement.isWhileStmt()) {
                countIf(statement, parameters);
                countInner(((NodeWithBody) statement).getBody().asBlockStmt(), parameters);
            } else if (statement.isForEachStmt() || statement.isTryStmt() || statement.isThrowStmt()) {
                countInner(((NodeWithBody) statement).getBody().asBlockStmt(), parameters);
            } else if (statement.isSwitchStmt()) {
                countConditions(statement.asSwitchStmt().getSelector(), parameters);
            }
        });
    }

    private void countIf(Statement statement, NodeList<Parameter> parameters) {

        if (statement.isIfStmt()) {
            countConditions(statement.asIfStmt().getCondition(), parameters);
            if(statement.asIfStmt().getElseStmt().isPresent() && statement.asIfStmt().getElseStmt().get().isIfStmt()){
                countIf(statement.asIfStmt().getElseStmt().get(), parameters);
                countInner(statement.asIfStmt().getElseStmt().get().asIfStmt().getThenStmt().asBlockStmt(), parameters);
            }
            else if(statement.asIfStmt().getElseStmt().isPresent()){
                countInner(statement.asIfStmt().getElseStmt().get().asBlockStmt(), parameters);
            }
        } else if (statement.isForStmt() && statement.asForStmt().getCompare().isPresent()) {
            countConditions(statement.asForStmt().getCompare().get(), parameters);
        } else if (statement.isDoStmt()) {
            countConditions(statement.asDoStmt().getCondition(), parameters);
        } else if (statement.isWhileStmt()) {
            countConditions(statement.asWhileStmt().getCondition(), parameters);
        }

    }

    private void countConditions(Expression expression, NodeList<Parameter> parameters) {

        if (expression.isBinaryExpr()) {
            if (expression.asBinaryExpr().getLeft().isBinaryExpr()) {
                countConditions(expression.asBinaryExpr().getLeft(), parameters);
            }
            if (expression.asBinaryExpr().getRight().isBinaryExpr()) {
                countConditions(expression.asBinaryExpr().getRight(), parameters);
            }
            else {
                if(ifStatements.contains(expression.asBinaryExpr())){
                    innerIfCount.getAndIncrement();
                    innerIf = true;
                }
                else {
                    ifStatements.add(expression.asBinaryExpr());
                }

                List names = expression.getChildNodes().stream()
                        .map(node -> {
                            if (!node.getChildNodes().isEmpty() && node.getChildNodes().get(0) instanceof NameExpr) {
                                return ((NameExpr) node.getChildNodes().get(0)).getName().asString();
                            }
                            return node.toString();
                        })
                        .collect(Collectors.toList()
                        );
                if (!parameters.stream().filter(parameter -> names.contains(parameter.getName().asString())).collect(Collectors.toList()).isEmpty()) {
                    ifCount.getAndIncrement();
                }
            }
        }
        else if(expression.isMethodCallExpr()) {
            ifCount.getAndIncrement();
        }
    }
}