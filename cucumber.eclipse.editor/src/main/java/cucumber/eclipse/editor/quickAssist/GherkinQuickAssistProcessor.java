package cucumber.eclipse.editor.quickAssist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import cucumber.eclipse.editor.editors.GherkinCompletionProposal;
import cucumber.eclipse.editor.editors.Editor;
import cucumber.eclipse.editor.markers.MarkerIds;
import cucumber.eclipse.editor.steps.ExtensionRegistryStepProvider;
import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.steps.integration.Step;

public class GherkinQuickAssistProcessor implements IQuickAssistProcessor {

    private IStepProvider extensionRegistryStepProvider;
    private IFile featurefile;

    public GherkinQuickAssistProcessor(Editor editor) {
        IEditorInput input = editor.getEditorInput();
        featurefile = ((IFileEditorInput) input).getFile();
        extensionRegistryStepProvider = new ExtensionRegistryStepProvider();
    }

    @Override
    public boolean canAssist(IQuickAssistInvocationContext context) {
        return false;
    }

    @Override
    public boolean canFix(Annotation annotation) {
        return annotation.getType().equals(MarkerIds.UNMATCHED_STEP);
    }

    @Override
    public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext context) {
        return getCompletionProposals(context.getSourceViewer(), context.getOffset(), context.getLength())
                .toArray(new ICompletionProposal[0]);
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    private List<ICompletionProposal> getCompletionProposals(ISourceViewer sourceViewer, int offset, int length) {
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        if (offset >= 0) {
            IAnnotationModel annotationModel = sourceViewer.getAnnotationModel();
            Iterator<?> annotationIterator = annotationModel.getAnnotationIterator();
            while (annotationIterator.hasNext()) {
                Annotation annotation = (Annotation) annotationIterator.next();
                if (canFix(annotation) && !annotation.isMarkedDeleted() && isAnnotationSelected(annotation, annotationModel, offset, length)) {
                    proposals.addAll(getCompletionProposals(annotation, annotationModel, offset, length));
                }
            }
        }
        return proposals;
    }

    private boolean isAnnotationSelected(Annotation annotation, IAnnotationModel annotationModel, int offset,
            int length) {
        Position annotationPosition = annotationModel.getPosition(annotation);
        return annotationPosition.overlapsWith(offset, length);
    }

    private List<ICompletionProposal> getCompletionProposals(Annotation annotation, IAnnotationModel annotationModel,
            int offset, int length) {
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        Position annotationPosition = annotationModel.getPosition(annotation);
        for (Step step : getAllSteps()) {
            String replacementText = step.getContextHelpText();
            proposals.add(new GherkinCompletionProposal(new CompletionProposal(replacementText, annotationPosition.getOffset(), annotationPosition.getLength(),
            		replacementText.length()), step));
        }
        return proposals;
    }

    private Set<Step> getAllSteps() {
        return extensionRegistryStepProvider.getStepsInEncompassingProject(featurefile);
    }
}
