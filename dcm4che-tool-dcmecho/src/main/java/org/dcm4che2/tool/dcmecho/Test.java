package org.dcm4che2.tool.dcmecho;

import org.dcm4che2.net.ConfigurationException;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String[] para = new String[1];
        para[0] = "STORESCP@localhost:4444";
        DcmEcho.main(para);

//        String calledAET = "STORESCP";
//        String remoteSCP = "127.0.0.1";
//        int portSCP = 4444;
//        int repeat = 0;
//        boolean closeAssoc = true;
//        int interval = 500;
//
//        DcmEcho dcmEcho = new DcmEcho("DCMECHO");
//        dcmEcho.setCalledAET(calledAET,true);
//        dcmEcho.setRemoteHost(remoteSCP);
//        dcmEcho.setRemotePort(portSCP);
//        long t1 = System.currentTimeMillis();
//
//   /*     try {
//            dcmEcho.initTLS();
//        } catch (Exception var18) {
//            System.err.println("ERROR: Failed to initialize TLS context:" + var18.getMessage());
//            System.exit(2);
//        }*/
//        long t2 = System.currentTimeMillis();
//        //System.out.println("Initialize TLS context in " + (float)(t2 - t1) / 1000.0F + "s");*/
//
//        try {
//            dcmEcho.open();
//        } catch (Exception var17) {
//            System.err.println("ERROR: Failed to establish association:" + var17.getMessage());
//            System.exit(2);
//        }
//
//        while(true) {
//            try {
//                dcmEcho.echo();
//                long t3 = System.currentTimeMillis();
//                System.out.println("Perform Verification in " + (float)(t2 - t3) / 1000.0F + "s");
//                if (repeat == 0 || closeAssoc) {
//                    dcmEcho.close();
//                    System.out.println("Released connection to " + calledAET);
//                }
//                if (repeat-- == 0) {
//                    return;
//                }
//                Thread.sleep((long)interval);
//                long t4 = System.currentTimeMillis();
//                dcmEcho.open();
//                t2 = System.currentTimeMillis();
//                System.out.println("Reconnect or reuse connection to " + calledAET + " in " + (float)(t2 - t4) / 1000.0F + "s");
//            } catch (IOException var19) {
//                var19.printStackTrace();
//                System.exit(2);
//            } catch (InterruptedException var20) {
//                var20.printStackTrace();
//                System.exit(2);
//            } catch (ConfigurationException var21) {
//                var21.printStackTrace();
//                System.exit(2);
//            }
//        }
//
    }
}
