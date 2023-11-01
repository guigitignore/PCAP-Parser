public class Main{
    public static void main(String[] args) {
        PCAP pcap=null;

        for (String arg:args){
            try{
                pcap=new PCAP(arg);
                pcap.info();
            }catch(PCAPException e){
                System.err.println(e.getMessage());
            }
            
        }
    }
}