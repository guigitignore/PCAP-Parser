import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ICMP extends Ipv4Protocol{

    public class ICMPType{
        public final static int DestinationUnreachable=3;
        public final static int TimeExeeded=11;
        public final static int ParameterProblem=12;
        public final static int SourceQuench=4;
        public final static int Redirect=5;
        public final static int Echo=8;
        public final static int EchoReply=0;
        public final static int TimeStamp=13;
        public final static int TimeStampReply=14;
        public final static int InformationRequest=15;
        public final static int InformationReply=16;
    }

    private LinkedHashMap<String,String> infos;


    ICMP(IPv4 packet){
        super(packet);
        infos=new LinkedHashMap<>();

        short type=buffer.getUInt8();
        short code=buffer.getUInt8();

        buffer.skipBytes(2); //checksum

        switch(type){
            case ICMPType.DestinationUnreachable:
                infos.put("Type","Destination Unreachable");
                switch(code){
                    case 0:
                        infos.put("Code", "Net Unreachable");
                        break;
                    case 1:
                        infos.put("Code", "Host Unreachable");
                        break;
                    case 2:
                        infos.put("Code", "Protocol Unreachable");
                        break;
                    case 4:
                        infos.put("Code", "Port Unreachable");
                        break;
                    case 5:
                        infos.put("Code", "Fragmentation Needed and DF Set");
                        break;
                    case 6:
                        infos.put("Code", "Source Route Failed");
                        break;
                    default:
                        infos.put("Code", "Unknown Code");
                        break;
                }
                

                break;
            case ICMPType.TimeExeeded:
                infos.put("Type","Time Exceeded");
                if (code==0) infos.put("Code","time to live exceeded in transit");
                else if (code==1) infos.put("Code","fragment reassembly time exceeded");
                else infos.put("Code", "Unknown Code");
                break;
            case ICMPType.ParameterProblem:
                infos.put("Type","Parameter Problem");
                if (code==0) infos.put("Error position", String.format("%d",buffer.getUInt8()));
                else infos.put("Code", "Unknown Code"); 
                break;

            case ICMPType.SourceQuench:
                infos.put("Type","Source Quench");
                break;
            
            case ICMPType.Redirect:
                infos.put("Type","Redirect");
                switch(code){
                    case 0:
                        infos.put("Code", "Redirect datagrams for the Network");
                        break;
                    case 1:
                        infos.put("Code", "Redirect datagrams for the Host");
                        break;
                    case 2:
                        infos.put("Code", "Redirect datagrams for the Type of Service and Network");
                        break;
                    case 3:
                        infos.put("Code", "Redirect datagrams for the Type of Service and Host");
                        break;
                    default:
                        infos.put("Code", "Unknown Code");
                        break;
                }
                infos.put("Gateway Address",IPv4.getIPv4Address(buffer.createSubPCAPBuffer(4)));
                break;
            case ICMPType.Echo:
                infos.put("Type", "Echo");
            case ICMPType.EchoReply:
                infos.putIfAbsent("Type", "Echo Reply");
                infos.put("Request Id", String.format("0x%04X",buffer.getUInt16()));
                infos.put("Sequence Number", Integer.toString(buffer.getUInt16()));
                
                String message=new String(buffer.getBytes(buffer.remaining()),StandardCharsets.UTF_8);
                infos.put("Message", message);
                break;

            case ICMPType.TimeStamp:
                infos.put("Type", "Echo");
            case ICMPType.TimeStampReply:
                infos.putIfAbsent("Type", "Timestamp Reply");
                infos.put("Request Id", String.format("0x%04X",buffer.getUInt16()));
                infos.put("Sequence Number", Integer.toString(buffer.getUInt16()));
                
                infos.put("Originate Timestamp", Long.toString(buffer.getUInt32()));
                infos.put("Receive Timestamp", Long.toString(buffer.getUInt32()));
                infos.put("Transmit Timestamp", Long.toString(buffer.getUInt32()));
                break;
            case ICMPType.InformationRequest:
                infos.put("Type", "Information Request");
            case ICMPType.InformationReply:
                infos.putIfAbsent("Type", "Information Reply");
                infos.put("Request Id", String.format("0x%04X",buffer.getUInt16()));
                infos.put("Sequence Number", Integer.toString(buffer.getUInt16()));
                break;

        }
    }

    public String getTypeName() {
       return "ICMP";
    }

    public void info() {
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " : " + value);
        }
    }

    
}
