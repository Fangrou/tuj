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

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.talend.swtbot.Utilities;

/**
 * DOC fzhong class global comment. Detailled comment
 */
public class TalendFolderItem {

    private String itemName;

    private String itemPath; // folder path not include itself

    private String folderPath; // folder path include itself

    private SWTBotTreeItem item;

    private SWTBotTreeItem parentNode;

    private SWTGefBot gefBot = new SWTGefBot();

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Get the path of folder item, not include itself.
     * 
     * @return item path
     */
    public String getItemPath() {
        return itemPath;
    }

    /**
     * Set the path of folder item, not include itself.
     * 
     * @return item path
     */
    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    /**
     * Get the path of folder item, include itself.
     * 
     * @return folder path
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Set the path of folder item, include itself.
     * 
     * @return folder path
     */
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public SWTBotTreeItem getItem() {
        return item;
    }

    public void setItem(SWTBotTreeItem item) {
        this.item = item;
    }

    public SWTBotTreeItem getParentNode() {
        return parentNode;
    }

    public void setParentNode(SWTBotTreeItem parentNode) {
        this.parentNode = parentNode;
    }

    public void rename(String newFolderName) {
        parentNode.expandNode(itemName).contextMenu("Rename folder").click();
        SWTBotShell shell = gefBot.shell("New folder").activate();
        gefBot.textWithLabel("Label").setText(newFolderName);
        boolean isFinishButtonEnable = gefBot.button("Finish").isEnabled();
        if (!isFinishButtonEnable) {
            shell.close();
            Assert.assertTrue("folder name already exist", isFinishButtonEnable);
        }
        gefBot.button("Finish").click();

        SWTBotTreeItem newFolderItem = null;
        try {
            newFolderItem = parentNode.expand().select(newFolderName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Assert.assertNotNull("folder is renamed", newFolderItem);
        }
    }

    public void delete() {
        parentNode.expand().getNode(itemName).contextMenu("Delete").click();
        SWTBotTreeItem newItem = null;
        String path = "";
        try {
            if (itemPath != null)
                path = itemPath;
            path = " (" + path + ")";
            newItem = Utilities.getRepositoryTree().expandNode("Recycle bin").select(itemName + path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Assert.assertNotNull("item is not deleted to recycle bin", newItem);
        }
    }
}
