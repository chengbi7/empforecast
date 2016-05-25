package com.klaus.apiserviceimpl;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.klaus.apiservice.ScoreService;
import com.klaus.utils.StaticData;

public class ScoreServiceImpl implements ScoreService {

	public String uploadExcel(HttpServletRequest req) {

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

				String fileName = item.getName();
				int index = fileName.lastIndexOf("\\");
				fileName = fileName.substring(index + 1);
				req.setAttribute("realFileName", fileName);

				File file = new File(StaticData.StringData.FileScorePath + fileName);
				try {
					item.write(file);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception ex) {
			return "error";
		}

		return "ok";
	}

}
