package cucumber.eclipse.editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import cucumber.eclipse.steps.integration.Step;

public class CucumberCompletionProposal implements ICompletionProposal, ICompletionProposalExtension6 {
	
	CompletionProposal wrappedProposal;
	Step step;

	public CucumberCompletionProposal(CompletionProposal p, Step step) {
		this.wrappedProposal = p; 
		this.step = step;
	}
	
	@Override
	public StyledString getStyledDisplayString() {
		StyledString styledDisplayString = new StyledString(step.getContextHelpText());
		styledDisplayString.append(" - "+step.getFileLocation(), StyledString.QUALIFIER_STYLER);
		
		return styledDisplayString;
	}

	@Override
	public void apply(IDocument document) {
		wrappedProposal.apply(document);
	}

	@Override
	public String getAdditionalProposalInfo() {
		return wrappedProposal.getAdditionalProposalInfo();
	}

	@Override
	public IContextInformation getContextInformation() {
		return wrappedProposal.getContextInformation();
	}

	@Override
	public String getDisplayString() {
		return wrappedProposal.getDisplayString();
	}

	@Override
	public Image getImage() {
		return wrappedProposal.getImage();
	}

	@Override
	public Point getSelection(IDocument arg0) {
		return wrappedProposal.getSelection(arg0);
	}

}
