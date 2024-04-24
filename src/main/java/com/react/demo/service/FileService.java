package com.react.demo.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{

        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        //디렉토리 유무 확인
        File targetDir = new File(uploadPath);
        if(!targetDir.exists()) {
            if(!targetDir.mkdirs()) {
                log.info("이미지 저장 경로 생성 실패");
                throw new RuntimeException();
            }
        }
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            if(!deleteFile.delete()) {
                log.info("파일 삭제가 실패했습니다.");
            }else {
                log.info("파일을 삭제하였습니다.");
            }
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
