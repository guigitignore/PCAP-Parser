public abstract class ApplicationProtocol {
    public final static int PROTOCOL_SUPPORTED=1;

    public class ProtocolType{
        public final static int HTTP=0;
    }

    protected PCAPBuffer buffer;
    protected ITransportLayerProtocol segment;

    public static ApplicationProtocol getProtocol(ITransportLayerProtocol segment){

        for (int i=0;i<PROTOCOL_SUPPORTED;i++){
            segment.getProtocolData().reset(); //reset index
            try{
                switch(i){
                    case ProtocolType.HTTP:
                        return new HTTP(segment);

                }
            }catch(ApplicationProtocolException e){
                //ignore
            }
        }

        return null;
    }


    public ApplicationProtocol(ITransportLayerProtocol segment){
        this.segment=segment;
        buffer=segment.getProtocolData();
    }

    public ITransportLayerProtocol getSegment(){
        return segment;
    }

    public abstract String getTypeName();

    public abstract void info();
}
