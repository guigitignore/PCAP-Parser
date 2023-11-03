public abstract class Ipv4Protocol {
    protected IPv4 packet;
    protected PCAPBuffer buffer;

    public Ipv4Protocol(IPv4 packet){
        this.packet=packet;
        buffer=packet.getProtocolData();
    }


    public IPv4 getPacket(){
        return packet;
    }

    public abstract String getTypeName();

    public abstract void info();
}
