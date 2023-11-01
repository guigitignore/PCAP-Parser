import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PCAPRecord {
    private PCAP pcap;
    private long orig_len;
    private LocalDateTime time; 
    private PCAPBuffer recordBuffer;
    private int recordNumber;
    private ILinkLayer frame;

    public static String getHardwareAddress(PCAPBuffer buffer,int type){
        String result;

        switch(type){
            case LinkLayerType.ETHERNET:
                result=Ethernet.getMacAddress(buffer);
                break;
            default:
                result=buffer.toHexString();
            
        }

        return result;
    }

    public class LinkLayerType{
        public final static int ETHERNET=0x1;
    }


    PCAPRecord(PCAP pcap,int recordNumber) throws PCAPRecordException{
        this.pcap=pcap;
        this.recordNumber=recordNumber;
        
        PCAPBuffer fileBuffer=pcap.getFileBuffer();
        //read header

        long ts_sec=fileBuffer.getUInt32()+pcap.getTimeZoneCorrection();
        long ts_usec=fileBuffer.getUInt32();
        long incl_len=fileBuffer.getUInt32();
        orig_len=fileBuffer.getUInt32();

        Instant instant=Instant.ofEpochMilli(ts_sec*1000+ts_usec/1000);
        time=LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        recordBuffer=fileBuffer.createSubPCAPBuffer((int)orig_len);
        fileBuffer.skipBytes((int)(incl_len-orig_len));


        switch(pcap.getLinkLayerType()){
            case LinkLayerType.ETHERNET:
                frame=new Ethernet(this);
                break;
            default:
                frame=new UnknownLinkLayer(this);
        }

        
    }

    public PCAP getPCAP(){
        return pcap;
    }

    public PCAPBuffer getRecordBuffer(){
        return recordBuffer;
    }

    public ILinkLayer getFrame(){
        return frame;
    }

    public long getLength(){
        return orig_len;
    }

    public String getDate(){
        return time.toLocalDate().toString();
    }

    public String getTime(){
        return time.toLocalTime().toString();
    }

    public int getRecordNumber(){
        return recordNumber;
    }

    public void info(){
        System.out.println("PCAP record "+getRecordNumber()+":");
        System.out.println("Date: "+getDate());
        System.out.println("Time: "+getTime());
        System.out.println("Lenght:"+getLength());
        System.out.println();
        frame.info();
    }

}
