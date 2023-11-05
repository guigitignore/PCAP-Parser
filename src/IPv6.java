public class IPv6 extends EthernetProtocol {
    public static String getIPv6Address(PCAPBuffer buffer){
        String[] parts=new String[8];
        for (int i=0;i<8;i++) parts[i]=String.format("%04X",buffer.getUInt16());
        return String.join(".",parts);
    }


    public IPv6(Ethernet frame) {
        super(frame);
    }

    @Override
    public String getTypeName() {
       return "IPv6";
    }

    @Override
    public void info() {
        
    }
    
}
