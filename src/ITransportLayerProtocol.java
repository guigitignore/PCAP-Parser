public interface ITransportLayerProtocol {
    public String getTypeName();

    public int getSourcePort();

    public int getDestinationPort();

    public String getSourceAddress();

    public String getDestinationAddress();

    public void info();

    public IPv4 getPacket();

    public PCAPBuffer getProtocolData();

    public ApplicationProtocol getProtocol();
}
