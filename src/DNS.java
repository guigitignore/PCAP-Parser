import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DNS extends ApplicationProtocol{

    private LinkedHashMap<String,String> infos;
    private boolean qr;
    private HashMap<Integer,String> recordTypeMap;

    public DNS(ITransportLayerProtocol segment) throws ApplicationProtocolException {
        super(segment);

        if (buffer.remaining()<12) throw new ApplicationProtocolException("DNS header length invalid");

        int id=buffer.getUInt16();
        int flags=buffer.getUInt16();

        qr=(flags&0x8000)!=0;
        int opCode=(flags>>11)&0xF;
        boolean authritativeAnswer=(flags&0x400)!=0;
        boolean truncation=(flags&0x200)!= 0;
        boolean recursionDesired=(flags&0x100)!= 0;
        boolean recursionAvailable =(flags&0x80)!=0;

        int responseCode = flags & 0xF;

        int qdCount=buffer.getUInt16();
        int anCount=buffer.getUInt16();
        int nsCount=buffer.getUInt16();
        int arCount=buffer.getUInt16();

        infos=new LinkedHashMap<>();
        initRecordTypeMap();

        if (isDNSAnswer()){
            infos.put("Type","Response");

            String answerName=getRecordName(buffer.readUntil((byte)0));
            if (answerName==null) throw new ApplicationProtocolException("Invalid name in DNS answer");

            String answerType=getRecordType(buffer.getUInt16());
            if (answerType==null) throw new ApplicationProtocolException("Invalid type in DNS answer");

            String answerClass=getRecordClass(buffer.getUInt16());
            if (answerClass==null) throw new ApplicationProtocolException("Invalid class in DNS answer");

            //long ttl=buffer.getUInt32();
            

            infos.put("Answer",answerName+" "+answerType+" "+answerClass);
            infos.put("TTL",buffer.createSubPCAPBuffer(4).toHexString());

            int rdLength=buffer.getUInt16();
            if (buffer.remaining()<rdLength) throw new ApplicationProtocolException("Invalid rdlength in DNS answer");

            PCAPBuffer rdata=buffer.createSubPCAPBuffer(rdLength);
            String ipAddress=null;

            if (rdLength==4){
                ipAddress=IPv4.getIPv4Address(rdata);
            }else if (rdLength==16){
                ipAddress=IPv6.getIPv6Address(rdata);
            }else{
                infos.put("Rdata length", Integer.toString(rdLength));
            }

            if (ipAddress!=null) infos.put("IP",ipAddress);
        }else{
            infos.put("Type","Query");

            String queryName=getRecordName(buffer.readUntil((byte)0));
            if (queryName==null) throw new ApplicationProtocolException("Invalid qname in DNS query");

            String queryType=getRecordType(buffer.getUInt16());
            if (queryType==null) throw new ApplicationProtocolException("Invalid qtype in DNS query");

            String queryClass=getRecordClass(buffer.getUInt16());
            if (queryClass==null) throw new ApplicationProtocolException("Invalid qclass in DNS query");


            infos.put("Query",queryName+" "+queryType+" "+queryClass);
        }
        
    }

    
    private String getRecordClass(int recordClass){
        String[] availableClasses={"IN","CS","CH","HS"};
        String result;

        try{
            result=availableClasses[recordClass-1];
        }catch(IndexOutOfBoundsException e){
            result=null;
        }
        return result;
    }

    private String getRecordType(int recordType){
        return recordTypeMap.get(recordType);
    }

    private String getRecordName(byte[] recordName){
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

    public boolean isDNSAnswer(){
        return qr;
    }

    @Override
    public String getTypeName() {
        return "DNS";
    }

    @Override
    public void info() {
       for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " : " + value);
        }
    }

    private void initRecordTypeMap(){
        recordTypeMap = new HashMap<>();

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
        recordTypeMap.put(256, "URI");
        recordTypeMap.put(257, "CAA");
        recordTypeMap.put(32768, "TA");
        recordTypeMap.put(32769, "DLV");
    }
    
}
