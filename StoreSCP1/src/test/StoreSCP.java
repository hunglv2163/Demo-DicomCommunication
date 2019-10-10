package org.dcm4che3.tool;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4che3.net.service.DicomServiceRegistry;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.AttributesFormat;
import org.dcm4che3.util.SafeClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 *
 */
public class StoreSCP {

    private static final Logger LOG = LoggerFactory.getLogger(StoreSCP.class);

    private static ResourceBundle rb =
            ResourceBundle.getBundle("messages");
    private static final String PART_EXT = ".part";

    private final Device device = new Device("storescp");
    private final ApplicationEntity ae = new ApplicationEntity("*");
    private final Connection conn = new Connection();
    private File storageDir;
    private AttributesFormat filePathFormat;
    private int status;
    private int responseDelay;
    private final BasicCStoreSCP cstoreSCP = new BasicCStoreSCP("*") {

        @Override
        protected void store(Association as, PresentationContext pc,
                             Attributes rq, PDVInputStream data, Attributes rsp)
                throws IOException {
            try {
                rsp.setInt(Tag.Status, VR.US, status);
                if (storageDir == null)
                    return;

                String cuid = rq.getString(Tag.AffectedSOPClassUID);
                String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
                String tsuid = pc.getTransferSyntax();
                File file = new File(storageDir, iuid + PART_EXT);
                try {
                    storeTo(as, as.createFileMetaInformation(iuid, cuid, tsuid),
                            data, file);
                    renameTo(as, file, new File(storageDir,
                            filePathFormat == null
                                    ? iuid
                                    : filePathFormat.format(parse(file))));
                } catch (Exception e) {
                    deleteFile(as, file);
                    throw new DicomServiceException(Status.ProcessingFailure, e);
                }
            } finally {
                if (responseDelay > 0)
                    try {
                        Thread.sleep(responseDelay);
                    } catch (InterruptedException ignore) {
                    }
            }
        }

    };

    public StoreSCP() throws IOException {
        device.setDimseRQHandler(createServiceRegistry());
        device.addConnection(conn);
        device.addApplicationEntity(ae);
        ae.setAssociationAcceptor(true);
        ae.addConnection(conn);
    }

    private void storeTo(Association as, Attributes fmi,
                         PDVInputStream data, File file) throws IOException  {
        LOG.info("{}: M-WRITE {}", as, file);
        file.getParentFile().mkdirs();
        DicomOutputStream out = new DicomOutputStream(file);
        try {
            out.writeFileMetaInformation(fmi);
            data.copyTo(out);
        } finally {
            SafeClose.close(out);
        }
    }

    private static void renameTo(Association as, File from, File dest)
            throws IOException {
        LOG.info("{}: M-RENAME {} to {}", as, from, dest);
        if (!dest.getParentFile().mkdirs())
            dest.delete();
        if (!from.renameTo(dest))
            throw new IOException("Failed to rename " + from + " to " + dest);
    }

    private static Attributes parse(File file) throws IOException {
        DicomInputStream in = new DicomInputStream(file);
        try {
            in.setIncludeBulkData(IncludeBulkData.NO);
            return in.readDataset(-1, Tag.PixelData);
        } finally {
            SafeClose.close(in);
        }
    }

    private static void deleteFile(Association as, File file) {
        if (file.delete())
            LOG.info("{}: M-DELETE {}", as, file);
        else
            LOG.warn("{}: M-DELETE {} failed!", as, file);
    }

    private DicomServiceRegistry createServiceRegistry() {
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(cstoreSCP);
        return serviceRegistry;
    }

    public void setStorageDirectory(File storageDir) {
        if (storageDir != null)
            storageDir.mkdirs();
        this.storageDir = storageDir;
    }

