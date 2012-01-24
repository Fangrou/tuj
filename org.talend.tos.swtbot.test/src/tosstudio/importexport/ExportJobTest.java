// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package tosstudio.importexport;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.items.TalendJobItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ExportJobTest extends TalendSwtBotForTos {

    private SWTBotShell shell;

    private TalendJobItem jobItem;

    private static final String JOBNAME = "test01"; //$NON-NLS-1$

    private static final String SAMPLE_RELATIVE_FILEPATH = "items.zip"; //$NON-NLS-1$

    private static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator"); // $NON-NLS-1$

    @Before
    public void createAJob() {
        jobItem = new TalendJobItem(JOBNAME);
        jobItem.create();
    }

    @Test
    public void exportJob() throws IOException, URISyntaxException {
        jobItem.getItem().contextMenu("Export Job").click();
        shell = gefBot.shell("Export Job").activate();
        gefBot.comboBoxWithLabel("To &archive file:").setText(
                Utilities.getFileFromCurrentPluginSampleFolder(SAMPLE_RELATIVE_FILEPATH).getParent() + FILE_SEPARATOR
                        + "output_job.zip");
        gefBot.button("Finish").click();

        gefBot.waitUntil(new DefaultCondition() {

            public boolean test() throws Exception {
                return Utilities.getFileFromCurrentPluginSampleFolder("output_job.zip").exists();
            }

            public String getFailureMessage() {
                return "could not find the exported file";
            }
        });
    }

    @After
    public void removePreviouslyCreateItems() throws IOException, URISyntaxException {
        shell.close();
        jobItem.getEditor().saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.emptyRecycleBin();
        Utilities.getFileFromCurrentPluginSampleFolder("output_job.zip").delete();
    }
}
