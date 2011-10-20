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
package org.talend.swtbot.helpers;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.talend.swtbot.DndUtil;
import org.talend.swtbot.Utilities;

/**
 * DOC fzhong class global comment. Detailled comment
 */
public class JobHelper implements Helper {

    private static String executionResult;

    /**
     * DOC fzhong Comment method "filterStatistics". Filter all statistics for the execution result.
     * 
     * @param execution The result string in execution console
     * @return A string between statistics tags
     */
    public static String filterStatistics(String execution) {
        int statConnected = execution.indexOf("[statistics] connected");
        int statDisconnected = execution.indexOf("[statistics] disconnected");
        if (statConnected == -1 || statDisconnected == -1)
            Assert.fail(execution);
        return execution.substring(statConnected + "[statistics] connected".length(), statDisconnected).trim();
    }

    public static void runJob(SWTBotGefEditor jobEditor) {
        String[] array = jobEditor.getTitle().split(" ");
        String jobName = array[1];
        // String jobVersion = array[2];
        runJob(jobName);
    }

    public static void runJob(String jobName) {
        GEFBOT.viewByTitle("Run (Job " + jobName + ")").setFocus();
        GEFBOT.button(" Run").click();

        try {
            GEFBOT.shell("Find Errors in Jobs").close();
        } catch (WidgetNotFoundException e) {
        }

        GEFBOT.waitUntil(new DefaultCondition() {

            public boolean test() throws Exception {
                return GEFBOT.button(" Run").isEnabled();
            }

            public String getFailureMessage() {
                return "job did not finish running";
            }
        }, 60000);

        executionResult = GEFBOT.styledText().getText();
    }

    public static String getExecutionResult() {
        return executionResult;
    }

    /**
     * Link input component to tLogRow.
     * 
     * @param jobEditor
     * @param component The input component
     * @param rowName The name of row in the context menu of component.
     * @param point Where to put component 'tLogRow'
     * @param tLogRowName Actual name of component 'tLogRow' in job.
     */
    public static void connect2TLogRow(SWTBotGefEditor jobEditor, SWTBotGefEditPart component, String rowName, Point point,
            String tLogRowName) {
        Utilities.dndPaletteToolOntoJob(jobEditor, "tLogRow", point);
        SWTBotGefEditPart tLogRow = UTIL.getTalendComponentPart(jobEditor, tLogRowName);
        Assert.assertNotNull("can not get component '" + tLogRowName + "'", tLogRow);
        connect(jobEditor, component, tLogRow, rowName);
    }

    /**
     * Link input component to tLogRow. "tLogRow_1" as default component name.
     * 
     * @param jobEditor
     * @param component The input component
     * @param rowName The name of row in the context menu of component.
     * @param point Where to put component 'tLogRow'
     */
    public static void connect2TLogRow(SWTBotGefEditor jobEditor, SWTBotGefEditPart component, String rowName, Point point) {
        connect2TLogRow(jobEditor, component, rowName, point, "tLogRow_1");
    }

    /**
     * Link input component to tLogRow. "tLogRow_1" as default component name. "Main" as default row name.
     * 
     * @param jobEditor
     * @param component The input component
     * @param point Where to put component 'tLogRow'
     */
    public static void connect2TLogRow(SWTBotGefEditor jobEditor, SWTBotGefEditPart component, Point point) {
        connect2TLogRow(jobEditor, component, "Main", point);
    }

    /**
     * Link input component to tLogRow. "Main" as default row name.
     * 
     * @param jobEditor
     * @param component The input component
     * @param point Where to put component 'tLogRow'
     * @param tLogRowName Actual name of component 'tLogRow' in job.
     */
    public static void connect2TLogRow(SWTBotGefEditor jobEditor, SWTBotGefEditPart component, Point point, String tLogRowName) {
        connect2TLogRow(jobEditor, component, "Main", point, tLogRowName);
    }

    public static void connect(SWTBotGefEditor jobEditor, SWTBotGefEditPart sourceComponent, SWTBotGefEditPart targetComponent,
            String rowName) {
        jobEditor.select(sourceComponent).setFocus();
        jobEditor.clickContextMenu("Row").clickContextMenu(rowName);
        jobEditor.click(targetComponent);
        jobEditor.save();
    }

    public static void connect(SWTBotGefEditor jobEditor, SWTBotGefEditPart sourceComponent, SWTBotGefEditPart targetComponent) {
        connect(jobEditor, sourceComponent, targetComponent, "Main");
    }

    /**
     * Helper method for activate components from palette to job. Same as
     * <code>jobEditor.activateTool(toolLabel).click(locationOnJob.x, locationOnJob.y);</code>
     * 
     * @param jobEditor
     * @param toolLabel
     * @param locationOnJob
     */
    public void activateTool(SWTBotGefEditor jobEditor, String toolLabel, Point locationOnJob) {
        GEFBOT.viewByTitle("Palette").setFocus();
        GEFBOT.textWithTooltip("Enter component prefix or template (*, ?)").setText(toolLabel);
        GEFBOT.toolbarButtonWithTooltip("Search").click();
        GEFBOT.sleep(500);

        SWTBotGefFigureCanvas paletteFigureCanvas = new SWTBotGefFigureCanvas((FigureCanvas) GEFBOT.widget(WidgetOfType
                .widgetOfType(FigureCanvas.class)));
        SWTBotGefFigureCanvas jobFigureCanvas = new SWTBotGefFigureCanvas((FigureCanvas) GEFBOT.widget(
                WidgetOfType.widgetOfType(FigureCanvas.class), jobEditor.getWidget()));

        int x = 50;
        int y = 50;
        int folderLevel = 1;
        if (toolLabel.contains("tFile") || toolLabel.contains("tAdvanced"))
            folderLevel = 2;

        DndUtil dndUtil = new DndUtil(jobEditor.getWidget().getDisplay());
        dndUtil.dragAndDrop(paletteFigureCanvas, new Point(x, y + 20 * folderLevel), jobFigureCanvas, locationOnJob);
    }
}
