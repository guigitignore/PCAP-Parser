public class TCPStreamManager extends TransportLayerStreamManager<TCP,TCPStream>{


    public TCPStreamManager(PCAP pcap) {
        super(pcap);
    }

  
    public void add(TCP segment){
        if (segment.getFlags().SYN && !segment.getFlags().ACK){
            addInANewStream(segment);
            return;
        }
        super.add(segment);
    }


    public TCPStream createNewStream(String sourceAddress, String destinationAddress) {
        return new TCPStream(sourceAddress,destinationAddress);
    }
    
}
