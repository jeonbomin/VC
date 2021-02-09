// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CcutTcpUtil.java

package vc.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcTcpUtil{
	private static Logger logger = LoggerFactory.getLogger(VcTcpUtil.class);
    public VcTcpUtil(){}

    public static boolean sendResponse(String in, Integer pcsnStat, long srtSqno)throws Exception{
        boolean result = false ;
        Socket clientTcp = null;
        OutputStream outStream = null;
        String data = ">";

        data += "0051";
        data += "C01";
        data += String.format("%9s", in);
        data += String.format("%8s", in);
        data += String.format("%03d", in);
        data += String.format("%06d", in);
        data += String.format("%02d", in);
        data += String.format("%04d", in);
        data += String.format("%02d", in);
        data += String.format("%010d", in);
        data = "<";
        
        String SrvrIp = "127.0.0.1";
        int portNo = 9003;
        int loop = 0;
        while (true) {
        	try {
	        	clientTcp = new Socket(SrvrIp, portNo);
	            outStream = clientTcp.getOutputStream();
	            outStream.write(data.getBytes());
	            outStream.flush();
	            result = true;
	            break;
        	}catch (IOException e) {
				loop++;
				if(loop > 5) {
					logger.error("Socket Error");
					throw e;
				}
			} finally {
				if (outStream != null)
					outStream.close();
				if (clientTcp != null)
					clientTcp.close();
			}
        }
        
		return result;
    }

}
