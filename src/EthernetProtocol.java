public abstract class EthernetProtocol {
    protected Ethernet frame;
    protected PCAPBuffer buffer;

    public EthernetProtocol(Ethernet frame){
        this.frame=frame;
        buffer=frame.getProtocolData();
    }

    public abstract String getTypeName();

    public abstract void info();

    public Ethernet getFrame(){
        return frame;
    }
}
