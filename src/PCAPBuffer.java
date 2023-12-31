import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PCAPBuffer{
    private ByteBuffer buffer;

    PCAPBuffer(String filename) throws IOException{
        FileInputStream in=new FileInputStream(filename);
        buffer=ByteBuffer.wrap(in.readAllBytes());
        buffer.mark();
        in.close();
    }

    PCAPBuffer(byte[] bytes){
        buffer=ByteBuffer.wrap(bytes);
        buffer.mark();
    }

    public byte getInt8(){
        return buffer.get();
    }

    public short getUInt8(){
        return (short)(getInt8()&0xFF);
    }

    public short getInt16(){
        return buffer.getShort();
    }

    public int getUInt16(){
        return getInt16()&0xFFFF;
    }

    public int getInt32(){
        return buffer.getInt();
    }

    public long getUInt32(){
        return getInt32()&0xFFFFFFFFL;
    }

    public void setLittleEndian(){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setBigEndian(){
        buffer.order(ByteOrder.BIG_ENDIAN);
    }

    public byte[] getBytes(int number){
        byte[] bytes=new byte[number];
        buffer.get(bytes);
        return bytes;
    }

    public PCAPBuffer createSubPCAPBuffer(int lenght){
        return new PCAPBuffer(getBytes(lenght));
    }

    public void skipBytes(int number){
        buffer.position(buffer.position()+number);
    }

    public boolean hasRemaining(){
        return buffer.hasRemaining();
    }

    public int getLenght(){
        return buffer.capacity();
    }

    public int remaining(){
        return buffer.remaining();
    }

    public String toHexString(){
        int position=buffer.position();
        buffer.reset();
        String result=new String();
        while (remaining()!=0) result+=String.format("%02X",getUInt8());
        buffer.position(position);
        return result;
    }

    public byte[] readUntil(byte value){
        int initialPos=buffer.position();
        while (buffer.remaining()>0){
            if (getInt8()==value){
                int sizeToCopy=buffer.position()-initialPos;
                buffer.position(initialPos);
                return getBytes(sizeToCopy);
            }
        }
        buffer.position(initialPos);
        return null;
    }

    public void reset(){
        buffer.reset();
    }

    public int tell(){
        return buffer.position();
    }

}
