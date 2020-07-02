package org.taocaicai.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    /**
     * 标识数字
     */
    private int flag = 0;
    /**
     * 缓冲区大小
     */
    private int BLOCK = 4096;
    /**
     * 接受数据缓冲区
     */
    private ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
    /**
     * 发送数据缓冲区
     */
    private ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);

    private Selector selector;

    public NIOServer(int port) {
        try {

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));

            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server Start----9999");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handleKey(selectionKey);
            }
        }
    }

    private void handleKey(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;
        /**测试此键的通道是否已准备好接受新的套接字连接。*/
        if (selectionKey.isAcceptable()) {
            /** 返回为之创建此键的通道。*/
            server = (ServerSocketChannel) selectionKey.channel();
            /** 接受到此通道套接字的连接。*/
            /** 此方法返回的套接字通道（如果有）将处于阻塞模式。*/
            client = server.accept();
            /** 配置为非阻塞*/
            client.configureBlocking(false);
            /** 注册到selector，等待连接*/
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            /**返回为之创建此键的通道。*/
            client = (SocketChannel) selectionKey.channel();
            /**将缓冲区清空以备下次读取*/
            receivebuffer.clear();
            /**读取服务器发送来的数据到缓冲区中*/
            count = client.read(receivebuffer);
            if (count > 0) {
                receiveText = new String(receivebuffer.array(), 0, count);
                System.out.println("服务器端接受客户端数据--:" + receiveText);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            /**将缓冲区清空以备下次写入*/
            sendbuffer.clear();
            /**返回为之创建此键的通道*/
            client = (SocketChannel) selectionKey.channel();
            sendText = "message from server--" + flag++;
            /**向缓冲区中输入数据**/
            sendbuffer.put(sendText.getBytes());
            /**将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位*/
            sendbuffer.flip();
            /**输出到通道*/
            client.write(sendbuffer);
            System.out.println("服务器端向客户端发送数据--：" + sendText);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer(9999);
        nioServer.listen();
    }

}
