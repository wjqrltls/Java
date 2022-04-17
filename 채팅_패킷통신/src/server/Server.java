package server;

import com.sun.xml.internal.bind.v2.model.core.ID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {

    private ServerSocket serverSocket;

    public static List<OutputStream> outPutList = new ArrayList<OutputStream>();

    List<String> IDcontainer = new ArrayList<String>();
    List<String> NAMEcontainer = new ArrayList<String>();
    List<String> ADDRcontainer = new ArrayList<String>();

    public void startServer(int PORT) {
        try {

            serverSocket = new ServerSocket(PORT);
            System.out.println("서버 소켓이 생성되었습니다.");

            while(true){
                Socket socket = serverSocket.accept();
                ADDRcontainer.add(socket.getInetAddress().toString());
                System.out.println("클라이언트 접속됨 : " + socket.getInetAddress().toString());
                Agent agent = new Agent(socket);
                new Thread(agent).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Agent implements Runnable{
        Socket socket;
        public Agent(Socket socket) {this.socket = socket;}

        @Override
        public void run() {
            try {
                startTalking(socket);
            }
            catch (Exception o){
                o.printStackTrace();
            }
        }
    }

    public void startTalking(Socket socket) {
        try {
            InputStream is = socket.getInputStream();

            byte[] bytes = new byte[4096];
            while(true){
                int length = is.read(bytes);
                String message = new String(bytes, 0, length);
                System.out.println("message : " + message);
                Command(message, socket);
                checkSocket(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Command(String s, Socket socket) throws IOException {
        OutputStream os = socket.getOutputStream();
        String command = s.substring(0, 2);
        int length = Integer.parseInt(s.substring(2, 6));
        String pl = s.substring(6, s.length());
        String message;

        if(command.equals("ID")){
            if(IDcontainer.contains(pl.substring(0, 4))){
                message = "DR0000";
                socket.getInetAddress();
                socket.getOutputStream().write(message.getBytes(UTF_8));
                ADDRcontainer.remove(socket.getInetAddress().toString());
                socket.close();
            }
            else{
                message = "JR" + String.format("%04d", pl.getBytes(UTF_8).length) + pl;
                //System.out.println(message);
                IDcontainer.add(pl.substring(0, 4));
                NAMEcontainer.add(pl.substring(4, pl.length()));
                outPutList.add(socket.getOutputStream());
                SendAll(message);
                String users = "방장 : ";
                int len = 0;
                for (int i = 0; i < IDcontainer.size(); i++) {
                    String temp = null;
                    temp = IDcontainer.get(i) + NAMEcontainer.get(i);
                    len += temp.getBytes(UTF_8).length;
                    users += temp + ",";
                }
                message = "UR" + String.format("%04d", len) + users;
                os.write(message.getBytes(UTF_8));
            }
        }
        else if(command.equals("GM")){
            String id = IDcontainer.get(ADDRcontainer.indexOf(socket.getInetAddress().toString()));
            message = "GR" + String.format("%04d", (id + pl).getBytes(UTF_8).length) + id + pl;
            SendAll(message);
        }
        else if(command.equals("SM")){
            String id = IDcontainer.get(ADDRcontainer.indexOf(socket.getInetAddress().toString()));
            message = "SR" + String.format("%04d", (id + pl).getBytes(UTF_8).length) + id + pl;
            outPutList.get(IDcontainer.indexOf(id)).write(message.getBytes(UTF_8));
        }
        else if(command.equals("WD")){
            if(ADDRcontainer.indexOf(socket.getInetAddress().toString()) == 0){
                String id = IDcontainer.get(ADDRcontainer.indexOf(socket.getInetAddress().toString()));
                outPutList.get(IDcontainer.indexOf(id)).write("WR0000".getBytes(UTF_8));
                IDcontainer.remove(id);
                NAMEcontainer.remove(NAMEcontainer.get(IDcontainer.indexOf(id)));
                SendAll("WA0000" + pl);
            }
        }
    }

    public void SendAll(String message) throws IOException {
        for(int i = 0; i < outPutList.size(); i++) {
            outPutList.get(i).write(message.getBytes(UTF_8));
        }
    }

    public void checkSocket(Socket socket) throws IOException {
        if(!socket.isConnected()){
            SendAll("DC0004" + IDcontainer.get(outPutList.indexOf(socket.getOutputStream())));
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(1200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
