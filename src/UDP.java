public class UDP extends IpProtocol implements ITransportLayerProtocol{

    private int sourcePort;
    private int destinationPort;
    private ApplicationProtocol protocol=null;
    private PCAPBuffer protocolData=null;

    public UDP(INetworkLayerProtocol packet) throws NetworkLayerException{
        super(packet);

        sourcePort=buffer.getUInt16();
        destinationPort=buffer.getUInt16();

        int lenght=buffer.getUInt16();
        if (lenght<8) throw new NetworkLayerException("Insuffisiant length for UDP segment");


        buffer.skipBytes(2); //checksum

        int dataLenght=lenght-8;
        if (dataLenght>buffer.remaining()) throw new NetworkLayerException("UDP data is not compleete");

        protocolData=buffer.createSubPCAPBuffer(dataLenght);

        packet.getFrame().getPcapRecord().getPCAP().getUpdStreamManager().add(this);

        protocol=ApplicationProtocol.getProtocol(this);
    }

    @Override
    public String getTypeName() {
        return "UDP";
    }

    @Override
    public int getSourcePort() {
        return sourcePort;
    }

    @Override
    public int getDestinationPort() {
        return destinationPort;
    }

    public String getSourceAddress(){
        return packet.getSourceAddress()+":"+Integer.toString(sourcePort);
    }

    public String getDestinationAddress(){
        return packet.getDestinationAddress()+":"+Integer.toString(destinationPort);
    }

    @Override
    public void info() {
        System.out.println("Source Port: "+sourcePort);
        System.out.println("Destination Port: "+destinationPort);
        if (protocol!=null){
            System.out.println();
            System.out.println(protocol.getTypeName()+ ":");
            protocol.info();
        } 
    }

    @Override
    public PCAPBuffer getProtocolData() {
       return protocolData;
    }

    @Override
    public ApplicationProtocol getProtocol() {
        return protocol;
    }
    
}
