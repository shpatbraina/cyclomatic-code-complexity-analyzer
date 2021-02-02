package com.shpatbraina.cyclomaticcodeanalyzer.uicontroller;

import com.shpatbraina.cyclomaticcodeanalyzer.service.AnalyzerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@StyleSheet(value = "theme.css")
@Route("")
public class UIController extends HorizontalLayout {

    public UIController(@Autowired AnalyzerService analyzerService) {

        TextArea method = new TextArea("Method");
        method.setWidth("1000px");
        method.setHeight("700px");

        Button analyzeButton = new Button("Analyze", VaadinIcon.CHECK.create());

        Label resultLabel = new Label("Complexity Result");
        Paragraph result = new Paragraph();

        Label minTestCasesLabel = new Label("Minimum test cases needed");
        Paragraph minTestCases = new Paragraph();

        Label maxTestCasesLabel = new Label("Maximum test cases needed");
        Paragraph maxTestCases = new Paragraph();

        TextArea output = new TextArea("Output");
        output.setWidth("1000px");
        output.setHeight("700px");

        analyzeButton.addClickListener(e -> {
            try {
                Integer complexity = analyzerService.calculateCyclomaticComplexity(method.getValue());
                result.setText(complexity.toString());
                minTestCases.setText(complexity.toString());
                maxTestCases.setText(analyzerService.calculateMaxNumberOfTestCases(method.getValue()).toString());
                StringBuilder outputText = new StringBuilder(analyzerService.generateInputValues(method.getValue()));
                outputText.append(analyzerService.analyzeIfChecks(method.getValue()));
                output.setValue(outputText.toString());
            } catch (Exception exception) {
                output.setValue(exception.getMessage());
                exception.printStackTrace();
            }
        });

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("120px");
        verticalLayout.setHeight("700px");
        verticalLayout.setMargin(true);

        verticalLayout.add(analyzeButton, resultLabel, result, minTestCasesLabel, minTestCases, maxTestCasesLabel, maxTestCases);

        add(method, verticalLayout, output);
    }
}