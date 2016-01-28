package cucumber.eclipse.steps.integration;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;

public class Step {

    private String text;
    private String fileLocation;
    private String contextHelpText;
    private IResource source;
    private int lineNumber;
    private String lang;
    private Pattern compiledText;
    private String[] parameters;
    private static final int PARAMETER_REGEX_LENGTH = 8;

    public String getText() {
        return text;
    }

    public String getContextHelpText() {
        return contextHelpText;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation.replace("." + source.getFileExtension(), "");
    }

    public void setText(String text) {
        this.text = text;
        this.contextHelpText = this.text.replace("^", "").replace("$", "");
        this.contextHelpText = replaceParameters(this.contextHelpText);
        this.compiledText = Pattern.compile(text);
    }

    private String replaceParameters(String annotation) {
        String annotationWithParameters = annotation;
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
        return "\"<" + formattedParameter + ">\"";
    }

    public IResource getSource() {
        return source;
    }

    public void setSource(IResource source) {
        this.source = source;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean matches(String s) {
        return compiledText.matcher(s).matches();
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Step [text=" + text + ", source=" + source + ", lineNumber=" + lineNumber + "]";
    }
}
