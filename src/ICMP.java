public class ICMP extends Ipv4Protocol{
    ICMP(IPv4 packet){
        super(packet);

        short type=buffer.getUInt8();
        short code=buffer.getUInt8();

        buffer.skipBytes(2); //checksum
    }

    public String getTypeName() {
       return "ICMP";
    }

    public void info() {
        
    }

    
}
