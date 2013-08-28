package com.ebay.build.workspace;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ebay.build.reliability.ErrorCode;
import com.ebay.build.reliability.ReportInfo;
import com.ebay.build.reliability.ReportInfoMapper;
import com.ebay.build.utils.StringUtils;

public class ServerReliabilityJDBC {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public ReportInfo getReportInfoBeforeDay(String day) {
		final String SQL = "select  sum(a.amount) total, " + 
				"SUM( CASE WHEN a.caused_by = 0 Then a.amount ELSE 0 END) success , " +
				"Sum(CASE when a.caused_by_name='System Error' Then a.amount ELSE 0 END) syserror, " +
				"Sum(CASE when a.caused_by_name='User Error' Then a.amount ELSE 0 END) usererror, " +
				"Sum(CASE when a.caused_by_name='Unknown Error' Then a.amount ELSE 0 END) unknown " +
				"from statistics_ide_error_time AS a LEFT JOIN dimensional_error_code AS b ON a.error_type_id = b.id " +
				"where a.date_dim >= DATE_ADD(CURDATE(),INTERVAL - ? DAY) " +
				"and a.source = 'RIDE' AND a.phase_id = 6 and a.ide_name != 'RIDE Older' and a.ide_name != 'Unknown' ";
		try {
			ReportInfo reportInfo = jdbcTemplateObject.queryForObject(SQL,
					new Object[] { day }, new ReportInfoMapper());
			return reportInfo;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ReportInfo getReportInfoPeriod(String startDate, String endDate) {
		final String SQL = "select  sum(a.amount) total, " + 
				"SUM( CASE WHEN a.caused_by = 0 Then a.amount ELSE 0 END) success , " +
				"Sum(CASE when a.caused_by_name='System Error' Then a.amount ELSE 0 END) syserror, " +
				"Sum(CASE when a.caused_by_name='User Error' Then a.amount ELSE 0 END) usererror, " +
				"Sum(CASE when a.caused_by_name='Unknown Error' Then a.amount ELSE 0 END) unknown " +
				"from statistics_ide_error_time AS a LEFT JOIN dimensional_error_code AS b ON a.error_type_id = b.id " +
				"where a.date_dim < date_format(?, '%Y-%c-%d') " +
				"and a.date_dim >= date_format(?, '%Y-%c-%d') " +
				"and a.source = 'RIDE' AND a.phase_id = 6 and a.ide_name != 'RIDE Older' and a.ide_name != 'Unknown' ";
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
		for (ErrorCode error : topTenError) {
			if (StringUtils.isEmpty(error.getName())) {
				error.setDescription("N/A");
			} else {
				error.setDescription(error.getDescription());
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
		final String SQL = " SELECT * FROM " + 
				   " (SELECT sum(b.amount) as total, a.name as name, a.cause as cause" +
				   " from dimensional_error_code a " +
				   " LEFT JOIN statistics_ide_error_time AS b ON a.id = b.error_type_id " +
				   " WHERE b.caused_by_name = ? " +
				   " and b.source = 'RIDE' AND b.phase_id = 6 and b.ide_name != 'RIDE Older' and b.ide_name != 'Unknown' " +
				   " and b.date_dim >= DATE_ADD(CURDATE(),INTERVAL - 30 DAY) " +
				   " GROUP BY name " + 
				   " ORDER BY total DESC) as s limit 10 ";
		List<ErrorCode> topTenError = null;

		try {
			if ("System Error".equals(errorCatagory)) {
				topTenError = jdbcTemplateObject
						.query(SQL, new Object[] { errorCatagory },
								new SpaceUserErrorMapper());

			}
			else if("User Error".equals(errorCatagory))
			{
				topTenError = jdbcTemplateObject
						.query(SQL, new Object[] { errorCatagory },
								new SpaceUserErrorMapper());
			}

			return topTenError;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ReportInfo> getWeeklyReliability() {
		List<ReportInfo> reportList = new ArrayList<ReportInfo>();

		try {
			reportList.add(getReportInfoPeriod(previousNDays(49), previousNDays(42)));
			reportList.add(getReportInfoPeriod(previousNDays(42), previousNDays(35)));
			reportList.add(getReportInfoPeriod(previousNDays(35), previousNDays(28)));
			reportList.add(getReportInfoPeriod(previousNDays(28), previousNDays(21)));
			reportList.add(getReportInfoPeriod(previousNDays(21), previousNDays(14)));
			reportList.add(getReportInfoPeriod(previousNDays(14), previousNDays(7)));
			reportList.add(getReportInfoPeriod(previousNDays(7), previousNDays(0)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportList;
	}
	
	public double[] getWeeklySystemReliability(List<ReportInfo> reportList) {
		double[] week = new double[7];
		
		for (int i = 0; i < reportList.size(); i++) {
			week[i] = Double.valueOf(reportList.get(i).getSystemReliabilityRate());
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
	

	// set day before current day
	public String previousNDays(int previous) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(calendar.DATE, 0 - previous);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}


}
