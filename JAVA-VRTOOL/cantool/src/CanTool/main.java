package CanTool;

import gnu.io.SerialPort;
import serialPort.SerialTool;

public class main {
	
	public static void main(String[] args)throws Exception{
		System.out.println("start");
		SerialPort port=SerialTool.openPort("COM1", 115200);
		CanTool tool=new CanTool(port);
		SerialListener listener=new SerialListener(port,tool);
		tool.addListener(listener);
	}
}
