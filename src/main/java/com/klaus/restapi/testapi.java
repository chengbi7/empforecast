package com.klaus.restapi;


import java.io.File;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/tteess")
public class testapi {

	@POST
	public String uploadExcel(@Context HttpServletRequest request) {

		try {

			String savePath = request.getServletContext().getRealPath("/WEB-INF/uploadFile");
			
			System.out.println(savePath);
			
			//String savePath ="E:/temp/";
			
			// ��ȡ�ϴ����ļ�����
			Collection<Part> parts = request.getParts();			
			
			System.out.println("AAAAAAAAAAAAA"+parts.size());

			if (parts.size() == 1) {
				// Servlet3.0��multipart/form-data��POST�����װ��Part��ͨ��Part���ϴ����ļ����в�����
			//	 Part part = parts[0];//���ϴ����ļ������л�ȡPart����
				
				System.out.println("BBBBBBBBBBBBBBBB");
				
				Part part = request.getPart("file");// ͨ����file�ؼ�(<input
													// type="file"
													// name="file">)������ֱ�ӻ�ȡPart����
				
				System.out.println("CCCCCCCCCCCCCCCCc");
				
				// Servlet3û���ṩֱ�ӻ�ȡ�ļ����ķ���,��Ҫ������ͷ�н�������
				// ��ȡ����ͷ������ͷ�ĸ�ʽ��form-data; name="file";
				// filename="snmp4j--api.zip"
				String header = part.getHeader("content-disposition");
				
				System.out.println("DDDDDDDDDDDDDDDD");
				
				
				// ��ȡ�ļ���
				String fileName = getFileName(header);
				
				
				System.out.println("EEEEEEEEEEEEEEEE");
				
				// ���ļ�д��ָ��·��
				part.write(savePath + File.separator + fileName);
				
				
				System.out.println("FFFFFFFFFFFFFFFFFFFF");
				
				//part.write(savePath + fileName);
			} else {
				
				
				System.out.println("GGGGGGGGGGGGGGGGGGGG");
				
				// һ�����ϴ�����ļ�
				for (Part part : parts) {// ѭ�������ϴ����ļ�
					// ��ȡ����ͷ������ͷ�ĸ�ʽ��form-data; name="file";
					// filename="snmp4j--api.zip"
					
					System.out.println("HHHHHHHHHHHHHHHHHHHHH");
					
					String header = part.getHeader("content-disposition");
					
					System.out.println("IIIIIIIIIIIIIIIIIIIIII");
					
					// ��ȡ�ļ���
					String fileName = getFileName(header);
					
					System.out.println("JJJJJJJJJJJJJJJJJJJJJJJJ");
					
					// ���ļ�д��ָ��·��
					part.write(savePath + File.separator + fileName);
					//part.write(savePath + fileName);
					
					
					System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKK");
					
				}
			}

		} catch (Exception e) {
		}

		return "okklaus";

	}

	public String getFileName(String header) {
		/**
		 * String[] tempArr1 =
		 * header.split(";");����ִ����֮���ڲ�ͬ��������£�tempArr1���������������������
		 * �������google������£�tempArr1={form-data,name="file",filename=
		 * "snmp4j--api.zip"}
		 * IE������£�tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
		 */
		String[] tempArr1 = header.split(";");
		/**
		 * �������google������£�tempArr2={filename,"snmp4j--api.zip"}
		 * IE������£�tempArr2={filename,"E:\snmp4j--api.zip"}
		 */
		String[] tempArr2 = tempArr1[2].split("=");
		// ��ȡ�ļ��������ݸ����������д��
		String fileName = tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
		return fileName;
	}

}
