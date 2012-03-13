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
package org.talend.swtbot.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.talend.swtbot.Utilities;

/**
 * DOC fzhong class global comment. Detailled comment
 */
public class TalendCopybookItem extends TalendMetadataItem {

    private String filePath;

    private String dataFile;

    public TalendCopybookItem() {
        super(Utilities.TalendItemType.COPYBOOK);
        setFilePath(System.getProperty("copybook.filepath"));
        setDataFile(System.getProperty("copybook.datafile"));
    }

    public TalendCopybookItem(String itemName) {
        super(itemName, Utilities.TalendItemType.COPYBOOK);
        setFilePath(System.getProperty("copybook.filepath"));
        setDataFile(System.getProperty("copybook.datafile"));
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public Map<String, TalendSchemaItem> retrieveSchema(String... schemaName) {
        Map<String, TalendSchemaItem> schemas = new HashMap<String, TalendSchemaItem>();

        getItem().contextMenu("Retrieve Schema").click();
        SWTBotShell shell = gefBot.shell("Schema").activate();
        try {
            gefBot.button("Next >").click();
            List<String> schemaNames = new ArrayList<String>(Arrays.asList(schemaName));
            for (String schema : schemaNames) {
                gefBot.tableInGroup("Select Schema to create").getTableItem(schema).check();
            }
            gefBot.button("Next >").click();
            gefBot.button("Finish").click();
            for (String schema : schemaNames) {
                TalendSchemaItem schemaItem = new TalendSchemaItem();
                schemaItem.setItem(getItem().getNode(schema));
                schemaItem.setParentNode(getItem());
                schemas.put(schema, schemaItem);
            }
        } catch (WidgetNotFoundException wnfe) {
            shell.close();
            Assert.fail(wnfe.getCause().getMessage());
        } catch (Exception e) {
            shell.close();
            Assert.fail(e.getMessage());
        }
        return schemas;
    }

    @Override
    public void create() {
        SWTBotShell shell = beginCreationWizard("Create EBCDIC", "EBCDIC Connection");
        try {
            gefBot.textWithLabel("File").setText(Utilities.getFileFromCurrentPluginSampleFolder(filePath).getAbsolutePath());
            gefBot.textWithLabel("Data file").setText(Utilities.getFileFromCurrentPluginSampleFolder(dataFile).getAbsolutePath());
            gefBot.button("Generate").click();
            gefBot.button("Next >").click();
        } catch (WidgetNotFoundException wnfe) {
            shell.close();
            Assert.fail(wnfe.getCause().getMessage());
        } catch (Exception e) {
            shell.close();
            Assert.fail(e.getMessage());
        }
        finishCreationWizard(shell);
    }

}
