import java.nio.charset.StandardCharsets;

public class TCP extends Ipv4Protocol {

    private int sourcePort;
    private int destinationPort;
    private long sequenceNumber;
    private long acknowledgmentNumber;
    private long relativeSequenceNumber=0;
    private long relativeAcknowlegmentNumber=0;

    private PCAPBuffer protocolData=null;

    public class TCPFlags{
        public boolean URG;
        public boolean ACK;
        public boolean PSH;
        public boolean RST;
        public boolean SYN;
        public boolean FIN;
    }

    private TCPFlags flags;


    public TCP(IPv4 packet){
        super(packet);

        sourcePort=buffer.getUInt16();
        destinationPort=buffer.getUInt16();

        sequenceNumber=buffer.getUInt32();
        acknowledgmentNumber=buffer.getUInt32();

        int dataOffset=(buffer.getUInt8()&0xF0)>>2; //count directly the size of header in bytes (lword*4)
        
        short flagsValue=buffer.getUInt8();
        flags=new TCPFlags();

        flags.URG=(flagsValue&0x20)!=0;
        flags.ACK=(flagsValue&0x10)!=0;
        flags.PSH=(flagsValue&0x8)!=0;
        flags.RST=(flagsValue&0x4)!=0;
        flags.SYN=(flagsValue&0x2)!=0;
        flags.FIN=(flagsValue&0x1)!=0;

        buffer.skipBytes(2); //skip window size advertisement
        buffer.skipBytes(dataOffset-16); //we already read 16 bytes -> 4 lwords

        int dataSize=buffer.remaining();

        if (dataSize!=0) protocolData=buffer.createSubPCAPBuffer(buffer.remaining());

        packet.getFrame().getPcapRecord().getPCAP().getTcpStreamManager().add(this);
  

    }


    public String getTypeName() {
        return "TCP";
    }

    public int getSourcePort(){
        return sourcePort;
    }

    public int getDestinationPort(){
        return destinationPort;
    }

    public String getSourceAddress(){
        return packet.getSourceAddress()+":"+Integer.toString(sourcePort);
    }

    public String getDestinationAddress(){
        return packet.getDestinationAddress()+":"+Integer.toString(destinationPort);
    }

    public TCPFlags getFlags(){
        return flags;
    }

    public PCAPBuffer getProtocolData(){
        return protocolData;
    }

    public long getSequenceNumber(){
        return sequenceNumber;
    }

    public long getAcknowlegmentNumber(){
        return acknowledgmentNumber;
    }

    public long getRelativeSequenceNumber(){
        return relativeSequenceNumber;
    }

    public long getRelativeAcknowlegmentNumber(){
        return relativeAcknowlegmentNumber;
    }

    public void info() {
        System.out.println("Source Port: "+sourcePort);
        System.out.println("Destination Port: "+destinationPort);
        System.out.println("Sequence number: "+getSequenceNumber() +" / relative: "+getRelativeSequenceNumber());
        System.out.println("Acknowledgement number: "+getAcknowlegmentNumber() +" / relative: "+ getRelativeAcknowlegmentNumber());
        System.out.println("ACK: "+flags.ACK);
        System.out.println("SYN: "+flags.SYN);
        System.out.println("FIN: "+flags.FIN);
        if (protocolData!=null)
            System.out.println("DATA:"+new String(protocolData.getBytes(protocolData.remaining()),StandardCharsets.UTF_8));
    }
    
}
