import java.util.ArrayList;

public class TransportLayerStream<Protocol extends ITransportLayerProtocol>{

    protected ArrayList<Protocol> dialog;
    private String sourceAddress;
    private String destinationAddress;


    public TransportLayerStream(String sourceAddress,String destinationAddress){
        this.sourceAddress=sourceAddress;
        this.destinationAddress=destinationAddress;
        dialog=new ArrayList<>();
    }

    public boolean isPartOfStream(Protocol segment){
        return (segment.getSourceAddress().equals(sourceAddress) && segment.getDestinationAddress().equals(destinationAddress)) ||
                (segment.getDestinationAddress().equals(sourceAddress) && segment.getSourceAddress().equals(destinationAddress));
    }

    public void add(Protocol segment){
        dialog.add(segment);
    }

    public ApplicationProtocol getLastApplicationProtocol(){
        ApplicationProtocol result=null;
        for (int j=dialog.size()-1;j>=0;j--){
            result=dialog.get(j).getProtocol();
            if (result!=null) break;
        }
        return result;
    }

    public String getSourceAddress(){
        return sourceAddress;
    }

    public String getDestinationAddress(){
        return destinationAddress;
    }

    public int size(){
        return dialog.size();
    }

    public void info(){
        int counter=1;

        for (Protocol segment:dialog){
            System.out.println("Segment "+counter+":");
            segment.info();

            System.out.println();
            counter++;
        }
    }
}