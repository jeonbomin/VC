package vc.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class VcConfig {
	private VcConfig() {}
	private static Logger logger = LoggerFactory.getLogger(VcConfig.class);
	
	public static final String VC_DF_SYS_ID = "vc.df.sys.id";
	
	private static VcConfig instance = null;
	private static final Object classLock = VcConfig.class;
	
	private Properties prop;
	
	public String getInternal (String key) {
		return prop.getProperty(key);
	}
	
	public String getInternal (String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}
	
	public String get (String key) {
		return getInternal(key);
	}
	
	public String get (String key, String defaultValue) {
		return getInternal(key, defaultValue);
	}
	
	public static VcConfig getInstance() {
		if(instance != null) {
			return instance;
		}
		
		synchronized (classLock) {
			VcConfig config = load();
			instance = config;
		}
		
		return instance;
	}
	
	private static final String SELECT_VC_CFG = "SELECT VC_KEY, VC_VAL FROM VC_CFG"; 
	
	private static VcConfig load() {
		VcConfig result = new VcConfig();
		result.prop = new Properties();
		
		DataSource datasource = null;
		Connection con = null;
		PreparedStatement pre = null;
		ResultSet rset = null;
		try {
			con = datasource.getConnection();
			pre = con.prepareStatement(SELECT_VC_CFG);
			rset = pre.executeQuery();
			String key = "";
			String value = "";
			
			while(rset.next()) {
				key = rset.getString(1);
				value = rset.getString(2);
				if(!StringUtils.hasLength(key)) {
					continue;
				}
				if(!StringUtils.hasLength(value)) {
					continue;
				}
				if(System.getProperty(key) != null) {
					result.prop.put(key, System.getProperty(key));
				}else {
					result.prop.put(key, value);
				}
			}
		}catch (Throwable th) {
			logger.error("ERROR MSG");
			throw new RuntimeException(th);
		}finally {
			close(con, pre, rset);
		}
		
		return result;
	}
	
	private static void close(AutoCloseable... closeables ) {
		if (closeables == null) return;
		for(AutoCloseable closeable : closeables) {
			if (closeable == null) continue;
			try {
				closeable.close();
			}catch (Exception e) {
				logger.error("Resource close ERROR : {}", e.getMessage());
			}
		}
	}
}
