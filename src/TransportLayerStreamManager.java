import java.util.ArrayList;

public abstract class TransportLayerStreamManager<Protocol extends ITransportLayerProtocol,Stream extends TransportLayerStream<Protocol>>{
    
    protected PCAP pcap;
    protected ArrayList<Stream> streams;


    public TransportLayerStreamManager(PCAP pcap){
        this.pcap=pcap;
        streams=new ArrayList<>();
    }

    
    public PCAP getPCAP(){
        return pcap;
    }

    public abstract Stream createNewStream(String sourceAddress,String destinationAddress);

    public void addInANewStream(Protocol segment){
        Stream stream=createNewStream(segment.getSourceAddress(), segment.getDestinationAddress());
        stream.add(segment);
        streams.add(stream);
    }

    public void add(Protocol segment){
        for (int i=streams.size()-1;i>=0;i--){
            TransportLayerStream<Protocol> stream=streams.get(i);
            if (stream.isPartOfStream(segment)){
                stream.add(segment);
                return;
            }
        }
    }

    public void info(){
        int streamCounter=1;

        for (Stream stream:streams){
            System.out.println("-----------------------------------------");
            System.out.println("Stream "+streamCounter+":");
            System.out.println("Stream segments: "+stream.size());
            System.out.println("Source Address: "+ stream.getSourceAddress());
            System.out.println("Destination Address: "+stream.getDestinationAddress());
            System.out.println("-----------------------------------------");

            stream.info();

            System.out.println();
        }
    }


}
