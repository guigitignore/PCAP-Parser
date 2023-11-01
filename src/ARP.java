public class ARP implements IEthernetProtocol {
    private Ethernet frame;
    private PCAPBuffer buffer;

    private int hardwareAddressSpace;
    private int protocolAddressSpace;
    private int opCode;

    private String senderHardwareAddress;
    private String senderProtocolAddress;
    private String targetHardwareAddress;
    private String targetProtocolAddress;



    public class OpCode{
        public final static int REQUEST=1;
        public final static int REPLY=2;
    }


    ARP(Ethernet frame){
        this.frame=frame;
        buffer=frame.getProtocolData();

        hardwareAddressSpace=buffer.getUInt16();
        protocolAddressSpace=buffer.getUInt16();

        short hardwareAddressLength=buffer.getUInt8();
        short protocolAddressLength=buffer.getUInt8();

        opCode=buffer.getUInt16();

        senderHardwareAddress=PCAPRecord.getHardwareAddress(buffer.createSubPCAPBuffer(hardwareAddressLength),hardwareAddressSpace);  
        senderProtocolAddress=PCAPRecord.getHardwareAddress(buffer.createSubPCAPBuffer(protocolAddressLength),protocolAddressSpace); 

        targetHardwareAddress=PCAPRecord.getHardwareAddress(buffer.createSubPCAPBuffer(hardwareAddressLength),hardwareAddressSpace);  
        targetProtocolAddress=PCAPRecord.getHardwareAddress(buffer.createSubPCAPBuffer(protocolAddressLength),protocolAddressSpace);

    }

    public Ethernet getFrame(){
        return frame;
    }


    public String getTypeName() {
        return "ARP";
    }

    
    public void info() {
        System.out.println("ARP:");
        System.out.println("opcode: "+String.format("%x",opCode));
        System.out.println("sender hardware address: "+senderHardwareAddress);
        System.out.println("sender protocol address: "+senderProtocolAddress);
        System.out.println("target hardware address: "+targetHardwareAddress);
        System.out.println("target protocol address: "+targetProtocolAddress);
    }

    
}
