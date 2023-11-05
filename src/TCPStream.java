
public class TCPStream extends TransportLayerStream<TCP>{


    public TCPStream(String sourceAddress, String destinationAddress) {
        super(sourceAddress, destinationAddress);
    }

    public void info(){
        int counter=1;
        long baseAcknowlegmentNumber=0;
        long baseSequenceNumber=0;
        long relativeSequenceNumber=0;
        long relativeAcknowlegmentNumber=0;

        for (TCP segment:dialog){
            boolean same=segment.getSourceAddress().equals(getSourceAddress());
            TCP.TCPFlags flags=segment.getFlags();

            if (flags.SYN){
                if (flags.ACK){
                    baseAcknowlegmentNumber=segment.getSequenceNumber();
                }else{
                    baseSequenceNumber=segment.getSequenceNumber();
                }
            }else{
                if (segment.getFlags().ACK){
                    if (same){
                        relativeAcknowlegmentNumber=segment.getAcknowlegmentNumber()-baseAcknowlegmentNumber;
                    }else{
                        relativeAcknowlegmentNumber=segment.getAcknowlegmentNumber()-baseSequenceNumber;
                    }
                }
                if (same){
                    relativeSequenceNumber=segment.getSequenceNumber()-baseSequenceNumber;
                }else{
                    relativeSequenceNumber=segment.getSequenceNumber()-baseAcknowlegmentNumber;
                }

            }
            

            String title="TCP segment "+counter;

            if (baseSequenceNumber!=0) title+=" ,SEQ="+relativeSequenceNumber;
            if (baseAcknowlegmentNumber!=0)  title+=", ACK="+relativeAcknowlegmentNumber;

            System.out.println(title);
            segment.info();

            System.out.println();
            System.out.println();
            counter++;
        }
    }

}