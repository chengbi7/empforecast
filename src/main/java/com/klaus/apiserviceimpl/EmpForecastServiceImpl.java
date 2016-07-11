package com.klaus.apiserviceimpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.klaus.apiservice.EmpForecastService;
import com.klaus.bean.Ability;
import com.klaus.bean.AbilityStander;
import com.klaus.bean.Course;
import com.klaus.bean.CourseAbility;
import com.klaus.bean.StudentAbility;
import com.klaus.dao.AbilityDAO;
import com.klaus.dao.CourseAbilityDAO;
import com.klaus.dao.CourseDAO;
import com.klaus.factory.MyBeansFactory;

public class EmpForecastServiceImpl implements EmpForecastService {

	private Workbook workbook;

	private List<Course> listCourse = new ArrayList<Course>();
	private List<Map<String, String>> listCourseGrade = new ArrayList<Map<String, String>>();

	private Map<String, String> map = new HashMap<String, String>();
	private Map<String, String> abilityMap = new HashMap<String, String>();

	private Map<String, String> mapResult ;//= new HashMap<String, String>();
	private List<Map<String, String>> listResult=new ArrayList<Map<String,String>>(); 

	public List<Map<String, String>> uploadExcel(HttpServletRequest req) {
		// TODO Auto-generated method stub

		try {

			// �ļ��ϴ�������
			FileItemFactory factory = new DiskFileItemFactory();

			// �����ļ��ϴ�������
			ServletFileUpload upload = new ServletFileUpload(factory);

			// ��ʼ����������Ϣ
			List<?> items = null;
			try {
				items = upload.parseRequest(req);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			Iterator<?> iter = items.iterator();
			
			while (iter.hasNext()) {

				FileItem item = (FileItem) iter.next();

				workbook = WorkbookFactory.create(item.getInputStream()); // ���ַ�ʽ Excel2003/2007/2010 ���ǿ��Դ����

				saveExcel();

			}


		} catch (Exception ex) {

		}

		return listResult;

	}

	
	public void saveExcel() {
		
		getAbilityInfo();
		

		int sheetCount = workbook.getNumberOfSheets(); // Sheet������

		// ����ÿ��Sheet
		for (int s = 0; s < sheetCount; s++) {

			Sheet sheet = workbook.getSheetAt(s);


			if (sheet != null) {

				int rowCount = sheet.getPhysicalNumberOfRows(); // ��ȡ������

				// ����ÿһ��
				for (int r = 0; r < rowCount; r++) {

					Row row = sheet.getRow(r);


					if (row != null) {

						int cellCount = row.getPhysicalNumberOfCells(); // ��ȡ������

						// ����ÿһ��
						for (int c = 0; c < cellCount; c++) {

							Cell cell = row.getCell(c);

							if (cell != null) {

								int cellType = cell.getCellType();

								String cellValue = getCellValues(cellType, cell).replaceAll("\\s*", "");


								getCousrInfo(r, c, cellValue);

								getCourseGrade(r, c, cellValue);

							}

						}

						// System.out.println();

					}

				}

			}
		}

		CourseDAO course = (CourseDAO) MyBeansFactory.getBeans("coursedao");
		CourseAbilityDAO courseAbilityDao = (CourseAbilityDAO) MyBeansFactory.getBeans("courseabilitydao");

		
		for (int i = 0; i < listCourseGrade.size(); i++) {

			mapResult=new HashMap<String, String>();

			
			Map<String, String> tempMap = listCourseGrade.get(i);

			double sum = 0.0;

			List<AbilityStander> listAbilityStander = new ArrayList<AbilityStander>();

			for (Map.Entry<String, String> entry : tempMap.entrySet()) {

				String key = entry.getKey();

				if ("StudentName".equals(key) || "StudentId".equals(key) || "-".equals(entry.getValue())) {

					if (!"-".equals(entry.getValue())) {

						mapResult.put(key, entry.getValue());

					}

				} else {

					String courseid = course.getCourseId(key);

					if (courseid != null) {

						List<CourseAbility> listCA = courseAbilityDao.getMappingByCourseId(courseid);

						for (int h = 0; h < listCA.size(); h++) {

							CourseAbility courseability = listCA.get(h);

							String abilityName = abilityMap.get(courseability.getAbilityId());

							double score = ScoreTransaction(entry.getValue());

							sum = sum + courseability.getScore();

							AbilityStander abilityStander = new AbilityStander(abilityName, score,courseability.getScore());
							listAbilityStander.add(abilityStander);

						}

					}

				}

			}
			
			
			try {
				
				StudentAbility abi = new StudentAbility();
				
				Class<?> a =abi.getClass();		

				
				for (int j = 0; j < listAbilityStander.size(); j++) {
					
					AbilityStander ab=listAbilityStander.get(j);


					double xMapping=ab.getMapping()/sum;
					
					double xScore=ab.getScore()*xMapping;
					
					Field f = a.getField(ab.getAbility());

					
					Double tempX = (Double)f.get(abi);
					
					f.set(abi, new Double(tempX+xScore));

					
				}
				
				String str="";
				//mapResult=new HashMap<String, String>();
				
				
				Field[] fields=a.getFields();
				for(int h=0;h<fields.length;h++){
					
					Field f =fields[h];
					
					System.out.println(f.getName());
					//System.out.println(f.get(abi));
					
				}
				
				
				
				String strTemp="";
				
				Process pr = Runtime.getRuntime().exec("python test.py"+str);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {

					strTemp=strTemp+line;
					
				}
				in.close();
				pr.waitFor();
				
				
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(strTemp);  
		
				mapResult.put("city", root.get("city").toString());
				mapResult.put("choose", root.get("choose").toString());
				mapResult.put("apartment", root.get("apartment").toString());
			    
				
				listResult.add(mapResult);
				
				
			    //JsonNode data = root.path("data");  
			      // ��ȡ info  
			      //JsonNode info = data.path("info");  
			  
			      //System.out.println(info.size());  
			  
			      // �õ� info �ĵ� 0 ��  
			      //JsonNode item = info.get(0);  
			      //System.out.println(item.get("id"));  
			      //System.out.println(item.get("timestamp"));  
			    
				
			} catch (Exception e) {
				
				System.out.println("wrong :  ");
				
			}
			
			

		}

	}

	private String getCellValues(int cellType, Cell cell) {

		String cellValue = "";

		switch (cellType) {
		case Cell.CELL_TYPE_STRING: // �ı�
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC: // ���֡�����
			if (DateUtil.isCellDateFormatted(cell)) {

				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

				cellValue = fmt.format(cell.getDateCellValue()); // ������
			} else {

				DecimalFormat df = new DecimalFormat("0");

				cellValue = df.format(cell.getNumericCellValue());

			}

			break;

		case Cell.CELL_TYPE_BOOLEAN: // ������
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;

		case Cell.CELL_TYPE_BLANK: // �հ�
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_ERROR: // ����
			cellValue = "����";
			break;
		case Cell.CELL_TYPE_FORMULA: // ��ʽ
			cellValue = "����";
			break;
		default:
			cellValue = "����";
		}

		return cellValue;
	}

	private void getCousrInfo(int r, int c, String cellValue) {


		if (r == 1 && c >= 5) {

			if (cellValue.length() != 0) {

				Course bean = new Course();
				bean.setId(c + "");
				bean.setCourseId(cellValue);

				listCourse.add(bean);

			}

		}

		if (r == 2 && c >= 5) {

			if (cellValue.length() != 0) {
				for (int i = 0; i < listCourse.size(); i++) {

					Course beanOld = listCourse.get(i);

					if (beanOld.getId().equals(c + "")) {

						beanOld.setCourseName(cellValue);

					}

				}
			}
		}

		if (r == 3 && c >= 5) {

			if (cellValue.length() != 0) {

				for (int i = 0; i < listCourse.size(); i++) {

					Course beanOld = listCourse.get(i);

					if (beanOld.getId().equals(c + "")) {

						beanOld.setCourseGrade(Double.parseDouble(cellValue));

					}

				}

			}
		}

	}

	private String getCourseID(int clounmNumber) {

		for (int i = 0; i < listCourse.size(); i++) {

			Course beanOld = listCourse.get(i);

			if (Integer.parseInt(beanOld.getId()) == clounmNumber) {

				return beanOld.getCourseId();

			}

		}
		return null;

	}

	private void getCourseGrade(int r, int c, String cellValue) {

		if (r >= 5) {

			if (c == 1) {
				
				map.put("StudentId", cellValue);

			}

			if (c == 4) {

				map.put("StudentName", cellValue);

			}

			if (c >= 5) {

				String courseID = getCourseID(c);

				if (courseID != null) {
					
					map.put(courseID, cellValue);

				}

				if (map.size() == (listCourse.size() + 2)) {
					
					
					listCourseGrade.add(map);

					map = new HashMap<String, String>();

				}

			}

		}

	}

	private void getAbilityInfo() {

		AbilityDAO abilityDao = (AbilityDAO) MyBeansFactory.getBeans("abilitydao");

		List<Ability> list = abilityDao.getAllInfo();

		for (int i = 0; i < list.size(); i++) {

			abilityMap.put(list.get(i).getId(), list.get(i).getName());

		}

	}

	private double ScoreTransaction(String score) {

		if (null == score) {

			return 0;

		}

		double tempDouble = 0.0;

		try {

			tempDouble = Double.parseDouble(score);

		} catch (Exception e) {

			tempDouble = -1.0;
		}

		if (tempDouble == -1.0) {

			if ("ͨ��".equals(score)) {

				return 60.0;

			} else {

				if (score.contains("��1")) {

					String s = score.replace("��1", "");

					return Double.parseDouble(s) * 0.8;

				} else {

					if (score.contains("��2")) {

						String s = score.replace("��2", "");

						return Double.parseDouble(s) * 0.6;

					} else {

						if (score.contains("��1")) {

							String s = score.replace("��1", "");

							return Double.parseDouble(s) * 0.4;

						} else {

							if (score.contains("��2")) {

								String s = score.replace("��2", "");

								return Double.parseDouble(s) * 0.2;

							} else {

								if (score.contains("ȡ���ʸ�")) {

									return 0.0;

								} else {

									if (score.contains("��")) {

										return 0.0;

									} else {

									}

								}

							}

						}

					}

				}

			}

			return tempDouble;

		} else {

			return tempDouble;

		}

	}

}
