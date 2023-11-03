public class Ethernet extends LinkLayer {

    public static String getMacAddress(PCAPBuffer buffer){
        String[] bytes=new String[6];
        for (int i=0;i<6;i++) bytes[i]=String.format("%02x",buffer.getUInt8());
        return String.join(":",bytes);
    }

    public static String getProtocolAddress(PCAPBuffer buffer,int type){
        String result;

        switch(type){
            case EtherType.IPv4:
                result=IPv4.getIPv4Address(buffer);
                break;
            default:
                result=buffer.toHexString();
        }

        return result;
    }

    private String macDestination;
    private String macSource;
    private int protocolType;

    private PCAPBuffer protocolData;
    private EthernetProtocol protocol=null;
    private EthernetProtocolException exception=null;

    public class EtherType{
        public final static int IPv4=0x800;
        
        public final static int ARP=0x806;

        public final static int IPv6=0x86dd;

        public final static int ATA=0x88a2;
    }


    public Ethernet(PCAPRecord record){
        super(record);    

        macDestination=getMacAddress(buffer);
        macSource=getMacAddress(buffer);

        protocolType=buffer.getUInt16();

        protocolData=buffer.createSubPCAPBuffer(buffer.remaining());

        try{
            switch (protocolType){
                case EtherType.ARP:
                    protocol=new ARP(this);
                    break;
                case EtherType.IPv4:
                    protocol=new IPv4(this);
                    break;
                default:
                    throw new EthernetProtocolException("Unknown Ethernet protocol");
                
            }
        }catch(EthernetProtocolException e){
            exception=e;
        }
        

    }

    public String getTypeName() {
        return "Ethernet";
    }

    public String getSourceAddress(){
        return macSource;
    }

    public String getDestinationAddress(){
        return macDestination;
    }


    public int getProtocolType(){
        return protocolType;
    }

    public PCAPBuffer getProtocolData(){
        return protocolData;
    }

    public EthernetProtocol getProtocol(){
        return protocol;
    }


    public void info(){
        System.out.println("MAC source: "+getSourceAddress());
        System.out.println("MAC destination: "+getDestinationAddress());

        System.out.println();
        String hexa=String.format("0x%X",getProtocolType());

        if (exception==null){
            System.out.println(getProtocol().getTypeName()+" ("+hexa+"):");
            protocol.info();
        }else{
            System.out.println(getTypeName()+ " protocol "+hexa+":");
            System.out.println(exception.getMessage());
        }
    }

}


