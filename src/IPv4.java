public class IPv4 extends EthernetProtocol {
    public static String getIPv4Address(PCAPBuffer buffer){
        String[] bytes=new String[4];
        for (int i=0;i<4;i++) bytes[i]=Integer.toString(buffer.getUInt8());
        return String.join(".",bytes);
    }


    IPv4(Ethernet frame){
        super(frame);
    }

    public String getTypeName() {
        return "IPv4";
    }


    public void info() {
        
    }

    
    
}
