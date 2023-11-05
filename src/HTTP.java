import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTP extends ApplicationProtocol {

    private LinkedHashMap<String,String> infos;

    public HTTP(ITransportLayerProtocol segment) throws ApplicationProtocolException{
        super(segment);


        BufferedReader in=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.getBytes(buffer.remaining()))));

        try{
            String line=in.readLine();
            String[] lineArgs=line.split(" ");

            if (lineArgs.length!=3) throw new ApplicationProtocolException("Not HTTP");

            infos=new LinkedHashMap<>();

            if (lineArgs[0].startsWith("HTTP")){
                infos.put("Type", "HTTP reply");
                infos.put("Code",lineArgs[1]+" "+lineArgs[2]);
            }else{
                infos.put("Type", "HTTP request");
                String[] httpRequests={"GET","POST" ,"PUT","DELETE","HEAD","CONNECT","OPTION","PATCH","TRACE"};
                int index=Arrays.binarySearch(httpRequests, lineArgs[0]);

                if (index<0) throw new ApplicationProtocolException("Invalid HTTP request");

                infos.put("Request",lineArgs[0]+" "+ lineArgs[1]);
            }

            while (((line=in.readLine())!= null)){
                if (line.equals("")) break; //end of header
                lineArgs=line.split(":");
                if (lineArgs.length<2) break;

                infos.put(lineArgs[0].trim(),lineArgs[1].trim());
            }
            in.close();
        }catch(IOException e){
            throw new ApplicationProtocolException("Cannot read HTTP header");
        }
        
    }

    @Override
    public String getTypeName() {
        return "HTTP";
    }

    @Override
    public void info() {
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " : " + value);
        }
    }
    
}
