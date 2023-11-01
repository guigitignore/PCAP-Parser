public class UnknownLinkLayer implements ILinkLayer {
    private PCAPRecord record;

    UnknownLinkLayer(PCAPRecord record){
        this.record=record;
    }

    public String getTypeName() {
        return "<unknown>";
    }

    public void info(){
        System.out.println(getTypeName()+" Link layer. Code="+String.format("0x%X", record.getPCAP().getLinkLayerType()));
    }


    public PCAPRecord getPcapRecord() {
        return record;
    }
    
}
