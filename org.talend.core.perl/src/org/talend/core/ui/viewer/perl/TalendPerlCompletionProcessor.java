// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.core.ui.viewer.perl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.epic.core.model.ISourceElement;
import org.epic.core.util.PerlExecutor;
import org.epic.perleditor.PerlEditorPlugin;
import org.epic.perleditor.editors.perl.SourceParser;
import org.epic.perleditor.preferences.CodeAssistPreferences;
import org.epic.perleditor.templates.ContextType;
import org.epic.perleditor.templates.ContextTypeRegistry;
import org.epic.perleditor.templates.TemplateEngine;
import org.epic.perleditor.templates.perl.IPerlCompletionProposal;
import org.epic.perleditor.templates.perl.ModuleCompletionHelper;
import org.epic.perleditor.templates.perl.SubroutineEngine;
import org.epic.perleditor.templates.perl.VariableEngine;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.ui.proposal.IExternalProposals;
import org.talend.core.ui.proposal.ProposalFactory;
import org.talend.core.ui.viewer.proposal.TalendCompletionProposalComputer;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 */
public class TalendPerlCompletionProcessor implements IContentAssistProcessor {

    private static final IPerlCompletionProposal[] NO_PROPOSALS = new IPerlCompletionProposal[0];

    // package-scope to enable unit testing
    private static final Pattern MODULE_PREFIX_PATTERN = Pattern.compile("([A-Za-z0-9_]+(::|->))+"); //$NON-NLS-1$

    private static final Pattern VAR_PREFIX_PATTERN = Pattern.compile("\\$[A-Za-z0-9_]+(::|->)$"); //$NON-NLS-1$

    private final IContextInformationValidator fValidator = new Validator();

    private final TalendPerlCompletionProposalComparator fComparator = new TalendPerlCompletionProposalComparator();

    // private final TextEditor fTextEditor;
    private final TemplateEngine fTemplateEngine;

