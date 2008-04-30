// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.components.ecosystem.jobs;

import java.util.List;

/**
 * DOC hcw class global comment. Detailled comment
 */
public interface ISearchProvider<T> {

    void init() throws Exception;

    void addRecord(T bean, String key, String... fields) throws Exception;

    /**
     * Searches for the given string
     */
    List<T> search(String queryString) throws Exception;

}
