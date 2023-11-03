import java.nio.charset.StandardCharsets;

public class TCP extends Ipv4Protocol {

    private int sourcePort;
    private int destinationPort;
    private long sequenceNumber;
    private long acknowledgmentNumber;

    private boolean ACK;
    private boolean SYN;
    private boolean FIN;

    private PCAPBuffer protocolData;


    public TCP(IPv4 packet){
        super(packet);

        sourcePort=buffer.getUInt16();
        destinationPort=buffer.getUInt16();

        sequenceNumber=buffer.getUInt32();
        acknowledgmentNumber=buffer.getUInt32();

        int dataOffset=(buffer.getUInt8()&0xF0)>>2; //count directly the size of header in bytes (lword*4)
        
        short flags=buffer.getUInt8();

        ACK=(flags&0x10)!=0;
        SYN=(flags&0x2)!=0;
        FIN=(flags&0x1)!=0;

        int window=buffer.getUInt16();
        buffer.skipBytes(dataOffset-16); //we already read 16 bytes -> 4 lwords

        protocolData=buffer.createSubPCAPBuffer(buffer.remaining());


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

    public void info() {
        System.out.println("Source Port: "+sourcePort);
        System.out.println("Destination Port: "+destinationPort);
        System.out.println("Sequence number: "+sequenceNumber);
        System.out.println("Acknowledgement number: "+acknowledgmentNumber);
        System.out.println("ACK: "+ACK);
        System.out.println("SYN: "+SYN);
        System.out.println("FIN: "+FIN);
        System.out.println("DATA:"+new String(protocolData.getBytes(protocolData.remaining()),StandardCharsets.UTF_8));
    }
    
}
