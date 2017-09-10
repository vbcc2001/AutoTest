package com.xxx.lfs.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xxx.web.function.Function;
import com.xxx.web.function.RequestParameter;
import com.xxx.web.function.ResponseParameter;
import com.xxx.web.http.action.ActionResult;
import com.xxx.web.http.action.BaseAction;
import com.xxx.web.http.listener.Configure;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * http服务入口
 * @author 门士松  20121031
 * @version 1.0
 * @since
 */
public class UploadAction extends BaseAction {
	private Logger logger = Logger.getLogger(this.getClass());
	private ResponseParameter responseParameter = new ResponseParameter();
	public ActionResult doDefault() {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.info("----isMultipart---"+isMultipart);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Configure a repository (to ensure a secure temp location is used)
			ServletContext servletContext = request.getSession().getServletContext();
			File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			logger.info("----javax.servlet.context.tempdir---"+repository.getPath()+"/"+repository.getName());;
			factory.setRepository(repository);

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// 得到所有的表单域，它们目前都被当作FileItem
			try {
				List<FileItem> items = upload.parseRequest(request);
				logger.info("----items.size()---"+items.size());
				Iterator<FileItem> itr = items.iterator();
				logger.info("----itr.s---"+itr);
				// 依次处理每个表单域
				while (itr.hasNext()) {
					FileItem item = itr.next();
					if (item.isFormField()) {
						// 如果item是正常的表单域
						logger.info("表单参数名:" + item.getFieldName() + "，表单参数值:" + item.getString("UTF-8"));
					} else {
						if (item.getName() != null && !item.getName().equals("")) {
							logger.info("上传文件的大小:" + item.getSize());
							logger.info("上传文件的类型:" + item.getContentType());
							// item.getName()返回上传文件在客户端的完整路径名称
							logger.info("上传文件的名称:" + item.getName());
							// 上传文件的保存路径 运行目录下
							File file = new File(servletContext.getRealPath("/download"), item.getName());
							item.write(file);
							logger.info("upload.message:上传文件成功！");
						} else {
							logger.info("upload.message:没有选择上传文件！");
						}
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				logger.error("upload.message:上传文件失败！"+e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("upload.message:上传文件失败！"+e.getMessage());
			}
		} else {
			logger.error("the enctype must be multipart/form-data");
		}
		return null;
	}
}
