package cucumber.eclipse.editor.markerResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import cucumber.eclipse.editor.steps.ExtensionRegistryStepProvider;
import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.steps.integration.Step;

public class UnmatchedStepMarkerResolutionGenerator implements IMarkerResolutionGenerator {

    private IStepProvider extensionRegistryStepProvider;

    public UnmatchedStepMarkerResolutionGenerator() {
        extensionRegistryStepProvider = new ExtensionRegistryStepProvider();
    }

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
        IFile featureFile = (IFile) marker.getResource();
        Set<Step> steps = extensionRegistryStepProvider.getStepsInEncompassingProject(featureFile);
        for (Step step : steps) {
            resolutions.add(new UnmatchedStepMarkerResolution(step, featureFile));
        }
        return resolutions.toArray(new IMarkerResolution[0]);
    }
}
