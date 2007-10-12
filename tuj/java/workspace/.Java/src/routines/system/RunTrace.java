// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2007 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package routines.system;

public class RunTrace implements Runnable {

    private class TraceBean {

        private String componentId;

        private int nbLine;

        public TraceBean(String componentId) {
            this.componentId = componentId;
        }

        public String getComponentId() {
            return this.componentId;
        }

        public void setComponentId(String componentId) {
            this.componentId = componentId;
        }

        public int getNbLine() {
            return this.nbLine;
        }

        public void setNbLine(int nbLine) {
            this.nbLine = nbLine;
        }
    }

    private static java.util.concurrent.ConcurrentHashMap<String, TraceBean> processTraces = new java.util.concurrent.ConcurrentHashMap<String, TraceBean>();

    private java.net.Socket s;

    private java.io.PrintWriter pred;

    private boolean jobIsFinished = false;

    private String str = "";

    public void startThreadTrace(String clientHost, int portTraces) throws java.io.IOException,
            java.net.UnknownHostException {
        System.out.println("[trace] connecting to socket on port " + portTraces);
        s = new java.net.Socket(clientHost, portTraces);
        pred = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(s.getOutputStream())),
                true);
        System.out.println("[trace] connected");
        Thread t = new Thread(this);
        t.start();

    }

    public void run() {
        synchronized (this) {
            try {
                while (!jobIsFinished) {
                    wait(100);
                }
            } catch (InterruptedException e) {
                System.out.println("[trace] interrupted");
            }
        }
    }

    public void stopThreadTrace() {
        jobIsFinished = true;
        try {
            pred.close();
            s.close();
            System.out.println("[trace] disconnected");
        } catch (java.io.IOException ie) {
        }
    }

    public void sendTrace(String componentId, String datas) {
        TraceBean bean;
        if (processTraces.containsKey(componentId)) {
            bean = processTraces.get(componentId);
        } else {
            bean = new TraceBean(componentId);
        }
        bean.setNbLine(bean.getNbLine() + 1);
        processTraces.put(componentId, bean);

        str = bean.getComponentId() + "|" + bean.getNbLine() + "|" + datas;
        pred.println(str); // envoi d'un message
    }
}
