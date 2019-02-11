package com.chinatelecom.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Zw
 * @date 2018-08-27
 * @description
 * @version
 */

@RestController
@Api(description = "oos server's api")
@RequestMapping(value = "/oos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class Controller {

    public static Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private DemoService demoService;

    @ApiOperation(value = "create bucket", notes = "input example:   bucketName:bgm, acl: public-read", response = JSONObject.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"hostJobId\": \"1\"}") })
    @PostMapping(value = "/createBucket")
    public JSONObject createBucket(
            @ApiParam(required = true, name = "bucketName", value = "bucketName", example = "11.11.185.52") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.createBucket(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "delete bucket", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"status\": \"RUNNING\", \"progress\": \"50%\"}") })
    @PostMapping(value = "/deleteBucket")
    public JSONObject deleteBucket(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.deleteBucket(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "get bucket acl", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/getACL")
    public JSONObject getACL(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getACL(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "job bucket lifecycle", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ret_code\": \"1\"}") })
    @PostMapping(value = "/getLifecycle")
    public JSONObject getLifecycle(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getLifecycle(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "get bucket logging", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/getLogging")
    public JSONObject getLogging(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getLogging(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "get bucket policy", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/getPolicy")
    public JSONObject getPolicy(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getPolicy(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "get bucket cors", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/getCors")
    public JSONObject getCors(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getCors(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;

    }

    @ApiOperation(value = "get bucket website", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/getWebsite")
    public JSONObject getWebsite(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.getWebsite(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "upload object", notes = "input example:   bucketName:bgm, objName:ccccch, data:11111111", response = JSONObject.class)
    @PostMapping(value = "/uploadObject")
    public JSONObject uploadObject(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName,
            @ApiParam(required = true, name = "objName", value = "objName") @RequestParam(name = "objName") String objName,
            @ApiParam(required = true, name = "data", value = "data") @RequestParam(name = "data") String data)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName + "objName:"
                    + objName);
        }
        JSONObject response = demoService.uploadObject(bucketName, objName,
                data);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "download object", notes = "input example:   bucketName:bgm, objName:ccccch", response = JSONObject.class)
    @PostMapping(value = "/downloadObject")
    public JSONObject downloadObject(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName,
            @ApiParam(required = true, name = "objName", value = "objName") @RequestParam(name = "objName") String objName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.downloadObject(bucketName, objName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "delete object", notes = "input example:   bucketName:bgm, objName:ccccch", response = JSONObject.class)
    @PostMapping(value = "/deleteObject")
    public JSONObject deleteObject(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName,
            @ApiParam(required = true, name = "objName", value = "objName") @RequestParam(name = "objName") String objName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.deleteObject(bucketName, objName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

    @ApiOperation(value = "list object", notes = "input example:   bucketName:bgm", response = JSONObject.class)
    @PostMapping(value = "/listObject")
    public JSONObject listObject(
            @ApiParam(required = true, name = "bucketName", value = "bucketName") @RequestParam(name = "bucketName") String bucketName)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("request params bucketName:" + bucketName);
        }
        JSONObject response = demoService.listObject(bucketName);
        if(logger.isInfoEnabled()) {
            logger.info(response.toJSONString());
        }
        return response;
    }

}
