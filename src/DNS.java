import java.util.HashMap;
import java.util.HashSet;

public class DNS extends ApplicationProtocol{

    private boolean qr;

    private HashSet<DNSRecord> records;

    public abstract class DNSRecord{
        protected DNS protocol;
        protected int nameIndex;
        protected String recordName=null;
        protected String recordType=null;
        protected String recordClass=null;

        private static HashMap<Integer,String> recordTypeMap=new HashMap<>();

        static{
            recordTypeMap.put(1, "A");
            recordTypeMap.put(2, "NS");
            recordTypeMap.put(5, "CNAME");
            recordTypeMap.put(6, "SOA");
            recordTypeMap.put(12, "PTR");
            recordTypeMap.put(13, "HINFO");
            recordTypeMap.put(15, "MX");
            recordTypeMap.put(16, "TXT");
            recordTypeMap.put(17, "RP");
            recordTypeMap.put(18, "AFSDB");
            recordTypeMap.put(24, "SIG");
            recordTypeMap.put(25, "KEY");
            recordTypeMap.put(28, "AAAA");
            recordTypeMap.put(29, "LOC");
            recordTypeMap.put(33, "SRV");
            recordTypeMap.put(35, "NAPTR");
            recordTypeMap.put(36, "KX");
            recordTypeMap.put(37, "CERT");
            recordTypeMap.put(39, "DNAME");
            recordTypeMap.put(42, "APL");
            recordTypeMap.put(43, "DS");
            recordTypeMap.put(44, "SSHFP");
            recordTypeMap.put(45, "IPSECKEY");
            recordTypeMap.put(46, "RRSIG");
            recordTypeMap.put(47, "NSEC");
            recordTypeMap.put(48, "DNSKEY");
            recordTypeMap.put(49, "DHCID");
            recordTypeMap.put(50, "NSEC3");
            recordTypeMap.put(51, "NSEC3PARAM");
            recordTypeMap.put(52, "TLSA");
            recordTypeMap.put(53, "SMIMEA");
            recordTypeMap.put(55, "HIP");
            recordTypeMap.put(59, "CDS");
            recordTypeMap.put(60, "CDNSKEY");
            recordTypeMap.put(61, "OPENPGPKEY");
            recordTypeMap.put(62, "CSYNC");
            recordTypeMap.put(63, "ZONEMD");
            recordTypeMap.put(64, "SVCB");
            recordTypeMap.put(65, "HTTPS");
            recordTypeMap.put(108, "EUI48");
            recordTypeMap.put(109, "EUI64");
            recordTypeMap.put(249, "TKEY");
            recordTypeMap.put(250, "TSIG");
            recordTypeMap.put(252, "AXFR");
            recordTypeMap.put(256, "URI");
            recordTypeMap.put(257, "CAA");
            recordTypeMap.put(32768, "TA");
            recordTypeMap.put(32769, "DLV");
        }

        public DNSRecord(DNS protocol){
            this.protocol=protocol;
            protocol.addRecord(this);
        }

        public DNS getDNS(){
            return protocol;
        }

        public abstract boolean isDNSAnswer();

        public abstract void info();

        public int getNameIndex(){
            return nameIndex;
        }

        public String getRecordName(){
            return recordName;
        }

        public String getRecordType(){
            return recordType;
        }

        public String getRecordClass(){
            return recordClass;
        }

        protected String getRecordType(int recordType){
            return recordTypeMap.get(recordType);
        }

        protected String getRecordName(byte[] recordName){
            if (recordName==null) return null;

            String result=new String();
            PCAPBuffer localBuffer=new PCAPBuffer(recordName);
            
            while (true){
                short localLength=localBuffer.getUInt8();

                if (localLength>localBuffer.remaining()) return null;
                result+=new String(localBuffer.getBytes(localLength));

                if (localBuffer.remaining()==0) break;
                result+=".";
            }

            return result;
        }

        protected String getRecordClass(int recordClass){
            String[] availableClasses={"IN","CS","CH","HS"};
            String result;

            try{
                result=availableClasses[recordClass-1];
            }catch(IndexOutOfBoundsException e){
                result=null;
            }
            return result;
        }
    }

    public class DNSQuery extends DNSRecord{

