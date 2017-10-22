package CanTool;

import serialException.SendDataToSerialPortFailure;
import serialException.SerialPortOutputStreamCloseFailure;
import serialException.TooManyListeners;
import serialPort.SerialTool;
import java.io.UnsupportedEncodingException;
import gnu.io.SerialPort;

public class CanTool {
	private SerialPort port; 
	private boolean flag;
	private int speed;
	
	public CanTool(SerialPort serialport){
		this.port=serialport;
		flag=false;
		speed=10;
	}
	
	public void addListener(SerialListener listener){
		try{
			SerialTool.addListener(port, listener);
			System.out.println("�����ɹ�");
		}catch(TooManyListeners e){
			e.printStackTrace();
		}
	}
	
    public int returnActualLength(byte[] data) { 
        int i = 0; 
        for (; i < data.length; i++) { 
            if (data[i] == '\0') 
                break; 
        } 
        return i; 
    }
	
	public void getCommand(byte[] bytes) throws UnsupportedEncodingException, SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure{
		byte[] command=bytes;
		int length=returnActualLength(command);
		String temp=new String(command,0,length,"UTF-8");
	      char c=(char)command[0];
	      if(c=='O'&& command[1]=='1'){
	        if(checklength(c,length)){        //��������ȴ��� 
	          open_s();
	        }
	      }else if(c=='V'){
	        if(checklength(c,length)){        //��������ȴ��� 
//	          System.out.println("SV2.5-HV2.0");
	          SerialTool.sendToPort(port, "SV2.5-HV2.0\r".getBytes());
	        }
	      }else if(c=='C'){
	        if(checklength(c,length)){        //��������ȴ��� 
	          close_s();
	        }
	      }else if(c=='S'){ 
	        if(checklength(c,length)){        //��������ȴ��� 
	          Changespeed((char)command[1]);
	        }
	      }else if(c=='t' ){
	        Sendstandardframe(temp);
	      }else if(c=='T' ){
	        Sendexternalframe(temp);
	      }else{
	        fail();
	      }
	}
	 
	public void open_s(){
	  if(!flag){
	    flag=true;
	    //���سɹ�
	    success();
	    return;
	  }
	  fail();
	}
	
	public void close_s(){
	  if(flag){
	    flag=false;
	    //���سɹ�
	    success();
	    return;
	  }
	  fail();
	}
	
	public void Changespeed(char NO_speed){
	//  Serial.println(NO_speed);
	   if(flag){
	    int num[]={10,20,50,100,125,250,500,800,1000};
	    if(NO_speed<'0' || NO_speed>'8'){//���ش���
	      fail();
	    }else{
	      int pos_speed=(int)(NO_speed-'0');
	      speed=num[pos_speed];
	      System.out.println("�ٶȸı�Ϊ��"+speed);
	      //���سɹ�
	      success();
	    }
	  }else{
	    fail();
	  }
	}
	
	public void Sendstandardframe(String standardframe){
	//  Serial.println(standardframe);
	  if(Checkframe(standardframe,1)){
	    System.out.println("��CAN���߷��͸ñ�׼֡"); 
	    success();
	  }else
	    fail();
	}
	
	void Sendexternalframe(String externalframe){
	//  Serial.println(externalframe);
	  if(Checkframe(externalframe,0)){
	    System.out.println("��CAN���߷��͸���չ֡"); 
	    success();
	  }else
	    fail();
	}
	
	public void success(){
	  System.out.print("success");
	  String temp="success"+(char)0x07;
	  	try {
	  		SerialTool.sendToPort(port, temp.getBytes());
		} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fail(){
	  System.out.print("fail");
	  String temp="fail"+(char)0x07;
	  	try {
	  		SerialTool.sendToPort(port, temp.getBytes());
		} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean Checkframe(String frame,int n){//n=1����׼֡��n=0����չ֡
	  int idlen=0;
	  if(n==1){
	    idlen=3;
	  }else if(n==0){
	    idlen=8;
	  }
	  if(frame.length()<6+idlen)//��׼֡����չ֡����6+idlen��id��t/T�����ݳ��ȣ����ڣ�
		  return false;
	    String id=frame.substring(1,1+idlen);
	    int length_f=Integer.parseInt(frame.substring(1+idlen,2+idlen));
	    if(length_f<0 || length_f>8)
	      return false;
	    if(frame.length()!=(idlen+6+length_f*2)){
	      return false;
	    }
	    String data=frame.substring(2+idlen,length_f*2+idlen+2);
	    String period=frame.substring(length_f*2+idlen+2,length_f*2+idlen+6);

	    //��׼֡id[0]��0-0x7FF֮��,��չ֡�ڣ�00000000-1FFFFFFF) ֮��
	    if(n==1 && (id.charAt(0)>='8' || id.charAt(0)<'0')){
	        return false;
	    }else if(n==1 && (id.charAt(0)!='0' && id.charAt(0)!='1')){
	        return false;
	    }
	    for(int i=1;i<idlen;i++){
	      if((id.charAt(i)>'F' || id.charAt(i)<'0') || (id.charAt(i)<'A' && id.charAt(i)>'9')){
	        return false;
	      }
	    }
	    //��׼֡data��������'0'-'F'֮��
	    for(int i=2+idlen;i<length_f*2+idlen+6;i++){
	    	//���Ӷ�a-f�ַ���֧��
	      if((frame.charAt(i)>'F'&&frame.charAt(i)<'a')||frame.charAt(i)>'f' || frame.charAt(i)<'0' || (frame.charAt(i)<'A' && frame.charAt(i)>'9')){
	        return false;        
	      }
	    }
	    return true;
	}
	//��������
	public boolean checklength(char NO,int len){
	    boolean result=false;
	    if(( NO=='C' || NO=='V') && len==1){
	      result=true;
	    }else if((NO=='O' || NO=='S') && len==2){
	      result=true;
	    }else{
	      fail();
	    }
	    return result;
	}
	
	public void sendframetoAPP(){
		
	}
	

}
