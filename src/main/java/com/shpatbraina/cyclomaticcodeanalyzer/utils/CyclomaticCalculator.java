package com.shpatbraina.cyclomaticcodeanalyzer.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclomaticCalculator {

    public int countCyclomaticComplexity(MethodDeclaration method) {

        AtomicInteger complexityCount = new AtomicInteger(1);
        countInner(method.getBody().get(), complexityCount);
        return complexityCount.get();
    }

    private void countInner(BlockStmt body, AtomicInteger complexityCount) {
        body.getStatements().forEach(statement -> {

            if (statement.isIfStmt()) {
                countIf(statement, complexityCount);
                countInner(statement.asIfStmt().getThenStmt().asBlockStmt(), complexityCount);
            } else if (statement.isForStmt() || statement.isForEachStmt() || statement.isDoStmt() || statement.isWhileStmt()
                    || statement.isReturnStmt() || statement.isBreakStmt() || statement.isContinueStmt() || statement.isThrowStmt()
                    || statement.isTryStmt()) {

                if (statement.isReturnStmt()) {

                    List<Node> childList = ((MethodDeclaration) statement.findRootNode()).getBody().get().getChildNodes();

                    if (childList.get(childList.size() - 1) != statement) {

                        complexityCount.getAndIncrement();
                    }
                } else {

                    complexityCount.getAndIncrement();
                    countInner(((NodeWithBody) statement).getBody().asBlockStmt(), complexityCount);
                }
            } else if (statement.isSwitchStmt()) {
                complexityCount.getAndIncrement();
                int switchCount = statement.asSwitchStmt().getEntries().size();
                complexityCount.addAndGet(switchCount);
            }
        });
    }

    private void countIf(Statement statement, AtomicInteger integer) {

        if (statement.isIfStmt()) {
            AtomicInteger ifCount = new AtomicInteger(1);
            countConditions(statement.asIfStmt().getCondition(), ifCount);
            integer.addAndGet(ifCount.get());
            if (statement.asIfStmt().getElseStmt().isPresent()) {
                countIf(statement.asIfStmt().getElseStmt().get(), integer);
            }
        }
        if (statement.isBlockStmt()) {
            integer.getAndIncrement();
        }
    }

    private void countConditions(Expression expression, AtomicInteger count) {

        if(expression.isBinaryExpr()) {
            if (expression.asBinaryExpr().getLeft().isBinaryExpr()) {
                count.getAndIncrement();
                countConditions(expression.asBinaryExpr().getLeft(), count);
            } else if (expression.asBinaryExpr().getRight().isBinaryExpr()) {
                count.getAndIncrement();
                countConditions(expression.asBinaryExpr().getRight(), count);
            }
        }
    }
}