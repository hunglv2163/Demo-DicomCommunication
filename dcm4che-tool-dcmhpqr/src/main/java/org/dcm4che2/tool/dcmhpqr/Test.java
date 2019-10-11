package org.dcm4che2.tool.dcmhpqr;

import org.dcm4che2.tool.dcmqr.DcmQR;

public class Test {
    public static void main(String[] args) {
        String[] para = new String[2];
        para[0] = "STORESCP@localhost:4444";
        para[1] = "qStudyDate=20060204";
        DcmQR.main(para);
    }
}
