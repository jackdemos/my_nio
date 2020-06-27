package org.taocaicai.basedemo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BaseDemo {
    public static void main(String[] args) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("Y:/workspace/my_nio/my_nio/data.txt", "rw");
            FileChannel channel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(48);

            int bytesRead = channel.read(byteBuffer);
            while (bytesRead != -1) {
                System.out.println(bytesRead);
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    System.out.print((char) byteBuffer.get());
                }

                /** compact() method only clears the data which you have already read*/
                //byteBuffer.compact();

                /** clear() method clears the whole buffer*/
                byteBuffer.clear();
                bytesRead = channel.read(byteBuffer);
            }
            randomAccessFile.close();


        } catch (FileNotFoundException exception) {
            exception.getStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
