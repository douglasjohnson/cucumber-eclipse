package cucumber.eclipse.editor.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import cucumber.eclipse.editor.Activator;
import cucumber.eclipse.editor.steps.ExtensionRegistryStepProvider;
import cucumber.eclipse.steps.integration.Step;
import gherkin.I18n;

public class GherkinKeywordsAssistProcessor implements IContentAssistProcessor {
	//TODO This line copy from i18n,because of private var.
	private static final List<String> FEATURE_ELEMENT_KEYWORD_KEYS = Arrays.asList("feature", "background", "scenario", "scenario_outline", "examples");
	private Editor editor;
	
    private final IContextInformation[] NO_CONTEXTS = {};
    private ICompletionProposal[] NO_COMPLETIONS = {};

    public final Image ICON = Activator.getImageDescriptor("icons/cukes.gif").createImage();

    public GherkinKeywordsAssistProcessor(Editor editor) {
    	this.editor = editor;
	}
    
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
            int offset) {
        try {
            // TODO listen some language changed event.
            IDocument document = viewer.getDocument();

            // TODO a service bean.
            String lang = DocumentUtil.getDocumentLanguage(document);
            if (lang == null) {
                lang = "en";
            }
            I18n i18n = new I18n(lang);
            
            // line of cursor locate,and from begin to cursor.
            IRegion line = viewer.getDocument().getLineInformationOfOffset(offset);
            String typed = viewer.getDocument().get(line.getOffset(), offset - line.getOffset()).replaceAll("^\\s+", "");
            
            List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

            if (typed.length() > 0) {
            	if (!doesLineStartWithKeyword(typed,i18n)) {
	                // all key words
	                List<String> keywords = allKeywords(i18n);
	                for (String string : keywords) {
	                    if (string.toLowerCase().startsWith(typed.toLowerCase()) && typed.indexOf(string) == -1) {
	                        CompletionProposal p = new CompletionProposal(string,
	                                offset - typed.length(), typed.length(),
	                                string.length(), ICON, null, null, null);
	                        result.add(p);
	                    }
	                }
            	} else {
	                IEditorInput input = editor.getEditorInput();
	                IFile featurefile = ((IFileEditorInput) input).getFile();
	                		
	                Set<Step> steps = new ExtensionRegistryStepProvider().getStepsInEncompassingProject(featurefile);
	                for (Step step: steps) {
	                	if (step.getContextHelpText().toLowerCase().startsWith(typed.toLowerCase().substring(typed.indexOf(" ")+1, typed.length()))) {
	                		int replacementOffset = offset;
	                		int keyWordLength = getKeywordAtBeginningOfLine(typed, i18n).length();
	                		int replacementLength = typed.length() - keyWordLength;
	                		replacementOffset = replacementOffset - replacementLength;
	                		
	                		CompletionProposal p = new CompletionProposal(step.getContextHelpText(), replacementOffset, replacementLength, step.getContextHelpText().length());
	                		GherkinCompletionProposal ccp = new GherkinCompletionProposal(p, step);
		                	result.add(ccp);
	                	}
	                }
            	}
            } else {
            	if (!doesLineStartWithKeyword(typed,i18n)) {
	                List<String> keywords = allKeywords(i18n);
	                for (String string : keywords) {
	                    CompletionProposal p = new CompletionProposal(string,
	                            offset, 0, string.length(), ICON, null, null, null);
	                    result.add(p);
	                }
            	} else {
                    IEditorInput input = editor.getEditorInput();
	                IFile featurefile = ((IFileEditorInput) input).getFile();
	                		
	                Set<Step> steps = new ExtensionRegistryStepProvider().getStepsInEncompassingProject(featurefile);
	                for (Step step: steps) {
		                    CompletionProposal p = new CompletionProposal(step.getContextHelpText(), offset, 0, step.getContextHelpText().length());            	
		                    GherkinCompletionProposal ccp = new GherkinCompletionProposal(p, step);
		                    result.add(ccp);
	              	}
            	}
            }
            
            Collections.sort(result, CompletionProposalComparator);
            
            return (ICompletionProposal[]) result
                    .toArray(new ICompletionProposal[result.size()]);
        } catch (Exception e) {
            // ... log the exception ...
            return NO_COMPLETIONS;
        }
    }

    private List<String> allKeywords(I18n i18n) {
        List<String> keywords = i18n.getStepKeywords();
        for (String string : FEATURE_ELEMENT_KEYWORD_KEYS) {
        	keywords.addAll(i18n.keywords(string));
        }
        return keywords;
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer,
            int offset) {
        return NO_CONTEXTS;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    public static Comparator<ICompletionProposal> CompletionProposalComparator = new Comparator<ICompletionProposal>() {

		@Override
		public int compare(ICompletionProposal o1, ICompletionProposal o2) {
			return o1.getDisplayString().compareTo(o2.getDisplayString());
		}
		
    };
    
    private boolean doesLineStartWithKeyword(String typed, I18n i18n) {
    	List<String> keywords = allKeywords(i18n);
        for (String string : keywords) {
            if (typed.startsWith(string)) {
            	return true;
            }
        }
        return false;
    }
    
    private String getKeywordAtBeginningOfLine(String typed, I18n i18n) {
    	List<String> keywords = allKeywords(i18n);
        for (String keyword : keywords) {
            if (typed.startsWith(keyword)) {
            	return keyword;
            }
        }
        return null;
    }
}
