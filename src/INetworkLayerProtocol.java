public interface INetworkLayerProtocol {
    public String getTypeName();

    public String getSourceAddress();

    public String getDestinationAddress();

    public void info();

    public Ethernet getFrame();

    public PCAPBuffer getProtocolData();

    public IpProtocol getProtocol();
}
