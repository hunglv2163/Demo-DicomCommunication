
import org.dcm4che3.tool.storescp.StoreSCP;

public class test {
    public static void main(String[] args) {

        String[] abc = new String[2];
        abc[0] = "-b";
        abc[1] = "STORESCP:4444";
        StoreSCP.main(abc);
    }
}
