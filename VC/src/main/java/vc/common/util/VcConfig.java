package vc.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcConfig {
	private VcConfig() {}
	private static final Logger logger = LoggerFactory.getLogger(VcConfig.class);
	
public static final String UMS_DFT_STATUS = "dft";
	
    private static VcConfig instance = null;
    private static final Object classLock = VcConfig.class;
    private Properties prop;
    private static final String SELECT_UMS_COM_CFG = "SELECT CF_KEY, CF_VAL FROM UMS_COM_CFG ";
	
	
	public String getInternal(String key)
    {
        return prop.getProperty(key);
    }

    public String getInternal(String key, String defaultValue)
    {
        return prop.getProperty(key, defaultValue);
    }

    public static String get(String key)
    {
        return getInstance().getInternal(key);
    }

    public static String get(String key, String defaultValue)
    {
        return getInstance().getInternal(key, defaultValue);
    }

    public static VcConfig getInstance()
    {
        if(instance != null)
            return instance;
        synchronized(classLock)
        {
        	VcConfig config = load();
            instance = config;
        }
        return instance;
    }

    private static VcConfig load(){
    	VcConfig result = new VcConfig();
        DataSource datasource = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        
        /*if(LApplicationContext.isBatchContext())
        {
            Application app = ApplicationContextTrace.getCurrentApplication();
            if(app == null)
                throw new IllegalStateException("current application is not created.");
            datasource = app.getContainerDataSource();
        } else
        {
            ApplicationContainer container = ApplicationContainerLoader.getCurrentApplicationContainer();
            if(container == null)
                throw new IllegalStateException("current application container is not created.");
            datasource = container.getDataSoruce();
        }*/
        result.prop = new Properties();
        try{
        	
            con = datasource.getConnection();
            pstmt = con.prepareStatement(SELECT_UMS_COM_CFG);
            rset = pstmt.executeQuery();
            String key = null;
            String value = null;
            while(rset.next()) 
            {
                key = rset.getString(1);
                value = rset.getString(2);
                
                if(System.getProperty(key) != null){
                    result.prop.put(key, System.getProperty(key));
                    logger.info("BXM_COM_CFG key [{}]'s value replaced with SYSTEM ENV Value : SYSTEM ENV Value [{}], BXM_COM_CFG Value [{}]", 
                    		new Object[] {key, System.getProperty(key), value});
                } else{
                    result.prop.put(key, value);
                }
            }
        }
        catch(Throwable th){
            String errMsg = String.format("load BXM_COM_CFG is failed.", new Object[0]);
            logger.error("ERROR MSG : [{}] ", errMsg); 
        }finally {
        	close(con , rset, pstmt);
		}
        
        return result;
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
