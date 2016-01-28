package com.verint.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import cucumber.eclipse.editor.steps.ExtensionRegistryStepProvider;
import cucumber.eclipse.steps.integration.Step;

public class GenerateStepDefinitionsHandler extends AbstractHandler {

    private static final int PARAMETER_REGEX_LENGTH = 8;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        String location = getFileLocation();
        File stepDefinitionsFile = new File(location, "StepDefinitions.txt");
        Set<Step> stepDefinitions = collectStepDefinitions();
        outputStepDefinitions(stepDefinitionsFile, stepDefinitions);

        return null;
    }

    private Set<Step> collectStepDefinitions() {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        IEditorInput input = editor.getEditorInput();
        IFile featurefile = ((IFileEditorInput) input).getFile();

        return new ExtensionRegistryStepProvider().getStepsInEncompassingProject(featurefile);
    }

    private void outputStepDefinitions(File stepDefinitionsFile, Set<Step> stepDefinitions) {
        PrintWriter writer = null;
        List<String> stepTexts = new ArrayList<>();
        for (Step stepDefinition : stepDefinitions) {
            stepTexts.add(formatStepDefinition(stepDefinition) + "\n");
        }
        Collections.sort(stepTexts);
        try {
            writer = new PrintWriter(stepDefinitionsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String step : stepTexts) {
            writer.write(step);
        }
        writer.close();
    }

    private String formatStepDefinition(Step stepDefinition) {
        String stepText = stepDefinition.getText().replace("^", "").replace("$", "");
        stepText = replaceParameters(stepText, stepDefinition.getParameters());
        return stepText;
    }

    private String replaceParameters(String stepText, String[] parameters) {
        String annotationWithParameters = stepText;
        int parameterCount = 0;
        int index = 0;
        while ((index = annotationWithParameters.indexOf("\"([\"]*)\"")) != -1) {
            annotationWithParameters = annotationWithParameters.substring(0, index)
                    + formatParameter(parameters[parameterCount])
                    + annotationWithParameters.substring(index + PARAMETER_REGEX_LENGTH);
            parameterCount++;
        }
        return annotationWithParameters;
    }

    private String formatParameter(String parameter) {
        String formattedParameter = parameter;
        formattedParameter = formattedParameter.substring(formattedParameter.indexOf(" ") + 1);
        formattedParameter = formattedParameter.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
        return "<" + formattedParameter + ">";
    }

    private String getFileLocation() {
        Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        DirectoryDialog dialog = new DirectoryDialog(activeShell, SWT.OPEN);
        String result = dialog.open();
        return result;
    }
}
