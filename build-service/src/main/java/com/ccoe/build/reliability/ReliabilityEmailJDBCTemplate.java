package com.ccoe.build.reliability;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReliabilityEmailJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	private Map<String, ErrorCode> errorMap = new HashMap<String, ErrorCode>();

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public ReportInfo getReportInfoPeriod(String startDate, String endDate) {
		final String SQL = "select count(*) total, count(distinct r.user_name ) users, "
				+ " SUM( CASE WHEN r.cause is null THEN 1 ELSE 0 END) success , "
				+ " Sum(CASE when r.category ='user' Then 1 ELSE 0 END) usererror , "
				+ " Sum(CASE when r.category ='system' Then 1 ELSE 0 END) syserror, "
				+ " Sum(CASE when r.category ='exclude' Then 1 ELSE 0 END) exclude, " 
				+ " Sum(CASE when r.category is null and r.cause is not null Then 1 ELSE 0 END) unknown "
				+ " from rbt_session r "
				+ " where r.start_time < to_date(?, 'DD-Mon-YY HH24:MI:SS') "
				+ " and r.start_time >= to_date(?, 'DD-Mon-YY HH24:MI:SS') "
				+ " and r.machine_name != 'phx5qa01c-3a08' "
				+ " and r.machine_name != 'phx5qa01c-3838' " 
				+ " and r.machine_name != 'phx5qa01c-c25c' " 
				+ " and environment = 'CI' "
				+ " and raptor_version like '1%' ";
		try {
			ReportInfo reportInfo = jdbcTemplateObject
					.queryForObject(SQL, new Object[] { endDate, startDate },
							new ReportInfoMapper());
			return reportInfo;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setDescriptionNPercentage(List<ErrorCode> topTenError, int baseCount) {
		if (errorMap.isEmpty()) {
			errorMap.putAll(this.getErrorDescription());
		}
		
			
		for (ErrorCode error : topTenError) {
		
			ErrorCode errorDescription = errorMap.get(error.getName());
			
			if (errorDescription == null) {
				error.setDescription("N/A");
			} else {
				error.setDescription(errorDescription.getDescription());
			}
			
			double beforeSetDecimal = error.getCount() * 1.0
					/ baseCount * 100;
			DecimalFormat df = new DecimalFormat("#0.00");
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			error.setPercentage(df.format(beforeSetDecimal));
		}
	}
	
	public List<ErrorCode> getTopTenError(String errorCatagory) {
		final String SQL = "select * from "
				+ " (select filter, count(filter) as c_filter from rbt_session r "
				+ " where r.category= ? and r.start_time > sysdate - 30 "
				+ " and r.machine_name != 'phx5qa01c-3a08' "
				+ " and r.machine_name != 'phx5qa01c-3838' " 
				+ " and r.machine_name != 'phx5qa01c-c25c' " 
				+ " and environment = 'CI' "
				+ " and raptor_version like '1%' "
				+ " group by filter " + "order by c_filter desc) "
				+ " where rownum <= 10";
		List<ErrorCode> topTenError = null;

		try {
			if ("system".equals(errorCatagory)) {
				topTenError = jdbcTemplateObject
						.query(SQL, new Object[] { errorCatagory },
								new TopTenErrorMapper());

			}
			else if("user".equals(errorCatagory))
			{
				topTenError = jdbcTemplateObject
						.query(SQL, new Object[] { errorCatagory },
								new TopTenErrorMapper());
			}

			return topTenError;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, ErrorCode> getErrorDescription() {
		final String SQL = " select error_code, description from rbt_error_code ";
		Map<String, ErrorCode> results = new HashMap<String, ErrorCode>();
		try {
			List<ErrorCode> errorDescriptionList = jdbcTemplateObject.query(SQL, new DescriptionMapper());
			for (ErrorCode desc : errorDescriptionList) {
				results.put(desc.getName(), desc);
			}
			return results;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ReportInfo> getWeeklyReliability(Date date) {
		List<ReportInfo> reportList = new ArrayList<ReportInfo>();

		try {
			reportList.add(getReportInfoPeriod(previousNDays(49, date), previousNDays(42, date)));
			reportList.add(getReportInfoPeriod(previousNDays(42, date), previousNDays(35, date)));
			reportList.add(getReportInfoPeriod(previousNDays(35, date), previousNDays(28, date)));
			reportList.add(getReportInfoPeriod(previousNDays(28, date), previousNDays(21, date)));
			reportList.add(getReportInfoPeriod(previousNDays(21, date), previousNDays(14, date)));
			reportList.add(getReportInfoPeriod(previousNDays(14, date), previousNDays(7, date)));
			reportList.add(getReportInfoPeriod(previousNDays(7, date), previousNDays(0, date)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportList;
	}
	
	public double[] getWeeklySystemReliability(List<ReportInfo> reportList) {
		double[] week = new double[7];
		
		for (int i = 0; i < reportList.size(); i++) {
			week[i] = Double.parseDouble(reportList.get(i).getSystemReliabilityRate());
		}
		return week;
	}
	
	public double[] getWeeklyOverallReliability(List<ReportInfo> reportList) {
		double[] week = new double[7];
		
		for (int i = 0; i < reportList.size(); i++) {
			week[i] = Double.valueOf(reportList.get(i).getOverallReliabilityRate());
		}
		return week;
	}
	
	public Map<String, ReportInfo> generateReliabilityTable() {
		Date date = new Date();
		String endDate = formatDate(date);
		ReportInfo reportList1 = getReportInfoPeriod(previousNDays(1, date), endDate);
		ReportInfo reportList2 = getReportInfoPeriod(previousNDays(7, date), endDate);
		ReportInfo reportList3 = getReportInfoPeriod(previousNDays(30, date), endDate);

		Map<String, ReportInfo> infoList = new HashMap<String, ReportInfo>();
		infoList.put("info_24h", reportList1);
		infoList.put("info_7day", reportList2);
		infoList.put("info_30day", reportList3);

		return infoList;
	}

	// set day before current day
	public String previousNDays(int previous, Date currentDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(calendar.DATE, 0 - previous);
		Date date = calendar.getTime();
		return formatDate(date);
	}
	
	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm:ss", Locale.US);
		return sdf.format(date);
	}
}
