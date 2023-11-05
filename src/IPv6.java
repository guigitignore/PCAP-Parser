public class IPv6 extends EthernetProtocol implements INetworkLayerProtocol{
    public static String getIPv6Address(PCAPBuffer buffer){
        String[] parts=new String[8];
        for (int i=0;i<8;i++) parts[i]=String.format("%04x",buffer.getUInt16());
        return String.join(":",parts);
    }

    public class Ipv6Type{
        public final static int ICMP=1;

        public final static int TCP=6;

        public final static int UDP=17;
    }

    private int payloadLength;
    private int hopLimit;
    private int protocolType;

    private IpProtocol protocol;
    private String sourceAddress;
    private String destinationAddress;

    private PCAPBuffer protocolData;
    private NetworkLayerException exception=null;

    public IPv6(Ethernet frame) throws EthernetProtocolException {
        super(frame);

        if (buffer.remaining()<40) throw new EthernetProtocolException("Invalid size for header of IPv6");

        long lword=buffer.getUInt32();

        long version=(lword>>28);
        if (version!=6L) throw new EthernetProtocolException("Wrong version if IP");

        payloadLength=buffer.getUInt16();
        protocolType=buffer.getInt8();
        hopLimit=buffer.getInt8();

        sourceAddress=getIPv6Address(buffer.createSubPCAPBuffer(16));
        destinationAddress=getIPv6Address(buffer.createSubPCAPBuffer(16));

        protocolData=buffer.createSubPCAPBuffer(payloadLength);

        try{
            switch(protocolType){
                case Ipv6Type.ICMP:
                    //not supported
                    break;
                case Ipv6Type.TCP:
                    protocol=new TCP(this);
                    break;
                case Ipv6Type.UDP:
                    protocol=new UDP(this);
                    break;
                default:
                    throw new NetworkLayerException("Unknown IPv6 protocol");
            }
        }catch(NetworkLayerException e){
            exception=e;
        }

    }

    @Override
    public String getTypeName() {
       return "IPv6";
    }

    public int getLength(){
        return payloadLength;
    }

    public int getHopLimit(){
        return hopLimit;
    }

    public String getSourceAddress(){
        return sourceAddress;
    }

    public String getDestinationAddress(){
        return destinationAddress;
    }

    public IpProtocol getProtocol(){
        return protocol;
    }

    public PCAPBuffer getProtocolData(){
        return protocolData;
    }

    public int getProtocolType(){
        return protocolType;
    }

    @Override
    public void info() {
        System.out.println("Data length: "+getLength());
        System.out.println("Hop Limit: "+getHopLimit());
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