        public DNSQuery(DNS protocol,PCAPBuffer buffer) throws ApplicationProtocolException{
            super(protocol);
            nameIndex=buffer.tell();

            recordName=getRecordName(buffer.readUntil((byte)0));
            if (recordName==null) throw new ApplicationProtocolException("Invalid qname in DNS query");

            if (buffer.remaining()<4) throw new ApplicationProtocolException("Invalid size in DNS query");

            recordType=getRecordType(buffer.getUInt16());
            if (recordType==null) throw new ApplicationProtocolException("Invalid qtype in DNS query");

            recordClass=getRecordClass(buffer.getUInt16());
            if (recordClass==null) throw new ApplicationProtocolException("Invalid qclass in DNS query");
        }

        public boolean isDNSAnswer(){
            return false;
        }

        @Override
        public void info() {
            System.out.println("Query: "+getRecordName()+" "+getRecordType()+" "+getRecordClass());
        }
        
    }

    public class DNSAnswer extends DNSRecord{

        private long ttl;
        private String data;

        public DNSAnswer(DNS protocol,PCAPBuffer buffer) throws ApplicationProtocolException {
            super(protocol);
            
            if (buffer.remaining()<14) throw new ApplicationProtocolException("Invalid size for DNS answer");
            if (buffer.getUInt8()!=0xc0) throw new ApplicationProtocolException("Not a DNS answer");

            int index=buffer.getUInt8();
            for (DNSRecord record:protocol.getRecords()){
                if (record.getNameIndex()==index){
                    recordName=record.getRecordName();
                    break;
                }
            }

            recordType=getRecordType(buffer.getUInt16());
            if (recordType==null) throw new ApplicationProtocolException("Invalid type in DNS answer");

            recordClass=getRecordClass(buffer.getUInt16());
            if (recordClass==null) throw new ApplicationProtocolException("Invalid class in DNS answer");

            ttl=buffer.getUInt32();

            int rdLength=buffer.getUInt16();
            if (buffer.remaining()<rdLength) throw new ApplicationProtocolException("Invalid rdlength in DNS answer");

            data=parseRData(buffer.createSubPCAPBuffer(rdLength));
        }

        private String parseRData(PCAPBuffer rdata){
            int length=rdata.remaining();
            switch (recordType){
                case "A":
                    if (length==4) return IPv4.getIPv4Address(rdata);
                    if (length==16) return IPv6.getIPv6Address(rdata);
                default:
                    return rdata.toHexString();
            }
        }

        @Override
        public boolean isDNSAnswer() {
            return true;
        }

        public long getTTL(){
            return ttl;
        }

        public String getData(){
            return data;
        }

        @Override
        public void info() {
            System.out.println("Answer: "+getRecordName()+" "+getRecordType()+" "+getRecordClass());
            System.out.println("Data: "+data);
        }
        
    }

    private int qdCount;
    private int anCount;

    public DNS(ITransportLayerProtocol segment) throws ApplicationProtocolException {
        super(segment);

        if (buffer.remaining()<12) throw new ApplicationProtocolException("DNS header length invalid");

        //int id=buffer.getUInt16();
        buffer.skipBytes(2);
        int flags=buffer.getUInt16();

        qr=(flags&0x8000)!=0;
        //int opCode=(flags>>11)&0xF;
        //boolean authritativeAnswer=(flags&0x400)!=0;
        //boolean truncation=(flags&0x200)!= 0;
        //boolean recursionDesired=(flags&0x100)!= 0;
        //boolean recursionAvailable =(flags&0x80)!=0;

        //int responseCode = flags & 0xF;

        qdCount=buffer.getUInt16();
        anCount=buffer.getUInt16();

        //int nsCount=buffer.getUInt16();
        buffer.skipBytes(2);
        //int arCount=buffer.getUInt16();
        buffer.skipBytes(2);

        records=new HashSet<>();

        for (int i=0;i<qdCount;i++) new DNSQuery(this, buffer);
        for (int i=0;i<anCount;i++) new DNSAnswer(this, buffer);
        
    }


    public DNSRecord[] getRecords(){
        DNSRecord[] result=new DNSRecord[records.size()];
        records.toArray(result);
        return result;
    }

    public void addRecord(DNSRecord record){
        if (record.getDNS()==this) records.add(record);
    }

    public int getQueryNumber(){
        return qdCount;
    }

    public int getAnswerNumber(){
        return anCount;
    }
    

    public boolean isDNSAnswer(){
        return qr;
    }

    @Override
    public String getTypeName() {
        return "DNS";
    }

    @Override
    public void info() {
        
        for (DNSRecord record:records){
            if (record.isDNSAnswer()==isDNSAnswer()){
                record.info();
            }
        }
    }

    
    
}
