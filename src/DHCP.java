import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class DHCP extends ApplicationProtocol {

    public final static int HeaderSize=236;
    
    private HashSet<DHCPOption> options; 

    public class DHCPOptionsCode{
        public final static int ClientId=61;
        public final static int MessageType=53;
        public final static int HostName=12;
        public final static int End=255;
        public final static int FQDN=81;
        public final static int VendorClassId=60;
        public final static int ParameterRequestList=55;
        public final static int ServerIdentifier=54;
        public final static int IPAddressLeaseTime=51;
        public final static int RenewalTimeValue=58;
        public final static int RebindingTimeValue=59;
        public final static int SubnetMask=1;
        public final static int DomainNameServer=6;
        public final static int DomainName=15;
        public final static int Router=3;
    }

    public abstract class DHCPOption{
        protected PCAPBuffer optionBuffer;

        public DHCPOption(PCAPBuffer optionBuffer){
            this.optionBuffer=optionBuffer;
        }

        public abstract int getCode();

        public abstract void info();

        public abstract String getCodeName();

    }

    public class DHCPClientID extends DHCPOption{
        private String hardwareAddress;

        public DHCPClientID(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            short addressType=optionBuffer.getUInt8();

            hardwareAddress=PCAPRecord.getHardwareAddress(optionBuffer, addressType);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.ClientId;
        }

        @Override
        public void info() {
            System.out.println("Hardware Address: "+hardwareAddress);
        }

        @Override
        public String getCodeName() {
            return "ClientId";
        }
        
    }

    public class DHCPMessageType extends DHCPOption{
        private String messageType;

        private static HashMap<Integer,String> messageTypeMap=new HashMap<>();

        static{
            messageTypeMap.put(1, "DISCOVER");
            messageTypeMap.put(2, "OFFER");
            messageTypeMap.put(3, "REQUEST");
            messageTypeMap.put(4, "DECLINE");
            messageTypeMap.put(5, "ACK");
            messageTypeMap.put(6, "NAK");
            messageTypeMap.put(7, "RELEASE");
            messageTypeMap.put(8, "INFORM");
            messageTypeMap.put(9, "FORCERENEW");
            messageTypeMap.put(10, "LEASEQUERY");
            messageTypeMap.put(11, "LEASEUNASSIGNED");
            messageTypeMap.put(12, "LEASEUNKNOWN");
            messageTypeMap.put(13, "LEASEACTIVE");
            messageTypeMap.put(14, "BULKLEASEQUERY");
            messageTypeMap.put(15, "LEASEQUERYDONE");
            messageTypeMap.put(16, "ACTIVELEASEQUERY");
            messageTypeMap.put(17, "LEASEQUERYSTATUS");
            messageTypeMap.put(18, "TLS");
        }

        public DHCPMessageType(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            messageType= messageTypeMap.get((int)optionBuffer.getUInt8());
            if (messageType==null) messageType="Unknown";
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.MessageType;
        }

        @Override
        public void info() {
            System.out.println("Message Type: "+messageType);
        }

        @Override
        public String getCodeName() {
            return "MessageType";
        }

    }

    public class DHCPHostName extends DHCPOption{
        private String hostname;

        public DHCPHostName(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            hostname=new String(optionBuffer.getBytes(optionBuffer.remaining()));
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.HostName;
        }

        @Override
        public void info() {
            System.out.println("Hostname: "+hostname);
        }

        @Override
        public String getCodeName() {
            return "HostName";
        }
        
    }

    public class DHCPEnd extends DHCPOption{

        public DHCPEnd(PCAPBuffer optionBuffer) {
            super(optionBuffer);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.End;
        }

        @Override
        public void info() {
            //nothing
        }

        @Override
        public String getCodeName() {
            return "End";
        }
        
    }

    //RFC 4702
    public class DHCPFQDN extends DHCPOption{
        private int flags;
        private String fqdn;

        public DHCPFQDN(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            flags=optionBuffer.getUInt8();
            optionBuffer.skipBytes(2); // rcode1+rcode2
            fqdn=new String(optionBuffer.getBytes(optionBuffer.remaining()));
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.FQDN;
        }

        @Override
        public void info() {
            System.out.println("Flags: "+String.format("0x%X", flags));
            System.out.println("FQDN: "+fqdn);
        }

        @Override
        public String getCodeName() {
            return "FQDN";
        }
        
    }

    public class DHCPVendorClassId extends DHCPOption{
        private String vendorId;

        public DHCPVendorClassId(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            vendorId=new String(optionBuffer.getBytes(optionBuffer.remaining()));
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.VendorClassId;
        }

        @Override
        public void info() {
            System.out.println("Vendor Class Id: "+vendorId);
        }

        @Override
        public String getCodeName() {
            return "Vendor Class Identifier";
        }
        
    }

    class DHCPParameterRequestList extends DHCPOption{
        private static HashMap<Integer,String> parameterListMap=new HashMap<>();

        static{
            parameterListMap.put(1, "Subnet Mask");
            parameterListMap.put(3, "Router");
            parameterListMap.put(6, "Domain Name Server");
            parameterListMap.put(42, "Network Time Protocol Servers");
            parameterListMap.put(50, "Requested IP Address");
            parameterListMap.put(51, "IP Address Lease Time");
            parameterListMap.put(53, "Message Type");
            parameterListMap.put(54, "Server Identifier");
            parameterListMap.put(55, "Parameter Request List");
            parameterListMap.put(58, "Renewal Time Value");
            parameterListMap.put(59, "Rebinding Time Value");
            parameterListMap.put(61, "Client Identifier");
            parameterListMap.put(15, "Domain Name");
            parameterListMap.put(31, "Perform Router Discover");
            parameterListMap.put(33, "Static Route");
            parameterListMap.put(43, "Vendor-Specific Information");
            parameterListMap.put(44, "NetBIOS over TCP/IP Name Server");
            parameterListMap.put(46, "NetBIOS over TCP/IP Node Type");
            parameterListMap.put(47, "NetBIOS over TCP/IP Scope");
            parameterListMap.put(119, "Domain Search");
            parameterListMap.put(121, "Classless Static Route");
            parameterListMap.put(249, "Private/Classless Static Route (Microsoft)");
            parameterListMap.put(252, "Private/Proxy autodiscovery");
            parameterListMap.put(255, "END");
        }

        private ArrayList<String> parameters;

        public DHCPParameterRequestList(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            parameters=new ArrayList<>();

            while (optionBuffer.hasRemaining()){
                int option=optionBuffer.getUInt8();
                String parameter=parameterListMap.get(option);
                if (parameter!=null) parameters.add(parameter);
            }
        }

        @Override
        public int getCode() {
           return DHCPOptionsCode.ParameterRequestList;
        }

        @Override
        public void info() {
            System.out.println("Parameters:");
            for (String parameter:parameters){
                System.out.println("- "+parameter);
            }
        }

        @Override
        public String getCodeName() {
            return "ParameterRequestList";
        }
        
    }

    public class DHCPServerIdentifier extends DHCPOption{
        private String ip;

        public DHCPServerIdentifier(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            ip=IPv4.getIPv4Address(optionBuffer);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.ServerIdentifier;
        }

        @Override
        public void info() {
            System.out.println("IP Address: "+ip);
        }

        @Override
        public String getCodeName() {
            return "ServerIdentifier";
        }
        
    }

    public class DHCPRenewalTimeValue extends DHCPOption{
        private long time;

        public DHCPRenewalTimeValue(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            time=optionBuffer.getUInt32();
            
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.RenewalTimeValue;
        }

        @Override
        public void info() {
            System.out.println("Time: "+time);
        }

        @Override
        public String getCodeName() {
           return "Renewal Time Value";
        }
        
    }

    public class DHCPRebindingTimeValue extends DHCPOption{
        private long time;

        public DHCPRebindingTimeValue(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            time=optionBuffer.getUInt32();
            
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.RebindingTimeValue;
        }

        @Override
        public void info() {
            System.out.println("Time: "+time);
        }

        @Override
        public String getCodeName() {
           return "Rebinding Time Value";
        }
        
    }

    public class DHCPIpLeaseTime extends DHCPOption{
        private long time;

        public DHCPIpLeaseTime(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            time=optionBuffer.getUInt32();
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.IPAddressLeaseTime;
        }

        @Override
        public void info() {
            System.out.println("Time: "+time);
        }

        @Override
        public String getCodeName() {
           return "Ip Lease Time";
        }
        
    }

    public class DHCPSubnetMask extends DHCPOption{
        private String ip;

        public DHCPSubnetMask(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            ip=IPv4.getIPv4Address(optionBuffer);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.SubnetMask;
        }

        @Override
        public void info() {
            System.out.println("IP Address: "+ip);
        }

        @Override
        public String getCodeName() {
            return "Subnet Mask";
        }
        
    }

    public class DHCPDomainNameServer extends DHCPOption{
        private String ip;

        public DHCPDomainNameServer(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            ip=IPv4.getIPv4Address(optionBuffer);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.DomainNameServer;
        }

        @Override
        public void info() {
            System.out.println("IP Address: "+ip);
        }

        @Override
        public String getCodeName() {
            return "Domain Name Server";
        }
        
    }

    public class DHCPDomainName extends DHCPOption{
        private String domainName;

        public DHCPDomainName(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            domainName=new String(optionBuffer.getBytes(optionBuffer.remaining()));
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.DomainName;
        }

        @Override
        public void info() {
            System.out.println("Domain Name: "+domainName);
        }

        @Override
        public String getCodeName() {
            return "Domain Name";
        }
        
    }

    public class DHCPRouter extends DHCPOption{
        private String ip;

        public DHCPRouter(PCAPBuffer optionBuffer) {
            super(optionBuffer);
            ip=IPv4.getIPv4Address(optionBuffer);
        }

        @Override
        public int getCode() {
            return DHCPOptionsCode.Router;
        }

        @Override
        public void info() {
            System.out.println("IP Address: "+ip);
        }

        @Override
        public String getCodeName() {
            return "Router";
        }
        
    }

    private LinkedHashMap<String,String> infos;
    private short op;

    public DHCP(ITransportLayerProtocol segment) throws ApplicationProtocolException {
        super(segment);

        if (buffer.remaining()<HeaderSize) throw new ApplicationProtocolException("Not a DHCP header");

        infos=new LinkedHashMap<>();
        op=buffer.getUInt8();
        if (op==1) infos.put("Type", "BOOTREQUEST");
        else if (op==2) infos.put("Type", "BOOTREPLY");
        else infos.put("Type", "Unknown");

        short htype=buffer.getUInt8();
        short hlen=buffer.getUInt8();
        short hops=buffer.getUInt8();

        long xid=buffer.getUInt32();

        int secs=buffer.getUInt16();
        int flags=buffer.getUInt16();

        PCAPBuffer ciaddr=buffer.createSubPCAPBuffer(4);
        infos.put("Client IP address", IPv4.getIPv4Address(ciaddr));

        PCAPBuffer yiaddr=buffer.createSubPCAPBuffer(4);
        infos.put("Your IP address", IPv4.getIPv4Address(yiaddr));

        PCAPBuffer siaddr=buffer.createSubPCAPBuffer(4);
        infos.put("Next Server IP address", IPv4.getIPv4Address(siaddr));

        PCAPBuffer giaddr=buffer.createSubPCAPBuffer(4);
        infos.put("Replay agent IP address", IPv4.getIPv4Address(giaddr));

        PCAPBuffer chaddr=buffer.createSubPCAPBuffer(16);
        infos.put("Client Hardware address", PCAPRecord.getHardwareAddress(chaddr, htype));

        PCAPBuffer sname=buffer.createSubPCAPBuffer(64);
        PCAPBuffer file=buffer.createSubPCAPBuffer(128);

        options=new HashSet<>();

        //if (buffer.remaining()<312) throw new ApplicationProtocolException("Insufficiant size for DHCP options"); //RFC 1541

        parseOptions(buffer.createSubPCAPBuffer(buffer.remaining()));

        
    }


    private void parseOptions(PCAPBuffer optionBuffer) throws ApplicationProtocolException{
        if (optionBuffer.getUInt32()!=0x63825363L) throw new ApplicationProtocolException("DHCP magic number not recognized");

        while (optionBuffer.remaining()>=2){
            int code=optionBuffer.getUInt8();
            short length=optionBuffer.getUInt8();

            if (optionBuffer.remaining()<length) throw new ApplicationProtocolException("Insufficiant space to read data of DHCP option");

            DHCPOption option=getOptionFromCode(code, optionBuffer.createSubPCAPBuffer(length));

            if (option!=null) options.add(option);
        }
    }

    private DHCPOption getOptionFromCode(int code,PCAPBuffer optionBuffer) throws ApplicationProtocolException{
        switch (code){
            case DHCPOptionsCode.ClientId:
                return new DHCPClientID(optionBuffer);
            case DHCPOptionsCode.MessageType:
                return new DHCPMessageType(optionBuffer);
            case DHCPOptionsCode.HostName:
                return new DHCPHostName(optionBuffer);
            case DHCPOptionsCode.End:
                return new DHCPEnd(optionBuffer);
            case DHCPOptionsCode.FQDN:
                return new DHCPFQDN(optionBuffer);
            case DHCPOptionsCode.VendorClassId:
                return new DHCPVendorClassId(optionBuffer);
            case DHCPOptionsCode.ParameterRequestList:
                return new DHCPParameterRequestList(optionBuffer);
            case DHCPOptionsCode.ServerIdentifier:
                return new DHCPServerIdentifier(optionBuffer);
            case DHCPOptionsCode.IPAddressLeaseTime:
                return new DHCPIpLeaseTime(optionBuffer);
            case DHCPOptionsCode.RenewalTimeValue:
                return new DHCPRenewalTimeValue(optionBuffer);
            case DHCPOptionsCode.RebindingTimeValue:
                return new DHCPRebindingTimeValue(optionBuffer);
            case DHCPOptionsCode.SubnetMask:
                return new DHCPSubnetMask(optionBuffer);
            case DHCPOptionsCode.DomainNameServer:
                return new DHCPDomainNameServer(optionBuffer);
            case DHCPOptionsCode.DomainName:
                return new DHCPDomainName(optionBuffer);
            case DHCPOptionsCode.Router:
                return new DHCPRouter(optionBuffer);
            default:
                return null;
        }

    }

    @Override
    public String getTypeName() {
        return "DHCP";
    }


    @Override
    public void info() {
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " : " + value);
        }

        System.out.println();

        int counter=1;
        for (DHCPOption option:options){
            System.out.println("Option "+counter+" (code "+option.getCode()+"):");
            System.out.println("Type: "+option.getCodeName());
            option.info();
            System.out.println();
            counter++;
        }
    }
    
}
