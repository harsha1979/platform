/*
* Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.identity.provider.mgt.ui.util;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.wso2.carbon.identity.provider.mgt.stub.IdentityProviderMgtExceptionException;
import org.wso2.carbon.identity.provider.mgt.stub.dto.TrustedIdPDTO;
import org.wso2.carbon.identity.provider.mgt.ui.bean.CertData;
import org.wso2.carbon.identity.provider.mgt.ui.bean.TrustedIdPBean;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityProviderMgtUIUtil {

    public static boolean validateURI(String uriString) throws MalformedURLException {
        new URL(uriString);
        return true;
    }

    public static CertData getCertData(String encodedCert) throws CertificateException {
        byte[] bytes = Base64.decode(encodedCert);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(bytes));
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        return fillCertData(cert, formatter);

    }

    public static Map<String,String> getRoleMappings(String[] roleMappings){
        Map<String,String> roleMappingsMap = new HashMap<String,String>();
        for(String roleMapping:roleMappings){
            String[] split = roleMapping.split(":");
            roleMappingsMap.put(split[0],split[1]);
        }
        return roleMappingsMap;
    }

    public static Object[] getFormData(HttpServletRequest request) throws FileUploadException, IdentityProviderMgtExceptionException {
        if (ServletFileUpload.isMultipartContent(request)) {
            Object[] objects = new Object[2];
            ServletRequestContext servletContext = new ServletRequestContext(request);
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items =  upload.parseRequest(servletContext);
            String issuer = null;
            String url = null;
            String publicCert = null;
            String[] roleMappings = null;
            List<String> oldRoles = null;
            String[] oldRoleMappings = null;
            String oldPublicCert = null;
            List<String> newRoles = new ArrayList<String>();
            TrustedIdPBean oldBean = (TrustedIdPBean)request.getSession().getAttribute("trustedIdPBean");
            TrustedIdPDTO oldDTO = (TrustedIdPDTO)request.getSession().getAttribute("trustedIdPDTO");
            if(oldBean != null){
                oldRoles = oldBean.getRoles();
            }
            if(oldDTO != null){
                oldRoleMappings = oldDTO.getRoleMappings();
                oldPublicCert = oldDTO.getPublicCert();
            }
            if(oldRoles != null && !oldRoles.isEmpty()){
                for(int i = 0; i < oldRoles.size(); i++){
                    newRoles.add("");
                }
            }
            List<String> tempList = new ArrayList<String>();
            String delete = null;
            int finalRowId = -1;
            TrustedIdPDTO trustedIdPDTO = new TrustedIdPDTO();
            for (Object item : items) {
                DiskFileItem diskFileItem = (DiskFileItem) item;
                String name = diskFileItem.getFieldName();
                if (name.equals("issuer")) {
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] issuerArray = fileItem.get();
                    if(issuerArray != null && issuerArray.length > 0){
                        issuer = new String(issuerArray);
                    }
                } else if(name.equals("url")){
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] urlArray = fileItem.get();
                    if(urlArray != null && urlArray.length > 0){
                        url = new String(urlArray);
                    }
                } else if(name.equals("certFile")){
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] publicCertArray = fileItem.get();
                    if(publicCertArray != null && publicCertArray.length > 0){
                        publicCert = Base64.encode(publicCertArray);
                    } else {
                        if(fileItem.getName() == null || fileItem.getName().equals("")) {
                            publicCert = oldPublicCert;
                        }
                    }
                } else if(name.equals("roleMappingFile")){
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] roleMappingsArray = fileItem.get();
                    if(roleMappingsArray != null && roleMappingsArray.length > 0){
                        String roleMappingsString = new String(roleMappingsArray);
                        if(roleMappingsString != null){
                            roleMappingsString = roleMappingsString.replaceAll("\\s","");
                        }
                        roleMappings = roleMappingsString.split(",");
                    } else {
                        if(fileItem.getName() == null || fileItem.getName().equals("")){
                            roleMappings = oldRoleMappings;
                        }
                    }
                } else if(name.startsWith("rowname_")){
                    int rowId = Integer.parseInt(name.substring(name.indexOf("_")+1));
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] rolesArray = fileItem.get();
                    if(oldRoles != null && rowId < oldRoles.size()){
                        newRoles.remove(rowId);
                        newRoles.add(rowId, new String(rolesArray));
                    } else {
                        if(rolesArray != null && rolesArray.length > 0){
                            tempList.add(new String(rolesArray));
                        }
                    }
                } else if(name.equals("delete")){
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] deleteArray = fileItem.get();
                    if(deleteArray != null && deleteArray.length > 0){
                        delete = new String(deleteArray);
                    }
                } else if(name.equals("finalRowId")){
                    FileItem fileItem = (FileItem) diskFileItem;
                    byte[] finalRowIdArray = fileItem.get();
                    if(finalRowIdArray != null && finalRowIdArray.length > 0){
                        finalRowId = Integer.parseInt(new String(finalRowIdArray));
                    }
                }
            }
            newRoles.addAll(tempList);
            trustedIdPDTO.setIdPIssuerId(issuer);
            trustedIdPDTO.setIdPUrl(url);
            trustedIdPDTO.setPublicCert(publicCert);
            trustedIdPDTO.setRoles(newRoles.toArray(new String[newRoles.size()]));
            trustedIdPDTO.setRoleMappings(roleMappings);
            objects[0] = trustedIdPDTO;
            objects[1] = delete;
            return objects;
        } else {
            throw new IdentityProviderMgtExceptionException("Invalid Content Type: Not multipart/form-data");
        }
    }

    private static CertData fillCertData(X509Certificate cert, Format formatter) throws CertificateEncodingException {

        CertData certData = new CertData();
        certData.setSubjectDN(cert.getSubjectDN().getName());
        certData.setIssuerDN(cert.getIssuerDN().getName());
        certData.setSerialNumber(cert.getSerialNumber());
        certData.setVersion(cert.getVersion());
        certData.setNotAfter(formatter.format(cert.getNotAfter()));
        certData.setNotBefore(formatter.format(cert.getNotBefore()));
        certData.setPublicKey(Base64.encode(cert.getPublicKey().getEncoded()));
        return certData;
    }
}
