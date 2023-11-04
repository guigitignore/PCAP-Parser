import java.util.ArrayList;

public class TCPStreamManager {
    private PCAP pcap;
    private ArrayList<ArrayList<TCP>> streams;


    public TCPStreamManager(PCAP pcap){
        this.pcap=pcap;
        streams=new ArrayList<>();
    }

    public TCP[][] getStreams(){
        TCP[][] result=new TCP[streams.size()][];
        streams.toArray(result);

        int counter=0;
        for (ArrayList<TCP> stream:streams){
            result[counter]=new TCP[stream.size()];
            stream.toArray(result[counter]);
            counter++;
        }

        return result;
    }

    public void add(TCP segment){
        PCAP currentPCAP=segment.getPacket().getFrame().getPcapRecord().getPCAP();
        //auto correction
        if (currentPCAP!=pcap){
            currentPCAP.getTcpStreamManager().add(segment);
            return;
        }

        if (segment.getFlags().SYN && !segment.getFlags().ACK){
           addInANewStream(segment);
        }

        String sourceAddress=segment.getSourceAddress();
        String destinationAddress=segment.getDestinationAddress();

        for (ArrayList<TCP> stream:streams.reversed()){
            TCP last=stream.getLast();
            String lastSourceAddress=last.getSourceAddress();
            String lastDestinationAddress=last.getDestinationAddress();

            if ((lastSourceAddress.equals(sourceAddress) && lastDestinationAddress.equals(destinationAddress)) ||
                (lastDestinationAddress.equals(sourceAddress) && lastSourceAddress.equals(destinationAddress))){
                    stream.add(segment);
                    return;
            }
        }

        addInANewStream(segment);
    }

    private void addInANewStream(TCP segment){
        ArrayList<TCP> newstream=new ArrayList<>();
        newstream.add(segment);
        streams.add(newstream);

    }

    public PCAP getPCAP(){
        return pcap;
    }


    public void info(){
        int streamCounter=1;
        for (ArrayList<TCP> stream:streams){
            String sourceAddress=stream.getFirst().getSourceAddress();
            String destinationAddress=stream.getFirst().getDestinationAddress();

            System.out.println("-----------------------------------------");
            System.out.println("TCP stream "+streamCounter+":");
            System.out.println("Stream segments: "+stream.size());
            System.out.println("Source Address: "+ sourceAddress);
            System.out.println("Destination Address: "+destinationAddress);
            System.out.println("-----------------------------------------");

            int segmentCounter=1;
            long baseAcknowlegmentNumber=0;
            long baseSequenceNumber=0;
            long relativeSequenceNumber=0;
            long relativeAcknowlegmentNumber=0;

            

            for (TCP segment:stream){
                if (segmentCounter==1){
                    baseAcknowlegmentNumber=segment.getAcknowlegmentNumber();
                    baseSequenceNumber=segment.getSequenceNumber();
                }else{
                    boolean same=segment.getSourceAddress().equals(sourceAddress);
                    if (segment.getFlags().ACK){
                        if (same){
                            relativeAcknowlegmentNumber=segment.getAcknowlegmentNumber()-baseAcknowlegmentNumber;
                        }else{
                            relativeAcknowlegmentNumber=segment.getAcknowlegmentNumber()-baseSequenceNumber;
                        }
                    }
                    if (!segment.getFlags().SYN){ 
                        if (same){
                            relativeSequenceNumber=segment.getSequenceNumber()-baseSequenceNumber;
                        }else{
                            relativeSequenceNumber=segment.getSequenceNumber()-baseAcknowlegmentNumber;
                        }
                    }
                }

                System.out.println("TCP segment "+segmentCounter+" ,SEQ="+relativeSequenceNumber+", ACK="+relativeAcknowlegmentNumber);
                segment.info();

                System.out.println();
                segmentCounter++;
            }

            System.out.println();
            streamCounter++;
        }
    }
}
