package org.dcm4che2.tool.dcmsnd;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.ConfigurationException;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {

        String remoteAE = "STORESCP";
        String remotehost = "127.0.0.1";
        int port = 4444;
        String path = "5a7301a0e4b0b4037cf4bfec.dcm";



        DcmSnd dcmSnd = new DcmSnd();

        dcmSnd.setCalledAET(remoteAE);
        dcmSnd.setRemoteHost(remotehost);
        dcmSnd.setRemotePort(port);
        dcmSnd.addFile(new File(path));
        dcmSnd.configureTransferCapability();
        try {
            dcmSnd.initTLS();
        } catch (Exception var28) {
            System.err.println("ERROR: Failed to initialize TLS context:" + var28.getMessage());
            System.exit(2);
        }


        while(dcmSnd.getLastSentFile() < dcmSnd.getNumberOfFilesToSend()) {
        try {
            dcmSnd.start();
        } catch (Exception var27) {
            System.err.println("ERROR: Failed to start server for receiving Storage Commitment results:" + var27.getMessage());
            System.exit(2);
        }

        try {
           long t1 = System.currentTimeMillis();

            try {
                dcmSnd.open();
            } catch (Exception var25) {
                System.err.println("ERROR: Failed to establish association:" + var25.getMessage());
                System.exit(2);
            }

            long t2 = System.currentTimeMillis();
            System.out.println("Connected to " + remoteAE + " in " + (float)(t2 - t1) / 1000.0F + "s");
            t1 = System.currentTimeMillis();
            dcmSnd.send();
            t2 = System.currentTimeMillis();
            dcmSnd.prompt(dcmSnd, (float)(t2 - t1) / 1000.0F);
            DicomObject cmtrslt;
            if (dcmSnd.isStorageCommitment()) {
                t1 = System.currentTimeMillis();
                if (dcmSnd.commit()) {
                    t2 = System.currentTimeMillis();
                    System.out.println("Request Storage Commitment from " + remoteAE + " in " + (float)(t2 - t1) / 1000.0F + "s");
                    System.out.println("Waiting for Storage Commitment Result..");

                    try {
                        cmtrslt = dcmSnd.waitForStgCmtResult();
                        t1 = System.currentTimeMillis();
                        dcmSnd.promptStgCmt(cmtrslt, (float)(t1 - t2) / 1000.0F);
                    } catch (InterruptedException var24) {
                        System.err.println("ERROR:" + var24.getMessage());
                    }
                }
            }

            dcmSnd.close();
            System.out.println("Released connection to " + remoteAE);
            if (remoteStgCmtAE != null) {
                t1 = System.currentTimeMillis();

                try {
                    dcmSnd.openToStgcmtAE();
                } catch (Exception var23) {
                    System.err.println("ERROR: Failed to establish association:" + var23.getMessage());
                    System.exit(2);
                }

                t2 = System.currentTimeMillis();
                System.out.println("Connected to " + remoteStgCmtAE + " in " + (float)(t2 - t1) / 1000.0F + "s");
                t1 = System.currentTimeMillis();
                if (dcmSnd.commit()) {
                    t2 = System.currentTimeMillis();
                    System.out.println("Request Storage Commitment from " + remoteStgCmtAE + " in " + (float)(t2 - t1) / 1000.0F + "s");
                    System.out.println("Waiting for Storage Commitment Result..");

                    try {
                        cmtrslt = dcmSnd.waitForStgCmtResult();
                        t1 = System.currentTimeMillis();
                        dcmSnd.promptStgCmt(cmtrslt, (float)(t1 - t2) / 1000.0F);

                    } catch (InterruptedException var22) {
                        System.err.println("ERROR:" + var22.getMessage());
                    }
                }

                dcmSnd.close();
                System.out.println("Released connection to " + remoteStgCmtAE);
            }
        } finally {
            dcmSnd.stop();
        }
    }

    }
}
