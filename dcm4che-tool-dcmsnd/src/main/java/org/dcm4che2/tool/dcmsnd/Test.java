package org.dcm4che2.tool.dcmsnd;

public class Test {
    public static void main(String[] args) {
        String[] para = new String [2];
        para[0] = "STORESCP@localhost:4444";
        para[1] = "5a7301a0e4b0b4037cf4bfec.dcm";

        DcmSnd.main(para);
    }
}
