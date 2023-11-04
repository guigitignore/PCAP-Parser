public abstract class ApplicationProtocol {
    protected PCAPBuffer buffer;
    protected ITransportLayerProtocol segment;


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
