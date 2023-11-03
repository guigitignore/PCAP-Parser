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

    public class ICMPCode{
        public final static int NetUnreachable=0;
        public final static int HostUnreachable=1;
        public final static int ProtocolUnreachable=2;
        public final static int PortUnreachable=4;
        public final static int FragmentationNeededAndDFSet=5;
        public final static int SourceRouteFailed=6;
    }

    ICMP(IPv4 packet){
        super(packet);

        short type=buffer.getUInt8();
        short code=buffer.getUInt8();

        buffer.skipBytes(2); //checksum

        switch(type){
            
        }
    }

    public String getTypeName() {
       return "ICMP";
    }

    public void info() {
        
    }

    
}
