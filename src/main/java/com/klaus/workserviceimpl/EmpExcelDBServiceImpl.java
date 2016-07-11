package com.klaus.workserviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.klaus.bean.EmpInfo;
import com.klaus.dao.EmpInfoDAO;
import com.klaus.factory.MyBeansFactory;
import com.klaus.utils.SecurityCoder;
import com.klaus.utils.TimeUtil;
import com.klaus.workservice.ExcelDBService;

public class EmpExcelDBServiceImpl implements ExcelDBService {

	private Workbook workbook;
	
	public void saveData(String filePath) {
		// TODO Auto-generated method stub

		try {

			File excelFile = new File(filePath); // �����ļ�����
			FileInputStream is = new FileInputStream(excelFile); // �ļ���
			workbook = WorkbookFactory.create(is); // ���ַ�ʽ Excel 2003/2007/2010
													// ���ǿ��Դ����

			saveExcel();

		} catch (Exception e) {

		}
		
	}

	private void saveExcel() {
		
		
		
		int sheetCount = workbook.getNumberOfSheets(); // Sheet������

		EmpInfoDAO empInfoDao=(EmpInfoDAO)MyBeansFactory.getBeans("empinfodao");
		
		// ����ÿ��Sheet
		for (int s = 0; s < sheetCount; s++) {

			Sheet sheet = workbook.getSheetAt(s);

			if (sheet != null) {

				int rowCount = sheet.getPhysicalNumberOfRows(); // ��ȡ������

				// ����ÿһ��
				for (int r = 1; r < rowCount; r++) {

					Row row = sheet.getRow(r);

					if (row != null) {						
						
						EmpInfo info=new EmpInfo();
						info.setId(TimeUtil.getObjectId());
						
						
						int cellType =0;//= cell.getCellType();

						Cell cell = row.getCell(2);///stuid
						cellType = cell.getCellType();
						String cellValue2 = getCellValues(cellType, cell).replaceAll("\\s*", "");
						info.setStuId(SecurityCoder.encryptSHA(cellValue2));
						

						cell = row.getCell(3);///stuname
						cellType = cell.getCellType();
						String cellValue3 = getCellValues(cellType, cell).replaceAll("\\s*", "");						
						info.setStuName(SecurityCoder.encryptSHA(cellValue3));
						
						
						
						cell= row.getCell(11);//choose
						cellType = cell.getCellType();
						String cellValue11 = getCellValues(cellType, cell).replaceAll("\\s*", "");
						info.setChoose(cellValue11);
						
						
						cell= row.getCell(13);//city
						cellType = cell.getCellType();
						String cellValue13 = getCellValues(cellType, cell).replaceAll("\\s*", "");
						info.setCity(cellValue13);
						
						
						
						cell = row.getCell(14);//apartment
						cellType = cell.getCellType();
						String cellValue14 = getCellValues(cellType, cell).replaceAll("\\s*", "");
						info.setApartment(cellValue14);						
						
						
						
						EmpInfo empp= empInfoDao.getInfobyStuId(SecurityCoder.encryptSHA(cellValue2));
						
						if(empp==null){
							
							empInfoDao.insertAllEmpInfo(info);
							empInfoDao.insertEmpInfo(info);
							
						}
						
						System.out.println(info.getId()+"  "+info.getChoose());
						
					}
				}
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
				//cellValue = String.valueOf(cell.getNumericCellValue()); // ����
				
				
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

				cellValue = fmt.format(cell.getDateCellValue()); // ������
				
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

		if(cellValue.length()==0||cellValue==null||"----------".equals(cellValue)){
			return "����";
		}
		
		return cellValue;
	}
	
}
