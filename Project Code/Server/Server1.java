package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server1 {

    public static void main(String[] args)throws Exception{
        ServerSocket server = new ServerSocket(9000);
        ClientConnection[] conn = new ClientConnection[50];
        System.out.println("SERVER 1 STARTED");
        while(true){
            Socket client  = server.accept();
            for(int i=0;i<50;i++){
                if(conn[i]==null){
                    //System.out.println("CLIENT CONNECTED->"+i);
                    (new ClientConnection(client,conn,"localhost",1)).start();
                    break;
                }
            }
        }
    }
    
}

class ClientConnection extends Thread{
    
    public Socket client=null;
    public ClientConnection[] conn = new ClientConnection[50];
    public DataInputStream din = null;
    public DataOutputStream dout = null;
    public String ip="";
    public int s;
    public String[] ips={"192.168.43.122","192.168.43.122"};
    
    public ClientConnection(Socket c,ClientConnection[] obj,String p,int server){
        this.client=c;
        this.conn=obj;
        this.ip=p;
        this.s = server;
        try{
            din = new DataInputStream(c.getInputStream());
            dout = new DataOutputStream(c.getOutputStream());
        }catch(IOException e){
            System.out.println("Data Stream Failed");
        }
    }
    
    public void UPDATE()throws Exception{
        // TODO: Update all files and tables
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
            PreparedStatement s = con.prepareStatement("select * from LOG");
            ResultSet set = s.executeQuery();
            Connection con1 = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
            while(set.next()){
                String[] arr = set.getString("QUERY").split("->");
                int id = set.getInt("ID");
                PreparedStatement ps = con.prepareStatement("delete from LOG where ID=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                if(arr[0].equals("MYSQL")){
                    if(arr[1].equals("LOGIN")){
                        PreparedStatement s1 = con1.prepareStatement("insert into LOGIN(USERNAME,PASSWORD) values(?,?)");
                        s1.setString(1, arr[2]);
                        s1.setString(2, arr[3]);
                        s1.executeUpdate();
                        File f = new File("./SERVER1/"+arr[2]);
                        f.mkdir();
                    }else{
                        PreparedStatement s1 = con1.prepareStatement("insert into FILELIST(FILENAME,OWNER,PRIVLAGE) values(?,?,?)");
                        s1.setString(1, arr[2]);
                        s1.setString(2, arr[3]);
                        s1.setString(3, arr[4]);
                        s1.executeUpdate();
                        File f = new File("./SERVER1/"+arr[3]+"/"+arr[2].trim()+".csv");
                        FileWriter fout = new FileWriter(f);
                        String asd="";
                        for(int i=0;i<100;i++){
                            String res = "\"\"";
                            for(int j=1;j<100;j++){
                                res = res +",\"\"";
                            }
                            asd = asd+res+"\n";
                        }
                        PrintWriter w = new PrintWriter(fout);
                        w.print(asd);
                        w.close();
                        fout.close();
                    }
                }else{
                    File f = new File("./SERVER1/"+arr[2]+"/"+arr[3]+".csv");
                    int row = Integer.parseInt(arr[4]);
                    int col = Integer.parseInt(arr[5]);
                    String val = arr[6];
                    Scanner sc = new Scanner(f);
                    String asd="";
                    for(int l=0;l<100;l++){
                        String line = sc.nextLine();
                        if(l==row){
                            String[] arr1 = line.split("\",\"");
                            line="\""+arr1[0].replace("\"", "")+"\"";
                            for(int i=1;i<arr1.length;i++){
                                arr1[i] = arr1[i].replace("\"", "");
                                if(i==col){
                                    line = line+",\""+val+"\"";
                                }else{
                                    line = line+",\""+arr1[i]+"\"";
                                }
                            }
                        }
                        asd = asd + line +"\n";
                    }
                    FileWriter fout = new FileWriter(f);
                    PrintWriter w = new PrintWriter(fout);
                    w.print(asd);
                    w.close();
                    fout.close();
                }
            }
        }catch(Exception e){}
    }
    
    public void LOGIN()throws Exception{
        UPDATE();
        System.out.println("LOGIN");
        String username = din.readUTF();
        String password = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
        if(con==null){
            System.out.println("CONNECTION FAILED");
        }else{
            PreparedStatement s = con.prepareStatement("select * from LOGIN where USERNAME=? and PASSWORD=?");
            s.setString(1, username);
            s.setString(2, password);
            ResultSet set = s.executeQuery();
            int count=0;
            while(set.next()){
                count++;
            }
            if(count>0){
                dout.writeUTF("CORRECT");
                dout.flush();
            }else{
                dout.writeUTF("INCORRECT");
                dout.flush();
            }
        }
    }

    public void SIGNUP()throws Exception {
        UPDATE();
        String username = din.readUTF();
        String password = din.readUTF();
        String cache = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
        if(con==null){
            System.out.println("CONNECTION FAILED");
        }else{
            PreparedStatement s = con.prepareStatement("insert into LOGIN(USERNAME,PASSWORD) values(?,?)");
            s.setString(1, username);
            s.setString(2, password);
            int res = s.executeUpdate();
            if(res==0){
                dout.writeUTF("FAILED");
                dout.flush();
            }else{
                dout.writeUTF("SUCCESS");
                dout.flush();
                File f = new File("./SERVER1/"+username);
                f.mkdir();
            }
            if(cache.equals("1")){
                s = con.prepareStatement("insert into LOG(QUERY) values(?)");
                s.setString(1, "MYSQL->LOGIN->"+username+"->"+password);
                s.executeUpdate();
            }
        }
    }
    
    public void FILELIST()throws Exception{
        UPDATE();
        String username = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
        if(con==null){
            System.out.println("CONNECTION FAILED");
        }else{
            ArrayList<HashMap<String,String>> list = new ArrayList<>();
            PreparedStatement s = con.prepareStatement("select * from FILELIST");
            ResultSet set = s.executeQuery();
            while(set.next()){
                HashMap<String,String> m = new HashMap<>();
                if(set.getString("OWNER").equals(username)){
                    m.put("OWNER", set.getString("OWNER"));
                    m.put("FILENAME", set.getString("FILENAME"));
                    m.put("PRIVLAGE", set.getString("PRIVLAGE"));
                }else if(!set.getString("PRIVLAGE").equals("00")){
                    m.put("OWNER", set.getString("OWNER"));
                    m.put("FILENAME", set.getString("FILENAME"));
                    m.put("PRIVLAGE", set.getString("PRIVLAGE"));
                }
                if(!m.isEmpty()){
                    list.add(m);
                }
            }
            ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
            oout.writeObject(list);
            oout.flush();
        }
    }
    
    public void CREATEFILE() throws Exception{
        UPDATE();
        String user = din.readUTF();
        String filename = din.readUTF();
        String privlage = din.readUTF();
        String cache = din.readUTF();
        File f = new File("./SERVER1/"+user+"/"+filename.trim()+".csv");
        if(f.createNewFile()){
            FileWriter fout = new FileWriter(f);
            String asd="";
            for(int i=0;i<100;i++){
                String res = "\"\"";
                for(int j=1;j<100;j++){
                    res = res +",\"\"";
                }
                asd = asd+res+"\n";
            }
            PrintWriter w = new PrintWriter(fout);
            w.print(asd);
            w.close();
            fout.close();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
            if(con==null){
                System.out.println("CONNECTION FAILED");
            }else{
                PreparedStatement s = con.prepareStatement("insert into FILELIST(FILENAME,OWNER,PRIVLAGE) values(?,?,?)");
                s.setString(1, filename);
                s.setString(2, user);
                s.setString(3, privlage);
                int res = s.executeUpdate();
                if(res==0){
                    dout.writeUTF("FAILED");
                    dout.flush();
                }else{
                    dout.writeUTF("SUCCESS");
                    dout.flush();
                }
                if(cache.equals("1")){
                    s = con.prepareStatement("insert into LOG(QUERY) values(?)");
                    s.setString(1, "MYSQL->FILELIST->"+filename+"->"+user+"->"+privlage);
                    s.executeUpdate();
                }
            }
        }else{
            dout.writeUTF("EXIST");
            dout.flush();
        }
    }
    
    public void FILECONTENT() throws Exception{
        UPDATE();
        String owner = din.readUTF();
        String file = din.readUTF();
        ArrayList<String[]> list = new ArrayList<>();
        File f = new File("./SERVER1/"+owner+"/"+file.trim()+".csv");
        Scanner sc = new Scanner(f);
        for(int i=0;i<100;i++){
            String s = sc.nextLine();
            String[] arr = s.split("\",\"");
            for(int j=0;j<100;j++){
                arr[j] = arr[j].replace("\"", "");
            }
            list.add(arr);
        }
        ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
        oout.writeObject(list);
        oout.flush();
    }
    
    public void FILETIME() throws Exception{
        UPDATE();
        String owner = din.readUTF();
        String file = din.readUTF();
        File f = new File("./SERVER1/"+owner+"/"+file+".csv");
        String op = ""+f.lastModified();
        dout.writeUTF(op);
        dout.flush();
    }
    
    public void UPDATECELL() throws Exception{
        UPDATE();
        String owner = din.readUTF();
        String file = din.readUTF();
        String cache = din.readUTF();
        int row = Integer.parseInt(din.readUTF());
        int col = Integer.parseInt(din.readUTF());
        String val = din.readUTF();
        ArrayList<String[]> list = new ArrayList<>();
        File f = new File("./SERVER1/"+owner+"/"+file.trim()+".csv");
        Scanner sc = new Scanner(f);
        String asd="";
        for(int l=0;l<100;l++){
            String line = sc.nextLine();
            if(l==row){
                String[] arr = line.split("\",\"");
                if(col==0){
                    line="\""+val+"\"";
                }else{
                    line="\""+arr[0].replace("\"", "")+"\"";
                }
                for(int i=1;i<arr.length;i++){
                    arr[i] = arr[i].replace("\"", "");
                    if(i==col){
                        line = line+",\""+val+"\"";
                    }else{
                        line = line+",\""+arr[i]+"\"";
                    }
                }
            }
            asd = asd + line +"\n";
        }
        FileWriter fout = new FileWriter(f);
        PrintWriter w = new PrintWriter(fout);
        w.print(asd);
        w.close();
        fout.close();
        if(cache.equals("1")){
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
            PreparedStatement s = con.prepareStatement("insert into LOG(QUERY) values(?)");
            s.setString(1, "FILE->SERVER->"+owner+"->"+file+"->"+row+"->"+col+"->"+val);
            s.executeUpdate();
        }
        dout.writeUTF("DONE1");
    }
    
    @Override
    public void run() {
        try{
            UPDATE();
            String key = din.readUTF();
            switch(key){
                case "LOGIN":{
                    LOGIN();
                    break;
                }
                case "SIGN UP":{
                    SIGNUP();
                    break;
                } 
                case "FILELIST":{
                    FILELIST();
                    break;
                }
                case "CREATE-FILE":{
                    CREATEFILE();
                    break;
                }
                case "FILE-CONTENT":{
                    FILECONTENT();
                    break;
                }
                case "FILETIME":{
                    FILETIME();
                    break;
                }
                case "UPDATECELL":{
                    UPDATECELL();
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            for(int i=0;i<50;i++){
                if(this==conn[i]){
                    conn[i]=null;
                }
            }
        }
    }    
}