package vc.common.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCallUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpCallUtils.class);
	private static final String Encoding = "UTF-8";
	private static final int ConTimeout = 0;
	private static final int ReadTimeout = 0;
	private static final int BUFFER_SIZE = 2048;
	
	public static byte[] RemoteCall(String url, byte[] reqBytes) throws Exception{
		
		HttpURLConnection con = null;
		byte[] resBytes = null;
		int resCodes = 0;
		
		try {
			URL svcUrl = new URL(url);
			
			con = (HttpURLConnection) svcUrl.openConnection();
			// con set
			con.setDoOutput(true);
			if(Encoding == null) 
				con.addRequestProperty("Content-Type", "text/xml");
			else 
				con.addRequestProperty("Content-Type", "text/xml;charset=" + Encoding);
			con.setConnectTimeout(ConTimeout);
			con.setReadTimeout(ReadTimeout);

			try(DataOutputStream dout = new DataOutputStream(con.getOutputStream())) {
				dout.write(reqBytes);
				dout.flush();
				resCodes = con.getResponseCode();
				
				logger.debug("Http Response Code= [{}]", resCodes);
				
				if(resCodes == HttpURLConnection.HTTP_OK) {
					try(DataInputStream din = new DataInputStream(con.getInputStream()); ByteArrayOutputStream bout = new ByteArrayOutputStream();){
						byte[] outBuffer = new byte[BUFFER_SIZE];
						int readLen = 0;
						do {
							bout.write(outBuffer, 0, readLen);
							readLen = din.read(outBuffer);
						} while ( readLen > 0 );
						
						bout.flush();
						resBytes = bout.toByteArray();
					}
				}else {
					logger.warn("HttpURLConnection ERROR URL : {}", svcUrl);
					throw new Exception("HTTP ERROR CODE = "+ resCodes);
				}
				return resBytes;
			}catch (SocketException e) {
				throw new Exception("SocketException ERROR");
			}
		}catch (Exception e) {
			logger.error("MSG : {}", e);
		}finally {
			if(con != null) con.disconnect();
		}
		return resBytes;
	}
	
   private static void close(AutoCloseable... closeables) {
    	if(closeables == null) return;
    	for(AutoCloseable closeable : closeables) {
    		if(closeable == null) continue;
    		try {
    			closeable.close();
			} catch (Exception e) {}
    	}
    }
}