    public TalendPerlCompletionProcessor() {
        // fTextEditor = textEditor;
        //
        ContextType contextType = getContextType();
        if (contextType != null) {
            fTemplateEngine = new TemplateEngine(contextType);
        } else {
            fTemplateEngine = null;
        }
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
        // get the current document's text, up to caret position
        IDocument document = viewer.getDocument();
        String lastTextLine = null;
        try {
            int lastLine = document.getLineOfOffset(documentOffset);
            lastTextLine = document.get(document.getLineOffset(lastLine), document.getLineLength(lastLine));
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        String moduleNamePrefix = getModuleNamePrefix(lastTextLine);
        TalendCompletionProposalComputer computer = new TalendCompletionProposalComputer();
        List<ICompletionProposal> talendCompletionList = computer.computeCompletionProposals(new ContentAssistInvocationContext(
                viewer), new NullProgressMonitor());
        ICompletionProposal[] talendProposals = talendCompletionList
                .toArray(new ICompletionProposal[talendCompletionList.size()]);

        ICompletionProposal[] externalProposals = new ICompletionProposal[0];
        for (IExternalProposals externalProposal : ProposalFactory.getInstances()) {
            concatenate(externalProposal.getAdvancedProposals(documentOffset, "").toArray(new ICompletionProposal[0]), //$NON-NLS-1$
                    externalProposals);
        }

        if (moduleNamePrefix != null) {
            return sort(concatenate(talendProposals, computeModuleNameProposals(viewer, documentOffset, moduleNamePrefix)));
        } else {
            String className = getClassName(documentOffset, document);
            if (className != null) {
                ICompletionProposal[] methodProposals = computeMethodProposals(viewer, documentOffset, className);
                ICompletionProposal[] standardProposals = sort(concatenate(methodProposals, externalProposals));
                return sort(concatenate(talendProposals, standardProposals));
            } else {
                ICompletionProposal[] standardProposals = sort(concatenate(computeVariableProposals(viewer, documentOffset),
                        computeTemplateProposals(viewer, documentOffset)));
                standardProposals = sort(concatenate(standardProposals, externalProposals));
                return sort(concatenate(talendProposals, standardProposals));
            }
        }
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        String activationChars = PerlEditorPlugin.getDefault().getPreferenceStore().getString(
                CodeAssistPreferences.AUTO_ACTIVATION_CHARS);
        return activationChars.toCharArray();
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }

    public String getErrorMessage() {
        return null;
    }

    private ICompletionProposal[] computeMethodProposals(ITextViewer viewer, int documentOffset, String className) {
        List proposals = getProposalsForClassname(className);

        ContextType contextType = getContextType();
        if (contextType != null) {
            SubroutineEngine subroutineEngine = new SubroutineEngine(contextType);
            subroutineEngine.complete(viewer, documentOffset, (String[]) proposals.toArray(new String[proposals.size()]));
            return subroutineEngine.getResults();
        }
        return new IPerlCompletionProposal[0];
    }

    private ICompletionProposal[] computeModuleNameProposals(ITextViewer viewer, int documentOffset, String moduleNamePrefix) {
        ModuleCompletionHelper completionHelper = ModuleCompletionHelper.getInstance();
        return completionHelper.getProposals(moduleNamePrefix, documentOffset, viewer);
    }

    private IPerlCompletionProposal[] computeTemplateProposals(ITextViewer viewer, int documentOffset) {
        fTemplateEngine.reset();
        fTemplateEngine.complete(viewer, documentOffset);
        return fTemplateEngine.getResults();
    }

    private IPerlCompletionProposal[] computeVariableProposals(ITextViewer viewer, int documentOffset) {
        if (!PerlEditorPlugin.getDefault().getPreferenceStore().getBoolean(CodeAssistPreferences.INSPECT_VARIABLES)) {
            return NO_PROPOSALS;
        }

        ContextType contextType = getContextType();
        if (contextType == null) {
            return NO_PROPOSALS;
        }

        Set variables = getCompletionVariables(viewer, documentOffset);
        VariableEngine varsEngine = new VariableEngine(contextType);

        varsEngine.complete(viewer, documentOffset, (String[]) variables.toArray(new String[variables.size()]));

        return varsEngine.getResults();
    }

    /**
     * Gets variable/filedescriptor info from source file
     */
    private Set getCompletionVariables(ITextViewer viewer, int documentOffset) {
        List variablesModel = new ArrayList();
        Set variables = new HashSet();

        String variableChars = "%$@"; //$NON-NLS-1$
        String filehandleChars = "<"; //$NON-NLS-1$

        try {
            documentOffset = getCompletionStartOffset(viewer.getDocument(), documentOffset, variableChars + filehandleChars);

            if (documentOffset < viewer.getDocument().getLength()) {
                String key = viewer.getDocument().get(documentOffset, 1);
                if (variableChars.indexOf(key) != -1) {
                    variablesModel = SourceParser.getElements(viewer.getDocument(), "([$@%][a-z0-9A-Z_]+)\\s*[,)=;]", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            true);
                } else if (filehandleChars.indexOf(key) != -1) {
                    variablesModel = SourceParser.getElements(viewer.getDocument(),
                            "open[a-z]*\\s*?\\s*?[(]\\s*?([A-Z_0-9]+)\\s*?[,]", "<", ">", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }

            for (Iterator i = variablesModel.iterator(); i.hasNext();) {
                ISourceElement elem = (ISourceElement) i.next();
                String name = elem.getName();

                // Only insert variables once
                if (!variables.contains(name)) {
                    variables.add(name);

                    if (name.startsWith("@")) //$NON-NLS-1$
                        variables.add("$" + name.substring(1) + "[]"); //$NON-NLS-1$ //$NON-NLS-2$
                    else if (name.startsWith("%")) //$NON-NLS-1$
                        variables.add("$" + name.substring(1) + "{}"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } catch (BadLocationException ex) {
            // ex.printStackTrace(); // TODO log it
            ExceptionHandler.process(ex);
        }
        return variables;
    }

    private String getClassName(int documentOffset, IDocument document) {
        try {
            // Find offset just after the -> or :: for which completion is
            // requested
            documentOffset = getCompletionStartOffset(document, documentOffset, "_"); //$NON-NLS-1$

            String text = document.get(0, documentOffset);
            if (!text.endsWith("->") && !text.endsWith("::")) //$NON-NLS-1$ //$NON-NLS-2$
                return null;

            // Get the object name
            int lineNo = document.getLineOfOffset(documentOffset);
            String line = text.substring(document.getLineOffset(lineNo));

            // Lines can end with "->" of "::"
            if (VAR_PREFIX_PATTERN.matcher(line).find()) {
                String objName = line.substring(line.lastIndexOf('$'));

                objName = objName.indexOf("->") != -1 ? objName.substring(0, objName.indexOf("->")) : objName.substring(0, //$NON-NLS-1$ //$NON-NLS-2$
                        objName.indexOf("::")); //$NON-NLS-1$

                // **** Get the classname ***
                Pattern p = Pattern.compile("\\" + objName + "\\s*=\\s*([a-zA-Z:->]+)(->|::|;)"); //$NON-NLS-1$ //$NON-NLS-2$
                Matcher m = p.matcher(text);

                String className = null;
                while (m.find())
                    className = m.group(1);
                return className;
            } else {
                Matcher m = MODULE_PREFIX_PATTERN.matcher(line);
                if (m.find()) {
                    // strip -> or :: from the end
                    String str = m.group(0);
                    return str.substring(0, str.length() - 2);
                } else
                    return null;
            }
        } catch (Exception ex) {
            return null; // TODO error handling
        }
    }

    private List getProposalsForClassname(String className) {
        String perlCode = "use " + className + ";\n\n" + "foreach $name (sort keys %" + className + "::) {\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + " next if($name !~ /[a-z]/ || $name =~ /^_/);\n" + "   if(defined &{\"" + className + "::$name\"}) {\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "       print \"$name()\\n\";\n" + "   }\n" + "   else {\n" + "       #print \"$name\\n\";\n" + "   }\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "}\n"; //$NON-NLS-1$

        PerlExecutor executor = new PerlExecutor();
        // try {
        // // return executor.execute(fTextEditor, null, perlCode)
        // // .getStdoutLines();
        // } catch (CoreException e) {
        // PerlEditorPlugin.getDefault().getLog().log(e.getStatus());
        // return new ArrayList();
        // } finally {
        executor.dispose();
        // }

        return new ArrayList();
    }

    /**
     * Orders the given proposals.
     */
    private ICompletionProposal[] sort(ICompletionProposal[] proposals) {
        Arrays.sort(proposals, fComparator);
        return proposals;
    }

    /**
     * Finds the content assist start offset: the offset just after the sequence of characters triggering the
     * completion. For example, if the completion was requested after typing "$foo->ab", the method would return the
     * offset just after "$foo->".
     * 
     * @param document document in which completion was requested
     * @param caretOffset offset at which completion was requested
     * @return offset
     */
    private int getCompletionStartOffset(IDocument document, int caretOffset, String specialChars) throws BadLocationException {
        while (stepLeft(document, caretOffset, specialChars))
            caretOffset--;
        if (stepLeft(document, caretOffset, specialChars))
            caretOffset--;
        return caretOffset;
    }

    private ContextType getContextType() {
        return ContextTypeRegistry.getInstance().getContextType("perl"); //$NON-NLS-1$
    }

    private String getModuleNamePrefix(String line) {
        Pattern pattern = Pattern.compile(".*use\\s*(.*)$", Pattern.MULTILINE | Pattern.DOTALL); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(line);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private boolean stepLeft(IDocument doc, int offset, String specialChars) throws BadLocationException {
        return offset != 0
                && (Character.isUnicodeIdentifierPart(doc.getChar(offset - 1)) || specialChars.indexOf(doc.getChar(offset - 1)) != -1);
    }

    private static ICompletionProposal[] concatenate(ICompletionProposal[] a, ICompletionProposal[] b) {
        ICompletionProposal[] result = new ICompletionProposal[b.length + a.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(a, 0, result, b.length, a.length);
        return result;
    }

    /**
     * Simple content assist tip closer. The tip is valid in a range of 5 characters around its popup location.
     */
    private static class Validator implements IContextInformationValidator, IContextInformationPresenter {

        protected int fInstallOffset;

        public boolean isContextInformationValid(int offset) {
            return Math.abs(fInstallOffset - offset) < 5;
        }

        public void install(IContextInformation info, ITextViewer viewer, int offset) {
            fInstallOffset = offset;
        }

        public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
            return false;
        }
    }
}
