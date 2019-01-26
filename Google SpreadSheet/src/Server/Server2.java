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

public class Server2 {

    /**
     * 
     * Setting up the primary server.
     * When a new client connects to the server the server system will create a 
     * new thread to connect to the client and the same thread will work on it.
     * 
    **/
    public static void main(String[] args)throws Exception{
        ServerSocket server = new ServerSocket(8999);
        ClientConnection2[] conn = new ClientConnection2[50];
        System.out.println("SERVER 2 STARTED");
        while(true){
            Socket client  = server.accept();
            for(int i=0;i<50;i++){
                if(conn[i]==null){
                    (new ClientConnection2(client,conn,"localhost",1)).start();
                    break;
                }
            }
        }
    }
    
}

class ClientConnection2 extends Thread{
    
    public Socket client=null;
    public ClientConnection2[] conn = new ClientConnection2[50];
    public DataInputStream din = null;
    public DataOutputStream dout = null;
    public String ip="";
    public int s;
    public String[] ips={"localhost","localhost"};
    
    /**
     * Establish a Client Connection and set up a data stream connection with the 
     * server.
     */
    public ClientConnection2(Socket c,ClientConnection2[] obj,String p,int server){
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
    
    /**
     * 
     * This Function does the work of Updating the database if the server had 
     * failed at some point of time.
     * This is done using a LOG table.
     * 
     */
    public void UPDATE(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[0]+":3306/DOCS1","ADITYA","Aditya@2997");
            PreparedStatement s = con.prepareStatement("select * from LOG");
            ResultSet set = s.executeQuery();
            Connection con1 = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
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
                        File f = new File("./SERVER2/"+arr[2]);
                        f.mkdir();
                    }else{
                        PreparedStatement s1 = con1.prepareStatement("insert into FILELIST(FILENAME,OWNER,PRIVLAGE) values(?,?,?)");
                        s1.setString(1, arr[2]);
                        s1.setString(2, arr[3]);
                        s1.setString(3, arr[4]);
                        s1.executeUpdate();
                        File f = new File("./SERVER2/"+arr[3]+"/"+arr[2].trim()+".csv");
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
                    File f = new File("./SERVER2/"+arr[2]+"/"+arr[3]+".csv");
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
            con.close();
            con1.close();
        }catch(Exception e){
            
        }
    }
    
    /**
     * 
     * This function is to perform LOGIN using the data from the LOGIN table from
     * the Database.
     * 
     */
    public void LOGIN()throws Exception{
        System.out.println("LOGIN");
        String username = din.readUTF();
        String password = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
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
        con.close();
    }

    /**
     * 
     * This function is to perform SIGNUP to the data from the LOGIN table from
     * the Database.
     * 
     */
    public void SIGNUP()throws Exception {
        String username = din.readUTF();
        String password = din.readUTF();
        String cache = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
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
                File f = new File("./SERVER2/"+username);
                f.mkdir();
            }
            if(cache.equals("1")){
                s = con.prepareStatement("insert into LOG(QUERY) values(?)");
                s.setString(1, "MYSQL->LOGIN->"+username+"->"+password);
                s.executeUpdate();
            }
        }
        con.close();
    }
    
    /**
     * 
     * FILELIST function resonds to the client all the files he/she can view based
     * on the user and the Privilage levels.
     * The user will be able to see all files created my him and the Public File
     * of other user based on the Privilage Level.
     * 
     */
    public void FILELIST()throws Exception{
        String username = din.readUTF();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
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
            oout.close();
        }
        con.close();
    }
    
    /**
     * 
     * CREATEFILE function is used to create a file for the user with the user 
     * selected Privilage Level.
     * The Privilage Level are:-
     * Private FIle
     * Public File - But can only View
     * Public File - Can also Edit
     * Based on the name and Privilage Level a File is created on both servers.
     * If one of then is Down then the other server will insert an Entry in the 
     * LOG table.
     * 
     */
    public void CREATEFILE() throws Exception{
        String user = din.readUTF();
        String filename = din.readUTF();
        String privlage = din.readUTF();
        String cache = din.readUTF();
        File f = new File("./SERVER2/"+user+"/"+filename.trim()+".csv");
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
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
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
            con.close();
        }else{
            dout.writeUTF("EXIST");
            dout.flush();
        }
    }
    
    /**
     * 
     * FILECONTENT is used to send the data from the file from the server to the 
     * Client.
     * It breaks the cells of the CSV excel sheet and inserts them into a ArrayList.
     * Then this ArrayList is sent to the client.
     * 
     */
    public void FILECONTENT() throws Exception{
        String owner = din.readUTF();
        String file = din.readUTF();
        ArrayList<String[]> list = new ArrayList<>();
        File f = new File("./SERVER2/"+owner+"/"+file.trim()+".csv");
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
    
    /**
     * 
     * FILETIME function is to check if any modification have been made to the 
     * file in recent time.
     * If any modification has been made then the file on the screen muse be 
     * updated.
     * 
     */
    public void FILETIME() throws Exception{
        String owner = din.readUTF();
        String file = din.readUTF();
        File f = new File("./SERVER2/"+owner+"/"+file+".csv");
        String op = ""+f.lastModified();
        dout.writeUTF(op);
        dout.flush();
    }
    
    /**
     * 
     * UPDATECELL function is used to update the cells of the excel sheet in real
     * time.
     * The Update work is done on the file of both the Primary and the Secondary
     * Server.
     * If one of them is not running at the time then a LOG is inserted in the 
     * respective server LOG table.
     * 
     */
    public void UPDATECELL() throws Exception{
        String owner = din.readUTF();
        String file = din.readUTF();
        String cache = din.readUTF();
        int row = Integer.parseInt(din.readUTF());
        int col = Integer.parseInt(din.readUTF());
        String val = din.readUTF();
        ArrayList<String[]> list = new ArrayList<>();
        File f = new File("./SERVER2/"+owner+"/"+file.trim()+".csv");
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
            Connection con = DriverManager.getConnection("jdbc:mysql://"+ips[1]+":3306/DOCS2","ADITYA","Aditya@2997");
            PreparedStatement s = con.prepareStatement("insert into LOG(QUERY) values(?)");
            s.setString(1, "FILE->SERVER->"+owner+"->"+file+"->"+row+"->"+col+"->"+val);
            s.executeUpdate();
        }
        dout.writeUTF("DONE1");
    }
    
    /**
     * 
     * This is the main function that handles all the threads of the Client 
     * Connection to the server.
     * 
     */
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
