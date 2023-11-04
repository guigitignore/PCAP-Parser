import java.util.ArrayList;

public class Main{

    public class PCAPSimpleOptions{
        public final static String followTCPStreams="f";
    }

    public class PCAPComplexOptions{
        public final static String followTCPStreams="follow-tcp-streams";
    }
    public static void main(String[] args) {
        ArrayList<PCAP> pcaps=new ArrayList<>();
        boolean followTCPStreams=false;

        for (String arg:args){
            if (arg.startsWith("-")){
                switch(arg.substring(1)){
                    case PCAPSimpleOptions.followTCPStreams:
                        followTCPStreams=true;
                        break;
                }
            }else if (arg.startsWith("--")){
                switch(arg.substring(2)){
                    case PCAPComplexOptions.followTCPStreams:
                        followTCPStreams=true;
                        break;
                }
            }else{
                try{
                    pcaps.add(new PCAP(arg));
                }catch(PCAPException e){
                    System.err.println(e.getMessage());
                }
            }
            
        }

        if (followTCPStreams){
            for (PCAP pcap:pcaps){
                pcap.getTcpStreamManager().info();
            }
        }else{
            for (PCAP pcap:pcaps){
                pcap.info();
            }
        }
    }
}