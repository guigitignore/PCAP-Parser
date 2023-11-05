public abstract class IpProtocol {
    protected INetworkLayerProtocol packet;
    protected PCAPBuffer buffer;

    public IpProtocol(INetworkLayerProtocol packet){
        this.packet=packet;
        buffer=packet.getProtocolData();
    }


    public INetworkLayerProtocol getPacket(){
        return packet;
    }

    public abstract String getTypeName();

    public abstract void info();
}
