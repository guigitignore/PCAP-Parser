public class IPv4 implements IEthernetProtocol {
    public static String getIPv4Address(PCAPBuffer buffer){
        String[] bytes=new String[4];
        for (int i=0;i<6;i++) bytes[i]=Integer.toString(buffer.getUInt8());
        return String.join(".",bytes);
    }

    private Ethernet frame;

    IPv4(Ethernet frame){
        this.frame=frame;
    }

    public String getTypeName() {
        return "IPv4";
    }

    public Ethernet getFrame() {
        return frame;
    }

    public void info() {
        
    }

    
    
}
