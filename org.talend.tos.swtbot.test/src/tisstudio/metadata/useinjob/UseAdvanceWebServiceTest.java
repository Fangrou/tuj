// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package tisstudio.metadata.useinjob;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.talend.swtbot.TalendSwtBotForTos;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.helpers.JobHelper;
import org.talend.swtbot.helpers.MetadataHelper;
import org.talend.swtbot.items.TalendJobItem;
import org.talend.swtbot.items.TalendWebServiceItem;

/**
 * DOC fzhong class global comment. Detailled comment
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UseAdvanceWebServiceTest extends TalendSwtBotForTos {

    private TalendJobItem jobItem;

    private TalendWebServiceItem webServiceItem;

    private static final String JOB_NAME = "jobTest";

    private static final String METADATA_NAME = "webServiceTest";

    @Before
    public void createJobAndMetadata() {
        jobItem = new TalendJobItem(JOB_NAME);
        jobItem.create();
        webServiceItem = new TalendWebServiceItem(METADATA_NAME);
        webServiceItem.setTypeAsAdvanced();
        webServiceItem.create();
    }

    @Test
    public void useMetadataInJob() throws IOException, URISyntaxException {
        Utilities.dndPaletteToolOntoJob(jobItem.getEditor(), "tFixedFlowInput", new Point(100, 100));
        SWTBotGefEditPart fixedFlowInput = getTalendComponentPart(jobItem.getEditor(), "tFixedFlowInput_1");
        Assert.assertNotNull("can not get component '" + webServiceItem.getComponentType() + "'", fixedFlowInput);
        jobItem.getEditor().click(fixedFlowInput);
        gefBot.viewByTitle("Component").setFocus();
        gefBot.tableInGroup("Mode").click(0, 1);
        gefBot.text().setText("test");

        Utilities.dndMetadataOntoJob(jobItem.getEditor(), webServiceItem.getItem(), "tWebService", new Point(300, 100));
        SWTBotGefEditPart webService = getTalendComponentPart(jobItem.getEditor(), webServiceItem.getItemName());
        Assert.assertNotNull("can not get component '" + webServiceItem.getComponentType() + "'", webService);

        JobHelper.connect(jobItem.getEditor(), fixedFlowInput, webService);
        JobHelper.connect2TLogRow(jobItem.getEditor(), webService, new Point(500, 100));

        JobHelper.runJob(jobItem.getEditor());

        String[] resultArray = { "Sunny", "Party Cloudy", "Rain", "Cloudy" };
        String results = "";
        for (int i = 0; i < resultArray.length; i++) {
            results += resultArray[i] + ",";
        }
        webServiceItem.setExpectResult(results);
        String actualResult = JobHelper.getExecutionResult();
        MetadataHelper.assertResult(actualResult, webServiceItem);
    }

    @After
    public void removePreviousCreateItems() {
        jobItem.getEditor().saveAndClose();
        Utilities.cleanUpRepository(jobItem.getParentNode());
        Utilities.cleanUpRepository(webServiceItem.getParentNode());
        Utilities.emptyRecycleBin();
    }
}
