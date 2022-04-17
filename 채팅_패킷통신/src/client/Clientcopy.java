package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Clientcopy {

    private Socket socket;

    private InputStream is;

    private OutputStream os;

    private Scanner scanner;

    public String ID = null;

    private static final String SEVER_ADDRESS = "10.80.163.152";

    private static final int PORT = 1200;

    public void connect() throws IOException {
        socket = new Socket(SEVER_ADDRESS, PORT);
    }

    public void disconnect() throws Exception {
        if (is != null) {
            is.close();
        }

        if (os != null) {
            os.close();
        }

        if (socket != null) {
            socket.close();
        }
    }

    public void prepareTalking() throws Exception {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    public void SetID() throws Exception {
        String id;
        System.out.print("아이디와 이를을 입력 해주세요. : ");
        id = scanner.nextLine();
        String command = "ID" + String.format("%04d", id.getBytes(UTF_8).length);
        sendMessage(command + id);

        ID = id.substring(0, 4);
    }

    public void sendMessage(String message) throws Exception  {
        os.write(message.getBytes(UTF_8));
    }

    private class Agent implements Runnable{

        @Override
        public void run() {
            try {
                receiveMessage();
            }
            catch (Exception o){
                o.printStackTrace();
            }
        }
    }

    public void receiveMessage() throws Exception {
        byte[] buffer = new byte[4096];
        while (true){
            int length = is.read(buffer);

            String message = new String(buffer, 0, length);
            command(message);
        }
    }

    public void processUserInput() throws Exception {

        Agent a = new Agent();
        String message;
        scanner = new Scanner(System.in);

        while (true) {

            new Thread(a).start();

            if(ID == null){
                SetID();
            }
            else{
                message = scanner.next();
                if(message.equals("귓")){
                    message = scanner.next() + scanner.next();
                    sendMessage("SM" + String.format("%04d", message.getBytes(UTF_8).length) + message);
                }
                else if(message.equals("강퇴")){
                    message = scanner.next();
                    sendMessage("WD" + String.format("%04d", message.getBytes(UTF_8).length) + message);
                }
                else{
                    if(10000 > message.getBytes(UTF_8).length){
                        sendMessage("GM" + String.format("%04d", message.getBytes(UTF_8).length) + message);
                    }
                    else{
                        System.out.println("보낼 문자는 9999바이트를 넘을 수 없습니다.");
                    }
                }


                if (message.equals("q")) {
                    socket.close();
                    break;
                }
            }
        }

        scanner.close();
    }

    public void command(String s) {
        String command = s.substring(0, 2);
        int length = Integer.parseInt(s.substring(2, 6));
        String pl = s.substring(6, s.length());
        String message;
        if(command.equals("DR")){
            System.out.println("중복된 아이디 입니다. 다시 설정해 주세요.");
        }
        else if(command.equals("JR")){
            System.out.println(pl + "님이 채팅방에 접속하셨습니다.");
        }
        else if(command.equals("UR")){
            System.out.println("현재 접속중인 유저 목록 : " + pl);
        }
        else if(command.equals("GR")){
            System.out.println(pl.substring(0, 4) + " : " + pl.substring(4, pl.length()));
        }
        else if(command.equals("SR")){
            System.out.println(pl.substring(0, 4) + "님 께서 보낸 귓속말 : " + pl.substring(4, pl.length()));
        }
        else if(command.equals("DC")){
            System.out.println(pl + "님이 채팅방에서 퇴장하셨습니다.");
        }
        else if(command.equals("WR")){
            System.out.println("방장에 의해 퇴장당하셨습니다.");
        }
        else if(command.equals("WA")){
            System.out.println("방장이" + pl + "님을 채팅방에서 퇴장시켰습니다.");
        }
    }

    public static void main(String[] args) {
        try {
            Clientcopy client = new Clientcopy();
            client.connect();
            client.prepareTalking();
            client.processUserInput();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
