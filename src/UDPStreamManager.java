public class UDPStreamManager extends TransportLayerStreamManager<UDP,UDPStream>{

    public UDPStreamManager(PCAP pcap) {
        super(pcap);
    }

    @Override
    public UDPStream createNewStream(String sourceAddress, String destinationAddress) {
        return new UDPStream(sourceAddress, destinationAddress);
    }
    
}