    public void setStorageFilePathFormat(String pattern) {
        this.filePathFormat = new AttributesFormat(pattern);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    private static CommandLine parseComandLine(String[] args)
            throws ParseException {
        Options opts = new Options();
        CLIUtils.addBindServerOption(opts);
        CLIUtils.addAEOptions(opts);
        CLIUtils.addCommonOptions(opts);
        addStatusOption(opts);
        addResponseDelayOption(opts);
        addStorageDirectoryOptions(opts);
        addTransferCapabilityOptions(opts);
        return CLIUtils.parseComandLine(args, opts, rb, StoreSCP.class);
    }

    @SuppressWarnings("static-access")
    private static void addStatusOption(Options opts) {
        opts.addOption(Option.builder()
                .hasArg()
                .argName("code")
                .desc(rb.getString("status"))
                .longOpt("status")
                .build());
    }

    private static void addResponseDelayOption(Options opts) {
        opts.addOption(Option.builder()
                .hasArg()
                .argName("ms")
                .desc(rb.getString("response-delay"))
                .longOpt("response-delay")
                .build());
    }

    @SuppressWarnings("static-access")
    private static void addStorageDirectoryOptions(Options opts) {
        opts.addOption(null, "ignore", false,
                rb.getString("ignore"));
        opts.addOption(Option.builder()
                .hasArg()
                .argName("path")
                .desc(rb.getString("directory"))
                .longOpt("directory")
                .build());
        opts.addOption(Option.builder()
                .hasArg()
                .argName("pattern")
                .desc(rb.getString("filepath"))
                .longOpt("filepath")
                .build());
    }

    @SuppressWarnings("static-access")
    private static void addTransferCapabilityOptions(Options opts) {
        opts.addOption(null, "accept-unknown", false,
                rb.getString("accept-unknown"));
        opts.addOption(Option.builder()
                .hasArg()
                .argName("file|url")
                .desc(rb.getString("sop-classes"))
                .longOpt("sop-classes")
                .build());
    }

    public static void main(String[] args) {
        try {
            CommandLine cl = parseComandLine(args);
            StoreSCP main = new StoreSCP();
            CLIUtils.configureBindServer(main.conn, main.ae, cl);
            CLIUtils.configure(main.conn, cl);
            main.setStatus(CLIUtils.getIntOption(cl, "status", 0));
            main.setResponseDelay(CLIUtils.getIntOption(cl, "response-delay", 0));
            configureTransferCapability(main.ae, cl);
            configureStorageDirectory(main, cl);
            ExecutorService executorService = Executors.newCachedThreadPool();
            ScheduledExecutorService scheduledExecutorService =
                    Executors.newSingleThreadScheduledExecutor();
            main.device.setScheduledExecutor(scheduledExecutorService);
            main.device.setExecutor(executorService);
            main.device.bindConnections();
        } catch (ParseException e) {
            System.err.println("storescp: " + e.getMessage());
            System.err.println(rb.getString("try"));
            System.exit(2);
        } catch (Exception e) {
            System.err.println("storescp: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void configureStorageDirectory(StoreSCP main, CommandLine cl) {
        if (!cl.hasOption("ignore")) {
            main.setStorageDirectory(
                    new File(cl.getOptionValue("directory", "")));
            if (cl.hasOption("filepath"))
                main.setStorageFilePathFormat(cl.getOptionValue("filepath"));
        }
    }

    private static void configureTransferCapability(ApplicationEntity ae,
                                                    CommandLine cl) throws IOException {
        if (cl.hasOption("accept-unknown")) {
            ae.addTransferCapability(
                    new TransferCapability(null,
                            "*",
                            TransferCapability.Role.SCP,
                            "*"));
        } else {
            Properties p = CLIUtils.loadProperties(
                    cl.getOptionValue("sop-classes",
                            "resource:sop-classes.properties"),
                    null);
            for (String cuid : p.stringPropertyNames()) {
                String ts = p.getProperty(cuid);
                TransferCapability tc = new TransferCapability(null,
                        CLIUtils.toUID(cuid),
                        TransferCapability.Role.SCP,
                        CLIUtils.toUIDs(ts));
                ae.addTransferCapability(tc);
            }
        }
    }

}
