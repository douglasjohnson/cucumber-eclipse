package cucumber.eclipse.editor.quickAssist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

class UnmatchedStepCompletionProposal implements ICompletionProposal {

    private ICompletionProposal completionProposal;

    UnmatchedStepCompletionProposal(String replacementString, int replacementOffset,
            int replacementLength, int cursorPosition) {
        completionProposal = new CompletionProposal(replacementString, replacementOffset, replacementLength,
                cursorPosition);
    }

    @Override
    public void apply(IDocument document) {
        completionProposal.apply(document);
    }

    @Override
    public Point getSelection(IDocument document) {
        return completionProposal.getSelection(document);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return completionProposal.getAdditionalProposalInfo();
    }

    @Override
    public String getDisplayString() {
        return completionProposal.getDisplayString();
    }

    @Override
    public Image getImage() {
        return completionProposal.getImage();
    }

    @Override
    public IContextInformation getContextInformation() {
        return completionProposal.getContextInformation();
    }

}