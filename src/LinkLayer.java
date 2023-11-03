public abstract class LinkLayer {

    protected PCAPRecord record;
    protected PCAPBuffer buffer;
    
    public LinkLayer(PCAPRecord record){
        this.record=record;
        buffer=record.getRecordBuffer();
    }

    public abstract String getTypeName();

    public abstract void info();

    public PCAPRecord getPcapRecord(){
        return record;
    }
}