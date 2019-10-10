import org.dcm4che3.tool.storescp.StoreSCP;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String[] para = new String[2];
        para[0] = "-b";
        para[1] = "STORESCP:4444";
        StoreSCP.main(para);
    }
}
