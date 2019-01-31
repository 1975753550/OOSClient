package com.chinatelecom.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import static com.chinatelecom.util.XmlToJsonUtils.*;
/**
 * @author Zw
 * @date 2018-08-27
 * @description
 * @version
 */
@Service
public class DemoService {

    Logger logger = LoggerFactory.getLogger(DemoService.class);
    private Bucket bucket = new Bucket();
    private Object object = new Object();

    public JSONObject createBucket(String bucketName) {
        String code = "";
        try {
            code = bucket.createBucket(bucketName);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        JSONObject obj = new JSONObject();
        if(code.equalsIgnoreCase("200")) {
            obj.put("data", "succeed");
            return obj;
        }else {
            obj.put("data", "failed");
            return obj;
        }
    }

    public JSONObject deleteBucket(String bucketName) {
        String code = "";
        JSONObject obj;
        try {
            code = bucket.deleteBucket(bucketName);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        obj = new JSONObject();
        if(code.equalsIgnoreCase("204")) {
            obj.put("data", "succeed");
            return obj;
        }else {
            obj.put("data", "failed");
            return obj;
        }
    }

    public JSONObject getACL(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketACL(bucketName);
            obj = xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

    public JSONObject getLifecycle(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketLifecycle(bucketName);
            obj =xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

    public JSONObject getLogging(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketLogging(bucketName);
            obj = xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

    public JSONObject getPolicy(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketPolicy(bucketName);
            obj = JSONObject.parseObject(result);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

    public JSONObject getCors(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketCors(bucketName);
            obj = xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

    public JSONObject getWebsite(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucketWebsit(bucketName);
            obj = xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }
    
    public JSONObject uploadObject(String bucketName, String objName, String data) {
        String code = "";
        JSONObject obj;
        try {
            code = object.putObject(bucketName, objName, data);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        obj = new JSONObject();
        if(code.equalsIgnoreCase("200")) {
            obj.put("data", "succeed");
            return obj;
        }else {
            obj.put("data", "failed");
            return obj;
        }
    }
    
    public JSONObject downloadObject(String bucketName, String objName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = object.getObject(bucketName, objName);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        obj.put("data", result);
        logger.info(obj.toJSONString());
        return obj;
    }
    
    public JSONObject deleteObject(String bucketName, String objName) {
        String code = "";
        try {
            code = object.deleteObject(bucketName, objName);
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(code);
        JSONObject obj = new JSONObject();
        if(code.equalsIgnoreCase("204")) {
            obj.put("data", "succeed");
            return obj;
        }else {
            obj.put("data", "failed");
            return obj;
        }
    }
    
    public JSONObject listObject(String bucketName) {
        String result = "";
        JSONObject obj = new JSONObject();
        try {
            result = bucket.getBucket(bucketName);
            logger.info(result);
            obj = xml2Json(result.trim());
        }catch(Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errormessage", e.getMessage());
            return error;
        }
        logger.info(obj.toJSONString());
        return obj;
    }

}
