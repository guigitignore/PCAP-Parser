import java.io.IOException;
import java.util.ArrayList;

public class PCAP {

    private PCAPBuffer fileBuffer;
    private int version_major;
    private int version_minor;
    private int thiszone;
    private long snaplen;
    private ArrayList<PCAPRecord> records;
    private int recordsNumber=0;
    private int failedRecordsNumber=0;
    private String filename;
    private long network;

    private TCPStreamManager tcpStreamManager;

    

    PCAP(String filename) throws PCAPException{
        if (!filename.endsWith(".pcap")) throw new PCAPException(filename + " is not a PCAP");

        try{
            fileBuffer=new PCAPBuffer(filename);
        }catch(IOException e){
            throw new PCAPException("Cannot read file "+ filename);
        }

        this.filename=filename;

        long magic=fileBuffer.getUInt32();

        if (magic==0xd4c3b2a1L){
            fileBuffer.setLittleEndian();
        }else if (magic!=0xa1b2c3d4L){
            throw new PCAPException("Bad magic number in "+filename);
        }

        version_major=fileBuffer.getUInt16();
        version_minor=fileBuffer.getUInt16();
        thiszone=fileBuffer.getInt32();
        //skip sigfigs field
        fileBuffer.skipBytes(4);

        snaplen=fileBuffer.getUInt32();
        network=fileBuffer.getUInt32();

        tcpStreamManager=new TCPStreamManager(this);
        records=new ArrayList<>();

        while (fileBuffer.hasRemaining()){
            recordsNumber++;
            try{
                PCAPRecord record=new PCAPRecord(this,recordsNumber);
                records.add(record);
            }catch(PCAPRecordException e){
                System.err.println("An error occured in record "+recordsNumber+"=> "+e.getMessage());
                failedRecordsNumber++;
            }
            
        }
        
    }

    public TCPStreamManager getTcpStreamManager(){
        return tcpStreamManager;
    }

    public PCAPBuffer getFileBuffer(){
        return fileBuffer;
    }

    public String getVersion(){
        return Integer.toString(version_major)+"."+Integer.toString(version_minor);
    }

    public int getTimeZoneCorrection(){
        return thiszone;
    }

    public long getRecordMaxLenght(){
        return snaplen;
    }

    public int getLinkLayerType(){
        return (int)network;
    }

    public String getFilename(){
        return filename;
    }

    public void info(){
        System.out.println("PCAP file :"+getFilename());
        System.out.println("PCAP version: "+getVersion());
        System.out.println("Link layer type: "+String.format("0x%X", getLinkLayerType()));
        System.out.println(recordsNumber+ " records read and "+ failedRecordsNumber+" failed");

        for (PCAPRecord record:records){
            System.out.println("--------------------------------------------------------------------");
            record.info();
            System.out.println("--------------------------------------------------------------------");
        }
    }

}
