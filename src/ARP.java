public class ARP extends EthernetProtocol {

    private int hardwareAddressSpace;
    private int protocolAddressSpace;
    private int opCode;

    private String senderHardwareAddress;
    private String senderProtocolAddress;
    private String targetProtocolAddress;



    public class OpCode{
        public final static int REQUEST=1;
        public final static int REPLY=2;
    }


    ARP(Ethernet frame) throws EthernetProtocolException{
        super(frame);

        this.frame=frame;
        buffer=frame.getProtocolData();

        hardwareAddressSpace=buffer.getUInt16();
        protocolAddressSpace=buffer.getUInt16();

        short hardwareAddressLength=buffer.getUInt8();
        short protocolAddressLength=buffer.getUInt8();

        opCode=buffer.getUInt16();
        if (opCode!=OpCode.REQUEST && opCode!=OpCode.REPLY){
            throw new EthernetProtocolException("Invalid OpCode "+String.format("0x%04X", opCode)+"in ARP request");
        }

        senderHardwareAddress=PCAPRecord.getHardwareAddress(buffer.createSubPCAPBuffer(hardwareAddressLength),hardwareAddressSpace);  
        senderProtocolAddress=Ethernet.getProtocolAddress(buffer.createSubPCAPBuffer(protocolAddressLength),protocolAddressSpace); 

        buffer.skipBytes(hardwareAddressLength); //target hardware address not used
  
        targetProtocolAddress=Ethernet.getProtocolAddress(buffer.createSubPCAPBuffer(protocolAddressLength),protocolAddressSpace);

    }


    public String getTypeName() {
        return "ARP";
    }

    
    public void info() {
        if (opCode==OpCode.REQUEST){
            System.out.println("Who has "+targetProtocolAddress+"? Tell "+senderHardwareAddress);
        }else if (opCode==OpCode.REPLY){
            System.out.println(senderProtocolAddress+" is at "+senderHardwareAddress);
        }
    }

    
}
