package com.klaus.workserviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.klaus.bean.Course;
import com.klaus.bean.ProductState;
import com.klaus.dao.ProductStateDAO;
import com.klaus.factory.MyBeansFactory;
import com.klaus.utils.StaticData;
import com.klaus.workservice.WorkerService;

public class ForecastWorkerServiceImpl implements WorkerService {
	
	private String pId;
	private Workbook workbook;
	private List<Course> listCourse = new ArrayList<Course>();

	public void start() {
		// TODO Auto-generated method stub
		
		
		ProductStateDAO productStateDao=(ProductStateDAO)MyBeansFactory.getBeans("productstatedao");
		
		List<ProductState> list= productStateDao.findEmptyProduct();
		
		if(list.size()==0){
			
			return ;
			
		}
		
		pId=list.get(0).getId();		
		
		productStateDao.startProduct(pId);
		
		File fold=new File(StaticData.StringData.ForecastScorePath);
		
		String[] fileList=fold.list();
		
		int len=fileList.length;
		
		for(int i=0;i<len;i++){
			
			
			String fileName=fileList[i];
			
			if(fileName.substring(0, 15).equals(pId)){
				
				paserExcel(StaticData.StringData.ForecastScorePath+fileName);
				
			}
				
		}		

	}
	
	
	private void paserExcel(String filePath){
		
		
		try{
			
			File excelFile = new File(filePath); // �����ļ�����
			FileInputStream is = new FileInputStream(excelFile); // �ļ���
			workbook = WorkbookFactory.create(is);
			
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

									// System.out.print(cellValue + "("+r+","+c+")
									// ");

							
								}

							}

							// System.out.println();

						}

					}

				}
			}
			
		}catch(Exception e){
			
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
				cellValue = String.valueOf(cell.getNumericCellValue()); // ����
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
				bean.setCourseName("name");
				bean.setCourseGrade(1.0);

				listCourse.add(bean);

			}

		}
	}
	

}
