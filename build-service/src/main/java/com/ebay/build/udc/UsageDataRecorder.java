package com.ebay.build.udc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import com.ebay.build.profiler.filter.ErrorClassifier;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.udc.dao.IUsageDataDao;
import com.ebay.build.udc.dao.IUsageDataDao.DaoException;


/**
 * @author bishen
 */
public class UsageDataRecorder extends Thread
{

	private static final String[] COLUMNS = new String[] { "ideType",
			"ideVersion", "sessionId", "kind", "what", "description",
			"bundleId", "bundleVersion", "when", "duration", "size",
			"quantity", "exception", "properties", "sessionProperties" };
    
	private ErrorClassifier errorClassifier;

    private List<File>            m_files = new ArrayList<File>();
    private List<UsageDataInfo>   m_data  = new ArrayList<UsageDataInfo>();
    private static Logger logger = Logger.getLogger(UsageDataRecorder.class.getName());
   
    private IUsageDataDao dao;
	
    public UsageDataRecorder(){
    	logger.log(Level.INFO, "Initialize UsageDataRecorder");
    }
    
    public void setFiles(List<File> files){
    	 if (files != null) {
             this.m_files.addAll(files);
         }
    }
    
    public void setErrorClassifier(ErrorClassifier errorClassifier) {
		this.errorClassifier = errorClassifier;
	}

	public void setDao(IUsageDataDao dao) {
		this.dao = dao;
	}

