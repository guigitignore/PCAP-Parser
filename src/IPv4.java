public class IPv4 extends EthernetProtocol {
    public static String getIPv4Address(PCAPBuffer buffer){
        String[] bytes=new String[4];
        for (int i=0;i<4;i++) bytes[i]=Integer.toString(buffer.getUInt8());
        return String.join(".",bytes);
    }

    private IPv4Exception exception=null;
    private short protocolType;
    private short timeToLive;
    private boolean MF;
    private boolean DF;

    private int headerLenght;
    private int dataLenght;

    private String sourceAddress;
    private String destinationAddress;

    private PCAPBuffer protocolData;
    Ipv4Protocol protocol=null;

    public class Ipv4Type{
        public final static int ICMP=1;

        public final static int TCP=6;

        public final static int UDP=17;
    }

    IPv4(Ethernet frame) throws EthernetProtocolException{
        super(frame);

        short firstByte=buffer.getUInt8();
        int version=(firstByte&0xF0)>>4;
        int ihl=firstByte&0x0F;

        if (version!=4) throw new EthernetProtocolException("wrong version in IPv4 header");
        if (ihl<5) throw new EthernetProtocolException("IHL need to have a value greater or equal to 5");

        buffer.skipBytes(1); //type of service
        int totalLenght=buffer.getUInt16();

        headerLenght=ihl*4;
        dataLenght=totalLenght-headerLenght;

        if (buffer.remaining()<totalLenght-4) throw new EthernetProtocolException("IPv4 packet has an insufficiant size");

        buffer.skipBytes(2);

        int fragment=buffer.getUInt16();
        DF=(fragment&0x4000)!=0;
        MF=(fragment&0x2000)!=0;

        timeToLive=buffer.getUInt8();
        protocolType=buffer.getUInt8();

        buffer.skipBytes(2); //checksum

        sourceAddress=getIPv4Address(buffer.createSubPCAPBuffer(4));
        destinationAddress=getIPv4Address(buffer.createSubPCAPBuffer(4));

        buffer.skipBytes((ihl-5)*4); //options + padding

        protocolData=buffer.createSubPCAPBuffer(dataLenght);

        try{
            if (MF) throw new IPv4Exception("IP fragmentation not supported");
            
            switch(protocolType){
                case Ipv4Type.ICMP:
                    protocol=new ICMP(this);
                    break;
                case Ipv4Type.TCP:
                    protocol=new TCP(this);
                    break;
                case Ipv4Type.UDP:
                    protocol=new UDP(this);
                    break;
                default:
                    throw new IPv4Exception("Unknown IPv4 protocol");
            }

        }catch(IPv4Exception e){
            exception=e;
        }
    }

    public String getTypeName() {
        return "IPv4";
    }

    public String getSourceAddress(){
        return sourceAddress;
    }

    public String getDestinationAddress(){
        return destinationAddress;
    }

    public short getProtocolType(){
        return protocolType;
    }

    public Ipv4Protocol getProtocol(){
        return protocol;
    }

    public PCAPBuffer getProtocolData(){
        return protocolData;
    }


    public void info() {
        System.out.println("Header length: "+headerLenght);
        System.out.println("Data length: "+dataLenght);
        System.out.println("Don't fragment:"+DF);
        System.out.println("More fragment:"+MF);
        System.out.println("Time To Live (TTL): "+timeToLive);
        System.out.println("Source address: " +sourceAddress);
        System.out.println("Destination address: "+destinationAddress);
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
