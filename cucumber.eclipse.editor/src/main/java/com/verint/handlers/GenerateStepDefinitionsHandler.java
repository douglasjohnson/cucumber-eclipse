package com.verint.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class GenerateStepDefinitionsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        String location = getFileLocation();
        File stepDefinitionsFile = new File(location, "StepDefinitions.txt");
        outputStepDefinitions(stepDefinitionsFile);

        return null;
    }

    private void outputStepDefinitions(File stepDefinitionsFile) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(stepDefinitionsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.println("The Step Definitions");
        writer.println("go here!");
        writer.close();
    }

    private String getFileLocation() {
        Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        DirectoryDialog dialog = new DirectoryDialog(activeShell, SWT.OPEN);
        String result = dialog.open();
        return result;
    }
}