	@Override
    public void run()
    {
    	long startTime = System.currentTimeMillis();
        try
        {
            for (File file : this.m_files)
            {
                process(file);
            }
            
            logger.log(Level.INFO, "UsageDataRecorder parse .csv time is " + (System.currentTimeMillis()-startTime));
            long time = System.currentTimeMillis();
            insertUsageData();
            logger.log(Level.INFO, "UsageDataRecorder: insert "+m_data.size()+" records. And use " + (System.currentTimeMillis()-time) + " milliseconds");
            cleanFiles();

            logger.log(Level.INFO, "Processed " + this.m_files.size() + " log files!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        long duration = System.currentTimeMillis() - startTime;
        logger.log(Level.INFO, "UsageDataRecorder execute time is " + duration);
    }

    private void cleanFiles()
    {
        Iterator<File> iter = this.m_files.iterator();
        while (iter.hasNext())
        {
            File file = iter.next();
            file.delete();
        }
    }

    private void insertUsageData()
            throws DaoException
    {
        if ( this.m_data.size() == 0 )
        {
            return;
        }

    
        dao.insertUsageData(this.m_data);
    }

    private void process(File report)
            throws IOException
    {
        String fileName = report.getName();
        String[] names = fileName.split("_");

        String hostName = names[0]; // first part is host name
        String userName = names[1];

        // check host and user name
        if ( isEmpty(hostName) || isEmpty(userName) )
        {
            return;
        }

        ColumnPositionMappingStrategy<UsageDataRecord> strat = new ColumnPositionMappingStrategy<UsageDataRecord>();
        strat.setType(UsageDataRecord.class);
        strat.setColumnMapping(COLUMNS);
        CsvToBean<UsageDataRecord> csv = new CsvToBean<UsageDataRecord>();
        CSVReader reader = new CSVReader(new FileReader(report));
        List<UsageDataRecord> list = csv.parse(strat, reader);
        reader.close();

        boolean firstLineSkipped = false;
        RECORD: for (UsageDataRecord record : list)
        {
            if ( !firstLineSkipped )
            {
                firstLineSkipped = true;
                continue;
            }

            // check required fields
            if ( isEmpty(record.getKind()) || isEmpty(record.getDescription()) )
            {
                continue;
            }
            
            //20101028#kevin shen:Be sure to filter blacklist 
			for (String name : IFilterConstants.FEATURE_BLACKLIST) {
				if (record.getDescription().contains(name)) {
					continue RECORD;
				}
			}
            
            try
            {
                UsageDataInfo info = new UsageDataInfo();
                info.setHost(hostName);
                info.setUser(userName);
                info.setIdeType(record.getIdeType());
                
                if(!StringUtils.isEmpty(record.getSessionId())){
                	info.setSessionId(record.getSessionId());
                }
                info.setIdeVersion(record.getIdeVersion());
                info.setKind(record.getKind());
                info.setWhat(record.getWhat());
                info.setDescription(record.getDescription());
                info.setBundleId(record.getBundleId());
                info.setBundleVersion(record.getBundleVersion());
                info.setWhen(Long.parseLong(record.getWhen()));
                info.setDuration(Integer.parseInt(record.getDuration()));
                info.setSize(Integer.parseInt(record.getSize()));
                info.setQuantity(Integer.parseInt(record.getQuantity()));
                info.setException(record.getException());
                info.setProperties(record.getProperties());
                info.setSessionProperties(record.getSessionProperties());
                
                Filter findFilter = null;
				//ensure that the failure of filter process will not affect the insert.
                try {
					if(errorClassifier !=null && info.getException() != null){
						findFilter = errorClassifier.doClassify(info.getWhat(), info.getException());
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error In Filter Process");
					e.printStackTrace();
				}
                
				if(findFilter!=null){
                	info.setCategory(findFilter.getCategory());
                	info.setErrorCode(findFilter.getName());
                }else
                {
                	info.setCategory(null);
                	info.setErrorCode(null);
                }
                
                this.m_data.add(info);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmpty(String text)
    {
        if ( text == null || text.trim().length() == 0 )
        {
            return true;
        }

        return false;
    }

    public static class UsageDataRecord
    {

    	private String ideType;
    	private String ideVersion;
    	private String sessionId;
        private String kind;
        private String what;
        private String description;
        private String bundleId;
        private String bundleVersion;
        private String when;
        private String duration;
        private String size;
        private String quantity;
        private String exception;
        private String properties;
        private String sessionProperties;

        public UsageDataRecord()
        {
        }

       

		public String getKind()
        {
            return this.kind;
        }

        public void setKind(String kind)
        {
            this.kind = kind;
        }

        public String getBundleId()
        {
            return this.bundleId;
        }

        public void setBundleId(String bundleId)
        {
            this.bundleId = bundleId;
        }

        public String getBundleVersion()
        {
            return this.bundleVersion;
        }

        public void setBundleVersion(String bundleVersion)
        {
            this.bundleVersion = bundleVersion;
        }

        public String getDescription()
        {
            return this.description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getDuration()
        {
            return this.duration;
        }

        public void setDuration(String duration)
        {
            this.duration = duration;
        }

        public String getWhat()
        {
            return this.what;
        }

        public void setWhat(String what)
        {
            this.what = what;
        }

        public String getWhen()
        {
            return this.when;
        }

        public void setWhen(String when)
        {
            this.when = when;
        }

        public String getException()
        {
            return this.exception;
        }

        public void setException(String exception)
        {
            this.exception = exception;
        }

        public String getSize()
        {
            return this.size;
        }

        public void setSize(String size)
        {
            this.size = size;
        }

        public String getQuantity()
        {
            return this.quantity;
        }

        public void setQuantity(String quantity)
        {
            this.quantity = quantity;
        }

        public String getProperties()
        {
            return this.properties;
        }

        public void setProperties(String properties)
        {
            this.properties = properties;
        }

		public String getIdeType() {
			return ideType;
		}

		public void setIdeType(String ideType) {
			this.ideType = ideType;
		}

		public String getIdeVersion() {
			return ideVersion;
		}

		public void setIdeVersion(String ideVersion) {
			this.ideVersion = ideVersion;
		}


		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}



		public String getSessionProperties() {
			return sessionProperties;
		}



		public void setSessionProperties(String sessionProperties) {
			this.sessionProperties = sessionProperties;
		}
        
		
        

    }

}
