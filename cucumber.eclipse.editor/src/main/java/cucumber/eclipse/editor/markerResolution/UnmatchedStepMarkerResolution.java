package cucumber.eclipse.editor.markerResolution;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;

import cucumber.eclipse.steps.integration.Step;

class UnmatchedStepMarkerResolution implements IMarkerResolution, IRunnableWithProgress {

    private Step step;
    private IFile featureFile;
    private IMarker marker;

    public UnmatchedStepMarkerResolution(Step step, IFile featureFile) {
        this.step = step;
        this.featureFile = featureFile;
    }

    @Override
    public String getLabel() {
        return step.getText().replace("\"([^\"]*)\"", "\"<string>\"").replace("^", "").replace("$", "");
    }

    @Override
    public void run(IMarker marker) {
        this.marker = marker;
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()); 
            dialog.run(false, false, this);
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("", 3);
            monitor.subTask(featureFile.getName());
            InputStream inputStream = featureFile.getContents();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); 
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter); 
            while (bufferedReader.ready()) { 
                String line = bufferedReader.readLine(); 
                printWriter.println(line);
            }
            monitor.worked(1);
            
            String resolvedContent = stringWriter.toString();
            int markerStart = (int) marker.getAttribute("charStart");
            int markerEnd = (int) marker.getAttribute("charEnd");
            resolvedContent = resolvedContent.substring(0, markerStart)
                    + getLabel()
                    + resolvedContent.substring(markerEnd);
            featureFile.setContents(new ByteArrayInputStream(resolvedContent.getBytes()), false, true, monitor);
            monitor.worked(1);
            featureFile.refreshLocal(IResource.DEPTH_ZERO, monitor);
            monitor.worked(1);
        } catch (CoreException | IOException e) {
            e.printStackTrace();
        } 
    }
}