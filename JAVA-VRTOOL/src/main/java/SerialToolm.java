import gnu.io.SerialPort;
import serialException.ReadDataFromSerialPortFailure;
import serialException.SerialPortInputStreamCloseFailure;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Admin on 2017/10/17.
 */
public class SerialToolm {

	/**
	 * 从串口读取数据
	 * @param serialPort 当前已建立连接的SerialPort对象
	 * @return 读取到的数据
	 * @throws ReadDataFromSerialPortFailure 从串口读取数据时出错
	 * @throws SerialPortInputStreamCloseFailure 关闭串口对象输入流出错
	 */
	public static byte[] readFromPort(SerialPort serialPort) throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {

		InputStream in = null;
		byte[] bytes = new byte[1024];

		try {
			int temp;
			in = serialPort.getInputStream();
			int bufflenth = in.available();		//获取buffer里的数据长度
			temp=in.read();
			int num=0;//bytes数组存放字节的位置

			while (bufflenth != 0 && temp!=0x0D) {
				bytes[num]=(byte) temp;
				bufflenth = in.available();		//获取buffer里的数据长度
				temp=in.read();
				num++;
			}

		} catch (IOException e) {
			throw new ReadDataFromSerialPortFailure();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch(IOException e) {
				throw new SerialPortInputStreamCloseFailure();
			}
		}
		return bytes;
	}

}