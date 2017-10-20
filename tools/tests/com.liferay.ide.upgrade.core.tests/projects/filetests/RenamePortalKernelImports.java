package com.test;

import com.liferay.portal.model.Role;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.asset.model.BaseAssetRenderer;

import java.nio.file.Files;

public class Test extends MVCPortlet {
	
	String titleURL;
	
	public  void doView (RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException 
	{
		
		include("/view.jsp",renderRequest,renderResponse);
		
	}
	
	public void sendImage(ActionRequest request, ActionResponse response) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		SessionMessages.add(request, PortalUtil.getPortletId(request) + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		long userId = themeDisplay.getUserId();
		long groupId = themeDisplay.getScopeGroupId();
	
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		long folderId = 0;
		 
	    java.io.File submissionFile = null;
	    String submissionFileName = "";
		try {
			try {
				ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (PortalException e3) {
			e3.printStackTrace();
		}
	    
		String contentType = "";
	    String changeLog = "";
	    Map<String, Fields> fieldsMap = new HashMap<String, Fields>();
	    
		if (uploadRequest != null) {
		
			submissionFileName = uploadRequest.getFileName("uploadFileForMessage");
	    	submissionFile = uploadRequest.getFile("uploadFileForMessage");
	    	contentType = uploadRequest.getContentType("uploadFileForMessage");
	    	changeLog = ParamUtil.getString(request, "changeLog");

		}
	
	 if(!submissionFileName.equals("")){
		 
		ServiceContext serviceContext = null;
		try {
			try {
				serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (PortalException e3) {
			e3.printStackTrace();
		}
	    
		String title = submissionFileName;
		String description = "Message Media";
		DLFileEntry dlf = null;
		long realUserId = themeDisplay.getRealUserId();
		
		FileInputStream fis = null;
		
		try {
			fis = Files.newInputStream(submissionFile.toPath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	    try {
	    	
			dlf = DLFileEntryLocalServiceUtil.addFileEntry(realUserId, groupId, groupId, 0, submissionFileName, contentType, title, description, changeLog, 0, fieldsMap, submissionFile, fis, groupId, serviceContext);
			long feID = dlf.getFileEntryId();
			DLFileEntryLocalServiceUtil.updateFileEntry(realUserId, feID, submissionFileName, contentType, title, description, changeLog, false, 0, fieldsMap, submissionFile, fis, groupId, serviceContext);
			
			String primKeyDLFE = dlf.getPrimaryKey() + "";
			String[] actionIds = new String[1];
			
			actionIds[0] = "VIEW";
			
			Role user = RoleLocalServiceUtil.getRole(dlf.getCompanyId(), RoleConstants.USER);
			ResourcePermissionLocalServiceUtil.setResourcePermissions(dlf.getCompanyId(), "com.liferay.portlet.documentlibrary.model.DLFileEntry", 4, primKeyDLFE, user.getRoleId(), actionIds);
			
	    } catch (DuplicateFileException e) {
			
			int randomNumber = randInt(1000, 9999);
			
			title = title + "_" + randomNumber;
			
			try {
				try {
					dlf = DLFileEntryLocalServiceUtil.addFileEntry(realUserId, groupId, groupId, 0, submissionFileName, contentType, title, description, changeLog, 0, fieldsMap, submissionFile, fis, groupId, serviceContext);
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (PortalException e1) {
				e1.printStackTrace();
			}
			
			long feID = dlf.getFileEntryId();
			
			try {
				try {
					DLFileEntryLocalServiceUtil.updateFileEntry(realUserId, feID, submissionFileName, contentType, title, description, changeLog, false, 0, fieldsMap, submissionFile, fis, groupId, serviceContext);
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (PortalException e1) {
				e1.printStackTrace();
			}
		
			String[] actionIds = new String[1];
			
			actionIds[0] = "VIEW";	
			String primKeyDLFE = dlf.getPrimaryKey() + "";
			
			Role user = null;
			
			try {
				try {
					user = RoleLocalServiceUtil.getRole(dlf.getCompanyId(), RoleConstants.USER);
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (PortalException e1) {
				e1.printStackTrace();
			}
			
			try {
				try {
					ResourcePermissionLocalServiceUtil.setResourcePermissions(dlf.getCompanyId(), "com.liferay.portlet.documentlibrary.model.DLFileEntry", 4, primKeyDLFE, user.getRoleId(), actionIds);
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (PortalException e1) {
				e1.printStackTrace();
			}
			
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	    
	    titleURL = "/documents/" + groupId + "/" + dlf.getFolderId() + "/" + dlf.getTitle();
		
	}
		
		System.out.println("OPEN IMAGE FROM THIS PATH: " + titleURL);
		
	}
	
	public static int randInt(int min, int max) {
		
	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	    
	}

}
